package com.buuz135.replication.api.pattern;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IMatterPatternModifier<T> {

    /**
     *
     * @param element
     * @param stack
     * @param progress
     * @return Null if it can't add more items to it
     */
    @Nullable ModifierAction addPattern(Level level, T element, ItemStack stack, float progress);


    public static class ModifierAction{

        public static ModifierAction isFull(MatterPattern pattern){
            return new ModifierAction(pattern, ModifierType.FULL);
        }
        public static ModifierAction canKeepAdding(MatterPattern pattern){
            return new ModifierAction(pattern, ModifierType.CAN_KEEP_ADDING);
        }

        private final MatterPattern pattern;
        private final ModifierType type;

        public ModifierAction(@Nullable MatterPattern pattern, ModifierType type) {
            this.pattern = pattern;
            this.type = type;
        }

        @Nullable
        public MatterPattern getPattern() {
            return pattern;
        }

        public ModifierType getType() {
            return type;
        }
    }

    public enum ModifierType{
        FULL,
        CAN_KEEP_ADDING
    }
}
