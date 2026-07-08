package net.apertyotis.createwheelsuponchairs.mixin.create.contraption.glue;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionPacket;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(value = SuperGlueSelectionPacket.class, remap = false)
public abstract class SuperGlueSelectionPacketMixin {

    @Final
    @Shadow
    private BlockPos from;

    @Final
    @Shadow
    private BlockPos to;

    // 允许强力胶设置选区时无视方块是否相连，不影响实际连接逻辑
    @WrapOperation(
        method = "handle",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"
        )
    )
    private boolean ignoreGlueGroup(Set<BlockPos> instance, Object o, Operation<Boolean> original) {
        if (!AllConfig.hachimi_glue) {
            return original.call(instance, o);
        }
        return !from.equals(to);
    }
}
