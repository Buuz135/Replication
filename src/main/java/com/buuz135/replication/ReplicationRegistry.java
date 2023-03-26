package com.buuz135.replication;

import com.buuz135.replication.aequivaleo.ReplicationCompoundType;
import com.buuz135.replication.aequivaleo.ReplicationCompoundTypeGroup;
import com.buuz135.replication.api.MatterType;
import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.ldtteam.aequivaleo.api.compound.type.group.ICompoundTypeGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

public class ReplicationRegistry {


    public static void init(){
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TYPE_GROUPS.register(eventBus);
        TYPES.register(eventBus);
    }
    public static final DeferredRegister<ICompoundType> TYPES = DeferredRegister.create(new ResourceLocation("aequivaleo", "compound_type"), Replication.MOD_ID);
    public static final DeferredRegister<ICompoundTypeGroup> TYPE_GROUPS = DeferredRegister.create(new ResourceLocation("aequivaleo", "compound_type_group"), Replication.MOD_ID);

    //TYPE GROUPS
    public static final RegistryObject<ICompoundTypeGroup> MATTER_TYPES_GROUPS = TYPE_GROUPS.register("matter_types", ReplicationCompoundTypeGroup::new);

    //MATTER TYPES
    public static final RegistryObject<ICompoundType> METALLIC = TYPES.register("metallic", () -> new ReplicationCompoundType(MatterType.METALLIC, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> EARTH = TYPES.register("earth", () -> new ReplicationCompoundType(MatterType.EARTH, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> NETHER = TYPES.register("nether", () -> new ReplicationCompoundType(MatterType.NETHER, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> ORGANIC = TYPES.register("organic", () -> new ReplicationCompoundType(MatterType.ORGANIC, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> ENDER = TYPES.register("ender", () -> new ReplicationCompoundType(MatterType.ENDER, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> PRECIOUS = TYPES.register("precious", () -> new ReplicationCompoundType(MatterType.PRECIOUS, MATTER_TYPES_GROUPS));
    public static final RegistryObject<ICompoundType> QUANTUM = TYPES.register("quantum", () -> new ReplicationCompoundType(MatterType.QUANTUM, MATTER_TYPES_GROUPS));

    public static final RegistryObject<ICompoundType> LIVING = TYPES.register("living", () -> new ReplicationCompoundType(MatterType.LIVING, MATTER_TYPES_GROUPS));


    public static class Blocks{

        public static Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> MATTER_NETWORK_PIPE = null;

    }


}
