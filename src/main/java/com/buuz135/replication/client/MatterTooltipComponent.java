package com.buuz135.replication.client;

import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class MatterTooltipComponent implements TooltipComponent {

    private final CompoundInstance instance;


    public MatterTooltipComponent(CompoundInstance instance) {
        this.instance = instance;
    }

    public CompoundInstance getInstance() {
        return instance;
    }
}
