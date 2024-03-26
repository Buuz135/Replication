package com.buuz135.replication.block.shapes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class ReplicationTerminalShapes {

    public static VoxelShape NORTH = Block.box(0, 0, 13, 16, 16, 16);

    public static VoxelShape EAST = Block.box(0, 0, 0, 3, 16, 16);

    public static VoxelShape SOUTH = Block.box(0, 0, 0, 16, 16, 3);

    public static VoxelShape WEST = Block.box(13, 0, 0, 16, 16, 16);
}
