package com.buuz135.replication.block.tile;

import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class CreativeMatterTankBlockEntity extends BaseMatterTankBlockEntity<CreativeMatterTankBlockEntity> {

    public CreativeMatterTankBlockEntity(BasicTileBlock<CreativeMatterTankBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, BooleanSupplier isCreative) {
        super(base, blockEntityType, pos, state, isCreative);
    }

    @Override
    public @NotNull CreativeMatterTankBlockEntity getSelf() {
        return this;
    }
}
