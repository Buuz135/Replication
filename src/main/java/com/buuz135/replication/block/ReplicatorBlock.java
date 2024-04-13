package com.buuz135.replication.block;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.shapes.ReplicatorShapes;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.FacingUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ReplicatorBlock extends RotatableBlock<ReplicatorBlockEntity> implements INetworkDirectionalConnection {

    public ReplicatorBlock() {
        super("replicator", Properties.copy(Blocks.IRON_BLOCK), ReplicatorBlockEntity.class);
        setItemGroup(Replication.TAB);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (pos, blockState) -> new ReplicatorBlockEntity(this, ReplicationRegistry.Blocks.REPLICATOR.getRight().get(), pos, blockState);
    }

    @NotNull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext) {
        var rotation = state.getValue(FACING_HORIZONTAL);
        if (rotation == Direction.NORTH){
            return ReplicatorShapes.NORTH_FULL;
        }
        if (rotation == Direction.SOUTH){
            return ReplicatorShapes.SOUTH_FULL;
        }
        if (rotation == Direction.EAST){
            return ReplicatorShapes.EAST_FULL;
        }
        if (rotation == Direction.WEST){
            return ReplicatorShapes.WEST_FULL;
        }
        return super.getCollisionShape(state, world, pos, selectionContext);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        var rotation = state.getValue(FACING_HORIZONTAL);
        if (rotation == Direction.NORTH){
            return ReplicatorShapes.NORTH_FULL;
        }
        if (rotation == Direction.SOUTH){
            return ReplicatorShapes.SOUTH_FULL;
        }
        if (rotation == Direction.EAST){
            return ReplicatorShapes.EAST_FULL;
        }
        if (rotation == Direction.WEST){
            return ReplicatorShapes.WEST_FULL;
        }
        return super.getShape(state, p_60556_, p_60557_, p_60558_);
    }

    public Pair<VoxelShape, VoxelShape> getShapePlate(BlockState state) {
        var rotation = state.getValue(FACING_HORIZONTAL);
        if (rotation == Direction.NORTH){
            return Pair.of(ReplicatorShapes.NORTH, ReplicatorShapes.NORTH_PLATE);
        }
        if (rotation == Direction.SOUTH){
            return Pair.of(ReplicatorShapes.SOUTH, ReplicatorShapes.SOUTH_PLATE);
        }
        if (rotation == Direction.EAST){
            return Pair.of(ReplicatorShapes.EAST, ReplicatorShapes.EAST_PLATE);
        }
        if (rotation == Direction.WEST){
            return Pair.of(ReplicatorShapes.WEST, ReplicatorShapes.WEST_PLATE);
        }
        return Pair.of(ReplicatorShapes.NORTH, ReplicatorShapes.NORTH_PLATE);
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        var sideness = FacingUtil.getFacingRelative(direction, state.getValue(FACING_HORIZONTAL));
        if (direction == Direction.UP) return false;
        return sideness == FacingUtil.Sideness.BOTTOM || sideness == FacingUtil.Sideness.BACK;
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("IP ")
                .pattern("IRM")
                .pattern("III")
                .define('P', Items.PISTON)
                .define('I', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('R', Items.REDSTONE)
                .define('M', Tags.Items.INGOTS_IRON)
                .save(consumer);
    }
}
