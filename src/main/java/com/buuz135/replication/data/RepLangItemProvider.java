package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.block.ReplicatorBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class RepLangItemProvider extends LanguageProvider {

    private final List<Block> blocks;
    public RepLangItemProvider(DataGenerator gen, String modid, String locale, List<Block> blocks) {
        super(gen.getPackOutput(), modid, locale);
        this.blocks = blocks;
    }

    @Override
    protected void addTranslations() {
        this.add("itemGroup.replication", "Replication");
        this.blocks.forEach(block -> this.add(block, WordUtils.capitalize(ForgeRegistries.BLOCKS.getKey(block).getPath().replace("_", " "))));
        this.add(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getKey().get(), "Matter Network Pipe");
        this.formatItem(ReplicationRegistry.Items.MEMORY_CHIP.get());
        this.add("tooltip.replication.identification_chamber.slow_mode", "Slow Mode");
        this.add("tooltip.replication.identification_chamber.slow_mode.desc", "Scanning is slower but the item will");
        this.add("tooltip.replication.identification_chamber.slow_mode.desc_1", "only be consumed when reaching 100%");
        this.add("tooltip.replication.identification_chamber.fast_mode", "Fast Mode");
        this.add("tooltip.replication.identification_chamber.fast_mode.desc", "Scanning is much faster but the");
        this.add("tooltip.replication.identification_chamber.fast_mode.desc_1", "item can be consumed on each action");
        this.add("tooltip.replication.tank.matter", "Matter: ");
        for (MatterType value : MatterType.values()) {
            this.add("replication.matter_type." + value.getName(), WordUtils.capitalize(value.getName()));
        }
    }

    private void formatItem(Item item){
        this.add(item, WordUtils.capitalize(ForgeRegistries.ITEMS.getKey(item).getPath().replace("_", " ")));
    }
}
