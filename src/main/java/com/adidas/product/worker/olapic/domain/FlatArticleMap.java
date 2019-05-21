package com.adidas.product.worker.olapic.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatArticleMap {
    private String locale;
    private Map<String, FlatArticle> articles;
}
