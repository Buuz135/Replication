package com.buuz135.replication.client;

import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {

    public static void init(){
        EventManager.mod(FMLClientSetupEvent.class).process(fmlClientSetupEvent -> {
            ItemBlockRenderTypes.setRenderLayer(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getKey().get(), RenderType.translucent());
        }).subscribe();
    }
}
