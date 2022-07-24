package com.buuz135.replication.block.tile;

import com.buuz135.replication.api.network.NetworkElement;
import com.buuz135.replication.network.NetworkManager;
import com.buuz135.replication.network.element.type.DefaultMatterNetworkElement;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class NetworkBlockEntity<T extends BasicTile<T>> extends BasicTile<T> {

    public NetworkBlockEntity(BasicTileBlock<T> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
    }


    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (!level.isClientSide) {
            NetworkManager networkManager = NetworkManager.get(level);

            if (networkManager.getElement(worldPosition) == null) {
                networkManager.addElement(createElement(level, worldPosition));
            }
        }
    }

    private boolean unloaded;

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unloaded = true;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (!level.isClientSide && !unloaded) {
            NetworkManager networkManager = NetworkManager.get(level);

            NetworkElement pipe = networkManager.getElement(worldPosition);
            if (pipe != null) {
                //spawnDrops(pipe);
            }

            networkManager.removeElement(worldPosition);
        }
    }

    protected NetworkElement createElement(Level level, BlockPos pos){
        return new DefaultMatterNetworkElement(level, pos);
    }

}
