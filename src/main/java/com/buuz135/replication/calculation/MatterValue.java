package com.buuz135.replication.calculation;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class MatterValue implements INBTSerializable<CompoundTag> {

    private IMatterType matter;
    private double amount;

    public MatterValue(IMatterType matter, double amount) {
        this.matter = matter;
        this.amount = amount;
    }

    public MatterValue add(double amount) {
        this.amount += amount;
        return this;
    }

    public IMatterType getMatter() {
        return matter;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "MatterValue{" +
                "matter=" + matter +
                ", amount=" + amount +
                '}';
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("amount", amount);
        tag.putString("matter", ReplicationRegistry.MATTER_TYPES_REGISTRY.get().getKey(matter).toString());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        this.amount = compoundTag.getDouble("amount");
        this.matter = ReplicationRegistry.MATTER_TYPES_REGISTRY.get().getValue(new ResourceLocation(compoundTag.getString("matter")));
    }
}
