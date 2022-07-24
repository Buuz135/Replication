package com.buuz135.replication.network.matter;

import com.buuz135.replication.Replication;
import com.buuz135.replication.network.Network;
import com.buuz135.replication.network.NetworkFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class MatterNetwork extends Network {

    public static ResourceLocation MATTER = new ResourceLocation(Replication.MOD_ID, "matter");

    public MatterNetwork(BlockPos originPos, String id) {
        super(originPos, id);
    }

    @Override
    public void onMergedWith(Network mainNetwork) {

    }

    @Override
    public ResourceLocation getType() {
        return MATTER;
    }

    public static class Factory implements NetworkFactory {

        private static final Logger LOGGER = LogManager.getLogger(Factory.class);
        @Override
        public Network create(BlockPos pos) {
            return new MatterNetwork(pos, NetworkFactory.randomString(new Random(), 8));
        }

        @Override
        public Network create(CompoundTag tag) {
            MatterNetwork network = new MatterNetwork(BlockPos.of(tag.getLong("origin")), tag.getString("id"));

            LOGGER.debug("Deserialized matter network {} of type {}", network.getId(), network.getType().toString());

            return network;
        }
    }
}
