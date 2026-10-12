package com.buuz135.replication.util;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ReplicationEnergyStorage extends SimpleEnergyHandler {

    public ReplicationEnergyStorage(int capacity, int maxInsert, int maxExtract, int energy) {
        super(capacity, maxInsert, maxExtract, energy);
    }

    public int getEnergyStored() {
        return getAmountAsInt();
    }

    public int getMaxEnergyStored() {
        return getCapacityAsInt();
    }

    public int receiveEnergy(int amount, boolean simulate) {
        try (var transaction = Transaction.openRoot()) {
            int inserted = insert(amount, transaction);
            if (!simulate) transaction.commit();
            return inserted;
        }
    }

    public int extractEnergy(int amount, boolean simulate) {
        try (var transaction = Transaction.openRoot()) {
            int extracted = extract(amount, transaction);
            if (!simulate) transaction.commit();
            return extracted;
        }
    }
}
