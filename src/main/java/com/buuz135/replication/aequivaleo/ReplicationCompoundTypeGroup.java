package com.buuz135.replication.aequivaleo;

import com.buuz135.replication.Replication;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.container.ICompoundContainer;
import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.ldtteam.aequivaleo.api.compound.type.group.ICompoundTypeGroup;
import com.ldtteam.aequivaleo.api.mediation.IMediationCandidate;
import com.ldtteam.aequivaleo.api.mediation.IMediationEngine;
import com.ldtteam.aequivaleo.api.recipe.equivalency.IEquivalencyRecipe;
import com.ldtteam.aequivaleo.vanilla.api.recipe.equivalency.ITagEquivalencyRecipe;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class ReplicationCompoundTypeGroup implements ICompoundTypeGroup {

    private final ResourceLocation RL = new ResourceLocation(Replication.MOD_ID, "matter_types");
    @Override
    public @NotNull IMediationEngine getMediationEngine()
    {
        return context -> {
            return context
                    .getCandidates()
                    .stream()
                    .min((o1, o2) -> {
                        if (o1.isSourceIncomplete() && !o2.isSourceIncomplete())
                            return 1;

                        if (!o1.isSourceIncomplete() && o2.isSourceIncomplete())
                            return -1;

                        if (o1.getValues().isEmpty() && !o2.getValues().isEmpty())
                            return 1;

                        if (!o1.getValues().isEmpty() && o2.getValues().isEmpty())
                            return -1;

                        return (int) (o1.getValues().stream().mapToDouble(CompoundInstance::getAmount).sum() -
                                o2.getValues().stream().mapToDouble(CompoundInstance::getAmount).sum());
                    })
                    .map(IMediationCandidate::getValues);
        };
    }


    @Override
    public ResourceLocation getRegistryName() {
        return RL;
    }

    @Override
    public String getDirectoryName() {
        return "replication/matter";
    }

    @Override
    public boolean shouldIncompleteRecipeBeProcessed(@NotNull final IEquivalencyRecipe iEquivalencyRecipe)
    {
        return false;
    }

    @Override
    public boolean canContributeToRecipeAsInput(IEquivalencyRecipe iEquivalencyRecipe, CompoundInstance compoundInstance) {
        return !(iEquivalencyRecipe instanceof ITagEquivalencyRecipe);
    }

    @Override
    public boolean canContributeToRecipeAsOutput(IEquivalencyRecipe iEquivalencyRecipe, CompoundInstance compoundInstance) {
        return !(iEquivalencyRecipe instanceof ITagEquivalencyRecipe);
    }

    public boolean isValidFor(final ICompoundContainer<?> iCompoundContainer, final CompoundInstance compoundInstance)
    {
        Object contents = iCompoundContainer.getContents();
        return contents instanceof ItemStack || contents instanceof Item || contents instanceof FluidStack;
    }

    private boolean isValidForGooRecipe(ICompoundContainer<?> container) {
        return container.getContents() instanceof ItemStack;
    }
}
