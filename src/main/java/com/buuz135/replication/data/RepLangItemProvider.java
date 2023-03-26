package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.ReplicatorBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class RepLangItemProvider extends LanguageProvider {

    private final List<Block> blocks;
    public RepLangItemProvider(DataGenerator gen, String modid, String locale, List<Block> blocks) {
        super(gen, modid, locale);
        this.blocks = blocks;
    }

    @Override
    protected void addTranslations() {
        this.add("itemGroup.replication", "Replication");
        this.blocks.forEach(block -> this.add(block, WordUtils.capitalize(ForgeRegistries.BLOCKS.getKey(block).getPath().replace("_", " "))));
        this.add(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getKey().get(), "Matter Network Pipe");
    }
}
