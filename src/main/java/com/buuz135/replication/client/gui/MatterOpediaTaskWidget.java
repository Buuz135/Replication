package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.calculation.client.ClientReplicationCalculation;
import com.buuz135.replication.calculation.client.matteropedia.MatterOpediaCatalog;
import com.buuz135.replication.calculation.client.matteropedia.MatterOpediaQuery;
import com.buuz135.replication.calculation.client.matteropedia.MatterOpediaResult;
import com.buuz135.replication.client.gui.button.ReplicationTerminalConfigButton;
import com.buuz135.replication.client.gui.button.ReplicationTerminalTexturedButton;
import com.buuz135.replication.util.NumberUtils;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.buuz135.replication.client.gui.ReplicationTerminalScreen.BUTTONS;

public class MatterOpediaTaskWidget extends AbstractWidget implements Renderable {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/matteropedia.png");
    private static final ResourceLocation EXTRAS = ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png");
    private final List<AbstractWidget> widgets;
    private final ReplicationTerminalScreen replicationTerminalScreen;
    private final ReplicationTerminalTexturedButton closeButton;
    private final int scrollBarX;
    private final int scrollBarY;
    private final int scrollBarWidth;
    private final int scrollBarHeight;
    private float scrollOffs;
    private boolean scrolling;
    private ReplicationTerminalConfigButton sortingType;
    private ReplicationTerminalConfigButton sortingDirection;
    private EditBox searchBox;
    private IMatterType searchMatterType;
    private int searchMatterAmount;
    private MatterOpediaQuery.FilterMode filteringMode;
    private MatterOpediaCatalog catalog = ClientReplicationCalculation.getMatterOpediaCatalog();
    private MatterOpediaResult result = MatterOpediaResult.empty();
    private final Map<Integer, ItemStack> visibleStacks = new HashMap<>();
    private int firstVisibleEntry;

