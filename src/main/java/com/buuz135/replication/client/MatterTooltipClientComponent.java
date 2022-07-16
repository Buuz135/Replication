package com.buuz135.replication.client;

import com.buuz135.replication.Replication;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

public class MatterTooltipClientComponent implements ClientTooltipComponent {

    public static final ResourceLocation BAR = new ResourceLocation(Replication.MOD_ID, "textures/gui/matter_bar.png");
    private final MatterTooltipComponent instance;

    public MatterTooltipClientComponent(MatterTooltipComponent instance) {
        this.instance = instance;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public int getWidth(Font p_169952_) {
        return 128;
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix4f, MultiBufferSource.BufferSource bufferSource) {
        var text = String.valueOf(instance.getInstance().getAmount().intValue());
        var length = 12;
        font.drawInBatch(text, (float) ((float)x + 52 - font.width(text) / 2D + length), (float)y +2, -1, true, matrix4f, bufferSource, false, 0, 15728880);
    }

    @Override
    public void renderImage(Font p_194048_, int x, int y, PoseStack poseStack, ItemRenderer p_194052_, int p_194053_) {
        RenderSystem.setShaderTexture(0, BAR);
        RenderSystem.setShaderColor(0.75f, 0.75f,0.75f,1);
        var glitch = Minecraft.getInstance().level.random.nextDouble() < 0.007;
        Minecraft.getInstance().screen.blit(poseStack, x + (glitch ? 1 : 0), y +(glitch ? 1 : 0), 0,0, 128, 12, 128, 128);
        var max = 128;
        var steps = (128-2) / 2D;

        for (int i = 0; i < steps / max * Math.min(instance.getInstance().getAmount(), max); i++) {
            if (i > 25 && i < 39) continue;
            Minecraft.getInstance().screen.blit(poseStack, x + i*2 + (glitch ? 1 : 0), y + 3 + (glitch ? 1 : 0), 0,15, 2, 6, 128, 128);
        }

    }
}
