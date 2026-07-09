package net.apertyotis.createwheelsuponchairs.mixin.create.fluids;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OpenEndedPipe.class)
public abstract class OpenEndedPipeMixin {
    @Definition(id = "convertToStill", method = "Lcom/simibubi/create/foundation/fluid/FluidHelper;convertToStill(Lnet/minecraft/world/level/material/Fluid;)Lnet/minecraft/world/level/material/Fluid;")
    @Expression("convertToStill(?) != ?")
    @ModifyExpressionValue(method = "provideFluidToSpace", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean redirectFluidTypeComparison(boolean original) {
        if (AllConfig.replace_any_flowing_fluid)
            return false;
        return original;
    }
}
