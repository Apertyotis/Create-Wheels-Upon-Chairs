package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.deployer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DeployerBlockEntity.class)
public abstract class DeployerBlockEntityMixin {

    @Shadow
    protected DeployerFakePlayer player;

    @Shadow
    protected List<ItemStack> overflowItems;

    @Shadow
    protected FilteringBehaviour filtering;

    // 提前机械手溢出物品的判断，使额外产物能立即排出
    @Inject(method = "activate", at = @At("TAIL"))
    private void afterActivate(CallbackInfo ci) {
        if (!AllConfig.deployer_instant_output || !overflowItems.isEmpty())
            return;

        ItemStack stack = player.getMainHandItem();
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (overflowItems.size() > 10)
                break;
            ItemStack item = inventory.getItem(i);
            if (item.isEmpty())
                continue;
            if (item != stack || !filtering.test(item)) {
                overflowItems.add(item);
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    // 取消原先溢出物品的判断
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;getContainerSize()I"
        )
    )
    private int cancelOldOverflowItemsHandle(Inventory instance, Operation<Integer> original) {
        if (AllConfig.deployer_instant_output)
            return 0;
        else
            return original.call(instance);
    }
}
