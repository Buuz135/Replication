package com.buuz135.replication.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface IItemVariantProvider {
    // Get name of variant
    String getVariantKey(ItemStack stack);

    // Do not mutate stack.
    ItemStack normalize(ItemStack stack);

    Set<ItemStack> getVariants(HolderLookup.Provider registries);
}
