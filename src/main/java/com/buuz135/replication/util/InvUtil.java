package com.buuz135.replication.util;

import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.Direction;

public class InvUtil {

    public static void disableAllSidesAndEnable(SidedInventoryComponent sidedInventoryComponent, Direction currentDirection, IFacingComponent.FaceMode newMode, FacingUtil.Sideness... enabledSides){
        for (FacingUtil.Sideness value : FacingUtil.Sideness.values()) {
            sidedInventoryComponent.getFacingModes().put(value, IFacingComponent.FaceMode.NONE);
        }
        for (FacingUtil.Sideness enabledDirection : enabledSides) {
            sidedInventoryComponent.getFacingModes().put(enabledDirection, newMode);
        }
    }

}
