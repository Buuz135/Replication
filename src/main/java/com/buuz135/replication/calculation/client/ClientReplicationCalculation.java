package com.buuz135.replication.calculation.client;

import com.buuz135.replication.calculation.MatterCompound;
import com.buuz135.replication.calculation.ReplicationCalculation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientReplicationCalculation {

    public static HashMap<String, MatterCompound> DEFAULT_MATTER_COMPOUND = new HashMap<String, MatterCompound>();

    @Nullable
    public static MatterCompound getMatterCompound(ItemStack stack) {
        if (DEFAULT_MATTER_COMPOUND.containsKey(ReplicationCalculation.getNameFromStack(stack))){
            return DEFAULT_MATTER_COMPOUND.get(ReplicationCalculation.getNameFromStack(stack));
        }
        return null;
    }

    public static void acceptData(CompoundTag compoundTag){
        DEFAULT_MATTER_COMPOUND.clear();
        for (String allKey : compoundTag.getAllKeys()) {
            var matter =  new MatterCompound();
            matter.deserializeNBT(compoundTag.getCompound(allKey));
            DEFAULT_MATTER_COMPOUND.put(allKey, matter);
        }
    }
}
