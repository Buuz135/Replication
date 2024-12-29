package com.buuz135.replication;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.matter_fluid.IMatterHandler;
import com.buuz135.replication.recipe.MatterValueRecipe;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.recipe.serializer.CodecRecipeSerializer;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


public class ReplicationRegistry {

    public static final ResourceKey<Registry<IMatterType>> MATTER_TYPES_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "matter_types"));
    public static Registry<IMatterType> MATTER_TYPES_REGISTRY = null;

    public static void init(IEventBus modEvent){
        CustomRecipeTypes.REC.register(modEvent);
        Serializers.SER.register(modEvent);
        ReplicationAttachments.DR.register(modEvent);
        Matter.IMATTER_TYPES.register(modEvent);
    }

    public static class Blocks{

        public static BlockWithTile REPLICATOR = null;
        public static BlockWithTile IDENTIFICATION_CHAMBER = null;
        public static BlockWithTile DISINTEGRATOR = null;
        public static BlockWithTile MATTER_NETWORK_PIPE = null;
        public static BlockWithTile MATTER_TANK = null;
        public static BlockWithTile REPLICATION_TERMINAL = null;
        public static BlockWithTile CHIP_STORAGE = null;
        public static DeferredHolder<Block, Block> DEEPSLATE_REPLICA_ORE = null;
        public static DeferredHolder<Block, Block> REPLICA_BLOCK = null;
        public static DeferredHolder<Block, Block> RAW_REPLICA_BLOCK = null;

    }

    public static class Items{

        public static DeferredHolder<Item, Item> MEMORY_CHIP;
        public static DeferredHolder<Item, Item> MATTER_BLUEPRINT;
        public static DeferredHolder<Item, Item> RAW_REPLICA;
        public static DeferredHolder<Item, Item> REPLICA_INGOT;


    }

    public static class Sounds{

        public static DeferredHolder<SoundEvent, SoundEvent> TERMINAL_BUTTON = null;

    }

    public static class Matter{

        public static final DeferredRegister<IMatterType> IMATTER_TYPES = DeferredRegister.create(MATTER_TYPES_KEY, Replication.MOD_ID);

        public static final DeferredHolder<IMatterType, IMatterType> EMPTY = IMATTER_TYPES.register("empty", () -> MatterType.EMPTY);
        public static final DeferredHolder<IMatterType, IMatterType> METALLIC = IMATTER_TYPES.register("metallic", () -> MatterType.METALLIC);
        public static final DeferredHolder<IMatterType, IMatterType> EARTH = IMATTER_TYPES.register("earth", () -> MatterType.EARTH);
        public static final DeferredHolder<IMatterType, IMatterType> NETHER = IMATTER_TYPES.register("nether", () -> MatterType.NETHER);
        public static final DeferredHolder<IMatterType, IMatterType> ORGANIC = IMATTER_TYPES.register("organic", () -> MatterType.ORGANIC);
        public static final DeferredHolder<IMatterType, IMatterType> ENDER = IMATTER_TYPES.register("ender", () -> MatterType.ENDER);
        public static final DeferredHolder<IMatterType, IMatterType> PRECIOUS = IMATTER_TYPES.register("precious", () -> MatterType.PRECIOUS);
        public static final DeferredHolder<IMatterType, IMatterType> QUANTUM = IMATTER_TYPES.register("quantum", () -> MatterType.QUANTUM);
        public static final DeferredHolder<IMatterType, IMatterType> LIVING = IMATTER_TYPES.register("living", () -> MatterType.LIVING);

    }

    public static class Colors{

        public static float[] GREEN_SPLIT = new float[]{23/255f,229/255f,23/255f};
        public static int GREEN = Mth.color(GREEN_SPLIT[0], GREEN_SPLIT[1], GREEN_SPLIT[2]);

    }

    public static class Capabilities{

        public static final BlockCapability<IMatterHandler, @Nullable Direction> MATTER_HANDLER =
                BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "matter_handler"), IMatterHandler.class);
;

    }

    public static class CustomRecipeTypes {

        public static final DeferredRegister<RecipeType<?>> REC = DeferredRegister.create(Registries.RECIPE_TYPE, Replication.MOD_ID);

        public static final DeferredHolder<RecipeType<?>, RecipeType<?>> MATTER_VALUE_RECIPE_TYPE = REC.register("matter_value", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "matter_value")));
    }

    public static class Serializers{

        public static final DeferredRegister<RecipeSerializer<?>> SER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Replication.MOD_ID);

        public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> MATTER_VALUE_RECIPE_SERIALIZER = SER.register("matter_value", () -> new CodecRecipeSerializer(MatterValueRecipe.class, CustomRecipeTypes.MATTER_VALUE_RECIPE_TYPE, MatterValueRecipe.CODEC ));

    }


}
