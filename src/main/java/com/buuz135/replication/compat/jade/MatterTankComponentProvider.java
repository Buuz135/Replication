package com.buuz135.replication.compat.jade;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.block.tile.MatterTankBlockEntity;
import com.buuz135.replication.util.NumberUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressElement;
import snownee.jade.impl.ui.ProgressStyle;
import snownee.jade.impl.ui.SlimProgressStyle;

import java.awt.*;

public class MatterTankComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static ResourceLocation MATTER_TANK_LOCATION = new ResourceLocation(Replication.MOD_ID, "matter_tank");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getServerData().contains("MatterStack")){
            var matterStack = MatterStack.loadFluidStackFromNBT(blockAccessor.getServerData().getCompound("MatterStack"));
            var floatColor = matterStack.getMatterType().getColor().get();
            var color = new Color(floatColor[0], floatColor[1], floatColor[2], floatColor[3]);
            //color = color.darker();
            iTooltip.add(new ProgressElement(matterStack.getAmount() / 256000f,
                    matterStack.isEmpty() ? Component.translatable("tooltip.titanium.tank.empty") : Component.translatable(matterStack.getTranslationKey()).append(" ").append(NumberUtils.getFormatedBigNumber(matterStack.getAmount()))
                    , IElementHelper.get().progressStyle().color(color.getRGB()).textColor(0xFFFFFF), BoxStyle.DEFAULT, false));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MATTER_TANK_LOCATION;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        MatterTankBlockEntity blockEntity = (MatterTankBlockEntity) blockAccessor.getBlockEntity();
        compoundTag.put("MatterStack", blockEntity.getTanks().get(0).getMatter().writeToNBT(new CompoundTag()));
    }
}
