package com.buuz135.replication.client.gui.button;

import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ReplicationTerminalTexturedCheckbox extends Checkbox {
    private final boolean showLabel;
    private final int textureX;
    private final int textureY;
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");

    public ReplicationTerminalTexturedCheckbox(int pX, int pY, int pWidth, int pHeight, Component component, int textureX, int textureY, boolean pSelected, boolean pShowLabel) {
        super(pX, pY, pWidth, pHeight, component, pSelected);
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
            guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.textureX, this.textureY, this.width, this.height);
        } else {
            guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.textureX + 9, this.textureY, this.width, this.height);
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.showLabel) {
            guiGraphics.drawString(font, this.getMessage(), this.getX() + 24, this.getY() + (this.height - 8) / 2, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
        }

//        this.isHovered = pMouseX >= this.getX() && pMouseY >= this.getY() && pMouseX < this.getX() + this.width && pMouseY < this.getY() + this.height;
//        guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.textureX, this.textureY, this.width, this.height);
//        if (isHovered) {
//            guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.hoveredTextureX, this.hoveredTextureY, this.width, this.height);
//            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable(tooltip), (int) pMouseX, (int) pMouseY);
//        } else {
//            guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.textureX, this.textureY, this.width, this.height);
//        }
//        int i = this.getFGColor();
//        this.renderString(guiGraphics, Minecraft.getInstance().font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

}
