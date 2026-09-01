package net.apertyotis.createwheelsuponchairs.mixin.create.fluids;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.content.fluids.*;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import net.apertyotis.createwheelsuponchairs.compat.Mods;
import net.apertyotis.createwheelsuponchairs.compat.lazytick.LazyTickConfig;
import net.apertyotis.createwheelsuponchairs.foundation.PipelineHelper;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mixin(FluidNetwork.class)
public abstract class FluidNetworkMixin {
    @Shadow
    FluidStack fluid;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow
    protected abstract boolean isPresent(BlockFace location);

    @Shadow
    BlockFace start;

    @Shadow
    @Nullable
    protected abstract PipeConnection get(BlockFace location);

    @Shadow
    Level world;

    @Shadow
    public abstract void reset();

    @Shadow
    List<BlockFace> queued;

    @Shadow
    int transferSpeed;

    @Shadow
    Set<Pair<BlockFace, PipeConnection>> frontier;

    @Shadow
    @Nullable
    protected abstract FluidTransportBehaviour getFluidTransfer(BlockPos pos);

    @Shadow
    List<Pair<BlockFace, FlowSource>> targets;

    @Shadow
    Set<BlockPos> visited;

    /**
     * 阻止管道抽取 0mB 液体<br>
     * 详见 Create PR <a href="https://github.com/Creators-of-Create/Create/pull/10055">#10055</a>
     */
    @Definition(id = "flowSpeed", local = @Local(type = int.class, name = "flowSpeed"))
    @Expression("flowSpeed - ?")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int preventDrainZero(int original, @Cancellable CallbackInfo ci) {
        if (AllConfig.hose_pulley_fix && original <= 0)
            ci.cancel();
        return original;
    }

