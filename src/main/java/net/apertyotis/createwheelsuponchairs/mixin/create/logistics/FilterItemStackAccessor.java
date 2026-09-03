package net.apertyotis.createwheelsuponchairs.mixin.create.logistics;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FilterItemStack.class)
public interface FilterItemStackAccessor {
    @Accessor("filterFluidStack")
    FluidStack getFilterFluidStack();
    @Invoker("resolveFluid")
    void invokeResolveFluid(Level level);
}
