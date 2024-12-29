package com.buuz135.replication.data;

import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;


public class RepModelProvider extends BlockModelProvider {

    public RepModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
