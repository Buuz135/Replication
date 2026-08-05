package com.buuz135.replication.calculation.client.matteropedia;

import java.util.Objects;

public final class MatterOpediaResult {
    private static final MatterOpediaResult EMPTY = new MatterOpediaResult(new int[0], 0, 0);

    private final int[] entryIds;
    private final int fromInclusive;
    private final int toExclusive;

    private MatterOpediaResult(int[] entryIds, int fromInclusive, int toExclusive) {
        this.entryIds = entryIds;
        this.fromInclusive = fromInclusive;
        this.toExclusive = toExclusive;
    }

    public static MatterOpediaResult empty() {
        return EMPTY;
    }

    public static MatterOpediaResult all(int[] entryIds) {
        return range(entryIds, 0, entryIds.length);
    }

    public static MatterOpediaResult range(int[] entryIds, int fromInclusive, int toExclusive) {
        Objects.requireNonNull(entryIds, "entryIds");
        Objects.checkFromToIndex(fromInclusive, toExclusive, entryIds.length);
        return fromInclusive == toExclusive ? EMPTY
                : new MatterOpediaResult(entryIds, fromInclusive, toExclusive);
    }

    public int size() {
        return toExclusive - fromInclusive;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int entryIdAt(int visibleIndex) {
        Objects.checkIndex(visibleIndex, size());
        return entryIds[fromInclusive + visibleIndex];
    }
}
