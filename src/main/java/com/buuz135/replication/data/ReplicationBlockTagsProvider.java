package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReplicationBlockTagsProvider extends BlockTagsProvider {

    private final List<Block> blocks;

    public ReplicationBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper, List<Block> blocks) {
        super(output, lookupProvider, modId, existingFileHelper);
        this.blocks = blocks;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(blocks.toArray(Block[]::new));
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS).add(ReplicationRegistry.Blocks.REPLICA_BLOCK.get(), ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK.get());
        this.tag(TagUtil.getBlockTag(new ResourceLocation("forge", "storage_blocks/replica"))).add(ReplicationRegistry.Blocks.REPLICA_BLOCK.get());
        this.tag(TagUtil.getBlockTag(new ResourceLocation("forge", "storage_blocks/raw_replica"))).add(ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK.get());
        this.tag(Tags.Blocks.ORES).add(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get());
        this.tag(TagUtil.getBlockTag(new ResourceLocation("forge", "ores/replica"))).add(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get());
    }
}
