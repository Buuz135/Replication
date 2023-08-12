package com.buuz135.replication.api.matter_fluid;

import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public interface IMatterTank {

    /**
     * @return MatterStack representing the fluid in the tank, MatterStack.EMPTY if the tank is empty.
     */
    @NotNull
    MatterStack getMatter();

    /**
     * @return Current amount of matter in the tank.
     */
    int getMatterAmount();

    /**
     * @return Capacity of this matter tank.
     */
    int getCapacity();

    /**
     * @param stack MatterStack holding the Fluid to be queried.
     * @return If the tank can hold the matter (EVER, not at the time of query).
     */
    boolean isMatterValid(MatterStack stack);

    /**
     * @param resource MatterStack attempting to fill the tank.
     * @param action   If SIMULATE, the fill will only be simulated.
     * @return Amount of fluid that was accepted (or would be, if simulated) by the tank.
     */
    int fill(MatterStack resource, IFluidHandler.FluidAction action);

    /**
     * @param maxDrain Maximum amount of fluid to be removed from the container.
     * @param action   If SIMULATE, the drain will only be simulated.
     * @return Amount of matter that was removed (or would be, if simulated) from the tank.
     */
    @NotNull
    MatterStack drain(int maxDrain, IFluidHandler.FluidAction action);

    /**
     * @param resource Maximum amount of fluid to be removed from the container.
     * @param action   If SIMULATE, the drain will only be simulated.
     * @return MatterStack representing fluid that was removed (or would be, if simulated) from the tank.
     */
    @NotNull
    MatterStack drain(MatterStack resource, IFluidHandler.FluidAction action);
}
