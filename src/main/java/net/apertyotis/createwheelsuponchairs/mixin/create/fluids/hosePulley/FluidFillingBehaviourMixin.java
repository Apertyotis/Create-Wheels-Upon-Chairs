package net.apertyotis.createwheelsuponchairs.mixin.create.fluids.hosePulley;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.fluids.transfer.FluidFillingBehaviour;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(FluidFillingBehaviour.class)
public abstract class FluidFillingBehaviourMixin {
    // 取消第一次 visited::add，使得软管滑轮可以放置最后一格液体
    @WrapOperation(
        method = "tryDeposit",
        at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z", ordinal = 0)
    )
    private boolean cancelFirstAdd(Set<?> instance, Object e, Operation<Boolean> original) {
        if (!AllConfig.hose_pulley_fix)
            return original.call(instance, e);
        return false;
    }
}
