package com.buuz135.replication.util;

import com.buuz135.replication.Replication;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ReplicationTags {

    public static TagKey<Item> CANT_BE_SCANNED = TagUtil.getOrCreateTag(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "cant_be_scanned"));
    public static TagKey<Item> CANT_BE_DISINTEGRATED = TagUtil.getOrCreateTag(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "cant_be_disintegrated"));

}
