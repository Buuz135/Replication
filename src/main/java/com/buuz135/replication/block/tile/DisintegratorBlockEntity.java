package com.buuz135.replication.block.tile;

import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.aequivaleo.ReplicationCompoundType;
import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.matter_fluid.component.MatterTankComponent;
import com.buuz135.replication.api.network.IMatterTanksSupplier;
import com.buuz135.replication.client.gui.addons.DisintegratorAddon;
import com.buuz135.replication.client.gui.addons.IdentificationChamberAddon;
import com.buuz135.replication.util.InvUtil;
import com.buuz135.replication.util.ReplicationTags;
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
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class DisintegratorBlockEntity extends ReplicationMachine<DisintegratorBlockEntity> implements IMatterTanksSupplier {

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
        this.input = (SidedInventoryComponent<?>) new SidedInventoryComponent<>("input", 29, 30, 3, 0)
                .disableFacingAddon()
                .setInputFilter((itemStack, integer) -> !IAequivaleoAPI.getInstance().getEquivalencyResults(this.level.dimension()).dataFor(itemStack).isEmpty())
                .setOutputFilter((itemStack, integer) -> false)
                .setSlotLimit(64)
                .setSlotPosition(integer -> Pair.of(0, 18*integer))
                .setOnSlotChanged((stack, integer) -> syncObject(this.input))
                .setColorGuiEnabled(false)
        ;
        InvUtil.disableAllSidesAndEnable(this.input, state.getValue(RotatableBlock.FACING_HORIZONTAL), IFacingComponent.FaceMode.ENABLED, FacingUtil.Sideness.BOTTOM, FacingUtil.Sideness.BACK, FacingUtil.Sideness.TOP);
        this.addInventory((InventoryComponent<DisintegratorBlockEntity>) input);

        this.progressBarComponent = new ProgressBarComponent<>(48, 28, ReplicationConfig.Disintegrator.MAX_PROGRESS)
                .setCanIncrease(iComponentHarness -> queuedMatterStacks.isEmpty())
                .setOnTickWork(() -> {
                    syncObject(this.progressBarComponent);
                })
                .setCanIncrease(iComponentHarness -> {
                    if (this.getEnergyStorage().getEnergyStored() < ReplicationConfig.Disintegrator.POWER_USAGE) return false;
                    for (int i = 0; i < this.input.getSlots(); i++) {
                        var stack = this.input.getStackInSlot(i);
                        if (!stack.isEmpty()) {return true;}
                    }
                    return false;
                })
                .setOnFinishWork(this::onFinish)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP);

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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(() -> new DisintegratorAddon(50, 30, this));
    }

    private void onFinish() {
        for (int i = 0; i < this.input.getSlots(); i++) {
            var stack = this.input.getStackInSlot(i);
            if (!stack.isEmpty() && !stack.is(ReplicationTags.CANT_BE_DISINTEGRATED) && this.getEnergyStorage().getEnergyStored() >= ReplicationConfig.Disintegrator.POWER_USAGE){
                var data = IAequivaleoAPI.getInstance().getEquivalencyResults(this.level.dimension()).dataFor(stack);
                for (CompoundInstance datum : data) {
                    if (datum.getType() instanceof ReplicationCompoundType replicationCompoundType){
                        queuedMatterStacks.add(new MatterStack(replicationCompoundType.getMatterType(), Mth.ceil(datum.getAmount())));
                    }
                }
                stack.shrink(1);
                this.getEnergyStorage().extractEnergy(ReplicationConfig.Disintegrator.POWER_USAGE, false);
            }
        }
        syncObject(this.input);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, DisintegratorBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (!this.queuedMatterStacks.isEmpty()){
            var peekedElement = this.queuedMatterStacks.peek();
            for (MatterTankComponent<DisintegratorBlockEntity> matterTankComponent : this.getMatterTankComponents()) {
                if (!matterTankComponent.isEmpty() && matterTankComponent.getMatter().isMatterEqual(peekedElement) && matterTankComponent.fillForced(peekedElement, IFluidHandler.FluidAction.SIMULATE) <= peekedElement.getAmount()){
                    peekedElement.setAmount(peekedElement.getAmount() - matterTankComponent.fillForced(peekedElement, IFluidHandler.FluidAction.EXECUTE));
                    if (peekedElement.isEmpty()){
                        this.queuedMatterStacks.poll();
                        return;
                    }
                }
            }
            if (!peekedElement.isEmpty()){
                for (MatterTankComponent<DisintegratorBlockEntity> matterTankComponent : this.getMatterTankComponents()) {
                    if (matterTankComponent.isEmpty()){
                        peekedElement.setAmount(peekedElement.getAmount() - matterTankComponent.fillForced(peekedElement, IFluidHandler.FluidAction.EXECUTE));
                        if (peekedElement.isEmpty()){
                            this.queuedMatterStacks.poll();
                            return;
                        }
                    }
                }
            }
        }
    }

    private MatterTankComponent<DisintegratorBlockEntity> createMatterTank(int index){
        return new MatterTankComponent<DisintegratorBlockEntity>("tank"+index, 16000, 42 + index * 18 , 28).setTankAction(FluidTankComponent.Action.DRAIN);
    }

    @NotNull
    @Override
    public DisintegratorBlockEntity getSelf() {
        return this;
    }

    @Override
    public List<? extends IMatterTank> getTanks() {
        return this.getMatterTankComponents();
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
