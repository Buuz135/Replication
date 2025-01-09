package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ReplicationItemTagsProvider extends ItemTagsProvider {


    public ReplicationItemTagsProvider(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> lookupCompletableFuture, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator.getPackOutput(), completableFuture, lookupCompletableFuture, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        this.copy(TagUtil.getBlockTag(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/replica")), TagUtil.getItemTag(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/replica")));
        this.copy(TagUtil.getBlockTag(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_replica")), TagUtil.getItemTag(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_replica")));
        this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
        this.copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);
        this.copy(TagUtil.getBlockTag(ResourceLocation.fromNamespaceAndPath("c", "ores/replica")), TagUtil.getItemTag(ResourceLocation.fromNamespaceAndPath("c", "ores/replica")));

        this.tag(Tags.Items.INGOTS).add(ReplicationRegistry.Items.REPLICA_INGOT.get());
        this.tag(TagUtil.getItemTag(ResourceLocation.fromNamespaceAndPath("c", "ingots/replica"))).add(ReplicationRegistry.Items.REPLICA_INGOT.get());
        this.tag(Tags.Items.RAW_MATERIALS).add(ReplicationRegistry.Items.RAW_REPLICA.value());
        this.tag(TagUtil.getItemTag(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/replica"))).add(ReplicationRegistry.Items.RAW_REPLICA.get());
    }
}
