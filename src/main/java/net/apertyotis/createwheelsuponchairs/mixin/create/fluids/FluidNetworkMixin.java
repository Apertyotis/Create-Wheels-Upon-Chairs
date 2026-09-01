package net.apertyotis.createwheelsuponchairs.mixin.create.fluids;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.FluidNetwork;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FluidNetwork.class, remap = false)
public abstract class FluidNetworkMixin {
    @Shadow
    FluidStack fluid;

    /**
     * 阻止管道抽取 0mB 液体<br>
     * 详见 Create PR <a href="https://github.com/Creators-of-Create/Create/pull/10055">#10055</a>
     */
    @Definition(id = "flowSpeed", local = @Local(type = int.class, name = "flowSpeed"))
    @Expression("flowSpeed - ?")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int preventDrainZero(int original, @Cancellable CallbackInfo ci) {
        if (AllConfig.hose_pulley_fix && original <= 0)
            ci.cancel();
        return original;
    }

    // 简化抽取逻辑
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraftforge/fluids/FluidStack;EMPTY:Lnet/minecraftforge/fluids/FluidStack;",
            opcode = Opcodes.GETSTATIC,
            ordinal = 0
        )
    )
    private FluidStack directlyDrain(
        Operation<FluidStack> original,
        @Local(name = "flowSpeed") int flowSpeed,
        @Local(name = "action") IFluidHandler.FluidAction action,
        @Local(name = "handler") IFluidHandler handler,
        @Cancellable CallbackInfo ci
    ) {
        if (!AllConfig.smart_fluid_pipe)
            return original.call();
        FluidStack toExtract = fluid.copy();
        if (toExtract.isEmpty()) {
            ci.cancel();
            return FluidStack.EMPTY;
        }
        toExtract.setAmount(flowSpeed);
        FluidStack extracted = handler.drain(toExtract, action);
        if (extracted.isEmpty())
            ci.cancel();
        return extracted;
    }

    // 取消原抽取逻辑
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fluids/capability/IFluidHandler;getTanks()I"
        )
    )
    private int breakFindFluidLoop(IFluidHandler instance, Operation<Integer> original) {
        if (AllConfig.smart_fluid_pipe)
            return 0;
        else
            return original.call(instance);
    }
}
