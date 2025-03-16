package com.buuz135.replication;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.block.*;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.block.tile.ReplicationMachine;
import com.buuz135.replication.calculation.ReplicationCalculation;
import com.buuz135.replication.client.ClientEvents;
import com.buuz135.replication.container.ReplicationTerminalContainer;
import com.buuz135.replication.data.*;
import com.buuz135.replication.item.CreativeMemoryChipItem;
import com.buuz135.replication.item.MatterBluePrintItem;
import com.buuz135.replication.item.MemoryChipItem;
import com.buuz135.replication.network.DefaultMatterNetworkElement;
import com.buuz135.replication.network.MatterNetwork;
import com.buuz135.replication.packet.*;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.block_network.NetworkRegistry;
import com.hrznstudio.titanium.block_network.element.NetworkElementRegistry;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.hrznstudio.titanium.reward.Reward;
import com.hrznstudio.titanium.reward.RewardGiver;
import com.hrznstudio.titanium.reward.RewardManager;
import com.hrznstudio.titanium.tab.TitaniumTab;
import guideme.Guide;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod("replication")
public class Replication extends ModuleController {

    public static String MOD_ID =  "replication";
    public static NetworkHandler NETWORK = new NetworkHandler(MOD_ID);
    public static TitaniumTab TAB = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"));

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public Replication(Dist dist, IEventBus modBus, ModContainer container) {
        super(container);
        NETWORK.registerMessage("pattern_sync_storage", PatternSyncStoragePacket.class);
        NETWORK.registerMessage("matter_fluid_sync", MatterFluidSyncPacket.class);
        NETWORK.registerMessage("task_create", TaskCreatePacket.class);
        NETWORK.registerMessage("task_sync", TaskSyncPacket.class);
        NETWORK.registerMessage("task_cancel", TaskCancelPacket.class);
        NETWORK.registerMessage("task_cancel_response", TaskCancelPacket.Response.class);
        NETWORK.registerMessage("replication_calculation", ReplicationCalculationPacket.class);
        ReplicationRegistry.init(modBus);
        CommonEvents.init();
        if (dist == Dist.CLIENT) {
            ClientEvents.init();
        }
        NetworkRegistry.INSTANCE.addFactory(MatterNetwork.MATTER, new MatterNetwork.Factory());
        NetworkElementRegistry.INSTANCE.addFactory(DefaultMatterNetworkElement.ID, new DefaultMatterNetworkElement.Factory());
        NBTManager.getInstance().scanTileClassForAnnotations(MatterPipeBlockEntity.class);
        EventManager.mod(RegisterCapabilitiesEvent.class).process(event -> {
            event.registerBlock(Capabilities.EnergyStorage.BLOCK, (level, blockPos, blockState, blockEntity, direction) -> {
                if (level instanceof ServerLevel && blockEntity instanceof MatterPipeBlockEntity pipe && NetworkManager.get(level) != null && NetworkManager.get(level).getElement(blockPos) != null) {
                    return pipe.getNetwork().getEnergyStorage();
                }
                return null;
            }, ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getBlock());
            event.registerBlock(Capabilities.EnergyStorage.BLOCK, (level, blockPos, blockState, blockEntity, direction) -> {
                if (blockState.getBlock() instanceof INetworkDirectionalConnection connection && connection.canConnect(level, blockPos, blockState, direction) && blockEntity instanceof ReplicationMachine<?> machine) {
                    return machine.getEnergyStorage();
                }
                return null;
            }, ReplicationRegistry.Blocks.DISINTEGRATOR.getBlock(), ReplicationRegistry.Blocks.IDENTIFICATION_CHAMBER.getBlock(), ReplicationRegistry.Blocks.REPLICATOR.getBlock());
        }).subscribe();

        if (ModList.get().isLoaded("darkmodeeverywhere")) {
            EventManager.mod(InterModEnqueueEvent.class).process(interModEnqueueEvent -> {
                InterModComms.sendTo("darkmodeeverywhere", "dme-shaderblacklist", () -> "com.buuz135.replication");
            }).subscribe();
        }
    }

