package com.buuz135.replication.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberUtilsTest {
    @Test
    void preservesMatterDisplayFormatting() {
        assertEquals("1", NumberUtils.getFormatedBigNumber(0.1));
        assertEquals("999", NumberUtils.getFormatedBigNumber(999));
        assertEquals("1K", NumberUtils.getFormatedBigNumber(1_000));
        assertEquals("1.5K", NumberUtils.getFormatedBigNumber(1_500));
        assertEquals("1M", NumberUtils.getFormatedBigNumber(1_000_000));
    }
}
