package com.buuz135.replication.network;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.network.IMatterTanksConsumer;
import com.buuz135.replication.api.network.IMatterTanksSupplier;
import com.buuz135.replication.api.pattern.IMatterPatternHolder;
import com.buuz135.replication.api.pattern.MatterPattern;
import com.buuz135.replication.api.task.IReplicationTask;
import com.buuz135.replication.api.task.ReplicationTask;
import com.buuz135.replication.block.tile.ReplicationTerminalBlockEntity;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.buuz135.replication.network.task.ReplicationTaskManager;
import com.buuz135.replication.packet.MatterFluidSyncPacket;
import com.buuz135.replication.packet.PatternSyncStoragePacket;
import com.buuz135.replication.packet.TaskCancelPacket;
import com.buuz135.replication.packet.TaskSyncPacket;
import com.hrznstudio.titanium.block_network.Network;
import com.hrznstudio.titanium.block_network.NetworkFactory;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.block_network.NetworkRegistry;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

    private ReplicationTaskManager taskManager;

    //TODO Mark dirty

    public MatterNetwork(BlockPos originPos, String id, int power, ReplicationTaskManager taskManager) {
        super(originPos, id);
        this.energyStorage = new EnergyStorage(100_000, 100_000, 100_000, power);
        this.matterStacksConsumers = new ArrayList<>();
        this.matterStacksSuppliers = new ArrayList<>();
        this.matterStacksHolders = new ArrayList<>();
        this.queueNetworkElements = new ArrayList<>();
        this.chipSuppliers = new ArrayList<>();
        this.terminals = new ArrayList<>();
        this.taskManager = taskManager;
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
                    for (IMatterTank inputTank : supplier.getTanks()) {
                        if (inputTank.getMatter().isEmpty()) continue;
                        boolean didWork = false;
                        // WE SEARCH FOR HOLDER TANKS THAT HAVE SOMETHING FIRST
                        for (NetworkElement destinationElement : this.matterStacksHolders) {
                            var destination = destinationElement.getLevel().getBlockEntity(destinationElement.getPos());
                            if (destination instanceof IMatterTanksConsumer consumerDestination){
                                for (IMatterTank outputTank : consumerDestination.getTanks()) {
                                    if (outputTank.getMatter().isMatterEqual(inputTank.getMatter())) {
                                        inputTank.drain(outputTank.fill(inputTank.drain(1024*4, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                        didWork = true;
                                        break;
                                    }
                                }
                                if (didWork) break;
                            }
                        }
                        if (!didWork && !inputTank.getMatter().isEmpty()){
                            for (NetworkElement destinationElement : this.matterStacksHolders) {
                                var destination = destinationElement.getLevel().getBlockEntity(destinationElement.getPos());
                                if (destination instanceof IMatterTanksConsumer consumerDestination){
                                    for (IMatterTank outputTank : consumerDestination.getTanks()) {
                                        if (outputTank.getMatter().isEmpty()) {
                                            inputTank.drain(outputTank.fill(inputTank.drain(1024*4, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                            didWork = true;
                                            break;
                                        }
                                    }
                                    if (didWork) break;
                                }
                            }
                        }
                    }
                }
            }
        }
        this.getTaskManager().getPendingTasks().values().forEach(task -> {
            if (task.isDirty()){
                for (NetworkElement terminal : this.terminals) {
                    var tile = terminal.getLevel().getBlockEntity(terminal.getPos());
                    if (tile instanceof ReplicationTerminalBlockEntity terminalBlockEntity){
                        terminalBlockEntity.getTerminalPlayerTracker().getPlayers().forEach(serverPlayer -> this.sendTaskSyncPacket(serverPlayer, task));
                    }
                }
                task.markDirty(false);
            }
        });
    }

    private void transfer(Level level, IMatterTanksSupplier supplier, List<NetworkElement> consumers, BiPredicate<MatterStack, MatterStack> stackPredicate, boolean shouldBreakWhenFound){
        for (NetworkElement matterStacksHolder : consumers) {
            if (matterStacksHolder.getLevel() != level) continue;
            var destination = matterStacksHolder.getLevel().getBlockEntity(matterStacksHolder.getPos());
            if (destination instanceof IMatterTanksConsumer consumer){
                for (IMatterTank inputTank : supplier.getTanks()) {
                    if (inputTank.getMatter().isEmpty()) continue;
                    for (IMatterTank outputTank : consumer.getTanks()) {
                        if (stackPredicate.test(inputTank.getMatter(), outputTank.getMatter())){
                            inputTank.drain(outputTank.fill(inputTank.drain(1024*4, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                            if (shouldBreakWhenFound) return;
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

    public void onTaskValueChanged(IReplicationTask task, ServerLevel serverLevel) {
        task.markDirty(true);
        if (task.getTotalAmount() == task.getCurrentAmount()){
            for (NetworkElement terminal : this.terminals) {
                var tile = terminal.getLevel().getBlockEntity(terminal.getPos());
                if (tile instanceof ReplicationTerminalBlockEntity terminalBlockEntity){
                    terminalBlockEntity.getTerminalPlayerTracker().getPlayers().forEach(serverPlayer -> this.sendTaskSyncPacket(serverPlayer, task));
                }
            }
        }
        markDirty(serverLevel);
    }

    public void sendTaskSyncPacket(ServerPlayer serverPlayer, IReplicationTask task){
        Replication.NETWORK.get().sendTo(new TaskSyncPacket(this.getId(), task.getUuid().toString(), task.serializeNBT()), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
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

    public void markDirty(ServerLevel serverLevel){
        NetworkManager.get(serverLevel).setDirty(true);
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
            matterNetwork.taskManager.getPendingTasks().putAll(this.getTaskManager().getPendingTasks());
        }
    }

    @Override
    public CompoundTag writeToNbt(CompoundTag tag) {
        var nbt = super.writeToNbt(tag);
        nbt.putInt("Power", this.energyStorage.getEnergyStored());
        nbt.put("TaskManager", this.taskManager.serializeNBT());
        return nbt;
    }

    @Override
    public ResourceLocation getType() {
        return MATTER;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ReplicationTaskManager getTaskManager() {
        return taskManager;
    }

    public void cancelTask(String task, Level level) {
        var replicationTask = this.getTaskManager().getPendingTasks().remove(task);
        if (replicationTask != null){
            //WE CANCEL REPLICATOR TASKS
            for (Long l : replicationTask.getReplicatorsOnTask()) {
                var pos = BlockPos.of(l);
                if (level.getBlockEntity(pos) instanceof ReplicatorBlockEntity replicatorBlockEntity){
                    replicatorBlockEntity.cancelTask();
                }
            }
            //WE RETURN THE MATTER STORED
            for (List<MatterStack> valueList : replicationTask.getStoredMatterStack().values()) {
                for (MatterStack matterStack : valueList) {
                    for (NetworkElement matterStacksHolder : this.getMatterStacksHolders()) {
                        if (matterStack.isEmpty()) continue;
                        if (matterStacksHolder.getLevel() != level) continue;
                        var destination = matterStacksHolder.getLevel().getBlockEntity(matterStacksHolder.getPos());
                        if (destination instanceof IMatterTanksConsumer consumer){
                            for (IMatterTank outputTank : consumer.getTanks()) {
                                if (matterStack.isEmpty()) continue;
                                if (outputTank.getMatter().getMatterType().equals(matterStack.getMatterType())){
                                    matterStack.setAmount(matterStack.getAmount() - outputTank.fill(matterStack, IFluidHandler.FluidAction.EXECUTE));
                                }
                            }
                        }
                    }
                    if (!matterStack.isEmpty()){
                        for (NetworkElement matterStacksHolder : this.getMatterStacksHolders()) {
                            if (matterStack.isEmpty()) continue;
                            if (matterStacksHolder.getLevel() != level) continue;
                            var destination = matterStacksHolder.getLevel().getBlockEntity(matterStacksHolder.getPos());
                            if (destination instanceof IMatterTanksConsumer consumer){
                                for (IMatterTank outputTank : consumer.getTanks()) {
                                    if (matterStack.isEmpty()) continue;
                                    if (outputTank.getMatter().isEmpty()){
                                        matterStack.setAmount(matterStack.getAmount() - outputTank.fill(matterStack, IFluidHandler.FluidAction.EXECUTE));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (NetworkElement terminal : this.terminals) {
            var tile = terminal.getLevel().getBlockEntity(terminal.getPos());
            if (tile instanceof ReplicationTerminalBlockEntity terminalBlockEntity){
                terminalBlockEntity.getTerminalPlayerTracker().getPlayers().forEach(serverPlayer -> {
                    Replication.NETWORK.get().sendTo(new TaskCancelPacket.Response(task, this.getId()), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                });
            }
        }
    }

    public static class Factory implements NetworkFactory {

        private static final Logger LOGGER = LogManager.getLogger(Factory.class);
        @Override
        public Network create(BlockPos pos) {
            return new MatterNetwork(pos, NetworkFactory.randomString(new Random(), 8), 0, new ReplicationTaskManager());
        }

        @Override
        public Network create(CompoundTag tag) {
            var taskManager = new ReplicationTaskManager();
            taskManager.deserializeNBT(tag.getCompound("TaskManager"));
            MatterNetwork network = new MatterNetwork(BlockPos.of(tag.getLong("origin")), tag.getString("id"), tag.getInt("Power"), taskManager);

            LOGGER.debug("Deserialized matter network {} of type {}", network.getId(), network.getType().toString());

            return network;
        }
    }
}
