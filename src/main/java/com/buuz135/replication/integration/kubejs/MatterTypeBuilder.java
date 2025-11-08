package com.buuz135.replication.integration.kubejs;

import com.buuz135.replication.api.IMatterType;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;

/**
 * KubeJS builder for Replication matter types.
 * Usage from scripts (StartupEvents.registry):
 * StartupEvents.registry('replication:matter_types', e => {
 * e.create('plasma').color(0.2, 0.7, 1.0, 1.0).max(10000);
 * });
 */
public class MatterTypeBuilder extends BuilderBase<IMatterType> {

    private float r = 1.0f;
    private float g = 1.0f;
    private float b = 1.0f;
    private float alpha = 1.0f;
    private int max = 1000;

    public MatterTypeBuilder(ResourceLocation id) {
        super(id);
    }

    public MatterTypeBuilder color(float r, float g, float b, float alpha) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.alpha = alpha;
        return this;
    }

    public MatterTypeBuilder max(int max) {
        this.max = max;
        return this;
    }

    @Override
    public IMatterType createObject() {
        // Use the path as the matter name (e.g., replication:plasma -> "plasma")
        String name = this.id.getPath();
        final float fr = r, fg = g, fb = b, fa = alpha;
        return new CustomMatterType(name, () -> new float[]{fr, fg, fb, fa}, max);
    }
}
