package com.buuz135.replication.block.tile;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.component.MatterTankComponent;
import com.buuz135.replication.api.network.IMatterTanksConsumer;
import com.buuz135.replication.api.network.IMatterTanksSupplier;
import com.buuz135.replication.client.gui.ReplicationAddonProvider;
import com.buuz135.replication.container.component.LockableMatterTankBundle;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MatterTankBlockEntity extends NetworkBlockEntity<MatterTankBlockEntity> implements IMatterTanksSupplier, IMatterTanksConsumer {

    @Save
    private LockableMatterTankBundle<MatterTankBlockEntity> lockableMatterTankBundle;
    private IMatterType cachedType = MatterType.EMPTY;

    public MatterTankBlockEntity(BasicTileBlock<MatterTankBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.lockableMatterTankBundle = new LockableMatterTankBundle<>(this,
                new MatterTankComponent<MatterTankBlockEntity>("tank", 256000, 78, 28).setTankAction(FluidTankComponent.Action.BOTH).setOnContentChange(this::onTankContentChange),
                78 + 20, 28, false);
        this.addBundle(lockableMatterTankBundle);
        this.addMatterTank(this.lockableMatterTankBundle.getTank());
    }

    private void onTankContentChange(){
        syncObject(this.lockableMatterTankBundle);
        this.getNetwork().onTankValueChanged(cachedType);
        if (!cachedType.equals(this.lockableMatterTankBundle.getTank().getMatter().getMatterType())) {
            this.cachedType = this.lockableMatterTankBundle.getTank().getMatter().getMatterType();
            this.getNetwork().onTankValueChanged(cachedType);
        }
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
    public List<? extends IMatterTank> getTanks() {
        return this.getMatterTankComponents();
    }

    @NotNull
    @Override
    public MatterTankBlockEntity getSelf() {
        return this;
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
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        if (compound.contains("tank")) {
            this.lockableMatterTankBundle.getTank().deserializeNBT(provider, compound.getCompound("tank"));
        }
    }
}
