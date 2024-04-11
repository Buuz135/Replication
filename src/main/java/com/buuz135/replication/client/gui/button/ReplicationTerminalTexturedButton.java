package com.buuz135.replication.client.gui.button;

import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.network.locator.LocatorInstance;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.hrznstudio.titanium.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.awt.*;

public class ReplicationTerminalTexturedButton extends Button {

    private final String tooltip;
    private final int textureX;
    private final int textureY;
    private final int hoveredTextureX;
    private final int hoveredTextureY;

    public ReplicationTerminalTexturedButton(int pX, int pY, int pWidth, int pHeight, Component component, String tooltip, int textureX, int textureY,int hoveredTextureX, int hoveredTextureY, OnPress onPress) {
        super(pX, pY, pWidth, pHeight, component, onPress, supplier -> Component.empty());
        this.tooltip = tooltip;
        this.textureX = textureX;
        this.textureY = textureY;
        this.hoveredTextureX = hoveredTextureX;
        this.hoveredTextureY = hoveredTextureY;
    }


    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.isHovered = pMouseX >= this.getX() && pMouseY >= this.getY() && pMouseX < this.getX() + this.width && pMouseY < this.getY() + this.height;
        guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.textureX, this.textureY, this.width, this.height);
        if (isHovered) {
            guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.hoveredTextureX, this.hoveredTextureY, this.width, this.height);
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable(tooltip), (int) pMouseX, (int) pMouseY);
        } else {
            guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.textureX, this.textureY, this.width, this.height);
        }
        int i = this.getFGColor();
        this.renderString(guiGraphics, Minecraft.getInstance().font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

}