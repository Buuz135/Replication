package net.minecraft.client.renderer.block.dispatch;

import com.mojang.math.Quadrant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record Variant(Identifier modelLocation, Variant.SimpleModelState modelState) implements BlockStateModelPart.Unbaked {
    public static final MapCodec<Variant> MAP_CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(Identifier.CODEC.fieldOf("model").forGetter(Variant::modelLocation), Variant.SimpleModelState.MAP_CODEC.forGetter(Variant::modelState))
            .apply(i, Variant::new)
    );
    public static final Codec<Variant> CODEC = MAP_CODEC.codec();

    public Variant(Identifier modelLocation) {
        this(modelLocation, Variant.SimpleModelState.DEFAULT);
    }

    public Variant withXRot(Quadrant x) {
        return this.withState(this.modelState.withX(x));
    }

    public Variant withYRot(Quadrant y) {
        return this.withState(this.modelState.withY(y));
    }

    public Variant withZRot(Quadrant z) {
        return this.withState(this.modelState.withZ(z));
    }

    public Variant withUvLock(boolean uvLock) {
        return this.withState(this.modelState.withUvLock(uvLock));
    }

    public Variant withModel(Identifier modelLocation) {
        return new Variant(modelLocation, this.modelState);
    }

    public Variant withState(Variant.SimpleModelState modelState) {
        return new Variant(this.modelLocation, modelState);
    }

    public Variant with(VariantMutator mutator) {
        return mutator.apply(this);
    }

    @Override
    public BlockStateModelPart bake(ModelBaker modelBakery) {
        return SimpleModelWrapper.bake(modelBakery, this.modelLocation, this.modelState.asModelState());
    }

    @Override
    public void resolveDependencies(ResolvableModel.Resolver resolver) {
        resolver.markDependency(this.modelLocation);
    }

    @OnlyIn(Dist.CLIENT)
    public record SimpleModelState(Quadrant x, Quadrant y, Quadrant z, boolean uvLock) {
        public static final MapCodec<Variant.SimpleModelState> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Quadrant.CODEC.optionalFieldOf("x", Quadrant.R0).forGetter(Variant.SimpleModelState::x),
                    Quadrant.CODEC.optionalFieldOf("y", Quadrant.R0).forGetter(Variant.SimpleModelState::y),
                    Quadrant.CODEC.optionalFieldOf("z", Quadrant.R0).forGetter(Variant.SimpleModelState::z),
                    Codec.BOOL.optionalFieldOf("uvlock", false).forGetter(Variant.SimpleModelState::uvLock)
                )
                .apply(i, Variant.SimpleModelState::new)
        );
        public static final Variant.SimpleModelState DEFAULT = new Variant.SimpleModelState(Quadrant.R0, Quadrant.R0, Quadrant.R0, false);

        public ModelState asModelState() {
            BlockModelRotation rotation = BlockModelRotation.get(Quadrant.fromXYZAngles(this.x, this.y, this.z));
            return (ModelState)(this.uvLock ? rotation.withUvLock() : rotation);
        }

        public Variant.SimpleModelState withX(Quadrant x) {
            return new Variant.SimpleModelState(x, this.y, this.z, this.uvLock);
        }

        public Variant.SimpleModelState withY(Quadrant y) {
            return new Variant.SimpleModelState(this.x, y, this.z, this.uvLock);
        }

        public Variant.SimpleModelState withZ(Quadrant z) {
            return new Variant.SimpleModelState(this.x, this.y, z, this.uvLock);
        }

        public Variant.SimpleModelState withUvLock(boolean uvLock) {
            return new Variant.SimpleModelState(this.x, this.y, this.z, uvLock);
        }
    }
}
