package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumRecipeProvider;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullLazy;

import java.util.List;
import java.util.function.Consumer;

public class ReplicationRecipesProvider extends TitaniumRecipeProvider {

    private final NonNullLazy<List<Block>> blocksToProcess;

    public ReplicationRecipesProvider(DataGenerator generatorIn, NonNullLazy<List<Block>> blocksToProcess) {
        super(generatorIn);
        this.blocksToProcess = blocksToProcess;
    }

    @Override
    public void register(Consumer<FinishedRecipe> consumer) {
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
        simpleCookingRecipe(consumer, "furnace", RecipeSerializer.SMELTING_RECIPE, 200, ReplicationRegistry.Items.RAW_REPLICA.get(), ReplicationRegistry.Items.REPLICA_INGOT.get(), 0.35F);
        simpleCookingRecipe(consumer, "blasting", RecipeSerializer.BLASTING_RECIPE, 100, ReplicationRegistry.Items.RAW_REPLICA.get(), ReplicationRegistry.Items.REPLICA_INGOT.get(), 0.35F);

    }
}
