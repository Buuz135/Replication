package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.client.gui.addons.MatterPatternButton;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ReplicationRequestWidget extends AbstractWidget implements Renderable {

    private static ResourceLocation TEXTURE = new ResourceLocation(Replication.MOD_ID, "textures/gui/request_window.png");

    private final MatterPatternButton matterPatternButton;
    private final ReplicationTerminalScreen replicationTerminalScreen;
    private final Button closeButton;
    private final EditBox amountBox;
    private final Checkbox checkbox;
    private List<AbstractWidget> widgets;

    public ReplicationRequestWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, MatterPatternButton matterPatternButton, ReplicationTerminalScreen replicationTerminalContainer) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.matterPatternButton = matterPatternButton;
        this.replicationTerminalScreen = replicationTerminalContainer;
        this.widgets = new ArrayList<>();
        this.closeButton = new ReplicationTerminalTexturedButton(this.getX() + 239, this.getY() + 8, 9, 9, Component.empty(),
                Component.translatable("replication.close").getString(), 247, 50,238, 50, button -> this.replicationTerminalScreen.disableRequest());

//        this.closeButton = new Button.Builder(Component.literal("x"), button -> this.replicationTerminalScreen.disableRequest())
//                .bounds(this.getX() + 177 - 16, this.getY() + 4, 12, 12)
//                .build();

        this.widgets.add(this.closeButton);
        this.amountBox = new EditBox(Minecraft.getInstance().font, this.getX() + 38, this.getY() + 43, 80, 16, Component.literal("1"));
        this.amountBox.setMaxLength(50);
        this.amountBox.setBordered(true);
        this.amountBox.setVisible(true);
        this.amountBox.setTextColor(16777215);
        this.amountBox.setValue("1");
        this.widgets.add(this.amountBox);
        var amount = new int[]{1, 8 , 16 , 64};
        for (int i = 0; i < amount.length; i++) {
            var am = amount[i];
            var incrementButton = new Button.Builder(Component.literal("+" + am), button -> {
                addNumber(am);
            })
                    .bounds(this.getX() + 20 + 34*i, this.getY() + 18, 32, 18)
                    .build();
            this.widgets.add(incrementButton);
            var decrementButton = new Button.Builder(Component.literal("-" + am), button -> {
                addNumber(-am);
            })
                    .bounds(this.getX() + 20 + 34*i, this.getY() + 66, 32, 18)
                    .build();
            this.widgets.add(decrementButton);
        }
        this.checkbox = new Checkbox(this.getX() + 20 , this.getY() + 86, 20, 20, Component.translatable("replication.single_mode").withStyle(ChatFormatting.DARK_GRAY), true, false);
        this.widgets.add(this.checkbox);
        this.widgets.add(new Button.Builder(Component.translatable("replication.replicate"), button -> {
            this.replicationTerminalScreen.createTask(matterPatternButton.pattern(), Integer.parseInt(this.amountBox.getValue()), checkbox.selected());
        })
                .bounds(this.getX() + 38 + 82, this.getY() + 42, 50, 18)
                .build());
    }

    public void addNumber(int number){
        var current = Long.parseLong(this.amountBox.getValue());
        if (current == 1 && number != 1){
            current = number;
        } else {
            current += number;
        }
        if (current <= 0){
            current = 1;
        }
        current = Math.min(current, Integer.MAX_VALUE);
        current = Math.min(current, this.matterPatternButton.cachedAmount());
        this.amountBox.setValue(current + "");
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, 177, 102);
        guiGraphics.renderItem(matterPatternButton.pattern().getStack(), this.getX() + 16, this.getY() + 43);
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
        guiGraphics.drawString(Minecraft.getInstance().font,  Component.translatable("replication.parallel_mode").withStyle(ChatFormatting.DARK_GRAY), this.getX() + 32 , this.getY() + 87, 0xFFFFFF, false);
        //this.widgets.forEach(abstractWidget -> abstractWidget.render(guiGraphics, mouseX, mouseY, v));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public EditBox getAmountBox() {
        return amountBox;
    }
}
