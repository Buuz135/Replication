package com.buuz135.replication.client;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.aequivaleo.ReplicationCompoundType;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {

    public static void init(){
        EventManager.mod(FMLClientSetupEvent.class).process(fmlClientSetupEvent -> {
            ItemBlockRenderTypes.setRenderLayer(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getKey().get(), RenderType.translucent());
        }).subscribe();
        EventManager.mod(RegisterClientTooltipComponentFactoriesEvent.class).process(event -> {
            event.register(MatterTooltipComponent.class, MatterTooltipClientComponent::new);
        }).subscribe();
        EventManager.forge(RenderTooltipEvent.GatherComponents.class).process(pre -> {
            if (Minecraft.getInstance().level != null){
                var instance = IAequivaleoAPI.getInstance().getEquivalencyResults(Minecraft.getInstance().level.dimension()).dataFor(pre.getItemStack());
                if (instance.size() > 0){
                    if (Screen.hasShiftDown()){
                        for (CompoundInstance compoundInstance : instance) {
                            if (compoundInstance.getType() instanceof ReplicationCompoundType type){
                                pre.getTooltipElements().add(Either.right(new MatterTooltipComponent(compoundInstance)));
                            }
                        }
                    } else {
                        pre.getTooltipElements().add(Either.left(Component.literal("Hold ").withStyle(ChatFormatting.GRAY).append(Component.literal("Shift").withStyle(ChatFormatting.YELLOW)).append(" to see matter values").withStyle(ChatFormatting.GRAY)));
                    }
                }

            }
        }).subscribe();
    }
}
