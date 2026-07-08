package net.apertyotis.createwheelsuponchairs.mixin.minecraft.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.GameType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GameType.class)
public abstract class GameTypeMixin {
    // 任何模式玩家可飞行，切换生存不打断飞行
    @SuppressWarnings("deprecation")
    @WrapOperation(
        method = "updatePlayerAbilities",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/player/Abilities;flying:Z",
            opcode = Opcodes.PUTFIELD,
            ordinal = 1
        )
    )
    private void alwaysAllowFly(Abilities instance, boolean value, Operation<Void> original) {
        if (!AllConfig.always_allow_flying) {
            original.call(instance, value);
            return;
        }
        // TODO: 考虑使用 CREATIVE_FLIGHT 以避免冲突
        instance.mayfly = true;
    }
}