package com.adidas.product.worker.olapic.service;

import com.adidas.product.worker.olapic.domain.Article;
import com.adidas.product.worker.olapic.domain.XmlArticleList;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;

import java.util.List;
import java.util.stream.Collectors;

public class XmlItemDelegator implements ItemStreamWriter<XmlArticleList> {
    private final StaxEventItemWriter<Article> itemWriter;

    public XmlItemDelegator(StaxEventItemWriter<Article> itemWriter) {
        this.itemWriter = itemWriter;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        itemWriter.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        itemWriter.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        itemWriter.close();
    }

    @Override
    public void write(List<? extends XmlArticleList> items) throws Exception {
        itemWriter.write(flatMapArticle(items));
    }


    private List<Article> flatMapArticle(List<? extends XmlArticleList> items) {
        return items.stream()
                .flatMap(articleList -> articleList.getArticles().stream())
                .collect(Collectors.toList());
    }
}
