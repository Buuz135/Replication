package com.buuz135.replication;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.buuz135.replication.command.ReplicationCommand;
import com.buuz135.replication.network.NetworkManager;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class CommonEvents {

    public static void init() {
        NBTManager.getInstance().scanTileClassForAnnotations(ReplicatorBlockEntity.class);
        NBTManager.getInstance().scanTileClassForAnnotations(MatterPipeBlockEntity.class);
        NBTManager.getInstance().scanTileClassForAnnotations(IdentificationChamberBlockEntity.class);
        NBTManager.getInstance().scanTileClassForAnnotations(DisintegratorBlockEntity.class);


        EventManager.forge(TickEvent.LevelTickEvent.class)
                .filter(worldTickEvent -> !worldTickEvent.level.isClientSide && worldTickEvent.phase == TickEvent.Phase.END)
                .process(worldTickEvent -> {
                    NetworkManager.get(worldTickEvent.level).getNetworks().forEach(network -> network.update(worldTickEvent.level));
                }).subscribe();

        EventManager.forge(ServerStartingEvent.class).process(serverStartingEvent -> {
            ReplicationCommand.register(serverStartingEvent.getServer().getCommands().getDispatcher());
        }).subscribe();

        EventManager.mod(NewRegistryEvent.class)
                .process(newRegistryEvent -> {
                    ReplicationRegistry.MATTER_TYPES_REGISTRY = newRegistryEvent.create(new RegistryBuilder<IMatterType>().setName(new ResourceLocation(Replication.MOD_ID, "matter_types")));
                }).subscribe();

    }
}
