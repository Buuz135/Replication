package com.buuz135.replication.calculation;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.api.MatterCalculationStatus;
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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;


public class ReplicationCalculation {

    public static final Logger CALCULATOR_LOG = LogManager.getLogger("Replication Calculator");


    public static HashMap<ItemVariant, CalculationReference> SORTED_CALCULATION_REFERENCE = new HashMap<ItemVariant, CalculationReference>();
    public static Set<RecipeHolder<MatterValueRecipe>> DEFAULT_MATTER_RECIPE = new HashSet<>();
    public static HashMap<ItemVariant, MatterCompound> DEFAULT_MATTER_COMPOUND = new HashMap<ItemVariant, MatterCompound>();
    private static CompoundTag cachedSyncTag = new CompoundTag();
    public static MatterCalculationStatus STATUS = MatterCalculationStatus.NOT_CALCULATED;

    public static void init() {
        EventManager.forge(AddReloadListenerEvent.class, EventPriority.LOWEST).process(addReloadListenerEvent -> {
            addReloadListenerEvent.addListener((ResourceManagerReloadListener) resourceManager -> organizeRecipes(addReloadListenerEvent.getServerResources().getRecipeManager(), addReloadListenerEvent.getRegistryAccess()));
        }).subscribe();
        EventManager.forge(TagsUpdatedEvent.class, EventPriority.LOWEST).process(tagsUpdatedEvent -> {
            if (tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD)
                calculateRecipes(tagsUpdatedEvent.getRegistryAccess());
        }).subscribe();
        EventManager.forge(PlayerEvent.PlayerLoggedInEvent.class, EventPriority.LOWEST).process(playerLoggedInEvent -> {
            if (!cachedSyncTag.isEmpty() && playerLoggedInEvent.getEntity() instanceof ServerPlayer serverPlayer) {
                Replication.NETWORK.sendTo(new ReplicationCalculationPacket(cachedSyncTag), serverPlayer);
            }
        }).subscribe();
    }

    private static HashMap<Ingredient, MatterCompound> INGREDIENT_CACHE = new HashMap<>();

