package com.buuz135.replication.client;

import com.buuz135.replication.calculation.MatterCompound;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.Set;

public class MatterTooltipComponent implements TooltipComponent {

    private final MatterCompound instance;


    public MatterTooltipComponent(MatterCompound instance) {
        this.instance = instance;
    }

    public MatterCompound getInstance() {
        return instance;
    }
}
