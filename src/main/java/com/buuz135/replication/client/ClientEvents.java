package com.buuz135.replication.client;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.block.ReplicatorBlock;
import com.buuz135.replication.block.tile.*;
import com.buuz135.replication.calculation.client.ClientReplicationCalculation;
import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.buuz135.replication.client.render.*;
import com.buuz135.replication.client.render.shader.ReplicationRenderTypes;
import com.buuz135.replication.container.ReplicationTerminalContainer;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Matrix4f;

import java.io.IOException;
import java.text.DecimalFormat;

public class ClientEvents {

    public static void init(){
        EventManager.mod(FMLClientSetupEvent.class).process(fmlClientSetupEvent -> {
            MenuScreens.register((MenuType<? extends ReplicationTerminalContainer>) ReplicationTerminalContainer.TYPE.get(), ReplicationTerminalScreen::new);
        }).subscribe();
        EventManager.mod(RegisterClientTooltipComponentFactoriesEvent.class).process(event -> {
            event.register(MatterTooltipComponent.class, MatterTooltipClientComponent::new);
        }).subscribe();
        EventManager.forge(RenderTooltipEvent.GatherComponents.class).process(pre -> {
            if (Minecraft.getInstance().level != null && !pre.getItemStack().isEmpty()) {
                var instance = ClientReplicationCalculation.getMatterCompound(pre.getItemStack());
                if (instance != null && !instance.getValues().isEmpty()) {
                    if (Screen.hasShiftDown() || Minecraft.getInstance().screen instanceof ReplicationTerminalScreen) {
                        pre.getTooltipElements().add(Either.right(new MatterTooltipComponent(instance)));
                    } else {
                        pre.getTooltipElements().add(Either.left(Component.literal("ℹ Hold ").withStyle(ChatFormatting.GRAY).append(Component.literal("Shift").withStyle(ChatFormatting.YELLOW)).append(" to see matter values").withStyle(ChatFormatting.GRAY)));
                    }
                }
            }
        }).subscribe();
        EventManager.forge(ItemTooltipEvent.class).process(pre -> {
            if (ItemStack.isSameItem(pre.getItemStack(), new ItemStack(ReplicationRegistry.Blocks.MATTER_TANK.getLeft().get())) && pre.getItemStack().hasTag()){
                var tag = pre.getItemStack().getTag().getCompound("Tile").getCompound("tank");
                var matterStack = MatterStack.loadFluidStackFromNBT(tag);
                pre.getToolTip().add(1, Component.translatable("tooltip.titanium.tank.amount").withStyle(ChatFormatting.GOLD).append(Component.literal(ChatFormatting.WHITE + new DecimalFormat().format(matterStack.getAmount()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(256000) + ChatFormatting.DARK_AQUA + " matter")));
                pre.getToolTip().add(1, Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.replication.tank.matter").getString()).append(matterStack.isEmpty() ? Component.translatable("tooltip.titanium.tank.empty").withStyle(ChatFormatting.WHITE) : Component.translatable(matterStack.getTranslationKey())).withStyle(ChatFormatting.WHITE));
            }
        }).subscribe();
        EventManager.mod(EntityRenderersEvent.RegisterRenderers.class).process(event -> {
            event.registerBlockEntityRenderer((BlockEntityType<? extends ReplicatorBlockEntity>)ReplicationRegistry.Blocks.REPLICATOR.getRight().get(), p_173571_ -> new ReplicatorRenderer());
            event.registerBlockEntityRenderer((BlockEntityType<? extends MatterPipeBlockEntity>)ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getRight().get(), MatterPipeRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends MatterTankBlockEntity>)ReplicationRegistry.Blocks.MATTER_TANK.getRight().get(), MatterTankRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends DisintegratorBlockEntity>)ReplicationRegistry.Blocks.DISINTEGRATOR.getRight().get(), DisintegratorRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends ChipStorageBlockEntity>)ReplicationRegistry.Blocks.CHIP_STORAGE.getRight().get(), ChipStorageRenderer::new);
            event.registerBlockEntityRenderer((BlockEntityType<? extends IdentificationChamberBlockEntity>)ReplicationRegistry.Blocks.IDENTIFICATION_CHAMBER.getRight().get(), p_173571_ -> new IdentificationChamberRenderer());
        }).subscribe();
        EventManager.mod(ModelEvent.BakingCompleted.class).process(event -> {
            ReplicatorRenderer.PLATE = bakeModel(new ResourceLocation(Replication.MOD_ID, "block/replicator_plate"), event.getModelBakery());
            DisintegratorRenderer.BLADE = bakeModel(new ResourceLocation(Replication.MOD_ID, "block/disintegrator_blade"), event.getModelBakery());
            for (int i = 0; i < 8; i++) {
                ChipStorageRenderer.CHIP_MODELS.add(bakeModel(new ResourceLocation(Replication.MOD_ID, "block/chips/chip_storage_chips_" + i), event.getModelBakery()));
            }
        }).subscribe();
        EventManager.forge(RenderHighlightEvent.Block.class).process(ClientEvents::blockOverlayEvent).subscribe();

