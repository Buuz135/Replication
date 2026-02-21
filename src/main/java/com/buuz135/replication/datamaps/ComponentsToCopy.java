package com.buuz135.replication.datamaps;

import com.buuz135.replication.Replication;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.List;

public record ComponentsToCopy(List<DataComponentType<?>> components) {
    private static final Codec<ComponentsToCopy> CODEC = Codec.list(BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec())
            .xmap(ComponentsToCopy::new, ComponentsToCopy::components);

    public static final DataMapType<Item, ComponentsToCopy> DATA_MAP = DataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "components_to_copy"),
                    Registries.ITEM,
                    CODEC)
            .synced(CODEC, true)
            .build();

    public void copyComponents(ItemStack from, ItemStack to) {
        for (DataComponentType<?> component : components) {
            copyComponent(component, from, to);
        }
    }

    private <T> void copyComponent(DataComponentType<T> component, ItemStack from, ItemStack to) {
        to.set(component, from.get(component));
    }
}
