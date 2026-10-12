package com.buuz135.replication.client;

import com.buuz135.replication.ReplicationAttachments;
import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.block.tile.*;
import com.buuz135.replication.calculation.client.ClientReplicationCalculation;
import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.buuz135.replication.client.render.*;
import com.buuz135.replication.client.render.shader.ReplicationRenderTypes;
import com.buuz135.replication.container.ReplicationTerminalContainer;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.text.DecimalFormat;

public class ClientEvents {

    public static void init(){
        EventManager.mod(RegisterRenderPipelinesEvent.class).process(ReplicationRenderTypes::register).subscribe();
        EventManager.mod(RegisterClientTooltipComponentFactoriesEvent.class).process(event -> {
            event.register(MatterTooltipComponent.class, MatterTooltipClientComponent::new);
        }).subscribe();
        EventManager.mod(RegisterMenuScreensEvent.class).process(event -> {
            event.register((MenuType<? extends ReplicationTerminalContainer>) ReplicationTerminalContainer.TYPE.get(), ReplicationTerminalScreen::new);
        }).subscribe();
        EventManager.forge(RenderTooltipEvent.GatherComponents.class).process(event -> {
            if (Minecraft.getInstance().level == null || event.getItemStack().isEmpty()) {
                return;
            }
            var compound = ClientReplicationCalculation.getMatterCompound(event.getItemStack());
            if (compound == null || compound.getValues().isEmpty()) {
                return;
            }
            boolean shiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_LSHIFT)
                    || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_RSHIFT);
            if ((ReplicationConfig.Client.SHOW_MATTER_TOOLTIP_VALUES_OUTSIDE_TERMINAL && shiftDown) || Minecraft.getInstance().screen instanceof ReplicationTerminalScreen) {
                event.getTooltipElements().add(Either.right(new MatterTooltipComponent(compound)));
            } else if (ReplicationConfig.Client.SHOW_PRESS_SHIFT_TEXT) {
                event.getTooltipElements().add(Either.left(Component.translatable("tooltip.replication.matter_value", Component.literal("Shift").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
            }
        }).subscribe();
        EventManager.forge(ItemTooltipEvent.class).process(pre -> {
            if (ItemStack.isSameItem(pre.getItemStack(), new ItemStack(ReplicationRegistry.Blocks.MATTER_TANK.getBlock())) && pre.getItemStack().has(ReplicationAttachments.TILE)){
                var tag = pre.getItemStack().get(ReplicationAttachments.TILE);
                var matterStack = MatterStack.loadMatterStackFromNBT(tag.contains("tank") ? tag.getCompoundOrEmpty("tank") : tag.getCompoundOrEmpty("lockableMatterTankBundle").getCompoundOrEmpty("Tank"));
                pre.getToolTip().add(1, Component.translatable("tooltip.titanium.tank.amount").withStyle(ChatFormatting.GOLD).append(Component.literal(ChatFormatting.WHITE + new DecimalFormat().format(matterStack.getAmount()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(ReplicationConfig.MatterTank.CAPACITY))).append(Component.translatable("tooltip.replication.tank.unit").withStyle(ChatFormatting.DARK_AQUA)));
                pre.getToolTip().add(1, Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.replication.tank.matter").getString()).append(matterStack.isEmpty() ? Component.translatable("tooltip.titanium.tank.empty").withStyle(ChatFormatting.WHITE) : Component.translatable(matterStack.getTranslationKey())).withStyle(ChatFormatting.WHITE));
            }
            if (ItemStack.isSameItem(pre.getItemStack(), new ItemStack(ReplicationRegistry.Blocks.CREATIVE_MATTER_TANK.getBlock())) && pre.getItemStack().has(ReplicationAttachments.TILE)) {
                var tag = pre.getItemStack().get(ReplicationAttachments.TILE);
                var matterStack = MatterStack.loadMatterStackFromNBT(tag.contains("tank") ? tag.getCompoundOrEmpty("tank") : tag.getCompoundOrEmpty("lockableMatterTankBundle").getCompoundOrEmpty("Tank"));
                pre.getToolTip().add(1, Component.translatable("tooltip.titanium.tank.amount").withStyle(ChatFormatting.GOLD).append(Component.literal(ChatFormatting.WHITE + new DecimalFormat().format(matterStack.getAmount()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(ReplicationConfig.MatterTank.CAPACITY))).append(Component.translatable("tooltip.replication.tank.unit").withStyle(ChatFormatting.DARK_AQUA)));
                pre.getToolTip().add(1, Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.replication.tank.matter").getString()).append(matterStack.isEmpty() ? Component.translatable("tooltip.titanium.tank.empty").withStyle(ChatFormatting.WHITE) : Component.translatable(matterStack.getTranslationKey())).withStyle(ChatFormatting.WHITE));
            }
        }).subscribe();
        EventManager.mod(ModelEvent.RegisterStandalone.class).process(event -> {
            event.register(ReplicatorRenderer.PLATE_KEY, SimpleUnbakedStandaloneModel.simpleModelWrapper(Identifier.fromNamespaceAndPath("replication", "block/replicator_plate")));
            event.register(DisintegratorRenderer.BLADE_KEY, SimpleUnbakedStandaloneModel.simpleModelWrapper(Identifier.fromNamespaceAndPath("replication", "block/disintegrator_blade")));
            for (int slot = 0; slot < ChipStorageRenderer.CHIP_KEYS.size(); slot++) {
                event.register(ChipStorageRenderer.CHIP_KEYS.get(slot), SimpleUnbakedStandaloneModel.simpleModelWrapper(Identifier.fromNamespaceAndPath("replication", "block/chips/chip_storage_chips_" + slot)));
            }
        }).subscribe();
        EventManager.mod(ModelEvent.BakingCompleted.class).process(event -> {
            ReplicatorRenderer.setPlate(event.getModelManager().getStandaloneModel(ReplicatorRenderer.PLATE_KEY));
            DisintegratorRenderer.setBlade(event.getModelManager().getStandaloneModel(DisintegratorRenderer.BLADE_KEY));
            ChipStorageRenderer.setChipModels(ChipStorageRenderer.CHIP_KEYS.stream().map(event.getModelManager()::getStandaloneModel).toList());
        }).subscribe();
        EventManager.mod(EntityRenderersEvent.RegisterRenderers.class).process(event -> {
            event.registerBlockEntityRenderer((BlockEntityType<? extends ReplicatorBlockEntity>) ReplicationRegistry.Blocks.REPLICATOR.type().get(), ReplicatorRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends MatterPipeBlockEntity>) ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.type().get(), MatterPipeRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends MatterTankBlockEntity>) ReplicationRegistry.Blocks.MATTER_TANK.type().get(), MatterTankRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends CreativeMatterTankBlockEntity>) ReplicationRegistry.Blocks.CREATIVE_MATTER_TANK.type().get(), MatterTankRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends DisintegratorBlockEntity>) ReplicationRegistry.Blocks.DISINTEGRATOR.type().get(), DisintegratorRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends ChipStorageBlockEntity>) ReplicationRegistry.Blocks.CHIP_STORAGE.type().get(), ChipStorageRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends IdentificationChamberBlockEntity>) ReplicationRegistry.Blocks.IDENTIFICATION_CHAMBER.type().get(), IdentificationChamberRenderer::new);
        }).subscribe();
        EventManager.mod(EntityRenderersEvent.AddLayers.class).process(event -> {
            for (var skin : event.getSkins()) {
                var renderer = event.getPlayerRenderer(skin);
                if (renderer != null) {
                    renderer.addLayer(new ContributorsAuraRender(renderer));
                }
            }
        }).subscribe();
    }
}
