package com.buuz135.replication.calculation.client;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.calculation.MatterCompound;
import com.buuz135.replication.calculation.client.matteropedia.MatterOpediaCatalog;
import com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery;
import com.buuz135.replication.calculation.client.matteropedia.MatterOpediaResult;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.FilterMode.AMOUNT_EQUAL;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.FilterMode.AMOUNT_LESS;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.FilterMode.DOESNT_HAVE;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.SortType.AMOUNT;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.SortType.DISPLAY_NAME;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void registeredEmptyMatterPreservesLegacyMissingAsZeroQueriesInBothDirections() {
        ResourceLocation empty = ResourceLocation.parse("replication:empty");
        ResourceKey<Registry<IMatterType>> registryKey = ResourceKey.createRegistryKey(
                ResourceLocation.parse("test:matter_types"));
        Registry<IMatterType> registry = new RegistryBuilder<>(registryKey)
                .disableRegistrationCheck()
                .create();
        Registry.register(registry, empty, MatterType.EMPTY);
        Registry.register(registry, ResourceLocation.parse("replication:earth"), MatterType.EARTH);

        MatterOpediaCatalog catalog = MatterOpediaCatalog.fromSeeds(List.of(
                seed("test:zulu", "Zulu"),
                seed("test:alpha", "Alpha"),
                seed("test:mike", "Mike")
        ), ClientReplicationCalculation.getMatterTypeKeys(registry), 1, 1);

        for (LegacySearch search : List.of(
                new LegacySearch("empty=0", AMOUNT_EQUAL, 0),
                new LegacySearch("=0", AMOUNT_EQUAL, 0),
                new LegacySearch("empty<1", AMOUNT_LESS, 1),
                new LegacySearch("<1", AMOUNT_LESS, 1),
                new LegacySearch("!empty", DOESNT_HAVE, 0),
                new LegacySearch("!", DOESNT_HAVE, 0))) {
            assertIds(search.expression(),
                    catalog.query(query(empty, search.mode(), search.amount(), AMOUNT, false)),
                    0, 1, 2);
            assertIds(search.expression(),
                    catalog.query(query(empty, search.mode(), search.amount(), AMOUNT, true)),
                    0, 1, 2);
            assertIds(search.expression(),
                    catalog.query(query(empty, search.mode(), search.amount(), DISPLAY_NAME, false)),
                    1, 2, 0);
            assertIds(search.expression(),
                    catalog.query(query(empty, search.mode(), search.amount(), DISPLAY_NAME, true)),
                    0, 2, 1);
        }
    }

    private record LegacySearch(
            String expression,
            MatterOpediaQuery.FilterMode mode,
            int amount
    ) {
    }

    private static MatterOpediaCatalog.Seed seed(String itemId, String displayName) {
        return new MatterOpediaCatalog.Seed(
                ResourceLocation.parse(itemId),
                new MatterCompound(),
                Map.of(),
                0,
                displayName.toLowerCase(Locale.ROOT)
        );
    }

    private static MatterOpediaQuery query(
            ResourceLocation matterType,
            MatterOpediaQuery.FilterMode filterMode,
            int amount,
            MatterOpediaQuery.SortType sortType,
            boolean descending
    ) {
        return new MatterOpediaQuery(matterType, filterMode, amount, sortType, descending);
    }

    private static void assertIds(
            String expression, MatterOpediaResult result, int... expectedIds
    ) {
        assertEquals(expectedIds.length, result.size(), expression);
        for (int index = 0; index < expectedIds.length; index++) {
            assertEquals(expectedIds[index], result.entryIdAt(index),
                    expression + " entry at index " + index);
        }
    }
}
