package com.buuz135.replication.aequivaleo;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.IMatterType;
import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.ldtteam.aequivaleo.api.compound.type.group.ICompoundTypeGroup;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ReplicationCompoundType implements ICompoundType {

    private final IMatterType matterType;
    private final Supplier<ICompoundTypeGroup> typeGroupSupplier;
    private ResourceLocation registryName;

    public ReplicationCompoundType(IMatterType matterType, Supplier<ICompoundTypeGroup> typeGroupSupplier) {
        this.matterType = matterType;
        this.typeGroupSupplier = typeGroupSupplier;
        this.registryName = new ResourceLocation(Replication.MOD_ID, matterType.getName());
    }

    @Override
    public ICompoundTypeGroup getGroup() {
        return typeGroupSupplier.get();
    }

    public IMatterType getMatterType() {
        return matterType;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }
}
