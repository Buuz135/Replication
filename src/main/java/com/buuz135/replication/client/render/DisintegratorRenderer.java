package com.buuz135.replication.client.render;

import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DisintegratorRenderer implements BlockEntityRenderer<DisintegratorBlockEntity, DisintegratorRenderer.State> {

    public static final StandaloneModelKey<BlockStateModelPart> BLADE_KEY = new StandaloneModelKey<>(() -> "replication:block/disintegrator_blade");
    private static BlockStateModelPart blade;
    private final ItemModelResolver itemModelResolver;

    public DisintegratorRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    public static void setBlade(BlockStateModelPart blade) {
        DisintegratorRenderer.blade = blade;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(DisintegratorBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(RotatableBlock.FACING_HORIZONTAL);
        long gameTime = blockEntity.getLevel() == null ? 0 : blockEntity.getLevel().getGameTime();
        state.bladeRotation = gameTime % 18 * 20F + partialTicks;
        state.itemRotation = gameTime % 360 + partialTicks;
        state.items = new ArrayList<>();
        for (int slot = 0; slot < blockEntity.getInput().getSlots(); slot++) {
            ItemStackRenderState item = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(item, blockEntity.getInput().getStackInSlot(slot), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, slot);
            state.items.add(item);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        ReplicationRenderHelper.rotate(poseStack, state.facing);
        poseStack.translate(0.5, 0.505, 0.38);
        poseStack.pushPose();
        poseStack.translate(0, -0.505, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.bladeRotation));
        if (blade != null) {
            submitNodeCollector.submitBlockModel(poseStack, Sheets.cutoutBlockSheet(), List.of(blade), BlockModelRenderState.EMPTY_TINTS, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        poseStack.popPose();
        for (int slot = 0; slot < state.items.size(); slot++) {
            ItemStackRenderState item = state.items.get(slot);
            if (item.isEmpty()) {
                continue;
            }
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(120F * slot));
            poseStack.mulPose(Axis.YP.rotationDegrees(state.bladeRotation));
            poseStack.mulPose(Axis.ZP.rotationDegrees(state.itemRotation + 120F * (slot + 1)));
            poseStack.translate(0, -0.05, -0.2);
            float scale = item.usesBlockLight() ? 0.475F : 0.3F;
            poseStack.scale(scale, scale, scale);
            item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        private Direction facing = Direction.NORTH;
        private float bladeRotation;
        private float itemRotation;
        private List<ItemStackRenderState> items = List.of();
    }
}
