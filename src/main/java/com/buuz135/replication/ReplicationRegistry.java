package com.buuz135.replication;

import com.buuz135.replication.aequivaleo.ReplicationCompoundType;
import com.buuz135.replication.aequivaleo.ReplicationCompoundTypeGroup;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.matter_fluid.IMatterHandler;
import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.ldtteam.aequivaleo.api.compound.type.group.ICompoundTypeGroup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Supplier;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class ReplicationRegistry {

    public static final ResourceKey<Registry<IMatterType>> MATTER_TYPES_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Replication.MOD_ID, "matter_types"));
    public static Supplier<IForgeRegistry<IMatterType>> MATTER_TYPES_REGISTRY = null;

    public static void init(){
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TYPE_GROUPS.register(eventBus);
        TYPES.register(eventBus);
        Matter.IMATTER_TYPES.register(eventBus);
    }
    public static final DeferredRegister<ICompoundType> TYPES = DeferredRegister.create(new ResourceLocation("aequivaleo", "compound_type"), Replication.MOD_ID);
    public static final DeferredRegister<ICompoundTypeGroup> TYPE_GROUPS = DeferredRegister.create(new ResourceLocation("aequivaleo", "compound_type_group"), Replication.MOD_ID);

    //TYPE GROUPS
    public static final RegistryObject<ICompoundTypeGroup> MATTER_TYPES_GROUPS = TYPE_GROUPS.register("matter_types", ReplicationCompoundTypeGroup::new);

    //MATTER TYPES
    public static final RegistryObject<ICompoundType> EARTH = TYPES.register("earth", () -> new ReplicationCompoundType(MatterType.EARTH, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> NETHER = TYPES.register("nether", () -> new ReplicationCompoundType(MatterType.NETHER, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> ORGANIC = TYPES.register("organic", () -> new ReplicationCompoundType(MatterType.ORGANIC, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> ENDER = TYPES.register("ender", () -> new ReplicationCompoundType(MatterType.ENDER, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> METALLIC = TYPES.register("metallic", () -> new ReplicationCompoundType(MatterType.METALLIC, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> PRECIOUS = TYPES.register("precious", () -> new ReplicationCompoundType(MatterType.PRECIOUS, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> QUANTUM = TYPES.register("quantum", () -> new ReplicationCompoundType(MatterType.QUANTUM, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> LIVING = TYPES.register("living", () -> new ReplicationCompoundType(MatterType.LIVING, MATTER_TYPES_GROUPS));


    public static class Blocks{

        public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> REPLICATOR = null;
        public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> IDENTIFICATION_CHAMBER = null;
        public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> DISINTEGRATOR = null;
        public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> MATTER_NETWORK_PIPE = null;
        public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> MATTER_TANK = null;
        public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> REPLICATION_TERMINAL = null;
        public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> CHIP_STORAGE = null;

    }

    public static class Items{

        public static RegistryObject<Item> MEMORY_CHIP;
        public static RegistryObject<Item> MATTER_BLUEPRINT;

    }

    public static class Sounds{

        //public static RegistryObject<SoundEvent> IDENTIFICATION_CHAMBER = null;

    }

    public static class Matter{

        public static final DeferredRegister<IMatterType> IMATTER_TYPES = DeferredRegister.create(MATTER_TYPES_KEY, Replication.MOD_ID);

        public static final RegistryObject<IMatterType> EMPTY = IMATTER_TYPES.register("empty", () -> MatterType.EMPTY);
        public static final RegistryObject<IMatterType> METALLIC = IMATTER_TYPES.register("metallic", () -> MatterType.METALLIC);
        public static final RegistryObject<IMatterType> EARTH = IMATTER_TYPES.register("earth", () -> MatterType.EARTH);
        public static final RegistryObject<IMatterType> NETHER = IMATTER_TYPES.register("nether", () -> MatterType.NETHER);
        public static final RegistryObject<IMatterType> ORGANIC = IMATTER_TYPES.register("organic", () -> MatterType.ORGANIC);
        public static final RegistryObject<IMatterType> ENDER = IMATTER_TYPES.register("ender", () -> MatterType.ENDER);
        public static final RegistryObject<IMatterType> PRECIOUS = IMATTER_TYPES.register("precious", () -> MatterType.PRECIOUS);
        public static final RegistryObject<IMatterType> QUANTUM = IMATTER_TYPES.register("quantum", () -> MatterType.QUANTUM);
        public static final RegistryObject<IMatterType> LIVING = IMATTER_TYPES.register("living", () -> MatterType.LIVING);

    }

    public static class Colors{

        public static float[] GREEN_SPLIT = new float[]{23/255f,229/255f,23/255f};
        public static int GREEN = Mth.color(GREEN_SPLIT[0], GREEN_SPLIT[1], GREEN_SPLIT[2]);

    }

    public static class Capabilities{

        public static Capability<IMatterHandler> MATTER_HANDLER = get(new CapabilityToken<>(){});

    }


}
