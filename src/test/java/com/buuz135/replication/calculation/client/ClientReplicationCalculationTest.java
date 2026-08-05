package com.buuz135.replication.calculation.client;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClientReplicationCalculationTest {
    private static final String MALFORMED_ITEM_ID = "Invalid Item ID";

    @Test
    void skipsMalformedItemIdsBeforeRegistryLookup() {
        AtomicBoolean actionInvoked = new AtomicBoolean();

        assertDoesNotThrow(() -> ClientReplicationCalculation.ifRegisteredItem(
                MALFORMED_ITEM_ID,
                (itemId, item) -> actionInvoked.set(true)
        ));

        assertFalse(actionInvoked.get());
    }
}
