package com.buuz135.replication.network.element;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class NetworkElementRegistry {

    public static final NetworkElementRegistry INSTANCE = new NetworkElementRegistry();

    private final Map<ResourceLocation, NetworkElementFactory> factories = new HashMap<>();

    private NetworkElementRegistry() {
    }

    public void addFactory(ResourceLocation id, NetworkElementFactory factory) {
        if (factories.containsKey(id)) {
            throw new RuntimeException("Cannot register duplicate pipe factory " + id.toString());
        }

        factories.put(id, factory);
    }

    @Nullable
    public NetworkElementFactory getFactory(ResourceLocation id) {
        return factories.get(id);
    }

}
