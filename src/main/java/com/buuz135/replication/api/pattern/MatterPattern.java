package com.buuz135.replication.api.pattern;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class MatterPattern implements INBTSerializable<CompoundTag> {


    private ItemStack stack;
    private float completion;

    public MatterPattern() {
        this.stack = ItemStack.EMPTY;
        this.completion = 0;
    }

    public MatterPattern(ItemStack stack, float completion) {
        this.stack = stack;
        this.completion = completion;
    }

    public ItemStack getStack() {
        return stack;
    }

    public float getCompletion() {
        return completion;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public void setCompletion(float completion) {
        this.completion = completion;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Stack", this.stack.saveOptional(provider));
        compoundTag.putFloat("Completion", this.completion);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.stack = ItemStack.parseOptional(provider, nbt.getCompound("Stack"));
        this.completion = nbt.getFloat("Completion");
    }

    @Override
    public String toString() {
        return "MatterPattern{" +
                "stack=" + stack +
                ", completion=" + completion +
                '}';
    }
}
