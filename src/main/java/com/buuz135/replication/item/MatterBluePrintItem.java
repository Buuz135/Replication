package com.buuz135.replication.item;

import com.buuz135.replication.ReplicationAttachments;
import com.buuz135.replication.api.pattern.IMatterPatternModifier;
import com.buuz135.replication.block.tile.ChipStorageBlockEntity;
import com.hrznstudio.titanium.item.BasicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class MatterBluePrintItem extends ReplicationItem {
    public MatterBluePrintItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean hasTooltipDetails(@Nullable BasicItem.Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (key == null){
            if (stack.has(ReplicationAttachments.BLUEPRINT)){
                var item = ItemStack.parseOptional(Minecraft.getInstance().level.registryAccess(), stack.get(ReplicationAttachments.BLUEPRINT).getCompound("Item"));
                var progress = stack.get(ReplicationAttachments.BLUEPRINT).getDouble("Progress");
                tooltip.add(Component.literal("").append(Component.translatable("relocation.blueprint.contains_information").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(new DecimalFormat("##.##").format(progress*100)).withStyle(ChatFormatting.WHITE))
                        .append(Component.literal("% ").withStyle(ChatFormatting.DARK_AQUA))
                        .append(item.getHoverName())
                );
            } else {
                tooltip.add(Component.translatable("relocation.blueprint.not_found").withStyle(ChatFormatting.GRAY));
            }
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("relocation.blueprint.use_on_chip_storage").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getItemInHand().has(ReplicationAttachments.BLUEPRINT)){
            var item = ItemStack.parseOptional(pContext.getPlayer().level().registryAccess(), pContext.getItemInHand().get(ReplicationAttachments.BLUEPRINT).getCompound("Item"));
            var tile = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
            if (tile instanceof ChipStorageBlockEntity chipStorageBlockEntity){
                for (int i = 0; i < chipStorageBlockEntity.getChips().getSlots(); i++) {
                    var stack = chipStorageBlockEntity.getChips().getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof IMatterPatternModifier<?> patternModifier){
                        var returnedValue = ((IMatterPatternModifier<ItemStack>)patternModifier).addPattern(pContext.getLevel(), stack, item, (float) pContext.getItemInHand().get(ReplicationAttachments.BLUEPRINT).getDouble("Progress"));
                        if (returnedValue.getPattern() != null){
                            pContext.getItemInHand().shrink(1);
                            chipStorageBlockEntity.cachePatterns();
                            return InteractionResult.CONSUME;
                        }
                    }
                }
            }
        }
        return super.useOn(pContext);
    }
}
