package com.buuz135.replication.packet;

import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.hrznstudio.titanium.network.CompoundSerializableDataHandler;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatternSyncStoragePacket extends Message {

    public static HashMap<String, HashMap<Long, List<ItemStack>>> CLIENT_PATTERN_STORAGE = new HashMap<>();

    static {
        CompoundSerializableDataHandler.map(ListHandler.class, buf -> {
            var list = new ListHandler(new ArrayList<>());
            list.deserializeNBT(buf.registryAccess(), buf.readNbt());
            return list;
        }, (buf, listHandler) -> buf.writeNbt(listHandler.serializeNBT(buf.registryAccess())));
    }

    public String network;
    public long position;
    public ListHandler patterns;

    public PatternSyncStoragePacket(String network, long position, List<ItemStack> patterns) {
        this.network = network;
        this.position = position;
        this.patterns = new ListHandler(patterns);
    }

    public PatternSyncStoragePacket() {

    }

    @Override
    protected void handleMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            CLIENT_PATTERN_STORAGE.computeIfAbsent(this.network, s -> new HashMap<>()).put(this.position, this.patterns.patterns);
            if (Minecraft.getInstance().screen instanceof ReplicationTerminalScreen terminalScreen){
                terminalScreen.refreshPatterns();
            }
        });
    }

    public static class ListHandler implements INBTSerializable<CompoundTag> {
        public List<ItemStack> patterns;

        public ListHandler(List<ItemStack> patterns) {
            this.patterns = patterns;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider levelRegistryAccess) {
            CompoundTag tag = new CompoundTag();
            for (int i = 0; i < this.patterns.size(); i++) {
                tag.put(i +"", this.patterns.get(i).saveOptional(levelRegistryAccess));
            }
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider levelRegistryAccess, CompoundTag compoundTag) {
            this.patterns = new ArrayList<>();
            for (String allKey : compoundTag.getAllKeys()) {
                this.patterns.add(ItemStack.parseOptional(levelRegistryAccess, compoundTag.getCompound(allKey)));
            }
        }
    }
}
