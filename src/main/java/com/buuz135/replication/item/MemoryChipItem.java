package com.buuz135.replication.item;

import com.buuz135.replication.ReplicationAttachments;
import com.buuz135.replication.api.pattern.IMatterPatternHolder;
import com.buuz135.replication.api.pattern.IMatterPatternModifier;
import com.buuz135.replication.api.pattern.MatterPattern;
import com.hrznstudio.titanium.item.BasicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryChipItem extends ReplicationItem implements IMatterPatternHolder<ItemStack>, IMatterPatternModifier<ItemStack> {

    public MemoryChipItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getPatternSlots(ItemStack element) {
        return 16;
    }

    @Override
    public List<MatterPattern> getPatterns(Level level, ItemStack element) {
        var items = new ArrayList<MatterPattern>();
        if (element.has(ReplicationAttachments.CHIP_PATTERNS)){
            var tag = element.get(ReplicationAttachments.CHIP_PATTERNS);
            for (int i = 0; i < getPatternSlots(element); i++) {
                if (tag.contains(i + "")){
                    var pattern = new MatterPattern();
                    pattern.deserializeNBT(level.registryAccess(), tag.getCompound(i + ""));
                    items.add(pattern);
                } else {
                    items.add(new MatterPattern());
                }
            }
        }
        return items;
    }

    @Override
    @Nullable
    public ModifierAction addPattern(Level level, ItemStack element, ItemStack stack, float progress){
        var currentPatterns = getPatterns(level, element).stream().filter(pattern -> !pattern.getStack().isEmpty()).collect(Collectors.toList());
        for (MatterPattern currentPattern : currentPatterns) {
            if (ItemStack.isSameItemSameComponents(currentPattern.getStack(), stack)){
                currentPattern.setCompletion(Math.min(1, currentPattern.getCompletion() + progress));
                savePatterns(level, element, currentPatterns);
                if (currentPattern.getCompletion() >= 1 && currentPatterns.size() >= getPatternSlots(element)){
                    return ModifierAction.isFull(currentPattern);
                }
                return ModifierAction.canKeepAdding(currentPattern);
            }
        }
        if (currentPatterns.size() >= getPatternSlots(element)){
            return ModifierAction.isFull(null);
        }
        var newPattern = new MatterPattern(stack, progress);
        currentPatterns.add(newPattern);
        savePatterns(level, element, currentPatterns);
        return ModifierAction.canKeepAdding(newPattern);
    }

    private void savePatterns(Level level, ItemStack stack, List<MatterPattern> currentPatterns){
        CompoundTag patterns = new CompoundTag();
        for (int i = 0; i < currentPatterns.size(); i++) {
            patterns.put(i + "", currentPatterns.get(i).serializeNBT(level.registryAccess()));
        }
        stack.set(ReplicationAttachments.CHIP_PATTERNS, patterns);
    }


    @Override
    public boolean hasTooltipDetails(@Nullable BasicItem.Key key) {
        return key == Key.SHIFT;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addTooltipDetails(@Nullable BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (key == Key.SHIFT){
            var fullPatterns = 0;
            if (stack.has(ReplicationAttachments.CHIP_PATTERNS)){
                var patterns = getPatterns(Minecraft.getInstance().level, stack);

                for (MatterPattern pattern : patterns) {
                    if (pattern.getStack().isEmpty()) continue;
                    var component = Component.literal(" - ").setStyle(Style.EMPTY.withColor(Mth.color(114/255f, 229/255f, 103/255f)))
                            .append(Component.translatable(pattern.getStack().getDescriptionId()).withStyle(pattern.getCompletion() >= 1 ? ChatFormatting.GOLD : ChatFormatting.WHITE));
                    if (pattern.getCompletion() < 1){
                        component.append(Component.literal(" " + new DecimalFormat("##.## %").format(pattern.getCompletion())).withStyle(Style.EMPTY.withColor(Mth.color(242/255f, 82/255f, 82/255f))));
                    }
                    tooltip.add(component);
                    ++fullPatterns;
                }
            }
            if (fullPatterns < getPatternSlots(stack)){
                tooltip.add(Component.literal(getPatternSlots(stack) - fullPatterns + " ").withStyle(ChatFormatting.GOLD).append(Component.literal("slots left").withStyle(ChatFormatting.WHITE)));
            }
        }
    }
}
