package com.buuz135.replication;

import com.buuz135.replication.aequivaleo.ReplicationCompoundType;
import com.buuz135.replication.block.MatterPipeBlock;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.client.ClientEvents;
import com.buuz135.replication.client.MatterTooltipClientComponent;
import com.buuz135.replication.client.MatterTooltipComponent;
import com.buuz135.replication.data.AequivaleoDataProvider;
import com.buuz135.replication.data.RepBlockstateProvider;
import com.buuz135.replication.network.NetworkRegistry;
import com.buuz135.replication.network.element.NetworkElementRegistry;
import com.buuz135.replication.network.element.type.DefaultMatterNetworkElement;
import com.buuz135.replication.network.matter.MatterNetwork;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;

import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.Reference;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("replication")
public class Replication extends ModuleController {

    public static String MOD_ID =  "replication";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public Replication() {
        ReplicationRegistry.init();
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
        CommonEvents.init();
        ClientEvents.init();
        NetworkRegistry.INSTANCE.addFactory(MatterNetwork.MATTER, new MatterNetwork.Factory());
        NetworkElementRegistry.INSTANCE.addFactory(DefaultMatterNetworkElement.ID, new DefaultMatterNetworkElement.Factory());
        NBTManager.getInstance().scanTileClassForAnnotations(MatterPipeBlockEntity.class);
    }

    @Override
    protected void initModules() {
        ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE = this.getRegistries().registerBlockWithTile("matter_network_pipe", MatterPipeBlock::new);
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        event.getGenerator().addProvider(true, new AequivaleoDataProvider(MOD_ID, event.getGenerator()));
        event.getGenerator().addProvider(true, new RepBlockstateProvider(event.getGenerator(), MOD_ID, event.getExistingFileHelper()));
    }
}
