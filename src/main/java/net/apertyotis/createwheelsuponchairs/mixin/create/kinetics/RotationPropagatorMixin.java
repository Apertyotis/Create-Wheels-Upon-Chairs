
package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.apertyotis.createwheelsuponchairs.content.belt.BeltBlockEntityEx;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RotationPropagator.class, remap = false)
public abstract class RotationPropagatorMixin {
    @WrapOperation(
        method = "propagateNewSource",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;getSpeed()F"
        )
    )
    private static float getBeltKineticSpeed(KineticBlockEntity instance, Operation<Float> original) {
        if (instance instanceof BeltBlockEntityEx ex)
            return ex.caa$getKineticSpeed();
        else
            return original.call(instance);
    }
}