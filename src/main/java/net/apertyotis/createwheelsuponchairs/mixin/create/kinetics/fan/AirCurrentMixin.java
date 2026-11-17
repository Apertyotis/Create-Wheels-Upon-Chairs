package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import net.apertyotis.createwheelsuponchairs.content.fan.FanGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AirCurrent.class)
public abstract class AirCurrentMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void noAirCurrent(CallbackInfo ci) {
        if (((AirCurrent)(Object) this).source instanceof FanGenerator fan && fan.cwuc$getHeatLevel() > 0)
            ci.cancel();
    }
}
