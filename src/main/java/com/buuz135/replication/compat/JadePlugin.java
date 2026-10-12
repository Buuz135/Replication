package com.buuz135.replication.compat;

import com.buuz135.replication.block.CreativeMatterTankBlock;
import com.buuz135.replication.block.MatterTankBlock;
import com.buuz135.replication.block.tile.BaseMatterTankBlockEntity;
import com.buuz135.replication.compat.jade.MatterTankComponentProvider;
import com.buuz135.replication.compat.jade.MatterTankDataProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new MatterTankDataProvider(), BaseMatterTankBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        MatterTankComponentProvider componentProvider = new MatterTankComponentProvider();
        registration.registerBlockComponent(componentProvider, MatterTankBlock.class);
        registration.registerBlockComponent(componentProvider, CreativeMatterTankBlock.class);
    }
}
