package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.ReplicatorBlock;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.client.screen.addon.WidgetScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.network.locator.ILocatable;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ReplicatorMotorAddon extends WidgetScreenAddon {

    public static ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/toasts.png");

    private final ReplicatorBlockEntity blockEntity;
    private EditBox editBox;
    private String lastValue;

    public ReplicatorMotorAddon(ReplicatorBlockEntity blockEntity, int posX, int posY) {
        super(posX, posY, new EditBox(Minecraft.getInstance().font, 85, 20, 160, 26, Component.translatable("itemGroup.search")));
        this.blockEntity = blockEntity;
        this.lastValue = "";
        this.editBox = (EditBox) getWidget();
        this.editBox.setValue(blockEntity.getMotorSpeedMultiplier() + "");
        this.editBox.setFilter(s -> {
            if (s.isEmpty()) return true;
            try {
                var value = Integer.parseInt(s);
                if (value <= 100) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return false;
        });
        this.editBox.setMaxLength(3);
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
        if (blockEntity.getBlockState().hasProperty(ReplicatorBlock.HAS_MOTOR) && blockEntity.getBlockState().getValue(ReplicatorBlock.HAS_MOTOR)) {
            this.editBox.setResponder(s -> {
                if (s.isEmpty()) return;
                if (!this.lastValue.equals(s) && screen instanceof AbstractContainerScreen<?> containerScreen && containerScreen.getMenu() instanceof ILocatable locatable) {
                    var compound = new CompoundTag();
                    compound.putInt("Multiplier", Integer.parseInt(s));
                    Titanium.NETWORK.sendToServer(new ButtonClickNetworkMessage(locatable.getLocatorInstance(), 124578, compound));
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000);
                            this.editBox.setValue(blockEntity.getMotorSpeedMultiplier() + "");
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
                this.lastValue = s;
            });
            super.drawBackgroundLayer(guiGraphics, screen, iAssetProvider, guiX, guiY, mouseX, mouseY, partialTicks);
            guiGraphics.blit(TEXTURE, guiX + this.getPosX(), guiY + this.getPosY(), 0, 0, 160, 30);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tooltip.replication_motor.acceleration"), guiX + this.getPosX() + 7, guiY + this.getPosY() + 8, 0x72e567, false);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(11 + Minecraft.getInstance().font.width(Component.translatable("tooltip.replication_motor.acceleration").getString()), 8, 0);
            this.editBox.render(guiGraphics, mouseX, mouseY, partialTicks);
            guiGraphics.pose().popPose();
            guiGraphics.drawString(Minecraft.getInstance().font, "%", guiX + this.getPosX() + 12 + Minecraft.getInstance().font.width(Component.translatable("tooltip.replication_motor.acceleration").getString()) + Minecraft.getInstance().font.width("000"), guiY + this.getPosY() + 8, 0x72e567, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tooltip.replication_motor.failure_chance")
                    .append(Component.literal(blockEntity.getFailureChance() + "").withStyle(ChatFormatting.RED))
                    .append(" %"), guiX + this.getPosX() + 7, guiY + this.getPosY() + 8 + Minecraft.getInstance().font.lineHeight, 0x72e567, false);
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        if (blockEntity.getBlockState().hasProperty(ReplicatorBlock.HAS_MOTOR) && blockEntity.getBlockState().getValue(ReplicatorBlock.HAS_MOTOR)) {
            //super.drawForegroundLayer(guiGraphics, screen, iAssetProvider, guiX, guiY, mouseX, mouseY, partialTicks);
            /*guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("itemGroup.search").getString(), guiX + this.getPosX() + 85, guiY + this.getPosY() + 12, 0x72e567, false);
            if (editBox.isFocused()) {
                guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("itemGroup.search.tooltip").getString(), guiX + this.getPosX() + 85, guiY + this.getPosY() + 25, 0x72e567, false);
            }*/

        }
    }
}
