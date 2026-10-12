package com.buuz135.replication.compat.jade;

import com.buuz135.replication.block.tile.BaseMatterTankBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class MatterTankDataProvider implements IServerDataProvider<BlockAccessor> {

    @Override
    public Identifier getUid() {
        return JadeIds.MATTER_TANK;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        BaseMatterTankBlockEntity<?> blockEntity = (BaseMatterTankBlockEntity<?>) blockAccessor.getBlockEntity();
        compoundTag.put("MatterStack", blockEntity.getTanks().get(0).getMatter().writeToNBT(new CompoundTag()));
    }
}
