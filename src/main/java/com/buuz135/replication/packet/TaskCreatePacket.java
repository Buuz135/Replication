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
    private boolean singleMode;
    private BlockPos source;

    public TaskCreatePacket(String network, int amount, ItemStack stack, boolean singleMode, BlockPos source) {
        this.networkId = network;
        this.amount = amount;
        this.stack = stack;
        this.singleMode = singleMode;
        this.source = source;
    }

    public TaskCreatePacket() {
    }

    @Override
    protected void handleMessage(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            for (Network network : NetworkManager.get(context.getSender().level()).getNetworks()) {
                if (network.getId().equals(networkId) && network instanceof MatterNetwork matterNetwork){
                    var task = new ReplicationTask(stack, amount, singleMode ? IReplicationTask.Mode.SINGLE : IReplicationTask.Mode.MULTIPLE, source);
                    matterNetwork.getTaskManager().getPendingTasks().put(task.getUuid().toString(), task);
                    break;
                }
            }
        });
    }
}
