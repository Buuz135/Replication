package com.buuz135.replication.block.tile;

import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.component.MatterTankComponent;
import com.buuz135.replication.api.network.IMatterTanksConsumer;
import com.buuz135.replication.api.network.IMatterTanksSupplier;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MatterTankBlockEntity extends NetworkBlockEntity<MatterTankBlockEntity> implements IMatterTanksSupplier, IMatterTanksConsumer {


    @Save
    private MatterTankComponent<MatterTankBlockEntity> tank;

    public MatterTankBlockEntity(BasicTileBlock<MatterTankBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.tank = new MatterTankComponent<MatterTankBlockEntity>("tank", 256000, 40 , 28).setTankAction(FluidTankComponent.Action.BOTH);
        this.addMatterTank(this.tank);
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
    public List<? extends IMatterTank> getTanks() {
        return this.getMatterTankComponents();
    }

    @NotNull
    @Override
    public MatterTankBlockEntity getSelf() {
        return this;
    }
}
