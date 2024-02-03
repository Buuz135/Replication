package com.buuz135.replication.block;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.shapes.IdentificationChamberShapes;
import com.buuz135.replication.block.shapes.ReplicatorShapes;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
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

public class IdentificationChamberBlock extends RotatableBlock<IdentificationChamberBlockEntity> implements INetworkDirectionalConnection {

    public IdentificationChamberBlock() {
        super("identification_chamber", Properties.copy(Blocks.IRON_BLOCK), IdentificationChamberBlockEntity.class);
        setItemGroup(Replication.TAB);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (pos, blockState) -> new IdentificationChamberBlockEntity(this, ReplicationRegistry.Blocks.IDENTIFICATION_CHAMBER.getRight().get(), pos, blockState);
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
            return IdentificationChamberShapes.NORTH;
        }
        if (rotation == Direction.SOUTH){
            return IdentificationChamberShapes.SOUTH;
        }
        if (rotation == Direction.EAST){
            return IdentificationChamberShapes.EAST;
        }
        if (rotation == Direction.WEST){
            return IdentificationChamberShapes.WEST;
        }
        return super.getCollisionShape(state, world, pos, selectionContext);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        var rotation = state.getValue(FACING_HORIZONTAL);
        if (rotation == Direction.NORTH){
            return IdentificationChamberShapes.NORTH;
        }
        if (rotation == Direction.SOUTH){
            return IdentificationChamberShapes.SOUTH;
        }
        if (rotation == Direction.EAST){
            return IdentificationChamberShapes.EAST;
        }
        if (rotation == Direction.WEST){
            return IdentificationChamberShapes.WEST;
        }
        return super.getShape(state, p_60556_, p_60557_, p_60558_);
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        var sideness = FacingUtil.getFacingRelative(direction, state.getValue(FACING_HORIZONTAL));
        return sideness == FacingUtil.Sideness.BOTTOM || sideness == FacingUtil.Sideness.BACK;
    }
}
