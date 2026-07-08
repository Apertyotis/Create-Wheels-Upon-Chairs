package net.apertyotis.createwheelsuponchairs.mixin.create.processing.basin;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public abstract class BasinOperatingBlockEntityMixin {
    // 修复盆加工类机器按原料数量比较配方优先级时未考虑流体原料的问题
    @Inject(method = "lambda$getMatchingRecipes$0", at = @At("HEAD"), cancellable = true)
    private static void redirectSortComparator(Recipe<?> r1, Recipe<?> r2, CallbackInfoReturnable<Integer> cir) {
        if (!AllConfig.processing_fix)
            return;
        int size1 = r1.getIngredients().size();
        int size2 = r2.getIngredients().size();
        if (r1 instanceof ProcessingRecipe<?, ?> pr1)
            size1 += pr1.getFluidIngredients().size();
        if (r2 instanceof ProcessingRecipe<?, ?> pr2)
            size2 += pr2.getFluidIngredients().size();
        cir.setReturnValue(size2 - size1);
    }
}
