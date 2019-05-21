package com.adidas.product.worker.olapic.service;

import com.adidas.product.worker.olapic.converter.ArticleConverter;
import com.adidas.product.worker.olapic.domain.Article;
import com.adidas.product.worker.olapic.domain.ArticleModel;
import org.springframework.batch.item.ItemProcessor;

public class ArticleXmlMapperProcessor implements ItemProcessor<ArticleModel, Article> {
    private final ArticleConverter articleConverter;

    public ArticleXmlMapperProcessor(final ArticleConverter articleConverter) {
        this.articleConverter = articleConverter;
    }

    @Override
    public Article process(final ArticleModel item) {
        return articleConverter.toXmlArticle(item);
    }
}
