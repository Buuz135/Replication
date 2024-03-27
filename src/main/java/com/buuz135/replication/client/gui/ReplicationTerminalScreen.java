package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.pattern.MatterPattern;
import com.buuz135.replication.client.gui.addons.MatterPatternButton;
import com.buuz135.replication.client.gui.addons.MatterTankDisplay;
import com.buuz135.replication.container.ReplicationTerminalContainer;
import com.buuz135.replication.packet.MatterFluidSyncPacket;
import com.buuz135.replication.packet.PatternSyncStoragePacket;
import com.buuz135.replication.packet.TaskCreatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.*;

public class ReplicationTerminalScreen extends AbstractContainerScreen<ReplicationTerminalContainer> {

    private static ResourceLocation TEXTURE = new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal.png");

    private EditBox searchBox;
    private PatternMenu patternMenu;
    private float scrollOffs;
    private boolean scrolling;
    private List<MatterTankDisplay> matterTankDisplays;
    private ReplicationRequestWidget replicationRequestWidget;

    public ReplicationTerminalScreen(ReplicationTerminalContainer container, Inventory inventory, Component component) {
        super(container, inventory, component);
        this.imageWidth = 220;
        this.imageHeight = 256;
        this.inventoryLabelY = 124;
    }

    @Override
    protected void init() {
        super.init();
        this.searchBox = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, 9, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.addWidget(this.searchBox);

        this.patternMenu = new PatternMenu();
        this.matterTankDisplays = new ArrayList<>();

        this.refreshPatterns();
        this.refreshTanks();
    }

    @Override
    public void render(GuiGraphics p_283479_, int p_283661_, int p_281248_, float p_281886_) {
        super.render(p_283479_, p_283661_, p_281248_, p_281886_);

        this.renderTooltip(p_283479_, p_283661_, p_281248_);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);

        int x = this.leftPos;
        int y = this.topPos;


