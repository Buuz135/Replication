package com.buuz135.replication.client.render;

import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;

public class DisintegratorRenderer<T extends DisintegratorBlockEntity> implements BlockEntityRenderer<T> {

    public static BakedModel BLADE = null;


    public DisintegratorRenderer(BlockEntityRendererProvider.Context p_173689_) {
    }

    @Override
    public void render(T tile, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
        var facing = tile.getBlockState().getValue(RotatableBlock.FACING_HORIZONTAL);
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

        poseStack.translate(0.5f, 0.505f, 0.38f);

        var speed = 20F;
        poseStack.pushPose();
        poseStack.translate(0, -0.505f, 0);
        var time = tile.getLevel().getGameTime() % 36000;
        poseStack.mulPose(Axis.YP.rotationDegrees((time % (360f / speed)) * speed + v));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),  multiBufferSource.getBuffer(RenderType.cutout()), null, BLADE, 255, 255, 255, combinedLightIn ,combinedOverlayIn);
        poseStack.popPose();



        for (int i = 0; i < tile.getInput().getSlots(); i++) {
            var scale = 0.3f;
            var stack = tile.getInput().getStackInSlot(i);
            if (!stack.isEmpty()){
                var model = Minecraft.getInstance().getItemRenderer().getModel(stack, Minecraft.getInstance().level, null, 0);

                if (model.isGui3d()){
                    scale = 0.475f;
                }

                poseStack.pushPose();

                poseStack.mulPose(Axis.YP.rotationDegrees((360/3F)*i));

                poseStack.mulPose(Axis.YP.rotationDegrees((time % (360 / speed)) * speed + v));
                poseStack.mulPose(Axis.ZP.rotationDegrees((time % 360 + ((360 / 3F * (i + 1)))) + v));
                poseStack.translate(0,-0.05,-0.2);
                poseStack.scale(scale, scale,scale);

                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStack, multiBufferSource, tile.getLevel(),0);

                poseStack.popPose();
            }
        }
    }


}