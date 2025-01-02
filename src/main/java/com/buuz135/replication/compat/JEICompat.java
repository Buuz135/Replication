package com.buuz135.replication.compat;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import com.buuz135.replication.compat.jei.ReplicationTerminalScreenHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@JeiPlugin
public class JEICompat implements IModPlugin {
    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        IModPlugin.super.registerItemSubtypes(registration);
        registration.useNbtForSubtypes(ReplicationRegistry.Blocks.MATTER_TANK.getKey().get().asItem());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Replication.MOD_ID, "replication");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        IModPlugin.super.registerGuiHandlers(registration);
        registration.addGuiContainerHandler(ReplicationTerminalScreen.class, new ReplicationTerminalScreenHandler());
    }
}
