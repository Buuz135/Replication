package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.datagenerator.loot.TitaniumLootTableProvider;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ReplicationLootTableDataProvider extends TitaniumLootTableProvider {

    private final Supplier<List<Block>> blocksToProcess;

    public ReplicationLootTableDataProvider(DataGenerator dataGenerator, Supplier<List<Block>> blocks, CompletableFuture<HolderLookup.Provider> providerCompletableFuture) {
        super(dataGenerator, blocks, providerCompletableFuture);
        this.blocksToProcess = blocks;
    }
    @Override
    protected BasicBlockLootTables createBlockLootTables(HolderLookup.Provider prov) {
        return new BasicBlockLootTables(this.blocksToProcess, prov){
            @Override
            protected void generate() {
                super.generate();
                add(ReplicationRegistry.Blocks.REPLICA_BLOCK.get(), droppingSelf(ReplicationRegistry.Blocks.REPLICA_BLOCK.get()));
                add(ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK.get(), droppingSelf(ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK.get()));
                add(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get(),
                        createSilkTouchDispatchTable(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get(),
                                (LootPoolEntryContainer.Builder)this.applyExplosionDecay(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get(), LootItem.lootTableItem(ReplicationRegistry.Items.RAW_REPLICA.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).apply(ApplyBonusCount.addOreBonusCount(registries.holderOrThrow(Enchantments.FORTUNE))))));
            }
        };
    }
}
