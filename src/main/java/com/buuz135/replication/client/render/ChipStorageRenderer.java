package com.buuz135.replication.client.render;

import com.buuz135.replication.block.tile.ChipStorageBlockEntity;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class ChipStorageRenderer implements BlockEntityRenderer<ChipStorageBlockEntity, ChipStorageRenderer.State> {

    public static final List<StandaloneModelKey<BlockStateModelPart>> CHIP_KEYS = IntStream.range(0, 8)
            .mapToObj(slot -> new StandaloneModelKey<BlockStateModelPart>(() -> "replication:block/chips/chip_storage_chips_" + slot))
            .toList();
    private static List<BlockStateModelPart> chipModels = Collections.emptyList();

    public ChipStorageRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void setChipModels(List<BlockStateModelPart> chipModels) {
        ChipStorageRenderer.chipModels = List.copyOf(chipModels);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ChipStorageBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(RotatableBlock.FACING_HORIZONTAL);
        state.occupied = new boolean[blockEntity.getChips().getSlots()];
        for (int slot = 0; slot < blockEntity.getChips().getSlots(); slot++) {
            state.occupied[slot] = !blockEntity.getChips().getStackInSlot(slot).isEmpty();
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        ReplicationRenderHelper.rotate(poseStack, state.facing);
        for (int slot = 0; slot < state.occupied.length && slot < chipModels.size(); slot++) {
            if (state.occupied[slot]) {
                submitNodeCollector.submitBlockModel(poseStack, Sheets.cutoutBlockSheet(), List.of(chipModels.get(slot)), BlockModelRenderState.EMPTY_TINTS, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            }
        }
        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        private Direction facing = Direction.NORTH;
        private boolean[] occupied = new boolean[0];
    }
}