    public static void organizeRecipes(RecipeManager recipeManager, RegistryAccess registryAccess) {
        STATUS = MatterCalculationStatus.NOT_CALCULATED;
        CALCULATOR_LOG.info("Sorting recipes");
        INGREDIENT_CACHE = new HashMap<>();
        cachedSyncTag = new CompoundTag();
        //LOADING DEFAULT VALUES
        long time = System.currentTimeMillis();
        DEFAULT_MATTER_COMPOUND = new HashMap<>();
        DEFAULT_MATTER_RECIPE = new HashSet<>(recipeManager.getAllRecipesFor((RecipeType<MatterValueRecipe>) ReplicationRegistry.CustomRecipeTypes.MATTER_VALUE_RECIPE_TYPE.get()));


        //SORTING RECIPES
        SORTED_CALCULATION_REFERENCE = new HashMap<ItemVariant, CalculationReference>();
        time = System.currentTimeMillis();
        for (RecipeHolder<CraftingRecipe> craftingRecipe : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
            var result = craftingRecipe.value().getResultItem(registryAccess);
            var resultVariant = ItemVariants.normalize(result);
            SORTED_CALCULATION_REFERENCE.computeIfAbsent(resultVariant, string -> new CalculationReference(resultVariant, new HashSet<>())).getReferences().add(new RecipeReference(craftingRecipe.id(), result, new ArrayList<>(craftingRecipe.value().getIngredients())));
        }
        for (RecipeHolder<SmeltingRecipe> craftingRecipe : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {
            var result = craftingRecipe.value().getResultItem(registryAccess);
            var resultVariant = ItemVariants.normalize(result);
            SORTED_CALCULATION_REFERENCE.computeIfAbsent(resultVariant, string -> new CalculationReference(resultVariant, new HashSet<>())).getReferences().add(new RecipeReference(craftingRecipe.id(), result, new ArrayList<>(craftingRecipe.value().getIngredients())));
        }
        CALCULATOR_LOG.info("Sorted " + SORTED_CALCULATION_REFERENCE.size() + " Recipes in " + (System.currentTimeMillis() - time) + "ms");
    }

    public static void calculateRecipes(RegistryAccess registryAccess) {
        CALCULATOR_LOG.info("Updating replication calculation");
        new Thread(() -> {
            long time = System.currentTimeMillis();
            for (RecipeHolder<MatterValueRecipe> matterValueRecipe : DEFAULT_MATTER_RECIPE) {
                for (ItemStack item : matterValueRecipe.value().input.getItems()) {
                    var compound = new MatterCompound();
                    for (MatterValue matterValue : matterValueRecipe.value().matter) {
                        if (matterValue.getMatter() == null) {
                            System.out.println("NULL");
                        }
                        compound.add(matterValue);
                    }
                    DEFAULT_MATTER_COMPOUND.put(ItemVariants.normalize(item), compound);
                }
            }
            CALCULATOR_LOG.info("Loaded default values in " + (System.currentTimeMillis() - time) + "ms");

            //RESOLVING VALUES

            /*
            time = System.currentTimeMillis();
            CALCULATOR_LOG.info("minecraft:iron_block");
            var resolved = SORTED_CALCULATION_REFERENCE.get(Items.IRON_BLOCK).resolve(0, new HashSet<>(), new HashSet<>(),true);
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

                    Set<ItemVariant> variants = ItemVariants.getVariants(item, registryAccess);
                    for (ItemVariant variant : variants) {
                        try {
                            var stack = variant.stack();
                            if (stack.isEmpty()) continue;
                            //if (InvUtil.hasExtraComponents(stack)) continue;
                            if (!DEFAULT_MATTER_COMPOUND.containsKey(variant) && !SORTED_CALCULATION_REFERENCE.containsKey(variant)) {
                                continue;
                            }
                            var compound = getMatterCompound(variant, 0, new HashSet<>(), new HashSet<>(), false);
                            // CALCULATOR_LOG.info("---------------------------------------------");
                            if (compound != null && !compound.getValues().isEmpty()) {
                                if (false) CALCULATOR_LOG.info(item + " -> " + compound.toString());
                                tempTag.put(ItemVariants.getName(variant), compound.serializeNBT(registryAccess));
                                ++amount;
                            }
                        } catch (Exception e) {
                            // TODO: Could we add more handy information?
                            CALCULATOR_LOG.info("Failed to calculate a variant of " + item, e);
                        }
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
            STATUS = MatterCalculationStatus.CALCULATED;
        }, "Replication").start();

    }

    @Nullable
    public static MatterCompound getMatterCompound(ItemStack stack) {
        return getMatterCompound(ItemVariants.normalize(stack), 0, new HashSet<>(), new HashSet<>(), false);
    }

    private static MatterCompound getMatterCompound(ItemVariant variant, int depth, Set<String> visitedRecipes, Set<Item> visitedCalculations, boolean printDebug) {
        MatterCompound result = null;
        //GET FROM DEFAULT VALUES
        result = getMatterCompound(variant, depth, visitedRecipes, visitedCalculations, printDebug, result);
        return result;
    }

    private static MatterCompound getMatterCompound(ItemVariant variant, int depth, Set<String> visitedRecipes, Set<Item> visitedCalculations, boolean printDebug, MatterCompound result) {
        var defaultValue = getDefaultValue(variant);
        if (defaultValue != null) {
            if (printDebug)
                CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "Found default value for " + variant.stack().toString());
            if (result == null) {
                result = defaultValue;
            } else {
                result = result.compare(defaultValue);
            }
        } else {
            //CALCULATE
            if(ReplicationConfig.RecipeCalculation.MAX_RECIPE_DEPTH == 0) return null;
            if (variant.stack().is(ReplicationTags.SKIP_CALCULATION)) return null;
            if (SORTED_CALCULATION_REFERENCE.containsKey(variant)) {
                if (printDebug)
                    CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "Calculating value for " + variant.stack());
                var temp = SORTED_CALCULATION_REFERENCE.get(variant).resolve(depth, visitedRecipes, visitedCalculations, printDebug);
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

    private static MatterCompound getMatterCompound(Ingredient input, int depth, Set<String> visitedRecipes, Set<Item> visitedCalculations, boolean printDebug) {
        var cached = INGREDIENT_CACHE.get(input);
        if (cached != null) return cached;
        MatterCompound result = null;
        for (ItemStack item : input.getItems()) {
            var temp = getMatterCompound(ItemVariants.normalize(item), depth, visitedRecipes, visitedCalculations, printDebug, result);
            if (ReplicationConfig.RecipeCalculation.SUBTRACT_CRAFTING_REMAINING_ITEM && temp != null && item.hasCraftingRemainingItem() && !item.is(ReplicationTags.DONT_CHECK_FOR_CRAFTING_RESULT)) {
                var craftingRemainingItem = item.getCraftingRemainingItem();
                if (ItemStack.isSameItem(craftingRemainingItem, item)) {
                    temp = new MatterCompound();
                } else {
                    var remaining = getMatterCompound(ItemVariants.normalize(craftingRemainingItem), depth, visitedRecipes, visitedCalculations, printDebug, result);
                    if (remaining != null) {
                        temp = temp.duplicate().substract(remaining);
                    }
                }
            }
            if (result == null) {
                result = temp;
            } else {
                result = result.compare(temp);
            }
        }
        if (result != null) INGREDIENT_CACHE.put(input, result);
        return result;
    }

    private static MatterCompound getDefaultValue(ItemVariant variant) {
        return DEFAULT_MATTER_COMPOUND.get(variant);
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

        public MatterCompound getCachedCompound() {
            return cachedCompound;
        }

        public void setCachedCompound(MatterCompound cachedCompound) {
            this.cachedCompound = cachedCompound;
        }
    }

    private static class CalculationReference {

        private final Set<RecipeReference> references;
        private final ItemVariant variant;
        private final Item name;
        private boolean resolved = false;
        private MatterCompound cached;

        public CalculationReference(ItemVariant variant, Set<RecipeReference> references) {
            this.references = references;
            this.variant = variant;
            this.name = variant.stack().getItem();
        }

        public MatterCompound resolve(int depth, Set<String> visitedRecipes, Set<Item> visitedCalculations, boolean printDebug) {
            if (visitedCalculations.contains(name)) {
                return null;
            }
            visitedCalculations.add(name);
            if (references.size() == 0) {
                CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "FOUND NO RECIPES FOR " + variant.stack().toString());
            }
            if (resolved) {
                if (printDebug)
                    CALCULATOR_LOG.info(repeatChar(' ', depth + 1) + "\\" + repeatChar('_', depth + 1) + "RESOLVED_" + (cached == null ? null : cached.toString()));
                return cached;
            }
            MatterCompound result = getDefaultValue(variant);
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
                            var tempVisitedRecipes = new HashSet<>(visitedRecipes);
                            var tempVisitedCalculations = new HashSet<>(visitedCalculations);
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

        public Set<RecipeReference> getReferences() {
            return references;
        }
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
