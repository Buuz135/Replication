package com.buuz135.replication.client;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.ReplicatorBlock;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.buuz135.replication.client.render.MatterPipeRenderer;
import com.buuz135.replication.client.render.ReplicatorRenderer;
import com.buuz135.replication.client.render.shader.ReplicationRenderTypes;
import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.util.RayTraceUtils;
import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Matrix4f;

import java.io.IOException;

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
                        pre.getTooltipElements().add(Either.right(new MatterTooltipComponent(instance)));
                    } else {
                        pre.getTooltipElements().add(Either.left(Component.literal("ℹ Hold ").withStyle(ChatFormatting.GRAY).append(Component.literal("Shift").withStyle(ChatFormatting.YELLOW)).append(" to see matter values").withStyle(ChatFormatting.GRAY)));
                    }
                }

            }
        }).subscribe();
        EventManager.mod(EntityRenderersEvent.RegisterRenderers.class).process(event -> {
            event.registerBlockEntityRenderer((BlockEntityType<? extends ReplicatorBlockEntity>)ReplicationRegistry.Blocks.REPLICATOR.getRight().get(), p_173571_ -> new ReplicatorRenderer());
            event.registerBlockEntityRenderer((BlockEntityType<? extends MatterPipeBlockEntity>)ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getRight().get(), MatterPipeRenderer::new);

        }).subscribe();
        EventManager.mod(ModelEvent.BakingCompleted.class).process(event -> {
            ReplicatorRenderer.PLATE = bakeModel(new ResourceLocation(Replication.MOD_ID, "block/replicator_plate"), event.getModelBakery());
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

                    var progress = (replicatorBlockEntity.getProgress() + event.getPartialTick() /100f)/ (float) ReplicatorBlockEntity.MAX_PROGRESS;
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
