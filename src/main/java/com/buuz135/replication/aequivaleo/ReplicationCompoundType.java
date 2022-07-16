package com.buuz135.replication.aequivaleo;

import com.buuz135.replication.api.IMatterType;
import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.ldtteam.aequivaleo.api.compound.type.group.ICompoundTypeGroup;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Supplier;

public class ReplicationCompoundType extends ForgeRegistryEntry<ICompoundType> implements ICompoundType {

    private final IMatterType matterType;
    private final Supplier<ICompoundTypeGroup> typeGroupSupplier;

    public ReplicationCompoundType(IMatterType matterType, Supplier<ICompoundTypeGroup> typeGroupSupplier) {
        this.matterType = matterType;
        this.typeGroupSupplier = typeGroupSupplier;
    }


    @Override
    public ICompoundTypeGroup getGroup() {
        return typeGroupSupplier.get();
    }

    public IMatterType getMatterType() {
        return matterType;
    }
}
