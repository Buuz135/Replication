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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ReplicatorMotorAddon extends WidgetScreenAddon {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/toasts.png");
    private final ReplicatorBlockEntity blockEntity;
    private final EditBox editBox;
    private ILocatable locatable;
    private String lastValue;

    public ReplicatorMotorAddon(ReplicatorBlockEntity blockEntity, int posX, int posY) {
        super(posX, posY, new EditBox(Minecraft.getInstance().font, 24, 12, Component.translatable("tooltip.replication_motor.acceleration")));
        this.blockEntity = blockEntity;
        this.editBox = (EditBox) this.getWidget();
        this.lastValue = Integer.toString(blockEntity.getMotorSpeedMultiplier());
        this.editBox.setValue(this.lastValue);
        this.editBox.setFilter(value -> value.isEmpty() || isValidMultiplier(value));
        this.editBox.setMaxLength(3);
        this.editBox.setBordered(false);
        this.editBox.setTextColor(0xFF72E567);
        this.editBox.setResponder(this::sendMultiplier);
    }

    private static boolean isValidMultiplier(String value) {
        try {
            int multiplier = Integer.parseInt(value);
            return multiplier >= 0 && multiplier <= 100;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private void sendMultiplier(String value) {
        if (value.isEmpty() || value.equals(this.lastValue) || this.locatable == null || !isValidMultiplier(value)) {
            return;
        }
        CompoundTag tag = new CompoundTag();
        tag.putInt("Multiplier", Integer.parseInt(value));
        Titanium.NETWORK.sendToServer(new ButtonClickNetworkMessage(this.locatable.getLocatorInstance(), 124578, tag));
        this.lastValue = value;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider assets, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        if (!this.blockEntity.getBlockState().hasProperty(ReplicatorBlock.HAS_MOTOR) || !this.blockEntity.getBlockState().getValue(ReplicatorBlock.HAS_MOTOR)) {
            return;
        }
        if (screen instanceof AbstractContainerScreen<?> containerScreen && containerScreen.getMenu() instanceof ILocatable menu) {
            this.locatable = menu;
        }
        int x = guiX + this.getPosX();
        int y = guiY + this.getPosY();
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, 160, 30, 256, 256);
        Component acceleration = Component.translatable("tooltip.replication_motor.acceleration");
        graphics.text(screen.getFont(), acceleration, x + 7, y + 8, 0xFF72E567, false);
        int editX = x + 11 + screen.getFont().width(acceleration);
        this.editBox.setX(editX);
        this.editBox.setY(y + 6);
        this.editBox.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.text(screen.getFont(), "%", editX + 24, y + 8, 0xFF72E567, false);
        Component failure = Component.translatable("tooltip.replication_motor.failure_chance")
                .append(Component.literal(Double.toString(this.blockEntity.getFailureChance())).withStyle(ChatFormatting.RED))
                .append(" %");
        graphics.text(screen.getFont(), failure, x + 7, y + 8 + screen.getFont().lineHeight, 0xFF72E567, false);
    }

    @Override
    public void drawForegroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider assets, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
    }
}
