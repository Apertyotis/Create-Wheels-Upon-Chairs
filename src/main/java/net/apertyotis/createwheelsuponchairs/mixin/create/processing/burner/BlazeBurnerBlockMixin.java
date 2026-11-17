package net.apertyotis.createwheelsuponchairs.mixin.create.processing.burner;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlazeBurnerBlock.class)
public abstract class BlazeBurnerBlockMixin {
    @SuppressWarnings("rawtypes")
    @WrapOperation(
        method = "getStateForPlacement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;",
            ordinal = 0
        )
    )
    private Object voidBurning(
        BlockState instance, Property property, Comparable comparable, Operation<Object> original
    ) {
        if (AllConfig.void_burning && comparable == BlazeBurnerBlock.HeatLevel.SMOULDERING)
            comparable = BlazeBurnerBlock.HeatLevel.KINDLED;
        return original.call(instance, property, comparable);
    }
}