    public MatterOpediaTaskWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, ReplicationTerminalScreen replicationTerminalContainer, String defaultSearch) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.widgets = new ArrayList<>();
        this.searchMatterAmount = 0;
        this.filteringMode = MatterOpediaQuery.FilterMode.NONE;
        this.replicationTerminalScreen = replicationTerminalContainer;
        this.closeButton = new ReplicationTerminalTexturedButton(this.getX() + 176, this.getY() + 10, 9, 9, Component.empty(), EXTRAS,
                Component.translatable("tooltip.replication.close").getString(), 247, 50, 238, 50, button -> this.replicationTerminalScreen.disableMatteropedia());
        this.widgets.add(this.closeButton);
        this.scrollOffs = 0;
        this.scrolling = false;
        this.scrollBarX = this.getX() + this.width;
        this.scrollBarY = this.getY() + 23;
        this.scrollBarHeight = 88;
        this.scrollBarWidth = 9;
        this.searchMatterType = ReplicationRegistry.Matter.EARTH.get();


        this.searchBox = new EditBox(Minecraft.getInstance().font, this.getX() + 85, this.getY() + 12, 79, 13, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0x72e567);
        this.searchBox.setValue(defaultSearch);
        cacheMatterType();
        this.widgets.add(this.searchBox);


        this.widgets.add(this.sortingType = new ReplicationTerminalConfigButton(this.getX() + 10, this.getY() + 10, 9, 9, BUTTONS, new TileEntityLocatorInstance(replicationTerminalContainer.getMenu().getPosition()), ReplicationTerminalConfigButton.Type.MATTEROPEDIA_TYPE, replicationTerminalContainer.getMenu().getMatterOpediaSortingType(),
                247, 5, 238, 5) {
            @Override
            public void onPress() {
                super.onPress();
                refreshQuery();
            }

            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
                super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        });

        this.widgets.add(this.sortingDirection = new ReplicationTerminalConfigButton(this.getX() + 20, this.getY() + 10, 9, 9, BUTTONS, new TileEntityLocatorInstance(replicationTerminalContainer.getMenu().getPosition()), ReplicationTerminalConfigButton.Type.MATTEROPEDIA_DIRECTION, replicationTerminalContainer.getMenu().getMatterOpediaSortingValue(),
                247, 23, 238, 23) {
            @Override
            public void onPress() {
                super.onPress();
                refreshQuery();
            }

            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
                super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        });

        refreshQuery();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("replication.matteropedia"), this.getX() + 98, this.getY() - 10, 0x72e567);
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, 195, 256);

        int j = this.getX() + 175;
        int k = this.getY() + 28;
        int i = k + 88;
        guiGraphics.blit(BUTTONS, j, k + (int) ((float) (i - k - 5) * this.scrollOffs), 245, 0, 11, 5);

        for (AbstractWidget widget : this.widgets) {
            widget.render(guiGraphics, mouseX, mouseY, v);
        }

        int visibleCount = Math.min(63, result.size() - firstVisibleEntry);
        for (int index = 0; index < visibleCount; index++) {
            int entryId = result.entryIdAt(firstVisibleEntry + index);
            MatterOpediaCatalog.Entry entry = catalog.entry(entryId);
            ItemStack stack = visibleStacks.computeIfAbsent(entryId, ignored ->
                    BuiltInRegistries.ITEM.getOptional(entry.itemId())
                            .map(Item::getDefaultInstance)
                            .orElse(ItemStack.EMPTY));
            renderEntry(guiGraphics, entry, stack,
                    getX() + (index % 9) * 18 + 9,
                    getY() + (index / 9) * 18 + 26,
                    mouseX, mouseY, v);
        }
    }

    private void renderEntry(
            GuiGraphics guiGraphics,
            MatterOpediaCatalog.Entry entry,
            ItemStack stack,
            int x,
            int y,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        guiGraphics.renderItem(stack, x + 2, y + 2);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        var scale = 0.5f;
        ResourceLocation matterKey = ReplicationRegistry.MATTER_TYPES_REGISTRY.getKey(searchMatterType);
        var amount = entry.amount(matterKey);
        if (amount > 0) {
            var display = NumberUtils.getFormatedBigNumber(amount);
            guiGraphics.pose().scale(scale, scale, scale);
            guiGraphics.drawString(Minecraft.getInstance().font, display,
                    (x + 18) / scale - Minecraft.getInstance().font.width(display),
                    (y + 14) / scale, 0xFFFFFF, true);
        }
        guiGraphics.pose().popPose();
        if (mouseX > x + 2 && mouseX < x + 19 && mouseY > y + 2 && mouseY < y + 19) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, stack, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }


    protected boolean insideScrollbar(double p_98524_, double p_98525_) {
        int i = this.getX();
        int j = this.getY();
        int k = i + 176;
        int l = j + 27;
        int i1 = k + 9;
        int j1 = l + 90;
        return p_98524_ >= (double) k && p_98525_ >= (double) l && p_98524_ < (double) i1 && p_98525_ < (double) j1;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling) {
            int i = this.scrollBarY + 1;
            int j = i + this.scrollBarHeight - 2;
            this.scrollOffs = ((float) pMouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.scrollTo(this.scrollOffs);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double p_98527_, double p_98528_, double p_98529_, double scrollY) {
        if (!this.canScroll()) {
            return false;
        } else {
            this.scrollOffs = Mth.clamp(
                    this.scrollOffs - (float) (scrollY / (double) this.scrollableRows()),
                    0.0F, 1.0F);
            this.scrollTo(this.scrollOffs);
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
        return this.result.size() > 63;
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
                this.scrollTo(this.scrollOffs);
                return true;
            }
            if (this.searchBox.isHovered()) {
                this.searchBox.setFocused(true);
                return this.searchBox.mouseClicked(pMouseX, pMouseY, pButton);
            } else {
                this.searchBox.setFocused(false);
            }
            if (this.closeButton.isHovered()) {
                return this.closeButton.mouseClicked(pMouseX, pMouseY, pButton);
            }
            if (this.sortingDirection.isHovered()) {
                return this.sortingDirection.mouseClicked(pMouseX, pMouseY, pButton);
            }
            if (this.sortingType.isHovered()) {
                return this.sortingType.mouseClicked(pMouseX, pMouseY, pButton);
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        String s = this.searchBox.getValue();
        if (this.searchBox.charTyped(pCodePoint, pModifiers)) {
            if (!Objects.equals(s, this.searchBox.getValue())) {
                if (cacheMatterType()) {
                    refreshQuery();
                }
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean keyPressed(int p_98547_, int p_98548_, int p_98549_) {
        if (p_98547_ == 256) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
            this.replicationTerminalScreen.disableMatteropedia();
        }
        String s = this.searchBox.getValue();
        if (this.searchBox.keyPressed(p_98547_, p_98548_, p_98549_)) {
            if (!Objects.equals(s, this.searchBox.getValue())) {
                if (cacheMatterType()) {
                    refreshQuery();
                }
                return true;
            }
        } else {
            return this.searchBox.isFocused() && this.searchBox.isVisible() && p_98547_ != 256 || super.keyPressed(p_98547_, p_98548_, p_98549_);
        }
        return false;
    }

    private boolean cacheMatterType() {
        IMatterType previousMatterType = this.searchMatterType;
        int previousMatterAmount = this.searchMatterAmount;
        MatterOpediaQuery.FilterMode previousFilteringMode = this.filteringMode;
        var s = this.searchBox.getValue();
        var matterType = s;
        this.searchMatterAmount = 0;
        if (s.contains("=")) {
            this.filteringMode = MatterOpediaQuery.FilterMode.AMOUNT_EQUAL;
            matterType = cleanString(s, "=");
        } else if (s.contains("<")) {
            this.filteringMode = MatterOpediaQuery.FilterMode.AMOUNT_LESS;
            matterType = cleanString(s, "<");
        } else if (s.contains(">")) {
            this.filteringMode = MatterOpediaQuery.FilterMode.AMOUNT_GREATER;
            matterType = cleanString(s, ">");
        } else if (s.startsWith("!")) {
            this.filteringMode = MatterOpediaQuery.FilterMode.DOESNT_HAVE;
            matterType = s.replaceFirst("!", "");
        } else if (s.startsWith("*")) {
            this.filteringMode = MatterOpediaQuery.FilterMode.ONLY_HAS;
            matterType = s.replaceFirst("\\*", "");
        } else {
            this.filteringMode = MatterOpediaQuery.FilterMode.NONE;
        }
        var found = false;
        for (IMatterType iMatterType : ReplicationRegistry.MATTER_TYPES_REGISTRY) {
            if (Component.translatable("replication.matter_type." + iMatterType.getName()).getString().toLowerCase().contains(matterType.toLowerCase())) {
                this.searchMatterType = iMatterType;
                found = true;
                break;
            }
        }
        if (!found) {
            this.searchMatterType = ReplicationRegistry.Matter.EARTH.get();
        }
        return previousMatterType != this.searchMatterType
                || previousMatterAmount != this.searchMatterAmount
                || previousFilteringMode != this.filteringMode;
    }

    private String cleanString(String input, String splitChar) {
        var split = input.split(splitChar);
        if (split.length == 2) {
            try {
                this.searchMatterAmount = Integer.parseInt(split[1]);
            } catch (Exception e) {

            }
            return split[0];
        }
        return input;
    }

    private MatterOpediaQuery currentQuery(MatterOpediaCatalog catalog) {
        ResourceLocation matterKey = ReplicationRegistry.MATTER_TYPES_REGISTRY.getKey(searchMatterType);
        MatterOpediaQuery.SortType sortType = sortingType.getState() == 0
                ? MatterOpediaQuery.SortType.AMOUNT
                : MatterOpediaQuery.SortType.DISPLAY_NAME;

        // Preserve the existing comparator's direction behavior exactly.
        boolean descending = sortType == MatterOpediaQuery.SortType.AMOUNT
                ? sortingDirection.getState() == 1
                : sortingDirection.getState() == 0;

        return new MatterOpediaQuery(
                matterKey, filteringMode, searchMatterAmount, sortType, descending);
    }

    private void refreshQuery() {
        result = catalog.query(currentQuery(catalog));
        firstVisibleEntry = 0;
        scrollOffs = 0.0F;
    }

    private int scrollableRows() {
        return Math.max(Mth.positiveCeilDiv(result.size(), 9) - 7, 0);
    }

    public void scrollTo(float offset) {
        int firstRow = Math.max((int) (offset * scrollableRows() + 0.5F), 0);
        firstVisibleEntry = firstRow * 9;
    }

    public void tickCatalog() {
        MatterOpediaCatalog current = ClientReplicationCalculation.getMatterOpediaCatalog();
        if (current.generation() != catalog.generation()
                || current.languageGeneration() != catalog.languageGeneration()) {
            catalog = current;
            visibleStacks.clear();
            refreshQuery();
        }
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }
}
