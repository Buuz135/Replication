package com.buuz135.replication.calculation;

import net.minecraft.world.item.ItemStack;

// Needed to ensure hashCode is valid for storing in maps.
public record ItemVariant(ItemStack stack) {
    @Override
    public int hashCode() {
        return ItemStack.hashItemAndComponents(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemVariant(ItemStack otherStack))) return false;
        return ItemStack.isSameItemSameComponents(stack, otherStack);
    }
}
