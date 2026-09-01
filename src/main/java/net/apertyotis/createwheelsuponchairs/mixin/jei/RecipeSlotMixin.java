package net.apertyotis.createwheelsuponchairs.mixin.jei;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import mezz.jei.library.gui.ingredients.TagContentTooltipComponent;
import net.apertyotis.createwheelsuponchairs.foundation.AssemblyContentTooltipComponent;
import net.apertyotis.createwheelsuponchairs.foundation.AssemblyJunkSlotWidget;
import net.apertyotis.createwheelsuponchairs.foundation.RecipeSlotEx;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(value = RecipeSlot.class, remap = false)
public abstract class RecipeSlotMixin implements RecipeSlotEx {
    @Shadow
    @Unmodifiable
    private @Nullable List<Optional<ITypedIngredient<?>>> displayIngredients;

    @Unique
    private AssemblyJunkSlotWidget cwuc$widget;

    @Unique
    @Override
    public void cwuc$setWidget(AssemblyJunkSlotWidget widget) {
        cwuc$widget = widget;
    }

    @Unique
    public AssemblyJunkSlotWidget cwuc$getWidget() {
        return cwuc$widget;
    }

    @Inject(method = "getDisplayedIngredient", at = @At("TAIL"), cancellable = true)
    private void redirectGetDisplayedIngredient(CallbackInfoReturnable<Optional<ITypedIngredient<?>>> cir) {
        if (cwuc$widget != null && displayIngredients != null) {
            cir.setReturnValue(displayIngredients.get(cwuc$widget.index));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @WrapOperation(
        method = "addIngredientsToTooltip",
        at = @At(
            value = "NEW",
            target = "(Lmezz/jei/api/ingredients/IIngredientRenderer;Ljava/util/List;)Lmezz/jei/library/gui/ingredients/TagContentTooltipComponent;"
        )
    )
    private TagContentTooltipComponent redirectClientTooltipComponent(
        IIngredientRenderer renderer, List ingredients, Operation<TagContentTooltipComponent> original
    ) {
        if (cwuc$widget == null)
            return original.call(renderer, ingredients);
        return new AssemblyContentTooltipComponent(renderer, ingredients, cwuc$widget);
    }
}
