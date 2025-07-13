package com.buuz135.replication.block.tile;

import com.buuz135.replication.api.pattern.IMatterPatternHolder;
import com.buuz135.replication.api.pattern.IMatterPatternModifier;
import com.buuz135.replication.api.pattern.MatterPattern;
import com.buuz135.replication.calculation.ReplicationCalculation;
import com.buuz135.replication.client.gui.ReplicationAddonProvider;
import com.buuz135.replication.client.gui.addons.ChipStorageAddon;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChipStorageBlockEntity extends NetworkBlockEntity<ChipStorageBlockEntity> implements IMatterPatternHolder<ChipStorageBlockEntity>, IMatterPatternModifier<ChipStorageBlockEntity> {

    @Save
    private SidedInventoryComponent<ChipStorageBlockEntity> chips;
    private List<MatterPattern> cachedPatters;
    private boolean hasInvChanged;

    public ChipStorageBlockEntity(BasicTileBlock<ChipStorageBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.cachedPatters = new ArrayList<>();
        this.hasInvChanged = false;
        this.chips = (SidedInventoryComponent<ChipStorageBlockEntity>) new SidedInventoryComponent<ChipStorageBlockEntity>("input", 68, 18, 8, 0)
                .disableFacingAddon()
                .setSlotPosition(integer -> getSlotPos(integer))
                .setSlotLimit(1)
                .setOutputFilter((stack, integer) -> false)
                .setComponentHarness(this)
                .setInputFilter(((stack, integer) -> stack.getItem() instanceof IMatterPatternHolder))
                .setOnSlotChanged((stack, integer) -> notifyNetworkOfSlotChange())
                .setColorGuiEnabled(false);
        addInventory(this.chips);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(() -> new ChipStorageAddon(50, 30, this));
    }

    @Override
    public ItemInteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == ItemInteractionResult.SUCCESS) {
            return ItemInteractionResult.SUCCESS;
        }
        openGui(playerIn);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, ChipStorageBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (hasInvChanged){
            notifyNetworkOfSlotChange();
            hasInvChanged = false;
        }
    }

    @Override
    public void setLevel(Level p_155231_) {
        super.setLevel(p_155231_);
        if (isServer()){
            cachePatterns();
        }
    }

    private void notifyNetworkOfSlotChange() {
        if (isServer()){
            for (int i = 0; i < this.chips.getSlots(); i++) {
                var stack = this.chips.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof IMatterPatternHolder stackHolder) {
                    List<MatterPattern> patterns = stackHolder.getPatterns(this.level, stack);
                    for (int j = 0; j < this.chips.getSlots(); j++) {
                        if (i == j) continue;
                        var otherStack = this.chips.getStackInSlot(j);
                        if (!stack.isEmpty() && otherStack.getItem() instanceof IMatterPatternHolder otherStackHolder && otherStack.getItem() instanceof IMatterPatternModifier otherPatternModifier) {
                            List<MatterPattern> otherPatterns = otherStackHolder.getPatterns(this.level, otherStack);
                            for (MatterPattern pattern : patterns) {
                                for (MatterPattern otherPattern : otherPatterns) {
                                    if (ItemStack.isSameItemSameComponents(pattern.getStack(), otherPattern.getStack())) {
                                        otherPatternModifier.removePattern(this.level, otherStack, otherPattern.getStack());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            syncObject(this.chips);
            cachePatterns();
            this.getNetwork().onChipValuesChanged(this, this.worldPosition);
        }
    }

    public void cachePatterns() {
        this.cachedPatters = new ArrayList<>();
        for (int i = 0; i < this.chips.getSlots(); i++) {
            var slot = this.chips.getStackInSlot(i);
            if (!slot.isEmpty() && slot.getItem() instanceof IMatterPatternHolder stackHolder){
                List<MatterPattern> patterns = (stackHolder).getPatterns(this.level, slot);
                patterns.forEach(pattern -> {
                    if (!pattern.getStack().isEmpty() && pattern.getCompletion() == 1 && ReplicationCalculation.getMatterCompound(pattern.getStack()) != null) {
                        this.cachedPatters.add(pattern);
                    }
                });
            }
        }
    }

    @NotNull
    @Override
    public ChipStorageBlockEntity getSelf() {
        return this;
    }

    public static Pair<Integer, Integer> getSlotPos(int slot) {
        int slotSpacing = 21;
        int offset = 2;
        return switch (slot) {
            case 1 -> Pair.of(slotSpacing, 0);
            case 2 -> Pair.of(slotSpacing * 2, slotSpacing);
            case 3 -> Pair.of(slotSpacing * 2, slotSpacing * 2);
            case 4 -> Pair.of(slotSpacing , slotSpacing * 3);
            case 5 -> Pair.of(0, slotSpacing * 3);
            case 6 -> Pair.of(-slotSpacing, slotSpacing * 2);
            case 7 -> Pair.of(-slotSpacing, slotSpacing);
            default -> Pair.of(0, 0);
        };
    }

    public SidedInventoryComponent<ChipStorageBlockEntity> getChips() {
        return chips;
    }

    @Override
    public int getPatternSlots(ChipStorageBlockEntity element) {
        return element.chips.getSlots();
    }

    @Override
    public List<MatterPattern> getPatterns(Level level, ChipStorageBlockEntity element) {
        return this.cachedPatters;
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return ReplicationAddonProvider.INSTANCE;
    }

    @Override
    public int getTitleColor() {
        return 0x72e567;
    }

    @Override
    public float getTitleYPos(float titleWidth, float screenWidth, float screenHeight, float guiWidth, float guiHeight) {
        return super.getTitleYPos(titleWidth, screenWidth, screenHeight, guiWidth, guiHeight) - 16;
    }

    @Override
    public @Nullable ModifierAction addPattern(Level level, ChipStorageBlockEntity element, ItemStack stack, float progress) {
        for (int i = 0; i < this.chips.getSlots(); i++) {
            var chipStack = this.chips.getStackInSlot(i);
            if (!chipStack.isEmpty() && chipStack.getItem() instanceof IMatterPatternModifier<?>) {
                var patternModifier = (IMatterPatternModifier<ItemStack>) chipStack.getItem();
                var returnedValue = patternModifier.addPattern(level, chipStack, stack, progress);
                if (returnedValue == null) continue;
                if (returnedValue.getType() == ModifierType.FULL && returnedValue.getPattern() != null)
                    return ModifierAction.isFull(null);
                ;
                if (returnedValue.getType() == ModifierType.FULL && returnedValue.getPattern() == null) continue;
                hasInvChanged = true;
                return returnedValue;
            }
        }
        return ModifierAction.isFull(null);
    }

    @Override
    public void removePattern(Level level, ChipStorageBlockEntity element, ItemStack stack) {

    }
}
