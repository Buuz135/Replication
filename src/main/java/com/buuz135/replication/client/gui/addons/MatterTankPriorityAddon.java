package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.block.tile.BaseMatterTankBlockEntity;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.client.screen.addon.WidgetScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.network.locator.ILocatable;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.hrznstudio.titanium.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class MatterTankPriorityAddon extends WidgetScreenAddon {

    private final BaseMatterTankBlockEntity blockEntity;
    private final EditBox editBox;
    private String lastValue;

    public MatterTankPriorityAddon(BaseMatterTankBlockEntity blockEntity, int posX, int posY) {
        super(posX, posY, new EditBox(Minecraft.getInstance().font, 85, 20, 160, 26, Component.translatable("tooltip.replication.tank.insert_priority")));
        this.blockEntity = blockEntity;
        this.lastValue = "";
        this.editBox = (EditBox) getWidget();
        this.editBox.setValue(blockEntity.getPriority() + "");
        this.editBox.setFilter(s -> {
            if (s.isEmpty()) return true;
            if (s.charAt(0) == '-') return true;
            try {
                var value = Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
        this.editBox.setMaxLength(6);
        this.editBox.setBordered(false);
        this.editBox.setVisible(true);
        this.editBox.setTextColor(0x72e567);

    }

    @Override
    public int getXSize() {
        return 0;
    }

    @Override
    public int getYSize() {
        return 0;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        this.editBox.setResponder(s -> {
            if (s.isEmpty()) return;
            if (!this.lastValue.equals(s) && screen instanceof AbstractContainerScreen<?> containerScreen && containerScreen.getMenu() instanceof ILocatable locatable) {
                var compound = new CompoundTag();
                if (s.charAt(0) == '-' && s.length() == 1) {
                    compound.putInt("Priority", -0);
                } else {
                    compound.putInt("Priority", Integer.parseInt(s));
                }
                Titanium.NETWORK.sendToServer(new ButtonClickNetworkMessage(locatable.getLocatorInstance(), 124578, compound));
                new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                        this.editBox.setValue(blockEntity.getPriority() + "");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
            this.lastValue = s;
        });
        super.drawBackgroundLayer(guiGraphics, screen, iAssetProvider, guiX, guiY, mouseX, mouseY, partialTicks);
        var textWidth = Minecraft.getInstance().font.width(Component.translatable("tooltip.replication.tank.priority").getString());
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tooltip.replication.tank.priority"), guiX + this.getPosX(), guiY + this.getPosY(), 0x72e567, false);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(textWidth, 0, 0);
        this.editBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.pose().popPose();
        for (int i = 0; i < 18; i++) {
            AssetUtil.drawHorizontalLine(guiGraphics, guiX + this.getPosX() + textWidth + i * 2, guiX + this.getPosX() + textWidth + i * 2, guiY + this.getPosY() + 8, 0xff72e567);
        }


    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {

    }
}
