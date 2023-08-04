package com.buuz135.replication.api;

import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

public enum MatterType implements IMatterType{

    EMPTY("empty", new float[]{01f, 1f, 1f,1f}, 1),
    METALLIC("metallic", new float[]{0.75f, 0.75f,0.75f,1f}, 128),

    EARTH("earth", new float[]{58/256f, 148/256f, 6/256f,1f}, 16),
    NETHER("nether", new float[]{173/256f, 3/256f, 32/256f,1f}, 32),
    ORGANIC("organic", new float[]{207/256f, 103/256f, 0, 1f}, 16),
    ENDER("ender", new float[]{0, 130/256f, 87/256f,1f}, 32),
    PRECIOUS("precious", new float[]{237/256f, 222/256f, 121/256f,1f}, 128),
    LIVING("living", new float[]{232/256f, 23/256f, 197/256f, 1f}, 32),
    QUANTUM("quantum", () -> {
        if (Minecraft.getInstance().level != null){
            return new float[]{181/256f, 132/256f, 227/256f, Minecraft.getInstance().level.random.nextDouble() < 0.95 ? (float) (Math.sin((Minecraft.getInstance().level.getGameTime() % 300) / 30f) + 1.25 )*0.5f : 1f};
        }
        return new float[]{181/256f, 132/256f, 227/256f,1f};
    }, 64);

    ;

    private final String name;
    private final Supplier<float[]> color;
    private final int max;


    MatterType(String name, float[] color, int max) {
        this(name, () -> color, max);
    }

    MatterType(String name, Supplier<float[]> color, int max){
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
