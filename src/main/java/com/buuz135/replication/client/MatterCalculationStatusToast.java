package com.buuz135.replication.client;

import com.buuz135.replication.Replication;
import com.hrznstudio.titanium.util.AssetUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class MatterCalculationStatusToast implements Toast {

    private final Component title;
    private final Component subtitle;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;
    private final ItemStack display;

    public MatterCalculationStatusToast(ItemStack stack, Component titleComponent, @Nullable Component subtitleComponent, boolean drawProgressBar) {
        this.title = titleComponent;
        this.subtitle = subtitleComponent;
        this.display = stack;
    }

    @Override
    public Visibility getWantedVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager toastManager, long time) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, Font font, long time) {
        var texture = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/toasts.png");
        AssetUtil.blit(guiGraphics, texture, 0, 0, 0, 0, this.width(), this.height());
        guiGraphics.item(display, 9, 8);
        if (this.subtitle == null) {
            guiGraphics.text(font, this.title, 30, 12, -11534256, false);
        } else {
            guiGraphics.text(font, this.title, 30, 7, -11534256);
            guiGraphics.text(font, this.subtitle, 30, 18, -16777216);
        }
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }
}
