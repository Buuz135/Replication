package com.buuz135.replication.client.render;

import com.buuz135.replication.Replication;
import com.buuz135.replication.client.render.shader.ReplicationRenderTypes;
import com.hrznstudio.titanium.reward.storage.ClientRewardStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class ContributorsAuraRender extends RenderLayer<AvatarRenderState, PlayerModel> {

    private static final Identifier AURA_REWARD = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "aura");

    public ContributorsAuraRender(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        if (state.isInvisible || Minecraft.getInstance().level == null) {
            return;
        }
        Entity entity = Minecraft.getInstance().level.getEntity(state.id);
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }
        var reward = ClientRewardStorage.REWARD_STORAGE.getRewards().get(player.getUUID());
        if (reward == null) {
            return;
        }
        String enabled = reward.getEnabled().get(AURA_REWARD);
        if (enabled == null) {
            return;
        }
        Replication.AuraType type;
        try {
            type = Replication.AuraType.valueOf(enabled);
        } catch (IllegalArgumentException exception) {
            return;
        }
        RenderType renderType;
        if (type.isUsePipeShader()) {
            renderType = ReplicationRenderTypes.matter(type.getIdentifier(), true);
        } else if (type == Replication.AuraType.SPOOK) {
            renderType = RenderTypes.entityTranslucent(type.getIdentifier());
        } else {
            renderType = RenderTypes.energySwirl(type.getIdentifier(), 0, state.ageInTicks * 0.01F);
        }
        collector.order(1).submitModel(
                getParentModel(),
                state,
                poseStack,
                renderType,
                lightCoords,
                OverlayTexture.NO_OVERLAY,
                0xCCFFFFFF,
                null,
                state.outlineColor,
                null
        );
    }
}
