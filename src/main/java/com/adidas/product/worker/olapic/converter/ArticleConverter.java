package com.adidas.product.worker.olapic.converter;

import com.adidas.product.worker.olapic.domain.Article;
import com.adidas.product.worker.olapic.domain.ArticleModel;
import com.adidas.product.worker.olapic.domain.FlatArticle;
import com.adidas.product.worker.olapic.domain.FlatArticleMap;
import com.adidas.product.worker.olapic.domain.ObjectFactory;
import com.adidas.product.worker.olapic.domain.Product;
import com.adidas.product.worker.olapic.util.MappingUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ArticleConverter {
    private static final int BULLETS_SIZE = 7;

    private final ObjectFactory factory;

    public ArticleConverter(final ObjectFactory factory) {
        this.factory = factory;
    }


    public FlatArticleMap toFlatArticleMap(final ResultSet rs, final int rowNumber,
                                           final String locale) throws SQLException {
        Map<String, FlatArticle> map = new HashMap<>();
        // TODO should be redesigned due to row mapper contract
        do {
            FlatArticle article = toFlatArticle(rs, rowNumber);
            map.put(article.getArticleNumber(), article);
        } while (rs.next());
        return new FlatArticleMap(locale, map);
    }

    public FlatArticle toFlatArticle(final ResultSet rs, int rowNumber) throws SQLException {
        Assert.notNull(rs, "Must not be null");

        FlatArticle flatArticle = new FlatArticle();

        flatArticle.setArticleNumber(rs.getString("article_number"));
        flatArticle.setModelNumber(resolveModelNumber(rs));
        flatArticle.setReferenceCode(rs.getByte("code"));
        flatArticle.setB2bCopy(rs.getString("b2b_copy"));
        flatArticle.setB2cModelName(rs.getString("b2c_model_name"));

        List<String> list = new ArrayList<>(BULLETS_SIZE);
        for (int i = 0; i < BULLETS_SIZE; i++) {
            list.add(rs.getString("bullets" + (i + 1)));
        }

        flatArticle.setBullets(list);
        flatArticle.setOnline(rs.getBoolean("online"));
        flatArticle.setOnlineTo(rs.getTimestamp("online_to").toLocalDateTime());
        flatArticle.setOnlineTo(rs.getTimestamp("online_from").toLocalDateTime());
        flatArticle.setGender(rs.getString("gender"));
        flatArticle.setProductFilters(rs.getString("product_filter"));
        flatArticle.setProductDivision(rs.getString("product_division"));
        flatArticle.setBrands(rs.getString("brand"));
        flatArticle.setSports(rs.getString("sports"));
        flatArticle.setGenderSub(rs.getString("gender_sub"));
        flatArticle.setOutlet(rs.getBoolean("outlet"));
        flatArticle.setDefaultColor(rs.getString("default_color"));
        flatArticle.setColors(rs.getString("colors"));
        flatArticle.setHiddenCampaigns(rs.getString("hidden_campaigns"));
        flatArticle.setPersonalizable(rs.getBoolean("personalizable"));
        flatArticle.setCampaignPlps(rs.getString("campaign_plps"));
        flatArticle.setTechnologies(rs.getString("technologies"));
        flatArticle.setFunctions(rs.getString("functions"));
        flatArticle.setPartners(rs.getString("partners"));
        flatArticle.setProductFamily(rs.getString("product_family"));
        flatArticle.setBestFor(rs.getString("best_for"));
        flatArticle.setCurrency(rs.getString("currency"));
        flatArticle.setType(rs.getString("type"));
        flatArticle.setProductTypes(rs.getString("product_types"));
        // TODO keep in touch about different prices which should be resolved in sql query
        flatArticle.setCurrentPrice(rs.getString("price"));
        flatArticle.setLink(rs.getString("link"));
        flatArticle.setSportSub(rs.getString("product_lines"));
        flatArticle.setAverageRating(rs.getFloat("average_rating"));
        flatArticle.setTotalReviewCount(rs.getInt("total_review_count"));

        return flatArticle;
    }

    private String resolveModelNumber(ResultSet rs) throws SQLException {
        String model = rs.getString("model_number");
        String pdpModel = rs.getString("pdp_model");
        return pdpModel == null ? model : pdpModel;
    }

    /**
     * All fields instead of article number and products
     * @param article
     * @param flatArticle
     * @return
     */
    public ArticleModel combine(ArticleModel article, FlatArticle flatArticle) {
        Assert.notNull(article, "Not null");
        Assert.notNull(flatArticle, "Not null");

        // TODO some values are stored like json arrays/objects, they should be mapped correctly
        article.setAverageRating(flatArticle.getAverageRating());
        article.setB2cCopy(flatArticle.getB2bCopy());
        article.setB2cModelName(flatArticle.getB2cModelName());
        article.setBrand(flatArticle.getBrands());
        article.setBullets(flatArticle.getBullets());
        // TODO categoryId rule
        article.setCategoryId(flatArticle.getGender() + StringUtils.SPACE + flatArticle.getSports());
        article.setCollections(Collections.emptyList());
        article.setColor(flatArticle.getColors());
        article.setCommingSoon(Boolean.TRUE);
        article.setCurrency(article.getCurrency());
        article.setCurrentPrice(flatArticle.getCurrentPrice());
        article.setCustomizable(Boolean.TRUE);
        article.setDivisionCode(flatArticle.getReferenceCode());
        article.setExclusive(Boolean.TRUE);
        article.setImage(flatArticle.getLink());
        article.setIsNew(Boolean.TRUE);
        article.setModelId(flatArticle.getModelNumber());
        article.setName("default");
        article.setOnline(article.getOnline());
        article.setOnlineFrom(flatArticle.getOnlineFrom());
        article.setOriginalPrice(flatArticle.getCurrentPrice());
        article.setPersonalization(Boolean.TRUE);
        article.setProductCategory(flatArticle.getProductDivision());
        article.setPromotion(Boolean.TRUE);
        article.setProductFilters(Arrays.asList(flatArticle.getProductFilters()));
        article.setRecommended(Boolean.TRUE);
        article.setSale(Boolean.TRUE);
        article.setSearchColor(flatArticle.getColors());
        article.setSecondaryCategories(Collections.emptyList());
        article.setBestFor(flatArticle.getBestFor());
        article.setProductFamily(flatArticle.getProductFamily());
        article.setProductType(flatArticle.getProductTypes());
        article.setSportSub(flatArticle.getSportSub());
        article.setFunctions(Arrays.asList(flatArticle.getFunctions()));
        article.setAverageRating(NumberUtils.FLOAT_ZERO);
        article.setTotalReviewCount(NumberUtils.BYTE_ZERO);
        article.setTechnologies(Arrays.asList(flatArticle.getTechnologies()));

        return article;
    }

    public Article toXmlArticle(ArticleModel article) {
        Assert.notNull(article, "Not null");

        Article xmlArticle = factory.createArticle();
        xmlArticle.setId(article.getArticleId());

        // in this case, null means, that there is no proper mapping in confluence page
        xmlArticle.setActivity(null);
        xmlArticle.setAge(null);
        xmlArticle.setBestFor(article.getBestFor());

        // TODO rid of jaxb addAll repeating via reflection or lambdas
        Article.Bullets bullets = factory.createArticleBullets();
        bullets.getBullet().addAll(MappingUtils.cdata(article.getBullets()));
        xmlArticle.setBullets(bullets);

        xmlArticle.setCategoryId(MappingUtils.cdata(article.getCategoryId()));

        Article.Collections collections = factory.createArticleCollections();
        collections.getCollection().addAll(article.getCollections());
        xmlArticle.setCollections(collections);

        xmlArticle.setColor(MappingUtils.cdata(article.getColor()));
        xmlArticle.setComingSoon(MappingUtils.booleanValue(article.getCommingSoon()));
        xmlArticle.setCustomizable(MappingUtils.booleanValue(article.getCustomizable()));
        xmlArticle.setDisplayName(MappingUtils.cdata(article.getB2cModelName()));
        xmlArticle.setDivision(MappingUtils.cdata(article.getBrand()));
        xmlArticle.setDivisionCode(article.getDivisionCode());
        xmlArticle.setExclusive(MappingUtils.booleanValue(article.getExclusive()));
        xmlArticle.setFootType(MappingUtils.cdata(article.getHiddenCampaigns()));

        Article.Functions functions = factory.createArticleFunctions();
        functions.getFunction().addAll(MappingUtils.cdata(article.getFunctions()));
        xmlArticle.setFunctions(functions);

        xmlArticle.setGender(article.getGender());
        xmlArticle.setSubGender(article.getGenderSub());
        xmlArticle.setImage(MappingUtils.cdata(article.getImage()));
        xmlArticle.setModelId(article.getModelId());
        xmlArticle.setNew(MappingUtils.booleanValue(article.getIsNew()));
        xmlArticle.setOnline(MappingUtils.booleanValue(article.getOnline()));
        xmlArticle.setOnlineFrom(MappingUtils.dateTimeValue(article.getOnlineFrom()));
        xmlArticle.setOnlineTo(MappingUtils.dateTimeValue(article.getOnlineFrom()));
        xmlArticle.setOutlet(MappingUtils.booleanValue(article.getOutlet()));
        xmlArticle.setPartner(MappingUtils.cdata(article.getArticleId()));
        xmlArticle.setPersonalization(MappingUtils.booleanValue(article.getPersonalization()));
        xmlArticle.setPreviewTo(MappingUtils.dateTimeValue(article.getPreviewTo()));
        xmlArticle.setProductCategory(MappingUtils.cdata(article.getProductCategory()));

        Article.ProductFilters filters = factory.createArticleProductFilters();
        filters.setProductFilter(MappingUtils.first(article.getProductFilters()));
        xmlArticle.setProductFilters(filters);

        xmlArticle.setProductLineStyle(MappingUtils.cdata(article.getProductFamily()));
        xmlArticle.setProductType(MappingUtils.cdata(article.getProductType()));

        Article.Review review = factory.createArticleReview();
        review.setCount(article.getTotalReviewCount());
        review.setRating(article.getAverageRating());
        xmlArticle.setReview(review);

        xmlArticle.setSale(MappingUtils.booleanValue(article.getSale()));
        xmlArticle.setSearchColor(MappingUtils.cdata(article.getSearchColor()));

        xmlArticle.getProduct().addAll(toProducts(article.getProducts()));

        xmlArticle.setShortDescription(MappingUtils.cdata(article.getB2cCopy()));

        Article.Sports sports = factory.createArticleSports();
        sports.getSport().addAll(MappingUtils.cdata(article.getSports()));
        xmlArticle.setSports(sports);

        xmlArticle.setSportSub(article.getSportSub());
        // TODO should be populated from inventory
        xmlArticle.setStock(NumberUtils.BYTE_ZERO);

        Article.Technologies technologies = factory.createArticleTechnologies();
        technologies.getTechnology().addAll(MappingUtils.cdata(article.getTechnologies()));
        xmlArticle.setTechnologies(technologies);

        return xmlArticle;
    }

    private List<Article.Product> toProducts(List<Product> products) {
        return CollectionUtils.emptyIfNull(products)
                .stream()
                .map(product -> {
                    Article.Product xmlProduct = factory.createArticleProduct();
                    xmlProduct.setId(product.getId());
                    xmlProduct.setUrl(MappingUtils.cdata(product.getPageUrl()));
                    xmlProduct.setSize(product.getSkuNumber());
                    xmlProduct.setSearchSize(product.getDisplaySize());
                    return xmlProduct;
                }).collect(Collectors.toList());
    }
}
