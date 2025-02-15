package com.buuz135.replication.api.matter_fluid.component;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import net.minecraft.world.inventory.ContainerData;

public class MatterTankReferenceHolder implements ContainerData {

    private final MatterTankComponent<?> matterTankComponent;
    private int matterAmount = -1;
    private int matterId = -1;

    public MatterTankReferenceHolder(MatterTankComponent<?> fluidTank) {
        this.matterTankComponent = fluidTank;
    }

    @Override
    public int get(int index) {
        MatterStack matterStack = this.matterTankComponent.getMatter();
        if (matterStack.isEmpty()) {
            return -1;
        } else if (index == 0) {
            return ReplicationRegistry.MATTER_TYPES_REGISTRY.getId(matterStack.getMatterType());
        } else {
            return matterStack.getAmount();
        }
    }

    @Override
    public void set(int index, int value) {
        if (index == 0) {
            matterId = value;
        } else {
            matterAmount = value;
        }

        if (matterAmount >= 0 && matterId >= 0) {
            matterTankComponent.setMatterStack(new MatterStack(ReplicationRegistry.MATTER_TYPES_REGISTRY.getHolder(matterId).get().value(), matterAmount));
        } else {
            matterTankComponent.setMatterStack(MatterStack.EMPTY);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

}
