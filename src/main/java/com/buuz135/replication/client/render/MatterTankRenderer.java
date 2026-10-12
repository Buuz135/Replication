package com.buuz135.replication.client.render;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.tile.BaseMatterTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class MatterTankRenderer<T extends BaseMatterTankBlockEntity<T>> implements BlockEntityRenderer<T, MatterTankRenderer.State> {

    private final TextureAtlasSprite sprite;

    public MatterTankRenderer(BlockEntityRendererProvider.Context context) {
        this.sprite = context.sprites().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(Replication.MOD_ID, "block/matter")));
    }

    private static int toColor(float[] color) {
        int alpha = Math.max(0, Math.min(255, Math.round(color[3] * 255)));
        int red = Math.max(0, Math.min(255, Math.round(color[0] * 255)));
        int green = Math.max(0, Math.min(255, Math.round(color[1] * 255)));
        int blue = Math.max(0, Math.min(255, Math.round(color[2] * 255)));
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        var tank = blockEntity.getTanks().get(0);
        var matter = tank.getMatter();
        state.fill = matter.isEmpty() || tank.getCapacity() <= 0 ? 0 : Math.min(1, matter.getAmount() / (double) tank.getCapacity());
        if (!matter.isEmpty()) {
            float[] color = matter.getMatterType().getColor().get();
            state.color = toColor(color);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.fill <= 0) {
            return;
        }
        AABB bounds = new AABB(0.2, 0.255, 0.2, 0.8, 0.255 + state.fill * 0.5, 0.8);
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose, buffer) ->
                ReplicationRenderHelper.texturedBox(pose, buffer, bounds, state.color, state.lightCoords,
                        this.sprite.getU0(), this.sprite.getV0(), this.sprite.getU1(), this.sprite.getV1()));
    }

    public static class State extends BlockEntityRenderState {
        private double fill;
        private int color = 0xFFFFFFFF;
    }
}
