package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ReplicationRecipesProvider extends RecipeProvider {

    private final Supplier<List<Block>> blocksToProcess;

    public ReplicationRecipesProvider(DataGenerator generatorIn, Supplier<List<Block>> blocksToProcess, CompletableFuture<HolderLookup.Provider> registries) {
        super(generatorIn.getPackOutput(), registries);
        this.blocksToProcess = blocksToProcess;
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        for (Block block : blocksToProcess.get()) {
            if (block instanceof BasicTileBlock<?> rotatableBlock){
                rotatableBlock.registerRecipe(consumer);
            }
        }
        TitaniumShapedRecipeBuilder.shapedRecipe(ReplicationRegistry.Items.MEMORY_CHIP.get())
                .pattern(" RI")
                .pattern("RIG")
                .pattern("IG ")
                .define('R', Items.REDSTONE)
                .define('I', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('G', Items.GOLD_INGOT)
                .save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ReplicationRegistry.Items.RAW_REPLICA.get()), RecipeCategory.MISC, ReplicationRegistry.Items.REPLICA_INGOT.get(), 0.35F, 200)
                .unlockedBy("has_plastic", this.has(ReplicationRegistry.Items.RAW_REPLICA.get())).save(consumer, ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "smelting_raw_replica"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ReplicationRegistry.Items.RAW_REPLICA.get()), RecipeCategory.MISC, ReplicationRegistry.Items.REPLICA_INGOT.get(), 0.35F, 100)
                .unlockedBy("has_plastic", this.has(ReplicationRegistry.Items.RAW_REPLICA.get())).save(consumer, ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "blasting_raw_replica"));
        new MatterValueDataProvider().buildRecipes(consumer);
    }
}
