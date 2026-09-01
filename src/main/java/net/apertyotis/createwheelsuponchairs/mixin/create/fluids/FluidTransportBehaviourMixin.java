package net.apertyotis.createwheelsuponchairs.mixin.create.fluids;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.foundation.FluidTransportBehaviourEx;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Optional;
@Mixin(FluidTransportBehaviour.class)
public abstract class FluidTransportBehaviourMixin implements FluidTransportBehaviourEx {
    @Unique
    private BlockPos caa$filterPos;

    @Unique
    private boolean caa$attached = false;

    @Unique
    private boolean caa$needUpdate = false;

    @Unique
    private boolean caa$noSource = false;

    @Unique
    @Override
    public void caa$attachFilterPos(BlockPos pos) {
        BlockEntity be = ((FluidTransportBehaviour)(Object) this).blockEntity;
        if (be instanceof FluidPipeBlockEntity || be instanceof StraightPipeBlockEntity) {
            if (caa$attached) {
                if (caa$filterPos != null && !caa$filterPos.equals(pos))
                    caa$filterPos = null;
            } else {
                caa$filterPos = pos;
                caa$attached = true;
            }
        }
    }

    @Unique
    @Override
    public BlockPos caa$getFilterPos() {
        return caa$filterPos;
    }

    @Unique
    @Override
    public void caa$pressureChanged() {
        caa$filterPos = null;
        caa$attached = false;
        caa$noSource = false;
    }

    @Unique
    @Override
    public void caa$scheduleUpdate() {
        caa$needUpdate = true;
    }

    @Inject(method = "canPullFluidFrom", at = @At("RETURN"), cancellable = true)
    private void canPullFluidFromWithFilter(
        FluidStack fluid, BlockState state, Direction direction, CallbackInfoReturnable<Boolean> cir
    ) {
        if (!AllConfig.smart_fluid_pipe)
            return;
        BlockEntity be = ((FluidTransportBehaviour)(Object) this).blockEntity;
        Level level = be.getLevel();
        if (cir.getReturnValue() && caa$filterPos != null && level != null) {
            FluidTransportBehaviour pipeBehaviour = FluidPropagator.getPipe(level, caa$filterPos);
            if (pipeBehaviour != null)
                cir.setReturnValue(pipeBehaviour.canPullFluidFrom(fluid, state, direction));
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void readFilterPos(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (nbt.contains("FilterPos")) {
            caa$filterPos = NbtUtils.readBlockPos(nbt, "FilterPos").orElse(null);
        }
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void writeFilterPos(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (caa$filterPos != null) {
            nbt.put("FilterPos", NbtUtils.writeBlockPos(caa$filterPos));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void updateWhenBorderChunkReload(CallbackInfo ci) {
        if (caa$needUpdate) {
            BlockEntity be = ((FluidTransportBehaviour)(Object) this).blockEntity;
            if (be.getLevel() == null)
                return;
            FluidPropagator.propagateChangedPipe(be.getLevel(), be.getBlockPos(), be.getBlockState());
            caa$needUpdate = false;
            ci.cancel();
        }
    }

    @Definition(id = "onServer", local = @Local(type = boolean.class, name = "onServer"))
    @Expression("onServer")
    @Inject(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1), cancellable = true)
    private void tickConnections(CallbackInfo ci) {
        if (!AllConfig.hyper_fluid_propagation)
            return;
        ci.cancel();
        if (caa$noSource)
            return;
        // 只 tick 网络源，液流动画传播逻辑移动到流体网络遍历时完成
        FluidTransportBehaviour behaviour = (FluidTransportBehaviour)(Object) this;
        Level world = behaviour.getWorld();
        if (world == null)
            return;
        Collection<PipeConnection> connections = behaviour.interfaces.values();
        boolean changed = false;
        // 缓存管道是否位于源的信息
        boolean noSource = true;
        for (PipeConnection connection: connections) {
            if (connection.comparePressure() >= 0)
                continue;
            Optional<FlowSource> source = ((PipeConnectionAccessor) connection).getSource();
            if (source.isEmpty() && !connection.determineSource(world, behaviour.getPos())) {
                if (connection.determineSource(world, behaviour.getPos())) {
                    source = ((PipeConnectionAccessor) connection).getSource();
                } else {
                    // 让未加载的管道有机会初始化
                    noSource = false;
                    continue;
                }
            }
            if (source.isPresent() && source.get().isEndpoint()) {
                noSource = false;
                changed |= connection.manageFlows(world, behaviour.getPos(), FluidStack.EMPTY, fluid ->
                    behaviour.canPullFluidFrom(fluid, behaviour.blockEntity.getBlockState(), connection.side));
            }
        }
        caa$noSource = noSource;
        if (changed)
            behaviour.blockEntity.notifyUpdate();
    }

    // 网络变化同时清空液流
    @Inject(method = "wipePressure", at = @At("TAIL"))
    private void onNetworkChange(CallbackInfo ci) {
        if (!AllConfig.hyper_fluid_propagation)
            return;
        for (PipeConnection connection: ((FluidTransportBehaviour)(Object) this).interfaces.values()) {
            ((PipeConnectionAccessor) connection).setFlow(Optional.empty());
        }
        caa$noSource = false;
    }
}
