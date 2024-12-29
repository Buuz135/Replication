package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.calculation.MatterValue;
import com.buuz135.replication.calculation.ReplicationCalculation;
import com.buuz135.replication.recipe.MatterValueRecipe;
import com.hrznstudio.titanium.recipe.generator.IJSONGenerator;
import com.hrznstudio.titanium.recipe.generator.IJsonFile;
import com.hrznstudio.titanium.recipe.generator.TitaniumSerializableProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.Arrays;
import java.util.Map;

import static net.minecraft.world.item.Items.*;
import static net.minecraft.world.item.Items.EMERALD;

public class MatterValueDataProvider extends TitaniumSerializableProvider {

    private Map<IJsonFile, IJSONGenerator> map;

    public MatterValueDataProvider(DataGenerator generatorIn, String modid) {
        super(generatorIn, modid);
    }

    @Override
    public void add(Map<IJsonFile, IJSONGenerator> map) {
        this.map = map;
        saveData(IRON_INGOT, metallic(9));
        saveData(GOLD_INGOT, metallic(9), precious(9));
        saveData(DIAMOND, precious(9*4));
        saveData(LAPIS_LAZULI, precious(1), earth(2), organic(2));
        saveData(COPPER_INGOT, precious(2), metallic(2));
        saveData(ANCIENT_DEBRIS, precious(18), nether(18));
        saveData(AMETHYST_SHARD, precious(4), earth(1));
        saveData(BUDDING_AMETHYST, precious(32), earth(8));
        saveData(COAL, earth(2), precious(0.25), organic(0.25));
        saveData(EMERALD, precious(9*4));
        saveData(new Item[]{EXPOSED_COPPER, WEATHERED_COPPER, OXIDIZED_COPPER}, precious(8), metallic(8));

        saveTag(ItemTags.LOGS, earth(8), organic(8));
        saveTag(ItemTags.SAPLINGS, earth(16), organic(16));
        saveData(MANGROVE_ROOTS, earth(16), organic(16));
        saveTag(ItemTags.LEAVES, organic(4));
        saveTag(ItemTags.SMALL_FLOWERS, earth(2), organic(2));
        saveTag(ItemTags.TALL_FLOWERS, earth(4), organic(4));
        saveTag(Tags.Items.MUSHROOMS, earth(2), living(2));
        saveData(BAMBOO, earth(0.5), organic(0.5));
        saveData(MOSS_BLOCK, earth(2), organic(2));

        saveData(STRING, organic(2), living(2));
        saveData(COBWEB, organic(18), living(18));
        saveData(GRASS, organic(4));
        saveData(FERN, organic(4));
        saveData(WET_SPONGE, organic(12), living(4), precious(4));

        saveData(DEAD_BUSH, earth(1), organic(1));
        saveData(SEAGRASS, earth(2), organic(2));
        saveData(SEA_PICKLE, earth(2), organic(2));
        saveData(SUGAR_CANE, earth(2), organic(2));
        saveData(KELP, earth(2), organic(2));
        saveData(PRISMARINE_SHARD, earth(4), organic(4));
        saveData(PRISMARINE_CRYSTALS, earth(4), organic(4));

        saveData(new Item[]{CRIMSON_FUNGUS, CRIMSON_ROOTS, WEEPING_VINES,
                WARPED_FUNGUS, WARPED_ROOTS, NETHER_SPROUTS, TWISTING_VINES}, organic(2), nether(2));

        saveTag(Tags.Items.STONE, earth(1));
        saveTag(Tags.Items.COBBLESTONE, earth(1));
        saveData(BLACKSTONE, earth(1), nether(1));
        saveData(OBSIDIAN, earth(4), nether(1));
        saveData(CRYING_OBSIDIAN, earth(4), nether(1), quantum(1));
        saveData(POINTED_DRIPSTONE, earth(1));
        saveData(CALCITE, earth(2));
        saveData(DIRT, earth(1));
        saveData(ROOTED_DIRT, earth(1));
        saveData(MYCELIUM, earth(1), organic(4));
        saveData(GRASS_BLOCK, earth(1), organic(1));
        saveData(PODZOL, earth(1), organic(1));
        saveData(ICE, earth(4), organic(4));
        saveData(SNOWBALL, earth(1));
        saveData(MUD, earth(1));
        saveData(FLINT, earth(1));
        saveData(CLAY_BALL, earth(2));
        saveData(new Item[]{SAND, GRAVEL, RED_SAND}, earth(1));
        saveData(new Item[]{WARPED_NYLIUM, CRIMSON_NYLIUM}, organic(2), nether(1));
        saveData(new Item[]{WHITE_CONCRETE, ORANGE_CONCRETE, MAGENTA_CONCRETE, LIGHT_BLUE_CONCRETE, YELLOW_CONCRETE,
                LIME_CONCRETE, PINK_CONCRETE, GRAY_CONCRETE, LIGHT_GRAY_CONCRETE, CYAN_CONCRETE, PURPLE_CONCRETE, BLUE_CONCRETE,
                BROWN_CONCRETE, GREEN_CONCRETE, RED_CONCRETE, BLACK_CONCRETE}, earth(1.25), organic(0.25));

        saveData(NETHERRACK, nether(1));
        saveData(SOUL_SAND, nether(2), earth(2));
        saveData(SOUL_SOIL, nether(2), earth(2));
        saveData(BASALT, nether(2), earth(2));
        saveData(END_STONE, ender(1));

        saveData(NETHER_STAR, quantum(64), nether(16));

        saveData(REDSTONE, precious(2), earth(2));
        saveData(SLIME_BALL, living(2), earth(2));
        saveData(HONEY_BLOCK, living(18), earth(18));
        saveData(ECHO_SHARD, quantum(1), living(4));
        saveData(HONEYCOMB, living(2), earth(2));
        saveData(NETHER_WART, living(2), nether(2));
        //saveData(WARPED_NYLIUM, living(2*9), nether(2*9));
        saveData(CHORUS_FRUIT, living(2), ender(8));

        saveData(QUARTZ, precious(4), nether(2));
        saveData(GLOWSTONE_DUST, precious(1), nether(2));

        saveData(FEATHER, living(2), organic(2));
        saveData(LEATHER, living(2), organic(2));
        saveData(WHEAT, organic(2), earth(2));
        saveData(GUNPOWDER, organic(2), earth(2));
        saveData(ENDER_PEARL, organic(4), quantum(1), ender(2));
        saveData(new Item[]{CHORUS_PLANT, CHORUS_FLOWER}, organic(1), quantum(1));
        saveData(GLOWSTONE, nether(4), precious(4));
        saveData(new Item[]{EGG, PUMPKIN, CARVED_PUMPKIN, PORKCHOP, APPLE, COD, SALMON, TROPICAL_FISH, PUFFERFISH, MELON_SLICE, BEEF, CHICKEN, POTATO, POISONOUS_POTATO, CARROT, MUTTON, RABBIT, BEETROOT, GLOW_BERRIES, SWEET_BERRIES,RABBIT_FOOT}, organic(4), living(4));
        saveData(GHAST_TEAR, living(2), organic(2), nether(2));
        saveData(BLAZE_ROD, living(2), organic(2), nether(2));
        saveData(new Item[]{ROTTEN_FLESH, SPIDER_EYE, BONE, INK_SAC, RABBIT_HIDE, GLOW_INK_SAC, SCUTE, NAUTILUS_SHELL, HEART_OF_THE_SEA}, living(2), organic(2));
        saveData(DRAGON_BREATH, living(2), organic(2), quantum(2));
        saveData(PHANTOM_MEMBRANE, living(2), organic(2), nether(2));
        saveData(SHULKER_SHELL, living(4), nether(8));
        saveData(TOTEM_OF_UNDYING, precious(16), quantum(8));
        saveData(COCOA_BEANS, organic(1), earth(1));
        saveData(ENCHANTED_GOLDEN_APPLE, living(4), precious(9*8*9));

        saveTag(ItemTags.MUSIC_DISCS, precious(3.3), quantum(1));
        saveData(new Item[]{SCULK, SCULK_VEIN}, organic(1), quantum(1));
        saveData(new Item[]{SCULK_CATALYST, SCULK_SHRIEKER, SCULK_SENSOR}, organic(8), quantum(8));
        saveData(new Item[]{WITHER_SKELETON_SKULL, PLAYER_HEAD, ZOMBIE_HEAD, CREEPER_HEAD, PIGLIN_HEAD, DRAGON_HEAD, SKELETON_SKULL}, organic(8));
        saveData(new Item[]{DEAD_TUBE_CORAL_BLOCK, DEAD_BRAIN_CORAL_BLOCK, DEAD_BUBBLE_CORAL_BLOCK, DEAD_FIRE_CORAL_BLOCK, DEAD_HORN_CORAL_BLOCK}, organic(6));
        saveData(new Item[]{CACTUS, VINE, TALL_GRASS, LARGE_FERN, SUNFLOWER, LILAC, ROSE_BUSH, PEONY, PITCHER_PLANT, BIG_DRIPLEAF, TORCHFLOWER_SEEDS, PITCHER_POD, SMALL_DRIPLEAF, LILY_PAD, HANGING_ROOTS, GLOW_LICHEN}, earth(1), organic(1));
        saveData(new Item[]{TUBE_CORAL_BLOCK, BRAIN_CORAL_BLOCK, BUBBLE_CORAL_BLOCK, FIRE_CORAL_BLOCK, HORN_CORAL_BLOCK}, organic(6), living(4));
        saveData(new Item[]{TUBE_CORAL, BRAIN_CORAL, BUBBLE_CORAL, FIRE_CORAL, HORN_CORAL, TUBE_CORAL_FAN, BRAIN_CORAL_FAN, BUBBLE_CORAL_FAN, FIRE_CORAL_FAN, HORN_CORAL_FAN}, organic(2), living(1));
        saveData(new Item[]{DEAD_BRAIN_CORAL, DEAD_BUBBLE_CORAL, DEAD_FIRE_CORAL, DEAD_HORN_CORAL, DEAD_TUBE_CORAL, DEAD_TUBE_CORAL_FAN, DEAD_BRAIN_CORAL_FAN, DEAD_BUBBLE_CORAL_FAN, DEAD_FIRE_CORAL_FAN, DEAD_HORN_CORAL_FAN}, organic(2));

    }

