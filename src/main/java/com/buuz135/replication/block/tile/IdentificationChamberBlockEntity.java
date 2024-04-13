package com.buuz135.replication.block.tile;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.pattern.IMatterPatternHolder;
import com.buuz135.replication.api.pattern.IMatterPatternModifier;
import com.buuz135.replication.client.gui.addons.IdentificationChamberAddon;
import com.buuz135.replication.client.gui.addons.ReplicatorCraftingAddon;
import com.buuz135.replication.item.ReplicationItem;
import com.buuz135.replication.util.InvUtil;
import com.buuz135.replication.util.ReplicationTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.util.FacingUtil;
import com.hrznstudio.titanium.util.LangUtil;
import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class IdentificationChamberBlockEntity extends ReplicationMachine<IdentificationChamberBlockEntity>{


    public static final int MAX_PROGRESS = 100;
    public static final float LOWER_PROGRESS = 0.78f-0.2f;

    @Save
    private SidedInventoryComponent<?> input;
    @Save
    private ProgressBarComponent<?> progressBarComponent;

    @Save
    private SidedInventoryComponent<?> memoryChipInput;
    @Save
    private SidedInventoryComponent<?> memoryChipOutput;

    public IdentificationChamberBlockEntity(BasicTileBlock<IdentificationChamberBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.input = (SidedInventoryComponent<?>) new SidedInventoryComponent<>("input", 44, 48, 1, 0)
                .disableFacingAddon()
                .setInputFilter((itemStack, integer) -> !IAequivaleoAPI.getInstance().getEquivalencyResults(this.level.dimension()).dataFor(itemStack).isEmpty()
                        || itemStack.is(ReplicationRegistry.Items.MATTER_BLUEPRINT.get()))
                .setOutputFilter((itemStack, integer) -> false)
                .setSlotLimit(1)
                .setOnSlotChanged((stack, integer) -> syncObject(this.input))
                .setColorGuiEnabled(false)
        ;
        InvUtil.disableAllSidesAndEnable(this.input, state.getValue(RotatableBlock.FACING_HORIZONTAL), IFacingComponent.FaceMode.ENABLED, FacingUtil.Sideness.BOTTOM, FacingUtil.Sideness.BACK);
        this.addInventory((InventoryComponent<IdentificationChamberBlockEntity>) input);

        this.progressBarComponent = new ProgressBarComponent<>(72, 48, MAX_PROGRESS * 2)
                .setCanIncrease(iComponentHarness -> canIncrease())
                .setOnTickWork(() -> {
                    syncObject(this.progressBarComponent);
                })
                .setOnFinishWork(this::onFinish)
                .setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);

        this.addProgressBar((ProgressBarComponent<IdentificationChamberBlockEntity>) this.progressBarComponent);

        this.memoryChipInput = (SidedInventoryComponent<?>) new SidedInventoryComponent<>("memoryChipInput", 106, 48 - 18, 3, 0)
                .disableFacingAddon()
                .setInputFilter((itemStack, integer) -> itemStack.getItem() instanceof IMatterPatternHolder<?>)
                .setOutputFilter((itemStack, integer) -> false)
                .setSlotPosition(integer -> Pair.of(0, 18*integer))
                .setColorGuiEnabled(false)
        ;
        InvUtil.disableAllSidesAndEnable(this.memoryChipInput, state.getValue(RotatableBlock.FACING_HORIZONTAL), IFacingComponent.FaceMode.ENABLED, FacingUtil.Sideness.BOTTOM, FacingUtil.Sideness.BACK);
        this.addInventory((InventoryComponent<IdentificationChamberBlockEntity>) this.memoryChipInput);
        this.memoryChipOutput = (SidedInventoryComponent<?>) new SidedInventoryComponent<>("memoryChipOutput", 142, 48 - 18, 3, 0)
                .disableFacingAddon()
                .setInputFilter((itemStack, integer) -> false)
                .setOutputFilter((itemStack, integer) -> true)
                .setSlotPosition(integer -> Pair.of(0, 18*integer))
                .setColorGuiEnabled(false)
        ;
        InvUtil.disableAllSidesAndEnable(this.memoryChipOutput, state.getValue(RotatableBlock.FACING_HORIZONTAL), IFacingComponent.FaceMode.ENABLED, FacingUtil.Sideness.BOTTOM, FacingUtil.Sideness.BACK);
        this.addInventory((InventoryComponent<IdentificationChamberBlockEntity>) this.memoryChipOutput);
    }

    @Override
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(() -> new IdentificationChamberAddon(50, 30, this));
    }

    private void onFinish(){
        var input = this.getInput().getStackInSlot(0);
        if (!input.isEmpty()){
            for (int i = 0; i < this.memoryChipInput.getSlots(); i++) {
                var stack = this.memoryChipInput.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof IMatterPatternModifier<?> patternModifier){
                    IMatterPatternModifier.ModifierAction returnedValue = null;
                    if (input.is(ReplicationRegistry.Items.MATTER_BLUEPRINT.get()) && input.hasTag()){
                        var item = ItemStack.of(input.getTag().getCompound("Item"));
                        var progress = input.getTag().getDouble("Progress");
                        returnedValue = ((IMatterPatternModifier<ItemStack>)patternModifier).addPattern(stack, item, (float) progress);
                        input.shrink(1);
                        syncObject(this.input);
                    } else {
                        returnedValue = ((IMatterPatternModifier<ItemStack>)patternModifier).addPattern(stack, input,  0.2f);
                    }
                    if (returnedValue.getPattern() != null && returnedValue.getPattern().getCompletion() >= 1){
                        input.shrink(1);
                        syncObject(this.input);
                    }
                    if (returnedValue.getPattern() != null){
                        this.getEnergyStorage().extractEnergy((int) (25000*0.2f), false);
                    }
                    if (returnedValue.getType() == IMatterPatternModifier.ModifierType.FULL && returnedValue.getPattern() != null){
                        var exportingItem = stack.copy();
                        for (int z = 0; z < this.memoryChipOutput.getSlots(); z++) {
                            if (this.memoryChipOutput.getStackInSlot(z).isEmpty()){
                                this.memoryChipOutput.setStackInSlot(z, exportingItem);
                                stack.shrink(1);
                                return;
                            }
                        }
                    }
                    if (returnedValue.getPattern() != null) break;
                }
            }
        }
    }

    private boolean canIncrease(){
        if (this.getEnergyStorage().getEnergyStored() <= 25000*0.2f) return false;
        if (this.input.getStackInSlot(0).isEmpty()) return false;
        if (this.input.getStackInSlot(0).is(ReplicationTags.CANT_BE_SCANNED)) return false;
        var hasOutputSlot = false;
        for (int i = 0; i < this.memoryChipOutput.getSlots(); i++) {
            if (this.memoryChipOutput.getStackInSlot(i).isEmpty()){
                hasOutputSlot = true;
                break;
            }
        }
        if (!hasOutputSlot) return false;
        for (int i = 0; i < this.memoryChipInput.getSlots(); i++) {
            if (!this.memoryChipInput.getStackInSlot(i).isEmpty()){
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public IdentificationChamberBlockEntity getSelf() {
        return this;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, IdentificationChamberBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, IdentificationChamberBlockEntity blockEntity) {
        super.clientTick(level, pos, state, blockEntity);
    }

    public int getProgress() {
        if (this.progressBarComponent.getProgress() > MAX_PROGRESS){
            return MAX_PROGRESS * 2 - this.progressBarComponent.getProgress();
        }
        return this.progressBarComponent.getProgress();
    }

    public SidedInventoryComponent<?> getInput() {
        return input;
    }

    @Override
    public int getTitleColor() {
        return 0x72e567;
    }

    @Override
    public float getTitleYPos(float titleWidth, float screenWidth, float screenHeight, float guiWidth, float guiHeight) {
        return super.getTitleYPos(titleWidth, screenWidth, screenHeight, guiWidth, guiHeight) - 16;
    }
}
