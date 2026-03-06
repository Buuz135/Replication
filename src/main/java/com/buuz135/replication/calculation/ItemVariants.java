package com.buuz135.replication.calculation;

import com.buuz135.replication.api.CollectItemVariantsEvent;
import com.buuz135.replication.api.IItemVariantProvider;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Should cache results.
public class ItemVariants {
    private static final Map<Item, IItemVariantProvider> providers = new HashMap<>();

    public static void init() {
        EventManager.mod(FMLCommonSetupEvent.class).process(commonSetupEvent -> {
            var event = new CollectItemVariantsEvent(providers::put);
            ModLoader.postEvent(event);
        }).subscribe();
    }

    public static String getName(ItemVariant variant) {
        if (providers.containsKey(variant.stack().getItem())) {
            return BuiltInRegistries.ITEM.getKey(variant.stack().getItem()).toString() +
                    providers.get(variant.stack().getItem()).getVariantKey(variant.stack());
        }

        return BuiltInRegistries.ITEM.getKey(variant.stack().getItem()).toString();
    }

    public static ItemVariant normalize(ItemStack stack) {
        if (providers.containsKey(stack.getItem())) {
            return new ItemVariant(providers.get(stack.getItem()).normalize(stack));
        }

        return new ItemVariant(stack.getItem().getDefaultInstance());
    }

    public static Set<ItemVariant> getVariants(Item item, HolderLookup.Provider registries) {
        if (providers.containsKey(item)) {
            return providers.get(item).getVariants(registries).stream().map(ItemVariant::new).collect(Collectors.toSet());
        }

        return Set.of(new ItemVariant(item.getDefaultInstance()));
    }
}
