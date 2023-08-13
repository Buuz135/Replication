package com.buuz135.replication.network.matter;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.network.IMatterStacksConsumer;
import com.buuz135.replication.api.network.IMatterStacksSupplier;
import com.buuz135.replication.api.network.NetworkElement;
import com.buuz135.replication.network.Network;
import com.buuz135.replication.network.NetworkFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.EnergyStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MatterNetwork extends Network {

    public static ResourceLocation MATTER = new ResourceLocation(Replication.MOD_ID, "matter");

    private EnergyStorage energyStorage;
    private List<Long> matterStacksConsumers;
    private List<Long> matterStacksSuppliers;
    private List<Long> matterStacksHolders;

    public MatterNetwork(BlockPos originPos, String id, int power) {
        super(originPos, id);
        this.energyStorage = new EnergyStorage(100_000, 100_000, 100_000, power);
        this.matterStacksConsumers = new ArrayList<>();
        this.matterStacksSuppliers = new ArrayList<>();
    }

    public void addElement(NetworkElement element){
        var tile = element.getLevel().getBlockEntity(element.getPos());
        if (tile instanceof IMatterStacksSupplier && tile instanceof IMatterStacksConsumer){
            this.matterStacksHolders.add(element.getPos().asLong());
        }else if (tile instanceof IMatterStacksSupplier){
            this.matterStacksSuppliers.add(element.getPos().asLong());
        }else if (tile instanceof IMatterStacksConsumer){
            this.matterStacksConsumers.add(element.getPos().asLong());
        }
    }

    public void removeElement(NetworkElement element){
        var tile = element.getLevel().getBlockEntity(element.getPos());
        if (tile instanceof IMatterStacksSupplier && tile instanceof IMatterStacksConsumer){
            this.matterStacksHolders.remove(element.getPos().asLong());
        }else if (tile instanceof IMatterStacksSupplier){
            this.matterStacksSuppliers.remove(element.getPos().asLong());
        }else if (tile instanceof IMatterStacksConsumer){
            this.matterStacksConsumers.remove(element.getPos().asLong());
        }
    }

    @Override
    public void update(Level level) {
        super.update(level);
    }

    @Override
    public void onMergedWith(Network mainNetwork) {
        if (mainNetwork instanceof MatterNetwork matterNetwork){
            matterNetwork.energyStorage.receiveEnergy(this.energyStorage.getEnergyStored(), false);
        }
    }

    @Override
    public CompoundTag writeToNbt(CompoundTag tag) {
        var nbt = super.writeToNbt(tag);
        nbt.putInt("Power", this.energyStorage.getEnergyStored());
        return nbt;
    }

    @Override
    public ResourceLocation getType() {
        return MATTER;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public static class Factory implements NetworkFactory {

        private static final Logger LOGGER = LogManager.getLogger(Factory.class);
        @Override
        public Network create(BlockPos pos) {
            return new MatterNetwork(pos, NetworkFactory.randomString(new Random(), 8), 0);
        }

        @Override
        public Network create(CompoundTag tag) {
            MatterNetwork network = new MatterNetwork(BlockPos.of(tag.getLong("origin")), tag.getString("id"), tag.getInt("Power"));

            LOGGER.debug("Deserialized matter network {} of type {}", network.getId(), network.getType().toString());

            return network;
        }
    }
}
