package com.buuz135.replication.block.shapes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class IdentificationChamberShapes {

    public static VoxelShape NORTH = Stream.of(
            Block.box(0, 2, 0, 4, 3, 4),
            Block.box(12, 2, 0, 16, 3, 4),
            Block.box(0, 2, 12, 3, 3, 16),
            Block.box(13, 2, 12, 16, 3, 16),
            Block.box(1, 3, 1, 3, 16, 3),
            Block.box(13, 3, 1, 15, 16, 3),
            Block.box(1, 3, 13, 3, 16, 15),
            Block.box(13, 3, 13, 15, 16, 15),
            Block.box(3, 2, 3, 13, 3, 14),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(14, 14, 3, 15, 15, 13),
            Block.box(1, 14, 3, 2, 15, 13),
            Block.box(3, 14, 1, 13, 15, 2),
            Block.box(3, 14, 14, 13, 15, 15),
            Block.box(3, 2, 14, 13, 14, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape EAST = Stream.of(
            Block.box(12, 2, 0, 16, 3, 4),
            Block.box(12, 2, 12, 16, 3, 16),
            Block.box(0, 2, 0, 4, 3, 3),
            Block.box(0, 2, 13, 4, 3, 16),
            Block.box(13, 3, 1, 15, 16, 3),
            Block.box(13, 3, 13, 15, 16, 15),
            Block.box(1, 3, 1, 3, 16, 3),
            Block.box(1, 3, 13, 3, 16, 15),
            Block.box(2, 2, 3, 13, 3, 13),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(3, 14, 14, 13, 15, 15),
            Block.box(3, 14, 1, 13, 15, 2),
            Block.box(14, 14, 3, 15, 15, 13),
            Block.box(1, 14, 3, 2, 15, 13),
            Block.box(0, 2, 3, 2, 14, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape SOUTH = Stream.of(
            Block.box(12, 2, 12, 16, 3, 16),
            Block.box(0, 2, 12, 4, 3, 16),
            Block.box(13, 2, 0, 16, 3, 4),
            Block.box(0, 2, 0, 3, 3, 4),
            Block.box(13, 3, 13, 15, 16, 15),
            Block.box(1, 3, 13, 3, 16, 15),
            Block.box(13, 3, 1, 15, 16, 3),
            Block.box(1, 3, 1, 3, 16, 3),
            Block.box(3, 2, 2, 13, 3, 13),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(1, 14, 3, 2, 15, 13),
            Block.box(14, 14, 3, 15, 15, 13),
            Block.box(3, 14, 14, 13, 15, 15),
            Block.box(3, 14, 1, 13, 15, 2),
            Block.box(3, 2, 0, 13, 14, 2)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape WEST = Stream.of(
            Block.box(0, 2, 12, 4, 3, 16),
            Block.box(0, 2, 0, 4, 3, 4),
            Block.box(12, 2, 13, 16, 3, 16),
            Block.box(12, 2, 0, 16, 3, 3),
            Block.box(1, 3, 13, 3, 16, 15),
            Block.box(1, 3, 1, 3, 16, 3),
            Block.box(13, 3, 13, 15, 16, 15),
            Block.box(13, 3, 1, 15, 16, 3),
            Block.box(3, 2, 3, 14, 3, 13),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(3, 14, 1, 13, 15, 2),
            Block.box(3, 14, 14, 13, 15, 15),
            Block.box(1, 14, 3, 2, 15, 13),
            Block.box(14, 14, 3, 15, 15, 13),
            Block.box(14, 2, 3, 16, 14, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
}
