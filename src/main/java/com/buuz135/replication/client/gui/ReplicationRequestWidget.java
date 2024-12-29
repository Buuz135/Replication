package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.client.gui.addons.MatterPatternButton;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedButton;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedCheckbox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ReplicationRequestWidget extends AbstractWidget implements Renderable {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/request_window.png");
    private static final ResourceLocation BUTTONS = ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png");
    private final MatterPatternButton matterPatternButton;
    private final ReplicationTerminalScreen replicationTerminalScreen;
    private final Button closeButton;
    private final EditBox amountBox;
    private final ReplicationTerminalTexturedCheckbox checkbox;
    private List<AbstractWidget> widgets;

    public ReplicationRequestWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, MatterPatternButton matterPatternButton, ReplicationTerminalScreen replicationTerminalContainer) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.matterPatternButton = matterPatternButton;
        this.replicationTerminalScreen = replicationTerminalContainer;
        this.widgets = new ArrayList<>();
        this.closeButton = new ReplicationTerminalTexturedButton(this.getX() + 159, this.getY() + 8, 9, 9, Component.empty(), BUTTONS,
                Component.translatable("tooltip.replication.close").getString(), 247, 50,238, 50, button -> this.replicationTerminalScreen.disableRequest());
        this.widgets.add(this.closeButton);
        this.amountBox = new EditBox(Minecraft.getInstance().font, this.getX() + 45, this.getY() + 47, 94, 16, Component.literal("1"));
        this.amountBox.setMaxLength(50);
        this.amountBox.setBordered(false);
        this.amountBox.setVisible(true);
        this.amountBox.setTextColor(0x72e567);
        this.amountBox.setValue("1");
        this.widgets.add(this.amountBox);
        var amount = new int[]{1, 8 , 16 , 64};
        for (int i = 0; i < amount.length; i++) {
            var am = amount[i];
            var incrementButton = new ReplicationTerminalTexturedButton(this.getX() + 19 + 36 * i, this.getY() + 21, 30, 16,
                    Component.literal("+" + am).setStyle(Style.EMPTY.withColor(Mth.color(114/255f, 229/255f, 103/255f))), BUTTONS,
                    Component.literal("+" + am).getString(), 226, 64,226, 80, button -> {addNumber(am);});
            this.widgets.add(incrementButton);
            var decrementButton = new ReplicationTerminalTexturedButton(this.getX() + 19 + 36 * i, this.getY() + 65, 30, 16,
                    Component.literal("-" + am).setStyle(Style.EMPTY.withColor(Mth.color(114/255f, 229/255f, 103/255f))), BUTTONS,
                    Component.literal("-" + am).getString(), 226, 64,226, 80, button -> {addNumber(-am);});
            this.widgets.add(decrementButton);
        }
        this.checkbox = new ReplicationTerminalTexturedCheckbox(this.getX() + 19, this.getY() + 84, 9, 9,
                Component.translatable("replication.single_mode"), BUTTONS,  238, 116, true, false);
        this.widgets.add(this.checkbox);
        this.widgets.add(new ReplicationTerminalTexturedButton(this.getX() + 137, this.getY() + 41, 20, 20,
                Component.empty(), BUTTONS,
                Component.translatable("replication.replicate").getString(), 236, 96,216, 96,
                        button -> {this.replicationTerminalScreen.createTask(matterPatternButton.pattern(), Integer.parseInt(this.amountBox.getValue()), checkbox.selected());}
                ));
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
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, 176, 102);
        guiGraphics.renderItem(matterPatternButton.pattern().getStack(), this.getX() + 21, this.getY() + 43);
        for (AbstractWidget widget : this.widgets) {
//            if (widget instanceof Checkbox){
//                guiGraphics.pose().pushPose();
////                guiGraphics.pose().scale(0.5f,0.5f,0.5f);
//                guiGraphics.pose().translate(this.getX(), this.getY(), 0);
//                widget.render(guiGraphics, mouseX, mouseY, v);
//                guiGraphics.pose().popPose();
//            } else {
//                widget.render(guiGraphics, mouseX, mouseY, v);
//            }
            widget.render(guiGraphics, mouseX, mouseY, v);
        }
        guiGraphics.drawString(Minecraft.getInstance().font,  Component.translatable("replication.parallel_mode").setStyle(Style.EMPTY.withColor(Mth.color(114/255f, 229/255f, 103/255f))), this.getX() + 32 , this.getY() + 85, 0xFFFFFF, false);
        //this.widgets.forEach(abstractWidget -> abstractWidget.render(guiGraphics, mouseX, mouseY, v));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public EditBox getAmountBox() {
        return amountBox;
    }

    @Override
    public void playDownSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
    }
}
