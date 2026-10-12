package com.buuz135.replication.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class TransferUtil {

    public static int extractEnergy(EnergyHandler handler, int amount, boolean simulate) {
        try (var transaction = Transaction.openRoot()) {
            int extracted = handler.extract(amount, transaction);
            if (!simulate) transaction.commit();
            return extracted;
        }
    }

    public static int insertEnergy(EnergyHandler handler, int amount, boolean simulate) {
        try (var transaction = Transaction.openRoot()) {
            int inserted = handler.insert(amount, transaction);
            if (!simulate) transaction.commit();
            return inserted;
        }
    }

    public static ItemStack insertItem(ResourceHandler<ItemResource> handler, ItemStack stack, boolean simulate) {
        try (var transaction = Transaction.openRoot()) {
            ItemStack remaining = ItemUtil.insertItemReturnRemaining(handler, stack, simulate, transaction);
            if (!simulate) transaction.commit();
            return remaining;
        }
    }
}