        EventManager.mod(RegisterShadersEvent.class).process(ClientEvents::registerShaders).subscribe();
    }
    private static void registerShaders(RegisterShadersEvent event) {

        try {
            for(ReplicationRenderTypes.ShaderRenderType type : ReplicationRenderTypes.getRenderTypes().values()) {
                type.register(event.getResourceProvider(), event::registerShader);
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BakedModel bakeModel(ResourceLocation model, ModelBakery modelBakery){
        UnbakedModel unbakedModel = modelBakery.getModel(model);
        ModelBaker baker = modelBakery.new ModelBakerImpl((modelLoc, material) -> material.sprite(), model);
        return unbakedModel.bake(baker, Material::sprite, new SimpleModelState(Transformation.identity()), model);
    }

    public static void blockOverlayEvent(RenderHighlightEvent.Block event) {
        if (event.getTarget() != null) {
            BlockHitResult traceResult = event.getTarget();
            BlockState og = Minecraft.getInstance().level.getBlockState(traceResult.getBlockPos());
            if (og.getBlock() instanceof ReplicatorBlock replicatorBlock && Minecraft.getInstance().level.getBlockEntity(traceResult.getBlockPos()) instanceof ReplicatorBlockEntity replicatorBlockEntity) {
                VoxelShape body = replicatorBlock.getShapePlate(og).getFirst();
                VoxelShape plate = replicatorBlock.getShapePlate(og).getSecond();
                BlockPos blockpos = event.getTarget().getBlockPos();
                event.setCanceled(true);

                    PoseStack stack = new PoseStack();
                    stack.pushPose();
                    Camera info = event.getCamera();
                    stack.mulPose(Axis.XP.rotationDegrees(info.getXRot()));
                    stack.mulPose(Axis.YP.rotationDegrees(info.getYRot() + 180));
                    double d0 = info.getPosition().x();
                    double d1 = info.getPosition().y();
                    double d2 = info.getPosition().z();
                    VertexConsumer builder = event.getMultiBufferSource().getBuffer(RenderType.LINES);
                    drawShape(stack, builder, body, blockpos.getX() - d0, blockpos.getY() - d1, blockpos.getZ() - d2, 0, 0, 0, 0.4F);
                    stack.translate(0 , -ReplicatorBlockEntity.LOWER_PROGRESS,0);

                    var progress = (replicatorBlockEntity.getProgress() + event.getPartialTick() /100f)/ (float) ReplicationConfig.Replicator.MAX_PROGRESS;
                    //progress = 0;

                    stack.translate(0, ReplicatorBlockEntity.LOWER_PROGRESS * progress, 0);
                    drawShape(stack, builder, plate, blockpos.getX() - d0, blockpos.getY() - d1, blockpos.getZ() - d2, 0, 0, 0, 0.4F);
                    stack.popPose();

            }
        }
    }

    private static void drawShape(PoseStack matrixStackIn, VertexConsumer bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixStackIn.last().pose();
        PoseStack.Pose posestack$pose = matrixStackIn.last();
        shapeIn.forAllEdges((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
            float f = (float) (p_230013_18_ - p_230013_12_);
            float f1 = (float) (p_230013_20_ - p_230013_14_);
            float f2 = (float) (p_230013_22_ - p_230013_16_);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f = f / f3;
            f1 = f1 / f3;
            f2 = f2 / f3;
            bufferIn.vertex(matrix4f, (float) (p_230013_12_ + xIn), (float) (p_230013_14_ + yIn), (float) (p_230013_16_ + zIn)).color(red, green, blue, alpha).normal(posestack$pose.normal(), f, f1, f2).endVertex();
            bufferIn.vertex(matrix4f, (float) (p_230013_18_ + xIn), (float) (p_230013_20_ + yIn), (float) (p_230013_22_ + zIn)).color(red, green, blue, alpha).normal(posestack$pose.normal(), f, f1, f2).endVertex();
        });
    }
}
