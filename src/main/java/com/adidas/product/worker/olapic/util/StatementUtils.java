package com.adidas.product.worker.olapic.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.IntStream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatementUtils {
    public static final int FETCH_SIZE = 512;

    public static String optimizedIn() {
        StringBuilder inClause = new StringBuilder("in (");
        IntStream.range(0, FETCH_SIZE).forEach(i -> inClause.append("?,"));
        return StringUtils.removeEnd(inClause.toString(),",") + ")";
    }

    public static String notOptimizedIn(int fetchSize) {
        StringBuilder inClause = new StringBuilder("in (");
        IntStream.range(0, fetchSize).forEach(i -> inClause.append("?,"));
        return StringUtils.removeEnd(inClause.toString(),",") + ")";
    }
}
