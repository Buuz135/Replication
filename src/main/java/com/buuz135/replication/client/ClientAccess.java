package com.buuz135.replication.client;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.calculation.client.ClientReplicationCalculation;
import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ClientAccess {

    private ClientAccess() {
    }

    public static Level level() {
        return Minecraft.getInstance().level;
    }

    public static void refreshTasks() {
        if (Minecraft.getInstance().screen instanceof ReplicationTerminalScreen terminalScreen) {
            terminalScreen.refreshTasks();
        }
    }

    public static void refreshPatterns() {
        if (Minecraft.getInstance().screen instanceof ReplicationTerminalScreen terminalScreen) {
            terminalScreen.refreshPatterns();
        }
    }

    public static void refreshTanks() {
        if (Minecraft.getInstance().screen instanceof ReplicationTerminalScreen terminalScreen) {
            terminalScreen.refreshTanks();
        }
    }

    public static void acceptCalculation(CompoundTag data) {
        ClientReplicationCalculation.acceptData(level().registryAccess(), data);
        var subtext = Component.translatable("replication.toast_context").withStyle(style -> style.withColor(0xff72e567));
        var toast = new MatterCalculationStatusToast(new ItemStack(ReplicationRegistry.Blocks.REPLICATOR.getBlock()),
                Component.translatable("replication.toast_title").withStyle(style -> style.withBold(true).withColor(0xff72e567)),
                subtext, false);
        Minecraft.getInstance().getToastManager().addToast(toast);
        new Thread(() -> {
            try {
                Thread.sleep(5 * 1000);
                toast.hide();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
