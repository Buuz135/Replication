package com.buuz135.replication.packet;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.client.ClientAccess;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public class MatterFluidSyncPacket extends Message {

    public static HashMap<String, HashMap<IMatterType, Long>> CLIENT_MATTER_STORAGE = new HashMap<>();

    public String network;
    public long amount;
    public Identifier matterType;

    public MatterFluidSyncPacket(String network, long amount, Identifier matterType) {
        this.network = network;
        this.amount = amount;
        this.matterType = matterType;
    }

    public MatterFluidSyncPacket() {
    }

    @Override
    protected void handleServerMessage(IPayloadContext context) {
    }

    @Override
    public void handleClientMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            CLIENT_MATTER_STORAGE.computeIfAbsent(this.network, s -> new HashMap<>()).put(ReplicationRegistry.MATTER_TYPES_REGISTRY.getValue(this.matterType), this.amount);
            ClientAccess.refreshTanks();
        });
    }
}
