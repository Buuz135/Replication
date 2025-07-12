package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationAttachments;
import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapelessRecipeBuilder;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class ReplicationRecipesProvider extends RecipeProvider {

    private final Supplier<List<Block>> blocksToProcess;
    private CompletableFuture<HolderLookup.Provider> registries;

    public ReplicationRecipesProvider(DataGenerator generatorIn, Supplier<List<Block>> blocksToProcess, CompletableFuture<HolderLookup.Provider> registries) {
        super(generatorIn.getPackOutput(), registries);
        this.blocksToProcess = blocksToProcess;
        this.registries = registries;
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
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK.get()).requires(ReplicationRegistry.Items.RAW_REPLICA.get(), 9).save(consumer);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ReplicationRegistry.Items.RAW_REPLICA.get(), 9).requires(ReplicationRegistry.Blocks.RAW_REPLICA_BLOCK.get(), 1).save(consumer);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ReplicationRegistry.Blocks.REPLICA_BLOCK.get()).requires(ReplicationRegistry.Items.REPLICA_INGOT.get(), 9).save(consumer);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ReplicationRegistry.Items.REPLICA_INGOT.get(), 9).requires(ReplicationRegistry.Blocks.REPLICA_BLOCK.get(), 1).save(consumer);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ReplicationRegistry.Items.RAW_REPLICA.get()), RecipeCategory.MISC, ReplicationRegistry.Items.REPLICA_INGOT.get(), 0.35F, 200)
                .unlockedBy("has_plastic", this.has(ReplicationRegistry.Items.RAW_REPLICA.get())).save(consumer, ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "smelting_raw_replica"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ReplicationRegistry.Items.RAW_REPLICA.get()), RecipeCategory.MISC, ReplicationRegistry.Items.REPLICA_INGOT.get(), 0.35F, 100)
                .unlockedBy("has_plastic", this.has(ReplicationRegistry.Items.RAW_REPLICA.get())).save(consumer, ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "blasting_raw_replica"));
        new MatterValueDataProvider().buildRecipes(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(ReplicationRegistry.Items.REPLICATOR_ENCLOSURE.get())
                .pattern("GGG")
                .pattern("GRG")
                .pattern("GGG")
                .define('R', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('G', Items.PURPLE_STAINED_GLASS_PANE)
                .save(consumer);
        var motorTemplate = new ItemStack(ReplicationRegistry.Items.MATTER_BLUEPRINT);
        var tag = new CompoundTag();
        try {
            tag.put("Item", new ItemStack(ReplicationRegistry.Items.REPLICATOR_MOTOR).saveOptional(this.registries.get()));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        tag.putDouble("Progress", 1);
        motorTemplate.set(ReplicationAttachments.BLUEPRINT, tag);
        var shaped = new ShapedRecipeBuilder(RecipeCategory.MISC, motorTemplate);
        shaped.unlockedBy("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(new ItemPredicate[]{ItemPredicate.Builder.item().of(new ItemLike[]{ReplicationRegistry.Items.REPLICA_INGOT.get()}).build()}));
        shaped.pattern("RBR")
                .pattern("RPR")
                .pattern("RMR")
                .define('R', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('P', Items.PAPER)
                .define('B', Items.BLAZE_POWDER)
                .define('M', Items.PISTON)
                .save(consumer);
    }
}
