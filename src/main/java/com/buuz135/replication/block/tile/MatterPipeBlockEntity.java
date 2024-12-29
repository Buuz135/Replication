package com.buuz135.replication.block.tile;

import com.buuz135.replication.ReplicationConfig;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MatterPipeBlockEntity extends NetworkBlockEntity<MatterPipeBlockEntity> {


    @Save
    private boolean needsToRecreateEnergyStorage;


    public MatterPipeBlockEntity(BasicTileBlock<MatterPipeBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.needsToRecreateEnergyStorage = false;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, MatterPipeBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);

        if (level.getGameTime() % 2 == 0){
            for (Direction value : Direction.values()) {
                var capability = this.level.getCapability(Capabilities.EnergyStorage.BLOCK, this.worldPosition.relative(value), value.getOpposite());
                var tile = this.level.getBlockEntity(this.worldPosition.relative(value));
                if (capability != null && !(tile instanceof MatterPipeBlockEntity) && this.getNetwork() != null){
                    var simulatedExtract = this.getNetwork().getEnergyStorage().extractEnergy(ReplicationConfig.MatterPipe.POWER_TRANSFER, true);
                    var realExtracted = capability.receiveEnergy(simulatedExtract, false);
                    this.getNetwork().getEnergyStorage().extractEnergy(realExtracted, false);
                }
            }
        }
    }

    @NotNull
    @Override
    public MatterPipeBlockEntity getSelf() {
        return this;
    }

    @Override
    protected NetworkElement createElement(Level level, BlockPos pos) {
        this.needsToRecreateEnergyStorage = true;
        return super.createElement(level, pos);
    }
}
