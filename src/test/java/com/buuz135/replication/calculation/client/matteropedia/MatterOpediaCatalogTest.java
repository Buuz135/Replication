package com.buuz135.replication.calculation.client.matteropedia;

import com.buuz135.replication.calculation.MatterCompound;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.FilterMode.AMOUNT_EQUAL;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.FilterMode.AMOUNT_GREATER;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.FilterMode.AMOUNT_LESS;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.FilterMode.DOESNT_HAVE;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.FilterMode.NONE;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.FilterMode.ONLY_HAS;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.SortType.AMOUNT;
import static com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery.SortType.DISPLAY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatterOpediaCatalogTest {
    private static final ResourceLocation EARTH = ResourceLocation.parse("test:earth");
    private static final ResourceLocation METAL = ResourceLocation.parse("test:metal");
    private static final ResourceLocation UNKNOWN = ResourceLocation.parse("test:unknown");

    private static final ResourceLocation ALPHA_ID = ResourceLocation.parse("test:alpha");
    private static final ResourceLocation BETA_ID = ResourceLocation.parse("test:beta");
    private static final ResourceLocation GAMMA_ID = ResourceLocation.parse("test:gamma");
    private static final ResourceLocation DELTA_ID = ResourceLocation.parse("test:delta");

    private static final int ALPHA = 0;
    private static final int BETA = 1;
    private static final int GAMMA = 2;
    private static final int DELTA = 3;

    private MatterOpediaCatalog catalog;

    @BeforeEach
    void setUp() {
        catalog = MatterOpediaCatalog.fromSeeds(List.of(
                seed("test:alpha", "Zulu", Map.of(EARTH, 10D), 1),
                seed("test:beta", "Alpha", Map.of(EARTH, 5D, METAL, 2D), 2),
                seed("test:gamma", "Mike", Map.of(EARTH, 10D), 1),
                seed("test:delta", "Beta", Map.of(METAL, 8D), 1)
        ), Set.of(EARTH, METAL), 7, 11);
    }

    @Test
    void usesPrebuiltAmountAndNameOrders() {
        assertIds(query(EARTH, NONE, 0, AMOUNT, false), BETA, ALPHA, GAMMA);
        assertIds(query(EARTH, NONE, 0, AMOUNT, true), ALPHA, GAMMA, BETA);
        assertIds(query(EARTH, NONE, 0, DISPLAY_NAME, false), BETA, GAMMA, ALPHA);
        assertIds(query(EARTH, NONE, 0, DISPLAY_NAME, true), ALPHA, GAMMA, BETA);
    }

    @Test
    void usesDedicatedOnlyHasAndMissingIndexes() {
        assertIds(query(EARTH, ONLY_HAS, 0, AMOUNT, false), ALPHA, GAMMA);
        assertIds(query(EARTH, DOESNT_HAVE, 0, DISPLAY_NAME, false), DELTA);
        assertIds(query(EARTH, DOESNT_HAVE, 0, AMOUNT, true), DELTA);
    }

    @Test
    void appliesBinaryBoundariesForAmountOrder() {
        assertIds(query(EARTH, AMOUNT_EQUAL, 10, AMOUNT, false), ALPHA, GAMMA);
        assertIds(query(EARTH, AMOUNT_EQUAL, 10, AMOUNT, true), ALPHA, GAMMA);
        assertIds(query(EARTH, AMOUNT_EQUAL, 0, AMOUNT, false), DELTA);
        assertIds(query(EARTH, AMOUNT_LESS, 10, AMOUNT, false), DELTA, BETA);
        assertIds(query(EARTH, AMOUNT_GREATER, 5, AMOUNT, false), ALPHA, GAMMA);
        assertIds(query(EARTH, AMOUNT_LESS, 10, AMOUNT, true), BETA, DELTA);
    }

    @Test
    void scansNameOrderWithoutResortingForThresholdQueries() {
        assertIds(query(EARTH, AMOUNT_GREATER, 5, DISPLAY_NAME, false), GAMMA, ALPHA);
        assertIds(query(EARTH, AMOUNT_LESS, 10, DISPLAY_NAME, false), BETA, DELTA);
        assertIds(query(EARTH, AMOUNT_GREATER, 5, DISPLAY_NAME, true), ALPHA, GAMMA);
    }

    @Test
    void returnsCanonicalEmptyResultForUnknownMatterTypes() {
        assertSame(MatterOpediaResult.empty(), query(UNKNOWN, NONE, 0, AMOUNT, false));
    }

    @Test
    void keepsEqualAmountTiesInEntryIdOrderForBothDirections() {
        assertIds(query(EARTH, AMOUNT_EQUAL, 10, AMOUNT, false), ALPHA, GAMMA);
        assertIds(query(EARTH, AMOUNT_EQUAL, 10, AMOUNT, true), ALPHA, GAMMA);
    }

    @Test
    void keepsNegativeAmountsOnlyInAllEntryThresholdQueries() {
        MatterOpediaCatalog negativeCatalog = MatterOpediaCatalog.fromSeeds(List.of(
                seed("test:negative", "Negative", Map.of(EARTH, -2D), 1),
                seed("test:zero", "Zero", Map.of(), 0),
                seed("test:positive", "Positive", Map.of(EARTH, 3D), 1)
        ), Set.of(EARTH), 1, 1);

        assertIds(negativeCatalog.query(queryOf(EARTH, NONE, 0, AMOUNT, false)), 2);
        assertIds(negativeCatalog.query(queryOf(EARTH, DOESNT_HAVE, 0, AMOUNT, false)), 1);
        assertIds(negativeCatalog.query(queryOf(EARTH, AMOUNT_EQUAL, -2, AMOUNT, false)), 0);
        assertIds(negativeCatalog.query(queryOf(EARTH, AMOUNT_GREATER, -1, AMOUNT, false)), 1, 2);
    }

    @Test
    void snapshotsSeedAndEntryDataAndNormalizesMissingNames() {
        Map<ResourceLocation, Double> mutableAmounts = new HashMap<>();
        mutableAmounts.put(EARTH, 4D);
        MatterOpediaCatalog.Seed seed = new MatterOpediaCatalog.Seed(
                ALPHA_ID, new MatterCompound(), mutableAmounts, 1, null);
        mutableAmounts.put(EARTH, 99D);

        MatterOpediaCatalog snapshot = MatterOpediaCatalog.fromSeeds(
                List.of(seed), Set.of(EARTH), 23, 29);
        MatterOpediaCatalog.Entry entry = snapshot.entry(0);

        assertEquals(ALPHA_ID, entry.itemId());
        assertEquals(4D, entry.amount(EARTH));
        assertEquals(0D, entry.amount(METAL));
        assertEquals("", entry.displayNameKey());
        assertEquals(23, snapshot.generation());
        assertEquals(29, snapshot.languageGeneration());
        assertThrows(UnsupportedOperationException.class, () -> entry.amounts().put(METAL, 2D));
    }

    @Test
    void evictsAndRecomputesTheOldestOfSeventeenDistinctQueries() {
        MatterOpediaQuery oldestQuery = queryOf(EARTH, AMOUNT_LESS, 1, AMOUNT, false);
        MatterOpediaResult oldestResult = catalog.query(oldestQuery);
        assertSame(oldestResult, catalog.query(oldestQuery));
        for (int threshold = 2; threshold <= 17; threshold++) {
            catalog.query(queryOf(EARTH, AMOUNT_LESS, threshold, AMOUNT, false));
        }

        MatterOpediaResult recomputed = catalog.query(oldestQuery);

        assertNotSame(oldestResult, recomputed);
        assertIds(recomputed, DELTA);
    }

    @Test
    void replacesNameIndexesAndDoesNotReuseOldLanguageCacheEntries() {
        MatterOpediaQuery nameQuery = queryOf(EARTH, NONE, 0, DISPLAY_NAME, false);
        MatterOpediaResult oldResult = catalog.query(nameQuery);
        MatterOpediaCatalog replacement = catalog.withDisplayNames(Map.of(
                ALPHA_ID, "aardvark",
                BETA_ID, "zulu",
                GAMMA_ID, "mike",
                DELTA_ID, "beta"
        ), 12);

        MatterOpediaResult replacementResult = replacement.query(nameQuery);

        assertIds(oldResult, BETA, GAMMA, ALPHA);
        assertIds(replacementResult, ALPHA, GAMMA, BETA);
        assertEquals(7, replacement.generation());
        assertEquals(12, replacement.languageGeneration());
        assertNotSame(oldResult, replacementResult);
    }

    private MatterOpediaResult query(
            ResourceLocation matterType,
            MatterOpediaQuery.FilterMode filterMode,
            int amount,
            MatterOpediaQuery.SortType sortType,
            boolean descending
    ) {
        return catalog.query(queryOf(matterType, filterMode, amount, sortType, descending));
    }

    private static MatterOpediaQuery queryOf(
            ResourceLocation matterType,
            MatterOpediaQuery.FilterMode filterMode,
            int amount,
            MatterOpediaQuery.SortType sortType,
            boolean descending
    ) {
        return new MatterOpediaQuery(matterType, filterMode, amount, sortType, descending);
    }

    private static MatterOpediaCatalog.Seed seed(
            String itemId, String name, Map<ResourceLocation, Double> amounts, int matterTypeCount
    ) {
        return new MatterOpediaCatalog.Seed(
                ResourceLocation.parse(itemId), new MatterCompound(), amounts, matterTypeCount,
                name.toLowerCase(Locale.ROOT));
    }

    private static void assertIds(MatterOpediaResult result, int... expectedIds) {
        assertEquals(expectedIds.length, result.size());
        for (int index = 0; index < expectedIds.length; index++) {
            assertEquals(expectedIds[index], result.entryIdAt(index), "entry at visible index " + index);
        }
    }
}
