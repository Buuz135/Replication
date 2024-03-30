package com.buuz135.replication;

import com.buuz135.replication.block.*;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.client.ClientEvents;
import com.buuz135.replication.container.ReplicationTerminalContainer;
import com.buuz135.replication.data.AequivaleoDataProvider;
import com.buuz135.replication.data.RepBlockstateProvider;
import com.buuz135.replication.data.RepLangItemProvider;
import com.buuz135.replication.item.MemoryChipItem;
import com.buuz135.replication.network.DefaultMatterNetworkElement;
import com.buuz135.replication.network.MatterNetwork;
import com.buuz135.replication.packet.MatterFluidSyncPacket;
import com.buuz135.replication.packet.PatternSyncStoragePacket;
import com.buuz135.replication.packet.TaskCreatePacket;
import com.hrznstudio.titanium.block_network.NetworkRegistry;
import com.hrznstudio.titanium.block_network.element.NetworkElementRegistry;
import com.hrznstudio.titanium.datagenerator.loot.TitaniumLootTableProvider;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.hrznstudio.titanium.tab.TitaniumTab;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("replication")
public class Replication extends ModuleController {

    public static String MOD_ID =  "replication";
    public static NetworkHandler NETWORK = new NetworkHandler(MOD_ID);
    public static TitaniumTab TAB = new TitaniumTab(new ResourceLocation(MOD_ID, "main"));

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public Replication() {
        NETWORK.registerMessage(PatternSyncStoragePacket.class);
        NETWORK.registerMessage(MatterFluidSyncPacket.class);
        NETWORK.registerMessage(TaskCreatePacket.class);
        ReplicationRegistry.init();
        CommonEvents.init();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientEvents::init);
        NetworkRegistry.INSTANCE.addFactory(MatterNetwork.MATTER, new MatterNetwork.Factory());
        NetworkElementRegistry.INSTANCE.addFactory(DefaultMatterNetworkElement.ID, new DefaultMatterNetworkElement.Factory());
        NBTManager.getInstance().scanTileClassForAnnotations(MatterPipeBlockEntity.class);
    }

    @Override
    protected void initModules() {
        this.addCreativeTab("main", () -> new ItemStack(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getLeft().get()), "replication", TAB);

        ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE = this.getRegistries().registerBlockWithTile("matter_network_pipe", MatterPipeBlock::new, TAB);
        ReplicationRegistry.Blocks.REPLICATOR = this.getRegistries().registerBlockWithTile("replicator", ReplicatorBlock::new, TAB);
        ReplicationRegistry.Blocks.IDENTIFICATION_CHAMBER = this.getRegistries().registerBlockWithTile("identification_chamber", IdentificationChamberBlock::new, TAB);
        ReplicationRegistry.Blocks.DISINTEGRATOR = this.getRegistries().registerBlockWithTile("disintegrator", DisintegratorBlock::new, TAB);
        ReplicationRegistry.Blocks.MATTER_TANK = this.getRegistries().registerBlockWithTile("matter_tank", MatterTankBlock::new, TAB);
        ReplicationRegistry.Blocks.REPLICATION_TERMINAL = this.getRegistries().registerBlockWithTile("replication_terminal", ReplicationTerminalBlock::new, TAB);
        ReplicationRegistry.Blocks.CHIP_STORAGE = this.getRegistries().registerBlockWithTile("chip_storage", ChipStorageBlock::new, TAB);

        ReplicationRegistry.Items.MEMORY_CHIP = this.getRegistries().registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "memory_chip", MemoryChipItem::new);

        ReplicationRegistry.Sounds.IDENTIFICATION_CHAMBER = this.getRegistries().registerGeneric(ForgeRegistries.SOUND_EVENTS.getRegistryKey(), "identification_chamber", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(Replication.MOD_ID, "identification_chamber"), 8));

        ReplicationTerminalContainer.TYPE = getRegistries().registerGeneric(ForgeRegistries.MENU_TYPES.getRegistryKey(), "replication_terminal_container", () -> (MenuType) IForgeMenuType.create(ReplicationTerminalContainer::new));
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        List<Block> blocks = ForgeRegistries.BLOCKS.getValues().stream().filter(block -> ForgeRegistries.BLOCKS.getKey(block).getNamespace().equals(Replication.MOD_ID)).toList();

        event.getGenerator().addProvider(true, new AequivaleoDataProvider(MOD_ID, event.getGenerator()));
        event.getGenerator().addProvider(true, new RepBlockstateProvider(event.getGenerator(), MOD_ID, event.getExistingFileHelper(), blocks));
        event.getGenerator().addProvider(true, new TitaniumLootTableProvider(event.getGenerator(), NonNullLazy.of(() -> blocks)));
        event.getGenerator().addProvider(true, new RepLangItemProvider(event.getGenerator(), MOD_ID, "en_us", blocks));
    }
}
