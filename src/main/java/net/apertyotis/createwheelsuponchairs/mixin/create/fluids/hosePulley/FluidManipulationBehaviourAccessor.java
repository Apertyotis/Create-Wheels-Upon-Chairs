package net.apertyotis.createwheelsuponchairs.mixin.create.fluids.hosePulley;

import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FluidManipulationBehaviour.class)
public interface FluidManipulationBehaviourAccessor {
    @Invoker("canDrainInfinitely")
    boolean invokeCanDrainInfinitely(Fluid fluid);
}
