package com.buuz135.replication.block;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DisintegratorBlock extends RotatableBlock<DisintegratorBlockEntity> implements INetworkDirectionalConnection {

    public DisintegratorBlock() {
        super("desintegrator", Properties.copy(Blocks.IRON_BLOCK), DisintegratorBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (pos, blockState) -> new DisintegratorBlockEntity(this, ReplicationRegistry.Blocks.DISINTEGRATOR.getRight().get(), pos, blockState);
    }

    @NotNull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        var sideness = FacingUtil.getFacingRelative(direction, state.getValue(FACING_HORIZONTAL));
        return sideness == FacingUtil.Sideness.BOTTOM || sideness == FacingUtil.Sideness.BACK;
    }
}
