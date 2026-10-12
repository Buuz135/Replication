package com.buuz135.replication.client.render;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.MatterPipeBlock;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.client.render.shader.ReplicationRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class MatterPipeRenderer implements BlockEntityRenderer<MatterPipeBlockEntity, MatterPipeRenderer.State> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/block/shader.png");

    public MatterPipeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(MatterPipeBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.north = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.NORTH));
        state.south = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.SOUTH));
        state.east = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.EAST));
        state.west = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.WEST));
        state.up = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.UP));
        state.down = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.DOWN));
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        submitNodeCollector.submitCustomGeometry(poseStack, ReplicationRenderTypes.matter(TEXTURE, false), (pose, buffer) -> {
            int color = 0xCCFFFFFF;
            ReplicationRenderHelper.texturedBox(pose, buffer, new AABB(0.375, 0.375, 0.375, 0.625, 0.625, 0.625), color, state.lightCoords, 0, 0, 1, 1);
            if (state.north) {
                ReplicationRenderHelper.texturedBox(pose, buffer, new AABB(0.375, 0.375, 0, 0.625, 0.625, 0.375), color, state.lightCoords, 0, 0, 1, 1);
            }
            if (state.south) {
                ReplicationRenderHelper.texturedBox(pose, buffer, new AABB(0.375, 0.375, 0.625, 0.625, 0.625, 1), color, state.lightCoords, 0, 0, 1, 1);
            }
            if (state.east) {
                ReplicationRenderHelper.texturedBox(pose, buffer, new AABB(0.625, 0.375, 0.375, 1, 0.625, 0.625), color, state.lightCoords, 0, 0, 1, 1);
            }
            if (state.west) {
                ReplicationRenderHelper.texturedBox(pose, buffer, new AABB(0, 0.375, 0.375, 0.375, 0.625, 0.625), color, state.lightCoords, 0, 0, 1, 1);
            }
            if (state.up) {
                ReplicationRenderHelper.texturedBox(pose, buffer, new AABB(0.375, 0.625, 0.375, 0.625, 1, 0.625), color, state.lightCoords, 0, 0, 1, 1);
            }
            if (state.down) {
                ReplicationRenderHelper.texturedBox(pose, buffer, new AABB(0.375, 0, 0.375, 0.625, 0.375, 0.625), color, state.lightCoords, 0, 0, 1, 1);
            }
        });
    }

    public static class State extends BlockEntityRenderState {
        private boolean north;
        private boolean south;
        private boolean east;
        private boolean west;
        private boolean up;
        private boolean down;
    }
}
