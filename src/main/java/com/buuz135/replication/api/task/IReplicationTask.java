package com.buuz135.replication.api.task;

import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.network.MatterNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface IReplicationTask extends INBTSerializable<CompoundTag> {

    ItemStack getReplicatingStack();

    int getCurrentAmount();

    int getTotalAmount();

    HashMap<Long, List<MatterStack>> getStoredMatterStack();

    Mode getMode();

    UUID getUuid();

    List<Long> getReplicatorsOnTask();

    BlockPos getSource();

    boolean canAcceptReplicator(BlockPos replicator);

    void acceptReplicator(BlockPos replicator);

    void storeMatterStacksFor(Level level, BlockPos pos, MatterNetwork matterNetwork);

    void finalizeReplication(Level level, BlockPos pos, MatterNetwork matterNetwork);

    void markDirty(boolean dirty);

    boolean isDirty();

    public enum Mode{
        SINGLE,
        MULTIPLE;
    }
}
