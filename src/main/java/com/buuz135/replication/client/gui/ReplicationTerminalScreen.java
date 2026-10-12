package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.pattern.MatterPattern;
import com.buuz135.replication.client.gui.addons.MatterPatternButton;
import com.buuz135.replication.client.gui.addons.TerminalMatterValueDisplay;
import com.buuz135.replication.client.gui.button.ReplicationTerminalConfigButton;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedButton;
import com.buuz135.replication.container.ReplicationTerminalContainer;
import com.buuz135.replication.packet.*;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ReplicationTerminalScreen extends AbstractContainerScreen<ReplicationTerminalContainer> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal.png");
    public static final Identifier BUTTONS = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png");
    private final List<AbstractWidget> baseWidgets = new ArrayList<>();
    private EditBox searchBox;
    private PatternMenu patternMenu;
    private float scrollOffs;
    private boolean scrolling;
    private List<TerminalMatterValueDisplay> terminalMatterValueDisplays = new ArrayList<>();
    private ReplicationRequestWidget replicationRequestWidget;
    private ReplicationTaskWidget replicationTaskWidget;
    private MatterOpediaTaskWidget matterOpediaTaskWidget;
    private ReplicationTerminalConfigButton sortingType;
    private ReplicationTerminalConfigButton sortingDirection;

    public ReplicationTerminalScreen(ReplicationTerminalContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, 195, 256);
        this.inventoryLabelY = 120;
        this.titleLabelY = -10;
        TaskSyncPacket.CLIENT_TASK_STORAGE.remove(container.getNetwork());
    }

    @Override
    protected void init() {
        super.init();
        baseWidgets.clear();
        this.searchBox = addBaseWidget(new EditBox(this.font, leftPos + 85, topPos + 12, 79, 13, Component.translatable("itemGroup.search")));
        searchBox.setMaxLength(50);
        searchBox.setBordered(false);
        searchBox.setTextColor(0xff72e567);
        searchBox.setResponder(value -> {
            scrollOffs = 0;
            if (patternMenu != null) {
                patternMenu.scrollTo(0);
            }
        });
        addBaseWidget(new ReplicationTerminalTexturedButton(leftPos + 176, topPos + 10, 9, 9, Component.empty(), BUTTONS, "replication.crafting_tasks", 247, 41, 238, 41, button -> enableTask(new ReplicationTaskWidget((width - 256) / 2, (height - 256) / 2, 256, 256, Component.translatable("replication.crafting_tasks"), this))));
        addBaseWidget(new ReplicationTerminalTexturedButton(leftPos + 66, topPos + 10, 9, 9, Component.empty(), BUTTONS, "replication.matteropedia", 229, 5, 220, 5, button -> enableMatteropedia(new MatterOpediaTaskWidget(leftPos, topPos, imageWidth, imageHeight, Component.translatable("replication.matteropedia"), this, "earth"))));
        Runnable sortingChanged = () -> {
            scrollOffs = 0;
            patternMenu.scrollTo(0);
        };
        this.sortingType = addBaseWidget(new ReplicationTerminalConfigButton(leftPos + 10, topPos + 10, 9, 9, BUTTONS, new TileEntityLocatorInstance(menu.getPosition()), ReplicationTerminalConfigButton.Type.SORTING_TYPE, menu.getSortingType(), 247, 5, 238, 5, sortingChanged));
        this.sortingDirection = addBaseWidget(new ReplicationTerminalConfigButton(leftPos + 20, topPos + 10, 9, 9, BUTTONS, new TileEntityLocatorInstance(menu.getPosition()), ReplicationTerminalConfigButton.Type.SORTING_DIRECTION, menu.getSortingValue(), 247, 23, 238, 23, sortingChanged));
        this.patternMenu = new PatternMenu();
        refreshPatterns();
        refreshTanks();
    }

    private <T extends AbstractWidget> T addBaseWidget(T widget) {
        baseWidgets.add(widget);
        return addRenderableWidget(widget);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        if (!shouldBaseGUIRender()) {
            return;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        int columns = Math.max(1, Mth.ceil(terminalMatterValueDisplays.size() / 8.0F));
        for (int column = 0; column < columns; column++) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BUTTONS, leftPos + imageWidth + column * 20, topPos + 19, 0, 0, 27, 174, 256, 256);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, BUTTONS, leftPos + 175, topPos + 28 + (int) (83 * scrollOffs), 245, 0, 11, 5, 256, 256);
        for (int index = 0; index < patternMenu.visibleButtons.size(); index++) {
            patternMenu.visibleButtons.get(index).render(graphics, leftPos + index % 9 * 18 + 11, topPos + index / 9 * 18 + 28, mouseX, mouseY);
        }
        for (int index = 0; index < terminalMatterValueDisplays.size(); index++) {
            terminalMatterValueDisplays.get(index).render(graphics, leftPos + imageWidth + 20 * (index / 8), topPos + index % 8 * 20 + 26, mouseX, mouseY);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (shouldBaseGUIRender()) {
            graphics.text(font, title.getString(), titleLabelX + 40, titleLabelY, 0xff72e567, false);
            graphics.text(font, playerInventoryTitle, inventoryLabelX + 2, inventoryLabelY, 0xFF72E567, false);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (Minecraft.getInstance().level == null) {
            return;
        }
        boolean shouldSort = false;
        for (MatterPatternButton button : patternMenu.matterPatternButtonList) {
            if (button.isShouldDisplayAnimation() && button.createdWhen() + 20 < Minecraft.getInstance().level.getGameTime()) {
                button.setShouldDisplayAnimation(false);
                shouldSort = true;
            }
        }
        if (shouldSort) {
            scrollOffs = 0;
            patternMenu.scrollTo(0);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (shouldBaseGUIRender() && event.button() == 0) {
            if (insideScrollbar(event.x(), event.y())) {
                scrolling = patternMenu.canScroll();
                updatePatternScroll(event.y());
                return true;
            }
            if (patternMenu.mouseClicked(event.x(), event.y())) {
                return true;
            }
            for (int index = 0; index < terminalMatterValueDisplays.size(); index++) {
                int x = leftPos + imageWidth + 20 * (index / 8);
                int y = topPos + index % 8 * 20 + 26;
                if (event.x() >= x && event.x() < x + 20 && event.y() >= y && event.y() < y + 20) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
                    enableMatteropedia(new MatterOpediaTaskWidget(leftPos, topPos, imageWidth, imageHeight, Component.translatable("replication.matteropedia"), this, terminalMatterValueDisplays.get(index).type().getName()));
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (scrolling && shouldBaseGUIRender()) {
            updatePatternScroll(event.y());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        scrolling = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (matterOpediaTaskWidget != null && matterOpediaTaskWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        if (replicationTaskWidget != null && replicationTaskWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        if (shouldBaseGUIRender() && patternMenu.canScroll()) {
            scrollOffs = patternMenu.subtractInputFromScroll(scrollOffs, scrollY);
            patternMenu.scrollTo(scrollOffs);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            if (replicationRequestWidget != null) {
                disableRequest();
                return true;
            }
            if (replicationTaskWidget != null) {
                disableTask();
                return true;
            }
            if (matterOpediaTaskWidget != null) {
                disableMatteropedia();
                return true;
            }
        }
        return super.keyPressed(event);
    }

    private boolean insideScrollbar(double mouseX, double mouseY) {
        return mouseX >= leftPos + 176 && mouseX < leftPos + 185 && mouseY >= topPos + 27 && mouseY < topPos + 117;
    }

    private void updatePatternScroll(double mouseY) {
        scrollOffs = Mth.clamp(((float) mouseY - (topPos + 19) - 7.5F) / 65.0F, 0.0F, 1.0F);
        patternMenu.scrollTo(scrollOffs);
    }

    public boolean shouldBaseGUIRender() {
        return replicationRequestWidget == null && replicationTaskWidget == null && matterOpediaTaskWidget == null;
    }

    public void refreshPatterns() {
        if (patternMenu == null || Minecraft.getInstance().level == null) {
            return;
        }
        patternMenu.matterPatternButtonList = new ArrayList<>();
        List<ItemStack> existing = new ArrayList<>();
        List<MatterPatternButton> patterns = PatternSyncStoragePacket.CLIENT_PATTERN_STORAGE.getOrDefault(menu.getNetwork(), new HashMap<>()).values().stream().flatMap(Collection::stream).map(stack -> new MatterPatternButton(new MatterPattern(stack, 1), -1, Minecraft.getInstance().level.getGameTime(), menu.getNetwork())).toList();
        for (MatterPatternButton button : patterns) {
            if (existing.stream().noneMatch(stack -> ItemStack.isSameItemSameComponents(stack, button.pattern().getStack()))) {
                patternMenu.matterPatternButtonList.add(button);
                existing.add(button.pattern().getStack());
            }
        }
        patternMenu.scrollTo(scrollOffs);
    }

    public void refreshTanks() {
        terminalMatterValueDisplays = new ArrayList<>();
        var entries = MatterFluidSyncPacket.CLIENT_MATTER_STORAGE.getOrDefault(menu.getNetwork(), new HashMap<>());
        for (IMatterType value : ReplicationRegistry.MATTER_TYPES_REGISTRY) {
            if (!value.equals(MatterType.EMPTY)) {
                terminalMatterValueDisplays.add(new TerminalMatterValueDisplay(value, entries.getOrDefault(value, 0L)));
            }
        }
        if (patternMenu != null) {
            patternMenu.matterPatternButtonList.forEach(button -> button.recalculateAmount(menu.getNetwork()));
            patternMenu.scrollTo(scrollOffs);
        }
    }

    public void enableRequest(ReplicationRequestWidget widget) {
        replicationRequestWidget = widget;
        setBaseVisible(false);
        menu.setEnabled(ReplicationTerminalContainer.SlotVisualType.NONE);
        addRenderableOnly(widget);
        widget.getWidgets().forEach(this::addRenderableWidget);
        addWidget(widget);
    }

    public void disableRequest() {
        if (replicationRequestWidget == null) {
            return;
        }
        replicationRequestWidget.getWidgets().forEach(this::removeWidget);
        removeWidget(replicationRequestWidget);
        replicationRequestWidget = null;
        restoreBase();
    }

    public void enableTask(ReplicationTaskWidget widget) {
        replicationTaskWidget = widget;
        setBaseVisible(false);
        menu.setEnabled(ReplicationTerminalContainer.SlotVisualType.NONE);
        addRenderableOnly(widget);
        widget.getWidgets().forEach(this::addRenderableWidget);
        addWidget(widget);
        TaskSyncPacket.CLIENT_TASK_STORAGE.remove(menu.getNetwork());
        widget.refreshTasks();
        Replication.NETWORK.sendToServer(new TaskSyncRequestPacket(menu.getNetwork()));
    }

    public void disableTask() {
        if (replicationTaskWidget == null) {
            return;
        }
        replicationTaskWidget.getWidgets().forEach(this::removeWidget);
        removeWidget(replicationTaskWidget);
        replicationTaskWidget = null;
        restoreBase();
    }

    public void enableMatteropedia(MatterOpediaTaskWidget widget) {
        matterOpediaTaskWidget = widget;
        setBaseVisible(false);
        menu.setEnabled(ReplicationTerminalContainer.SlotVisualType.INVENTORY_ONLY);
        addRenderableOnly(widget);
        widget.getWidgets().forEach(this::addRenderableWidget);
        addWidget(widget);
    }

    public void disableMatteropedia() {
        if (matterOpediaTaskWidget == null) {
            return;
        }
        matterOpediaTaskWidget.getWidgets().forEach(this::removeWidget);
        removeWidget(matterOpediaTaskWidget);
        matterOpediaTaskWidget = null;
        restoreBase();
    }

    private void restoreBase() {
        menu.setEnabled(ReplicationTerminalContainer.SlotVisualType.ALL);
        setBaseVisible(true);
    }

    private void setBaseVisible(boolean visible) {
        baseWidgets.forEach(widget -> widget.visible = visible);
    }

    public void createTask(MatterPattern pattern, int amount, boolean parallelMode) {
        Replication.NETWORK.sendToServer(new TaskCreatePacket(menu.getNetwork(), amount, pattern.getStack(), parallelMode, menu.getPosition()));
        disableRequest();
    }

    public void refreshTasks() {
        if (replicationTaskWidget != null) {
            replicationTaskWidget.refreshTasks();
        }
    }

    public int getLeftPos() {
        return leftPos;
    }

    public int getTopPos() {
        return topPos;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    private final class PatternMenu {
        private List<MatterPatternButton> matterPatternButtonList = new ArrayList<>();
        private List<MatterPatternButton> visibleButtons = new ArrayList<>();

        private int calculateRowCount() {
            return Math.max(1, Mth.positiveCeilDiv(getFilteredPatterns().size(), 9) - 5);
        }

        private float subtractInputFromScroll(float current, double input) {
            return Mth.clamp(current - (float) (input / calculateRowCount()), 0.0F, 1.0F);
        }

        private void scrollTo(float scroll) {
            int row = Math.max((int) (scroll * calculateRowCount() + 0.5F), 0);
            List<MatterPatternButton> filtered = new ArrayList<>(getFilteredPatterns());
            Comparator<MatterPatternButton> comparator = Comparator.comparingInt(MatterPatternButton::cachedAmount);
            if (sortingType != null && sortingType.getState() == 1) {
                comparator = Comparator.comparing(button -> button.pattern().getStack().getHoverName().getString().toLowerCase());
                comparator = comparator.reversed();
            }
            if (sortingDirection != null && sortingDirection.getState() == 1) {
                comparator = comparator.reversed();
            }
            filtered.sort(comparator);
            visibleButtons = filtered.stream().skip((long) row * 9).limit(45).collect(Collectors.toList());
        }

        private boolean canScroll() {
            return getFilteredPatterns().size() > 45;
        }

        private List<MatterPatternButton> getFilteredPatterns() {
            String query = searchBox == null ? "" : searchBox.getValue().toLowerCase();
            if (query.isBlank()) {
                return matterPatternButtonList;
            }
            return matterPatternButtonList.stream().filter(button -> button.pattern().getStack().getHoverName().getString().toLowerCase().contains(query)).toList();
        }

        private boolean mouseClicked(double mouseX, double mouseY) {
            for (int index = 0; index < visibleButtons.size(); index++) {
                int x = leftPos + index % 9 * 18 + 11;
                int y = topPos + index / 9 * 18 + 28;
                if (mouseX > x && mouseX < x + 18 && mouseY > y && mouseY < y + 18) {
                    MatterPatternButton button = visibleButtons.get(index);
                    if (button.cachedAmount() <= 0) {
                        return false;
                    }
                    enableRequest(new ReplicationRequestWidget((width - 177) / 2, (height - 102) / 2, 177, 102, Component.translatable("replication.request_amount"), button, ReplicationTerminalScreen.this));
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
                    return true;
                }
            }
            return false;
        }
    }
}
