package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ReplicationItemTagsProvider extends ItemTagsProvider {


    public ReplicationItemTagsProvider(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> lookupCompletableFuture, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator.getPackOutput(), completableFuture, lookupCompletableFuture, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        this.copy(TagUtil.getBlockTag(new ResourceLocation("c", "storage_blocks/replica")), TagUtil.getItemTag(new ResourceLocation("c", "storage_blocks/replica")));
        this.copy(TagUtil.getBlockTag(new ResourceLocation("c", "storage_blocks/raw_replica")), TagUtil.getItemTag(new ResourceLocation("c", "storage_blocks/raw_replica")));
        this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
        this.copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);
        this.copy(TagUtil.getBlockTag(new ResourceLocation("c", "ores/replica")), TagUtil.getItemTag(new ResourceLocation("c", "ores/replica")));

        this.tag(Tags.Items.INGOTS).add(ReplicationRegistry.Items.REPLICA_INGOT.get());
        this.tag(TagUtil.getItemTag(new ResourceLocation("c", "ingots/replica"))).add(ReplicationRegistry.Items.REPLICA_INGOT.get());
        this.tag(Tags.Items.RAW_MATERIALS).add(ReplicationRegistry.Items.RAW_REPLICA.get());
        this.tag(TagUtil.getItemTag(new ResourceLocation("c", "raw_materials/replica"))).add(ReplicationRegistry.Items.RAW_REPLICA.get());
    }
}
