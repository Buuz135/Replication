package com.buuz135.replication.calculation.client.matteropedia;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatterOpediaResultTest {
    @Test
    void exposesForwardRangeWithoutCopyingOrder() {
        var result = MatterOpediaResult.range(new int[]{4, 7, 9, 12}, 1, 4);

        assertEquals(3, result.size());
        assertEquals(7, result.entryIdAt(0));
        assertEquals(12, result.entryIdAt(2));
    }

    @Test
    void preservesTheOrderProvidedByTheSelectedIndex() {
        var result = MatterOpediaResult.range(new int[]{12, 9, 7, 4}, 1, 4);

        assertEquals(9, result.entryIdAt(0));
        assertEquals(4, result.entryIdAt(2));
    }

    @Test
    void rejectsInvalidRangesAndVisibleIndexes() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> MatterOpediaResult.range(new int[]{1, 2}, -1, 2));
        var result = MatterOpediaResult.all(new int[]{1, 2});
        assertThrows(IndexOutOfBoundsException.class, () -> result.entryIdAt(2));
    }
}
