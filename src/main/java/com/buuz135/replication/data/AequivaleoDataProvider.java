package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.aequivaleo.ReplicationCompoundTypeGroup;
import com.google.common.collect.ImmutableList;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.information.datagen.ForcedInformationProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;


import static net.minecraft.world.item.Items.*;

public class AequivaleoDataProvider extends ForcedInformationProvider {

    public AequivaleoDataProvider(String modId, DataGenerator dataGenerator) {
        super(modId, dataGenerator);
    }

    @Override
    public void calculateDataToSave() {
        saveData(IRON_INGOT, metallic(9));
        saveData(GOLD_INGOT, metallic(9), precious(9));
        saveData(DIAMOND, precious(9*4));
        saveData(LAPIS_LAZULI, precious(1), earth(2), organic(2));
        saveData(COPPER_INGOT, precious(2), metallic(2));
        saveData(ANCIENT_DEBRIS, precious(18), nether(18));
        saveData(AMETHYST_SHARD, precious(4), earth(1));
        saveData(COAL, earth(2), precious(0.25), organic(0.25));
        saveData(new Item[]{EXPOSED_COPPER, WEATHERED_COPPER, OXIDIZED_COPPER}, precious(8), metallic(8));

        saveTag(ItemTags.LOGS, earth(8), organic(8));
        saveTag(ItemTags.SAPLINGS, earth(16), organic(16));
        saveTag(ItemTags.LEAVES, organic(4));
        saveTag(ItemTags.SMALL_FLOWERS, earth(2), organic(2));
        saveTag(ItemTags.TALL_FLOWERS, earth(4), organic(4));
        saveTag(Tags.Items.MUSHROOMS, earth(2), living(2));

        saveData(STRING, organic(2), living(2));
        saveData(COBWEB, organic(18), living(18));
        saveData(GRASS, organic(4));
        saveData(FERN, organic(4));
        saveData(DEAD_BUSH, earth(1), organic(1));
        saveData(SEAGRASS, earth(2), organic(2));
        saveData(SEA_PICKLE, earth(2), organic(2));
        saveData(SUGAR_CANE, earth(2), organic(2));
        saveData(KELP, earth(2), organic(2));

        saveData(new Item[]{CRIMSON_FUNGUS, CRIMSON_ROOTS, WEEPING_VINES,
                WARPED_FUNGUS, WARPED_ROOTS, NETHER_SPROUTS, TWISTING_VINES}, organic(2), nether(2));

        saveTag(Tags.Items.STONE, earth(1));
        saveTag(Tags.Items.COBBLESTONE, earth(1));
        saveData(POINTED_DRIPSTONE, earth(1));
        saveData(CALCITE, earth(2));
        saveData(DIRT, earth(1));
        saveData(GRASS_BLOCK, earth(1), organic(1));
        saveData(PODZOL, earth(1), organic(1));
        saveData(MUD, earth(1));
        saveData(FLINT, earth(1));
        saveData(CLAY_BALL, earth(2));
        saveData(new Item[]{SAND, GRAVEL, RED_SAND}, earth(1));
        saveData(new Item[]{WARPED_NYLIUM, CRIMSON_NYLIUM}, organic(2), nether(1));
        saveData(new Item[]{WHITE_CONCRETE, ORANGE_CONCRETE, MAGENTA_CONCRETE, LIGHT_BLUE_CONCRETE, YELLOW_CONCRETE,
            LIME_CONCRETE, PINK_CONCRETE, GRAY_CONCRETE, LIGHT_GRAY_CONCRETE, CYAN_CONCRETE, PURPLE_CONCRETE, BLUE_CONCRETE,
            BROWN_CONCRETE, GREEN_CONCRETE, RED_CONCRETE, BLACK_CONCRETE}, earth(1.25), organic(0.25));

        saveData(NETHERRACK, nether(1));
        saveData(END_STONE, ender(1));

        saveData(NETHER_STAR, quantum(64), nether(16));

        saveData(REDSTONE, precious(2), earth(2));
        saveData(SLIME_BALL, living(2), earth(2));
        saveData(HONEY_BLOCK, living(18), earth(18));
        saveData(ECHO_SHARD, quantum(1), living(4));
        saveData(HONEYCOMB, living(2), earth(2));

        saveData(QUARTZ, precious(4), nether(2));

        saveData(FEATHER, living(2), organic(2));
        saveData(LEATHER, living(2), organic(2));
        saveData(WHEAT, organic(2), earth(2));
        saveData(GUNPOWDER, organic(2), earth(2));
        saveData(GLOWSTONE, nether(4), precious(4));
        saveData(new Item[]{PORKCHOP, APPLE, COD, SALMON, TROPICAL_FISH, PUFFERFISH, MELON_SLICE, BEEF, CHICKEN, POTATO, POISONOUS_POTATO, CARROT, MUTTON, RABBIT, BEETROOT, GLOW_BERRIES, SWEET_BERRIES}, organic(4), living(4));

    }

    private void saveData(Item item, CompoundInstance... instances) {
        save(specFor(item).withCompounds(instances));
    }

    private void saveData(Item[] items, CompoundInstance... instances) {
        var spec = specFor(items);
        save(spec.withCompounds(instances));
    }

    private void saveTag(TagKey<Item> tag, CompoundInstance... instances) {
        if (!ReplicationCompoundTypeGroup.ALLOWED_RECIPE_TAGS.contains(tag)){
            Replication.LOGGER.error("MISSING ALLOWED TAG " + tag.toString());
        }
        save(specFor(tag).withCompounds(instances));
    }

    private static CompoundInstance metallic(double d){
        return new CompoundInstance(ReplicationRegistry.METALLIC.get(), d);
    }

    private static CompoundInstance earth(double d){
        return new CompoundInstance(ReplicationRegistry.EARTH.get(), d);
    }

    private static CompoundInstance organic(double d){
        return new CompoundInstance(ReplicationRegistry.ORGANIC.get(), d);
    }

    private static CompoundInstance quantum(double d){
        return new CompoundInstance(ReplicationRegistry.QUANTUM.get(), d);
    }

    private static CompoundInstance nether(double d){
        return new CompoundInstance(ReplicationRegistry.NETHER.get(), d);
    }

    private static CompoundInstance precious(double d){
        return new CompoundInstance(ReplicationRegistry.PRECIOUS.get(), d);
    }

    private static CompoundInstance ender(double d){
        return new CompoundInstance(ReplicationRegistry.ENDER.get(), d);
    }

    private static CompoundInstance living(double d){
        return new CompoundInstance(ReplicationRegistry.LIVING.get(), d);
    }

}
