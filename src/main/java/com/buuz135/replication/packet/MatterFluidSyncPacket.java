package com.buuz135.replication.packet;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.List;

public class MatterFluidSyncPacket extends Message {

    public static HashMap<String, HashMap<IMatterType, Long>> CLIENT_MATTER_STORAGE = new HashMap<>();

    public String network;
    public long amount;
    public ResourceLocation matterType;

    public MatterFluidSyncPacket(String network, long amount, ResourceLocation matterType) {
        this.network = network;
        this.amount = amount;
        this.matterType = matterType;
    }

    public MatterFluidSyncPacket() {
    }

    @Override
    protected void handleMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            CLIENT_MATTER_STORAGE.computeIfAbsent(this.network, s -> new HashMap<>()).put(ReplicationRegistry.MATTER_TYPES_REGISTRY.get(this.matterType), this.amount);
            if (Minecraft.getInstance().screen instanceof ReplicationTerminalScreen terminalScreen){
                terminalScreen.refreshTanks();
            }
        });
    }
}
