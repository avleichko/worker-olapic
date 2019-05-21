package com.adidas.product.worker.olapic.config;

import com.adidas.product.worker.olapic.converter.ArticleConverter;
import com.adidas.product.worker.olapic.domain.Article;
import com.adidas.product.worker.olapic.domain.ArticleModel;
import com.adidas.product.worker.olapic.domain.FlatArticle;
import com.adidas.product.worker.olapic.domain.FlatArticleMap;
import com.adidas.product.worker.olapic.service.ArticleXmlMapperProcessor;
import com.adidas.product.worker.olapic.service.CustomProcessorListener;
import com.adidas.product.worker.olapic.service.CustomReaderListener;
import com.adidas.product.worker.olapic.service.CustomWriteListener;
import com.adidas.product.worker.olapic.service.KafkaPublisher;
import com.adidas.product.worker.olapic.service.SkuDateItemPopulator;
import com.adidas.product.worker.olapic.service.XmlItemDelegator;
import com.adidas.product.worker.olapic.util.SqlHelper;
import com.adidas.product.worker.olapic.util.StatementUtils;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.oxm.Marshaller;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    public static final String OLAPIC_JOB = "feed_generation_job";
    public static final String OLAPIC_STEP = "feed_generation_step";
    public static final String OLAPIC_LAUNCHER = "feed_generation_launcher";
    private static final int CHUNK_SIZE = 256;
    private static final String ROOT_XML = "catalog";

    @Autowired
    @Qualifier("auroraDataSource")
    private DataSource auroraDataSource;

    @Autowired
    @Qualifier("jdbcEnhancer")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private ArticleConverter articleConverter;

    @Autowired
    private KafkaPublisher<String> kafkaPublisher;

    @Autowired
    private Marshaller marshaller;

    @Bean
    public BatchConfigurer batchConfigurer(final DataSource batchDataSource) {
        return new DefaultBatchConfigurer(batchDataSource);
    }

    @Bean(name = OLAPIC_LAUNCHER)
    public JobLauncher jobLauncher(final TaskExecutor executor,
                                   final JobRepository jobRepository) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(executor);

        return jobLauncher;
    }

    @Bean(name = OLAPIC_JOB)
    public Job olapicJob(@Qualifier(OLAPIC_STEP) Step step) {
        return jobs.get(OLAPIC_JOB).start(step).build();
    }

    @Bean(name = OLAPIC_STEP)
    public Step olapicStep(@Qualifier("xmlWriter") final ItemWriter<Article> writer,
                           @Qualifier("processors") final ItemProcessor<FlatArticle, Article> processors) {
        return steps.get(OLAPIC_STEP)
                .<FlatArticle, Article>chunk(CHUNK_SIZE)
                .reader(pagingReader(null, null, null))
                .listener(readListener())
                .listener(processListener())
                .listener(writeListener())
                .processor(processors)
                .writer(writer)
                .build();
    }

    @Bean
    protected ItemReadListener<FlatArticleMap> readListener() {
        return new CustomReaderListener(kafkaPublisher);
    }

    @Bean
    protected ItemProcessListener<?, ?> processListener() {
        return new CustomProcessorListener(kafkaPublisher);
    }

    @Bean
    protected ItemWriteListener<ArticleModel> writeListener() {
        return new CustomWriteListener(kafkaPublisher);
    }

    @StepScope
    @Bean(name = "jdbcPaging")
    protected JdbcPagingItemReader<FlatArticle> pagingReader(@Value("#{jobParameters[locale]}") String locale,
                                                             @Value("#{jobParameters[brand_code]}") String brand,
                                                             @Value("#{jobParameters[type]}") String type) {
        Objects.requireNonNull(locale, "Locale must be injected");
        Objects.requireNonNull(brand, "Brand code must be injected");
        Objects.requireNonNull(type, "Type must be injected");
        return new JdbcPagingItemReaderBuilder<FlatArticle>()
                .name("jdbcPaging")
                .dataSource(auroraDataSource)
                .pageSize(1024)
                .fetchSize(StatementUtils.FETCH_SIZE)
                .queryProvider(pagingQueryProvider())
                .rowMapper(rowMapper())
                .parameterValues(parameters(brand, locale, type))
                .build();
    }

    private PagingQueryProvider pagingQueryProvider() {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();

        provider.setSelectClause(SqlHelper.FETCH_ARTICLE_SELECT);
        provider.setFromClause(SqlHelper.FETCH_ARTICLE_FROM);
        provider.setWhereClause(SqlHelper.FETCH_ARTICLE_WHERE);
        provider.setGroupClause(SqlHelper.FETCH_ARTICLE_GROUP);

        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put("article_number", Order.ASCENDING);

        provider.setSortKeys(sortKey);

        return provider;
    }

    private RowMapper<FlatArticle> rowMapper() {
        return (rs, num) -> articleConverter.toFlatArticle(rs, num);
    }

    private Map<String, Object> parameters(String brand, String locale, String type) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("1", brand);
        parameters.put("2", locale);
        parameters.put("3", type);

        return parameters;
    }

    @StepScope
    @Bean(name = "jdbcCursor")
    protected JdbcCursorItemReader<FlatArticleMap> reader(@Value("#{jobParameters[locale]}") String locale,
                                                          @Value("#{jobParameters[brand_code]}") String brand,
                                                          @Value("#{jobParameters[type]}") String type) {
        Objects.requireNonNull(locale, "Locale must be injected");
        Objects.requireNonNull(brand, "Brand code must be injected");
        Objects.requireNonNull(type, "Type must be injected");
        return new JdbcCursorItemReaderBuilder<FlatArticleMap>()
                .name("jdbcCursor")
                .dataSource(auroraDataSource)
                .fetchSize(StatementUtils.FETCH_SIZE)
                .sql(SqlHelper.FETCH_ARTICLE)
                .rowMapper((rs, rowNum) -> articleConverter.toFlatArticleMap(rs, rowNum, locale))
                .preparedStatementSetter(ps -> {
                    ps.setString(1, brand);
                    ps.setString(2, locale);
                    ps.setString(3, type);
                })
                .build();
    }


    @Bean(name = "processors")
    protected CompositeItemProcessor<FlatArticle, Article> compositeItemProcessor() {
        CompositeItemProcessor<FlatArticle, Article> processors
                = new CompositeItemProcessor<>();

        SkuDateItemPopulator populator = new SkuDateItemPopulator(jdbcTemplate, articleConverter);
        ArticleXmlMapperProcessor mapper = new ArticleXmlMapperProcessor(articleConverter);

        processors.setDelegates(Arrays.asList(populator, mapper));
        return processors;
    }

    @Bean(name = "flatXml")
    protected XmlItemDelegator flatXmlItemWriter() {
        return new XmlItemDelegator(xmlWriter());
    }

    @Bean
    @StepScope
    protected StaxEventItemWriter<Article> xmlWriter() {
        return new StaxEventItemWriterBuilder<Article>()
                .name("olapicXmlWriter")
                .rootTagName(ROOT_XML)
                .marshaller(marshaller)
                .resource(new FileSystemResource("output.xml"))
                .build();
    }
}
