package net.apertyotis.createwheelsuponchairs.mixin.create.processing.sequenced;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SequencedAssemblyRecipe.class, remap = false)
public abstract class SequencedAssemblyRecipeMixin {

    @Shadow
    protected Ingredient ingredient;

    @Shadow
    public abstract ItemStack getTransitionalItem();

    @Inject(method = "appliesTo", at = @At("HEAD"), cancellable = true)
    private void exactlyAppliesTo(ResourceLocation id, ItemStack input, CallbackInfoReturnable<Boolean> cir) {
        if (!AllConfig.processing_fix)
            return;
        // 输入是否为半成品
        boolean incomplete = input.has(AllDataComponents.SEQUENCED_ASSEMBLY);
        // 输入是否匹配起始加工原料
        boolean matchStart = ingredient.test(input);

        if (matchStart && !incomplete) {
            // 仅匹配起始加工原料还不够，不允许对序列装配半成品执行起始加工
            cir.setReturnValue(true);
            return;
        }

        if (incomplete) {
            SequencedAssemblyRecipe.SequencedAssembly component = input.get(AllDataComponents.SEQUENCED_ASSEMBLY);

            // noinspection DataFlowIssue
            if (!component.id().equals(id)) {
                // 拒绝装配 id 不一致的原料
                cir.setReturnValue(false);
                return;
            }

            if (getTransitionalItem().getItem() == input.getItem()) {
                // 接受符合中间原料的输入
                cir.setReturnValue(true);
                return;
            }

            if (component.step() == 0 && matchStart) {
                // 特别地，允许Step标签为0的半成品（通常不可能）匹配起始加工
                cir.setReturnValue(true);
                return;
            }
        }

        cir.setReturnValue(false);
    }
}
