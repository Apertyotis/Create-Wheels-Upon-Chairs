package net.apertyotis.createwheelsuponchairs.mixin.create.fluids;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.neoforged.neoforge.fluids.IFluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// 注入私有内部类
@Mixin(targets = "com.simibubi.create.content.fluids.OpenEndedPipe$OpenEndFluidHandler")
public abstract class OpenEndFluidHandlerMixin {
    // 修复一次性向世界排出超过 1000mB 液体时无法正确放置的问题
    @Definition(id = "hasBlockState", local = @Local(type = boolean.class, name = "hasBlockState"))
    @Expression("hasBlockState")
    @ModifyExpressionValue(method = "fill", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 2))
    private boolean recheckHashBlockState(boolean original) {
        if (!AllConfig.fluid_network_fix)
            return original;
        return FluidHelper.hasBlockState(((IFluidTank) this).getFluid().getFluid());
    }
}
