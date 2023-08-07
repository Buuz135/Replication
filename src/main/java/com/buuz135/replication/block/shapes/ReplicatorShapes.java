package com.buuz135.replication.block.shapes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class ReplicatorShapes {

    public static VoxelShape NORTH = Stream.of(
            Block.box(0, 2, 2, 2, 4, 12),
            Block.box(14, 2, 2, 16, 4, 12),
            Block.box(4, 4, 13, 5, 16, 15),
            Block.box(11, 4, 13, 12, 16, 15),
            Block.box(3, 4, 14, 13, 12, 16),
            Block.box(6, 4, 13, 10, 16, 15),
            Block.box(0, 2, 12, 16, 4, 16),
            Block.box(0, 2, 0, 16, 4, 2),
            Block.box(0, 0, 0, 16, 2, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape NORTH_FULL = Stream.of(
            Block.box(0, 2, 2, 2, 4, 12),
            Block.box(14, 2, 2, 16, 4, 12),
            Block.box(4, 4, 13, 5, 16, 15),
            Block.box(11, 4, 13, 12, 16, 15),
            Block.box(3, 4, 14, 13, 12, 16),
            Block.box(6, 4, 13, 10, 16, 15),
            Block.box(0, 2, 12, 16, 4, 16),
            Block.box(0, 2, 0, 16, 4, 2),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(10, 14, 12, 11, 16, 14),
            Block.box(5, 14, 12, 6, 16, 14),
            Block.box(6, 14, 5, 10, 16, 13),
            Block.box(7, 13, 6, 9, 14, 8),
            Block.box(2, 12, 2, 14, 13, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape NORTH_PLATE = Stream.of(
            Block.box(10, 14, 12, 11, 16, 14),
            Block.box(5, 14, 12, 6, 16, 14),
            Block.box(6, 14, 5, 10, 16, 13),
            Block.box(7, 13, 6, 9, 14, 8),
            Block.box(2, 12, 2, 14, 13, 12)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape EAST = Stream.of(
            Block.box(4, 2, 0, 14, 4, 2),
            Block.box(4, 2, 14, 14, 4, 16),
            Block.box(1, 4, 4, 3, 16, 5),
            Block.box(1, 4, 11, 3, 16, 12),
            Block.box(0, 4, 3, 2, 12, 13),
            Block.box(1, 4, 6, 3, 16, 10),
            Block.box(0, 2, 0, 4, 4, 16),
            Block.box(14, 2, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 2, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape EAST_FULL = Stream.of(
            Block.box(4, 2, 0, 14, 4, 2),
            Block.box(4, 2, 14, 14, 4, 16),
            Block.box(1, 4, 4, 3, 16, 5),
            Block.box(1, 4, 11, 3, 16, 12),
            Block.box(0, 4, 3, 2, 12, 13),
            Block.box(1, 4, 6, 3, 16, 10),
            Block.box(0, 2, 0, 4, 4, 16),
            Block.box(14, 2, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(2, 14, 10, 4, 16, 11),
            Block.box(2, 14, 5, 4, 16, 6),
            Block.box(3, 14, 6, 11, 16, 10),
            Block.box(8, 13, 7, 10, 14, 9),
            Block.box(4, 12, 2, 14, 13, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape EAST_PLATE = Stream.of(
            Block.box(2, 14, 10, 4, 16, 11),
            Block.box(2, 14, 5, 4, 16, 6),
            Block.box(3, 14, 6, 11, 16, 10),
            Block.box(8, 13, 7, 10, 14, 9),
            Block.box(4, 12, 2, 14, 13, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape SOUTH = Stream.of(
            Block.box(14, 2, 4, 16, 4, 14),
            Block.box(0, 2, 4, 2, 4, 14),
            Block.box(11, 4, 1, 12, 16, 3),
            Block.box(4, 4, 1, 5, 16, 3),
            Block.box(3, 4, 0, 13, 12, 2),
            Block.box(6, 4, 1, 10, 16, 3),
            Block.box(0, 2, 0, 16, 4, 4),
            Block.box(0, 2, 14, 16, 4, 16),
            Block.box(0, 0, 0, 16, 2, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape SOUTH_FULL = Stream.of(
            Block.box(14, 2, 4, 16, 4, 14),
            Block.box(0, 2, 4, 2, 4, 14),
            Block.box(11, 4, 1, 12, 16, 3),
            Block.box(4, 4, 1, 5, 16, 3),
            Block.box(3, 4, 0, 13, 12, 2),
            Block.box(6, 4, 1, 10, 16, 3),
            Block.box(0, 2, 0, 16, 4, 4),
            Block.box(0, 2, 14, 16, 4, 16),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(5, 14, 2, 6, 16, 4),
            Block.box(10, 14, 2, 11, 16, 4),
            Block.box(6, 14, 3, 10, 16, 11),
            Block.box(7, 13, 8, 9, 14, 10),
            Block.box(2, 12, 4, 14, 13, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape SOUTH_PLATE = Stream.of(
            Block.box(5, 14, 2, 6, 16, 4),
            Block.box(10, 14, 2, 11, 16, 4),
            Block.box(6, 14, 3, 10, 16, 11),
            Block.box(7, 13, 8, 9, 14, 10),
            Block.box(2, 12, 4, 14, 13, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape WEST = Stream.of(
            Block.box(2, 2, 14, 12, 4, 16),
            Block.box(2, 2, 0, 12, 4, 2),
            Block.box(13, 4, 11, 15, 16, 12),
            Block.box(13, 4, 4, 15, 16, 5),
            Block.box(14, 4, 3, 16, 12, 13),
            Block.box(13, 4, 6, 15, 16, 10),
            Block.box(12, 2, 0, 16, 4, 16),
            Block.box(0, 2, 0, 2, 4, 16),
            Block.box(0, 0, 0, 16, 2, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape WEST_FULL = Stream.of(
            Block.box(2, 2, 14, 12, 4, 16),
            Block.box(2, 2, 0, 12, 4, 2),
            Block.box(13, 4, 11, 15, 16, 12),
            Block.box(13, 4, 4, 15, 16, 5),
            Block.box(14, 4, 3, 16, 12, 13),
            Block.box(13, 4, 6, 15, 16, 10),
            Block.box(12, 2, 0, 16, 4, 16),
            Block.box(0, 2, 0, 2, 4, 16),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(12, 14, 5, 14, 16, 6),
            Block.box(12, 14, 10, 14, 16, 11),
            Block.box(5, 14, 6, 13, 16, 10),
            Block.box(6, 13, 7, 8, 14, 9),
            Block.box(2, 12, 2, 12, 13, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static VoxelShape WEST_PLATE = Stream.of(
            Block.box(12, 14, 5, 14, 16, 6),
            Block.box(12, 14, 10, 14, 16, 11),
            Block.box(5, 14, 6, 13, 16, 10),
            Block.box(6, 13, 7, 8, 14, 9),
            Block.box(2, 12, 2, 12, 13, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
}
