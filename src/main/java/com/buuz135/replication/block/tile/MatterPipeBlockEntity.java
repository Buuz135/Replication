package com.buuz135.replication.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MatterPipeBlockEntity extends NetworkBlockEntity<MatterPipeBlockEntity> {

    @Save
    private boolean wip; //TODO REMOVE

    public MatterPipeBlockEntity(BasicTileBlock<MatterPipeBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
    }

}
