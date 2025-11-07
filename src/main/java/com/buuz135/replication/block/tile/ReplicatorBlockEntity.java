package com.buuz135.replication.block.tile;

import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.api.MatterCalculationStatus;
import com.buuz135.replication.api.task.IReplicationTask;
import com.buuz135.replication.api.task.ReplicationTask;
import com.buuz135.replication.block.ReplicatorBlock;
import com.buuz135.replication.calculation.ReplicationCalculation;
import com.buuz135.replication.client.gui.addons.ReplicatorCraftingAddon;
import com.buuz135.replication.client.gui.addons.ReplicatorMotorAddon;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.filter.FilterSlot;
import com.hrznstudio.titanium.api.redstone.IRedstoneReader;
import com.hrznstudio.titanium.api.redstone.IRedstoneState;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.redstone.RedstoneAction;
import com.hrznstudio.titanium.block.redstone.RedstoneManager;
import com.hrznstudio.titanium.block.redstone.RedstoneState;
import com.hrznstudio.titanium.client.screen.addon.ItemstackFilterScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.button.RedstoneControlButtonComponent;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.filter.ItemStackFilter;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReplicatorBlockEntity extends ReplicationMachine<ReplicatorBlockEntity> implements IRedstoneReader {


    public static final float LOWER_PROGRESS = 0.563f;

    @Save
    private int progress;
    @Save
    private int action;
    @Save
    private ProgressBarComponent<ReplicatorBlockEntity> progressBarComponent;
    @Save
    private SidedInventoryComponent<ReplicatorBlockEntity> output;
    @Save
    private String craftingTask;
    @Save
    private ItemStack craftingStack;
    private IReplicationTask cachedReplicationTask;
    @Save
    private RedstoneManager<RedstoneAction> redstoneManager;
    private RedstoneControlButtonComponent<RedstoneAction> redstoneButton;
    @Save
    private ItemStackFilter infiniteCrafting;
    private boolean hasEnclosure;
    private boolean hasMotor;
    @Save
    private int motorSpeedMultiplier;
    @Save
    private boolean isCurrentTaskAFailure;


    public ReplicatorBlockEntity(BasicTileBlock<ReplicatorBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.progress = ReplicationConfig.Replicator.MAX_PROGRESS;
        this.action = 1;
        this.hasEnclosure = false;
        this.hasMotor = false;
        this.motorSpeedMultiplier = 100;
        this.isCurrentTaskAFailure = false;
        this.craftingStack = ItemStack.EMPTY;
        this.progressBarComponent = new ProgressBarComponent<ReplicatorBlockEntity>(26, 25, 0, ReplicationConfig.Replicator.MAX_PROGRESS * 2)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP);
        addProgressBar(this.progressBarComponent);
        this.output = (SidedInventoryComponent<ReplicatorBlockEntity>) new SidedInventoryComponent<ReplicatorBlockEntity>("output", 42, 63, 7, 0)
                .setColor(0xdea83c)
                .setInputFilter((stack, integer) -> false)
                .setColorGuiEnabled(false);
        addInventory(this.output);
        this.redstoneManager = new RedstoneManager<>(RedstoneAction.IGNORE, false);
        this.addButton(redstoneButton = new RedstoneControlButtonComponent<>(154, 84, 14, 14, () -> this.redstoneManager, () -> this));
//        this.infiniteCrafting = new ItemStackFilter("infiniteCrafting", 1);
        this.infiniteCrafting = new ItemStackFilter("infiniteCrafting", 1){

            @Override
            public void setFilter(int slot, ItemStack stack) {
                super.setFilter(slot, stack.getItem().getDefaultInstance());
            }

            @OnlyIn(Dist.CLIENT)
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                List<IFactory<? extends IScreenAddon>> list = new ArrayList();
                list.add(() -> {
                    return new ItemstackFilterScreenAddon(this){
                        @Override
                        public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
                            for (FilterSlot<ItemStack> filterSlot : infiniteCrafting.getFilterSlots()) {
                                if (filterSlot != null) {
                                    AssetUtil.drawAsset(guiGraphics, screen, Objects.requireNonNull(provider.getAsset(AssetTypes.SLOT)), guiX + filterSlot.getX(), guiY + filterSlot.getY());
                                    RenderSystem.setShaderColor(1, 1, 1, 1);
                                    if (!filterSlot.getFilter().isEmpty()) {
                                        Lighting.setupFor3DItems(); //enableGUIStandarItemLightning
                                        guiGraphics.renderItem(filterSlot.getFilter(), filterSlot.getX() + guiX + 1, filterSlot.getY() + guiY + 1);
                                    }
                                }
                            }
                        }
                    };
                });
                return list;
            }
        };
        this.infiniteCrafting.getFilterSlots()[0] = new FilterSlot<>(43, 28, 0, ItemStack.EMPTY);
        addFilter(infiniteCrafting);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(() -> new ReplicatorCraftingAddon(50, 30, this));
        addGuiAddonFactory(() -> new ReplicatorMotorAddon(this, 7, 184));
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, ReplicatorBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (this.level.getGameTime() % 20 == 0) {
            var maxProgress = ReplicationConfig.Replicator.MAX_PROGRESS * 2;
            this.hasEnclosure = state.getValue(ReplicatorBlock.HAS_ENCLOSURE);
            this.hasMotor = state.getValue(ReplicatorBlock.HAS_MOTOR);
            if (this.hasEnclosure) {
                maxProgress *= 0.80;
            }
            if (this.hasMotor) {
                maxProgress *= (this.motorSpeedMultiplier / 100D);
            }
            this.progressBarComponent.setMaxProgress(maxProgress);
            if (this.progress > this.getMaxProgress()) {
                this.progress = this.getMaxProgress();
            }
            syncObject(this.progressBarComponent);
            syncObject(this.progress);
        }
        if (getNetwork() == null) return;
        if (ReplicationCalculation.STATUS != MatterCalculationStatus.CALCULATED) return;
        if (this.redstoneManager.getAction().canRun(this.getEnvironmentValue(false, null)) && this.redstoneManager.shouldWork()){
            tickProgress();
            this.progressBarComponent.setProgress(this.action == 1 ? this.getMaxProgress() - progress : this.getMaxProgress() + progress);
            syncObject(this.progressBarComponent);
            if (this.level.getGameTime() % 4 == 0 && this.craftingTask == null && this.cachedReplicationTask == null && !this.infiniteCrafting.getFilterSlots()[0].getFilter().isEmpty()) {
                var task = new ReplicationTask(this.infiniteCrafting.getFilterSlots()[0].getFilter().copy(), 1, IReplicationTask.Mode.SINGLE, this.getBlockPos(), true);
                task.acceptReplicator(this.getBlockPos());
                this.isCurrentTaskAFailure = this.level.getRandom().nextInt(100) < getFailureChance();
                this.craftingTask = task.getUuid().toString();
                this.cachedReplicationTask = task;
                this.craftingStack = task.getReplicatingStack();
                syncObject(this.craftingStack);
                syncObject(this.isCurrentTaskAFailure);
                this.getNetwork().getTaskManager().getPendingTasks().put(task.getUuid().toString(), task);
                this.getNetwork().onTaskValueChanged(task, (ServerLevel) this.level);
            }
            if (this.level.getGameTime() % 4 == 0 && this.craftingTask == null) {
                var task = this.getNetwork().getTaskManager().findTaskForReplicator(this.getBlockPos(), this.getNetwork());
                if (task != null){
                    task.acceptReplicator(this.getBlockPos());
                    this.isCurrentTaskAFailure = this.level.getRandom().nextInt(100) < getFailureChance();
                    this.craftingTask = task.getUuid().toString();
                    this.cachedReplicationTask = task;
                    this.craftingStack = task.getReplicatingStack();
                    syncObject(this.craftingStack);
                    syncObject(this.isCurrentTaskAFailure);
                    this.getNetwork().onTaskValueChanged(task, (ServerLevel) this.level);
                }
            }
            if (this.level.getGameTime() % 4 == 0 && this.craftingTask != null && this.cachedReplicationTask == null) {
                if (this.getNetwork().getTaskManager().getPendingTasks().containsKey(this.craftingTask)) {
                    this.cachedReplicationTask = this.getNetwork().getTaskManager().getPendingTasks().get(this.craftingTask);
                    this.craftingStack = this.cachedReplicationTask.getReplicatingStack();
                    syncObject(this.craftingStack);
                } else {
                    cancelTask();
                }
            }
            if (this.level.getGameTime() % 4 == 0 && this.craftingTask != null && this.cachedReplicationTask != null
                    && !this.cachedReplicationTask.getStoredMatterStack().containsKey(this.getBlockPos().asLong())){
                this.cachedReplicationTask.storeMatterStacksFor(this.level, this.getBlockPos(), this.getNetwork());
            }
        }
    }

    @NotNull
    @Override
    public ReplicatorBlockEntity getSelf() {
        return this;
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, ReplicatorBlockEntity blockEntity) {
        super.clientTick(level, pos, state, blockEntity);
    }

    private void tickProgress(){
        if (craftingTask != null && getEnergyStorage().getEnergyStored() > this.getPowerConsumption() && cachedReplicationTask != null
                && cachedReplicationTask.getStoredMatterStack().containsKey(this.getBlockPos().asLong())){
            if (this.action == 0){
                if (this.progress >= this.getMaxProgress()) {
                    if (!this.isCurrentTaskAFailure) {
                        if (ItemHandlerHelper.insertItem(this.output, this.craftingStack.copy(), true).isEmpty()) {
                            this.action = 1;
                            syncObject(this.action);
                            replicateItem();
                        }
                    } else {
                        this.isCurrentTaskAFailure = this.level.getRandom().nextInt(100) < getFailureChance();
                        this.action = 1;
                        syncObject(this.action);
                        syncObject(this.isCurrentTaskAFailure);
                    }
                } else {
                    getEnergyStorage().extractEnergy(this.getPowerConsumption(), false);
                    ++this.progress;
                }
                syncObject(this.progress);
            }else{
                --this.progress;
                getEnergyStorage().extractEnergy(this.getPowerConsumption(), false);
                syncObject(this.progress);
                if (this.progress <= 0){
                    this.action = 0;
                    syncObject(this.action);
                }
            }
            markComponentDirty();
        }
        if (craftingTask == null && this.progress < this.getMaxProgress()) {
            this.action = 1;
            ++this.progress;
            syncObject(this.action);
            syncObject(this.progress);
        }
    }

    @NotNull
    @Override
    protected EnergyStorageComponent<ReplicatorBlockEntity> createEnergyStorage() {
        return new EnergyStorageComponent<>(25000, 7, 25);
    }

    private void replicateItem(){
        this.cachedReplicationTask.finalizeReplication(this.level, this.getBlockPos(), this.getNetwork());
        this.getNetwork().onTaskValueChanged(this.cachedReplicationTask, (ServerLevel) this.level);
        if (!this.getBlockPos().equals(this.cachedReplicationTask.getSource())){
            var capability = this.level.getCapability(Capabilities.ItemHandler.BLOCK, this.cachedReplicationTask.getSource(), Direction.UP);
                if (capability != null){
                    if (!ItemHandlerHelper.insertItem(capability, this.cachedReplicationTask.getReplicatingStack().copyWithCount(1), false).isEmpty()) {
                        ItemHandlerHelper.insertItem(this.output, this.cachedReplicationTask.getReplicatingStack().copyWithCount(1), false);
                    }
                } else {
                    ItemHandlerHelper.insertItem(this.output, this.cachedReplicationTask.getReplicatingStack().copyWithCount(1), false);
                }
        }else {
            ItemHandlerHelper.insertItem(this.output, this.cachedReplicationTask.getReplicatingStack().copyWithCount(1), false);
        }
        this.cachedReplicationTask = null;
        this.craftingStack = ItemStack.EMPTY;
        this.craftingTask = null;
        this.redstoneManager.finish();
        syncObject(this.craftingStack);
    }

    @Override
    public void setRemoved() {
        if (this.cachedReplicationTask != null) {
            this.cachedReplicationTask.getReplicatorsOnTask().remove(this.getBlockPos().asLong());
            if (this.cachedReplicationTask.getReplicatorsOnTask().isEmpty()) {
                this.getNetwork().cancelTask(this.craftingTask, this.level);
            }
        }
        super.setRemoved();
    }

    public void cancelTask(){
        this.cachedReplicationTask = null;
        this.craftingStack = ItemStack.EMPTY;
        this.craftingTask = null;
        this.redstoneManager.finish();
        syncObject(this.craftingStack);
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return progressBarComponent.getMaxProgress() / 2;
    }

    public int getPowerConsumption() {
        var power = ReplicationConfig.Replicator.POWER_TICK;
        if (this.hasEnclosure) power = (int) Math.ceil(power * ReplicationConfig.Replicator.ENCLOSURE_POWER_MULTIPLIER);
        return power;
    }

    public boolean hasMotor() {
        return hasMotor;
    }

    public boolean hasEnclosure() {
        return hasEnclosure;
    }

    public int getMotorSpeedMultiplier() {
        return motorSpeedMultiplier;
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        if (id == 124578) {
            motorSpeedMultiplier = compound.getInt("Multiplier");
            if (motorSpeedMultiplier > 100) {
                motorSpeedMultiplier = 100;
            }
            if (motorSpeedMultiplier < 20) {
                motorSpeedMultiplier = 20;
            }
            syncObject(motorSpeedMultiplier);
        }
    }

    public int getFailureChance() {
        int value = getMotorSpeedMultiplier();
        int oldMin = 20;
        int oldMax = 100;
        int newMin = 0;
        int newMax = 50;

        // Clamp value within [20, 100] to avoid unexpected output
        if (value < oldMin) value = oldMin;
        if (value > oldMax) value = oldMax;

        // Inverted scale: 100 → 0, 20 → 50
        int scaled = (int) Math.floor((oldMax - value) * (newMax - newMin) / (double) (oldMax - oldMin) + newMin);
        return scaled;
    }

    public boolean isCurrentTaskAFailure() {
        return isCurrentTaskAFailure;
    }

    public int getAction() {
        return action;
    }

    public ItemStack getCraftingStack() {
        return craftingStack;
    }

    public boolean isInfinite() {
        return !infiniteCrafting.getFilterSlots()[0].getFilter().isEmpty();
    }

    @Override
    public IRedstoneState getEnvironmentValue(boolean strongPower, Direction direction) {
        if (strongPower) {
            if (direction == null) {
                return this.level.hasNeighborSignal(this.worldPosition) ? RedstoneState.ON : RedstoneState.OFF;
            }
            return this.level.hasSignal(this.worldPosition, direction) ? RedstoneState.ON : RedstoneState.OFF;
        } else {
            return this.level.getBestNeighborSignal(this.worldPosition) > 0 ? RedstoneState.ON : RedstoneState.OFF;
        }
    }

    @Override
    public void onNeighborChanged(Block blockIn, BlockPos fromPos) {
        super.onNeighborChanged(blockIn, fromPos);
        redstoneManager.setLastRedstoneState(this.getEnvironmentValue(false, null).isReceivingRedstone());
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
