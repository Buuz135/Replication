package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.client.gui.addons.MatterPatternButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ReplicationTaskWidget extends AbstractWidget implements Renderable {

    private static ResourceLocation TEXTURE = new ResourceLocation(Replication.MOD_ID, "textures/gui/task_window.png");

    private final ReplicationTerminalScreen replicationTerminalScreen;
    private final Button closeButton;
    private List<AbstractWidget> widgets;

    public ReplicationTaskWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, ReplicationTerminalScreen replicationTerminalContainer) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.replicationTerminalScreen = replicationTerminalContainer;
        this.widgets = new ArrayList<>();
        this.closeButton = new Button.Builder(Component.literal("x"), button -> this.replicationTerminalScreen.disableTask())
                .bounds(this.getX() + 256 - 16, this.getY() + 4, 12, 12)
                .build();
        this.widgets.add(this.closeButton);
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, 256, 256);

        for (AbstractWidget widget : this.widgets) {
            if (widget instanceof Checkbox){
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.5f,0.5f,0.5f);
                guiGraphics.pose().translate(this.getX() + 20 , this.getY() + 86, 0);
                widget.render(guiGraphics, mouseX, mouseY, v);
                guiGraphics.pose().popPose();
            } else {
                widget.render(guiGraphics, mouseX, mouseY, v);
            }
        }
        //this.widgets.forEach(abstractWidget -> abstractWidget.render(guiGraphics, mouseX, mouseY, v));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

}
