package com.buuz135.replication.block;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.tile.ReplicationTerminalBlockEntity;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ReplicationTerminalBlock extends RotatableBlock<ReplicationTerminalBlockEntity> implements INetworkDirectionalConnection {
    public ReplicationTerminalBlock() {
        super("replication_terminal", Properties.copy(Blocks.IRON_BLOCK), ReplicationTerminalBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) -> new ReplicationTerminalBlockEntity(this, ReplicationRegistry.Blocks.REPLICATION_TERMINAL.getValue().get(), blockPos, blockState);
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        return true;
    }
}
