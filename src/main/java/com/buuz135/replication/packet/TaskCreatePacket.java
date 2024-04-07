package com.buuz135.replication.packet;

import com.buuz135.replication.api.task.IReplicationTask;
import com.buuz135.replication.api.task.ReplicationTask;
import com.buuz135.replication.network.MatterNetwork;
import com.hrznstudio.titanium.block_network.Network;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class TaskCreatePacket extends Message {

    private String networkId;
    private int amount;
    private ItemStack stack;
    private boolean parallelMode;
    private BlockPos source;

    public TaskCreatePacket(String network, int amount, ItemStack stack, boolean parallelMode, BlockPos source) {
        this.networkId = network;
        this.amount = amount;
        this.stack = stack;
        this.parallelMode = parallelMode;
        this.source = source;
    }

    public TaskCreatePacket() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            for (Network network : NetworkManager.get(context.getSender().level()).getNetworks()) {
                if (network.getId().equals(networkId) && network instanceof MatterNetwork matterNetwork){
                    var task = new ReplicationTask(stack, amount, parallelMode ? IReplicationTask.Mode.MULTIPLE : IReplicationTask.Mode.SINGLE, source);
                    matterNetwork.getTaskManager().getPendingTasks().put(task.getUuid().toString(), task);
                    break;
                }
            }
        });
    }
}
