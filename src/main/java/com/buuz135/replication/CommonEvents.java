package com.buuz135.replication;

import com.buuz135.replication.network.NetworkManager;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraftforge.event.TickEvent;

public class CommonEvents {

    public static void init() {
        EventManager.forge(TickEvent.WorldTickEvent.class)
                .filter(worldTickEvent -> !worldTickEvent.world.isClientSide && worldTickEvent.phase == TickEvent.Phase.END)
                .process(worldTickEvent -> {
                    NetworkManager.get(worldTickEvent.world).getNetworks().forEach(network -> network.update(worldTickEvent.world));
                }).subscribe();
    }
}
