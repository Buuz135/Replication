package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.block.tile.BaseMatterTankBlockEntity;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.client.screen.addon.WidgetScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.network.locator.ILocatable;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class MatterTankPriorityAddon extends WidgetScreenAddon {

    private final BaseMatterTankBlockEntity blockEntity;
    private final EditBox editBox;
    private ILocatable locatable;
    private String lastValue;

    public MatterTankPriorityAddon(BaseMatterTankBlockEntity blockEntity, int posX, int posY) {
        super(posX, posY, new EditBox(Minecraft.getInstance().font, 36, 12, Component.translatable("tooltip.replication.tank.insert_priority")));
        this.blockEntity = blockEntity;
        this.editBox = (EditBox) this.getWidget();
        this.lastValue = Integer.toString(blockEntity.getPriority());
        this.editBox.setValue(this.lastValue);
        this.editBox.setFilter(value -> value.isEmpty() || value.equals("-") || isInteger(value));
        this.editBox.setMaxLength(6);
        this.editBox.setBordered(false);
        this.editBox.setTextColor(0xFF72E567);
        this.editBox.setResponder(this::sendPriority);
    }

    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private void sendPriority(String value) {
        if (value.isEmpty() || value.equals("-") || value.equals(this.lastValue) || this.locatable == null) {
            return;
        }
        CompoundTag tag = new CompoundTag();
        tag.putInt("Priority", Integer.parseInt(value));
        Titanium.NETWORK.sendToServer(new ButtonClickNetworkMessage(this.locatable.getLocatorInstance(), 124578, tag));
        this.lastValue = value;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider assets, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        if (screen instanceof AbstractContainerScreen<?> containerScreen && containerScreen.getMenu() instanceof ILocatable menu) {
            this.locatable = menu;
        }
        Component label = Component.translatable("tooltip.replication.tank.priority");
        int x = guiX + this.getPosX();
        int y = guiY + this.getPosY();
        int textWidth = screen.getFont().width(label);
        graphics.text(screen.getFont(), label, x, y, 0xFF72E567, false);
        this.editBox.setX(x + textWidth);
        this.editBox.setY(y - 2);
        this.editBox.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.horizontalLine(x + textWidth, x + textWidth + 35, y + 9, 0xFF72E567);
    }

    @Override
    public void drawForegroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider assets, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
    }
}
