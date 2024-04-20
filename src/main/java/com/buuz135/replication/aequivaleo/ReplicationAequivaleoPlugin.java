package com.buuz135.replication.aequivaleo;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.client.MatterCalculationStatusToast;
import com.ldtteam.aequivaleo.api.plugin.AequivaleoPlugin;
import com.ldtteam.aequivaleo.api.plugin.IAequivaleoPlugin;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@AequivaleoPlugin
public class ReplicationAequivaleoPlugin implements IAequivaleoPlugin {
    @Override
    public String getId() {
        return Replication.MOD_ID;
    }

    @Override
    public void onConstruction() {
        IAequivaleoPlugin.super.onConstruction();
    }

    @Override
    public void onCommonSetup() {
        IAequivaleoPlugin.super.onCommonSetup();
    }

    @Override
    public void onReloadStartedFor(ServerLevel world) {
        IAequivaleoPlugin.super.onReloadStartedFor(world);
    }

    @Override
    public void onReloadFinishedFor(ServerLevel world) {
        IAequivaleoPlugin.super.onReloadFinishedFor(world);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataSynced(ResourceKey<Level> worldRegistryKey) {
        IAequivaleoPlugin.super.onDataSynced(worldRegistryKey);
        if (worldRegistryKey.location().toString().equals("minecraft:overworld")){
            var toast = new MatterCalculationStatusToast(new ItemStack(ReplicationRegistry.Blocks.REPLICATOR.getLeft().get()),
                    Component.literal( "Replication").withStyle(style -> style.withBold(true).withColor(0x72e567)),
                    Component.literal("Matter Values Synced").withStyle(style -> style.withColor(0x72e567)), false);
            Minecraft.getInstance().getToasts().addToast(toast);
            new Thread(() -> {
                try {
                    Thread.sleep(5 * 1000);
                    toast.hide();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    public void onCompoundTypeRegistrySync() {
        IAequivaleoPlugin.super.onCompoundTypeRegistrySync();
    }
}
