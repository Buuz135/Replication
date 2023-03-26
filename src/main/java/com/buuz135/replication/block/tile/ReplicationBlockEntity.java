package com.buuz135.replication.block.tile;

import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ReplicationBlockEntity extends NetworkBlockEntity<ReplicationBlockEntity>{
    public ReplicationBlockEntity(BasicTileBlock<ReplicationBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
    }
}
