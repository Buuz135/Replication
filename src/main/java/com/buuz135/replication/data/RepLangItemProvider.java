package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.block.ReplicatorBlock;
import com.buuz135.replication.item.ReplicationItem;
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
        this.formatItem(ReplicationRegistry.Items.MEMORY_CHIP.get());
        this.formatItem(ReplicationRegistry.Items.MATTER_BLUEPRINT.get());
        this.formatItem(ReplicationRegistry.Items.RAW_REPLICA.get());
        this.formatItem(ReplicationRegistry.Items.REPLICA_INGOT.get());
        this.add("tooltip.replication.identification_chamber.slow_mode", "Slow Mode");
        this.add("tooltip.replication.identification_chamber.slow_mode.desc", "Scanning is slower but the item will");
        this.add("tooltip.replication.identification_chamber.slow_mode.desc_1", "only be consumed when reaching 100%");
        this.add("tooltip.replication.identification_chamber.fast_mode", "Fast Mode");
        this.add("tooltip.replication.identification_chamber.fast_mode.desc", "Scanning is much faster but the");
        this.add("tooltip.replication.identification_chamber.fast_mode.desc_1", "item can be consumed on each action");
        this.add("tooltip.replication.tank.matter", "Matter: ");
        this.add("relocation.blueprint.contains_information", "Contains information: ");
        this.add("relocation.blueprint.not_found", "Information not found");
        this.add("relocation.blueprint.use_on_chip_storage", "Scan it in the identification chamber to get the information or right click the Chip Storage to transfer directly");
        this.add("replication.parallel_mode", "Parallel Mode");
        this.add("replication.replicate", "Replicate");
        this.add("replication.crafting_tasks", "Replication Tasks");
        this.add("replication.current_crafting", "Replicating");
        this.add("replication.infinite_mode", "Infinite Mode");
        this.add("tooltip.replication.terminal.sorting_type.state_0", "Sorting Type: Amount");
        this.add("tooltip.replication.terminal.sorting_type.state_1", "Sorting Type: Name");
        this.add("tooltip.replication.terminal.sorting_direction.state_0", "Sorting Direction: Ascending");
        this.add("tooltip.replication.terminal.sorting_direction.state_1", "Sorting Direction: Descending");
        this.add("tooltip.replication.terminal.cancel_task", "Sneak + Click to Cancel Task");
        this.add("tooltip.replication.terminal.amount", "Amount: ");
        this.add("tooltip.replication.terminal.workers", "Workers: ");
        this.add("tooltip.replication.terminal.mode", "Mode: ");
        this.add("tooltip.replication.terminal.single", "Single");
        this.add("tooltip.replication.terminal.multiple", "Parallel");
        this.add("tooltip.replication.close", "Close");

        for (MatterType value : MatterType.values()) {
            this.add("replication.matter_type." + value.getName(), WordUtils.capitalize(value.getName()));
        }
    }

    private void formatItem(Item item){
        this.add(item, WordUtils.capitalize(ForgeRegistries.ITEMS.getKey(item).getPath().replace("_", " ")));
    }
}
