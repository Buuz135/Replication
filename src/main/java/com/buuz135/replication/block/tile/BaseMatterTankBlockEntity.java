package com.buuz135.replication.block.tile;

import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.component.MatterTankComponent;
import com.buuz135.replication.api.network.IMatterTanksConsumer;
import com.buuz135.replication.api.network.IMatterTanksSupplier;
import com.buuz135.replication.client.gui.ReplicationAddonProvider;
import com.buuz135.replication.client.gui.addons.MatterTankPriorityAddon;
import com.buuz135.replication.container.component.LockableMatterTankBundle;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.BooleanSupplier;

public abstract class BaseMatterTankBlockEntity<T extends BaseMatterTankBlockEntity<T>> extends NetworkBlockEntity<T> implements IMatterTanksSupplier, IMatterTanksConsumer {

    @Save
    private LockableMatterTankBundle<T> lockableMatterTankBundle;
    @Save
    private boolean voidExcess;
    @Save
    private int tankPriority;

    private ButtonComponent voidExcessButton;

    private IMatterType cachedType = MatterType.EMPTY;

    public BaseMatterTankBlockEntity(BasicTileBlock<T> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, BooleanSupplier isCreative) {
        super(base, blockEntityType, pos, state);
        this.lockableMatterTankBundle = new LockableMatterTankBundle<T>((T) this,
                new MatterTankComponent<T>("tank", ReplicationConfig.MatterTank.CAPACITY, 32, 28, this::isVoidExcess, isCreative).setTankAction(FluidTankComponent.Action.BOTH).setOnContentChange(this::onTankContentChange),
                32 - 16, 30, false);
        this.addBundle(lockableMatterTankBundle);
        this.addMatterTank(this.lockableMatterTankBundle.getTank());
        this.voidExcess = false;
        this.addButton(this.voidExcessButton = new ButtonComponent(32 + 20, 30, 14, 14).setId(234));
        this.tankPriority = 0;
    }

    private void onTankContentChange() {
        syncObject(this.lockableMatterTankBundle);
        this.getNetwork().onTankValueChanged(cachedType);
        if (!cachedType.equals(this.lockableMatterTankBundle.getTank().getMatter().getMatterType())) {
            this.cachedType = this.lockableMatterTankBundle.getTank().getMatter().getMatterType();
            this.getNetwork().onTankValueChanged(cachedType);
        }
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        if (id == 234) {
            this.voidExcess = !this.voidExcess;
            syncObject(this.voidExcess);
        }
        if (id == 124578) {
            this.tankPriority = compound.getInt("Priority");
            syncObject(this.tankPriority);
        }
        markComponentDirty();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        this.addGuiAddonFactory(() -> {
            return new StateButtonAddon(this.voidExcessButton,
                    new StateButtonInfo(0, AssetTypes.BUTTON_SIDENESS_DISABLED),
                    new StateButtonInfo(1, AssetTypes.BUTTON_SIDENESS_ENABLED)
            ) {
                @Override
                public int getState() {
                    return voidExcess ? 1 : 0;
                }
            };
        });
        this.addGuiAddonFactory(() -> new TextScreenAddon(Component.translatable("tooltip.replication.tank.void_excess").getString(), 32 + 20 + 16, 34, false, getTitleColor()));
        this.addGuiAddonFactory(() -> new MatterTankPriorityAddon(this, 32 + 20 + 1, 34 + 18));
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

    @Override
    public int getPriority() {
        return this.tankPriority;
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

    public boolean isVoidExcess() {
        return voidExcess;
    }

}
