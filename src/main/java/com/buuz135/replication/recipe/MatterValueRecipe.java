package com.buuz135.replication.recipe;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.calculation.MatterValue;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

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
    public ItemStack assemble(CraftingInput craftingInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.input);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return new RecipeBookCategory();
    }

    @Override
    public RecipeSerializer<? extends Recipe<CraftingInput>> getSerializer() {
        return (RecipeSerializer<? extends Recipe<CraftingInput>>) (RecipeSerializer<?>) ReplicationRegistry.Serializers.MATTER_VALUE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<CraftingInput>> getType() {
        return (RecipeType<? extends Recipe<CraftingInput>>) (RecipeType<?>) ReplicationRegistry.CustomRecipeTypes.MATTER_VALUE_RECIPE_TYPE.get();
    }
}
