package com.buuz135.replication.client.render;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.block.MatterPipeBlock;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.block.tile.MatterTankBlockEntity;
import com.buuz135.replication.client.render.shader.ReplicationRenderTypes;
import com.buuz135.replication.client.render.shader.ShaderTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public class MatterTankRenderer<T extends MatterTankBlockEntity> implements BlockEntityRenderer<T> {


    public MatterTankRenderer(BlockEntityRendererProvider.Context p_173689_) {
    }

    @Override
    public void render(T tile, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
        var padding = 0.2;
        var matterStack = tile.getTanks().get(0).getMatter();
        if (!matterStack.isEmpty())renderFaces(poseStack, multiBufferSource, new AABB(padding,0.255,padding,1-padding,0.255 + (matterStack.getAmount()/ (double) tile.getTanks().get(0).getCapacity()) * 0.5,1-padding), combinedLightIn, combinedOverlayIn, matterStack.getMatterType());
    }

    private void renderFaces(PoseStack matrixStack, MultiBufferSource bufferIn, AABB bounds, int combinedLight, int combinedOverlay, IMatterType matterType) {

        matrixStack.pushPose();
        //matrixStack.translate(0,1,0);
        ResourceLocation texture = new ResourceLocation(Replication.MOD_ID, "block/matter");
        TextureAtlasSprite still = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        VertexConsumer builder = bufferIn.getBuffer(RenderType.translucent());

        float[] color = matterType.getColor().get();
        float red = color[0];
        float green = color[1];
        float blue = color[2];
        float alpha =  color[3];

        float x1 = (float) bounds.minX;
        float x2 = (float) bounds.maxX;
        float y1 = (float) bounds.minY;
        float y2 = (float) bounds.maxY;
        float z1 = (float) bounds.minZ;
        float z2 = (float) bounds.maxZ;
        double bx1 = bounds.minX * 16;
        double bx2 = bounds.maxX * 16;
        double by1 = bounds.minY * 16;
        double by2 = bounds.maxY * 16;
        double bz1 = bounds.minZ * 16;
        double bz2 = bounds.maxZ * 16;


        Matrix4f posMat = matrixStack.last().pose();

        //TOP
        float u1 = still.getU(0);
        float u2 = still.getU(16);
        float v1 = still.getV(0);
        float v2 = still.getV(16);
        if (true) {

            builder.vertex(posMat, x1, y2, z2).color(red, green, blue, alpha).uv(u1, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 1f, 0f).endVertex();
            builder.vertex(posMat, x2, y2, z2).color(red, green, blue, alpha).uv(u2, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 1f, 0f).endVertex();
            builder.vertex(posMat, x2, y2, z1).color(red, green, blue, alpha).uv(u2, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 1f, 0f).endVertex();
            builder.vertex(posMat, x1, y2, z1).color(red, green, blue, alpha).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 1f, 0f).endVertex();
        }
        u1 = still.getU(0);
        u2 = still.getU(16);
        v1 = still.getV(by1);
        v2 = still.getV(by2);
        //FRONT
        if (true) {

            builder.vertex(posMat, x2, y1, z2).color(red, green, blue, alpha).uv(u2, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x2, y2, z2).color(red, green, blue, alpha).uv(u2, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y2, z2).color(red, green, blue, alpha).uv(u1, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y1, z2).color(red, green, blue, alpha).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
        }

        if (true) {
            matrixStack.translate(0,0,1);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90));
            builder.vertex(posMat, x2, y1, z2).color(red, green, blue, alpha).uv(u2, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x2, y2, z2).color(red, green, blue, alpha).uv(u2, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y2, z2).color(red, green, blue, alpha).uv(u1, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y1, z2).color(red, green, blue, alpha).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
        }

        if (true) {
            matrixStack.translate(0,0,1);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90));
            builder.vertex(posMat, x2, y1, z2).color(red, green, blue, alpha).uv(u2, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x2, y2, z2).color(red, green, blue, alpha).uv(u2, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y2, z2).color(red, green, blue, alpha).uv(u1, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y1, z2).color(red, green, blue, alpha).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
        }

        if (true) {
            matrixStack.translate(0,0,1);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90));
            builder.vertex(posMat, x2, y1, z2).color(red, green, blue, alpha).uv(u2, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x2, y2, z2).color(red, green, blue, alpha).uv(u2, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y2, z2).color(red, green, blue, alpha).uv(u1, v2).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
            builder.vertex(posMat, x1, y1, z2).color(red, green, blue, alpha).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0f, 0f, 1f).endVertex();
        }

        matrixStack.popPose();
    }
}