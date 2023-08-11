package com.buuz135.replication.api.pattern;

import java.util.List;

public interface IMatterPatternHolder<T> {


    int getPatternSlots(T element);

    List<MatterPattern> getPatterns(T element);

}
