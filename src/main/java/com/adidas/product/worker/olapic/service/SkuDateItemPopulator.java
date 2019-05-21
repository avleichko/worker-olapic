package com.adidas.product.worker.olapic.service;

import com.adidas.product.worker.olapic.converter.ArticleConverter;
import com.adidas.product.worker.olapic.domain.ArticleList;
import com.adidas.product.worker.olapic.domain.ArticleModel;
import com.adidas.product.worker.olapic.domain.FlatArticle;
import com.adidas.product.worker.olapic.domain.FlatArticleMap;
import com.adidas.product.worker.olapic.domain.Product;
import com.adidas.product.worker.olapic.exception.CustomProcessItemException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SkuDateItemPopulator implements ItemProcessor<FlatArticle, ArticleModel> {
    private final JdbcTemplate jdbcTemplate;
    private final ArticleConverter articleConverter;

    public SkuDateItemPopulator(final JdbcTemplate jdbcTemplate,
                                final ArticleConverter articleConverter) {
        this.jdbcTemplate = jdbcTemplate;
        this.articleConverter = articleConverter;
    }

    @Override
    public ArticleModel process(final FlatArticle item) {
        String article = item.getArticleNumber();
        return enhanceArticle(
                item,
                jdbcTemplate.query(
                        preparedStatement(article, item.getLocale()),
                        extractArticleModel(article))
        );
    }

    private PreparedStatementCreator preparedStatement(String article, String locale) {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "select "
                            + " ad.article_number as article_number,"
                            + " esd.sku_number as sku_number,"
                            + " esd.display_size as display_size"
                            + " from article_data ad join ecom_sku_data esd"
                            + " on esd.article_number = ad.article_number and esd.locale = ?"
                            + " and ad.article_number = ?",
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            statement.setString(1, locale);
            statement.setString(2, article);
            return statement;
        };
    }

    private void populateIds(List<String> ids, PreparedStatement statement) {
        IntStream.range(0, ids.size()).forEach(i -> {
            try {
                statement.setObject(i + 2, ids.get(i));
            } catch (SQLException e) {
                throw new CustomProcessItemException("Could not populate ids", e);
            }
        });
    }

    private ResultSetExtractor<ArticleModel> extractArticleModel(String article) {
        return rs -> {
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(createProduct(rs));
            }
            rs.close();
            return toArticles(article, products);
        };
    }

    private Product createProduct(ResultSet rs) throws SQLException {
        Assert.notNull(rs, "Must not be null");
        Product product = new Product();

        product.setSkuNumber(rs.getString("sku_number"));
        product.setDisplaySize(rs.getString("display_size"));

        return product;
    }

    private ArticleModel toArticles(String article, List<Product> products) {
        ArticleModel model = new ArticleModel();
        model.setArticleId(article);
        model.setProducts(products);

        return model;
    }

    private ArticleModel enhanceArticle(FlatArticle flat, ArticleModel model) {
        return articleConverter.combine(model, flat);
    }

    private ArticleList enhanceArticle(FlatArticleMap articleMap, ArticleList articleList) {
        CollectionUtils.emptyIfNull(articleList.getArticles())
                .forEach(article -> {
                    FlatArticle flatArticle = articleMap.getArticles().get(article.getArticleId());
                    articleConverter.combine(article, flatArticle);
                });

        return articleList;
    }


}
