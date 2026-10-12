package com.buuz135.replication.api.task;

import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.network.IMatterTanksSupplier;
import com.buuz135.replication.calculation.MatterCompound;
import com.buuz135.replication.calculation.MatterValue;
import com.buuz135.replication.calculation.ReplicationCalculation;
import com.buuz135.replication.network.MatterNetwork;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import com.hrznstudio.titanium.util.ItemStackSerialization;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.*;

public class ReplicationTask implements IReplicationTask {

    private UUID uuid;
    private ItemStack crafting;
    private int currentAmount;
    private int totalAmount;
    private HashMap<Long, List<MatterStack>> matterStacks;
    private Mode mode;
    private List<Long> replicatorsOnTask;
    private BlockPos source;
    private boolean dirty;
    private boolean isInfinteMode;

    public ReplicationTask(ItemStack crafting, int totalAmount, Mode mode, BlockPos source, boolean isInfinteMode) {
        this.crafting = crafting;
        this.totalAmount = totalAmount;
        this.mode = mode;
        this.uuid = UUID.randomUUID();
        this.currentAmount = 0;
        this.matterStacks = new HashMap<>();
        this.replicatorsOnTask = new ArrayList<>();
        this.source = source;
        this.dirty = false;
        this.isInfinteMode = isInfinteMode;
    }

    @Override
    public ItemStack getReplicatingStack() {
        return this.crafting;
    }

    @Override
    public int getCurrentAmount() {
        return this.currentAmount;
    }

    @Override
    public int getTotalAmount() {
        return this.totalAmount;
    }

    @Override
    public HashMap<Long, List<MatterStack>> getStoredMatterStack() {
        return this.matterStacks;
    }

