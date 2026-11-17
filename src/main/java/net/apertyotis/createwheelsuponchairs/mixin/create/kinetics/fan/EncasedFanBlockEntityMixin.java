package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.fan;

import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.content.fan.FanGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EncasedFanBlockEntity.class)
public abstract class EncasedFanBlockEntityMixin extends KineticBlockEntity implements FanGenerator {
    @Unique
    private boolean cwuc$reActivateSource;

    @Unique
    private int cwuc$heatLevel;

    @Unique
    private boolean cwuc$clockwise;

    public EncasedFanBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void setLazyTick(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
        setLazyTickRate(30);
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;tick()V",
            shift = At.Shift.AFTER
        )
    )
    private void generatorTick(CallbackInfo ci) {
        if (cwuc$reActivateSource) {
            cwuc$updateGeneratedRotation();
            cwuc$reActivateSource = false;
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level == null)
            return;
        int oldHeat = cwuc$heatLevel;
        if (getBlockState().getValue(BlockStateProperties.FACING) != Direction.DOWN) {
            cwuc$heatLevel = 0;
        } else {
            BlockPos pos = getBlockPos().below();
            cwuc$heatLevel = (int) BoilerHeater.findHeat(level, pos, level.getBlockState(pos)) + 1;
        }

        if (oldHeat != cwuc$heatLevel) {
            cwuc$reActivateSource = true;
            cwuc$clockwise = speed < 0;
        }
    }

    @Unique
    @Override
    public int cwuc$getHeatLevel() {
        return cwuc$heatLevel;
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void writeHeatLevel(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        compound.putInt("HeatLevel", cwuc$heatLevel);
        compound.putBoolean("Clockwise", cwuc$clockwise);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void readHeatLevel(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        cwuc$heatLevel = compound.getInt("HeatLevel");
        cwuc$clockwise = compound.getBoolean("Clockwise");
    }

    @Unique
    public void cwuc$updateGeneratedRotation() {
        float speed = getGeneratedSpeed();
        float prevSpeed = this.speed;

        if (level == null || level.isClientSide)
            return;

        if (prevSpeed != speed) {
            if (!hasSource()) {
                IRotate.SpeedLevel levelBefore = IRotate.SpeedLevel.of(this.speed);
                IRotate.SpeedLevel levelAfter = IRotate.SpeedLevel.of(speed);
                if (levelBefore != levelAfter)
                    effects.queueRotationIndicators();
            }

            cwuc$applyNewSpeed(prevSpeed, speed);
        }

        if (hasNetwork() && speed != 0) {
            KineticNetwork network = getOrCreateNetwork();
            cwuc$notifyStressCapacityChange(calculateAddedStressCapacity());
            getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
            network.updateStress();
        }

        onSpeedChanged(prevSpeed);
        sendData();
    }

    @Unique
    public void cwuc$applyNewSpeed(float prevSpeed, float speed) {

        // Speed changed to 0
        if (speed == 0) {
            if (hasSource()) {
                cwuc$notifyStressCapacityChange(0);
                getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
                return;
            }
            detachKinetics();
            setSpeed(0);
            setNetwork(null);
            return;
        }

        // Now turning - create a new Network
        if (prevSpeed == 0) {
            setSpeed(speed);
            setNetwork(cwuc$createNetworkId());
            attachKinetics();
            return;
        }

        // Change speed when overpowered by other generator
        if (hasSource()) {

            // Staying below Overpowered speed
            if (Math.abs(prevSpeed) >= Math.abs(speed)) {
                if (Math.signum(prevSpeed) != Math.signum(speed)) {
                    // noinspection DataFlowIssue
                    level.destroyBlock(worldPosition, true);
                }
                return;
            }

            // Faster than attached network -> become the new source
            detachKinetics();
            setSpeed(speed);
            source = null;
            setNetwork(cwuc$createNetworkId());
            attachKinetics();
            return;
        }

        // Reapply source
        detachKinetics();
        setSpeed(speed);
        attachKinetics();
    }

    @Unique
    protected void cwuc$notifyStressCapacityChange(float capacity) {
        getOrCreateNetwork().updateCapacityFor(this, capacity);
    }

    @Unique
    public Long cwuc$createNetworkId() {
        return worldPosition.asLong();
    }

    @Override
    public float getGeneratedSpeed() {
        return cwuc$heatLevel > 0 ? (cwuc$clockwise ? -32 : 32) : 0;
    }

    @Override
    public float calculateStressApplied() {
        return cwuc$heatLevel > 0 ? 0 : super.calculateStressApplied();
    }

    @Override
    public float calculateAddedStressCapacity() {
        float generated = Math.abs(getGeneratedSpeed());
        if (generated == 0 || cwuc$heatLevel <= 0)
            return 0;
        else if (cwuc$heatLevel == 1)
            return AllConfig.fan_passive_generation / generated;
        else
            return AllConfig.fan_active_generation * (cwuc$heatLevel - 1) / generated;
    }

    @Override
    public void removeSource() {
        if (hasSource() && isSource())
            cwuc$reActivateSource = true;
        super.removeSource();
    }

    @Override
    public void setSource(BlockPos source) {
        super.setSource(source);
        if (level == null)
            return;
        BlockEntity blockEntity = level.getBlockEntity(source);
        if (!(blockEntity instanceof KineticBlockEntity sourceBE))
            return;
        if (cwuc$reActivateSource && Math.abs(sourceBE.getSpeed()) >= Math.abs(getGeneratedSpeed()))
            cwuc$reActivateSource = false;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (!IRotate.StressImpact.isEnabled() || cwuc$heatLevel <= 0)
            return added;

        float stressBase = calculateAddedStressCapacity();
        if (Mth.equal(stressBase, 0))
            return added;

        CreateLang.translate("gui.goggles.generator_stats")
            .forGoggles(tooltip);
        CreateLang.translate("tooltip.capacityProvided")
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip);

        float speed = getTheoreticalSpeed();
        if (speed != getGeneratedSpeed() && speed != 0)
            stressBase *= getGeneratedSpeed() / speed;

        float stressTotal = Math.abs(stressBase * speed);

        CreateLang.number(stressTotal)
            .translate("generic.unit.stress")
            .style(ChatFormatting.AQUA)
            .space()
            .add(CreateLang.translate("gui.goggles.at_current_speed")
                .style(ChatFormatting.DARK_GRAY))
            .forGoggles(tooltip, 1);

        return true;
    }
}

