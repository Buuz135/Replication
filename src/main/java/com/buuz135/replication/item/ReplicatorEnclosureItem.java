package com.buuz135.replication.item;

import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.block.ReplicatorBlock;
import com.hrznstudio.titanium.item.BasicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReplicatorEnclosureItem extends ReplicationItem {

    public ReplicatorEnclosureItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.getBlock() instanceof ReplicatorBlock && !blockstate.getValue(ReplicatorBlock.HAS_ENCLOSURE)) {
            context.getLevel().setBlockAndUpdate(context.getClickedPos(), blockstate.setValue(ReplicatorBlock.HAS_ENCLOSURE, true));
            context.getItemInHand().shrink(1);
            context.getPlayer().swing(context.getHand());
            return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }

    @Override
    public boolean hasTooltipDetails(@Nullable BasicItem.Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        tooltip.add(Component.translatable("tooltip.replication_enclosure.speed", (int) (100 - ReplicationConfig.Replicator.ENCLOSURE_SPEED_MULTIPLIER * 100) + "%").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.replication_enclosure.energy", (int) (ReplicationConfig.Replicator.ENCLOSURE_POWER_MULTIPLIER * 100 - 100) + "%").withStyle(ChatFormatting.GRAY));
    }
}
