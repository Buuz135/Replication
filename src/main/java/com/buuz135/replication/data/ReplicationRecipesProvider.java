package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationAttachments;
import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapelessRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ReplicationRecipesProvider extends RecipeProvider {

    private final Supplier<List<Block>> blocksToProcess;

    public ReplicationRecipesProvider(HolderLookup.Provider registries, RecipeOutput output, Supplier<List<Block>> blocksToProcess) {
        super(registries, output);
        this.blocksToProcess = blocksToProcess;
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Replication.MOD_ID, path));
    }

    @Override
    protected void buildRecipes() {
        for (Block block : blocksToProcess.get()) {
            if (block instanceof BasicTileBlock<?> tileBlock) {
                tileBlock.registerRecipe(output);
            }
        }
        TitaniumShapedRecipeBuilder.shapedRecipe(ReplicationRegistry.Items.MEMORY_CHIP.get())
                .pattern(" RI")
                .pattern("RIG")
                .pattern("IG ")
                .define('R', Items.REDSTONE)
                .define('I', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('G', Items.GOLD_INGOT)
                .save(output);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK.get()).requires(Ingredient.of(ReplicationRegistry.Items.RAW_REPLICA.get()), 9).save(output);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ReplicationRegistry.Items.RAW_REPLICA.get(), 9).requires(ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK.get()).save(output);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ReplicationRegistry.Blocks.REPLICA_BLOCK.get()).requires(Ingredient.of(ReplicationRegistry.Items.REPLICA_INGOT.get()), 9).save(output);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ReplicationRegistry.Items.REPLICA_INGOT.get(), 9).requires(ReplicationRegistry.Blocks.REPLICA_BLOCK.get()).save(output);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ReplicationRegistry.Items.RAW_REPLICA.get()), RecipeCategory.MISC, CookingBookCategory.MISC, ReplicationRegistry.Items.REPLICA_INGOT.get(), 0.35F, 200)
                .unlockedBy("has_raw_replica", has(ReplicationRegistry.Items.RAW_REPLICA.get())).save(output, recipeKey("smelting_raw_replica"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ReplicationRegistry.Items.RAW_REPLICA.get()), RecipeCategory.MISC, CookingBookCategory.MISC, ReplicationRegistry.Items.REPLICA_INGOT.get(), 0.35F, 100)
                .unlockedBy("has_raw_replica", has(ReplicationRegistry.Items.RAW_REPLICA.get())).save(output, recipeKey("blasting_raw_replica"));
        new MatterValueDataProvider().buildRecipes(output);
        TitaniumShapedRecipeBuilder.shapedRecipe(ReplicationRegistry.Items.REPLICATOR_ENCLOSURE.get())
                .pattern("GGG")
                .pattern("GRG")
                .pattern("GGG")
                .define('R', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('G', Items.PURPLE_STAINED_GLASS_PANE)
                .save(output);
        var tag = new CompoundTag();
        var itemTag = new CompoundTag();
        itemTag.putString("id", ReplicationRegistry.Items.REPLICATOR_MOTOR.getId().toString());
        itemTag.putInt("count", 1);
        tag.put("Item", itemTag);
        tag.putDouble("Progress", 1);
        var components = DataComponentPatch.builder().set(ReplicationAttachments.BLUEPRINT.get(), tag).build();
        var motorTemplate = new ItemStackTemplate(ReplicationRegistry.Items.MATTER_BLUEPRINT, components);
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, motorTemplate)
                .unlockedBy("has_item", has(ReplicationRegistry.Items.REPLICA_INGOT.get()))
                .pattern("RBR")
                .pattern("RPR")
                .pattern("RMR")
                .define('R', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('P', Items.PAPER)
                .define('B', Items.BLAZE_POWDER)
                .define('M', Items.PISTON)
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {

        private final Supplier<List<Block>> blocksToProcess;

        public Runner(DataGenerator generator, Supplier<List<Block>> blocksToProcess, CompletableFuture<HolderLookup.Provider> registries) {
            super(generator.getPackOutput(), registries);
            this.blocksToProcess = blocksToProcess;
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ReplicationRecipesProvider(registries, output, blocksToProcess);
        }

        @Override
        public String getName() {
            return "Replication Recipes";
        }
    }
}
