package com.buuz135.replication.client.gui.button;

import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.network.locator.LocatorInstance;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ReplicationTerminalConfigButton extends Button {
    private final ResourceLocation textureRL;
    private final LocatorInstance locatable;
    private final Type type;
    private int state;
    private final int textureX;
    private final int textureY;
    private final int hoveredTextureX;
    private final int hoveredTextureY;

    protected ReplicationTerminalConfigButton(int pX, int pY, int pWidth, int pHeight, ResourceLocation textureRL, LocatorInstance locatable,  Type type, int defaultState, int textureX, int textureY,int hoveredTextureX, int hoveredTextureY) {
        super(pX, pY, pWidth, pHeight, Component.empty(), button -> {}, supplier -> Component.empty());
        this.textureRL = textureRL;
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
        Titanium.NETWORK.sendToServer(new ButtonClickNetworkMessage(locatable, 999, component));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.isHovered = pMouseX >= this.getX() && pMouseY >= this.getY() && pMouseX < this.getX() + this.width && pMouseY < this.getY() + this.height;

        // TODO (RID): Make the +9 part of the Constructor
        guiGraphics.blit(textureRL, this.getX(), this.getY(), this.textureX, this.textureY + 9 * state, getWidth(), getHeight());
        if (isHovered) {
            guiGraphics.blit(textureRL, this.getX(), this.getY(), this.hoveredTextureX, this.hoveredTextureY + 9 * state, getWidth(), getHeight());
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("tooltip.replication.terminal." + type.name().toLowerCase() + ".state_" + getState()), (int) pMouseX, (int) pMouseY);
        } else {
            guiGraphics.blit(textureRL, this.getX(), this.getY(), this.textureX, this.textureY + 9 * state, getWidth(), getHeight());
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

    @Override
    public void playDownSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
    }

    public static enum Type{
        SORTING_TYPE(2),
        SORTING_DIRECTION(2),
        MATTEROPEDIA_TYPE(2),
        MATTEROPEDIA_DIRECTION(2);

        private final int states;

        Type(int states) {
            this.states = states;
        }
    }
}
