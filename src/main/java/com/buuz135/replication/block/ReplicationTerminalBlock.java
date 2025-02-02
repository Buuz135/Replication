package com.buuz135.replication.block;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.shapes.ReplicationTerminalShapes;
import com.buuz135.replication.block.tile.ReplicationTerminalBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class ReplicationTerminalBlock extends RotatableBlock<ReplicationTerminalBlockEntity> implements INetworkDirectionalConnection {
    public ReplicationTerminalBlock() {
        super("replication_terminal", Properties.ofFullCopy(Blocks.IRON_BLOCK), ReplicationTerminalBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) -> new ReplicationTerminalBlockEntity(this, ReplicationRegistry.Blocks.REPLICATION_TERMINAL.type().get(), blockPos, blockState);
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
            return ReplicationTerminalShapes.NORTH;
        }
        if (rotation == Direction.SOUTH){
            return ReplicationTerminalShapes.SOUTH;
        }
        if (rotation == Direction.EAST){
            return ReplicationTerminalShapes.EAST;
        }
        if (rotation == Direction.WEST){
            return ReplicationTerminalShapes.WEST;
        }
        return super.getCollisionShape(state, world, pos, selectionContext);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        var rotation = state.getValue(FACING_HORIZONTAL);
        if (rotation == Direction.NORTH){
            return ReplicationTerminalShapes.NORTH;
        }
        if (rotation == Direction.SOUTH){
            return ReplicationTerminalShapes.SOUTH;
        }
        if (rotation == Direction.EAST){
            return ReplicationTerminalShapes.EAST;
        }
        if (rotation == Direction.WEST){
            return ReplicationTerminalShapes.WEST;
        }
        return super.getShape(state, p_60556_, p_60557_, p_60558_);
    }
    @Override
    public boolean canConnect(Level level, BlockPos pos, BlockState state, Direction direction) {
        var sideness = FacingUtil.getFacingRelative(direction, state.getValue(FACING_HORIZONTAL));
        return sideness == FacingUtil.Sideness.BACK;
    }

    @Override
    public void registerRecipe(RecipeOutput consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("III")
                .pattern("IDP")
                .pattern("IDI")
                .define('P', Tags.Items.GLASS_PANES)
                .define('I', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('D', Items.DIAMOND)
                .save(consumer);
    }
}
