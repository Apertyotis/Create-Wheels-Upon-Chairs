package net.apertyotis.createwheelsuponchairs.mixin.create.foundation.blockEntity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsClient;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = FilteringRenderer.class, remap = false)
public abstract class FilteringRendererMixin {
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsClient;showHoverTip(Ljava/util/List;)V"
        )
    )
    private static void addEasyFilteringTooltip(
        ValueSettingsClient instance, List<MutableComponent> tip, Operation<Void> original
    ) {
        tip.add(Component.translatable("cwuc.easy_filter.hold_to_open"));
        original.call(instance, tip);
    }
}
