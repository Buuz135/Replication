package com.buuz135.replication.api.matter_fluid;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

@AutoRegisterCapability
public interface IMatterHandler {


    /**
     * Returns the number of matter storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    int getTanks();

    /**
     * Returns the MatterStack in a given tank.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This MatterStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED MatterStack</em></strong>
     * </p>
     *
     * @param tank Tank to query.
     * @return MatterStack in a given tank. MatterStack.EMPTY if the tank is empty.
     */
    @NotNull
    MatterStack getMatterInTank(int tank);

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @param tank Tank to query.
     * @return The maximum fluid amount held by the tank.
     */
    int getTankCapacity(int tank);

    /**
     * This function is a way to determine which matter can exist inside a given handler. General purpose tanks will
     * basically always return TRUE for this.
     *
     * @param tank  Tank to query for validity
     * @param stack Stack to test with for validity
     * @return TRUE if the tank can hold the MatterStack, not considering current state.
     * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
     */
    boolean isMatterValid(int tank, @NotNull MatterStack stack);

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource MatterStack representing the Matter and maximum amount of matter to be filled.
     * @param action   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    int fill(MatterStack resource, IFluidHandler.FluidAction action);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IMatterHandler.
     *
     * @param resource MatterStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return MatterStack representing the Matter and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    MatterStack drain(MatterStack resource, IFluidHandler.FluidAction action);

    /**
     * Drains matter out of internal tanks, distribution is left entirely to the IMatterHandler.
     * <p>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    MatterStack drain(int maxDrain, IFluidHandler.FluidAction action);

}
