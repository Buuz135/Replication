package com.buuz135.replication.api.pattern;

import net.minecraft.world.level.Level;

import java.util.List;

public interface IMatterPatternHolder<T> {


    int getPatternSlots(T element);

    List<MatterPattern> getPatterns(Level level, T element);

}
