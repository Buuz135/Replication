package com.buuz135.replication.client.render;

import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class ReplicatorRenderer implements BlockEntityRenderer<ReplicatorBlockEntity> {

    private static RenderType AREA_TYPE = createRenderType();

    public static RenderType createRenderType() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                }, () -> {
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                })).createCompositeState(true);
        return RenderType.create("working_area_render", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true, state);
    }

    public static BakedModel PLATE = null;

    @Override
    public void render(ReplicatorBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource,  int combinedLightIn, int combinedOverlayIn) {
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

        var color = MatterType.EARTH.getColor().get();
        renderPlane(poseStack, multiBufferSource, Block.box( 2,0,2,14,1,12).bounds(), 0,0.15,0, color[0], color[1], color[2], 0.75f);
        renderFaces(poseStack, multiBufferSource, Block.box( 4,0,2,12,4,12).bounds(), 0,-0.2,0, 1,1,1, 0.005f);


        poseStack.translate(0 , -ReplicatorBlockEntity.LOWER_PROGRESS,0);

        var progress = (entity.getProgress() + partialTicks /100f)/ (float) ReplicatorBlockEntity.MAX_PROGRESS;
        //progress = 0;

        poseStack.translate(0, ReplicatorBlockEntity.LOWER_PROGRESS * progress, 0);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),  multiBufferSource.getBuffer(RenderType.solid()), null, PLATE, 255, 255, 255, combinedLightIn ,combinedOverlayIn);

        poseStack.translate(0.5f, 0.56f, 0.45f);
        var scale = 0.4f;
        var stack = new ItemStack(Items.DIRT);
        var model = Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, null, 0);

        if (model.isGui3d()){
            scale = 0.75f;
        }
        poseStack.scale(scale, scale,scale);
        if (entity.getAction() == 0) Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStack, multiBufferSource, entity.getLevel(),0);


    }

    private void renderPlane(PoseStack stack, MultiBufferSource renderTypeBuffer, AABB pos, double x, double y, double z, float red, float green, float blue, float alpha) {

        float x1 = (float) (pos.minX + x);
        float x2 = (float) (pos.maxX + x);
        float y1 = (float) (pos.minY + y);
        float y2 = (float) (pos.maxY + y);
        float z1 = (float) (pos.minZ + z);
        float z2 = (float) (pos.maxZ + z);

        Matrix4f matrix = stack.last().pose();
        VertexConsumer buffer;

        buffer = renderTypeBuffer.getBuffer(AREA_TYPE);



        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();

    }

    private void renderFaces(PoseStack stack, MultiBufferSource renderTypeBuffer, AABB pos, double x, double y, double z, float red, float green, float blue, float alpha) {

        float x1 = (float) (pos.minX + x);
        float x2 = (float) (pos.maxX + x);
        float y1 = (float) (pos.minY + y);
        float y2 = (float) (pos.maxY + y);
        float z1 = (float) (pos.minZ + z);
        float z2 = (float) (pos.maxZ + z);

        Matrix4f matrix = stack.last().pose();
        VertexConsumer buffer;

        buffer = renderTypeBuffer.getBuffer(AREA_TYPE);

        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();

        buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();


        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();

        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();


        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();

        buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();

    }
}
