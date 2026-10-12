package com.buuz135.replication.client.render;

import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.buuz135.replication.calculation.MatterValue;
import com.buuz135.replication.calculation.client.ClientReplicationCalculation;
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
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ReplicatorRenderer implements BlockEntityRenderer<ReplicatorBlockEntity, ReplicatorRenderer.State> {

    public static final StandaloneModelKey<BlockStateModelPart> PLATE_KEY = new StandaloneModelKey<>(() -> "replication:block/replicator_plate");
    private static BlockStateModelPart plate;
    private final ItemModelResolver itemModelResolver;

    public ReplicatorRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    public static void setPlate(BlockStateModelPart plate) {
        ReplicatorRenderer.plate = plate;
    }

    private static int getMatterColor(ReplicatorBlockEntity blockEntity, float progress) {
        if (blockEntity.getCraftingStack().isEmpty() || blockEntity.getAction() != 0) {
            return 0;
        }
        var compound = ClientReplicationCalculation.getMatterCompound(blockEntity.getCraftingStack());
        if (compound == null || compound.getValues().isEmpty()) {
            return 0;
        }
        double total = compound.getValues().values().stream().mapToDouble(MatterValue::getAmount).sum();
        double current = progress * total;
        double consumed = 0;
        for (MatterValue value : compound.getValues().values()) {
            consumed += value.getAmount();
            if (consumed >= current) {
                float[] color = value.getMatter().getColor().get();
                int red = Math.max(0, Math.min(255, Math.round(color[0] * 255)));
                int green = Math.max(0, Math.min(255, Math.round(color[1] * 255)));
                int blue = Math.max(0, Math.min(255, Math.round(color[2] * 255)));
                return 0xBF000000 | red << 16 | green << 8 | blue;
            }
        }
        return 0;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ReplicatorBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(RotatableBlock.FACING_HORIZONTAL);
        state.progress = blockEntity.getMaxProgress() <= 0 ? 0 : Math.min(1, (blockEntity.getProgress() + partialTicks) / blockEntity.getMaxProgress());
        state.color = getMatterColor(blockEntity, state.progress);
        ItemStack stack = blockEntity.getAction() == 0 && !blockEntity.isCurrentTaskAFailure() ? blockEntity.getCraftingStack() : ItemStack.EMPTY;
        this.itemModelResolver.updateForTopItem(state.item, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        ReplicationRenderHelper.rotate(poseStack, state.facing);
        if ((state.color >>> 24) != 0) {
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), (pose, buffer) ->
                    ReplicationRenderHelper.colorBox(pose, buffer, new AABB(0.125, 0.15, 0.125, 0.875, 0.155, 0.75), state.color));
        }
        poseStack.translate(0, -ReplicatorBlockEntity.LOWER_PROGRESS + ReplicatorBlockEntity.LOWER_PROGRESS * state.progress - 0.001F, 0);
        if (plate != null) {
            submitNodeCollector.submitBlockModel(poseStack, Sheets.cutoutBlockSheet(), List.of(plate), BlockModelRenderState.EMPTY_TINTS, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        if (!state.item.isEmpty()) {
            poseStack.translate(0.5F, 0.56F, 0.45F);
            float scale = state.item.usesBlockLight() ? 0.75F : 0.4F;
            poseStack.scale(scale, scale, scale);
            state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        private final ItemStackRenderState item = new ItemStackRenderState();
        private Direction facing = Direction.NORTH;
        private float progress;
        private int color;
    }
}
