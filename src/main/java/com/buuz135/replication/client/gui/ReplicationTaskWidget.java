package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.task.IReplicationTask;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedButton;
import com.buuz135.replication.packet.TaskSyncPacket;
import com.buuz135.replication.util.NumberUtils;
import com.hrznstudio.titanium.util.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReplicationTaskWidget extends AbstractWidget implements Renderable {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Replication.MOD_ID, "textures/gui/task_window.png");

    private final ReplicationTerminalScreen replicationTerminalScreen;
    private final ReplicationTerminalTexturedButton closeButton;
    private final List<AbstractWidget> widgets;
    private List<TaskDisplay> taskDisplays;

    public ReplicationTaskWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, ReplicationTerminalScreen replicationTerminalContainer) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.replicationTerminalScreen = replicationTerminalContainer;
        this.widgets = new ArrayList<>();
        this.closeButton = new ReplicationTerminalTexturedButton(this.getX() + 239, this.getY() + 8, 9, 9, Component.empty(),
                Component.translatable("replication.close").getString(), 247, 50,238, 50, button -> this.replicationTerminalScreen.disableTask());
//                new Button.Builder(Component.literal("x"), button -> this.replicationTerminalScreen.disableTask())
//                .bounds(this.getX() + 256 - 16, this.getY() + 4, 12, 12)
//                .build();
        this.widgets.add(this.closeButton);
        this.taskDisplays = new ArrayList<>();
        refreshTasks();
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, 256, 256);
        guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 10, this.getY() + 10, 0x72e567, false);

        for (AbstractWidget widget : this.widgets) {
            if (widget instanceof Checkbox) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
                guiGraphics.pose().translate(this.getX() + 20, this.getY() + 86, 0);
                widget.render(guiGraphics, mouseX, mouseY, v);
                guiGraphics.pose().popPose();
            } else {
                widget.render(guiGraphics, mouseX, mouseY, v);
            }
        }
        for (int i = 0; i < this.taskDisplays.size(); i++) {
            var widget = this.taskDisplays.get(i);
            widget.render(guiGraphics,  mouseX, mouseY, v);
        }
        //this.widgets.forEach(abstractWidget -> abstractWidget.render(guiGraphics, mouseX, mouseY, v));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void refreshTasks() {
        this.taskDisplays = new ArrayList<>();
        var tasks = TaskSyncPacket.CLIENT_TASK_STORAGE.getOrDefault(this.replicationTerminalScreen.getMenu().getNetwork(), new HashMap<>());
        IReplicationTask[] tastValues = tasks.values().toArray(new IReplicationTask[0]);
        for (int i = 0; i < tastValues.length; i++) {
            this.taskDisplays.add(new TaskDisplay(tastValues[i], this.getX() + 8 + (i % 3) * 81, this.getY() + 25 + (i / 3) * 25));

        }
    }

    public static final class TaskDisplay {
        private final IReplicationTask task;
        private final ReplicationTerminalTexturedButton cancelButton;
        private final int x;
        private final int y;

        public TaskDisplay(IReplicationTask task, int x, int y) {
            this.task = task;
            this.x = x;
            this.y = y;
            this.cancelButton = new ReplicationTerminalTexturedButton(x + 72, y + 1, 5, 5, Component.empty(),
                    Component.translatable("tooltip.replication.terminal.cancel_task").getString(), 251, 59,246, 59, button -> {});
        }

        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
            guiGraphics.renderItem(task.getReplicatingStack(), x + 3, y + 3);
            this.cancelButton.render(guiGraphics, mouseX, mouseY, v);
            guiGraphics.pose().pushPose();
            var scale = 0.6f;
            guiGraphics.pose().scale(scale, scale, scale);
            var textY = 3;
            var display = LangUtil.getString("tooltip.replication.terminal.amount") + NumberUtils.getFormatedBigNumber(task.getCurrentAmount()) + "/" + NumberUtils.getFormatedBigNumber(task.getTotalAmount());
            guiGraphics.drawString(Minecraft.getInstance().font, display, (x + 16 + 12) / scale, (y + textY) / scale, 0x72e567, false);
            display =  LangUtil.getString("tooltip.replication.terminal.workers") + NumberUtils.getFormatedBigNumber(task.getReplicatorsOnTask().size());
            guiGraphics.drawString(Minecraft.getInstance().font, display, (x + 16 + 12) / scale, (y + textY + 6) / scale, 0x72e567, false);
            display =  LangUtil.getString("tooltip.replication.terminal.mode") +  LangUtil.getString("tooltip.replication.terminal." + task.getMode().name().toLowerCase());
            guiGraphics.drawString(Minecraft.getInstance().font, display, (x + 16 + 12) / scale, (y + textY + 6 * 2) / scale, 0x72e567, false);
            guiGraphics.pose().popPose();
            if (mouseX > x + 2 && mouseX < x + 19 && mouseY > y + 2 && mouseY < y + 19) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, task.getReplicatingStack(), (int) mouseX, (int) mouseY);
            }
        }

        public IReplicationTask task() {
            return task;
        }


    }
}
