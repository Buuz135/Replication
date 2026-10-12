package com.buuz135.replication.calculation;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.hrznstudio.titanium.nbthandler.INBTSerializable;
import com.hrznstudio.titanium.network.CompoundSerializableDataHandler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class MatterValue implements INBTSerializable<CompoundTag> {

    public static final Codec<MatterValue> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(o -> ReplicationRegistry.MATTER_TYPES_REGISTRY.getKey(o.matter)),
                    Codec.DOUBLE.fieldOf("amount").forGetter(MatterValue::getAmount)
            ).apply(instance, MatterValue::new)));

    static {
        CompoundSerializableDataHandler.map(MatterValue.class, ByteBufCodecs.fromCodec(CODEC));
    }

    private IMatterType matter;
    private double amount;

    public MatterValue(Identifier matter, double amount) {
        this(ReplicationRegistry.MATTER_TYPES_REGISTRY.getValue(matter), amount);
    }

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
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("amount", amount);
        tag.putString("matter", ReplicationRegistry.MATTER_TYPES_REGISTRY.getKey(matter).toString());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        this.amount = compoundTag.getDoubleOr("amount", 0);
        this.matter = ReplicationRegistry.MATTER_TYPES_REGISTRY.getValue(Identifier.parse(compoundTag.getStringOr("matter", "")));
    }
}
