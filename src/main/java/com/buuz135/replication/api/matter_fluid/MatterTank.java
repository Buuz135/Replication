package com.buuz135.replication.api.matter_fluid;

import com.hrznstudio.titanium.nbthandler.data.NBTSerializableNBTHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class MatterTank implements IMatterHandler, IMatterTank, INBTSerializable<CompoundTag> {

    protected Predicate<MatterStack> validator;
    @NotNull
    protected MatterStack matterStack = MatterStack.EMPTY;
    protected int capacity;

    public MatterTank(int capacity) {
        this(capacity, e -> true);
    }

    public MatterTank(int capacity, Predicate<MatterStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    public MatterTank setValidator(Predicate<MatterStack> validator) {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isMatterValid(MatterStack stack) {
        return validator.test(stack);
    }

    public int getCapacity() {
        return capacity;
    }

    public MatterTank setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    @NotNull
    public MatterStack getMatter() {
        return matterStack;
    }

    public int getMatterAmount() {
        return matterStack.getAmount();
    }

    public MatterTank readFromNBT(CompoundTag nbt) {
        MatterStack fluid = MatterStack.loadFluidStackFromNBT(nbt);
        setMatter(fluid);
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        matterStack.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    public MatterStack getMatterInTank(int tank) {
        return getMatter();
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isMatterValid(int tank, @NotNull MatterStack stack) {
        return isMatterValid(stack);
    }

    @Override
    public int fill(MatterStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !isMatterValid(resource)) {
            return 0;
        }
        if (action.simulate()) {
            if (matterStack.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!matterStack.isMatterEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - matterStack.getAmount(), resource.getAmount());
        }
        if (matterStack.isEmpty()) {
            matterStack = new MatterStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return matterStack.getAmount();
        }
        if (!matterStack.isMatterEqual(resource)) {
            return 0;
        }
        int filled = capacity - matterStack.getAmount();

        if (resource.getAmount() < filled) {
            matterStack.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            matterStack.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    @NotNull
    @Override
    public MatterStack drain(MatterStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !resource.isMatterEqual(matterStack)) {
            return MatterStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @NotNull
    @Override
    public MatterStack drain(int maxDrain, IFluidHandler.FluidAction action) {
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

    protected void onContentsChanged() {

    }

    public void setMatter(MatterStack stack) {
        this.matterStack = stack;
    }

    public boolean isEmpty() {
        return matterStack.isEmpty();
    }

    public int getSpace() {
        return Math.max(0, capacity - matterStack.getAmount());
    }

    @Override
    public CompoundTag serializeNBT() {
        return writeToNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        readFromNBT(nbt);
    }
}
