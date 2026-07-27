package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.millstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MillstoneBlockEntity.class)
public abstract class MillstoneBlockEntityMixin {
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/items/ItemStackHandler;getSlots()I"
        )
    )
    private int getCorrectLimit(ItemStackHandler outputInv, Operation<Integer> original, @Cancellable CallbackInfo ci) {
        if (!AllConfig.millstone_fix)
            return original.call(outputInv);
        for (int i = 0; i < outputInv.getSlots(); i++) {
            ItemStack stackIn = outputInv.getStackInSlot(i);
            if (stackIn.getCount() >= Math.min(outputInv.getSlotLimit(i), stackIn.getOrDefault(DataComponents.MAX_STACK_SIZE, 64))) {
                ci.cancel();
                break;
            }
        }
        return 0;
    }
}
