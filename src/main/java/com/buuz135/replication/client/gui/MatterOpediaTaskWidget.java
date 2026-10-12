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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MatterOpediaTaskWidget extends AbstractWidget {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/matteropedia.png");
    private static final Identifier EXTRAS = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png");
    private final List<AbstractWidget> widgets = new ArrayList<>();
    private final ReplicationTerminalConfigButton sortingType;
    private final ReplicationTerminalConfigButton sortingDirection;
    private final EditBox searchBox;
    private MatterValuesMenu matterValuesMenu;
    private float scrollOffs;
    private boolean scrolling;
    private IMatterType searchMatterType;
    private int searchMatterAmount;
    private FilteringMode filteringMode = FilteringMode.NONE;

    public MatterOpediaTaskWidget(int x, int y, int width, int height, Component message, ReplicationTerminalScreen screen, String defaultSearch) {
        super(x, y, width, height, message);
        widgets.add(new ReplicationTerminalTexturedButton(x + 176, y + 10, 9, 9, Component.empty(), EXTRAS, "tooltip.replication.close", 247, 50, 238, 50, button -> screen.disableMatteropedia()));
        this.searchMatterType = ReplicationRegistry.Matter.EARTH.get();
        this.searchBox = new EditBox(Minecraft.getInstance().font, x + 85, y + 12, 79, 13, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setTextColor(0xff72e567);
        this.searchBox.setValue(defaultSearch);
        this.searchBox.setResponder(value -> {
            cacheMatterType();
            if (matterValuesMenu != null) {
                scrollOffs = 0;
                matterValuesMenu.scrollTo(0);
            }
        });
        widgets.add(searchBox);
        Runnable changed = () -> {
            scrollOffs = 0;
            if (matterValuesMenu != null) {
                matterValuesMenu.scrollTo(0);
            }
        };
        this.sortingType = new ReplicationTerminalConfigButton(x + 10, y + 10, 9, 9, ReplicationTerminalScreen.BUTTONS, new TileEntityLocatorInstance(screen.getMenu().getPosition()), ReplicationTerminalConfigButton.Type.MATTEROPEDIA_TYPE, screen.getMenu().getMatterOpediaSortingType(), 247, 5, 238, 5, changed);
        this.sortingDirection = new ReplicationTerminalConfigButton(x + 20, y + 10, 9, 9, ReplicationTerminalScreen.BUTTONS, new TileEntityLocatorInstance(screen.getMenu().getPosition()), ReplicationTerminalConfigButton.Type.MATTEROPEDIA_DIRECTION, screen.getMenu().getMatterOpediaSortingValue(), 247, 23, 238, 23, changed);
        widgets.add(sortingType);
        widgets.add(sortingDirection);
        cacheMatterType();
        this.matterValuesMenu = new MatterValuesMenu();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.centeredText(Minecraft.getInstance().font, Component.translatable("replication.matteropedia"), getX() + 98, getY() - 10, 0xFF72E567);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), 0, 0, 195, 256, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, ReplicationTerminalScreen.BUTTONS, getX() + 175, getY() + 28 + (int) (83 * scrollOffs), 245, 0, 11, 5, 256, 256);
        for (int index = 0; index < matterValuesMenu.visibleButtons.size(); index++) {
            matterValuesMenu.visibleButtons.get(index).render(graphics, getX() + index % 9 * 18 + 9, getY() + index / 9 * 18 + 26, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && insideScrollbar(event.x(), event.y())) {
            scrolling = matterValuesMenu.canScroll();
            updateScroll(event.y());
            return true;
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
        if (!matterValuesMenu.canScroll()) {
            return false;
        }
        scrollOffs = matterValuesMenu.subtractInputFromScroll(scrollOffs, scrollY);
        matterValuesMenu.scrollTo(scrollOffs);
        return true;
    }

    private void updateScroll(double mouseY) {
        scrollOffs = Mth.clamp(((float) mouseY - (getY() + 24) - 7.5F) / 71.0F, 0.0F, 1.0F);
        matterValuesMenu.scrollTo(scrollOffs);
    }

    private boolean insideScrollbar(double mouseX, double mouseY) {
        return mouseX >= getX() + 176 && mouseX < getX() + 185 && mouseY >= getY() + 27 && mouseY < getY() + 117;
    }

    private void cacheMatterType() {
        String input = searchBox.getValue();
        String matterType = input;
        searchMatterAmount = 0;
        if (input.contains("=")) {
            filteringMode = FilteringMode.AMOUNT_EQUAL;
            matterType = cleanString(input, "=");
        } else if (input.contains("<")) {
            filteringMode = FilteringMode.AMOUNT_LESS;
            matterType = cleanString(input, "<");
        } else if (input.contains(">")) {
            filteringMode = FilteringMode.AMOUNT_GREATER;
            matterType = cleanString(input, ">");
        } else if (input.startsWith("!")) {
            filteringMode = FilteringMode.DOESNT_HAVE;
            matterType = input.substring(1);
        } else if (input.startsWith("*")) {
            filteringMode = FilteringMode.ONLY_HAS;
            matterType = input.substring(1);
        } else {
            filteringMode = FilteringMode.NONE;
        }
        String query = matterType.toLowerCase();
        searchMatterType = ReplicationRegistry.MATTER_TYPES_REGISTRY.stream().filter(type -> Component.translatable("replication.matter_type." + type.getName()).getString().toLowerCase().contains(query)).findFirst().orElse(ReplicationRegistry.Matter.EARTH.get());
    }

    private String cleanString(String input, String separator) {
        String[] split = input.split(separator, 2);
        if (split.length == 2) {
            try {
                searchMatterAmount = Integer.parseInt(split[1]);
            } catch (NumberFormatException exception) {
                searchMatterAmount = 0;
            }
            return split[0];
        }
        return input;
    }

    public List<AbstractWidget> getWidgets() {
        return widgets;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    private enum FilteringMode {
        NONE,
        AMOUNT_EQUAL,
        AMOUNT_LESS,
        AMOUNT_GREATER,
        DOESNT_HAVE,
        ONLY_HAS
    }

    private final class MatterDisplay {
        private final ItemStack stack;
        private final MatterCompound matterCompound;

        private MatterDisplay(ItemStack stack, MatterCompound matterCompound) {
            this.stack = stack;
            this.matterCompound = matterCompound;
        }

        private double getAmountFor(IMatterType type) {
            return matterCompound.getValues().containsKey(type) ? matterCompound.getValues().get(type).getAmount() : 0;
        }

        private void render(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
            graphics.item(stack, x + 2, y + 2);
            double amount = getAmountFor(searchMatterType);
            if (amount > 0) {
                String display = NumberUtils.getFormatedBigNumber(amount);
                float scale = 0.5F;
                graphics.pose().pushMatrix();
                graphics.pose().scale(scale, scale);
                graphics.text(Minecraft.getInstance().font, display, (int) ((x + 18) / scale) - Minecraft.getInstance().font.width(display), (int) ((y + 14) / scale), 0xFFFFFFFF, true);
                graphics.pose().popMatrix();
            }
            if (mouseX > x + 2 && mouseX < x + 19 && mouseY > y + 2 && mouseY < y + 19) {
                graphics.setTooltipForNextFrame(Minecraft.getInstance().font, stack, mouseX, mouseY);
            }
        }
    }

    private final class MatterValuesMenu {
        private final List<MatterDisplay> displays = new ArrayList<>();
        private List<MatterDisplay> visibleButtons = new ArrayList<>();

        private MatterValuesMenu() {
            ClientReplicationCalculation.DEFAULT_MATTER_COMPOUND.forEach((id, compound) -> {
                ItemStack stack = BuiltInRegistries.ITEM.getValue(Identifier.parse(id)).getDefaultInstance();
                if (!stack.isEmpty()) {
                    displays.add(new MatterDisplay(stack, compound));
                }
            });
            scrollTo(0);
        }

        private int calculateRowCount() {
            return Math.max(1, Mth.positiveCeilDiv(getFilteredPatterns().size(), 9) - 7);
        }

        private float subtractInputFromScroll(float current, double input) {
            return Mth.clamp(current - (float) (input / calculateRowCount()), 0.0F, 1.0F);
        }

        private void scrollTo(float scroll) {
            int row = Math.max((int) (scroll * calculateRowCount() + 0.5F), 0);
            List<MatterDisplay> filtered = getFilteredPatterns();
            Comparator<MatterDisplay> comparator = Comparator.comparingDouble(display -> display.getAmountFor(searchMatterType));
            if (sortingType.getState() == 1) {
                comparator = Comparator.comparing(display -> display.stack.getHoverName().getString().toLowerCase());
                comparator = comparator.reversed();
            }
            if (sortingDirection.getState() == 1) {
                comparator = comparator.reversed();
            }
            filtered.sort(comparator);
            visibleButtons = filtered.stream().skip((long) row * 9).limit(63).collect(Collectors.toList());
        }

        private boolean canScroll() {
            return getFilteredPatterns().size() > 63;
        }

        private List<MatterDisplay> getFilteredPatterns() {
            return displays.stream().filter(display -> switch (filteringMode) {
                case NONE -> display.getAmountFor(searchMatterType) > 0;
                case AMOUNT_EQUAL -> display.getAmountFor(searchMatterType) == searchMatterAmount;
                case AMOUNT_LESS -> display.getAmountFor(searchMatterType) < searchMatterAmount;
                case AMOUNT_GREATER -> display.getAmountFor(searchMatterType) > searchMatterAmount;
                case ONLY_HAS ->
                        display.getAmountFor(searchMatterType) > 0 && display.matterCompound.getValues().size() == 1;
                case DOESNT_HAVE -> display.getAmountFor(searchMatterType) == 0;
            }).collect(Collectors.toList());
        }
    }
}
