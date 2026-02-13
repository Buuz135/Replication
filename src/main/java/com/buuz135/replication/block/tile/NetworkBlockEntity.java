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
    private boolean needsNetworkRegistration = false;

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
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            NetworkManager networkManager = NetworkManager.get(level);

            if (networkManager.getElement(worldPosition) == null) {
                try {
                    networkManager.addElement(createElement(level, worldPosition));
                } catch (RuntimeException e) {
                    // Titanium может выбросить "Element network is null!" при попытке объединить сети
                    // Это происходит, когда соседние элементы еще не инициализированы
                    // Отложим регистрацию на следующий тик
                    if (e.getMessage() != null && e.getMessage().contains("Element network is null")) {
                        needsNetworkRegistration = true;
                    } else {
                        throw e; // Пробросить исключение, если это другая проблема
                    }
                }
            }
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        // Попытка повторной регистрации, если не удалось при onLoad
        if (needsNetworkRegistration) {
            NetworkManager networkManager = NetworkManager.get(level);
            if (networkManager.getElement(worldPosition) == null) {
                try {
                    networkManager.addElement(createElement(level, worldPosition));
                    needsNetworkRegistration = false;
                } catch (RuntimeException e) {
                    // Если все еще не удается, попробуем в следующий тик
                    if (e.getMessage() == null || !e.getMessage().contains("Element network is null")) {
                        throw e;
                    }
                }
            } else {
                needsNetworkRegistration = false;
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

                // Переместить внутрь блока null-проверки
                if (pipe.getNetwork() instanceof MatterNetwork matterNetwork){
                    matterNetwork.removeElement(pipe);
                }
            }

            networkManager.removeElement(worldPosition);
        }
    }

    protected NetworkElement createElement(Level level, BlockPos pos){
        return new DefaultMatterNetworkElement(level, pos);
    }

    public MatterNetwork getNetwork(){
        if (this.level == null) return null;
        NetworkElement element = NetworkManager.get(this.level).getElement(worldPosition);
        if (element == null) return null;
        return (MatterNetwork) element.getNetwork();
    }

    public List<MatterTankComponent<T>> getMatterTankComponents() {
        return matterTankComponents;
    }


}
