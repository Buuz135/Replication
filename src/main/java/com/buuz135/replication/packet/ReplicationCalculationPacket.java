package com.buuz135.replication.packet;

import com.buuz135.replication.client.ClientAccess;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ReplicationCalculationPacket extends Message {

    public CompoundTag data;

    public ReplicationCalculationPacket(CompoundTag data) {
        this.data = data;
    }

    public ReplicationCalculationPacket() {
    }

    @Override
    protected void handleServerMessage(IPayloadContext context) {
    }

    @Override
    public void handleClientMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientAccess.acceptCalculation(data);
        });
    }
}
