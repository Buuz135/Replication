package com.buuz135.replication.compat.jade;

import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.util.NumberUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.JadeUI;
import snownee.jade.api.view.ProgressView;

import java.awt.*;

public class MatterTankComponentProvider implements IBlockComponentProvider {

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getServerData().contains("MatterStack")){
            var matterStack = MatterStack.loadMatterStackFromNBT(blockAccessor.getServerData().getCompoundOrEmpty("MatterStack"));
            var floatColor = matterStack.getMatterType().getColor().get();
            var color = new Color(floatColor[0], floatColor[1], floatColor[2], floatColor[3]);
            float progress = (float) Math.max(0, Math.min(1, matterStack.getAmount() / ReplicationConfig.MatterTank.CAPACITY));
            Component text = matterStack.isEmpty() ? Component.translatable("tooltip.titanium.tank.empty") : Component.translatable(matterStack.getTranslationKey()).append(" ").append(NumberUtils.getFormatedBigNumber(matterStack.getAmount()));
            iTooltip.add(JadeUI.progress(new ProgressView(ProgressView.Part.of(progress, color.getRGB()), text, JadeUI.progressStyle(), BoxStyle.nestedBox())));
        }
    }

    @Override
    public Identifier getUid() {
        return JadeIds.MATTER_TANK;
    }
}
