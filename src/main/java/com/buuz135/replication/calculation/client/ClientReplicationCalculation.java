package com.buuz135.replication.calculation.client;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.calculation.MatterCompound;
import com.buuz135.replication.calculation.ReplicationCalculation;
import com.buuz135.replication.calculation.client.matteropedia.MatterOpediaCatalog;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class ClientReplicationCalculation {

    public static HashMap<String, MatterCompound> DEFAULT_MATTER_COMPOUND = new HashMap<String, MatterCompound>();
    private static volatile MatterOpediaCatalog matterOpediaCatalog = MatterOpediaCatalog.empty(0, 0);

    @Nullable
    public static MatterCompound getMatterCompound(ItemStack stack) {
        if (DEFAULT_MATTER_COMPOUND.containsKey(ReplicationCalculation.getNameFromStack(stack))){
            return DEFAULT_MATTER_COMPOUND.get(ReplicationCalculation.getNameFromStack(stack));
        }
        return null;
    }

    public static void acceptData(HolderLookup.Provider provider, CompoundTag compoundTag){
        DEFAULT_MATTER_COMPOUND.clear();
        for (String allKey : compoundTag.getAllKeys()) {
            var matter =  new MatterCompound();
            matter.deserializeNBT(provider, compoundTag.getCompound(allKey));
            DEFAULT_MATTER_COMPOUND.put(allKey, matter);
        }

        List<MatterOpediaCatalog.Seed> seeds = new ArrayList<>(DEFAULT_MATTER_COMPOUND.size());
        DEFAULT_MATTER_COMPOUND.forEach((itemName, compound) -> {
            ifRegisteredItem(itemName, (itemId, item) -> {
                ItemStack stack = item.getDefaultInstance();
                if (stack.isEmpty()) {
                    return;
                }

                Map<ResourceLocation, Double> amounts = new LinkedHashMap<>();
                compound.getValues().values().forEach(value -> {
                    ResourceLocation matterKey = ReplicationRegistry.MATTER_TYPES_REGISTRY.getKey(value.getMatter());
                    if (matterKey != null) {
                        amounts.put(matterKey, value.getAmount());
                    }
                });
                seeds.add(new MatterOpediaCatalog.Seed(
                        itemId,
                        compound,
                        amounts,
                        amounts.size(),
                        stack.getDisplayName().getString().toLowerCase(Locale.ROOT)
                ));
            });
        });

        MatterOpediaCatalog current = matterOpediaCatalog;
        MatterOpediaCatalog replacement = MatterOpediaCatalog.fromSeeds(
                seeds,
                getMatterTypeKeys(),
                current.generation() + 1,
                current.languageGeneration()
        );
        matterOpediaCatalog = replacement;
    }

    public static MatterOpediaCatalog getMatterOpediaCatalog() {
        return matterOpediaCatalog;
    }

    public static void rebuildMatterOpediaNameIndexes() {
        if (DEFAULT_MATTER_COMPOUND.isEmpty()) {
            return;
        }

        Map<ResourceLocation, String> displayNames = new LinkedHashMap<>();
        for (String itemName : DEFAULT_MATTER_COMPOUND.keySet()) {
            ifRegisteredItem(itemName, (itemId, item) -> {
                ItemStack stack = item.getDefaultInstance();
                if (!stack.isEmpty()) {
                    displayNames.put(
                            itemId,
                            stack.getDisplayName().getString().toLowerCase(Locale.ROOT)
                    );
                }
            });
        }
        if (displayNames.isEmpty()) {
            return;
        }

        MatterOpediaCatalog current = matterOpediaCatalog;
        MatterOpediaCatalog replacement = current.withDisplayNames(
                displayNames,
                current.languageGeneration() + 1
        );
        matterOpediaCatalog = replacement;
    }

    public static void clearClientData() {
        DEFAULT_MATTER_COMPOUND.clear();
        MatterOpediaCatalog current = matterOpediaCatalog;
        matterOpediaCatalog = MatterOpediaCatalog.empty(
                current.generation() + 1,
                current.languageGeneration()
        );
    }

    static void ifRegisteredItem(String itemName, BiConsumer<ResourceLocation, Item> action) {
        ResourceLocation itemId = ResourceLocation.tryParse(itemName);
        if (itemId == null) {
            return;
        }
        BuiltInRegistries.ITEM.getOptional(itemId).ifPresent(item -> action.accept(itemId, item));
    }

    private static Set<ResourceLocation> getMatterTypeKeys() {
        Set<ResourceLocation> matterTypeKeys = new LinkedHashSet<>();
        ReplicationRegistry.MATTER_TYPES_REGISTRY.forEach(matterType -> {
            if (matterType != ReplicationRegistry.Matter.EMPTY.get()) {
                ResourceLocation matterKey = ReplicationRegistry.MATTER_TYPES_REGISTRY.getKey(matterType);
                if (matterKey != null) {
                    matterTypeKeys.add(matterKey);
                }
            }
        });
        return matterTypeKeys;
    }
}
