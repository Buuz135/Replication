package com.buuz135.replication.client.gui.button;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class ReplicationTerminalTexturedCheckbox extends Checkbox {
    private final ResourceLocation textureRL;
    private final boolean showLabel;
    private final int textureX;
    private final int textureY;

    public ReplicationTerminalTexturedCheckbox(int pX, int pY, int pWidth, int pHeight, Component component, ResourceLocation textureRL, int textureX, int textureY, boolean pSelected, boolean pShowLabel) {
        super(pX, pY, pWidth, pHeight, component, pSelected);
        this.textureRL = textureRL;
        this.textureX = textureX;
        this.textureY = textureY;
        this.showLabel = pShowLabel;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.enableDepthTest();
        Font font = minecraft.font;
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        if (super.selected()) {
            guiGraphics.blit(textureRL, this.getX(), this.getY(), this.textureX, this.textureY, this.width, this.height);
        } else {
            // TODO (RID): Make the +9 part of the Constructor
            guiGraphics.blit(textureRL, this.getX(), this.getY(), this.textureX + 9, this.textureY, this.width, this.height);
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.showLabel) {
            guiGraphics.drawString(font, this.getMessage(), this.getX() + 24, this.getY() + (this.height - 8) / 2, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
        }
    }

    @Override
    public void playDownSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
    }
}
