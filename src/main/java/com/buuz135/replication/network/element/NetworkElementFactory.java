package com.buuz135.replication.network.element;

import com.buuz135.replication.api.network.NetworkElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface NetworkElementFactory {
    NetworkElement createFromNbt(Level level, CompoundTag tag);
}
