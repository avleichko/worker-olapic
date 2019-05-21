package com.adidas.product.worker.olapic.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ArticleModel {
    private String articleId;
    private String modelId;
    private Byte divisionCode;
    private String b2cModelName;
    private String b2cCopy;
    private List<String> bullets;
    private Boolean online;
    private LocalDateTime onlineFrom;
    private LocalDateTime onlineTo;
    private LocalDateTime previewTo;
    private String categoryId;
    private List<String> secondaryCategories;
    private List<String> collections;
    private List<String> technologies;
    private List<String> functions;
    private String gender;
    private List<String> sports;
    private String type;
    private Boolean customizable;
    private Boolean exclusive;
    private String searchColor;
    private String color;
    private String image;
    private Boolean personalization;
    private Boolean recommended;
    private Boolean outlet;
    private String currency;
    private String name;
    private String currentPrice;
    private String originalPrice;
    private Boolean isNew;
    private Boolean sale;
    private Boolean commingSoon;
    private Boolean promotion;
    private String productCategory;
    private List<String> productFilters;
    private String brands;
    private String genderSub;
    private List<Product> products;
    private Float averageRating;
    private Byte totalReviewCount;
    private String brand;
    private String bestFor;
    private String productFamily;
    private String productType;
    private String sportSub;
    private String hiddenCampaigns;
}
