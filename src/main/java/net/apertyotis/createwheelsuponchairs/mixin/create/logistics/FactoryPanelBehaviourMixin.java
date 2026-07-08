package net.apertyotis.createwheelsuponchairs.mixin.create.logistics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FactoryPanelBehaviour.class, remap = false)
public abstract class FactoryPanelBehaviourMixin {
    @Shadow
    private int lastReportedUnloadedLinks;

    /**
     * 修复区块重载时工厂仪表发送多余请求的问题<br>
     * 详见 Create PR <a href="https://github.com/Creators-of-Create/Create/pull/9649">#9649</a>
     */
    @Inject(method = "tickStorageMonitor", at = @At("HEAD"))
    private void validateNetwork(CallbackInfo ci, @Share("unloadedLinkCountOld") LocalIntRef unloadedLinkCountOld) {
        if (!AllConfig.logistics_fix)
            return;
        FactoryPanelBehaviour behaviour = (FactoryPanelBehaviour)(Object) this;
        int unloadedLinkCount = behaviour.getUnloadedLinks();
        unloadedLinkCountOld.set(unloadedLinkCount);
        FactoryPanelBlockEntity panelBE = behaviour.panelBE();
        if (!panelBE.restocker && unloadedLinkCount == 0 && lastReportedUnloadedLinks != 0) {
            // All links have been loaded, invalidate cache so we can get an accurate summary!
            // Otherwise, we will have to wait for 20 ticks and unnecessary packages will be sent!
            LogisticsManager.SUMMARIES.invalidate(behaviour.network);
        }
    }

    @WrapOperation(
        method = "tickStorageMonitor",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;getUnloadedLinks()I"
        )
    )
    private int getUnloadedLinkCountOld(
        FactoryPanelBehaviour instance, Operation<Integer> original,
        @Share("unloadedLinkCountOld") LocalIntRef unloadedLinkCountOld
    ) {
        if (!AllConfig.logistics_fix)
            return original.call(instance);
        return unloadedLinkCountOld.get();
    }
}
