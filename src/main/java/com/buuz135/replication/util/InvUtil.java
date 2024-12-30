package com.buuz135.replication.util;

import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class InvUtil {

    public static void disableAllSidesAndEnable(SidedInventoryComponent sidedInventoryComponent, Direction currentDirection, IFacingComponent.FaceMode newMode, FacingUtil.Sideness... enabledSides){
        for (FacingUtil.Sideness value : FacingUtil.Sideness.values()) {
            sidedInventoryComponent.getFacingModes().put(value, IFacingComponent.FaceMode.NONE);
        }
        for (FacingUtil.Sideness enabledDirection : enabledSides) {
            sidedInventoryComponent.getFacingModes().put(enabledDirection, newMode);
        }
    }

    public static boolean hasExtraComponents(ItemStack itemStack) {
        for (TypedDataComponent<?> component : itemStack.getComponents()) {
            if (!BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.type()).getNamespace().equals("minecraft")) {
                return true;
            }
        }
        return false;
    }

}
