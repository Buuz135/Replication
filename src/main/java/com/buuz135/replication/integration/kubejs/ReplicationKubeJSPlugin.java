package com.buuz135.replication.integration.kubejs;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.registry.ServerRegistryRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class ReplicationKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("Replication", new ReplicationKubeJSGateway());
    }

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        // Allow: StartupEvents.registry('replication:matter_types', e => e.create('id').color(r,g,b).max(n))
        registry.addDefault(ReplicationRegistry.MATTER_TYPES_KEY, MatterTypeBuilder.class, MatterTypeBuilder::new);
    }

    @Override
    public void registerServerRegistries(ServerRegistryRegistry registry) {
        // Provide a direct codec to allow JSON-based registration if scripts use createFromJson
        registry.register(ReplicationRegistry.MATTER_TYPES_KEY, MatterTypeCodecs.DIRECT_CODEC, IMatterType.class);
    }
}
