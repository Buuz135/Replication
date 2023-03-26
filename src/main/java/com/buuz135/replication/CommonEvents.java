package com.buuz135.replication;

import com.buuz135.replication.command.ReplicationCommand;
import com.buuz135.replication.network.NetworkManager;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;

public class CommonEvents {

    public static void init() {
        EventManager.forge(TickEvent.LevelTickEvent.class)
                .filter(worldTickEvent -> !worldTickEvent.level.isClientSide && worldTickEvent.phase == TickEvent.Phase.END)
                .process(worldTickEvent -> {
                    NetworkManager.get(worldTickEvent.level).getNetworks().forEach(network -> network.update(worldTickEvent.level));
                }).subscribe();

        EventManager.forge(ServerStartingEvent.class).process(serverStartingEvent -> {
            ReplicationCommand.register(serverStartingEvent.getServer().getCommands().getDispatcher());
        }).subscribe();

    }
}
