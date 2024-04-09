package com.buuz135.replication.client.render;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.tile.ChipStorageBlockEntity;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
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
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class ChipStorageRenderer<T extends ChipStorageBlockEntity> implements BlockEntityRenderer<T>  {

    public static List<BakedModel> CHIP_MODELS = new ArrayList<>();

    public ChipStorageRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(ChipStorageBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource,  int combinedLightIn, int combinedOverlayIn) {
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
        //poseStack.translate(0.5f, 0.375f, 0.5f);

        for (int i = 0; i < entity.getChips().getSlots(); i++) {
            var stack = entity.getChips().getStackInSlot(i);
            if (!stack.isEmpty()) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),  multiBufferSource.getBuffer(RenderType.cutout()), null, CHIP_MODELS.get(i), 255, 255, 255, combinedLightIn ,combinedOverlayIn);
            }
        }

    }


}
