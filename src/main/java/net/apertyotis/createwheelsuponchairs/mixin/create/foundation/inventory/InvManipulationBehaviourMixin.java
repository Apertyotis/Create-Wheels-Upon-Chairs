package net.apertyotis.createwheelsuponchairs.mixin.create.foundation.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(InvManipulationBehaviour.class)
public abstract class InvManipulationBehaviourMixin {
    // 调整筛选条件的判断顺序，先考虑是否满足过滤器，再考虑是否能插入目标存储
    @WrapOperation(
        method = "getFilterTest",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/Predicate;and(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;"
        )
    )
    private Predicate<ItemStack> revertedAnd(
        Predicate<ItemStack> insertTest, Predicate<ItemStack> filterTest, Operation<Predicate<ItemStack>> original
    ) {
        if (!AllConfig.fast_logistics)
            return original.call(insertTest, filterTest);
        return filterTest.and(insertTest);
    }
}
