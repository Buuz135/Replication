package com.buuz135.replication.packet;

import com.buuz135.replication.container.ReplicationTerminalContainer;
import com.buuz135.replication.network.MatterNetwork;
import com.hrznstudio.titanium.block_network.Network;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TaskSyncRequestPacket extends Message {

    private String network;

    public TaskSyncRequestPacket(String network) {
        this.network = network;
    }

    public TaskSyncRequestPacket() {
    }

    @Override
    protected void handleServerMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)
                    || !(serverPlayer.containerMenu instanceof ReplicationTerminalContainer container)
                    || !container.getNetwork().equals(network)) {
                return;
            }
            for (Network candidate : NetworkManager.get(serverPlayer.level()).getNetworks()) {
                if (candidate instanceof MatterNetwork matterNetwork && candidate.getId().equals(network)) {
                    matterNetwork.getTaskManager().getPendingTasks().values().forEach(task -> matterNetwork.sendTaskSyncPacket(serverPlayer, task));
                    return;
                }
            }
        });
    }
}
