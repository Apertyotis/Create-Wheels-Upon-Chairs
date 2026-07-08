package net.apertyotis.createwheelsuponchairs.mixin.create.logistics;

import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LogisticallyLinkedBehaviour.class, remap = false)
public abstract class LogisticallyLinkedBehaviourMixin {
    /**
     * 修复区块重载时工厂仪表发送多余请求的问题<br>
     * 详见 Create PR <a href="https://github.com/Creators-of-Create/Create/pull/9649">#9649</a>
     */
    @Inject(
        method = "initialize",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/logistics/packagerLink/GlobalLogisticsManager;linkLoaded(Ljava/util/UUID;Lnet/minecraft/core/GlobalPos;)V",
            shift = At.Shift.AFTER
        )
    )
    private void keepAliveRegardlessRedstone(CallbackInfo ci) {
        if (!AllConfig.logistics_fix)
            return;
        // Call keepAlive regardless of redstone power.
        // Otherwise, when no redstone power is present
        // keepAlive won't be called until next lazy tick.
        LogisticallyLinkedBehaviour.keepAlive((LogisticallyLinkedBehaviour)(Object) this);
    }
}
