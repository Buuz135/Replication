package com.buuz135.replication.block.tile;

import com.buuz135.replication.aequivaleo.ReplicationCompoundType;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.matter_fluid.component.MatterTankComponent;
import com.buuz135.replication.util.InvUtil;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.util.FacingUtil;
import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class DisintegratorBlockEntity extends ReplicationMachine<DisintegratorBlockEntity>{

    @Save
    private SidedInventoryComponent<?> input;
    @Save
    private ProgressBarComponent<?> progressBarComponent;
    @Save
    private MatterTankComponent<DisintegratorBlockEntity> tank1;
    @Save
    private MatterTankComponent<DisintegratorBlockEntity> tank2;
    @Save
    private MatterTankComponent<DisintegratorBlockEntity> tank3;
    @Save
    private MatterTankComponent<DisintegratorBlockEntity> tank4;
    @Save
    private MatterTankComponent<DisintegratorBlockEntity> tank5;
    @Save
    private MatterTankComponent<DisintegratorBlockEntity> tank6;

    private Queue<MatterStack> queuedMatterStacks;

    public DisintegratorBlockEntity(BasicTileBlock<DisintegratorBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.queuedMatterStacks = new ArrayDeque<>();
        this.input = (SidedInventoryComponent<?>) new SidedInventoryComponent<>("input", 28, 30, 3, 0)
                .setColor(DyeColor.BLUE)
                .disableFacingAddon()
                .setInputFilter((itemStack, integer) -> !IAequivaleoAPI.getInstance().getEquivalencyResults(this.level.dimension()).dataFor(itemStack).isEmpty())
                .setOutputFilter((itemStack, integer) -> false)
                .setSlotLimit(1)
                .setSlotPosition(integer -> Pair.of(0, 18*integer))
                .setOnSlotChanged((stack, integer) -> syncObject(this.input))
        ;
        InvUtil.disableAllSidesAndEnable(this.input, state.getValue(RotatableBlock.FACING_HORIZONTAL), IFacingComponent.FaceMode.ENABLED, FacingUtil.Sideness.BOTTOM, FacingUtil.Sideness.BACK);
        this.addInventory((InventoryComponent<DisintegratorBlockEntity>) input);

        this.progressBarComponent = new ProgressBarComponent<>(47, 28, 20 * 2)
                .setCanIncrease(iComponentHarness -> queuedMatterStacks.isEmpty())
                .setOnTickWork(() -> {
                    syncObject(this.progressBarComponent);
                })
                .setOnFinishWork(this::onFinish)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);

        this.addProgressBar((ProgressBarComponent<DisintegratorBlockEntity>) this.progressBarComponent);

        this.tank1 = this.createMatterTank(1);
        this.tank2 = this.createMatterTank(2);
        this.tank3 = this.createMatterTank(3);
        this.tank4 = this.createMatterTank(4);
        this.tank5 = this.createMatterTank(5);
        this.tank6 = this.createMatterTank(6);

        this.addMatterTank(this.tank1);
        this.addMatterTank(this.tank2);
        this.addMatterTank(this.tank3);
        this.addMatterTank(this.tank4);
        this.addMatterTank(this.tank5);
        this.addMatterTank(this.tank6);
    }

    private void onFinish() {
        for (int i = 0; i < this.input.getSlots(); i++) {
            var stack = this.input.getStackInSlot(i);
            var data = IAequivaleoAPI.getInstance().getEquivalencyResults(this.level.dimension()).dataFor(stack);
            for (CompoundInstance datum : data) {
                if (datum.getType() instanceof ReplicationCompoundType replicationCompoundType){
                    queuedMatterStacks.add(new MatterStack(replicationCompoundType.getMatterType(), Mth.floor(datum.getAmount())));
                    stack.shrink(1);
                }
            }
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, DisintegratorBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (!this.queuedMatterStacks.isEmpty()){
            var peekedElement = this.queuedMatterStacks.peek();
            for (MatterTankComponent<DisintegratorBlockEntity> matterTankComponent : this.getMatterTankComponents()) {
                if (!matterTankComponent.isEmpty() && matterTankComponent.getMatter().isMatterEqual(peekedElement) && matterTankComponent.fillForced(peekedElement, IFluidHandler.FluidAction.SIMULATE) == peekedElement.getAmount()){
                    matterTankComponent.fillForced(peekedElement, IFluidHandler.FluidAction.EXECUTE);
                    this.queuedMatterStacks.poll();
                    return;
                }
            }
            for (MatterTankComponent<DisintegratorBlockEntity> matterTankComponent : this.getMatterTankComponents()) {
                if (matterTankComponent.isEmpty()){
                    matterTankComponent.fillForced(peekedElement, IFluidHandler.FluidAction.EXECUTE);
                    this.queuedMatterStacks.poll();
                    return;
                }
            }
        }
    }

    private MatterTankComponent<DisintegratorBlockEntity> createMatterTank(int index){
        return new MatterTankComponent<DisintegratorBlockEntity>("tank"+index, 16000, 40 + index*19 , 28).setTankAction(FluidTankComponent.Action.DRAIN);
    }

    @Override
    public boolean canConnect(Direction direction) {
        return true;
    }

    @NotNull
    @Override
    public DisintegratorBlockEntity getSelf() {
        return this;
    }
}
