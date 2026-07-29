package net.apertyotis.createwheelsuponchairs.mixin.createjustenoughtime;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.compat.jei.category.PressingCategory;
import net.apertyotis.createjustenoughtime.util.RecipeTooltip;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PressingCategory.class, remap = false, priority = 2000)
public abstract class PressingCategoryMixin {
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @WrapOperation(
        method = "getTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/apertyotis/createjustenoughtime/util/RecipeTooltip;withDelay(I)Lnet/apertyotis/createjustenoughtime/util/RecipeTooltip;"
        )
    )
    private RecipeTooltip redirectWithDelay(RecipeTooltip instance, int delay, Operation<RecipeTooltip> original) {
        if (AllConfig.belt_processing_tweak)
            return instance;
        else
            return original.call(instance, delay);
    }

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @WrapOperation(
        method = "getTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/apertyotis/createjustenoughtime/util/RecipeTooltip;withDetailed(Lnet/minecraft/network/chat/Component;)Lnet/apertyotis/createjustenoughtime/util/RecipeTooltip;"
        )
    )
    private RecipeTooltip redirectWithDetailed(RecipeTooltip instance, Component detail, Operation<RecipeTooltip> original) {
        if (AllConfig.belt_processing_tweak)
            detail = Component.translatable("cwuc.cjetcompat.detail.press");
        return original.call(instance, detail);
    }
}
