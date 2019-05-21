package com.adidas.product.worker.olapic.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class FlatArticle {
    private String articleNumber;
    private String locale;
    private String modelNumber;
    private Byte referenceCode;
    private String b2cModelName;
    private String b2bCopy;
    private List<String> bullets;
    private Boolean online;
    private LocalDateTime onlineFrom;
    private LocalDateTime onlineTo;
    private String gender;
    private String productFilters;
    private String productDivision;
    private String brands;
    private String sports;
    private String genderSub;
    private Boolean outlet;
    private LocalDateTime previewTo;
    private String colors;
    private String defaultColor;
    private String hiddenCampaigns;
    private Boolean personalizable;
    private String campaignPlps;
    private String technologies;
    private String functions;
    private String productLines;
    private String productTypes;
    private String partners;
    private String productFamily;
    private String bestFor;
    private String currency;
    private String type;
    private String currentPrice;
    private String salePrice;
    private Float averageRating;
    private Integer totalReviewCount;
    private String link;
    private String sportSub;
}
