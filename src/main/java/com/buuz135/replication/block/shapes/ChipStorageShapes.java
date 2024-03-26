package com.buuz135.replication.block.shapes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class ChipStorageShapes {

    public static VoxelShape NORTH = Stream.of(
            Block.box(2, 10, 3, 4, 12, 9),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(1, 6, 1, 15, 7, 15),
            Block.box(3, 6, 13, 13, 13, 16),
            Block.box(4, 7, 2, 12, 9, 10),
            Block.box(5, 9, 1, 11, 11, 3),
            Block.box(5, 10, 0, 11, 12, 2),
            Block.box(5, 11, -1, 11, 13, 1),
            Block.box(5, 11, 11, 11, 13, 13),
            Block.box(5, 10, 10, 11, 12, 12),
            Block.box(5, 9, 9, 11, 11, 11),
            Block.box(11, 9, 3, 13, 11, 9),
            Block.box(12, 10, 3, 14, 12, 9),
            Block.box(13, 11, 3, 15, 13, 9),
            Block.box(3, 9, 3, 5, 11, 9),
            Block.box(1, 11, 3, 3, 13, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape EAST = Stream.of(
            Block.box(7, 10, 2, 13, 12, 4),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(1, 6, 1, 15, 7, 15),
            Block.box(0, 6, 3, 3, 13, 13),
            Block.box(6, 7, 4, 14, 9, 12),
            Block.box(13, 9, 5, 15, 11, 11),
            Block.box(14, 10, 5, 16, 12, 11),
            Block.box(15, 11, 5, 17, 13, 11),
            Block.box(3, 11, 5, 5, 13, 11),
            Block.box(4, 10, 5, 6, 12, 11),
            Block.box(5, 9, 5, 7, 11, 11),
            Block.box(7, 9, 11, 13, 11, 13),
            Block.box(7, 10, 12, 13, 12, 14),
            Block.box(7, 11, 13, 13, 13, 15),
            Block.box(7, 9, 3, 13, 11, 5),
            Block.box(7, 11, 1, 13, 13, 3)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape SOUTH = Stream.of(
            Block.box(12, 10, 7, 14, 12, 13),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(1, 6, 1, 15, 7, 15),
            Block.box(3, 6, 0, 13, 13, 3),
            Block.box(4, 7, 6, 12, 9, 14),
            Block.box(5, 9, 13, 11, 11, 15),
            Block.box(5, 10, 14, 11, 12, 16),
            Block.box(5, 11, 15, 11, 13, 17),
            Block.box(5, 11, 3, 11, 13, 5),
            Block.box(5, 10, 4, 11, 12, 6),
            Block.box(5, 9, 5, 11, 11, 7),
            Block.box(3, 9, 7, 5, 11, 13),
            Block.box(2, 10, 7, 4, 12, 13),
            Block.box(1, 11, 7, 3, 13, 13),
            Block.box(11, 9, 7, 13, 11, 13),
            Block.box(13, 11, 7, 15, 13, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape WEST = Stream.of(
            Block.box(3, 10, 12, 9, 12, 14),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(1, 6, 1, 15, 7, 15),
            Block.box(13, 6, 3, 16, 13, 13),
            Block.box(2, 7, 4, 10, 9, 12),
            Block.box(1, 9, 5, 3, 11, 11),
            Block.box(0, 10, 5, 2, 12, 11),
            Block.box(-1, 11, 5, 1, 13, 11),
            Block.box(11, 11, 5, 13, 13, 11),
            Block.box(10, 10, 5, 12, 12, 11),
            Block.box(9, 9, 5, 11, 11, 11),
            Block.box(3, 9, 3, 9, 11, 5),
            Block.box(3, 10, 2, 9, 12, 4),
            Block.box(3, 11, 1, 9, 13, 3),
            Block.box(3, 9, 11, 9, 11, 13),
            Block.box(3, 11, 13, 9, 13, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
}
