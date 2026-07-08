package net.apertyotis.createwheelsuponchairs.mixin.create.foundation.fluid;

import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CombinedTankWrapper.class, remap = false)
public interface CombinedTankWrapperAccessor {
    @Accessor("itemHandler")
    IFluidHandler[] getFluidHandler();

    @Accessor("enforceVariety")
    boolean isEnforceVariety();
}
