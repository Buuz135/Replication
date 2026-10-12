package com.buuz135.replication.client.render;

import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class IdentificationChamberRenderer implements BlockEntityRenderer<IdentificationChamberBlockEntity, IdentificationChamberRenderer.State> {

    private final ItemModelResolver itemModelResolver;

    public IdentificationChamberRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    private static void drawGrid(PoseStack.Pose pose, VertexConsumer buffer) {
        int red = Math.max(0, Math.min(255, Math.round(ReplicationRegistry.Colors.GREEN_SPLIT[0] * 255)));
        int green = Math.max(0, Math.min(255, Math.round(ReplicationRegistry.Colors.GREEN_SPLIT[1] * 255)));
        int blue = Math.max(0, Math.min(255, Math.round(ReplicationRegistry.Colors.GREEN_SPLIT[2] * 255)));
        int color = 0xDD000000 | red << 16 | green << 8 | blue;
        for (int index = 0; index <= 10; index++) {
            float offset = 0.155F + index * 0.069F;
            line(pose, buffer, offset, 0.155F, offset, 0.845F, color);
            line(pose, buffer, 0.155F, offset, 0.845F, offset, color);
        }
    }

    private static void line(PoseStack.Pose pose, VertexConsumer buffer, float x1, float z1, float x2, float z2, int color) {
        buffer.addVertex(pose.pose(), x1, 0, z1).setColor(color).setNormal(pose, 0, 1, 0).setLineWidth(5f);
        buffer.addVertex(pose.pose(), x2, 0, z2).setColor(color).setNormal(pose, 0, 1, 0).setLineWidth(5f);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(IdentificationChamberBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(RotatableBlock.FACING_HORIZONTAL);
        state.progress = Math.min(1, (blockEntity.getProgress() + partialTicks) / ReplicationConfig.IdentificationChamber.MAX_PROGRESS);
        state.active = blockEntity.getProgress() > 0;
        this.itemModelResolver.updateForTopItem(state.item, blockEntity.getInput().getStackInSlot(0), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        ReplicationRenderHelper.rotate(poseStack, state.facing);
        poseStack.translate(0.5, 0.375, 0.5);
        poseStack.pushPose();
        poseStack.scale(0.55F, 0.55F, 0.55F);
        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
        if (state.active) {
            poseStack.translate(-0.5, 0.37 - IdentificationChamberBlockEntity.LOWER_PROGRESS + IdentificationChamberBlockEntity.LOWER_PROGRESS * state.progress, -0.5);
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.linesTranslucent(), (pose, buffer) -> drawGrid(pose, buffer));
        }
        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        private final ItemStackRenderState item = new ItemStackRenderState();
        private Direction facing = Direction.NORTH;
        private float progress;
        private boolean active;
    }
}
