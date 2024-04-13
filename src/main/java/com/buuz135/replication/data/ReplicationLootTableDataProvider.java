package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.datagenerator.loot.TitaniumLootTableProvider;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.util.NonNullLazy;

import java.util.List;

public class ReplicationLootTableDataProvider extends TitaniumLootTableProvider {

    private final NonNullLazy<List<Block>> blocksToProcess;

    public ReplicationLootTableDataProvider(DataGenerator dataGenerator, NonNullLazy<List<Block>> blocks) {
        super(dataGenerator, blocks);
        this.blocksToProcess = blocks;
    }

    @Override
    protected BasicBlockLootTables createBlockLootTables() {
        return new BasicBlockLootTables(this.blocksToProcess){
            @Override
            protected void generate() {
                super.generate();
                add(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get(),
                        createSilkTouchDispatchTable(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get(),
                                (LootPoolEntryContainer.Builder)this.applyExplosionDecay(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get(), LootItem.lootTableItem(ReplicationRegistry.Items.RAW_REPLICA.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))));
            }
        };
    }
}
