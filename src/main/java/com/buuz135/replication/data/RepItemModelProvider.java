package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class RepItemModelProvider extends ItemModelProvider {

    private final List<Block> blocks;

    public RepItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper, List<Block> blocks) {
        super(generator.getPackOutput(), modid, existingFileHelper);
        this.blocks = blocks;
    }

    @Override
    protected void registerModels() {
        this.blocks.forEach(block -> getBuilder(ForgeRegistries.BLOCKS.getKey(block).getPath()).parent(new ModelFile.UncheckedModelFile(new ResourceLocation(Replication.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(block).getPath()))));
    }
}
