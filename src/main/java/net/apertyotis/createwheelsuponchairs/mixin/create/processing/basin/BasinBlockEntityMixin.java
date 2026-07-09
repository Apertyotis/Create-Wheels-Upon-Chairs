package net.apertyotis.createwheelsuponchairs.mixin.create.processing.basin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.mixin.create.foundation.fluid.CombinedTankWrapperAccessor;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BasinBlockEntity.class)
public abstract class BasinBlockEntityMixin {

    @Shadow
    protected List<ItemStack> spoutputBuffer;

    @Shadow
    protected List<FluidStack> spoutputFluidBuffer;

    // 修复工作盆对 1 个空流体输出槽分别判断是否接受两种输出而导致吞流体的问题
    @Inject(method = "acceptFluidOutputsIntoBasin", at = @At("HEAD"), cancellable = true)
    private void redirectAcceptFluidOutputsIntoBasin(
        List<FluidStack> outputFluids, boolean simulate, IFluidHandler targetTank, CallbackInfoReturnable<Boolean> cir
    ) {
        if (targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler internalFluidHandler) {
            if (!AllConfig.processing_fix)
                return;
            // 只有 simulate 且输出多种流体时存在 bug
            if (!simulate || outputFluids.size() <= 1)
                return;

            // 尝试测试插入多流体，会记录被占用的槽位
            CombinedTankWrapperAccessor accessor = (CombinedTankWrapperAccessor) internalFluidHandler;
            IFluidHandler[] handlers = accessor.getFluidHandler();
            boolean[] occupied = new boolean[handlers.length];
            for (FluidStack stack: outputFluids) {
                // 原样复制 CombinedTankWrapper 的 fill 方法，但是会记录成功注入流体的储罐，并在之后测试中认为这些储罐无法插入
                // 假如有配方存在多个同种流体输出则会判断失误，但目前应该不用担心这个，因为没有设定多个同种流体输出的必要，流体输出也没有概率设定
                if (stack.isEmpty()) continue;

                int filled = 0;
                FluidStack resource = stack.copy();

                boolean fittingHandlerFound = false;
                Outer: for (boolean searchPass : Iterate.trueAndFalse) {
                    for (int i = 0; i < handlers.length; i++) {
                        if (occupied[i]) continue;

                        IFluidHandler iFluidHandler = handlers[i];

                        if (searchPass) {
                            for (int j = 0; j < iFluidHandler.getTanks(); j++)
                                if (FluidStack.isSameFluidSameComponents(iFluidHandler.getFluidInTank(j), resource))
                                    fittingHandlerFound = true;

                            if (!fittingHandlerFound)
                                continue;
                        }

                        int filledIntoCurrent = iFluidHandler.fill(resource, IFluidHandler.FluidAction.SIMULATE);
                        resource.shrink(filledIntoCurrent);
                        if (filledIntoCurrent != 0) {
                            occupied[i] = true;
                            filled += filledIntoCurrent;
                        }

                        if (resource.isEmpty())
                            break Outer;

                        // 这里 simulate 逻辑与实际 execute 不同，会尝试填充尽可能多的储罐
                        // 而 execute 时会方便玩家交互，无论 enforceVariety 是什么，都会限定一次操作最多填充一个储罐
                        if (accessor.isEnforceVariety() && (fittingHandlerFound || filledIntoCurrent != 0))
                            break Outer;
                    }
                }

                if (stack.getAmount() != filled) {
                    cir.setReturnValue(false);
                    return;
                }
            }

            cir.setReturnValue(true);
        } else {
            // 根据原方法语义，绝不应该传入其他类型的 handler
            throw new IllegalArgumentException(
                "[Create: Whale Upon Clouds] Handler type contract violated: expected SmartFluidTankBehaviour.InternalFluidHandler, got "
                    + targetTank.getClass().getName()
            );
        }
    }

    // 添加工作盆自动输出内容的护目镜显示
    @WrapMethod(method = "addToGoggleTooltip")
    private boolean addOutputBufferTooltip(List<Component> tooltip, boolean isPlayerSneaking, Operation<Boolean> original) {
        boolean result = original.call(tooltip, isPlayerSneaking);
        if (!AllConfig.basin_faucet_view || spoutputBuffer.isEmpty() && spoutputFluidBuffer.isEmpty())
            return result;

        new LangBuilder("")
            .add(Component.translatable("cwuc.goggle.basin_spoutput"))
            .forGoggles(tooltip);

        for (ItemStack item: spoutputBuffer) {
            if (item.isEmpty())
                continue;
            CreateLang.text("")
                .add(Component.translatable(item.getDescriptionId())
                    .withStyle(ChatFormatting.GRAY))
                .add(CreateLang.text(" x" + item.getCount())
                    .style(ChatFormatting.GREEN))
                .forGoggles(tooltip, 1);
        }

        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        for (FluidStack fluid: spoutputFluidBuffer) {
            if (fluid.isEmpty())
                continue;
            CreateLang.text("")
                .add(CreateLang.fluidName(fluid)
                    .add(CreateLang.text(" "))
                    .style(ChatFormatting.GRAY)
                    .add(CreateLang.number(fluid.getAmount())
                        .add(mb)
                        .style(ChatFormatting.BLUE)))
                .forGoggles(tooltip, 1);
        }
        return true;
    }
}