    @Definition(id = "source", field = "Lcom/simibubi/create/content/fluids/FluidNetwork;source:Lcom/simibubi/create/foundation/ICapabilityProvider;")
    @Expression("?.source")
    @ModifyExpressionValue(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private @Nullable ICapabilityProvider<IFluidHandler> validateSource(@Nullable ICapabilityProvider<IFluidHandler> original) {
        if (!AllConfig.fluid_network_fix)
            return original;
        if (original != null && original.getCapability() != null)
            return original;
        return null;
    }

    // 简化抽取逻辑
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/neoforged/neoforge/fluids/FluidStack;EMPTY:Lnet/neoforged/neoforge/fluids/FluidStack;",
            opcode = Opcodes.GETSTATIC,
            ordinal = 0
        )
    )
    private FluidStack directlyDrain(
        Operation<FluidStack> original,
        @Local(name = "flowSpeed") int flowSpeed,
        @Local(name = "action") IFluidHandler.FluidAction action,
        @Local(name = "sourceCap") IFluidHandler handler,
        @Cancellable CallbackInfo ci
    ) {
        if (!AllConfig.smart_fluid_pipe)
            return original.call();
        FluidStack toExtract = fluid.copy();
        if (toExtract.isEmpty()) {
            ci.cancel();
            return FluidStack.EMPTY;
        }
        toExtract.setAmount(flowSpeed);
        FluidStack extracted = handler.drain(toExtract, action);
        if (extracted.isEmpty())
            ci.cancel();
        return extracted;
    }

    // 取消原抽取逻辑
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/fluids/capability/IFluidHandler;getTanks()I"
        )
    )
    private int breakFindFluidLoop(IFluidHandler instance, Operation<Integer> original) {
        if (AllConfig.smart_fluid_pipe)
            return 0;
        else
            return original.call(instance);
    }

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lcom/simibubi/create/content/fluids/FluidNetwork;transferSpeed:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void multiplyPumpSpeed(FluidNetwork instance, int value, Operation<Void> original) {
        original.call(instance, value * AllConfig.pump_speed_multiplier);
    }

    // 重写网络液流动画维护逻辑，不再是每 gt 每个管道各自遍历每个开口
    // 而是在网络建立、网络重置时完成
    @SuppressWarnings("SameReturnValue")
    @Definition(id = "cycle", local = @Local(type = int.class, name = "cycle"))
    @Expression("cycle < ?")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean betterFluidNetwork(boolean original, @Cancellable CallbackInfo ci) {
        if (!AllConfig.hyper_fluid_propagation)
            return original;
        if (!isPresent(start)) {
            ci.cancel();
            return false;
        }
        PipeConnection first = get(start);
        if (first == null) {
            ci.cancel();
            return false;
        }
        first.tickFlowProgress(world, start.getPos());
        FluidStack newFluid = first.getProvidedFluid();
        if (fluid.isEmpty()) {
            fluid = newFluid;
        } else if (!FluidStack.isSameFluidSameComponents(fluid, newFluid)) {
            reset();
            fluid = newFluid;
        }
        if (fluid.isEmpty()) {
            ci.cancel();
            return false;
        }

        // 遍历网络结构
        for (int cycle = 0; cycle < 16; cycle++) {
            boolean shouldContinue = false;
            for (var iterator = queued.iterator(); iterator.hasNext();) {
                BlockFace blockFace = iterator.next();
                if (!isPresent(blockFace))
                    continue;
                FluidTransportBehaviour behaviour = getFluidTransfer(blockFace.getPos());
                PipeConnection connection = behaviour == null ? null : behaviour.getConnection(blockFace.getFace());
                if (connection != null) {
                    if (blockFace.equals(start)) {
                        int multi = Mods.CreateLazyTick.runIfInstalled(() ->
                            LazyTickConfig::getFluidDelayMax).orElse(1);
                        transferSpeed = (int) Math.max(1, connection.getPressure().get(true) / 2f) *
                            AllConfig.pump_speed_multiplier * multi;
                    } else if (!connection.hasFlow()) {
                        if (!behaviour.canPullFluidFrom(fluid,
                            behaviour.blockEntity.getBlockState(),
                            blockFace.getFace())
                        ) {
                            iterator.remove();
                            continue;
                        }
                        ((PipeConnectionAccessor) connection).invokeTryStartingNewFlow(true, fluid);
                        connection.tickFlowProgress(world, blockFace.getPos());
                    }
                    frontier.add(Pair.of(blockFace, connection));
                }
                iterator.remove();
            }

            for (var iterator = frontier.iterator(); iterator.hasNext();) {
                Pair<BlockFace, PipeConnection> pair = iterator.next();
                BlockFace blockFace = pair.getFirst();
                PipeConnection connection = pair.getSecond();

                FluidTransportBehaviour behaviour = getFluidTransfer(blockFace.getPos());
                if (behaviour == null) {
                    iterator.remove();
                    continue;
                }
                boolean collision = false;
                List<PipeConnection> next = new ArrayList<>(5);
                for (PipeConnection adjacent: behaviour.interfaces.values()) {
                    if (adjacent == connection || adjacent.comparePressure() <= 0)
                        continue;

                    Optional<PipeConnection.Flow> flow = ((PipeConnectionAccessor) adjacent).getFlow();
                    if (flow.isPresent() && !FluidStack.isSameFluidSameComponents(fluid, flow.get().fluid)) {
                        if (!AllConfig.fluid_not_collide) {
                            FluidReactions.handlePipeFlowCollision(world, blockFace.getPos(), flow.get().fluid, fluid);
                            ci.cancel();
                            return false;
                        } else {
                            collision = true;
                            break;
                        }
                    }
                    next.add(adjacent);
                }
                if (collision)
                    continue;

                boolean isLoaded = true;
                boolean changed = false;
                for (PipeConnection adjacent: next) {
                    if (!adjacent.hasFlow()) {
                        ((PipeConnectionAccessor) adjacent).invokeTryStartingNewFlow(false, fluid);
                        adjacent.tickFlowProgress(world, blockFace.getPos());
                        changed = true;
                    }

                    // Give pipe end a chance to init connections
                    Optional<FlowSource> adjacentSource = ((PipeConnectionAccessor) adjacent).getSource();
                    if (adjacentSource.isEmpty()) {
                        if (adjacent.determineSource(world, blockFace.getPos())) {
                            adjacentSource = ((PipeConnectionAccessor) adjacent).getSource();
                        } else {
                            isLoaded = false;
                            continue;
                        }
                    }

                    BlockFace adjacentLocation = new BlockFace(blockFace.getPos(), adjacent.side);
                    // noinspection OptionalGetWithoutIsPresent
                    if (adjacentSource.get().isEndpoint()) {
                        targets.add(Pair.of(adjacentLocation, adjacentSource.get()));
                    } else if (visited.add(adjacentLocation.getConnectedPos())) {
                        queued.add(adjacentLocation.getOpposite());
                        shouldContinue = true;
                    }
                }
                if (changed)
                    behaviour.blockEntity.notifyUpdate();
                if (isLoaded)
                    iterator.remove();
            }
            if (!shouldContinue)
                break;
        }
        return false;
    }

    // 重建网络时清除前方液流
    @Inject(method = "reset", at = @At("HEAD"))
    private void beforeReset(CallbackInfo ci) {
        if (!AllConfig.hyper_fluid_propagation || targets.isEmpty())
            return;
        PipelineHelper.resetFrontPipeline(world, start, fluid);
    }
}
