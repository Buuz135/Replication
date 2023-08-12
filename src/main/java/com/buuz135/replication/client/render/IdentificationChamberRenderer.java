package com.buuz135.replication.client.render;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

public class IdentificationChamberRenderer implements BlockEntityRenderer<IdentificationChamberBlockEntity> {

    private static RenderType LINES = createRenderType();

    public static RenderType createRenderType() {
        var comp = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLinesShader))
                .setDepthTestState(new RenderStateShard.DepthTestStateShard("always", 515))
                .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(5)))
                .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                }, () -> {
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                }))
                .setOutputState(new RenderStateShard.OutputStateShard("item_entity_target", () -> {
                    if (Minecraft.useShaderTransparency()) {
                        Minecraft.getInstance().levelRenderer.getItemEntityTarget().bindWrite(false);
                    }

                }, () -> {
                    if (Minecraft.useShaderTransparency()) {
                        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                    }

                }))
                .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, true))
                .setCullState(new RenderStateShard.CullStateShard(false));
        return RenderType.create("lines", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, false, true, comp.createCompositeState(true));
    }
    @Override
    public void render(IdentificationChamberBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource,  int combinedLightIn, int combinedOverlayIn) {
        var facing = entity.getBlockState().getValue(RotatableBlock.FACING_HORIZONTAL);
        if (facing == Direction.EAST) {
            poseStack.translate(1,0,0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        }
        else if (facing == Direction.SOUTH) {
            poseStack.translate(1,0,1);
            poseStack.mulPose(Axis.YP.rotationDegrees(-180));
        }
        else if (facing == Direction.WEST) {
            poseStack.translate(0,0,1);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }

        poseStack.translate(0.5f, 0.375f, 0.5f);
        var scale = 0.4f;
        var stack = entity.getInput().getStackInSlot(0);
        if (!stack.isEmpty()){
            var model = Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, null, 0);

            if (model.isGui3d()){
                scale = 0.75f;
            }

            poseStack.pushPose();
            poseStack.scale(scale, scale,scale);

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStack, multiBufferSource, entity.getLevel(),0);

            poseStack.popPose();
        }
        poseStack.translate(-0.5f, -0.375f, -0.5f);
        var progress = (entity.getProgress() + partialTicks /100f)/ (float) IdentificationChamberBlockEntity.MAX_PROGRESS;
        //progress = 1;

        poseStack.translate(0,-0.2f,0);
        poseStack.translate(0,-IdentificationChamberBlockEntity.LOWER_PROGRESS, 0);
        poseStack.translate(0, IdentificationChamberBlockEntity.LOWER_PROGRESS * progress, 0);


        VertexConsumer builder = multiBufferSource.getBuffer(LINES);

        if (entity.getProgress() > 0){
            poseStack.pushPose();
            var reduction = 0.31f;
            poseStack.translate(reduction/2d, 0, reduction/2d);
            poseStack.scale(1-reduction,1,1-reduction);
            var alpha = (float) (0.8f+entity.getLevel().random.nextDouble()/30d);
            for (float i = 0; i <= 1.01f; i += 0.1f) {
                drawLine(poseStack, builder, 0 +i,0,1,0 +i, ReplicationRegistry.Colors.BLUE_SPLIT[0],ReplicationRegistry.Colors.BLUE_SPLIT[1],ReplicationRegistry.Colors.BLUE_SPLIT[2], alpha);
                drawLine(poseStack, builder, 0 ,0+i,0+i,1, ReplicationRegistry.Colors.BLUE_SPLIT[0],ReplicationRegistry.Colors.BLUE_SPLIT[1],ReplicationRegistry.Colors.BLUE_SPLIT[2], alpha);
            }
            poseStack.popPose();
        }

    }

    private static void drawLine(PoseStack matrixStackIn, VertexConsumer bufferIn, float xIn, float zIn, float xTo, float zTo,float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixStackIn.last().pose();
        bufferIn.vertex(matrix4f, xIn, 1, zIn).color(red, green, blue, alpha).endVertex();
        bufferIn.vertex(matrix4f, zTo, 1, xTo).color(red, green, blue, alpha).endVertex();

    }

}
