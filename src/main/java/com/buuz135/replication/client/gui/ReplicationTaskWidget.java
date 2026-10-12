package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.task.IReplicationTask;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedButton;
import com.buuz135.replication.packet.TaskCancelPacket;
import com.buuz135.replication.packet.TaskSyncPacket;
import com.buuz135.replication.util.NumberUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ReplicationTaskWidget extends AbstractWidget {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/task_window.png");
    private static final Identifier EXTRAS = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png");
    private final ReplicationTerminalScreen screen;
    private final List<AbstractWidget> widgets = new ArrayList<>();
    private List<IReplicationTask> visibleTasks = new ArrayList<>();
    private float scrollOffs;
    private boolean scrolling;

    public ReplicationTaskWidget(int x, int y, int width, int height, Component message, ReplicationTerminalScreen screen) {
        super(x, y, width, height, message);
        this.screen = screen;
        widgets.add(new ReplicationTerminalTexturedButton(x + 239, y + 8, 9, 9, Component.empty(), EXTRAS, "tooltip.replication.close", 247, 50, 238, 50, button -> screen.disableTask()));
        refreshTasks();
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), 0, 0, 256, 256, 256, 256);
        graphics.text(Minecraft.getInstance().font, getMessage(), getX() + 10, getY() + 10, 0xFF72E567, false);
        graphics.blit(RenderPipelines.GUI_TEXTURED, EXTRAS, getX() + width, getY() + 23, 27, 0, 20, 108, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, EXTRAS, getX() + width + 1, getY() + 33 + (int) (83 * scrollOffs), 245, 0, 11, 5, 256, 256);
        for (int index = 0; index < visibleTasks.size(); index++) {
            renderTask(graphics, visibleTasks.get(index), getX() + 8 + index % 3 * 81, getY() + 25 + index / 3 * 25, mouseX, mouseY);
        }
    }

    private void renderTask(GuiGraphicsExtractor graphics, IReplicationTask task, int x, int y, int mouseX, int mouseY) {
        graphics.fill(x, y, x + 78, y + 23, 0xB0202020);
        graphics.item(task.getReplicatingStack(), x + 2, y + 3);
        float scale = 0.6F;
        int textX = (int) ((x + 29) / scale);
        int textY = (int) ((y + 3) / scale);
        String amount = task.isInfinteMode()
                ? Component.translatable("replication.infinite").getString()
                : Component.translatable("tooltip.replication.terminal.amount").getString() + NumberUtils.getFormatedBigNumber(task.getCurrentAmount()) + "/" + NumberUtils.getFormatedBigNumber(task.getTotalAmount());
        String workers = Component.translatable("tooltip.replication.terminal.workers").getString() + NumberUtils.getFormatedBigNumber(task.getReplicatorsOnTask().size());
        String mode = Component.translatable("tooltip.replication.terminal.mode").getString() + Component.translatable("tooltip.replication.terminal." + task.getMode().name().toLowerCase()).getString();
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.text(Minecraft.getInstance().font, amount, textX, textY, 0xFF72E567, false);
        graphics.text(Minecraft.getInstance().font, workers, textX, textY + 10, 0xFF72E567, false);
        graphics.text(Minecraft.getInstance().font, mode, textX, textY + 20, 0xFF72E567, false);
        graphics.pose().popMatrix();
        graphics.fill(x + 72, y + 1, x + 78, y + 7, 0xFF9B3030);
        graphics.centeredText(Minecraft.getInstance().font, "×", x + 75, y, 0xFFFFFFFF);
        if (mouseX > x + 2 && mouseX < x + 19 && mouseY > y + 2 && mouseY < y + 20) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, task.getReplicatingStack(), mouseX, mouseY);
        }
        if (mouseX >= x + 72 && mouseX < x + 78 && mouseY >= y + 1 && mouseY < y + 7) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, Component.translatable("tooltip.replication.terminal.cancel_task"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) {
            return false;
        }
        if (insideScrollbar(event.x(), event.y())) {
            scrolling = canScroll();
            updateScroll(event.y());
            return true;
        }
        if (event.hasShiftDown()) {
            for (int index = 0; index < visibleTasks.size(); index++) {
                int x = getX() + 8 + index % 3 * 81;
                int y = getY() + 25 + index / 3 * 25;
                if (event.x() >= x + 72 && event.x() < x + 78 && event.y() >= y + 1 && event.y() < y + 7) {
                    IReplicationTask task = visibleTasks.get(index);
                    Replication.NETWORK.sendToServer(new TaskCancelPacket(task.getUuid().toString(), screen.getMenu().getNetwork()));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (scrolling) {
            updateScroll(event.y());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        scrolling = false;
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!canScroll()) {
            return false;
        }
        int rows = Math.max(1, Mth.positiveCeilDiv(getTasks().size(), 3) - 9);
        scrollOffs = Mth.clamp(scrollOffs - (float) (scrollY / rows), 0.0F, 1.0F);
        updateVisibleTasks();
        return true;
    }

    private boolean insideScrollbar(double mouseX, double mouseY) {
        return mouseX >= getX() + width && mouseX < getX() + width + 9 && mouseY >= getY() + 23 && mouseY < getY() + 111;
    }

    private void updateScroll(double mouseY) {
        scrollOffs = Mth.clamp(((float) mouseY - (getY() + 24) - 7.5F) / 71.0F, 0.0F, 1.0F);
        updateVisibleTasks();
    }

    private boolean canScroll() {
        return getTasks().size() > 27;
    }

    private List<IReplicationTask> getTasks() {
        return new ArrayList<>(TaskSyncPacket.CLIENT_TASK_STORAGE.getOrDefault(screen.getMenu().getNetwork(), new java.util.HashMap<>()).values());
    }

    private void updateVisibleTasks() {
        List<IReplicationTask> tasks = getTasks();
        int rows = Math.max(0, Mth.positiveCeilDiv(tasks.size(), 3) - 9);
        int row = Math.max(0, Math.round(scrollOffs * rows));
        visibleTasks = tasks.stream().skip((long) row * 3).limit(27).toList();
    }

    public void refreshTasks() {
        updateVisibleTasks();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
