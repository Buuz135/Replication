package com.buuz135.replication.api.matter_fluid.component;

import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.matter_fluid.MatterTank;
import com.buuz135.replication.api.matter_fluid.client.MatterTankScreenAddon;
import com.google.common.collect.Lists;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.container.addon.IContainerAddonProvider;
import com.hrznstudio.titanium.container.addon.IntArrayReferenceHolderAddon;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MatterTankComponent<T extends IComponentHarness> extends MatterTank implements IScreenAddonProvider,
        IContainerAddonProvider, INBTSerializable<CompoundTag> {

    private final int posX;
    private final int posY;
    private String name;
    private T componentHarness;
    private FluidTankComponent.Type tankType;
    private FluidTankComponent.Action tankAction;
    private Runnable onContentChange;

    public MatterTankComponent(String name, int amount, int posX, int posY) {
        super(amount);
        this.posX = posX;
        this.posY = posY;
        this.name = name;
        this.tankType = FluidTankComponent.Type.NORMAL;
        this.tankAction = FluidTankComponent.Action.BOTH;
        this.onContentChange = () -> {
        };
    }

    /**
     * Sets the tile to be automatically marked dirty when the contents change
     *
     * @param componentHarness The tile where the tank is
     * @return itself
     */
    public MatterTankComponent<T> setComponentHarness(T componentHarness) {
        this.componentHarness = componentHarness;
        return this;
    }

    public T getComponentHarness() {
        return componentHarness;
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        if (componentHarness != null) {
            //componentHarness.markComponentForUpdate(true);
        }
        onContentChange.run();
    }

    public String getName() {
        return name;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public FluidTankComponent.Type getTankType() {
        return tankType;
    }

    public MatterTankComponent<T> setTankType(FluidTankComponent.Type tankType) {
        this.tankType = tankType;
        return this;
    }

    public MatterTankComponent<T> setOnContentChange(Runnable onContentChange) {
        this.onContentChange = onContentChange;
        return this;
    }

    public FluidTankComponent.Action getTankAction() {
        return tankAction;
    }

    public MatterTankComponent<T> setTankAction(FluidTankComponent.Action tankAction) {
        this.tankAction = tankAction;
        return this;
    }

    @Override
    public int fill(MatterStack resource, IFluidHandler.FluidAction action) {
        return getTankAction().canFill() ? super.fill(resource, action) : 0;
    }

    @Nonnull
    @Override
    public MatterStack drain(MatterStack resource, IFluidHandler.FluidAction action) {
        return getTankAction().canDrain() ? drainInternal(resource, action) : MatterStack.EMPTY;
    }

    private MatterStack drainInternal(MatterStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !resource.isMatterEqual(matterStack)) {
            return MatterStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public MatterStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return getTankAction().canDrain() ? drainInternal(maxDrain, action) : MatterStack.EMPTY;
    }

    @Nonnull
    private MatterStack drainInternal(int maxDrain, IFluidHandler.FluidAction action) {
        int drained = maxDrain;
        if (matterStack.getAmount() < drained) {
            drained = matterStack.getAmount();
        }
        MatterStack stack = new MatterStack(matterStack, drained);
        if (action.execute() && drained > 0) {
            matterStack.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    public int fillForced(MatterStack resource, IFluidHandler.FluidAction action) {
        return super.fill(resource, action);
    }

    @Nonnull
    public MatterStack drainForced(MatterStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !resource.isMatterEqual(matterStack)) {
            return MatterStack.EMPTY;
        }
        return drainForced(resource.getAmount(), action);
    }

    @Nonnull
    public MatterStack drainForced(int maxDrain, IFluidHandler.FluidAction action) {
        return drainInternal(maxDrain, action);
    }

    public void setMatterStack(MatterStack MatterStack) {
        this.matterStack = MatterStack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> addons = new ArrayList<>();
        addons.add(() -> new MatterTankScreenAddon(posX, posY, this, tankType));
        return addons;
    }

    @Override
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        return Lists.newArrayList(
                //() -> new IntArrayReferenceHolderAddon(new MatterTankReferenceHolder(this))
        );
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return this.writeToNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.readFromNBT(nbt);
    }

    public Runnable getOnContentChange() {
        return onContentChange;
    }
}

