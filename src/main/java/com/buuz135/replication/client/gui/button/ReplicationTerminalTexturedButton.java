package com.buuz135.replication.client.gui.button;

import com.buuz135.replication.ReplicationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ReplicationTerminalTexturedButton extends Button {
    private final Identifier texture;
    private final Component tooltip;
    private final int textureX;
    private final int textureY;
    private final int hoveredTextureX;
    private final int hoveredTextureY;

    public ReplicationTerminalTexturedButton(int x, int y, int width, int height, Component component, Identifier texture, String tooltip, int textureX, int textureY, int hoveredTextureX, int hoveredTextureY, OnPress onPress) {
        super(x, y, width, height, component, onPress, supplier -> Component.empty());
        this.texture = texture;
        this.tooltip = Component.translatable(tooltip);
        this.textureX = textureX;
        this.textureY = textureY;
        this.hoveredTextureX = hoveredTextureX;
        this.hoveredTextureY = hoveredTextureY;
        setFGColor(0xFF72E567);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int u = isHovered() ? hoveredTextureX : textureX;
        int v = isHovered() ? hoveredTextureY : textureY;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), u, v, width, height, 256, 256);
        extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
        if (isHovered()) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
    }
}
