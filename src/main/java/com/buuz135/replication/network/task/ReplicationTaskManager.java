package com.buuz135.replication.network.task;

import com.buuz135.replication.api.task.IReplicationTask;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;

public class ReplicationTaskManager implements INBTSerializable<CompoundTag> {

    private HashMap<String, IReplicationTask> pendingTasks;

    public ReplicationTaskManager() {
        pendingTasks = new HashMap<>();
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
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {

    }
}
