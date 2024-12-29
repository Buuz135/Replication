package com.buuz135.replication.network.task;

import com.buuz135.replication.api.task.IReplicationTask;
import com.buuz135.replication.api.task.ReplicationTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ReplicationTaskManager implements INBTSerializable<CompoundTag> {

    private HashMap<String, IReplicationTask> pendingTasks;

    public ReplicationTaskManager() {
        pendingTasks = new LinkedHashMap<>();
    }

    public HashMap<String, IReplicationTask> getPendingTasks() {
        return pendingTasks;
    }
    @Nullable
    public IReplicationTask findTaskForReplicator(BlockPos pos){
        for (IReplicationTask value : this.getPendingTasks().values()) {
            if (value.canAcceptReplicator(pos)){
                return value;
            }
        }
        return null;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        pendingTasks.forEach((s, task) -> {
            compoundTag.put(s, task.serializeNBT(provider));
        });
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        this.pendingTasks = new LinkedHashMap<>();
        compoundTag.getAllKeys().forEach(s -> {
            var task = new ReplicationTask(ItemStack.EMPTY, Integer.MAX_VALUE, IReplicationTask.Mode.SINGLE, null);
            task.deserializeNBT(provider, compoundTag.getCompound(s));
            pendingTasks.put(s, task);
        });
    }
}
