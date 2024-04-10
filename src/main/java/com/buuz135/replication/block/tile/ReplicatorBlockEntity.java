package com.buuz135.replication.block.tile;

import com.buuz135.replication.api.task.IReplicationTask;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.redstone.IRedstoneReader;
import com.hrznstudio.titanium.api.redstone.IRedstoneState;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.redstone.RedstoneAction;
import com.hrznstudio.titanium.block.redstone.RedstoneManager;
import com.hrznstudio.titanium.block.redstone.RedstoneState;
import com.hrznstudio.titanium.component.button.RedstoneControlButtonComponent;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.util.FacingUtil;
import com.hrznstudio.titanium.util.InventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class ReplicatorBlockEntity extends ReplicationMachine<ReplicatorBlockEntity> implements IRedstoneReader {

    public static final int MAX_PROGRESS = 200;
    public static final int POWER_TICK = 80;
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


    public ReplicatorBlockEntity(BasicTileBlock<ReplicatorBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.progress = MAX_PROGRESS;
        this.action = 1;
        this.craftingStack = ItemStack.EMPTY;
        this.progressBarComponent = new ProgressBarComponent<ReplicatorBlockEntity>(26, 25, 0, MAX_PROGRESS * 2)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
        addProgressBar(this.progressBarComponent);
        this.output = (SidedInventoryComponent<ReplicatorBlockEntity>) new SidedInventoryComponent<ReplicatorBlockEntity>("output", 42, 62, 7, 0)
                .setColor(DyeColor.ORANGE)
                .setInputFilter((stack, integer) -> false);
        addInventory(this.output);
        this.redstoneManager = new RedstoneManager<>(RedstoneAction.IGNORE, false);
        this.addButton(redstoneButton = new RedstoneControlButtonComponent<>(154, 84, 14, 14, () -> this.redstoneManager, () -> this));
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, ReplicatorBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (this.redstoneManager.getAction().canRun(this.getEnvironmentValue(false, null)) && this.redstoneManager.shouldWork()){
            tickProgress();
            this.progressBarComponent.setProgress(this.action == 1 ? MAX_PROGRESS - progress : MAX_PROGRESS + progress);
            syncObject(this.progressBarComponent);
            if (this.level.getGameTime() % 20 == 0 && this.craftingTask == null){
                var task = this.getNetwork().getTaskManager().findTaskForReplicator(this.getBlockPos());
                if (task != null){
                    task.acceptReplicator(this.getBlockPos());
                    this.craftingTask = task.getUuid().toString();
                    this.cachedReplicationTask = task;
                    this.craftingStack = task.getReplicatingStack();
                    syncObject(this.craftingStack);
                    this.getNetwork().onTaskValueChanged(task, (ServerLevel) this.level);
                }
            }
            if (this.level.getGameTime() % 20 == 0 && this.craftingTask != null && this.cachedReplicationTask == null
                    && this.getNetwork().getTaskManager().getPendingTasks().containsKey(this.craftingTask)){
                this.cachedReplicationTask = this.getNetwork().getTaskManager().getPendingTasks().get(this.craftingTask);
                this.craftingStack = this.cachedReplicationTask.getReplicatingStack();
                syncObject(this.craftingStack);
            }
            if (this.level.getGameTime() % 20 == 0 && this.craftingTask != null && this.cachedReplicationTask != null
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
        if (craftingTask != null && getEnergyStorage().getEnergyStored() > POWER_TICK && cachedReplicationTask != null
                && cachedReplicationTask.getStoredMatterStack().containsKey(this.getBlockPos().asLong())){
            getEnergyStorage().extractEnergy(POWER_TICK, false);
            if (this.action == 0){
                if (this.progress >= MAX_PROGRESS && ItemHandlerHelper.insertItem(this.output, this.craftingStack.copy(), true).isEmpty()){
                    this.action = 1;
                    syncObject(this.action);
                    replicateItem();
                } else {
                    ++this.progress;
                }
                syncObject(this.progress);
            }else{
                --this.progress;
                syncObject(this.progress);
                if (this.progress <= 0){
                    this.action = 0;
                    syncObject(this.action);
                }
            }
            markComponentDirty();
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
        var entity = this.level.getBlockEntity(this.cachedReplicationTask.getSource());
        if (entity != null){
            if (entity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).isPresent()){
                entity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(iItemHandler -> {
                    if (!ItemHandlerHelper.insertItem(iItemHandler, ItemHandlerHelper.copyStackWithSize(this.cachedReplicationTask.getReplicatingStack(), 1), false).isEmpty()){
                        ItemHandlerHelper.insertItem(this.output, ItemHandlerHelper.copyStackWithSize(this.cachedReplicationTask.getReplicatingStack(), 1), false) ;
                    }
                });
            } else {
                ItemHandlerHelper.insertItem(this.output, ItemHandlerHelper.copyStackWithSize(this.cachedReplicationTask.getReplicatingStack(), 1), false) ;
            }
        }
        this.cachedReplicationTask = null;
        this.craftingStack = ItemStack.EMPTY;
        this.craftingTask = null;
        this.redstoneManager.finish();
        syncObject(this.craftingStack);
    }

    public int getProgress() {
        return progress;
    }

    public int getAction() {
        return action;
    }

    public ItemStack getCraftingStack() {
        return craftingStack;
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
}
