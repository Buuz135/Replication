package com.buuz135.replication.api;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.BiConsumer;

public class CollectItemVariantsEvent extends Event implements IModBusEvent {
    private final BiConsumer<Item, IItemVariantProvider> consumer;

    public CollectItemVariantsEvent(BiConsumer<Item, IItemVariantProvider> consumer) {
        this.consumer = consumer;
    }

    public void add(Item item, IItemVariantProvider provider) {
        consumer.accept(item, provider);
    }
}
