package com.buuz135.replication.api;

public enum MatterType implements IMatterType{
    METALLIC("metallic"),
    EARTH("earth"),
    NETHER("nether"),
    ORGANIC("organic"),
    ENDER("ender"),
    PRECIOUS("precious"),
    QUANTUM("quantum");

    ;

    private final String name;


    MatterType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
