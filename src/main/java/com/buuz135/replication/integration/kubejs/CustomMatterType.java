package com.buuz135.replication.integration.kubejs;

import com.buuz135.replication.api.IMatterType;

import java.util.function.Supplier;

public class CustomMatterType implements IMatterType {

    private final String name;
    private final Supplier<float[]> color;
    private final int max;

    public CustomMatterType(String name, Supplier<float[]> color, int max) {
        this.name = name;
        this.color = color;
        this.max = max;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Supplier<float[]> getColor() {
        return color;
    }

    @Override
    public int getMax() {
        return max;
    }
}
