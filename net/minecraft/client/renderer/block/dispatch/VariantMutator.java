package net.minecraft.client.renderer.block.dispatch;

import com.mojang.math.Quadrant;
import java.util.function.UnaryOperator;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface VariantMutator extends UnaryOperator<Variant> {
    VariantMutator.VariantProperty<Quadrant> X_ROT = Variant::withXRot;
    VariantMutator.VariantProperty<Quadrant> Y_ROT = Variant::withYRot;
    VariantMutator.VariantProperty<Quadrant> Z_ROT = Variant::withZRot;
    VariantMutator.VariantProperty<Identifier> MODEL = Variant::withModel;
    VariantMutator.VariantProperty<Boolean> UV_LOCK = Variant::withUvLock;

    default VariantMutator then(VariantMutator other) {
        return variant -> other.apply(this.apply(variant));
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface VariantProperty<T> {
        Variant apply(Variant input, T value);

        default VariantMutator withValue(T value) {
            return variant -> this.apply(variant, value);
        }
    }
}