        if (this.replicationRequestWidget != null){
            this.replicationRequestWidget.renderWidget(guiGraphics, mouseX, mouseY, v);
        } else {
            guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
            this.searchBox.render(guiGraphics, mouseX, mouseY, v);

            int j = this.leftPos + 175;
            int k = this.topPos + 18;
            int i = k + 90;

            guiGraphics.blit(TEXTURE, j, k + (int)((float)(i - k - 17) * this.scrollOffs), 232 + (this.patternMenu.canScroll() ? 0 : 12), 0, 12, 15);

            for (int matterButtonIndex = 0; matterButtonIndex < this.patternMenu.visibleButtons.size(); matterButtonIndex++) {
                var patternButton = this.patternMenu.visibleButtons.get(matterButtonIndex);
                patternButton.render(guiGraphics, this.leftPos + (matterButtonIndex % 9)*18 + 9, this.topPos + (matterButtonIndex / 9)*18 + 18, mouseX, mouseY);
            }
            for (int displayIndex = 0; displayIndex < this.matterTankDisplays.size(); displayIndex++) {
                var matterTankDisplay = this.matterTankDisplays.get(displayIndex);
                matterTankDisplay.render(guiGraphics, this.leftPos + this.getXSize() -23, this.topPos + displayIndex*20 + 10, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.searchBox.tick();
        for (MatterPatternButton matterPatternButton : this.patternMenu.matterPatternButtonList) {
            if (matterPatternButton.cachedAmount() == -1 && (matterPatternButton.createdWhen() + 1*20) < Minecraft.getInstance().level.getGameTime()){
                matterPatternButton.recalculateAmount(this.menu.getNetwork());
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (this.replicationRequestWidget == null) super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
    }

    protected boolean insideScrollbar(double p_98524_, double p_98525_) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 90;
        return p_98524_ >= (double) k && p_98525_ >= (double) l && p_98524_ < (double) i1 && p_98525_ < (double) j1;
    }

    @Override
    public boolean mouseDragged(double p_98535_, double p_98536_, int p_98537_, double p_98538_, double p_98539_) {
        if (this.scrolling) {
            int i = this.topPos + 18;
            int j = i + 80;
            this.scrollOffs = ((float) p_98536_ - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.patternMenu.scrollTo(this.scrollOffs);
            return true;
        } else {
            return super.mouseDragged(p_98535_, p_98536_, p_98537_, p_98538_, p_98539_);
        }
    }

    @Override
    public boolean mouseScrolled(double p_98527_, double p_98528_, double p_98529_) {
        if (!this.patternMenu.canScroll()) {
            return false;
        } else {
            this.scrollOffs = this.patternMenu.subtractInputFromScroll(this.scrollOffs, p_98529_);
            this.patternMenu.scrollTo(this.scrollOffs);
            return true;
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            if (this.replicationRequestWidget == null){
                if (this.insideScrollbar(pMouseX, pMouseY)) {
                    this.scrolling = this.patternMenu.canScroll();
                    int i = this.topPos + 18;
                    int j = i + 80;
                    this.scrollOffs = ((float) pMouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
                    this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
                    this.patternMenu.scrollTo(this.scrollOffs);
                    return true;
                }
                if (this.patternMenu.mouseClicked(pMouseX, pMouseY, pButton)){
                    return true;
                }
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double p_98622_, double p_98623_, int p_98624_) {
        if (p_98624_ == 0) {
            this.scrolling = false;
        }
        return super.mouseReleased(p_98622_, p_98623_, p_98624_);
    }
    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        String s = this.searchBox.getValue();
        if (this.replicationRequestWidget == null){
            if (this.searchBox.charTyped(pCodePoint, pModifiers)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.patternMenu.scrollTo(0f);
                }
                return true;
            } else {
                return false;
            }
        } else {
            if (Character.isDigit(pCodePoint) && this.replicationRequestWidget.getAmountBox().charTyped(pCodePoint, pModifiers)) {
                var number = Long.parseLong(this.replicationRequestWidget.getAmountBox().getValue());
                this.replicationRequestWidget.getAmountBox().setValue(Math.min(number, Integer.MAX_VALUE) + "");
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean keyPressed(int p_98547_, int p_98548_, int p_98549_) {
        String s = this.searchBox.getValue();
        if (this.replicationRequestWidget == null){
            if (this.searchBox.keyPressed(p_98547_, p_98548_, p_98549_)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.patternMenu.scrollTo(0f);
                }
                return true;
            } else {
                return this.searchBox.isFocused() && this.searchBox.isVisible() && p_98547_ != 256 ? true : super.keyPressed(p_98547_, p_98548_, p_98549_);
            }
        } else {
            if (this.replicationRequestWidget.getAmountBox().keyPressed(p_98547_, p_98548_, p_98549_)) {
                return true;
            } else {
                return this.replicationRequestWidget.getAmountBox().isFocused() && this.replicationRequestWidget.getAmountBox().isVisible() && p_98547_ != 256 ? true : super.keyPressed(p_98547_, p_98548_, p_98549_);
            }
        }

    }

    public void refreshPatterns() {
        this.patternMenu.matterPatternButtonList = PatternSyncStoragePacket.CLIENT_PATTERN_STORAGE.getOrDefault(this.menu.getNetwork(), new HashMap<>())
                .values().stream().flatMap(Collection::stream).map(stack -> new MatterPatternButton(new MatterPattern(stack, 1), -1, Minecraft.getInstance().level.getGameTime())).toList();
        this.patternMenu.scrollTo(this.scrollOffs);
    }

    public void refreshTanks() {
        this.matterTankDisplays = new ArrayList<>();
        var entries = MatterFluidSyncPacket.CLIENT_MATTER_STORAGE.get(this.menu.getNetwork());
        for (IMatterType value : MatterType.values()) {
            if (value.equals(MatterType.EMPTY)) continue;
            this.matterTankDisplays.add(new MatterTankDisplay(value, entries.getOrDefault(value, 0L)));
        }
        for (MatterPatternButton matterPatternButton : this.patternMenu.matterPatternButtonList) {
            if (matterPatternButton.cachedAmount() != -1){
                matterPatternButton.recalculateAmount(this.menu.getNetwork());
            }
        }
    }

    public void enableRequest(ReplicationRequestWidget widget){
        this.replicationRequestWidget = widget;
        this.menu.setEnabled(false);
        this.replicationRequestWidget.getWidgets().forEach(this::addWidget);
    }

    public void disableRequest(){
        this.replicationRequestWidget.getWidgets().forEach(abstractWidget -> this.children().remove(abstractWidget));
        this.replicationRequestWidget = null;
        this.menu.setEnabled(true);
    }

    public void createTask(MatterPattern pattern, int i, boolean singleMode){
        Replication.NETWORK.get().sendToServer(new TaskCreatePacket(this.menu.getNetwork(), i, pattern.getStack(), singleMode, this.menu.getPosition()));
        disableRequest();
    }


    public class PatternMenu  {
        public List<MatterPatternButton> matterPatternButtonList = new ArrayList<>();
        public List<MatterPatternButton> visibleButtons = new ArrayList<>();

        public PatternMenu() {
            this.scrollTo(0.0F);
        }


        protected int calculateRowCount() {
            return Mth.positiveCeilDiv(getFilteredPatterns().size(), 9) - 5;
        }

        protected int getRowIndexForScroll(float p_259664_) {
            return Math.max((int)((double)(p_259664_ * (float)this.calculateRowCount()) + 0.5), 0);
        }

        protected float getScrollForRowIndex(int p_259315_) {
            return Mth.clamp((float)p_259315_ / (float)this.calculateRowCount(), 0.0F, 1.0F);
        }

        protected float subtractInputFromScroll(float p_259841_, double p_260358_) {
            return Mth.clamp(p_259841_ - (float)(p_260358_ / (double)this.calculateRowCount()), 0.0F, 1.0F);
        }

        public void scrollTo(float p_98643_) {
            int i = this.getRowIndexForScroll(p_98643_);
            this.visibleButtons = new ArrayList<>();
            var filtered = getFilteredPatterns();
            for(int j = 0; j < 5; ++j) {
                for(int k = 0; k < 9; ++k) {
                    int l = k + (j + i) * 9;
                    if (l >= 0 && l < filtered.size()) {
                        this.visibleButtons.add(filtered.get(l));
                    } else {
                        //CreativeModeInventoryScreen.CONTAINER.setItem(k + j * 9, ItemStack.EMPTY);
                    }
                }
            }

        }

        public boolean canScroll() {
            return getFilteredPatterns().size() > 45;
        }

        private List<MatterPatternButton> getFilteredPatterns(){
            var textValue = ReplicationTerminalScreen.this.searchBox.getValue().toLowerCase();
            if (textValue.isBlank()) return this.matterPatternButtonList;
            return this.matterPatternButtonList.stream().filter(matterPatternButton -> matterPatternButton.pattern().getStack().getDisplayName().getString().toLowerCase().contains(textValue)).toList();
        }

        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            for (int matterButtonIndex = 0; matterButtonIndex < this.visibleButtons.size(); matterButtonIndex++) {
                if (pMouseX > ReplicationTerminalScreen.this.leftPos + (matterButtonIndex % 9)*18 + 9 && pMouseX < ReplicationTerminalScreen.this.leftPos + (matterButtonIndex % 9)*18 + 9 + 18
                        && pMouseY > ReplicationTerminalScreen.this.topPos + (matterButtonIndex / 9)*18 + 18 && pMouseY < ReplicationTerminalScreen.this.topPos + (matterButtonIndex / 9)*18 + 18 + 18){
                    var patternButton = this.visibleButtons.get(matterButtonIndex);
                    ReplicationTerminalScreen.this.enableRequest(new ReplicationRequestWidget((ReplicationTerminalScreen.this.width - 177) / 2,
                            (ReplicationTerminalScreen.this.height - 102) / 2, 177, 102, Component.translatable("replication.request_amount"), patternButton, ReplicationTerminalScreen.this));
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
            return false;
        }
    }
}
