package com.buuz135.replication.aequivaleo;

import com.buuz135.replication.Replication;
import com.ldtteam.aequivaleo.api.plugin.AequivaleoPlugin;
import com.ldtteam.aequivaleo.api.plugin.IAequivaleoPlugin;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

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

    @Override
    public void onDataSynced(ResourceKey<Level> worldRegistryKey) {
        IAequivaleoPlugin.super.onDataSynced(worldRegistryKey);
    }

    @Override
    public void onCompoundTypeRegistrySync() {
        IAequivaleoPlugin.super.onCompoundTypeRegistrySync();
    }
}
