package com.buuz135.replication;

import com.google.common.base.Suppliers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ReplicationAttachments {
    public static final DeferredRegister<DataComponentType<?>> DR = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Replication.MOD_ID);

    public static final Supplier<DataComponentType<CompoundTag>> TANK_STORAGE = register("matter_tank", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC));
    public static final Supplier<DataComponentType<CompoundTag>> BLUEPRINT = register("blueprint", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC));
    public static final Supplier<DataComponentType<CompoundTag>> TILE = register("tile", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC));
    public static final Supplier<DataComponentType<CompoundTag>> CHIP_PATTERNS = register("chip_patterns", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC));



    private static <T> ComponentSupplier<T> register(String name, Supplier<T> defaultVal, UnaryOperator<DataComponentType.Builder<T>> op) {
        var registered = DR.register(name, () -> op.apply(DataComponentType.builder()).build());
        return new ComponentSupplier<>(registered, defaultVal);
    }

    public static class ComponentSupplier<T> implements Supplier<DataComponentType<T>> {
        private final Supplier<DataComponentType<T>> type;
        private final Supplier<T> defaultSupplier;

        public ComponentSupplier(Supplier<DataComponentType<T>> type, Supplier<T> defaultSupplier) {
            this.type = type;
            this.defaultSupplier = Suppliers.memoize(defaultSupplier::get);
        }

        public T get(ItemStack stack) {
            return stack.getOrDefault(type, defaultSupplier.get());
        }

        @Override
        public DataComponentType<T> get() {
            return type.get();
        }
    }
}