package com.buuz135.replication.api;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface INetworkDirectionalConnection {

    boolean canConnect(BlockState state, Direction direction);

}
