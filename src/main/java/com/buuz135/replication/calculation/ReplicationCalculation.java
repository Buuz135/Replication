package com.buuz135.replication.calculation;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.packet.ReplicationCalculationPacket;
import com.buuz135.replication.recipe.MatterValueRecipe;
import com.buuz135.replication.util.ReplicationTags;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ReplicationCalculation {

    public static final Logger CALCULATOR_LOG = LogManager.getLogger("Replication Calculator");


    public static HashMap<String, CalculationReference> SORTED_CALCULATION_REFERENCE = new HashMap<String, CalculationReference>();
    public static List<RecipeHolder<MatterValueRecipe>> DEFAULT_MATTER_RECIPE = new ArrayList<>();
    public static HashMap<String, MatterCompound> DEFAULT_MATTER_COMPOUND = new HashMap<String, MatterCompound>();
    private static CompoundTag cachedSyncTag = new CompoundTag();

    public static void init() {
        EventManager.forge(AddReloadListenerEvent.class).process(addReloadListenerEvent -> {
            addReloadListenerEvent.addListener((ResourceManagerReloadListener) resourceManager -> organizeRecipes(addReloadListenerEvent.getServerResources().getRecipeManager(), addReloadListenerEvent.getRegistryAccess()));
        }).subscribe();
        EventManager.forge(TagsUpdatedEvent.class).process(tagsUpdatedEvent -> {
            if (tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD)
                calculateRecipes(tagsUpdatedEvent.getRegistryAccess());
        }).subscribe();
        EventManager.forge(PlayerEvent.PlayerLoggedInEvent.class).process(playerLoggedInEvent -> {
            if (!cachedSyncTag.isEmpty() && playerLoggedInEvent.getEntity() instanceof ServerPlayer serverPlayer) {
                Replication.NETWORK.sendTo(new ReplicationCalculationPacket(cachedSyncTag), serverPlayer);
            }
        }).subscribe();
    }

    private static HashMap<Ingredient, MatterCompound> INGREDIENT_CACHE = new HashMap<>();

    public static String getNameFromStack(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    public static void organizeRecipes(RecipeManager recipeManager, RegistryAccess registryAccess) {
        CALCULATOR_LOG.info("Sorting recipes");
        INGREDIENT_CACHE = new HashMap<>();
        cachedSyncTag = new CompoundTag();
        //LOADING DEFAULT VALUES
        long time = System.currentTimeMillis();
        DEFAULT_MATTER_COMPOUND = new HashMap<>();
        DEFAULT_MATTER_RECIPE = recipeManager.getAllRecipesFor((RecipeType<MatterValueRecipe>) ReplicationRegistry.CustomRecipeTypes.MATTER_VALUE_RECIPE_TYPE.get());


        //SORTING RECIPES
        SORTED_CALCULATION_REFERENCE = new HashMap<String, CalculationReference>();
        time = System.currentTimeMillis();
        for (RecipeHolder<CraftingRecipe> craftingRecipe : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
            var result = craftingRecipe.value().getResultItem(registryAccess);
            var rl = getNameFromStack(result);
            SORTED_CALCULATION_REFERENCE.computeIfAbsent(rl, string -> new CalculationReference(result, new ArrayList<>())).getReferences().add(new RecipeReference(craftingRecipe.id(), result, craftingRecipe.value().getIngredients()));
        }
        for (RecipeHolder<SmeltingRecipe> craftingRecipe : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {
            var result = craftingRecipe.value().getResultItem(registryAccess);
            var rl = getNameFromStack(result);
            SORTED_CALCULATION_REFERENCE.computeIfAbsent(rl, string -> new CalculationReference(result, new ArrayList<>())).getReferences().add(new RecipeReference(craftingRecipe.id(), result, craftingRecipe.value().getIngredients()));
        }
        CALCULATOR_LOG.info("Sorted Recipes in " + (System.currentTimeMillis() - time) + "ms");
    }

    public static void calculateRecipes(RegistryAccess registryAccess) {
        CALCULATOR_LOG.info("Updating replication calculation");
        new Thread(() -> {
            long time = System.currentTimeMillis();
            for (RecipeHolder<MatterValueRecipe> matterValueRecipe : DEFAULT_MATTER_RECIPE) {
                for (ItemStack item : matterValueRecipe.value().input.getItems()) {
                    var name = getNameFromStack(item);
                    var compound = new MatterCompound();
                    for (MatterValue matterValue : matterValueRecipe.value().matter) {
                        compound.add(matterValue);
                    }
                    DEFAULT_MATTER_COMPOUND.put(name, compound);
                }
            }
            CALCULATOR_LOG.info("Loaded default values in " + (System.currentTimeMillis() - time) + "ms");


            //RESOLVING VALUES

            /*
            time = System.currentTimeMillis();
            CALCULATOR_LOG.info("minecraft:red_dye");
            var resolved = SORTED_CALCULATION_REFERENCE.get("minecraft:red_dye").resolve(0, new ArrayList<>(), true);
            CALCULATOR_LOG.info(resolved);
            CALCULATOR_LOG.info("Checked oak in " + (System.currentTimeMillis() - time) + "ms");
            */


            var tempTag = new CompoundTag();
            time = System.currentTimeMillis();
            var timeTracker = System.currentTimeMillis();
            var totalAmount = BuiltInRegistries.ITEM.size();
            for (int i = 0; i < 1; i++) {
                var checkedAmount = 0;
                var amount = 0;
                for (Item item : BuiltInRegistries.ITEM) {
                    checkedAmount++;
                    if (System.currentTimeMillis() - timeTracker > 10000) {
                        CALCULATOR_LOG.info("Progress " + checkedAmount + " of " + totalAmount + " items");
                        timeTracker = System.currentTimeMillis();
                    }
                    try {
                        var stack = item.getDefaultInstance();
                        if (stack.isEmpty()) continue;
                        //if (InvUtil.hasExtraComponents(stack)) continue;
                        var rl = getNameFromStack(stack);
                        if (!DEFAULT_MATTER_COMPOUND.containsKey(rl) && !SORTED_CALCULATION_REFERENCE.containsKey(rl)) {
                            continue;
                        }
                        var compound = getMatterCompound(stack, 0, new ArrayList<>(), new ArrayList<>(), false);
                        // CALCULATOR_LOG.info("---------------------------------------------");
                        if (compound != null && !compound.getValues().isEmpty()) {
                            if (false) CALCULATOR_LOG.info(rl + " -> " + compound.toString());
                            tempTag.put(rl, compound.serializeNBT(registryAccess));
                            ++amount;
                        }
                    } catch (Exception e) {
                        CALCULATOR_LOG.info("Failed to calculate " + item, e);
                    }
                }
                CALCULATOR_LOG.info("Resolved " + amount + " values in " + (System.currentTimeMillis() - time) + "ms");
            }
            cachedSyncTag = tempTag;
            if (ServerLifecycleHooks.getCurrentServer() != null) {
                for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    Replication.NETWORK.sendTo(new ReplicationCalculationPacket(cachedSyncTag), player);
                }
            }
        }, "Replication").start();

    }

    private static class RecipeReference {

        private final ResourceLocation name;
        private final ItemStack output;
        private final List<Ingredient> inputs;
        private MatterCompound cachedCompound;

        public RecipeReference(ResourceLocation name, ItemStack output, List<Ingredient> inputs) {
            this.name = name;
            this.output = output;
            this.inputs = inputs;
        }

        public ResourceLocation getName() {
            return name;
        }

        public ItemStack getOutput() {
            return output;
        }

        public List<Ingredient> getInputs() {
            return inputs;
        }

        public void setCachedCompound(MatterCompound cachedCompound) {
            this.cachedCompound = cachedCompound;
        }

        public MatterCompound getCachedCompound() {
            return cachedCompound;
        }
    }

    @Nullable
    public static MatterCompound getMatterCompound(ItemStack stack) {
        return getMatterCompound(stack, 0, new ArrayList<>(), new ArrayList<>(), false);
    }

    private static MatterCompound getMatterCompound(ItemStack item, int depth, List<String> visitedRecipes, List<String> visitedCalculations, boolean printDebug) {
        MatterCompound result = null;
        //GET FROM DEFAULT VALUES
        result = getMatterCompound(item, depth, visitedRecipes, visitedCalculations, printDebug, result);
        return result;
    }

    private static MatterCompound getMatterCompound(ItemStack item, int depth, List<String> visitedRecipes, List<String> visitedCalculations, boolean printDebug, MatterCompound result) {
        var defaultValue = getDefaultValue(item);
        if (defaultValue != null) {
            if (printDebug)
                CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "Found default value for " + item.toString());
            if (result == null) {
                result = defaultValue;
            } else {
                result = result.compare(defaultValue);
            }
        } else {
            //CALCULATE
            if(ReplicationConfig.RecipeCalculation.MAX_RECIPE_DEPTH == 0) return null;
            if (item.is(ReplicationTags.SKIP_CALCULATION)) return null;
            var name = getNameFromStack(item);
            if (SORTED_CALCULATION_REFERENCE.containsKey(name)) {
                if (printDebug)
                    CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "Calculating value for " + item);
                var temp = SORTED_CALCULATION_REFERENCE.get(name).resolve(depth, visitedRecipes, visitedCalculations, printDebug);
                if (temp != null) {
                    if (result == null) {
                        result = temp;
                    } else {
                        result = result.compare(temp);
                    }
                }
            }
        }
        return result;
    }

    private static MatterCompound getMatterCompound(Ingredient input, int depth, List<String> visitedRecipes, List<String> visitedCalculations, boolean printDebug) {
        if (INGREDIENT_CACHE.containsKey(input)) {
            return INGREDIENT_CACHE.get(input);
        }
        MatterCompound result = null;
        for (ItemStack item : input.getItems()) {
            var temp = getMatterCompound(item, depth, visitedRecipes, visitedCalculations, printDebug, result);
            if (result == null) {
                result = temp;
            } else {
                result = result.compare(temp);
            }
        }
        if (result != null) INGREDIENT_CACHE.put(input, result);
        return result;
    }

    private static class CalculationReference {

        private final List<RecipeReference> references;
        private final ItemStack stack;
        private final String name;
        private boolean resolved = false;
        private MatterCompound cached;

        public CalculationReference(ItemStack stack, List<RecipeReference> references) {
            this.references = references;
            this.stack = stack;
            this.name = getNameFromStack(stack);
        }

        public MatterCompound resolve(int depth, List<String> visitedRecipes, List<String> visitedCalculations, boolean printDebug) {
            if (visitedCalculations.contains(name)) {
                return null;
            }
            visitedCalculations.add(name);
            if (references.size() == 0) {
                CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "FOUND NO RECIPES FOR " + stack.toString());
            }
            if (resolved) {
                if (printDebug)
                    CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "RESOLVED_" + (cached == null ? null : cached.toString()));
                return cached;
            }
            MatterCompound result = getDefaultValue(stack);
            if (result != null) {
                resolved = true;
                this.cached = result;
                if (printDebug)
                    CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "DEFAULT_" + cached.toString());
                return this.cached;
            }
            // SAFETY CHECKS
            if (visitedCalculations.size() > ReplicationConfig.RecipeCalculation.MAX_RECIPE_DEPTH || visitedRecipes.size() > ReplicationConfig.RecipeCalculation.MAX_VISITED_RECIPES) { //TODO CONFIG
                if (printDebug)
                    CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "POSIBLE INFINTE LOOP FOUND, BREAKING");
                //resolved = true;
                //this.cached = null;
                return this.cached;
            }
            for (RecipeReference reference : references) {
                if (printDebug)
                    CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "Visiting Recipe: " + reference.getName());
                if (!visitedRecipes.contains(reference.getName().toString())) {
                    if (printDebug)
                        CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + reference.getName().toString());
                    MatterCompound temp = null;
                    if (reference.getCachedCompound() != null) {
                        temp = reference.getCachedCompound();
                        if (printDebug)
                            CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "RECIPECACHE" + temp);
                    } else {
                        visitedRecipes.add(reference.getName().toString());
                        for (Ingredient input : reference.inputs) {
                            if (input.getItems().length == 0) continue;
                            var tempVisitedRecipes = new ArrayList<>(visitedRecipes);
                            var tempVisitedCalculations = new ArrayList<>(visitedCalculations);
                            var inputMatter = getMatterCompound(input, depth + 1, tempVisitedRecipes, tempVisitedCalculations, printDebug);
                            tempVisitedRecipes = null;
                            tempVisitedCalculations = null;
                            // ONE INGREDIENT IS NULL SO ITS NOT VALID
                            if (inputMatter == null) {
                                if (printDebug) {
                                    CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "ONE INGREDIENT DOESNT HAVE VALUE " + input.getItems().length);
                                    for (ItemStack item : input.getItems()) {
                                        CALCULATOR_LOG.info(repeatChar(' ', depth + 2) + "\\" + repeatChar('_', depth + 2) + item.toString());
                                    }
                                }
                                temp = null;
                                break;
                            } else {
                                if (temp == null) {
                                    temp = new MatterCompound();
                                }
                                temp.add(inputMatter);
                            }
                        }
                        if (temp != null) {
                            temp.divide(reference.getOutput().getCount());
                            reference.setCachedCompound(temp);
                        }
                        if (printDebug)
                            CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "CALCULATED_" + temp);
                    }
                    if (temp != null) {
                        if (result == null) {
                            if (!temp.getValues().isEmpty()) result = temp;
                        } else {
                            result = result.compare(temp);
                        }
                    }
                    //CLEANING
                    temp = null;
                } else {
                    if (false) CALCULATOR_LOG.info("\\" + repeatChar('_', depth + 1) + "BROKEN LOOP");
                }
                //break;
            }
            if (result != null) {
                resolved = true;
                this.cached = result;

                return this.cached;
            }
            return result;
        }

        public List<RecipeReference> getReferences() {
            return references;
        }
    }

    private static MatterCompound getDefaultValue(ItemStack stack) {
        if (DEFAULT_MATTER_COMPOUND.containsKey(getNameFromStack(stack))) {
            return DEFAULT_MATTER_COMPOUND.get(getNameFromStack(stack));
        }
        return null;
    }

    private static String repeatChar(char character, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must be non-negative.");
        }
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(character);
        }
        return builder.toString();
    }
}
