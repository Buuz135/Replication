package com.buuz135.replication.network.matter;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.network.IMatterTanksConsumer;
import com.buuz135.replication.api.network.IMatterTanksSupplier;
import com.buuz135.replication.api.pattern.IMatterPatternHolder;
import com.buuz135.replication.api.pattern.MatterPattern;
import com.buuz135.replication.block.tile.ChipStorageBlockEntity;
import com.buuz135.replication.block.tile.ReplicationTerminalBlockEntity;
import com.buuz135.replication.packet.MatterFluidSyncPacket;
import com.buuz135.replication.packet.PatternSyncStoragePacket;
import com.hrznstudio.titanium.block_network.Network;
import com.hrznstudio.titanium.block_network.NetworkFactory;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.NetworkDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;

public class MatterNetwork extends Network {

    public static ResourceLocation MATTER = new ResourceLocation(Replication.MOD_ID, "matter");

    private EnergyStorage energyStorage;
    private List<NetworkElement> matterStacksConsumers;
    private List<NetworkElement> matterStacksSuppliers;
    private List<NetworkElement> matterStacksHolders;
    private List<NetworkElement> queueNetworkElements;
    private List<NetworkElement> chipSuppliers;
    private List<NetworkElement> terminals;

    public MatterNetwork(BlockPos originPos, String id, int power) {
        super(originPos, id);
        this.energyStorage = new EnergyStorage(100_000, 100_000, 100_000, power);
        this.matterStacksConsumers = new ArrayList<>();
        this.matterStacksSuppliers = new ArrayList<>();
        this.matterStacksHolders = new ArrayList<>();
        this.queueNetworkElements = new ArrayList<>();
        this.chipSuppliers = new ArrayList<>();
        this.terminals = new ArrayList<>();
    }

    public void addElement(NetworkElement element){
        this.queueNetworkElements.add(element);
    }

    public void removeElement(NetworkElement element){
        var tile = element.getLevel().getBlockEntity(element.getPos());
        if (tile instanceof IMatterTanksSupplier && tile instanceof IMatterTanksConsumer){
            this.matterStacksHolders.remove(element);
        }else if (tile instanceof IMatterTanksSupplier){
            this.matterStacksSuppliers.remove(element);
        }else if (tile instanceof IMatterTanksConsumer){
            this.matterStacksConsumers.remove(element);
        } else if (tile instanceof IMatterPatternHolder<?>) {
            this.chipSuppliers.remove(element);
        } else if (tile instanceof ReplicationTerminalBlockEntity){
            this.terminals.remove(element);
        }
    }

    @Override
    public void update(Level level) {
        super.update(level);
        for (NetworkElement element : this.queueNetworkElements) {
            var tile = element.getLevel().getBlockEntity(element.getPos());
            if (tile instanceof IMatterTanksSupplier && tile instanceof IMatterTanksConsumer){
                this.matterStacksHolders.add(element);
            }else if (tile instanceof IMatterTanksSupplier){
                this.matterStacksSuppliers.add(element);
            }else if (tile instanceof IMatterTanksConsumer){
                this.matterStacksConsumers.add(element);
            }else if (tile instanceof IMatterPatternHolder<?>) {
                this.chipSuppliers.add(element);
            }else if (tile instanceof ReplicationTerminalBlockEntity){
                this.terminals.add(element);
            }
        }
        this.queueNetworkElements.clear();
        if (level.getGameTime() % 5 == 0){
            for (NetworkElement matterStacksSupplier : this.matterStacksSuppliers) {
                if (matterStacksSupplier.getLevel() != level) continue;
                var origin = matterStacksSupplier.getLevel().getBlockEntity(matterStacksSupplier.getPos());
                if (origin instanceof IMatterTanksSupplier supplier){
                    // WE SEARCH FOR HOLDER TANKS THAT HAVE SOMETHING FIRST
                    transfer(level, supplier, this.matterStacksHolders, (input, output) -> output.isMatterEqual(input));
                    // WE SEARCH FOR HOLDER TANKS THAT ARE EMPTY
                    transfer(level, supplier, this.matterStacksHolders, (input, output) -> output.isEmpty());
                }
            }
        }
    }

