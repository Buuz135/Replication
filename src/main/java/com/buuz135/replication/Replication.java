package com.buuz135.replication;

import com.buuz135.replication.aequivaleo.ReplicationCompoundType;
import com.buuz135.replication.client.MatterTooltipClientComponent;
import com.buuz135.replication.client.MatterTooltipComponent;
import com.buuz135.replication.data.AequivaleoDataProvider;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.Reference;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("replication")
public class Replication extends ModuleController {

    public static String MOD_ID =  "replication";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Replication() {
        ReplicationRegistry.init();
        MinecraftForgeClient.registerTooltipComponentFactory(MatterTooltipComponent.class, MatterTooltipClientComponent::new);
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
                        pre.getTooltipElements().add(Either.left(new TextComponent("Hold ").withStyle(ChatFormatting.GRAY).append(new TextComponent("Shift").withStyle(ChatFormatting.YELLOW)).append(" to see matter values").withStyle(ChatFormatting.GRAY)));
                    }
                }

            }
        }).subscribe();
    }

    @Override
    protected void initModules() {

    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        event.getGenerator().addProvider(new AequivaleoDataProvider(MOD_ID, event.getGenerator()));
    }
}
