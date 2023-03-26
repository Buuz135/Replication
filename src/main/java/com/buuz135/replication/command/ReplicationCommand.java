package com.buuz135.replication.command;

import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.stream.Collectors;

public class ReplicationCommand {

    private static final Logger LOGGER = LogManager.getLogger("REPLICATION-MISSING-DUMP");


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder) Commands
                .literal("replication")
                        .then(Commands.literal("dump-inventory").executes(context -> dumpInventoryItems(context)))
                .then(Commands.literal("dump-missing").executes(context -> dumpMissing(context))));
    }

    public static int dumpMissing(CommandContext<CommandSourceStack> context){
        var totalMissing = 0;
        for (CreativeModeTab tab : CreativeModeTab.TABS) {
            if (tab == CreativeModeTab.TAB_HOTBAR || tab == CreativeModeTab.TAB_SEARCH) continue;
            LOGGER.info("SCANNING TAB " + tab.getDisplayName().getString());
            NonNullList<ItemStack> list = NonNullList.create();
            tab.fillItemList(list);
            var missing = list.stream().filter(item -> ForgeRegistries.ITEMS.getKey(item.getItem()).getNamespace().equals("minecraft"))
                    .filter(itemStack -> !itemStack.hasTag())
                    .filter(itemStack -> !(itemStack.getItem() instanceof SpawnEggItem))
                    .filter(item -> IAequivaleoAPI.getInstance().getEquivalencyResults(Minecraft.getInstance().level.dimension()).dataFor(item).size() == 0).collect(Collectors.toList());
            missing.forEach(item -> LOGGER.info(ForgeRegistries.ITEMS.getKey(item.getItem())));
            totalMissing += missing.size();
            LOGGER.info("WE ARE MISSING " + missing.size() + " items");
            LOGGER.info("--------------------------------------------");
        }
        LOGGER.info("WE ARE TOTAL MISSING " + totalMissing + " items");

        return 1;
    }

    public static int dumpInventoryItems(CommandContext<CommandSourceStack> context) {
        try {
            LOGGER.info(context.getSource().getPlayerOrException().getInventory().items.stream()
                            .filter(itemStack -> !itemStack.isEmpty())
                    .map(itemStack -> ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().toUpperCase(Locale.ROOT)).collect(Collectors.joining(", ")));
        } catch (Exception e) {

        }

        return 1;
    }
}
