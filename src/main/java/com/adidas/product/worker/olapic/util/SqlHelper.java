package com.adidas.product.worker.olapic.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SqlHelper {
    public static final String FETCH_ARTICLE_SELECT = "select " +
            "    ad.article_number as article_number,\n" +
            "    ad.model_number as model_number,\n" +
            "    ed.pdp_model as pdp_model,\n" +
            "    rd.code as code,\n" +
            "    cd.b2b_copy as b2b_copy,\n" +
            "    cd.b2c_model_name as b2c_model_name,\n" +
            "    cd.bullets1 as bullets1,\n" +
            "    cd.bullets2 as bullets2,\n" +
            "    cd.bullets3 as bullets3,\n" +
            "    cd.bullets4 as bullets4,\n" +
            "    cd.bullets5 as bullets5,\n" +
            "    cd.bullets6 as bullets6,\n" +
            "    cd.bullets7 as bullets7,\n" +
            "    if (current_timestamp() between ed.online_from and ed.online_to, 1, 0) as online,\n" +
            "    ed.online_from as online_from,\n" +
            "    ed.online_to as online_to,\n" +
            "    ed.gender as gender,\n" +
            "    ed.product_filter as product_filter,\n" +
            "    ed.product_division as product_division,\n" +
            "    ed.brand as brand,\n" +
            "    ed.sports as sports,\n" +
            "    ed.gender_sub as gender_sub,\n" +
            "    ed.outlet as outlet,\n" +
            "    ed.colors as colors,\n" +
            "    ed.preview_to as preview_to,\n" +
            "    ed.default_color as default_color,\n" +
            "    ed.hidden_campaigns as hidden_campaigns,\n" +
            "    ed.personalizable as personalizable,\n" +
            "    ecd.campaign_plps as campaign_plps,\n" +
            "    ecd.technologies as technologies,\n" +
            "    ecd.functions as functions,\n" +
            "    ecd.product_lines as product_lines,\n" +
            "    ecd.product_types as product_types,\n" +
            "    ecd.partners as partners,\n" +
            "    ecd.product_family as product_family,\n" +
            "    ecd.best_for as best_for,\n" +
            "    epd.currency as currency,\n" +
            "    epd.type as type,\n" +
            "    epd.price as price,\n" +
            "    asd.link as link,\n" +
            "    bvarl.average_rating as average_rating,\n" +
            "    bvprsl.total_review_count as total_review_count\n";
    public static final String FETCH_ARTICLE_FROM = "from article_data ad\n" +
            "join asset_data asd on asd.article_number = ad.article_number and sort_order = '0'\n" +
            "join ecom_data ed on ed.article_number = ad.article_number\n" +
            "join ecom_custom_data ecd on ecd.article_number = ed.article_number and ecd.locale = ed.locale\n" +
            "join catalog_data cd on cd.article_number = ecd.article_number and cd.locale = ecd.locale\n" +
            "join ecom_price_data epd on epd.article_number = cd.article_number and epd.locale = cd.locale\n" +
            "join reference_data rd on rd.id = ad.product_division_code\n" +
            "left join bv_average_rating_locale bvarl on bvarl.model_number = if (ed.pdp_model is null or ed.pdp_model = '', ad.model_number, ed.pdp_model)\n" +
            "left join bv_product_review_statistics_locale bvprsl on bvprsl.model_number = if (ed.pdp_model is null or ed.pdp_model = '', ad.model_number, ed.pdp_model)";

    public static final String FETCH_ARTICLE_WHERE = "where ad.brand_code = ? \n" +
            "and ed.locale = ? \n" +
            "and ed.online_to >= current_timestamp()\n" +
            "and ad.type = ?\n";

    public static final String FETCH_ARTICLE_GROUP = "group by article_number";

    public static final String FETCH_ARTICLE = String.join("\n",
            FETCH_ARTICLE_SELECT
            + FETCH_ARTICLE_FROM
            + FETCH_ARTICLE_WHERE
            + FETCH_ARTICLE_SELECT);
}
