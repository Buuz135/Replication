package com.buuz135.replication.util;

import com.buuz135.replication.Replication;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class ReplicationTags {

    public static TagKey<Item> CANT_BE_SCANNED = TagUtil.getOrCreateTag(ForgeRegistries.ITEMS, new ResourceLocation(Replication.MOD_ID, "cant_be_scanned"));
}
