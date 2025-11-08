package com.buuz135.replication.integration.kubejs;

import com.buuz135.replication.api.IMatterType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MatterTypeCodecs {

    public static final Codec<IMatterType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("r", 1.0f).forGetter(mt -> mt.getColor().get()[0]),
            Codec.FLOAT.optionalFieldOf("g", 1.0f).forGetter(mt -> mt.getColor().get()[1]),
            Codec.FLOAT.optionalFieldOf("b", 1.0f).forGetter(mt -> mt.getColor().get()[2]),
            Codec.FLOAT.optionalFieldOf("a", 1.0f).forGetter(mt -> {
                float[] c = mt.getColor().get();
                return c.length >= 4 ? c[3] : 1.0f;
            }),
            Codec.INT.optionalFieldOf("max", 1000).forGetter(IMatterType::getMax),
            Codec.STRING.optionalFieldOf("name", "").forGetter(IMatterType::getName)
    ).apply(instance, (r, g, b, a, max, name) -> new CustomMatterType(name.isEmpty() ? "custom" : name, () -> new float[]{r, g, b, a}, max)));
}
