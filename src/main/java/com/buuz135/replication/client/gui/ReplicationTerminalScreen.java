package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.pattern.MatterPattern;
import com.buuz135.replication.client.gui.addons.MatterPatternButton;
import com.buuz135.replication.client.gui.addons.TerminalMatterValueDisplay;
import com.buuz135.replication.client.gui.button.ReplicationTerminalConfigButton;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedButton;
import com.buuz135.replication.container.ReplicationTerminalContainer;
import com.buuz135.replication.packet.MatterFluidSyncPacket;
import com.buuz135.replication.packet.PatternSyncStoragePacket;
import com.buuz135.replication.packet.TaskCreatePacket;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;


import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ReplicationTerminalScreen extends AbstractContainerScreen<ReplicationTerminalContainer> {

    public static ResourceLocation TEXTURE = new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal.png");

    private EditBox searchBox;
    private PatternMenu patternMenu;
    private float scrollOffs;
    private boolean scrolling;
    private List<TerminalMatterValueDisplay> terminalMatterValueDisplays;
    private ReplicationRequestWidget replicationRequestWidget;
    private ReplicationTaskWidget replicationTaskWidget;
    private ReplicationTerminalConfigButton sortingType;
    private ReplicationTerminalConfigButton sortingDirection;
    private ReplicationTerminalTexturedButton craftingButton;

    public ReplicationTerminalScreen(ReplicationTerminalContainer container, Inventory inventory, Component component) {
        super(container, inventory, component);
        this.imageWidth = 195;
        this.imageHeight = 256;
        this.inventoryLabelY = 120;
        this.titleLabelY = -10;
    }

    @Override
    protected void init() {
        super.init();
        this.searchBox = new EditBox(this.font, this.leftPos + 85, this.topPos + 12, 79, 13, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0x72e567);
        this.addWidget(this.searchBox);
        this.addWidget(this.craftingButton = new ReplicationTerminalTexturedButton(this.leftPos + 176, this.topPos + 14, 9, 9, Component.empty(),
                Component.translatable("replication.crafting_tasks").getString(), 247, 41, button -> {enableTask(new ReplicationTaskWidget((this.width - 256) / 2,(this.height - 256) / 2, 256,256, Component.translatable("replication.crafting_tasks"), this));}));

        this.addRenderableWidget(this.sortingType = new ReplicationTerminalConfigButton(this.leftPos + 10, this.topPos + 10, 9, 9, new TileEntityLocatorInstance(menu.getPosition()), ReplicationTerminalConfigButton.Type.SORTING_TYPE, this.menu.getSortingType()) {
            @Override
            public void onPress() {
                super.onPress();
                scrollOffs = 0;
                patternMenu.scrollTo(0);
            }

            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
                if (shouldBaseGUIRender()) super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        });

        this.addRenderableWidget(this.sortingDirection = new ReplicationTerminalConfigButton(this.leftPos + 20, this.topPos + 10, 9, 9, new TileEntityLocatorInstance(menu.getPosition()), ReplicationTerminalConfigButton.Type.SORTING_DIRECTION, this.menu.getSortingValue()) {
            @Override
            public void onPress() {
                super.onPress();
                scrollOffs = 0;
                patternMenu.scrollTo(0);
            }

            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
                if (shouldBaseGUIRender()) super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        });

        this.patternMenu = new PatternMenu();
        this.terminalMatterValueDisplays = new ArrayList<>();

        this.refreshPatterns();
        this.refreshTanks();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float p_281886_) {
        super.render(guiGraphics, mouseX, mouseY, p_281886_);
        if (shouldBaseGUIRender()){
            for (int matterButtonIndex = 0; matterButtonIndex < this.patternMenu.visibleButtons.size(); matterButtonIndex++) {
                var patternButton = this.patternMenu.visibleButtons.get(matterButtonIndex);
                patternButton.render(guiGraphics, this.leftPos + (matterButtonIndex % 9) * 18 + 11, this.topPos + (matterButtonIndex / 9) * 18 + 28, mouseX, mouseY);
            }
            for (int displayIndex = 0; displayIndex < this.terminalMatterValueDisplays.size(); displayIndex++) {
                var matterTankDisplay = this.terminalMatterValueDisplays.get(displayIndex);
                // matterTankDisplay.render(guiGraphics, this.leftPos + this.getXSize() + (displayIndex % 3) * 18 , this.topPos + (displayIndex / 3) * 18 + 7, mouseX, mouseY);
                matterTankDisplay.render(guiGraphics, this.leftPos + this.getXSize(), this.topPos + (displayIndex) * 20 + 26, mouseX, mouseY);
            }
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);

        int x = this.leftPos;
        int y = this.topPos;


        if (this.replicationRequestWidget != null) {
            this.replicationRequestWidget.renderWidget(guiGraphics, mouseX, mouseY, v);
        } else if (this.replicationTaskWidget != null){
            this.replicationTaskWidget.renderWidget(guiGraphics, mouseX, mouseY, v);
        }else {
            guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
            // TODO: Work on the Extras
            guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), x + this.imageWidth, y + 19, 0, 0, 27, 174);
            this.searchBox.render(guiGraphics, mouseX, mouseY, v);
            this.craftingButton.render(guiGraphics, mouseX, mouseY, v);

            int j = this.leftPos + 175;
            int k = this.topPos + 28;
            int i = k + 88;

            guiGraphics.blit(TEXTURE, j, k + (int) ((float) (i - k - 5) * this.scrollOffs), 245, 0, 11, 5);


        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.searchBox.tick();
        var shouldSort = false;
        for (MatterPatternButton matterPatternButton : this.patternMenu.matterPatternButtonList) {
            if (matterPatternButton.isShouldDisplayAnimation() && (matterPatternButton.createdWhen() + 20) < Minecraft.getInstance().level.getGameTime()) {
                matterPatternButton.setShouldDisplayAnimation(false);
                shouldSort = true;
            }
        }
        if (shouldSort) {
            scrollOffs = 0;
            patternMenu.scrollTo(0);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (shouldBaseGUIRender()) {
            pGuiGraphics.drawString(this.font, this.title.copy().setStyle(Style.EMPTY.withColor(Mth.color(114/255f, 229/255f, 103/255f))), this.titleLabelX + 40, this.titleLabelY, 0x000000, true);
            pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX + 2, this.inventoryLabelY, 0x72e567, false);
        }
    }

    private boolean shouldBaseGUIRender(){
        return this.replicationRequestWidget == null && replicationTaskWidget == null;
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
            if (shouldBaseGUIRender()) {
                if (this.insideScrollbar(pMouseX, pMouseY)) {
                    this.scrolling = this.patternMenu.canScroll();
                    int i = this.topPos + 18;
                    int j = i + 80;
                    this.scrollOffs = ((float) pMouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
                    this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
                    this.patternMenu.scrollTo(this.scrollOffs);
                    return true;
                }
                if (this.patternMenu.mouseClicked(pMouseX, pMouseY, pButton)) {
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
        if (this.replicationRequestWidget == null) {
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
        if (this.replicationRequestWidget == null) {
            if (this.searchBox.keyPressed(p_98547_, p_98548_, p_98549_)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.patternMenu.scrollTo(0f);
                }
                return true;
            } else {
                return this.searchBox.isFocused() && this.searchBox.isVisible() && p_98547_ != 256 || super.keyPressed(p_98547_, p_98548_, p_98549_);
            }
        } else {
            if (this.replicationRequestWidget.getAmountBox().keyPressed(p_98547_, p_98548_, p_98549_)) {
                return true;
            } else {
                return this.replicationRequestWidget.getAmountBox().isFocused() && this.replicationRequestWidget.getAmountBox().isVisible() && p_98547_ != 256 || super.keyPressed(p_98547_, p_98548_, p_98549_);
            }
        }

    }

    public void refreshPatterns() {
        this.patternMenu.matterPatternButtonList = new ArrayList<>();
        List<ItemStack> existing = new ArrayList<>();
        var temp = PatternSyncStoragePacket.CLIENT_PATTERN_STORAGE.getOrDefault(this.menu.getNetwork(), new HashMap<>())
                .values().stream().flatMap(Collection::stream).map(stack -> new MatterPatternButton(new MatterPattern(stack, 1), -1, Minecraft.getInstance().level.getGameTime(), this.menu.getNetwork())).collect(Collectors.toList());
        for (MatterPatternButton matterPatternButton : temp) {
            if (existing.stream().noneMatch(o -> ItemStack.isSameItemSameTags(o, matterPatternButton.pattern().getStack()))) {
                this.patternMenu.matterPatternButtonList.add(matterPatternButton);
                existing.add(matterPatternButton.pattern().getStack());
            }
        }
        this.patternMenu.scrollTo(this.scrollOffs);
    }

    public void refreshTanks() {
        this.terminalMatterValueDisplays = new ArrayList<>();
        var entries = MatterFluidSyncPacket.CLIENT_MATTER_STORAGE.get(this.menu.getNetwork());
        for (IMatterType value : MatterType.values()) {
            if (value.equals(MatterType.EMPTY)) continue;
            this.terminalMatterValueDisplays.add(new TerminalMatterValueDisplay(value, entries.getOrDefault(value, 0L)));
        }
        for (MatterPatternButton matterPatternButton : this.patternMenu.matterPatternButtonList) {
            matterPatternButton.recalculateAmount(this.menu.getNetwork());
        }
    }

    public void enableRequest(ReplicationRequestWidget widget) {
        this.replicationRequestWidget = widget;
        this.menu.setEnabled(false);
        this.replicationRequestWidget.getWidgets().forEach(this::addWidget);
    }

    public void disableRequest() {
        this.replicationRequestWidget.getWidgets().forEach(abstractWidget -> this.children().remove(abstractWidget));
        this.replicationRequestWidget = null;
        this.menu.setEnabled(true);
    }

    public void enableTask(ReplicationTaskWidget widget) {
        this.replicationTaskWidget = widget;
        this.menu.setEnabled(false);
        this.replicationTaskWidget.getWidgets().forEach(this::addWidget);
    }

    public void disableTask() {
        this.replicationTaskWidget.getWidgets().forEach(abstractWidget -> this.children().remove(abstractWidget));
        this.replicationTaskWidget = null;
        this.menu.setEnabled(true);
    }

    public void createTask(MatterPattern pattern, int i, boolean parallelMode) {
        Replication.NETWORK.get().sendToServer(new TaskCreatePacket(this.menu.getNetwork(), i, pattern.getStack(), parallelMode, this.menu.getPosition()));
        disableRequest();
    }


    public class PatternMenu {
        public List<MatterPatternButton> matterPatternButtonList = new ArrayList<>();
        public List<MatterPatternButton> visibleButtons = new ArrayList<>();

        public PatternMenu() {
            this.scrollTo(0.0F);
        }


        protected int calculateRowCount() {
            return Mth.positiveCeilDiv(getFilteredPatterns().size(), 9) - 5;
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
            Comparator<MatterPatternButton> comparator = Comparator.comparingInt(MatterPatternButton::cachedAmount);
            if (ReplicationTerminalScreen.this.sortingType.getState() == 1) {
                comparator = Comparator.comparing(matterPatternButton -> matterPatternButton.pattern().getStack().getDisplayName().getString().toLowerCase());
                comparator = comparator.reversed();
            }
            if (ReplicationTerminalScreen.this.sortingDirection.getState() == 1) {
                comparator = comparator.reversed();
            }
            filtered.sort(comparator);
            for (int j = 0; j < 5; ++j) {
                for (int k = 0; k < 9; ++k) {
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

        private List<MatterPatternButton> getFilteredPatterns() {
            var textValue = ReplicationTerminalScreen.this.searchBox.getValue().toLowerCase();
            if (textValue.isBlank()) return this.matterPatternButtonList;
            return this.matterPatternButtonList.stream().filter(matterPatternButton -> matterPatternButton.pattern().getStack().getDisplayName().getString().toLowerCase().contains(textValue)).collect(Collectors.toList());
        }

        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            for (int matterButtonIndex = 0; matterButtonIndex < this.visibleButtons.size(); matterButtonIndex++) {
                if (pMouseX > ReplicationTerminalScreen.this.leftPos + (matterButtonIndex % 9) * 18 + 11 && pMouseX < ReplicationTerminalScreen.this.leftPos + (matterButtonIndex % 9) * 18 + 11 + 18
                        && pMouseY > ReplicationTerminalScreen.this.topPos + (matterButtonIndex / 9) * 18 + 28 && pMouseY < ReplicationTerminalScreen.this.topPos + (matterButtonIndex / 9) * 18 + 28 + 18) {
                    var patternButton = this.visibleButtons.get(matterButtonIndex);
                    if (patternButton.cachedAmount() == 0) return false;
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
