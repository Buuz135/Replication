package com.buuz135.replication.container;

import com.buuz135.replication.block.tile.ReplicationTerminalBlockEntity;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.container.addon.IContainerAddonProvider;
import com.hrznstudio.titanium.container.addon.SlotContainerAddon;
import com.hrznstudio.titanium.container.addon.UpdatableSlotItemHandler;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.LocatorInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;

public class ReplicationTerminalContainer extends AbstractContainerMenu {

    public static RegistryObject<MenuType<?>> TYPE;

    private Inventory inventory;
    private ReplicationTerminalBlockEntity blockEntity;
    private String network;
    private int sortingType;
    private int sortingValue;
    private BlockPos position;

    private boolean isEnabled;

    public ReplicationTerminalContainer(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(TYPE.get(), id);
        this.inventory = inventory;
        this.isEnabled = true;
        LocatorInstance instance = LocatorFactory.readPacketBuffer(buffer);
        if (instance != null) {
            Player playerEntity = inventory.player;
            var provider = instance.locale(playerEntity).orElse(null);
            if (provider instanceof ReplicationTerminalBlockEntity addonProvider){
                this.position = addonProvider.getBlockPos();
                addonProvider.getOutput().getContainerAddons().stream()
                        .map(IFactory::create)
                        .forEach(containAddon -> {
                            containAddon.getSlots().forEach(slot -> {
                                addSlot(new UpdatableSlotItemHandler(addonProvider.getOutput(), slot.getContainerSlot(), slot.x, slot.y){
                                    @Override
                                    public boolean isActive() {
                                        return isEnabled;
                                    }
                                });
                            });
                            containAddon.getIntReferenceHolders().forEach(this::addDataSlot);
                            containAddon.getIntArrayReferenceHolders().forEach(this::addDataSlots);
                        });
            }
        }
        this.network = buffer.readUtf();
        this.sortingType = buffer.readInt();
        this.sortingValue = buffer.readInt();
        this.initInventory();
    }

    public ReplicationTerminalContainer(ReplicationTerminalBlockEntity blockEntity, Inventory inventory, int id) {
        super(TYPE.get(), id);
        this.inventory = inventory;
        this.blockEntity = blockEntity;
        if (inventory.player instanceof ServerPlayer serverPlayer)
            this.blockEntity.getTerminalPlayerTracker().addPlayer(serverPlayer);
        this.position = this.blockEntity.getBlockPos();
        this.network = blockEntity.getNetwork().getId();
        this.addExtraSlots();
        this.initInventory();
    }


    public void initInventory() {
        addPlayerChestInventory();
        addHotbarSlots();
    }

    private void addExtraSlots() {
        if (this.blockEntity != null){
            this.blockEntity.getOutput().getContainerAddons().stream()
                    .map(IFactory::create)
                    .forEach(containAddon -> {
                        containAddon.getSlots().forEach(slot -> {
                            addSlot(new UpdatableSlotItemHandler(this.blockEntity.getOutput(), slot.getContainerSlot(), slot.x, slot.y){
                                @Override
                                public boolean isActive() {
                                    return isEnabled;
                                }
                            });
                        });
                        containAddon.getIntReferenceHolders().forEach(this::addDataSlot);
                        containAddon.getIntArrayReferenceHolders().forEach(this::addDataSlots);
                    });
        }
    }

    public void addPlayerChestInventory() {
        Point invPos = new Point(9,174);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, invPos.x + j * 18, invPos.y + i * 18){
                    @Override
                    public boolean isActive() {
                        return isEnabled;
                    }
                });
            }
        }
    }

    public void addHotbarSlots() {
        Point hotbarPos = new Point(9,232);
        for (int k = 0; k < 9; k++) {
            addSlot(new Slot(getPlayerInventory(), k, hotbarPos.x + k * 18, hotbarPos.y){
                @Override
                public boolean isActive() {
                    return isEnabled;
                }
            });
        }
    }

    public Inventory getPlayerInventory() {
        return inventory;
    }

    public String getNetwork() {
        return network;
    }

    public int getSortingType() {
        return sortingType;
    }

    public int getSortingValue() {
        return sortingValue;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true; //TODO
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotPos) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotPos);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int containerSlots = this.slots.size() - 9*4;
            if (slotPos < containerSlots) {
                if (!this.moveItemStackTo(itemstack1, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer && this.blockEntity != null){
            this.blockEntity.getTerminalPlayerTracker().removePlayer((ServerPlayer) player);
        }
    }

    public BlockPos getPosition() {
        return position;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
