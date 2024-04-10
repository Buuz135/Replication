package com.buuz135.replication.block.tile;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.pattern.IMatterPatternHolder;
import com.buuz135.replication.container.ReplicationTerminalContainer;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReplicationTerminalBlockEntity extends NetworkBlockEntity<ReplicationTerminalBlockEntity>{

    @Save
    private InventoryComponent<ReplicationTerminalBlockEntity> output;
    @Save
    private int sortingTypeValue;
    @Save
    private int sortingDirection;
    private TerminalPlayerTracker terminalPlayerTracker;


    public ReplicationTerminalBlockEntity(BasicTileBlock<ReplicationTerminalBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.terminalPlayerTracker = new TerminalPlayerTracker();
        this.sortingTypeValue = 0;
        this.sortingDirection = 1;
        this.output = new InventoryComponent<ReplicationTerminalBlockEntity>("output", 11,131, 9*2)
                .setRange(9,2);
        this.addInventory(this.output);
    }

    @Override
    public void initClient() {
        super.initClient();
    }

    @NotNull
    @Override
    public ReplicationTerminalBlockEntity getSelf() {
        return this;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, ReplicationTerminalBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        this.terminalPlayerTracker.checkIfValid();
    }

    @Override
    public InteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        }
        if (playerIn instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, this, buffer -> {
                LocatorFactory.writePacketBuffer(buffer, new TileEntityLocatorInstance(this.worldPosition));
                buffer.writeUtf(this.getNetwork().getId());
                buffer.writeInt(this.sortingTypeValue);
                buffer.writeInt(this.sortingDirection);
            });
            for (NetworkElement chipSupplier : this.getNetwork().getChipSuppliers()) {
                var tile = chipSupplier.getLevel().getBlockEntity(chipSupplier.getPos());
                if (tile instanceof IMatterPatternHolder holder){
                    this.getNetwork().sendPatternSyncPacket(serverPlayer, holder, tile.getBlockPos());
                }
            }
            ReplicationRegistry.MATTER_TYPES_REGISTRY.get().getValues().forEach(iMatterType -> this.getNetwork().sendMatterSyncPacket(serverPlayer, this.getNetwork().calculateMatterAmount(iMatterType), iMatterType));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        if (id == 999){
            var type = compound.getString("type");
            var value = compound.getInt("state");
            if (type.equals("SORTING_TYPE")){
                sortingTypeValue = value;
                syncObject(sortingTypeValue);
            } else if (type.equals("SORTING_ACTION")){
                sortingDirection = value;
                syncObject(sortingDirection);
            }
        }
    }

    public TerminalPlayerTracker getTerminalPlayerTracker() {
        return terminalPlayerTracker;
    }

    public InventoryComponent<ReplicationTerminalBlockEntity> getOutput() {
        return output;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int menu, Inventory inventoryPlayer, Player entityPlayer) {
        return new ReplicationTerminalContainer(this, inventoryPlayer, menu);
    }

    public static class TerminalPlayerTracker{

        private List<ServerPlayer> players;
        private List<UUID> uuidsToRemove;
        private List<ServerPlayer> playersToAdd;

        public TerminalPlayerTracker() {
            this.players = new ArrayList<>();
            this.uuidsToRemove = new ArrayList<>();
            this.playersToAdd = new ArrayList<>();
        }

        public void checkIfValid(){
            var output = new ArrayList<>(playersToAdd);
            var input = new ArrayList<>(players);
            for (ServerPlayer serverPlayer : input) {
                if (serverPlayer.containerMenu instanceof ReplicationTerminalContainer && !this.uuidsToRemove.contains(serverPlayer.getUUID())){
                    output.add(serverPlayer);
                }
            }
            this.players = output;
            this.uuidsToRemove = new ArrayList<>();
            this.playersToAdd = new ArrayList<>();
        }

        public void removePlayer(ServerPlayer serverPlayer){
            this.uuidsToRemove.add(serverPlayer.getUUID());
        }

        public void addPlayer(ServerPlayer serverPlayer){
            this.playersToAdd.add(serverPlayer);
        }

        public List<ServerPlayer> getPlayers() {
            return players;
        }
    }
}
