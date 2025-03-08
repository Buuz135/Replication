package com.buuz135.replication.item;

import com.buuz135.replication.api.pattern.IMatterPatternHolder;
import com.buuz135.replication.api.pattern.MatterPattern;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CreativeMemoryChipItem extends ReplicationItem implements IMatterPatternHolder<ItemStack> {

    public CreativeMemoryChipItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getPatternSlots(ItemStack element) {
        return Integer.MAX_VALUE;
    }

    @Override
    public List<MatterPattern> getPatterns(Level level, ItemStack element) {
        var items = new ArrayList<MatterPattern>();
        for (Item item : BuiltInRegistries.ITEM) {
            items.add(new MatterPattern(item.getDefaultInstance(), 1));
        }
        return items;
    }

}
