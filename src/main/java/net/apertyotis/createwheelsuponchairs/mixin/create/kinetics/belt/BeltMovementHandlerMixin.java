package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.belt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.content.belt.BeltBlockEntityEx;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeltMovementHandler.class)
public abstract class BeltMovementHandlerMixin {
    @WrapOperation(
        method = "transportEntity",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;getSpeed()F"
        )
    )
    private static float redirectGetSpeed(BeltBlockEntity instance, Operation<Float> original) {
        if (!AllConfig.easy_belt)
            return original.call(instance);
        return ((BeltBlockEntityEx) instance).caa$getTargetSpeed();
    }
}
