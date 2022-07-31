package com.buuz135.replication.client;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.client.render.MatterPipeRender;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {

    public static void init(){
        EventManager.mod(FMLClientSetupEvent.class).process(fmlClientSetupEvent -> {
            ItemBlockRenderTypes.setRenderLayer(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getKey().get(), RenderType.translucent());
        }).subscribe();
        EventManager.mod(EntityRenderersEvent.RegisterRenderers.class).process(registerRenderers -> {
            registerRenderers.registerBlockEntityRenderer((BlockEntityType<? extends MatterPipeBlockEntity>)ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getRight().get(), MatterPipeRender::new);
        }).subscribe();
    }
}
