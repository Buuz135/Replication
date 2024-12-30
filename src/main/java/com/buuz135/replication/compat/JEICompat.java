package com.buuz135.replication.compat;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationAttachments;
import com.buuz135.replication.ReplicationRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
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
        registration.registerSubtypeInterpreter(ReplicationRegistry.Blocks.MATTER_TANK.asItem(), new ISubtypeInterpreter<ItemStack>() {
            @Override
            public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
                return List.of(ReplicationAttachments.TANK_STORAGE);
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
                return ingredient.getOrDefault(ReplicationAttachments.TANK_STORAGE, new CompoundTag()).toString();
            }
        });
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "replication");
    }
}
