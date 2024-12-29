package com.buuz135.replication.recipe;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.calculation.MatterValue;
import com.buuz135.replication.calculation.ReplicationCalculation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.JSONSerializableDataHandler;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatterValueRecipe implements Recipe<CraftingInput>  {


    public static final MapCodec<MatterValueRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
            Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
            MatterValue.CODEC.listOf().fieldOf("matter").forGetter(recipe -> recipe.matter)
    ).apply(in, MatterValueRecipe::new));

    public Ingredient input;
    public List<MatterValue> matter;

    public MatterValueRecipe(Ingredient input, MatterValue... matter) {
        this(input, Arrays.asList(matter));
    }

    public MatterValueRecipe(Ingredient input, List<MatterValue> matter) {
        super();
        this.input = input;
        this.matter = matter;
    }

    public MatterValueRecipe() {
        super();
    }


    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ReplicationRegistry.Serializers.MATTER_VALUE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ReplicationRegistry.CustomRecipeTypes.MATTER_VALUE_RECIPE_TYPE.get();
    }
}
