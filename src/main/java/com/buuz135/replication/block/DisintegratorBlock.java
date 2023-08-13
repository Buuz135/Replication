package com.buuz135.replication.block;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class DisintegratorBlock extends RotatableBlock<DisintegratorBlockEntity> {

    public DisintegratorBlock() {
        super("desintegrator", Properties.copy(Blocks.IRON_BLOCK), DisintegratorBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (pos, blockState) -> new DisintegratorBlockEntity(this, ReplicationRegistry.Blocks.DISINTEGRATOR.getRight().get(), pos, blockState);
    }

    @NotNull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
