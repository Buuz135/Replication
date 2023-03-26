package com.buuz135.replication.block;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.tile.ReplicationBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class ReplicatorBlock extends RotatableBlock<ReplicationBlockEntity> {

    public ReplicatorBlock() {
        super("replicator", Properties.copy(Blocks.IRON_BLOCK), ReplicationBlockEntity.class);
        setItemGroup(Replication.TAB);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (pos, blockState) -> new ReplicationBlockEntity(this, ReplicationRegistry.Blocks.REPLICATOR.getRight().get(), pos, blockState);
    }

    @NotNull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
