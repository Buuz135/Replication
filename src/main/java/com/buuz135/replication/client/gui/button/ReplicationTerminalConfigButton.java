package com.buuz135.replication.client.gui.button;

import com.buuz135.replication.ReplicationRegistry;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.network.locator.LocatorInstance;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ReplicationTerminalConfigButton extends Button {
    private final Identifier texture;
    private final LocatorInstance locatable;
    private final Type type;
    private final Runnable changed;
    private int state;
    private final int textureX;
    private final int textureY;
    private final int hoveredTextureX;
    private final int hoveredTextureY;

    public ReplicationTerminalConfigButton(int x, int y, int width, int height, Identifier texture, LocatorInstance locatable, Type type, int defaultState, int textureX, int textureY, int hoveredTextureX, int hoveredTextureY) {
        this(x, y, width, height, texture, locatable, type, defaultState, textureX, textureY, hoveredTextureX, hoveredTextureY, () -> {
        });
    }

    public ReplicationTerminalConfigButton(int x, int y, int width, int height, Identifier texture, LocatorInstance locatable, Type type, int defaultState, int textureX, int textureY, int hoveredTextureX, int hoveredTextureY, Runnable changed) {
        super(x, y, width, height, Component.empty(), button -> {
        }, supplier -> Component.empty());
        this.texture = texture;
        this.locatable = locatable;
        this.type = type;
        this.state = defaultState;
        this.textureX = textureX;
        this.textureY = textureY;
        this.hoveredTextureX = hoveredTextureX;
        this.hoveredTextureY = hoveredTextureY;
        this.changed = changed;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        CompoundTag component = new CompoundTag();
        state = (++state) % type.states;
        component.putString("type", type.name());
        component.putInt("state", state);
        Titanium.NETWORK.sendToServer(new ButtonClickNetworkMessage(locatable, 999, component));
        changed.run();
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int u = isHovered() ? hoveredTextureX : textureX;
        int v = (isHovered() ? hoveredTextureY : textureY) + 9 * state;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), u, v, width, height, 256, 256);
        if (isHovered()) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, Component.translatable("tooltip.replication.terminal." + type.name().toLowerCase() + ".state_" + state), mouseX, mouseY);
        }
    }

    public int getState() {
        return state;
    }

    @Override
    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
    }

    public enum Type {
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
