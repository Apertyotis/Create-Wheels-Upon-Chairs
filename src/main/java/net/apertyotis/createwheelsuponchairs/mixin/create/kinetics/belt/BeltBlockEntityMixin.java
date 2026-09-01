package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.belt;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.content.belt.BeltBlockEntityEx;
import net.apertyotis.createwheelsuponchairs.content.belt.BeltScrollValueBehaviour;
import net.apertyotis.createwheelsuponchairs.content.belt.BeltValueBoxTransform;
import net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.base.KineticBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = BeltBlockEntity.class, remap = false)
public abstract class BeltBlockEntityMixin extends KineticBlockEntity implements BeltBlockEntityEx {
    @Unique
    private int caa$stopWhenChanged = 0;
    @Unique
    public ScrollValueBehaviour caa$targetSpeed;

    public BeltBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (caa$stopWhenChanged > 0)
            caa$stopWhenChanged--;
    }

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
    public float getSpeed() {
        int value = caa$stopWhenChanged != 0 || caa$targetSpeed == null  || !AllConfig.easy_belt ? 0 :
            caa$targetSpeed.getValue();
        return value == 0 ? super.getSpeed() : value;
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        // 应力网络变化时，所有受影响机器会暂停1tick
        // 传送带可能和上方加工机器在相同网络，因此传送带变化时应该暂停，以免在机器暂停期间放行物品
        caa$stopWhenChanged = 2;
        networkDirty = false;
        this.capacity = maxStress;
        this.stress = currentStress;
        ((KineticBlockEntityAccessor) this).setNetworkSize(networkSize);
        boolean overStressed = maxStress < currentStress && IRotate.StressImpact.isEnabled();
        setChanged();

        if (overStressed != this.overStressed) {
            float prevSpeed = super.getSpeed();
            this.overStressed = overStressed;
            onSpeedChanged(prevSpeed);
            sendData();
        }
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        float kineticSpeed = super.getSpeed();
        boolean fromOrToZero = (previousSpeed == 0) != (kineticSpeed == 0);
        boolean directionSwap = !fromOrToZero && Math.signum(previousSpeed) != Math.signum(kineticSpeed);
        if (fromOrToZero || directionSwap)
            ((KineticBlockEntityAccessor) this).setFlickerTally(getFlickerScore() + 5);
        setChanged();
    }

    @Override
    public void removeSource() {
        caa$stopWhenChanged = 2;
        float prevSpeed = super.getSpeed();

        speed = 0;
        source = null;
        setNetwork(null);
        sequenceContext = null;

        onSpeedChanged(prevSpeed);
    }

    @Override
    public float caa$getKineticSpeed() {
        return super.getSpeed();
    }

    @Override
    public void caa$setTargetSpeed(int value) {
        if (caa$targetSpeed != null)
            caa$targetSpeed.value = value;
    }
}
