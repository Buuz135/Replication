package com.buuz135.replication.client.gui;

import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.network.locator.LocatorInstance;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class ReplicationTerminalConfigButton extends Button {

    private final LocatorInstance locatable;
    private final Type type;
    private int state;

    protected ReplicationTerminalConfigButton(int pX, int pY, int pWidth, int pHeight, LocatorInstance locatable,  Type type, int defaultState) {
        super(pX, pY, pWidth, pHeight, Component.empty(), button -> {}, supplier -> Component.empty());
        this.locatable = locatable;
        this.type = type;
        this.state = defaultState;
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
        guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.getX(), this.getY(), 240, type.yPos + 16*state, 16,16);
    }

    public int getState() {
        return state;
    }

    public static enum Type{
        SORTING_TYPE(15, 2), SORTING_DIRECTION(47, 2);

        private final int yPos;
        private final int states;

        Type(int yPos, int states) {
            this.yPos = yPos;
            this.states = states;
        }

        public int getyPos() {
            return yPos;
        }
    }
}