    private void saveData(Item item, MatterValue... instances) {
        var rl = BuiltInRegistries.ITEM.getKey(item);
        var recipe = new MatterValueRecipe(new ResourceLocation(rl.getNamespace(), rl.getNamespace() + "/items/" + rl.getPath()), Ingredient.of(item), instances);
        map.put(recipe, recipe);
    }

    private void saveData(Item[] items, MatterValue... instances) {
        Arrays.stream(items).forEach(item -> saveData(item, instances));
    }

    private void saveTag(TagKey<Item> tag, MatterValue... instances) {
        var recipe = new MatterValueRecipe(new ResourceLocation(tag.location().getNamespace(), tag.location().getNamespace() + "/tags/" + tag.location().getPath()), Ingredient.of(tag), instances);
        map.put(recipe, recipe);
    }

    private static MatterValue metallic(double d){
        return new MatterValue(ReplicationRegistry.Matter.METALLIC.get(), d);
    }

    private static MatterValue earth(double d){
        return new MatterValue(ReplicationRegistry.Matter.EARTH.get(), d);
    }

    private static MatterValue organic(double d){
        return new MatterValue(ReplicationRegistry.Matter.ORGANIC.get(), d);
    }

    private static MatterValue quantum(double d){
        return new MatterValue(ReplicationRegistry.Matter.QUANTUM.get(), d);
    }

    private static MatterValue nether(double d){
        return new MatterValue(ReplicationRegistry.Matter.NETHER.get(), d);
    }

    private static MatterValue precious(double d){
        return new MatterValue(ReplicationRegistry.Matter.PRECIOUS.get(), d);
    }

    private static MatterValue ender(double d){
        return new MatterValue(ReplicationRegistry.Matter.ENDER.get(), d);
    }

    private static MatterValue living(double d){
        return new MatterValue(ReplicationRegistry.Matter.LIVING.get(), d);
    }
}
