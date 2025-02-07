package com.buuz135.replication.client.gui;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.calculation.MatterCompound;
import com.buuz135.replication.calculation.client.ClientReplicationCalculation;
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
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private MatterValuesMenu matterValuesMenu;
    private EditBox searchBox;
    private IMatterType searchMatterType;
    private int searchMatterAmount;
    private FilteringMode filteringMode;

    public MatterOpediaTaskWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, ReplicationTerminalScreen replicationTerminalContainer, String defaultSearch) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.widgets = new ArrayList<>();
        this.searchMatterAmount = 0;
        this.filteringMode = FilteringMode.NONE;
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
                scrollOffs = 0;
                matterValuesMenu.scrollTo(0);
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
                scrollOffs = 0;
                matterValuesMenu.scrollTo(0);
            }

            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
                super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        });

        this.matterValuesMenu = new MatterValuesMenu();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("replication.matteropedia"), this.getX() + 98, this.getY() - 10, 0x72e567);
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, 195, 256);

        int j = this.getX() + 175;
        int k = this.getY() + 28;
        int i = k + 88;
        guiGraphics.blit(BUTTONS, j, k + (int) ((float) (i - k - 5) * this.scrollOffs), 245, 0, 11, 5);
        this.searchBox.render(guiGraphics, mouseX, mouseY, v);

        for (AbstractWidget widget : this.widgets) {
            widget.render(guiGraphics, mouseX, mouseY, v);
        }
        for (int index = 0; index < this.matterValuesMenu.visibleButtons.size(); index++) {
            var widget = this.matterValuesMenu.visibleButtons.get(index);
            widget.render(guiGraphics, this.getX() + (index % 9) * 18 + 9, this.getY() + (index / 9) * 18 + 26, mouseX, mouseY, v);
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
            this.matterValuesMenu.scrollTo(this.scrollOffs);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double p_98527_, double p_98528_, double p_98529_, double scrollY) {
        if (!this.canScroll()) {
            return false;
        } else {
            this.scrollOffs = this.matterValuesMenu.subtractInputFromScroll(this.scrollOffs, scrollY);
            this.matterValuesMenu.scrollTo(this.scrollOffs);
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
        return this.matterValuesMenu.canScroll();
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
                this.matterValuesMenu.scrollTo(this.scrollOffs);
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
            if (this.matterValuesMenu.mouseClicked(pMouseX, pMouseY, pButton)) {
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        String s = this.searchBox.getValue();
        if (this.searchBox.charTyped(pCodePoint, pModifiers)) {
            if (!Objects.equals(s, this.searchBox.getValue())) {
                cacheMatterType();
                this.matterValuesMenu.scrollTo(0f);
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
                cacheMatterType();
                this.matterValuesMenu.scrollTo(0f);
                return true;
            }
        } else {
            return this.searchBox.isFocused() && this.searchBox.isVisible() && p_98547_ != 256 || super.keyPressed(p_98547_, p_98548_, p_98549_);
        }
        return false;
    }

    private void cacheMatterType() {
        var s = this.searchBox.getValue();
        var matterType = s;
        this.searchMatterAmount = 0;
        if (s.contains("=")) {
            this.filteringMode = FilteringMode.AMOUNT_EQUAL;
            matterType = cleanString(s, "=");
        } else if (s.contains("<")) {
            this.filteringMode = FilteringMode.AMOUNT_LESS;
            matterType = cleanString(s, "<");
        } else if (s.contains(">")) {
            this.filteringMode = FilteringMode.AMOUNT_GREATER;
            matterType = cleanString(s, ">");
        } else if (s.startsWith("!")) {
            this.filteringMode = FilteringMode.DOESNT_HAVE;
            matterType = s.replaceFirst("!", "");
        } else if (s.startsWith("*")) {
            this.filteringMode = FilteringMode.ONLY_HAS;
            matterType = s.replaceFirst("\\*", "");
        } else {
            this.filteringMode = FilteringMode.NONE;
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

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    public enum FilteringMode {
        NONE,
        AMOUNT_EQUAL,
        AMOUNT_LESS,
        AMOUNT_GREATER,
        DOESNT_HAVE,
        ONLY_HAS;

    }

    public class MatterDisplay {

        private ItemStack stack;
        private MatterCompound matterCompound;

        public MatterDisplay(ItemStack stack, MatterCompound values) {
            this.stack = stack;
            this.matterCompound = values;
        }

        public ItemStack getStack() {
            return stack;
        }

        public MatterCompound getMatterCompound() {
            return matterCompound;
        }

        public double getAmountFor(IMatterType type) {
            if (matterCompound.getValues().containsKey(type)) {
                return matterCompound.getValues().get(type).getAmount();
            }
            return 0;
        }

        public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float v) {
            guiGraphics.renderItem(stack, x + 2, y + 2);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 200);
            var scale = 0.5f;
            var amount = getAmountFor(MatterOpediaTaskWidget.this.searchMatterType);
            if (amount > 0) {
                var display = NumberUtils.getFormatedBigNumber((int) Math.ceil(amount));
                guiGraphics.pose().scale(scale, scale, scale);
                guiGraphics.drawString(Minecraft.getInstance().font, display, (x + 18) / scale - Minecraft.getInstance().font.width(display), (y + 14) / scale, 0xFFFFFF, true);
            }
            guiGraphics.pose().popPose();
            if (mouseX > x + 2 && mouseX < x + 19 && mouseY > y + 2 && mouseY < y + 19) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, stack, mouseX, mouseY);
            }
        }
    }

    public class MatterValuesMenu {
        public List<MatterDisplay> matterPatternButtonList = new ArrayList<>();
        public List<MatterDisplay> visibleButtons = new ArrayList<>();

        public MatterValuesMenu() {
            ClientReplicationCalculation.DEFAULT_MATTER_COMPOUND.forEach((s, matterCompound) -> {
                var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(s));
                var stack = item.getDefaultInstance();
                if (!stack.isEmpty()) {
                    matterPatternButtonList.add(new MatterDisplay(stack, matterCompound));
                }
            });
            this.scrollTo(0.0F);
        }


        protected int calculateRowCount() {
            return Mth.positiveCeilDiv(getFilteredPatterns().size(), 9) - 7;
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
            Comparator<MatterDisplay> comparator = Comparator.comparingDouble(matterDisplay -> matterDisplay.getAmountFor(MatterOpediaTaskWidget.this.searchMatterType));
            if (MatterOpediaTaskWidget.this.sortingType.getState() == 1) {
                comparator = Comparator.comparing(matterPatternButton -> matterPatternButton.getStack().getDisplayName().getString().toLowerCase());
                comparator = comparator.reversed();
            }
            if (MatterOpediaTaskWidget.this.sortingDirection.getState() == 1) {
                comparator = comparator.reversed();
            }
            filtered.sort(comparator);
            for (int j = 0; j < 7; ++j) {
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
            return getFilteredPatterns().size() > 9 * 7;
        }

        private List<MatterDisplay> getFilteredPatterns() {
            var list = this.matterPatternButtonList.stream();
            if (MatterOpediaTaskWidget.this.filteringMode == FilteringMode.NONE) {
                list = list.filter(matterPatternButton -> matterPatternButton.getAmountFor(MatterOpediaTaskWidget.this.searchMatterType) > 0);
            } else if (MatterOpediaTaskWidget.this.filteringMode == FilteringMode.AMOUNT_EQUAL) {
                list = list.filter(matterPatternButton -> matterPatternButton.getAmountFor(MatterOpediaTaskWidget.this.searchMatterType) == MatterOpediaTaskWidget.this.searchMatterAmount);
            } else if (MatterOpediaTaskWidget.this.filteringMode == FilteringMode.AMOUNT_LESS) {
                list = list.filter(matterPatternButton -> matterPatternButton.getAmountFor(MatterOpediaTaskWidget.this.searchMatterType) < MatterOpediaTaskWidget.this.searchMatterAmount);
            } else if (MatterOpediaTaskWidget.this.filteringMode == FilteringMode.AMOUNT_GREATER) {
                list = list.filter(matterPatternButton -> matterPatternButton.getAmountFor(MatterOpediaTaskWidget.this.searchMatterType) > MatterOpediaTaskWidget.this.searchMatterAmount);
            } else if (MatterOpediaTaskWidget.this.filteringMode == FilteringMode.ONLY_HAS) {
                list = list.filter(matterPatternButton -> matterPatternButton.getAmountFor(MatterOpediaTaskWidget.this.searchMatterType) > 0 && matterPatternButton.getMatterCompound().getValues().size() == 1);
            } else if (MatterOpediaTaskWidget.this.filteringMode == FilteringMode.DOESNT_HAVE) {
                list = list.filter(matterPatternButton -> matterPatternButton.getAmountFor(MatterOpediaTaskWidget.this.searchMatterType) == 0);
            }
            return list.collect(Collectors.toList());
        }

        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            /**for (int matterButtonIndex = 0; matterButtonIndex < this.visibleButtons.size(); matterButtonIndex++) {
             if (pMouseX > MatterOpediaTaskWidget.this.getX() + (matterButtonIndex % 9) * 18 + 11 && pMouseX < MatterOpediaTaskWidget.this.getX()  + (matterButtonIndex % 9) * 18 + 11 + 18
             && pMouseY > MatterOpediaTaskWidget.this.topPos + (matterButtonIndex / 9) * 18 + 28 && pMouseY < MatterOpediaTaskWidget.this.topPos + (matterButtonIndex / 9) * 18 + 28 + 18) {
             var patternButton = this.visibleButtons.get(matterButtonIndex);
             if (patternButton.cachedAmount() == 0) return false;
             MatterOpediaTaskWidget.this.enableRequest(new ReplicationRequestWidget((MatterOpediaTaskWidget.this.width - 177) / 2,
             (MatterOpediaTaskWidget.this.height - 102) / 2, 177, 102, Component.translatable("replication.request_amount"), patternButton, MatterOpediaTaskWidget.this));
             Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ReplicationRegistry.Sounds.TERMINAL_BUTTON.get(), 1.0F));
             return true;
             }
             }**/
            return false;
        }
    }
}
