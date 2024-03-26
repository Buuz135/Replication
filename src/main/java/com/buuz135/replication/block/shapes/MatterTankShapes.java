package com.buuz135.replication.block.shapes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class MatterTankShapes {

    public static VoxelShape SHAPE = Stream.of(
            Block.box(3, 3, 3, 13, 13, 13),
            Block.box(4, 13, 4, 12, 14, 12),
            Block.box(4, 2, 4, 12, 3, 12),
            Block.box(3, 14, 3, 13, 16, 13),
            Block.box(3, 0, 3, 13, 2, 13)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
}
