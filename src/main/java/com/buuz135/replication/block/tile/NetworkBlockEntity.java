package com.buuz135.replication.block.tile;

import com.buuz135.replication.api.matter_fluid.component.MatterTankComponent;
import com.buuz135.replication.network.DefaultMatterNetworkElement;
import com.buuz135.replication.network.MatterNetwork;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.block.tile.ITickableBlockEntity;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public abstract class NetworkBlockEntity<T extends ActiveTile<T>> extends ActiveTile<T> implements ITickableBlockEntity<T> {

    private List<MatterTankComponent<T>> matterTankComponents;

    public NetworkBlockEntity(BasicTileBlock<T> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.matterTankComponents = new ArrayList<>();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        this.matterTankComponents.forEach(matterTankComponent -> matterTankComponent.getScreenAddons().forEach(this::addGuiAddonFactory));
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

    public void addMatterTank(MatterTankComponent<T> matterTankComponent){
        this.matterTankComponents.add(matterTankComponent);
        matterTankComponent.setComponentHarness(this.getSelf());
        matterTankComponent.getContainerAddons().forEach(this::addContainerAddonFactory);
        var change = matterTankComponent.getOnContentChange();
        matterTankComponent.setOnContentChange(() -> {
           syncObject(matterTankComponent);
           change.run();
        });
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
            if (pipe.getNetwork() instanceof MatterNetwork matterNetwork){
                matterNetwork.removeElement(pipe);
            }
        }
    }

    protected NetworkElement createElement(Level level, BlockPos pos){
        return new DefaultMatterNetworkElement(level, pos);
    }

    public MatterNetwork getNetwork(){
        return (MatterNetwork) NetworkManager.get(this.level).getElement(worldPosition).getNetwork();
    }

    public List<MatterTankComponent<T>> getMatterTankComponents() {
        return matterTankComponents;
    }


}
