package com.buuz135.replication.packet;

import com.buuz135.replication.client.ClientAccess;
import com.buuz135.replication.network.MatterNetwork;
import com.hrznstudio.titanium.block_network.Network;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.network.Message;
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
    protected void handleServerMessage(IPayloadContext context) {
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
        protected void handleServerMessage(IPayloadContext context) {
        }

        @Override
        public void handleClientMessage(IPayloadContext context) {
            context.enqueueWork(() -> {
               TaskSyncPacket.CLIENT_TASK_STORAGE.getOrDefault(network, new LinkedHashMap<>()).remove(task);
                ClientAccess.refreshTasks();
            });
        }
    }
}
