package net.apertyotis.createwheelsuponchairs.mixin.create.logistics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.api.packager.InventoryIdentifier.Pair;
import com.simibubi.create.content.logistics.packager.AllInventoryIdentifiers;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AllInventoryIdentifiers.class, remap = false)
public abstract class AllInventoryIdentifiersMixin {
    /**
     * 修复连接在大箱子两端的仓储链接器会报告双倍物品的问题<br>
     * 详见 Create PR <a href="https://github.com/Creators-of-Create/Create/pull/9793">#9793</a>
     */
    @WrapOperation(
        method = "chest",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/api/packager/InventoryIdentifier$Pair;"
        )
    )
    private static Pair sortedPos(BlockPos first, BlockPos second, Operation<Pair> original) {
        if (!AllConfig.logistics_fix)
            return original.call(first, second);
        boolean isFirstLower = first.compareTo(second) < 0;
        return original.call(isFirstLower ? first : second, isFirstLower ? second : first);
    }
}
