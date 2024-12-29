package com.buuz135.replication.packet;

import com.buuz135.replication.api.task.IReplicationTask;
import com.buuz135.replication.api.task.ReplicationTask;
import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class TaskSyncPacket extends Message {

    public static HashMap<String, HashMap<String, IReplicationTask>> CLIENT_TASK_STORAGE = new HashMap<>();

    public String network;
    private String uuid;
    private CompoundTag tag;

    public TaskSyncPacket(String network,String uuid, CompoundTag tag) {
        this.network = network;
        this.uuid = uuid;
        this.tag = tag;
    }

    public TaskSyncPacket() {
    }

    @Override
    protected void handleMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            var task = new ReplicationTask(ItemStack.EMPTY, Integer.MAX_VALUE, IReplicationTask.Mode.SINGLE, null);
            task.deserializeNBT(context.player().level().registryAccess(), tag);
            var tasks = CLIENT_TASK_STORAGE.computeIfAbsent(this.network, s -> new LinkedHashMap<>());
            if (task.getTotalAmount() == task.getCurrentAmount()){
                tasks.remove(uuid);
            } else if (tasks.containsKey(task.getUuid().toString())){
                tasks.replace(this.uuid, task);
            } else {
                tasks.put(task.getUuid().toString(), task);
            }
            if (Minecraft.getInstance().screen instanceof ReplicationTerminalScreen terminalScreen){
                terminalScreen.refreshTasks();
            }
        });
    }
}
