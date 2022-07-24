package com.buuz135.replication.network.element.type;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.network.NetworkElement;
import com.buuz135.replication.network.element.NetworkElementFactory;
import com.buuz135.replication.network.matter.MatterNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class DefaultMatterNetworkElement extends NetworkElement {

    public static final ResourceLocation ID = new ResourceLocation(Replication.MOD_ID, "default_matter");

    public DefaultMatterNetworkElement(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getNetworkType() {
        return MatterNetwork.MATTER;
    }

    public static class Factory implements NetworkElementFactory {

        @Override
        public NetworkElement createFromNbt(Level level, CompoundTag tag) {
            BlockPos pos = BlockPos.of(tag.getLong("pos"));
            DefaultMatterNetworkElement element = new DefaultMatterNetworkElement(level, pos);
            return element;
        }
    }
}
