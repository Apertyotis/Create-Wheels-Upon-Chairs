package net.apertyotis.createwheelsuponchairs.mixin.create.equipment;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.equipment.toolbox.RadialToolboxMenu;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.content.toolbox.BetterToolboxHandlerClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RadialToolboxMenu.class)
public abstract class RadialToolboxMenuMixin {
    @Expression("? / 10.0")
    @ModifyExpressionValue(method = "renderWindow", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float redirectOpenAnimationTicks(float original) {
        if (AllConfig.toolbelt_animation_ticks <= 0)
            return 1;
        return original * 10 / AllConfig.toolbelt_animation_ticks;
    }

    // 由于窗口拦截了按键事件，需要在此处追加处理
    @Inject(
        method = "keyReleased",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/equipment/toolbox/RadialToolboxMenu;onClose()V"
        )
    )
    private void onKeyReleased(int code, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        BetterToolboxHandlerClient handler = BetterToolboxHandlerClient.BETTER_TOOLBOX_HANDLER_CLIENT;
        handler.keyPressed = false;
        handler.keyBlocked = false;
        handler.holdTicks = 0;
    }
}
