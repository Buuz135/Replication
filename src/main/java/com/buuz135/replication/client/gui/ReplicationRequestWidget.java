package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.client.gui.addons.MatterPatternButton;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedButton;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedCheckbox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ReplicationRequestWidget extends AbstractWidget {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/request_window.png");
    private static final Identifier BUTTONS = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png");
    private final MatterPatternButton matterPatternButton;
    private final ReplicationTerminalScreen screen;
    private final EditBox amountBox;
    private final ReplicationTerminalTexturedCheckbox checkbox;
    private final List<AbstractWidget> widgets = new ArrayList<>();

    public ReplicationRequestWidget(int x, int y, int width, int height, Component message, MatterPatternButton matterPatternButton, ReplicationTerminalScreen screen) {
        super(x, y, width, height, message);
        this.matterPatternButton = matterPatternButton;
        this.screen = screen;
        widgets.add(new ReplicationTerminalTexturedButton(x + 159, y + 8, 9, 9, Component.empty(), BUTTONS, "tooltip.replication.close", 247, 50, 238, 50, button -> screen.disableRequest()));
        this.amountBox = new EditBox(Minecraft.getInstance().font, x + 45, y + 47, 84, 16, Component.literal("1"));
        this.amountBox.setMaxLength(10);
        this.amountBox.setBordered(false);
        this.amountBox.setTextColor(0xff72e567);
        this.amountBox.setValue("1");
        this.amountBox.setFilter(value -> value.isEmpty() || value.chars().allMatch(Character::isDigit));
        widgets.add(amountBox);
        int[] amounts = {1, 8, 16, 64};
        for (int index = 0; index < amounts.length; index++) {
            int amount = amounts[index];
            widgets.add(new ReplicationTerminalTexturedButton(x + 19 + 36 * index, y + 21, 30, 16, Component.literal("+" + amount), BUTTONS, "+" + amount, 226, 64, 226, 80, button -> addNumber(amount)));
            widgets.add(new ReplicationTerminalTexturedButton(x + 19 + 36 * index, y + 65, 30, 16, Component.literal("-" + amount), BUTTONS, "-" + amount, 226, 64, 226, 80, button -> addNumber(-amount)));
        }
        this.checkbox = new ReplicationTerminalTexturedCheckbox(x + 19, y + 84, 9, 9, Component.translatable("replication.parallel_mode"), BUTTONS, 238, 116, true, true);
        widgets.add(checkbox);
        widgets.add(new ReplicationTerminalTexturedButton(x + 137, y + 41, 20, 20, Component.empty(), BUTTONS, "replication.replicate", 236, 96, 216, 96, button -> screen.createTask(matterPatternButton.pattern(), getRequestedAmount(), checkbox.selected())));
    }

    public void addNumber(int number) {
        long current;
        try {
            current = Long.parseLong(amountBox.getValue());
        } catch (NumberFormatException exception) {
            current = 0;
        }
        current = current == 1 && number != 1 ? number : current + number;
        current = Math.max(1, Math.min(current, Math.min(Integer.MAX_VALUE, matterPatternButton.cachedAmount())));
        amountBox.setValue(Long.toString(current));
    }

    private int getRequestedAmount() {
        try {
            return Math.max(1, Math.min(matterPatternButton.cachedAmount(), Integer.parseInt(amountBox.getValue())));
        } catch (NumberFormatException exception) {
            return 1;
        }
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), 0, 0, 176, 102, 256, 256);
        graphics.item(matterPatternButton.pattern().getStack(), getX() + 21, getY() + 43);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    public EditBox getAmountBox() {
        return amountBox;
    }
}
