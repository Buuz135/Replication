package com.buuz135.replication.calculation.client.matteropedia;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record MatterOpediaQuery(
        ResourceLocation matterType,
        FilterMode filterMode,
        int amount,
        SortType sortType,
        boolean descending
) {
    public MatterOpediaQuery {
        Objects.requireNonNull(matterType, "matterType");
        Objects.requireNonNull(filterMode, "filterMode");
        Objects.requireNonNull(sortType, "sortType");
    }

    public enum FilterMode {
        NONE, AMOUNT_EQUAL, AMOUNT_LESS, AMOUNT_GREATER, DOESNT_HAVE, ONLY_HAS
    }

    public enum SortType {
        AMOUNT, DISPLAY_NAME
    }
}
