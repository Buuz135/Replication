package com.buuz135.replication;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.buuz135.replication.command.ReplicationCommand;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.nbthandler.NBTManager;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;


import java.util.function.Supplier;

public class CommonEvents {

    public static void init() {
        NBTManager.getInstance().scanTileClassForAnnotations(ReplicatorBlockEntity.class);
        NBTManager.getInstance().scanTileClassForAnnotations(MatterPipeBlockEntity.class);
        NBTManager.getInstance().scanTileClassForAnnotations(IdentificationChamberBlockEntity.class);
        NBTManager.getInstance().scanTileClassForAnnotations(DisintegratorBlockEntity.class);

        EventManager.forge(ServerStartingEvent.class).process(serverStartingEvent -> {
            ReplicationCommand.register(serverStartingEvent.getServer().getCommands().getDispatcher());
        }).subscribe();

        EventManager.mod(NewRegistryEvent.class)
                .process(newRegistryEvent -> {
                    ReplicationRegistry.MATTER_TYPES_REGISTRY = newRegistryEvent.create(new RegistryBuilder<IMatterType>(ReplicationRegistry.MATTER_TYPES_KEY));
                }).subscribe();

    }
}
