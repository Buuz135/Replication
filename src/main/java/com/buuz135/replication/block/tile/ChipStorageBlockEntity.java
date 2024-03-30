package com.buuz135.replication.block.tile;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.pattern.IMatterPatternHolder;
import com.buuz135.replication.api.pattern.MatterPattern;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChipStorageBlockEntity extends NetworkBlockEntity<ChipStorageBlockEntity> implements IMatterPatternHolder<ChipStorageBlockEntity> {

    @Save
    private SidedInventoryComponent<ChipStorageBlockEntity> chips;
    private List<MatterPattern> cachedPatters;
    private boolean hasInvChanged;

    public ChipStorageBlockEntity(BasicTileBlock<ChipStorageBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.cachedPatters = new ArrayList<>();
        this.hasInvChanged = false;
        this.chips = (SidedInventoryComponent<ChipStorageBlockEntity>) new SidedInventoryComponent<ChipStorageBlockEntity>("input", 58, 22, 8, 0).
                setColor(DyeColor.LIGHT_BLUE).
                setSlotPosition(integer -> getSlotPos(integer)).
                setSlotLimit(1).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this).
                setInputFilter(((stack, integer) -> stack.is(ReplicationRegistry.Items.MEMORY_CHIP.get()))).
                setOnSlotChanged((stack, integer) -> notifyNetworkOfSlotChange());
        addInventory(this.chips);
    }

    @Override
    public InteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        }
        openGui(playerIn);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, ChipStorageBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (hasInvChanged){
            notifyNetworkOfSlotChange();

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
            cachePatterns();
            this.getNetwork().onChipValuesChanged(this, this.worldPosition);
        }
    }

    private void cachePatterns(){
        this.cachedPatters = new ArrayList<>();
        for (int i = 0; i < this.chips.getSlots(); i++) {
            var slot = this.chips.getStackInSlot(i);
            if (!slot.isEmpty() && slot.getItem() instanceof IMatterPatternHolder stackHolder){
                List<MatterPattern> patterns = (stackHolder).getPatterns(slot);
                patterns.forEach(pattern -> {
                    if (!pattern.getStack().isEmpty() && pattern.getCompletion() == 1){
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
        int slotSpacing = 22;
        int offset = 2;
        return switch (slot) {
            case 1 -> Pair.of(slotSpacing, -offset);
            case 2 -> Pair.of(slotSpacing * 2, 0);
            case 3 -> Pair.of(-offset, slotSpacing);
            case 4 -> Pair.of(slotSpacing * 2 + offset, slotSpacing);
            case 5 -> Pair.of(0, slotSpacing * 2);
            case 6 -> Pair.of(slotSpacing, slotSpacing * 2 + offset);
            case 7 -> Pair.of(slotSpacing * 2, slotSpacing * 2);
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
    public List<MatterPattern> getPatterns(ChipStorageBlockEntity element) {
        return this.cachedPatters;
    }
}
