package net.apertyotis.createwheelsuponchairs.mixin.create.logistics;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.foundation.CrossNetworkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FactoryPanelBehaviour.class)
public abstract class FactoryPanelBehaviourMixin {
    @Inject(
        method = "tickRequests",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;entrySet()Ljava/util/Set;",
            ordinal = 1
        )
    )
    private void initCrossNetworkData(CallbackInfo ci) {
        if (AllConfig.logistics_fix)
            CrossNetworkData.INSTANCE = new CrossNetworkData();
    }

    @Inject(
        method = "tickRequests",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;iterator()Ljava/util/Iterator;",
            ordinal = 0
        )
    )
    private void clearCrossNetworkData(CallbackInfo ci) {
        CrossNetworkData.INSTANCE = null;
    }
}
