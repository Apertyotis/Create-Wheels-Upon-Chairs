package net.apertyotis.createwheelsuponchairs.mixin.create.logistics;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChuteBlockEntity.class, remap = false)
public abstract class ChuteBlockEntityMixin {

    // 修复斜溜槽底部漏风，不让斜溜槽输出到下方容器
    @ModifyExpressionValue(
        method = "handleDownwardOutput",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/util/LazyOptional;isPresent()Z",
            ordinal = 1
        )
    )
    private boolean redirectOutputToCapBelow(boolean original, @Local(name = "direction") Direction direction) {
        if (!AllConfig.no_chute_leaking) return original;

        if (direction != Direction.DOWN)
            return false;
        else
            return original;
    }
}
