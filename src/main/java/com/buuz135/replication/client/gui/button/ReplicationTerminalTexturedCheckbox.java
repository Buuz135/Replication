package com.buuz135.replication.client.gui.button;

import com.buuz135.replication.ReplicationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ReplicationTerminalTexturedCheckbox extends AbstractButton {
    private final Identifier texture;
    private final boolean showLabel;
    private final int textureX;
    private final int textureY;
    private boolean selected;

    public ReplicationTerminalTexturedCheckbox(int x, int y, int width, int height, Component component, Identifier texture, int textureX, int textureY, boolean selected, boolean showLabel) {
        super(x, y, width, height, component);
        this.texture = texture;
        this.textureX = textureX;
        this.textureY = textureY;
        this.selected = selected;
        this.showLabel = showLabel;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        selected = !selected;
    }

    public boolean selected() {
        return selected;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), textureX + (selected ? 0 : 9), textureY, width, height, 256, 256);
        if (showLabel) {
            graphics.text(Minecraft.getInstance().font, getMessage(), getX() + width + 5, getY(), 0xFF72E567, false);
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
    }
}
