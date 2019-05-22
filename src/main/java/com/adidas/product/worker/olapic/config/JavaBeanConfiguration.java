package com.adidas.product.worker.olapic.config;

import com.adidas.product.worker.olapic.converter.CDataEscapeHandler;
import com.adidas.product.worker.olapic.domain.ObjectFactory;
import com.adidas.product.worker.olapic.service.KafkaPublisher;
import com.adidas.product.worker.olapic.service.AvroKafkaPublisher;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JavaBeanConfiguration {
    @Value("${aurora.datasource.url}")
    private String url;
    @Value("${aurora.datasource.password}")
    private String password;
    @Value("${aurora.datasource.username}")
    private String username;
    @Value("${aurora.datasource.driver}")
    private String driver;

    @Bean("jdbcEnhancer")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(auroraDataSource());
    }

    @Bean("auroraDataSource")
    public DataSource auroraDataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driver)
                .build();
    }

    @Primary
    @Bean("batchDataSource")
    public DataSource batchDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:testdb")
                .username("sa")
                .driverClassName("org.h2.Driver")
                .build();
    }

    @Primary
    @Bean(name = "asyncTaskExecutor")
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(1);

        return executor;
    }

    @Bean(name = "marshaller")
    public Marshaller jaxb2() {
        Map<String, Object> map = new HashMap<>();
        map.put(CharacterEscapeHandler.class.getName(), new CDataEscapeHandler());

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.adidas.product.worker.olapic.domain");
        marshaller.setMarshallerProperties(map);

        return marshaller;
    }

    @Bean
    public KafkaPublisher kafkaPublisher() {
        return new AvroKafkaPublisher();
    }

    @Bean
    public ObjectFactory objectFactory() {
        return new ObjectFactory();
    }

}
