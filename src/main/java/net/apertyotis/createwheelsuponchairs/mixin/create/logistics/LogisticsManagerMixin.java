package net.apertyotis.createwheelsuponchairs.mixin.create.logistics;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.foundation.CrossNetworkData;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(LogisticsManager.class)
public abstract class LogisticsManagerMixin {
    @Definition(id = "order", local = @Local(type = PackageOrderWithCrafts.class, argsOnly = true))
    @Expression("order")
    @ModifyExpressionValue(method = "findPackagersForRequest", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
    private static PackageOrderWithCrafts fixMetadata1(PackageOrderWithCrafts order) {
        if (!AllConfig.logistics_fix || CrossNetworkData.INSTANCE == null)
            return order;

        if (CrossNetworkData.INSTANCE.firstContextTracker == null) {
            CrossNetworkData.INSTANCE.firstContextTracker = order;
            return order;
        } else {
            CrossNetworkData.INSTANCE.firstContextTracker.stacks().addAll(order.stacks());
            return null;
        }
    }

    @WrapOperation(
        method = "findPackagersForRequest",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextInt()I"
        )
    )
    private static int fixMetadata2(Random r, Operation<Integer> original) {
        if (!AllConfig.logistics_fix || CrossNetworkData.INSTANCE == null)
            return original.call(r);

        if (!CrossNetworkData.INSTANCE.hasOrderId) {
            CrossNetworkData.INSTANCE.orderId = original.call(r);
            CrossNetworkData.INSTANCE.hasOrderId = true;
        }
        return CrossNetworkData.INSTANCE.orderId;
    }

    @ModifyArg(
        method = "findPackagersForRequest",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/logistics/packagerLink/LogisticallyLinkedBehaviour;processRequest(Lnet/minecraft/world/item/ItemStack;ILjava/lang/String;ILorg/apache/commons/lang3/mutable/MutableBoolean;ILcom/simibubi/create/content/logistics/stockTicker/PackageOrderWithCrafts;Lcom/simibubi/create/content/logistics/packager/IdentifiedInventory;)Lnet/createmod/catnip/data/Pair;"
        ),
        index = 3
    )
    private static int fixMetadata3(int linkIndex) {
        if (!AllConfig.logistics_fix || CrossNetworkData.INSTANCE == null)
            return linkIndex;
        return linkIndex + CrossNetworkData.INSTANCE.usedLinks;
    }

    @Inject(
        method = "findPackagersForRequest",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            ordinal = 4
        )
    )
    private static void fixMetadata4(CallbackInfoReturnable<Multimap<PackagerBlockEntity, PackagingRequest>> cir) {
        if (AllConfig.logistics_fix && CrossNetworkData.INSTANCE != null && CrossNetworkData.INSTANCE.finalLinkTracker != null)
            CrossNetworkData.INSTANCE.finalLinkTracker.setFalse();
    }

    @WrapOperation(
        method = "findPackagersForRequest",
        at = @At(
            value = "INVOKE",
            target = "Lorg/apache/commons/lang3/mutable/MutableBoolean;setTrue()V"
        )
    )
    private static void fixMetadata5(
        MutableBoolean finalLinkTracker, Operation<Void> original,
        @Local(name = "usedLinks") List<LogisticallyLinkedBehaviour> usedLinks
    ) {
        original.call(finalLinkTracker);
        if (AllConfig.logistics_fix && CrossNetworkData.INSTANCE != null) {
            CrossNetworkData.INSTANCE.finalLinkTracker = finalLinkTracker;
            CrossNetworkData.INSTANCE.usedLinks += usedLinks.size();
        }
    }
}
