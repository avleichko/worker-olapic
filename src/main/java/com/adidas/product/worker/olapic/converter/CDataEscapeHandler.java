package com.adidas.product.worker.olapic.converter;

import com.adidas.product.worker.olapic.util.MappingUtils;
import com.sun.xml.internal.bind.marshaller.MinimumEscapeHandler;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;

public class CDataEscapeHandler implements CharacterEscapeHandler {
    private static final char[] PREFIX_ARRAY = MappingUtils.CDATA_PREFIX.toCharArray();
    private static final char[] SUFFIX_ARRAY = MappingUtils.CDATA_SUFFIX.toCharArray();


    @Override
    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
        if (isCData(ch, start, length)) {
            out.write(ch, start, length);
        } else {
            MinimumEscapeHandler.theInstance.escape(ch, start, length, isAttVal, out);
        }
    }

    private boolean isCData(final char[] ch, final int start, final int length) {
        boolean isCData = length > PREFIX_ARRAY.length + SUFFIX_ARRAY.length;
        if (isCData) {
            for (int i = 0, j = start; i < PREFIX_ARRAY.length; ++i, ++j) {
                if (PREFIX_ARRAY[i] != ch[j]) {
                    isCData = false;
                    break;
                }
            }
            if (isCData) {
                for (int i = SUFFIX_ARRAY.length - 1, j = start + length - 1; i >= 0; --i, --j) {
                    if (SUFFIX_ARRAY[i] != ch[j]) {
                        isCData = false;
                        break;
                    }
                }
            }
        }
        return isCData;
    }
}
