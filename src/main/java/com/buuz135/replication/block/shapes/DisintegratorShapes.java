package com.buuz135.replication.block.shapes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class DisintegratorShapes {

    public static VoxelShape NORTH = Stream.of(
            Block.box(2, 5, 0, 14, 14, 12),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 14, 0, 16, 16, 16),
            Block.box(0, 2, 14, 16, 14, 16),
            Block.box(1, 2, 12, 15, 14, 14),
            Block.box(3, 2, 1, 13, 4, 12),
            Block.box(2, 4, 0, 14, 5, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape EAST = Stream.of(
            Block.box(4, 5, 2, 16, 14, 14),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 14, 0, 16, 16, 16),
            Block.box(0, 2, 0, 2, 14, 16),
            Block.box(2, 2, 1, 4, 14, 15),
            Block.box(4, 2, 3, 15, 4, 13),
            Block.box(4, 4, 2, 16, 5, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape SOUTH = Stream.of(
            Block.box(2, 5, 4, 14, 14, 16),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 14, 0, 16, 16, 16),
            Block.box(0, 2, 0, 16, 14, 2),
            Block.box(1, 2, 2, 15, 14, 4),
            Block.box(3, 2, 4, 13, 4, 15),
            Block.box(2, 4, 4, 14, 5, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape WEST = Stream.of(
            Block.box(0, 5, 2, 12, 14, 14),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 14, 0, 16, 16, 16),
            Block.box(14, 2, 0, 16, 14, 16),
            Block.box(12, 2, 1, 14, 14, 15),
            Block.box(1, 2, 3, 12, 4, 13),
            Block.box(0, 4, 2, 12, 5, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
}
