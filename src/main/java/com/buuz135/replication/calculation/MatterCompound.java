package com.buuz135.replication.calculation;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;

public class MatterCompound implements INBTSerializable<CompoundTag> {

    private final HashMap<IMatterType, MatterValue> values = new HashMap<>();
    private double cachedWeight = Integer.MAX_VALUE;

    public MatterCompound() {

    }

    public MatterCompound add(MatterValue value) {
        values.computeIfAbsent(value.getMatter(), iMatterType -> new MatterValue(iMatterType, 0)).add(value.getAmount());
        this.cachedWeight = getWeight();
        return this;
    }

    public MatterCompound add(MatterCompound other) {
        if (other != null) {
            for (MatterValue value : other.getValues().values()) {
                this.add(value);
            }
        }
        return this;
    }

    public MatterCompound compare(MatterCompound other) { //RETURNS WHICH ONE HAS LESS
        return other == null || getCachedWeight() < other.getCachedWeight() ? this : other;
    }

    public MatterCompound divide(double amount) {
        for (MatterValue value : values.values()) {
            value.setAmount(value.getAmount() / amount);
        }
        this.cachedWeight = getWeight();
        return this;
    }

    public double getWeight() {
        var amount = 0D;
        for (MatterValue value : this.values.values()) {
            amount += value.getAmount();
        }
        return amount;
    }

    public double getCachedWeight() {
        return cachedWeight;
    }

    public HashMap<IMatterType, MatterValue> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "MatterCompound{" +
                "values=" + values +
                '}';
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (MatterValue value : getValues().values()) {
            tag.put(value.getMatter().getName(), value.serializeNBT(provider));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        values.clear();
        for (String allKey : compoundTag.getAllKeys()) {
            var matterValue = new MatterValue(ReplicationRegistry.Matter.EMPTY.get(), 0);
            matterValue.deserializeNBT(provider, compoundTag.getCompound(allKey));
            values.put(matterValue.getMatter(), matterValue);
        }
    }
}