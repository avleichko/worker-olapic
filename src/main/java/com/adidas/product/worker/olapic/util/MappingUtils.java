package com.adidas.product.worker.olapic.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MappingUtils {
    public static final String CDATA_PREFIX = "<![CDATA[";
    public static final String CDATA_SUFFIX = "]]>";

    public static String booleanValue(Boolean bool) {
        return Optional.ofNullable(bool).map(Object::toString).map(String::toUpperCase).orElse(null);
    }

    public static String dateTimeValue(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime).map(LocalDateTime::toString).orElse(null);
    }

    public static String cdata(String value) {
        return Objects.nonNull(value) ? CDATA_PREFIX + value + CDATA_SUFFIX : null;
    }

    public static List<String> cdata(List<String> value) {
        return CollectionUtils.emptyIfNull(value)
                .stream()
                .map(MappingUtils::cdata)
                .collect(Collectors.toList());
    }

    public static <T> T first(List<T> list) {
        return CollectionUtils.isNotEmpty(list) ? list.iterator().next() : null;
    }

    public static <T> List<T> firstAsList(List<T> list) {
        return CollectionUtils.isNotEmpty(list)
                ? Collections.singletonList(list.iterator().next())
                : null;
    }

}
