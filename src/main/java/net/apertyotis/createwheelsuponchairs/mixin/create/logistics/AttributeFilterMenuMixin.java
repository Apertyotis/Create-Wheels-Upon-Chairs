package net.apertyotis.createwheelsuponchairs.mixin.create.logistics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AttributeFilterMenu.class, remap = false)
public abstract class AttributeFilterMenuMixin {
    /**
     * 修复属性过滤器中 shift + 左键快速选取槽位判断错误的问题<br>
     * 详见 Create PR <a href="https://github.com/Creators-of-Create/Create/pull/9729">#9729</a>
     */
    @WrapOperation(
        method = "quickMoveStack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;getItem(I)Lnet/minecraft/world/item/ItemStack;"
        ),
        remap = true
    )
    private ItemStack getCorrectSlot(Inventory instance, int i, Operation<ItemStack> original) {
        if (!AllConfig.fix_9729)
            return original.call(instance, i);
        return ((AttributeFilterMenu)(Object) this).slots.get(i).getItem();
    }
}
