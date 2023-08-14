package com.buuz135.replication.block;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.INetworkDirectionalConnection;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.buuz135.replication.block.tile.MatterTankBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MatterTankBlock extends RotatableBlock<MatterTankBlockEntity> implements INetworkDirectionalConnection {

    public MatterTankBlock() {
        super("matter_tank", Properties.copy(Blocks.IRON_BLOCK), MatterTankBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (pos, blockState) -> new MatterTankBlockEntity(this, ReplicationRegistry.Blocks.MATTER_TANK.getRight().get(), pos, blockState);
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
