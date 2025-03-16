/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.replication.client.render;

import com.buuz135.replication.Replication;
import com.buuz135.replication.client.render.shader.ReplicationRenderTypes;
import com.buuz135.replication.client.render.shader.ShaderTexture;
import com.hrznstudio.titanium.reward.storage.ClientRewardStorage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ContributorsAuraRender extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public ContributorsAuraRender(LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int p_225628_3_, AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().containsKey(entitylivingbaseIn.getUUID())) return;
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().get(entitylivingbaseIn.getUUID()).getEnabled().containsKey(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "aura")))
            return;
        Replication.AuraType type = Replication.AuraType.valueOf(ClientRewardStorage.REWARD_STORAGE.getRewards().get(entitylivingbaseIn.getUUID()).getEnabled().get(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "aura")));
        float f = (float) entitylivingbaseIn.tickCount + partialTicks;
        EntityModel<AbstractClientPlayer> entitymodel = this.getParentModel();
        entitymodel.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
        this.getParentModel().copyPropertiesTo(entitymodel);
        RenderType shader = null;
        if (type.isUsePipeShader()) {
            shader = ReplicationRenderTypes.getRenderType("matter_pipe_transparent")
                    .using(List.of(
                            new ShaderTexture(type.getResourceLocation())
                    ));
        } else {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEnergySwirlShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(type.getResourceLocation(), false, false))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                        RenderSystem.enableBlend();
                        if (type != Replication.AuraType.SPOOK)
                            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    })).setOverlayState(new RenderStateShard.OverlayStateShard(true))
                    .setCullState(new RenderStateShard.CullStateShard(false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(0, f * 0.01F))
                    .createCompositeState(true);

            shader = RenderType.create("matter_pipe_transparent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
        }

        entitymodel.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        entitymodel.renderToBuffer(stack, buffer.getBuffer(shader), 100, 100);

    }


}