    @Override
    public Mode getMode() {
        return this.mode;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public List<Long> getReplicatorsOnTask() {
        return this.replicatorsOnTask;
    }

    @Override
    public BlockPos getSource() {
        return this.source;
    }

    @Override
    public boolean canAcceptReplicator(BlockPos replicator, int maxReplicatorsInMultipleMode) {
        return (this.mode == Mode.SINGLE && this.replicatorsOnTask.isEmpty()) ||
                (this.mode == Mode.MULTIPLE && this.replicatorsOnTask.size() < maxReplicatorsInMultipleMode && (this.getTotalAmount() - this.getCurrentAmount() - this.replicatorsOnTask.size()) > 0);
    }

    @Override
    public void acceptReplicator(BlockPos replicator) {
        this.replicatorsOnTask.add(replicator.asLong());
    }

    @Override
    public void storeMatterStacksFor(Level level, BlockPos pos, MatterNetwork matterNetwork) {
        var data = ReplicationCalculation.getMatterCompound(this.getReplicatingStack());
        List<MatterStack> matterStackList = new ArrayList<>();
        if (data != null && checkHasEnough(data, level, pos, matterNetwork)) {
            for (MatterValue matterValue : data.getValues().values()) {
                var type = matterValue.getMatter();
                var amount = Mth.ceil(matterValue.getAmount());
                var listOfHolders = matterNetwork.getMatterStacksHolders().stream()
                        .filter(element -> element.getLevel().isLoaded(element.getPos()))
                        .filter(element -> element.getLevel().getBlockEntity(element.getPos()) instanceof IMatterTanksSupplier consumer)
                        .map(element -> (IMatterTanksSupplier) element.getLevel().getBlockEntity(element.getPos()))
                        .sorted(Comparator.comparingInt(IMatterTanksSupplier::getPriority))
                        .toList();
                for (IMatterTanksSupplier tanksSupplier : listOfHolders) {
                    for (IMatterTank tank : tanksSupplier.getTanks()) {
                        if (!tank.getMatter().isEmpty() && tank.getMatter().getMatterType().equals(type)) {
                            var drained = tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                            amount -= drained.getAmount();
                            if (amount <= 0) {
                                break;
                            }
                        }
                    }
                    if (amount <= 0) {
                        break;
                    }
                }
                matterStackList.add(new MatterStack(type, (int) Math.ceil(matterValue.getAmount())));
            }
            this.getStoredMatterStack().put(pos.asLong(), matterStackList);
        }
    }

    @Override
    public void finalizeReplication(Level level, BlockPos pos, MatterNetwork matterNetwork) {
        ++this.currentAmount;
        this.matterStacks.remove(pos.asLong());
        this.replicatorsOnTask.remove(pos.asLong());
        if (this.currentAmount >= this.totalAmount) {
            matterNetwork.getTaskManager().getPendingTasks().remove(this.getUuid().toString());
        }
    }

    @Override
    public void markDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    private boolean checkHasEnough(MatterCompound data, Level level, BlockPos pos, MatterNetwork matterNetwork) {
        for (MatterValue matterValue : data.getValues().values()) {
            var type = matterValue.getMatter();
            var amount = (int) Math.ceil(matterValue.getAmount());
            for (NetworkElement matterStacksSupplier : matterNetwork.getMatterStacksHolders()) {
                var tile = matterStacksSupplier.getLevel().getBlockEntity(matterStacksSupplier.getPos());
                if (tile instanceof IMatterTanksSupplier tanksSupplier) {
                    for (IMatterTank tank : tanksSupplier.getTanks()) {
                        if (!tank.getMatter().isEmpty() && tank.getMatter().getMatterType().equals(type)) {
                            var drained = tank.drain(amount, IFluidHandler.FluidAction.SIMULATE);
                            amount -= drained.getAmount();
                            if (amount <= 0) {
                                break;
                            }
                        }
                    }
                }
                if (amount <= 0) {
                    break;
                }
            }
            if (amount > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isInfinteMode() {
        return isInfinteMode;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Crafting", ItemStackSerialization.save(provider, this.crafting));
        compoundTag.putInt("Current", this.currentAmount);
        compoundTag.putInt("Total", this.totalAmount);
        CompoundTag matterStacksCompound = new CompoundTag();
        for (Long l : this.matterStacks.keySet()) {
            CompoundTag list = new CompoundTag();
            for (int i = 0; i < this.matterStacks.get(l).size(); i++) {
                list.put(i + "", this.matterStacks.get(l).get(i).writeToNBT(new CompoundTag()));
            }
            matterStacksCompound.put(l + "", list);
        }
        compoundTag.put("MatterStacks", matterStacksCompound);
        compoundTag.putString("Mode", this.mode.name());
        compoundTag.store("UUID", UUIDUtil.CODEC, this.uuid);
        compoundTag.putLongArray("OnTask", this.replicatorsOnTask.stream().mapToLong(Long::longValue).toArray());
        compoundTag.putLong("Source", this.source.asLong());
        compoundTag.putBoolean("Infinte", isInfinteMode);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        this.crafting = ItemStackSerialization.load(provider, compoundTag.getCompoundOrEmpty("Crafting"));
        this.currentAmount = compoundTag.getIntOr("Current", 0);
        this.totalAmount = compoundTag.getIntOr("Total", 0);
        this.matterStacks = new HashMap<>();
        CompoundTag matterStacksCompound = compoundTag.getCompoundOrEmpty("MatterStacks");
        for (String allKey : matterStacksCompound.keySet()) {
            CompoundTag list = matterStacksCompound.getCompoundOrEmpty(allKey);
            List<MatterStack> matterStackList = new ArrayList<>();
            for (String key : list.keySet()) {
                matterStackList.add(MatterStack.loadMatterStackFromNBT(list.getCompoundOrEmpty(key)));
            }
            this.matterStacks.put(Long.parseLong(allKey), matterStackList);
        }
        this.mode = Mode.valueOf(compoundTag.getStringOr("Mode", Mode.SINGLE.name()));
        this.uuid = compoundTag.read("UUID", UUIDUtil.CODEC).orElseGet(UUID::randomUUID);
        this.replicatorsOnTask = new ArrayList<>();
        for (long onTask : compoundTag.getLongArray("OnTask").orElseGet(() -> new long[0])) {
            this.replicatorsOnTask.add(onTask);
        }
        this.source = BlockPos.of(compoundTag.getLongOr("Source", 0));
        this.isInfinteMode = compoundTag.getBooleanOr("Infinte", false);
    }
}
