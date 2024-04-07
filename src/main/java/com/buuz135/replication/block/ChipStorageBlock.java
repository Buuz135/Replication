package com.buuz135.replication.block;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.shapes.ChipStorageShapes;
import com.buuz135.replication.block.shapes.DisintegratorShapes;
import com.buuz135.replication.block.tile.ChipStorageBlockEntity;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ChipStorageBlock extends RotatableBlock<ChipStorageBlockEntity> implements INetworkDirectionalConnection {


    public ChipStorageBlock() {
        super("chip_storage", Properties.copy(Blocks.IRON_BLOCK), ChipStorageBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) -> new ChipStorageBlockEntity(this, ReplicationRegistry.Blocks.CHIP_STORAGE.getRight().get(), blockPos, blockState);
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
            return ChipStorageShapes.NORTH;
        }
        if (rotation == Direction.SOUTH){
            return ChipStorageShapes.SOUTH;
        }
        if (rotation == Direction.EAST){
            return ChipStorageShapes.EAST;
        }
        if (rotation == Direction.WEST){
            return ChipStorageShapes.WEST;
        }
        return super.getCollisionShape(state, world, pos, selectionContext);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        var rotation = state.getValue(FACING_HORIZONTAL);
        if (rotation == Direction.NORTH){
            return ChipStorageShapes.NORTH;
        }
        if (rotation == Direction.SOUTH){
            return ChipStorageShapes.SOUTH;
        }
        if (rotation == Direction.EAST){
            return ChipStorageShapes.EAST;
        }
        if (rotation == Direction.WEST){
            return ChipStorageShapes.WEST;
        }
        return super.getShape(state, p_60556_, p_60557_, p_60558_);
    }
    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        var sideness = FacingUtil.getFacingRelative(direction, state.getValue(FACING_HORIZONTAL));
        return sideness == FacingUtil.Sideness.BACK || direction == Direction.DOWN;
    }
}
