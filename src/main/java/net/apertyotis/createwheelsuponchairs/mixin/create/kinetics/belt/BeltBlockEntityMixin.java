package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.belt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.content.belt.BeltBlockEntityEx;
import net.apertyotis.createwheelsuponchairs.content.belt.BeltScrollValueBehaviour;
import net.apertyotis.createwheelsuponchairs.content.belt.BeltValueBoxTransform;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BeltBlockEntity.class)
public class BeltBlockEntityMixin implements BeltBlockEntityEx {
    @Unique
    public ScrollValueBehaviour caa$targetSpeed;

    // 添加轮椅选项
    @Inject(method = "addBehaviours", at = @At("TAIL"))
    private void speedControlBehaviour(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        caa$targetSpeed = new BeltScrollValueBehaviour(
            Component.translatable("create.kinetics.speed_controller.rotation_speed"),
            (BeltBlockEntity)(Object) this, new BeltValueBoxTransform());
        caa$targetSpeed.between(-256, 256);
        caa$targetSpeed.requiresWrench();
        caa$targetSpeed.onlyActiveWhen(() -> AllConfig.easy_belt);
        behaviours.add(caa$targetSpeed);
    }

    @Override
    public float caa$getTargetSpeed() {
        int value = caa$targetSpeed == null ? 0 : caa$targetSpeed.getValue();
        return value == 0 ? ((BeltBlockEntity)(Object) this).getSpeed() : value;
    }

    @Override
    public void caa$setTargetSpeed(int value) {
        if (caa$targetSpeed != null)
            caa$targetSpeed.value = value;
    }

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;getSpeed()F"
        )
    )
    private float redirectGetSpeed1(BeltBlockEntity instance, Operation<Float> original) {
        if (!AllConfig.easy_belt)
            return original.call(instance);
        return caa$getTargetSpeed();
    }

    @WrapOperation(
        method = "getBeltMovementSpeed",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;getSpeed()F"
        )
    )
    private float redirectGetSpeed2(BeltBlockEntity instance, Operation<Float> original) {
        if (!AllConfig.easy_belt)
            return original.call(instance);
        return caa$getTargetSpeed();
    }

    @WrapOperation(
        method = "getMovementDirection(ZZ)Lnet/minecraft/core/Vec3i;",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;getSpeed()F"
        )
    )
    private float redirectGetSpeed3(BeltBlockEntity instance, Operation<Float> original) {
        if (!AllConfig.easy_belt)
            return original.call(instance);
        return caa$getTargetSpeed();
    }

    @WrapOperation(
        method = "canInsertFrom",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;getSpeed()F"
        )
    )
    private float redirectGetSpeed4(BeltBlockEntity instance, Operation<Float> original) {
        if (!AllConfig.easy_belt)
            return original.call(instance);
        return caa$getTargetSpeed();
    }

    @WrapOperation(
        method = "isOccupied",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/BeltBlockEntity;getSpeed()F"
        )
    )
    private float redirectGetSpeed5(BeltBlockEntity instance, Operation<Float> original) {
        if (!AllConfig.easy_belt)
            return original.call(instance);
        return caa$getTargetSpeed();
    }
}
