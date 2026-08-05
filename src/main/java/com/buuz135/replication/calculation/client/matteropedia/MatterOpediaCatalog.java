package com.buuz135.replication.calculation.client.matteropedia;

import com.buuz135.replication.calculation.MatterCompound;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntPredicate;

public final class MatterOpediaCatalog {
    private final List<Entry> entries;
    private final Map<ResourceLocation, MatterIndex> indexes;
    private final long generation;
    private final long languageGeneration;
    private final Map<CacheKey, MatterOpediaResult> queryCache =
            new LinkedHashMap<>(16, 0.75F, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<CacheKey, MatterOpediaResult> eldest) {
                    return size() > 16;
                }
            };

    private MatterOpediaCatalog(
            List<Entry> entries,
            Map<ResourceLocation, MatterIndex> indexes,
            long generation,
            long languageGeneration
    ) {
        this.entries = List.copyOf(entries);
        this.indexes = Map.copyOf(indexes);
        this.generation = generation;
        this.languageGeneration = languageGeneration;
    }

    public static MatterOpediaCatalog fromSeeds(
            List<Seed> seeds,
            Set<ResourceLocation> matterTypes,
            long catalogGeneration,
            long languageGeneration
    ) {
        Objects.requireNonNull(seeds, "seeds");
        Objects.requireNonNull(matterTypes, "matterTypes");

        List<Entry> entries = new ArrayList<>(seeds.size());
        for (int entryId = 0; entryId < seeds.size(); entryId++) {
            Seed seed = Objects.requireNonNull(seeds.get(entryId), "seed");
            entries.add(new Entry(
                    entryId,
                    seed.itemId(),
                    seed.compound(),
                    seed.amounts(),
                    seed.matterTypeCount(),
                    seed.displayNameKey()
            ));
        }
        List<Entry> immutableEntries = List.copyOf(entries);

        Map<ResourceLocation, MatterIndex> indexes = new LinkedHashMap<>();
        for (ResourceLocation matterType : matterTypes) {
            ResourceLocation key = Objects.requireNonNull(matterType, "matterType");
            indexes.put(key, MatterIndex.build(key, immutableEntries));
        }
        return new MatterOpediaCatalog(
                immutableEntries, indexes, catalogGeneration, languageGeneration);
    }

    public static MatterOpediaCatalog empty(long catalogGeneration, long languageGeneration) {
        return new MatterOpediaCatalog(List.of(), Map.of(), catalogGeneration, languageGeneration);
    }

    public MatterOpediaResult query(MatterOpediaQuery query) {
        Objects.requireNonNull(query, "query");
        MatterIndex index = indexes.get(query.matterType());
        if (index == null) {
            return MatterOpediaResult.empty();
        }

        CacheKey cacheKey = new CacheKey(query, generation, languageGeneration);
        MatterOpediaResult cached = queryCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        MatterOpediaResult result = index.query(query);
        queryCache.put(cacheKey, result);
        return result;
    }

    public Entry entry(int entryId) {
        return entries.get(entryId);
    }

    public long generation() {
        return generation;
    }

    public long languageGeneration() {
        return languageGeneration;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public MatterOpediaCatalog withDisplayNames(
            Map<ResourceLocation, String> displayNames,
            long nextLanguageGeneration
    ) {
        Objects.requireNonNull(displayNames, "displayNames");

        List<Entry> renamedEntries = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            renamedEntries.add(entry.withDisplayNameKey(displayNames.get(entry.itemId())));
        }
        List<Entry> immutableEntries = List.copyOf(renamedEntries);

        Map<ResourceLocation, MatterIndex> renamedIndexes = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, MatterIndex> index : indexes.entrySet()) {
            renamedIndexes.put(index.getKey(), index.getValue().withEntries(immutableEntries));
        }
        return new MatterOpediaCatalog(
                immutableEntries, renamedIndexes, generation, nextLanguageGeneration);
    }

    public record Seed(
            ResourceLocation itemId,
            MatterCompound compound,
            Map<ResourceLocation, Double> amounts,
            int matterTypeCount,
            String displayNameKey
    ) {
        public Seed {
            Objects.requireNonNull(itemId, "itemId");
            Objects.requireNonNull(compound, "compound");
            amounts = Map.copyOf(Objects.requireNonNull(amounts, "amounts"));
            displayNameKey = normalizeDisplayName(displayNameKey);
        }
    }

    public record Entry(
            int entryId,
            ResourceLocation itemId,
            MatterCompound compound,
            Map<ResourceLocation, Double> amounts,
            int matterTypeCount,
            String displayNameKey
    ) {
        public Entry {
            Objects.requireNonNull(itemId, "itemId");
            Objects.requireNonNull(compound, "compound");
            amounts = Map.copyOf(Objects.requireNonNull(amounts, "amounts"));
            displayNameKey = normalizeDisplayName(displayNameKey);
        }

        public double amount(ResourceLocation matterType) {
            return amounts.getOrDefault(matterType, 0D);
        }

        private Entry withDisplayNameKey(String nextDisplayNameKey) {
            return new Entry(
                    entryId, itemId, compound, amounts, matterTypeCount, nextDisplayNameKey);
        }
    }

    private static String normalizeDisplayName(String displayNameKey) {
        return displayNameKey == null ? "" : displayNameKey;
    }

    private record CacheKey(
            MatterOpediaQuery query,
            long catalogGeneration,
            long languageGeneration
    ) {
    }

    private static final class MatterIndex {
        private final ResourceLocation matterType;
        private final double[] amountsByEntryId;
        private final int[] allByAmountAscending;
        private final int[] allByAmountDescending;
        private final int[] allByNameAscending;
        private final int[] allByNameDescending;
        private final int[] positiveByAmountAscending;
        private final int[] positiveByAmountDescending;
        private final int[] positiveByNameAscending;
        private final int[] positiveByNameDescending;
        private final int[] onlyHasByAmountAscending;
        private final int[] onlyHasByAmountDescending;
        private final int[] onlyHasByNameAscending;
        private final int[] onlyHasByNameDescending;
        private final int[] zeroStable;
        private final int[] zeroByNameAscending;
        private final int[] zeroByNameDescending;

        private MatterIndex(
                ResourceLocation matterType,
                double[] amountsByEntryId,
                int[] allByAmountAscending,
                int[] allByAmountDescending,
                int[] allByNameAscending,
                int[] allByNameDescending,
                int[] positiveByAmountAscending,
                int[] positiveByAmountDescending,
                int[] positiveByNameAscending,
                int[] positiveByNameDescending,
                int[] onlyHasByAmountAscending,
                int[] onlyHasByAmountDescending,
                int[] onlyHasByNameAscending,
                int[] onlyHasByNameDescending,
                int[] zeroStable,
                int[] zeroByNameAscending,
                int[] zeroByNameDescending
        ) {
            this.matterType = matterType;
            this.amountsByEntryId = amountsByEntryId;
            this.allByAmountAscending = allByAmountAscending;
            this.allByAmountDescending = allByAmountDescending;
            this.allByNameAscending = allByNameAscending;
            this.allByNameDescending = allByNameDescending;
            this.positiveByAmountAscending = positiveByAmountAscending;
            this.positiveByAmountDescending = positiveByAmountDescending;
            this.positiveByNameAscending = positiveByNameAscending;
            this.positiveByNameDescending = positiveByNameDescending;
            this.onlyHasByAmountAscending = onlyHasByAmountAscending;
            this.onlyHasByAmountDescending = onlyHasByAmountDescending;
            this.onlyHasByNameAscending = onlyHasByNameAscending;
            this.onlyHasByNameDescending = onlyHasByNameDescending;
            this.zeroStable = zeroStable;
            this.zeroByNameAscending = zeroByNameAscending;
            this.zeroByNameDescending = zeroByNameDescending;
        }

        private static MatterIndex build(ResourceLocation matterType, List<Entry> entries) {
            Comparator<Integer> amountAscending = Comparator
                    .comparingDouble((Integer id) -> entries.get(id).amount(matterType))
                    .thenComparingInt(Integer::intValue);
            Comparator<Integer> amountDescending = Comparator
                    .<Integer>comparingDouble(id -> entries.get(id).amount(matterType))
                    .reversed()
                    .thenComparingInt(Integer::intValue);
            Comparator<Integer> nameAscending = Comparator
                    .comparing((Integer id) -> entries.get(id).displayNameKey())
                    .thenComparingInt(Integer::intValue);
            Comparator<Integer> nameDescending = Comparator
                    .comparing(
                            (Integer id) -> entries.get(id).displayNameKey(),
                            Comparator.reverseOrder())
                    .thenComparingInt(Integer::intValue);

            int entryCount = entries.size();
            IntPredicate positive = id -> entries.get(id).amount(matterType) > 0;
            IntPredicate onlyHas = id -> positive.test(id)
                    && entries.get(id).matterTypeCount() == 1;
            IntPredicate zero = id -> entries.get(id).amount(matterType) == 0;
            double[] amountsByEntryId = new double[entryCount];
            for (int entryId = 0; entryId < entryCount; entryId++) {
                amountsByEntryId[entryId] = entries.get(entryId).amount(matterType);
            }

            return new MatterIndex(
                    matterType,
                    amountsByEntryId,
                    sortedEntryIds(entryCount, ignored -> true, amountAscending),
                    sortedEntryIds(entryCount, ignored -> true, amountDescending),
                    sortedEntryIds(entryCount, ignored -> true, nameAscending),
                    sortedEntryIds(entryCount, ignored -> true, nameDescending),
                    sortedEntryIds(entryCount, positive, amountAscending),
                    sortedEntryIds(entryCount, positive, amountDescending),
                    sortedEntryIds(entryCount, positive, nameAscending),
                    sortedEntryIds(entryCount, positive, nameDescending),
                    sortedEntryIds(entryCount, onlyHas, amountAscending),
                    sortedEntryIds(entryCount, onlyHas, amountDescending),
                    sortedEntryIds(entryCount, onlyHas, nameAscending),
                    sortedEntryIds(entryCount, onlyHas, nameDescending),
                    stableEntryIds(entryCount, zero),
                    sortedEntryIds(entryCount, zero, nameAscending),
                    sortedEntryIds(entryCount, zero, nameDescending)
            );
        }

        private MatterIndex withEntries(List<Entry> renamedEntries) {
            Comparator<Integer> nameAscending = Comparator
                    .comparing((Integer id) -> renamedEntries.get(id).displayNameKey())
                    .thenComparingInt(Integer::intValue);
            Comparator<Integer> nameDescending = Comparator
                    .comparing(
                            (Integer id) -> renamedEntries.get(id).displayNameKey(),
                            Comparator.reverseOrder())
                    .thenComparingInt(Integer::intValue);
            IntPredicate positive = id -> amountsByEntryId[id] > 0;
            IntPredicate onlyHas = id -> positive.test(id)
                    && renamedEntries.get(id).matterTypeCount() == 1;
            IntPredicate zero = id -> amountsByEntryId[id] == 0;

            return new MatterIndex(
                    matterType,
                    amountsByEntryId,
                    allByAmountAscending,
                    allByAmountDescending,
                    sortedEntryIds(renamedEntries.size(), ignored -> true, nameAscending),
                    sortedEntryIds(renamedEntries.size(), ignored -> true, nameDescending),
                    positiveByAmountAscending,
                    positiveByAmountDescending,
                    sortedEntryIds(renamedEntries.size(), positive, nameAscending),
                    sortedEntryIds(renamedEntries.size(), positive, nameDescending),
                    onlyHasByAmountAscending,
                    onlyHasByAmountDescending,
                    sortedEntryIds(renamedEntries.size(), onlyHas, nameAscending),
                    sortedEntryIds(renamedEntries.size(), onlyHas, nameDescending),
                    zeroStable,
                    sortedEntryIds(renamedEntries.size(), zero, nameAscending),
                    sortedEntryIds(renamedEntries.size(), zero, nameDescending)
            );
        }

        private MatterOpediaResult query(MatterOpediaQuery query) {
            return switch (query.filterMode()) {
                case NONE -> directResult(
                        query.sortType(), query.descending(),
                        positiveByAmountAscending, positiveByAmountDescending,
                        positiveByNameAscending, positiveByNameDescending);
                case ONLY_HAS -> directResult(
                        query.sortType(), query.descending(),
                        onlyHasByAmountAscending, onlyHasByAmountDescending,
                        onlyHasByNameAscending, onlyHasByNameDescending);
                case DOESNT_HAVE -> query.sortType() == MatterOpediaQuery.SortType.AMOUNT
                        ? MatterOpediaResult.all(zeroStable)
                        : MatterOpediaResult.all(query.descending()
                        ? zeroByNameDescending : zeroByNameAscending);
                case AMOUNT_EQUAL, AMOUNT_LESS, AMOUNT_GREATER ->
                        query.sortType() == MatterOpediaQuery.SortType.AMOUNT
                                ? amountRange(query)
                                : scanNameOrder(query);
            };
        }

        private MatterOpediaResult directResult(
                MatterOpediaQuery.SortType sortType,
                boolean descending,
                int[] amountAscending,
                int[] amountDescending,
                int[] nameAscending,
                int[] nameDescending
        ) {
            if (sortType == MatterOpediaQuery.SortType.AMOUNT) {
                return MatterOpediaResult.all(descending ? amountDescending : amountAscending);
            }
            return MatterOpediaResult.all(descending ? nameDescending : nameAscending);
        }

        private MatterOpediaResult amountRange(MatterOpediaQuery query) {
            int from;
            int to;
            switch (query.filterMode()) {
                case AMOUNT_EQUAL -> {
                    from = lowerBound(query.amount());
                    to = upperBound(query.amount());
                }
                case AMOUNT_LESS -> {
                    from = 0;
                    to = lowerBound(query.amount());
                }
                case AMOUNT_GREATER -> {
                    from = upperBound(query.amount());
                    to = allByAmountAscending.length;
                }
                default -> throw new IllegalArgumentException(
                        "Filter mode does not define an amount range: " + query.filterMode());
            }

            if (!query.descending()) {
                return MatterOpediaResult.range(allByAmountAscending, from, to);
            }
            int entryCount = allByAmountAscending.length;
            return MatterOpediaResult.range(
                    allByAmountDescending, entryCount - to, entryCount - from);
        }

        private int lowerBound(double target) {
            int low = 0;
            int high = allByAmountAscending.length;
            while (low < high) {
                int mid = (low + high) >>> 1;
                if (amount(allByAmountAscending[mid]) < target) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            return low;
        }

        private int upperBound(double target) {
            int low = 0;
            int high = allByAmountAscending.length;
            while (low < high) {
                int mid = (low + high) >>> 1;
                if (amount(allByAmountAscending[mid]) <= target) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            return low;
        }

        private MatterOpediaResult scanNameOrder(MatterOpediaQuery query) {
            int[] nameOrder = query.descending()
                    ? allByNameDescending : allByNameAscending;
            IntBuffer matchingEntryIds = new IntBuffer(nameOrder.length);
            for (int entryId : nameOrder) {
                double value = amount(entryId);
                boolean matches = switch (query.filterMode()) {
                    case AMOUNT_EQUAL -> value == query.amount();
                    case AMOUNT_LESS -> value < query.amount();
                    case AMOUNT_GREATER -> value > query.amount();
                    default -> throw new IllegalArgumentException(
                            "Filter mode does not define an amount predicate: "
                                    + query.filterMode());
                };
                if (matches) {
                    matchingEntryIds.add(entryId);
                }
            }
            return MatterOpediaResult.all(matchingEntryIds.toArray());
        }

        private double amount(int entryId) {
            return amountsByEntryId[entryId];
        }

        private static int[] sortedEntryIds(
                int entryCount,
                IntPredicate inclusion,
                Comparator<Integer> comparator
        ) {
            List<Integer> entryIds = new ArrayList<>(entryCount);
            for (int entryId = 0; entryId < entryCount; entryId++) {
                if (inclusion.test(entryId)) {
                    entryIds.add(entryId);
                }
            }
            entryIds.sort(comparator);
            return toPrimitiveArray(entryIds);
        }

        private static int[] stableEntryIds(int entryCount, IntPredicate inclusion) {
            IntBuffer entryIds = new IntBuffer(entryCount);
            for (int entryId = 0; entryId < entryCount; entryId++) {
                if (inclusion.test(entryId)) {
                    entryIds.add(entryId);
                }
            }
            return entryIds.toArray();
        }

        private static int[] toPrimitiveArray(List<Integer> entryIds) {
            int[] result = new int[entryIds.size()];
            for (int index = 0; index < entryIds.size(); index++) {
                result[index] = entryIds.get(index);
            }
            return result;
        }
    }

    private static final class IntBuffer {
        private int[] values;
        private int size;

        private IntBuffer(int maximumSize) {
            values = new int[Math.min(maximumSize, 16)];
        }

        private void add(int value) {
            if (size == values.length) {
                int nextLength = values.length == 0
                        ? 1 : Math.min(values.length * 2, Integer.MAX_VALUE - 8);
                values = Arrays.copyOf(values, nextLength);
            }
            values[size++] = value;
        }

        private int[] toArray() {
            return Arrays.copyOf(values, size);
        }
    }
}
