/*
 * This file is part of Titanium
 * Copyright (C) 2024, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.buuz135.replication.container.component;

import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.matter_fluid.component.MatterTankComponent;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.client.screen.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.screen.addon.StateButtonInfo;
import com.hrznstudio.titanium.component.IComponentBundle;
import com.hrznstudio.titanium.component.IComponentHandler;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;


import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class LockableMatterTankBundle<T extends BasicTile & IComponentHarness> implements IComponentBundle, INBTSerializable<CompoundTag> {

    private MatterTankComponent<T> inventory;
    private Predicate<MatterStack> cachedFilter;
    private ButtonComponent buttonAddon;
    private T componentHarness;
    private MatterStack filter;
    private int lockPosX;
    private int lockPosY;
    private boolean isLocked;

    public LockableMatterTankBundle(T componentHarness, MatterTankComponent<T> inventory, int lockPosX, int lockPosY, boolean isLocked) {
        this.componentHarness = componentHarness;
        this.inventory = inventory;
        this.cachedFilter = inventory.getInsertPredicate();
        this.filter = MatterStack.EMPTY;
        this.lockPosX = lockPosX;
        this.lockPosY = lockPosY;
        this.isLocked = isLocked;
        this.buttonAddon = new ButtonComponent(lockPosX, lockPosY, 14, 14) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new StateButtonAddon(buttonAddon,
                        new StateButtonInfo(0, AssetTypes.BUTTON_UNLOCKED, ChatFormatting.GOLD + LangUtil.getString("tooltip.titanium.locks") + ChatFormatting.WHITE + " " + LangUtil.getString("tooltip.titanium.facing_handler." + inventory.getName().toLowerCase())),
                        new StateButtonInfo(1, AssetTypes.BUTTON_LOCKED, ChatFormatting.GOLD + LangUtil.getString("tooltip.titanium.unlocks") + ChatFormatting.WHITE + " " + LangUtil.getString("tooltip.titanium.facing_handler." + inventory.getName().toLowerCase()),
                                Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.replication.tank.matter").getString()).getString()
                                        + ChatFormatting.WHITE + (LockableMatterTankBundle.this.filter.isEmpty() ? Component.translatable("tooltip.titanium.tank.empty").getString() : Component.translatable(LockableMatterTankBundle.this.filter.getTranslationKey()).getString()))) {
                    @Override
                    public int getState() {
                        return LockableMatterTankBundle.this.isLocked ? 1 : 0;
                    }
                });
            }
        }.setPredicate((playerEntity, compoundNBT) -> {
            this.isLocked = !this.isLocked;
            this.filter = this.inventory.getMatter();
            updateFilter();
            this.componentHarness.syncObject(this);
        });
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        return Collections.emptyList();
    }

    @Override
    public void accept(IComponentHandler... handler) {
        for (IComponentHandler iComponentHandler : handler) {
            iComponentHandler.add(this.buttonAddon);
        }
    }

    @Nonnull
    @Override
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        return Collections.emptyList();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.put("Tank", this.inventory.serializeNBT());
        compoundNBT.putBoolean("Locked", this.isLocked);
        compoundNBT.put("Filter", this.filter.writeToNBT(new CompoundTag()));
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.inventory.deserializeNBT(nbt.getCompound("Tank"));
        this.isLocked = nbt.getBoolean("Locked");
        this.filter = MatterStack.loadMatterStackFromNBT(nbt.getCompound("Filter"));

        updateFilter();
    }

    private void updateFilter() {
        if (isLocked) {
            this.inventory.setInputFilter((stack) -> this.filter.isMatterEqual(stack));
        } else {
            this.filter = MatterStack.EMPTY;
            this.inventory.setInputFilter(this.cachedFilter);
        }
    }

    public MatterTankComponent<T> getTank() {
        return inventory;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public MatterStack getFilter() {
        return filter;
    }

    public void setFilter(MatterStack filter) {
        this.filter = filter;
    }
}
