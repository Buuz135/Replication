package com.buuz135.replication.packet;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.task.IReplicationTask;
import com.buuz135.replication.api.task.ReplicationTask;
import com.buuz135.replication.block.tile.ReplicationTerminalBlockEntity;
import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.buuz135.replication.network.MatterNetwork;
import com.hrznstudio.titanium.block_network.Network;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.LinkedHashMap;

public class TaskCancelPacket extends Message {

    public String task;
    public String network;

    public TaskCancelPacket(String task, String network) {
        this.task = task;
        this.network = network;
    }

    public TaskCancelPacket() {
    }

    @Override
    protected void handleMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            for (Network network : NetworkManager.get(context.player().level()).getNetworks()) {
                if (network.getId().equals(this.network) && network instanceof MatterNetwork matterNetwork){
                    matterNetwork.cancelTask(task, context.player().level());
                    break;
                }
            }
        });
    }

    public static class Response extends Message {

        public String task;
        public String network;

        public Response(String task, String network) {
            this.task = task;
            this.network = network;
        }


        public Response() {

        }

        @Override
        protected void handleMessage(IPayloadContext context) {
            context.enqueueWork(() -> {
               TaskSyncPacket.CLIENT_TASK_STORAGE.getOrDefault(network, new LinkedHashMap<>()).remove(task);
                if (Minecraft.getInstance().screen instanceof ReplicationTerminalScreen terminalScreen){
                    terminalScreen.refreshTasks();
                }
            });
        }
    }
}
