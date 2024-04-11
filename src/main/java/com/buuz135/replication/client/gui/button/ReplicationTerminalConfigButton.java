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

public class ReplicationTerminalConfigButton extends Button {

    private final LocatorInstance locatable;
    private final Type type;
    private int state;
    private final int textureX;
    private final int textureY;
    private final int hoveredTextureX;
    private final int hoveredTextureY;

    protected ReplicationTerminalConfigButton(int pX, int pY, int pWidth, int pHeight, LocatorInstance locatable,  Type type, int defaultState, int textureX, int textureY,int hoveredTextureX, int hoveredTextureY) {
        super(pX, pY, pWidth, pHeight, Component.empty(), button -> {}, supplier -> Component.empty());
        this.locatable = locatable;
        this.type = type;
        this.state = defaultState;
        this.textureX = textureX;
        this.textureY = textureY;
        this.hoveredTextureX = hoveredTextureX;
        this.hoveredTextureY = hoveredTextureY;
    }

    @Override
    public void onPress() {
        var component = new CompoundTag();
        state = (++state) % type.states;
        component.putString("type", type.name());
        component.putInt("state", state);
        Titanium.NETWORK.get().sendToServer(new ButtonClickNetworkMessage(locatable, 999, component));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.isHovered = pMouseX >= this.getX() && pMouseY >= this.getY() && pMouseX < this.getX() + this.width && pMouseY < this.getY() + this.height;

        guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.textureX, this.textureY + 9 * state, getWidth(), getHeight());
        if (isHovered) {
            guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.hoveredTextureX, this.hoveredTextureY + 9 * state, getWidth(), getHeight());
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("tooltip.replication.terminal." + type.name().toLowerCase() + ".state_" + getState()), (int) pMouseX, (int) pMouseY);
        } else {
            guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), this.textureX, this.textureY + 9 * state, getWidth(), getHeight());
        }
        int i = this.getFGColor();
        this.renderString(guiGraphics, Minecraft.getInstance().font, i | Mth.ceil(this.alpha * 255.0F) << 24);
//
//        guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), 247, type.yPos + 9 * state, 9,9);
//        if (isHovered) {
//            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("tooltip.replication.terminal." + type.name().toLowerCase() + ".state_" + getState()), (int) pMouseX, (int) pMouseY);
//            Rectangle area = new Rectangle(getX(), getY(), getWidth()-1, getHeight() -1);
//            var color = 0xffffffff;
//            AssetUtil.drawHorizontalLine(guiGraphics, area.x, area.x + area.width, area.y, color);
//            AssetUtil.drawHorizontalLine(guiGraphics, area.x, area.x + area.width, area.y + area.height, color);
//            AssetUtil.drawVerticalLine(guiGraphics, area.x, area.y, area.y + area.height, color);
//            AssetUtil.drawVerticalLine(guiGraphics, area.x + area.width, area.y, area.y + area.height, color);
//        }
    }

    public int getState() {
        return state;
    }

    public static enum Type{
        SORTING_TYPE(2),
        SORTING_DIRECTION(2);

        private final int states;

        Type(int states) {
            this.states = states;
        }
    }
}