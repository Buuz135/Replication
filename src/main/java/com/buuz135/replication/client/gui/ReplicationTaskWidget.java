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
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReplicationTaskWidget extends AbstractWidget implements Renderable {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Replication.MOD_ID, "textures/gui/task_window.png");

    private final ReplicationTerminalScreen replicationTerminalScreen;
    private final ReplicationTerminalTexturedButton closeButton;
    private final List<AbstractWidget> widgets;
    private float scrollOffs;
    private boolean scrolling;
    private final int scrollBarX;
    private final int scrollBarY;
    private final int scrollBarWidth;
    private final int scrollBarHeight;
    private final CraftingMenu craftingMenu;

    public ReplicationTaskWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, ReplicationTerminalScreen replicationTerminalContainer) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.replicationTerminalScreen = replicationTerminalContainer;
        this.widgets = new ArrayList<>();
        this.closeButton = new ReplicationTerminalTexturedButton(this.getX() + 239, this.getY() + 8, 9, 9, Component.empty(),
                Component.translatable("replication.close").getString(), 247, 50, 238, 50, button -> this.replicationTerminalScreen.disableTask());
//                new Button.Builder(Component.literal("x"), button -> this.replicationTerminalScreen.disableTask())
//                .bounds(this.getX() + 256 - 16, this.getY() + 4, 12, 12)
//                .build();
        this.widgets.add(this.closeButton);
        this.scrollOffs = 0;
        this.scrolling = false;
        this.craftingMenu = new CraftingMenu();
        this.scrollBarX = this.getX() + this.width + 8;
        this.scrollBarY = this.getY() + 20;
        this.scrollBarHeight = 88;
        this.scrollBarWidth = 9;
        refreshTasks();
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, 256, 256);
        guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 10, this.getY() + 10, 0x72e567, false);
        guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, this.scrollBarX, this.scrollBarY, 176, 27, 9, 90);
        int j = this.scrollBarX;
        int k = this.scrollBarY;
        int y = k + this.scrollBarHeight;
        guiGraphics.blit(ReplicationTerminalScreen.TEXTURE, j - 1, k + (int) ((float) (y - k - 5) * this.scrollOffs) + 1, 245, 0, 11, 5);

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
        for (int i = 0; i < this.craftingMenu.visibleButtons.size(); i++) {
            var widget = this.craftingMenu.visibleButtons.get(i);
            widget.render(guiGraphics, this.getX() + 8 + (i % 3) * 81, this.getY() + 25 + (i / 3) * 25, mouseX, mouseY, v);
        }
        //this.widgets.forEach(abstractWidget -> abstractWidget.render(guiGraphics, mouseX, mouseY, v));
    }

    protected boolean insideScrollbar(double p_98524_, double p_98525_) {
        int k = this.scrollBarX;
        int l = this.scrollBarY;
        int i1 = k + this.scrollBarWidth;
        int j1 = l + this.scrollBarHeight;
        return p_98524_ >= (double) k && p_98525_ >= (double) l && p_98524_ < (double) i1 && p_98525_ < (double) j1;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling) {
            int i = this.scrollBarY + 1;
            int j = i + this.scrollBarHeight - 2;
            this.scrollOffs = ((float) pMouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.craftingMenu.scrollTo(this.scrollOffs);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double p_98527_, double p_98528_, double p_98529_) {
        if (!this.canScroll()) {
            return false;
        } else {
            this.scrollOffs = this.craftingMenu.subtractInputFromScroll(this.scrollOffs, p_98529_);
            this.craftingMenu.scrollTo(this.scrollOffs);
            return true;
        }
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            this.scrolling = false;
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private boolean canScroll() {
        return this.craftingMenu.canScroll();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            if (this.insideScrollbar(pMouseX, pMouseY)) {
                this.scrolling = this.canScroll();
                int i = this.scrollBarY + 1;
                int j = i + this.scrollBarHeight - 2;
                this.scrollOffs = ((float) pMouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
                this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
                this.craftingMenu.scrollTo(this.scrollOffs);
                return true;
            }
            if (this.closeButton.isHovered()) {
                this.closeButton.mouseClicked(pMouseX, pMouseY, pButton);
            }
            if (this.craftingMenu.mouseClicked(pMouseX, pMouseY, pButton)) {
                return true;
            }

        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void refreshTasks() {
        var originalSize = this.craftingMenu.taskDisplayList.size();
        this.craftingMenu.taskDisplayList.clear();
        var tasks = TaskSyncPacket.CLIENT_TASK_STORAGE.getOrDefault(this.replicationTerminalScreen.getMenu().getNetwork(), new HashMap<>());
        IReplicationTask[] tastValues = tasks.values().toArray(new IReplicationTask[0]);
        for (int i = 0; i < tastValues.length; i++) {
            this.craftingMenu.taskDisplayList.add(new TaskDisplay(tastValues[i]));
        }
        if (this.craftingMenu.taskDisplayList.size() != originalSize){
            this.craftingMenu.scrollTo(this.scrollOffs);
        }
    }

    public static final class TaskDisplay {
        private final IReplicationTask task;
        private final ReplicationTerminalTexturedButton cancelButton;


        public TaskDisplay(IReplicationTask task) {
            this.task = task;

            this.cancelButton = new ReplicationTerminalTexturedButton(72, 1, 5, 5, Component.empty(),
                    Component.translatable("tooltip.replication.terminal.cancel_task").getString(), 251, 59, 246, 59, button -> {
            });
        }

        public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float v) {
            guiGraphics.renderItem(task.getReplicatingStack(), x + 3, y + 3);
            this.cancelButton.setX(x + 72);
            this.cancelButton.setY(y + 1);
            this.cancelButton.render(guiGraphics, mouseX, mouseY, v);
            guiGraphics.pose().pushPose();
            var scale = 0.6f;
            guiGraphics.pose().scale(scale, scale, scale);
            var textY = 3;
            var display = LangUtil.getString("tooltip.replication.terminal.amount") + NumberUtils.getFormatedBigNumber(task.getCurrentAmount()) + "/" + NumberUtils.getFormatedBigNumber(task.getTotalAmount());
            guiGraphics.drawString(Minecraft.getInstance().font, display, (x + 16 + 12) / scale, (y + textY) / scale, 0x72e567, false);
            display = LangUtil.getString("tooltip.replication.terminal.workers") + NumberUtils.getFormatedBigNumber(task.getReplicatorsOnTask().size());
            guiGraphics.drawString(Minecraft.getInstance().font, display, (x + 16 + 12) / scale, (y + textY + 6) / scale, 0x72e567, false);
            display = LangUtil.getString("tooltip.replication.terminal.mode") + LangUtil.getString("tooltip.replication.terminal." + task.getMode().name().toLowerCase());
            guiGraphics.drawString(Minecraft.getInstance().font, display, (x + 16 + 12) / scale, (y + textY + 6 * 2) / scale, 0x72e567, false);
            guiGraphics.pose().popPose();
            if (mouseX > x + 2 && mouseX < x + 19 && mouseY > y + 2 && mouseY < y + 19) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, task.getReplicatingStack(), mouseX, mouseY);
            }
        }

        public IReplicationTask task() {
            return task;
        }

    }

    public class CraftingMenu {
        public List<TaskDisplay> taskDisplayList = new ArrayList<>();
        public List<TaskDisplay> visibleButtons = new ArrayList<>();

        public CraftingMenu() {
            this.scrollTo(0.0F);
        }


        protected int calculateRowCount() {
            return Mth.positiveCeilDiv(getFilteredPatterns().size(), 3) - 9;
        }

        protected int getRowIndexForScroll(float p_259664_) {
            return Math.max((int) ((double) (p_259664_ * (float) this.calculateRowCount()) + 0.5), 0);
        }

        protected float getScrollForRowIndex(int p_259315_) {
            return Mth.clamp((float) p_259315_ / (float) this.calculateRowCount(), 0.0F, 1.0F);
        }

        protected float subtractInputFromScroll(float p_259841_, double p_260358_) {
            return Mth.clamp(p_259841_ - (float) (p_260358_ / (double) this.calculateRowCount()), 0.0F, 1.0F);
        }

        public void scrollTo(float p_98643_) {
            int i = this.getRowIndexForScroll(p_98643_);
            this.visibleButtons = new ArrayList<>();
            var filtered = getFilteredPatterns();
            for (int j = 0; j < 9; ++j) {
                for (int k = 0; k < 3; ++k) {
                    int l = k + (j + i) * 3;
                    if (l >= 0 && l < filtered.size()) {
                        this.visibleButtons.add(filtered.get(l));
                    } else {
                        //CreativeModeInventoryScreen.CONTAINER.setItem(k + j * 9, ItemStack.EMPTY);
                    }
                }
            }

        }

        public boolean canScroll() {
            return getFilteredPatterns().size() > 27;
        }

        private List<TaskDisplay> getFilteredPatterns() {
            return this.taskDisplayList;
        }

        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            for (int matterButtonIndex = 0; matterButtonIndex < this.visibleButtons.size(); matterButtonIndex++) {
                if (pMouseX > ReplicationTaskWidget.this.getX() + (matterButtonIndex % 3) * 18 + 11 && pMouseX < ReplicationTaskWidget.this.getX() + (matterButtonIndex % 3) * 18 + 11 + 18
                        && pMouseY > ReplicationTaskWidget.this.getY() + (matterButtonIndex / 3) * 18 + 28 && pMouseY < ReplicationTaskWidget.this.getY() + (matterButtonIndex / 3) * 18 + 28 + 18) {
                    var patternButton = this.visibleButtons.get(matterButtonIndex);

                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
            return false;
        }
    }
}
