package com.adidas.product.worker.olapic.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product {
    private String id;
    private String skuNumber;
    private String displaySize;
    private String pageUrl;
}
