package net.apertyotis.createwheelsuponchairs.mixin.create.processing.burner;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlazeBurnerBlockEntity.class, remap = false)
public abstract class BlazeBurnerBlockEntityMixin {
    @Shadow
    protected BlazeBurnerBlockEntity.FuelType activeFuel;

    @Shadow
    protected int remainingBurnTime;

    @Shadow
    protected abstract void setBlockHeat(BlazeBurnerBlock.HeatLevel heat);

    @Definition(id = "isCreative", field = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlockEntity;isCreative:Z")
    @Expression("?.isCreative")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean liquidBurningTick(boolean isCreative, @Cancellable CallbackInfo ci) {
        if (!isCreative) {
            if (AllConfig.void_burning) {
                if (activeFuel == BlazeBurnerBlockEntity.FuelType.NONE) {
                    activeFuel = BlazeBurnerBlockEntity.FuelType.NORMAL;
                    setBlockHeat(BlazeBurnerBlock.HeatLevel.KINDLED);
                }
                if (activeFuel == BlazeBurnerBlockEntity.FuelType.NORMAL) {
                    remainingBurnTime = Math.max(remainingBurnTime, 200);
                    ci.cancel();
                }
            }
        }
        return isCreative;
    }
}