    private void transfer(Level level, IMatterTanksSupplier supplier, List<NetworkElement> consumers, BiPredicate<MatterStack, MatterStack> stackPredicate){
        for (NetworkElement matterStacksHolder : consumers) {
            if (matterStacksHolder.getLevel() != level) continue;
            var destination = matterStacksHolder.getLevel().getBlockEntity(matterStacksHolder.getPos());
            if (destination instanceof IMatterTanksConsumer consumer){
                for (IMatterTank inputTank : supplier.getTanks()) {
                    if (inputTank.getMatter().isEmpty()) continue;
                    for (IMatterTank outputTank : consumer.getTanks()) {
                        if (stackPredicate.test(inputTank.getMatter(), outputTank.getMatter())){
                            inputTank.drain(outputTank.fill(inputTank.drain(1024, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            }
        }
    }

    public void onChipValuesChanged(IMatterPatternHolder blockEntity, BlockPos pos){
        for (NetworkElement terminal : this.terminals) {
            var tile = terminal.getLevel().getBlockEntity(terminal.getPos());
            if (tile instanceof ReplicationTerminalBlockEntity terminalBlockEntity){
                terminalBlockEntity.getTerminalPlayerTracker().getPlayers().forEach(serverPlayer -> this.sendPatternSyncPacket(serverPlayer, blockEntity, pos));
            }
        }
    }

    public void onTankValueChanged(IMatterType matterType){
        for (NetworkElement terminal : this.terminals) {
            var tile = terminal.getLevel().getBlockEntity(terminal.getPos());
            if (tile instanceof ReplicationTerminalBlockEntity terminalBlockEntity){
                terminalBlockEntity.getTerminalPlayerTracker().getPlayers().forEach(serverPlayer -> this.sendMatterSyncPacket(serverPlayer, calculateMatterAmount(matterType), matterType));
            }
        }
    }

    public long calculateMatterAmount(IMatterType matterType){
        var amount = 0L;
        for (NetworkElement matterStacksSupplier : this.getMatterStacksHolders()) {
            var tile = matterStacksSupplier.getLevel().getBlockEntity(matterStacksSupplier.getPos());
            if (tile instanceof IMatterTanksSupplier tanksSupplier){
                for (IMatterTank tank : tanksSupplier.getTanks()) {
                    if (tank.getMatter().getMatterType().equals(matterType)){
                        amount += tank.getMatterAmount();
                    }
                }
            }
        }
        return amount;
    }

    public void sendPatternSyncPacket(ServerPlayer serverPlayer, IMatterPatternHolder holder, BlockPos blockPos){
        List<MatterPattern> patterns = (holder).getPatterns(holder);
        Replication.NETWORK.get().sendTo(new PatternSyncStoragePacket(this.getId(), blockPos.asLong(),
                patterns.stream().map(MatterPattern::getStack).toList()), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void sendMatterSyncPacket(ServerPlayer serverPlayer, long amount, IMatterType type){
        Replication.NETWORK.get().sendTo(new MatterFluidSyncPacket(this.getId(), amount, ReplicationRegistry.MATTER_TYPES_REGISTRY.get().getKey(type)), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public List<NetworkElement> getChipSuppliers() {
        return chipSuppliers;
    }

    public List<NetworkElement> getMatterStacksSuppliers() {
        return matterStacksSuppliers;
    }

    public List<NetworkElement> getMatterStacksConsumers() {
        return matterStacksConsumers;
    }

    public List<NetworkElement> getMatterStacksHolders() {
        return matterStacksHolders;
    }

    public List<NetworkElement> getTerminals() {
        return terminals;
    }

    @Override
    public void onMergedWith(Network mainNetwork) {
        if (mainNetwork instanceof MatterNetwork matterNetwork){
            matterNetwork.energyStorage.receiveEnergy(this.energyStorage.getEnergyStored(), false);
            //TODO MERGE TANKS
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
