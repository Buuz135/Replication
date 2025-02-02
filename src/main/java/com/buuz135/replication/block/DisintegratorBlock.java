package com.buuz135.replication.block;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.shapes.DisintegratorShapes;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class DisintegratorBlock extends RotatableBlock<DisintegratorBlockEntity> implements INetworkDirectionalConnection {

    public DisintegratorBlock() {
        super("desintegrator", Properties.ofFullCopy(Blocks.IRON_BLOCK), DisintegratorBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (pos, blockState) -> new DisintegratorBlockEntity(this, ReplicationRegistry.Blocks.DISINTEGRATOR.type().get(), pos, blockState);
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
            return DisintegratorShapes.NORTH;
        }
        if (rotation == Direction.SOUTH){
            return DisintegratorShapes.SOUTH;
        }
        if (rotation == Direction.EAST){
            return DisintegratorShapes.EAST;
        }
        if (rotation == Direction.WEST){
            return DisintegratorShapes.WEST;
        }
        return super.getCollisionShape(state, world, pos, selectionContext);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        var rotation = state.getValue(FACING_HORIZONTAL);
        if (rotation == Direction.NORTH){
            return DisintegratorShapes.NORTH;
        }
        if (rotation == Direction.SOUTH){
            return DisintegratorShapes.SOUTH;
        }
        if (rotation == Direction.EAST){
            return DisintegratorShapes.EAST;
        }
        if (rotation == Direction.WEST){
            return DisintegratorShapes.WEST;
        }
        return super.getShape(state, p_60556_, p_60557_, p_60558_);
    }

    @Override
    public boolean canConnect(Level level, BlockPos pos, BlockState state, Direction direction) {
        var sideness = FacingUtil.getFacingRelative(direction, state.getValue(FACING_HORIZONTAL));
        return sideness == FacingUtil.Sideness.BOTTOM || sideness == FacingUtil.Sideness.BACK;
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("ITG")
                .pattern("ITG")
                .pattern("III")
                .define('T', ReplicationRegistry.Blocks.MATTER_TANK.getBlock())
                .define('I', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('G', Tags.Items.GLASS_BLOCKS)
                .save(consumer);
    }
}