    @Override
    protected void initModules() {
        this.addCreativeTab("main", () -> new ItemStack(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getBlock()), "replication", TAB);

        ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE = this.getRegistries().registerBlockWithTile("matter_network_pipe", MatterPipeBlock::new, TAB);
        ReplicationRegistry.Blocks.REPLICATOR = this.getRegistries().registerBlockWithTile("replicator", ReplicatorBlock::new, TAB);
        ReplicationRegistry.Blocks.IDENTIFICATION_CHAMBER = this.getRegistries().registerBlockWithTile("identification_chamber", IdentificationChamberBlock::new, TAB);
        ReplicationRegistry.Blocks.DISINTEGRATOR = this.getRegistries().registerBlockWithTile("disintegrator", DisintegratorBlock::new, TAB);
        ReplicationRegistry.Blocks.MATTER_TANK = this.getRegistries().registerBlockWithTile("matter_tank", MatterTankBlock::new, TAB);
        ReplicationRegistry.Blocks.REPLICATION_TERMINAL = this.getRegistries().registerBlockWithTile("replication_terminal", ReplicationTerminalBlock::new, TAB);
        ReplicationRegistry.Blocks.CHIP_STORAGE = this.getRegistries().registerBlockWithTile("chip_storage", ChipStorageBlock::new, TAB);

        ReplicationRegistry.Items.MEMORY_CHIP = this.getRegistries().registerGeneric(Registries.ITEM, "memory_chip", MemoryChipItem::new);
        ReplicationRegistry.Items.CREATIVE_MEMORY_CHIP = this.getRegistries().registerGeneric(Registries.ITEM, "creative_memory_chip", CreativeMemoryChipItem::new);
        ReplicationRegistry.Items.MATTER_BLUEPRINT = this.getRegistries().registerGeneric(Registries.ITEM, "matter_blueprint", MatterBluePrintItem::new);

        ReplicationRegistry.Sounds.TERMINAL_BUTTON = this.getRegistries().registerGeneric(Registries.SOUND_EVENT, "terminal_button", () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "terminal_button"), 8));

        ReplicationTerminalContainer.TYPE = getRegistries().registerGeneric(Registries.MENU, "replication_terminal_container", () -> (MenuType) IMenuTypeExtension.create(ReplicationTerminalContainer::new));

        ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE = getRegistries().registerGeneric(Registries.BLOCK, "deepslate_replica_ore", () -> new DropExperienceBlock(UniformInt.of(3, 7), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
        getRegistries().registerGeneric(Registries.ITEM, "deepslate_replica_ore", () -> {
            var item = new BlockItem(ReplicationRegistry.Blocks.DEEPSLATE_REPLICA_ORE.get(), new Item.Properties());
            TAB.getTabList().add(item);
            return item;
        });
        ReplicationRegistry.Blocks.REPLICA_BLOCK = getRegistries().registerGeneric(Registries.BLOCK, "replica_block", () -> new Block(BlockBehaviour.
                Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .mapColor(MapColor.COLOR_BLACK)
                .requiresCorrectToolForDrops()
                .strength(4.5F, 3.0F)
                .sound(SoundType.METAL))
        );
        getRegistries().registerGeneric(Registries.ITEM, "replica_block", () -> {
            var item = new BlockItem(ReplicationRegistry.Blocks.REPLICA_BLOCK.get(), new Item.Properties());
            TAB.getTabList().add(item);
            return item;
        });
        ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK = getRegistries().registerGeneric(Registries.BLOCK, "raw_replica_block", () -> new Block(BlockBehaviour.
                Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK)
                .mapColor(MapColor.COLOR_BLACK)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 6.0F))
        );
        getRegistries().registerGeneric(Registries.ITEM, "raw_replica_block", () -> {
            var item = new BlockItem(ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK.get(), new Item.Properties());
            TAB.getTabList().add(item);
            return item;
        });
        ReplicationRegistry.Items.RAW_REPLICA = getRegistries().registerGeneric(Registries.ITEM, "raw_replica", () -> {
            var item = new Item(new Item.Properties());
            TAB.getTabList().add(item);
            return item;
        });
        ReplicationRegistry.Items.REPLICA_INGOT = getRegistries().registerGeneric(Registries.ITEM, "replica_ingot", () -> {
            var item = new Item(new Item.Properties());
            TAB.getTabList().add(item);
            return item;
        });
        EventManager.mod(BuildCreativeModeTabContentsEvent.class, EventPriority.LOW).process(buildCreativeModeTabContentsEvent -> {
            if (buildCreativeModeTabContentsEvent.getTabKey().location().equals(TAB.getResourceLocation())){
                for (IMatterType value : ReplicationRegistry.MATTER_TYPES_REGISTRY.stream().toList()) {
                    if (value.equals(MatterType.EMPTY)) continue;
                    var matterStack = new MatterStack(value, ReplicationConfig.MatterTank.CAPACITY);
                    var tile = new CompoundTag();
                    var tank = matterStack.writeToNBT(new CompoundTag());
                    tile.put("tank", tank);
                    var item = new ItemStack(ReplicationRegistry.Blocks.MATTER_TANK);
                    item.set(ReplicationAttachments.TILE, tile);
                    buildCreativeModeTabContentsEvent.accept(item);
                }
            }
        }).subscribe();

        ReplicationCalculation.init();

        if (ModList.get().isLoaded("guideme")) {
            Guide.builder(ResourceLocation.parse("replication:main")).build();
        }

        RewardGiver giver = RewardManager.get().getGiver(UUID.fromString("d28b7061-fb92-4064-90fb-7e02b95a72a6"), "Buuz135");
        try {
            giver.addReward(new Reward(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "aura"), new URL("https://raw.githubusercontent.com/Buuz135/Industrial-Foregoing/master/contributors.json"), () -> dist -> {
            }, Arrays.stream(AuraType.values()).map(Enum::toString).collect(Collectors.toList()).toArray(new String[]{})));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        List<Block> blocks = BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(Replication.MOD_ID)).toList();

        //event.getGenerator().addProvider(true, new AequivaleoDataProvider(MOD_ID, event.getGenerator()));
        //event.getGenerator().addProvider(true, new RepBlockstateProvider(event.getGenerator(), MOD_ID, event.getExistingFileHelper(), blocks));
        event.getGenerator().addProvider(true, new ReplicationLootTableDataProvider(event.getGenerator(), () -> blocks, event.getLookupProvider()));
        event.getGenerator().addProvider(true, new RepLangItemProvider(event.getGenerator(), MOD_ID, "en_us", blocks));
        var blockTags = new ReplicationBlockTagsProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), MOD_ID, event.getExistingFileHelper(), blocks);
        event.getGenerator().addProvider(true, blockTags);
        event.getGenerator().addProvider(true, new ReplicationItemTagsProvider(event.getGenerator(), event.getLookupProvider(), blockTags.contentsGetter(), MOD_ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new ReplicationRecipesProvider(event.getGenerator(), () -> blocks, event.getLookupProvider()));
    }

    public enum AuraType {
        REPLICATION(ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/shader_only_green.png"), true),
        ORE(ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/replica_ore.png"), true),
        BLUE_BOSS(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/boss_bar/blue_background.png"), true),
        PINK_BOSS(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/boss_bar/pink_background.png"), true),
        EVIL(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/container/beacon/cancel.png"), true),
        POWDER(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/powder_snow_outline.png"), true),
        FORCE_FIELD(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/forcefield.png"), false),
        UNDERWATER(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/underwater.png"), false),
        SPOOK(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/pumpkinblur.png"), false),
        END(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/environment/end_sky.png"), false),
        CLOUDS(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/environment/clouds.png"), false),
        RAIN(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/environment/rain.png"), true),
        SGA(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/font/ascii_sga.png"), false),
        ENCHANTED(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/enchanted_glint_item.png"), true),
        RECIPE_BOOK(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/recipe_book.png"), true),
        END_PORTAL(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/end_portal.png"), true),
        MOON(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/environment/moon_phases.png"), true);

        private final ResourceLocation resourceLocation;
        private final boolean usePipeShader;

        AuraType(ResourceLocation resourceLocation, boolean enableBlend) {
            this.resourceLocation = resourceLocation;
            this.usePipeShader = enableBlend;
        }

        public ResourceLocation getResourceLocation() {
            return resourceLocation;
        }

        public boolean isUsePipeShader() {
            return usePipeShader;
        }
    }
}
