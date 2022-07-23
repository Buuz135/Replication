package com.buuz135.replication.api;

import java.util.function.Supplier;

public interface IMatterType {

    String getName();

    Supplier<float[]> getColor();

    int getMax();
}
