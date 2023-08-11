package com.buuz135.replication.block.tile;

import com.buuz135.replication.api.network.NetworkElement;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MatterPipeBlockEntity extends NetworkBlockEntity<MatterPipeBlockEntity> {


    @Save
    private boolean needsToRecreateEnergyStorage;

    private LazyOptional<EnergyStorage> energyStorageLazyOptional;

    public MatterPipeBlockEntity(BasicTileBlock<MatterPipeBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.energyStorageLazyOptional = LazyOptional.of(() -> this.getNetwork().getEnergyStorage());
        this.needsToRecreateEnergyStorage = false;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, MatterPipeBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (this.needsToRecreateEnergyStorage){
            this.energyStorageLazyOptional.invalidate();
            this.energyStorageLazyOptional = LazyOptional.of(() -> this.getNetwork().getEnergyStorage());
        }
    }

    @Override
    public boolean canConnect(Direction direction) {
        return true;
    }

    @NotNull
    @Override
    public MatterPipeBlockEntity getSelf() {
        return this;
    }

    @NotNull
    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY){
            return this.energyStorageLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyStorageLazyOptional.invalidate();
    }

    public LazyOptional<EnergyStorage> getEnergyStorageLazyOptional() {
        return energyStorageLazyOptional;
    }

    public void setEnergyStorageLazyOptional(LazyOptional<EnergyStorage> energyStorageLazyOptional) {
        this.energyStorageLazyOptional = energyStorageLazyOptional;
    }

    @Override
    protected NetworkElement createElement(Level level, BlockPos pos) {
        this.needsToRecreateEnergyStorage = true;
        return super.createElement(level, pos);
    }
}
