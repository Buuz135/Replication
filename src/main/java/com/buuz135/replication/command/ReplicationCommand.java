package com.buuz135.replication.command;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.item.ReplicationItem;
import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public class ReplicationCommand {

    private static final Logger LOGGER = LogManager.getLogger("REPLICATION-MISSING-DUMP");


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder) Commands
                .literal("replication")
                .then(Commands.literal("dump-inventory").executes(context -> dumpInventoryItems(context)).requires(commandSourceStack -> commandSourceStack.hasPermission(4)))
                .then(Commands.literal("dump-missing").executes(context -> dumpMissing(context)).requires(commandSourceStack -> commandSourceStack.hasPermission(4)))
                .then(
                        Commands.literal("create-blueprint-using-hand")
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                                .then(Commands.argument("progress", DoubleArgumentType.doubleArg(0D, 1D))
                                        .executes(context -> createBlueprint(context))))
        );
    }

    public static int dumpMissing(CommandContext<CommandSourceStack> context){
        var tabs = CreativeModeTabs.tabs();
        var hotbar = BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.HOTBAR);
        var search = BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.SEARCH);
        var missingItems = new ArrayList<>();
        for (CreativeModeTab tab : tabs) {
            if (tab == hotbar || tab == search) continue;
            LOGGER.info("SCANNING TAB " + tab.getDisplayName().getString());
            var list = tab.getDisplayItems();
            var missing = list.stream().filter(item -> ForgeRegistries.ITEMS.getKey(item.getItem()).getNamespace().equals("minecraft"))
                    .filter(itemStack -> !itemStack.hasTag())
                    .filter(itemStack -> !(itemStack.getItem() instanceof SpawnEggItem))
                    .filter(itemStack -> !ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().contains("_ore"))
                    .filter(itemStack -> !ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().contains("_shulker"))
                    .filter(itemStack -> !ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().contains("raw_"))
                    .filter(itemStack -> !ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().contains("_sherd"))
                    .filter(itemStack -> !ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().contains("_template"))
                    .filter(itemStack -> !ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().contains("_bucket"))
                    .filter(itemStack -> !ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().contains("infested_"))
                    .filter(itemStack -> !ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath().contains("_pattern"))
                    .filter(item -> IAequivaleoAPI.getInstance().getEquivalencyResults(Minecraft.getInstance().level.dimension()).dataFor(item).size() == 0).collect(Collectors.toList());
            missing.forEach(item -> LOGGER.info(ForgeRegistries.ITEMS.getKey(item.getItem())));
            for (ItemStack itemStack : missing) {
                if (!missingItems.contains(itemStack.getItem())){
                    missingItems.add(itemStack.getItem());
                }
            }
            LOGGER.info("WE ARE MISSING " + missing.size() + " items");
            LOGGER.info("--------------------------------------------");
        }
        LOGGER.info("WE ARE TOTAL MISSING " + missingItems.size() + " items");

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

    public static int createBlueprint(CommandContext<CommandSourceStack> context){
        try {
            var mainStack = ItemHandlerHelper.copyStackWithSize(context.getSource().getPlayerOrException().getMainHandItem(), 1);
            if (!mainStack.isEmpty()){
                var blueprint = new ItemStack(ReplicationRegistry.Items.MATTER_BLUEPRINT.get());
                var tag = new CompoundTag();
                tag.put("Item", mainStack.serializeNBT());
                tag.putDouble("Progress", context.getArgument("progress", Double.class));
                blueprint.setTag(tag);
                ItemHandlerHelper.giveItemToPlayer(context.getSource().getPlayerOrException(), blueprint, 0);
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
