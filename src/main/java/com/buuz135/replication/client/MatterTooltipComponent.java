package com.buuz135.replication.client;

import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.Set;

public class MatterTooltipComponent implements TooltipComponent {

    private final CompoundInstance[] instance;


    public MatterTooltipComponent(Set<CompoundInstance> instance) {
        this.instance = instance.toArray(new CompoundInstance[]{});
    }

    public CompoundInstance[] getInstance() {
        return instance;
    }
}
