package com.buuz135.replication.recipe;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.calculation.MatterValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.JSONSerializableDataHandler;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class MatterValueRecipe extends SerializableRecipe {


    static {
        JSONSerializableDataHandler.map(MatterValue[].class, matterValues -> {
            JsonArray array = new JsonArray();
            for (MatterValue matterValue : matterValues) {
                JsonObject element = new JsonObject();
                element.addProperty("type", ReplicationRegistry.MATTER_TYPES_REGISTRY.get().getKey(matterValue.getMatter()).toString());
                element.addProperty("value", matterValue.getAmount());
                array.add(element);
            }
            return array;
        }, jsonElement -> {
            List<MatterValue> values = new ArrayList<>();
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                values.add(new MatterValue(ReplicationRegistry.MATTER_TYPES_REGISTRY.get().getValue(new ResourceLocation(element.getAsJsonObject().get("type").getAsString())), element.getAsJsonObject().get("value").getAsDouble()));
            }
            return values.toArray(new MatterValue[0]);
        });
    }

    public Ingredient input;
    public MatterValue[] matter;

    public MatterValueRecipe(ResourceLocation resourceLocation, Ingredient input, MatterValue... matter) {
        super(resourceLocation);
        this.input = input;
        this.matter = matter;
    }

    public MatterValueRecipe(ResourceLocation pId) {
        super(pId);
    }


    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
<<<<<<<HEAD
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return null;
=======
        public ItemStack assemble (CraftingInput craftingInput, HolderLookup.Provider provider){
            return ItemStack.EMPTY;
>>>>>>>9415 b55(Return empty on the recipe)
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
<<<<<<<HEAD
    public ItemStack getResultItem(RegistryAccess registryAccess) {
=======
            public ItemStack getResultItem (HolderLookup.Provider provider){
>>>>>>>9415 b55(Return empty on the recipe)
        return ItemStack.EMPTY;
    }

    @Override
    public GenericSerializer<? extends SerializableRecipe> getSerializer() {
        return (GenericSerializer<? extends SerializableRecipe>) ReplicationRegistry.Serializers.MATTER_VALUE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ReplicationRegistry.CustomRecipeTypes.MATTER_VALUE_RECIPE_TYPE.get();
    }
}
