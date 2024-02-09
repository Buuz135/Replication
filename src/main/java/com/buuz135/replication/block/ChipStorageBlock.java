package com.buuz135.replication.block;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.tile.ChipStorageBlockEntity;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChipStorageBlock extends RotatableBlock<ChipStorageBlockEntity> implements INetworkDirectionalConnection {


    public ChipStorageBlock() {
        super("chip_storage", Properties.copy(Blocks.IRON_BLOCK), ChipStorageBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) -> new ChipStorageBlockEntity(this, ReplicationRegistry.Blocks.CHIP_STORAGE.getRight().get(), blockPos, blockState);
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        return true;
    }
}
