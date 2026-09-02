package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.press;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.BeltPressingCallbacks;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.belt.BeltInventoryAccessor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BeltPressingCallbacks.class, remap = false)
public abstract class BeltPressingCallbacksMixin {
    @WrapOperation(
        method = "whenItemHeld",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour;handleProcessingOnItem(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$TransportedResult;)V"
        )
    )
    private static void fastOutput(
        TransportedItemStackHandlerBehaviour handler, TransportedItemStack transported,
        TransportedItemStackHandlerBehaviour.TransportedResult processOutput, Operation<Void> original,
        @Local(name = "bulk") boolean bulk,
        @Cancellable CallbackInfoReturnable<BeltProcessingBehaviour.ProcessingResult> cir
    ) {
        if (!AllConfig.belt_processing_tweak || !(handler.blockEntity instanceof BeltBlockEntity belt)) {
            original.call(handler, transported, processOutput);
            return;
        }

        BeltBlockEntity beltBE = belt.getControllerBE();
        if (beltBE == null)
            return;
        BeltInventoryAccessor inv = (BeltInventoryAccessor) beltBE.getInventory();
        if (inv == null)
            return;

        if (bulk) {
            if (processOutput == TransportedItemStackHandlerBehaviour.TransportedResult.removeItem()) {
                inv.getToRemove().add(transported);
            } else {
                List<TransportedItemStack> output = processOutput.getOutputs();
                transported.stack = output.get(0).stack;
                transported.angle = output.get(0).angle;
                // 使冲压机提前放行产物
                transported.locked = false;
                for (int i = 1; i < output.size(); i++) {
                    inv.getToInsert().add(output.get(i));
                }
            }
        } else {
            if (processOutput.hasHeldOutput()) {
                TransportedItemStack left = processOutput.getHeldOutput();
                // noinspection DataFlowIssue
                if (!left.stack.isEmpty()) {
                    left.beltPosition = belt.index + .5f - (inv.isPositive() ? 1 / 512f : -1 / 512f);
                    inv.getToInsert().add(left);
                }

                List<TransportedItemStack> output = processOutput.getOutputs();
                transported.stack = output.get(0).stack;
                transported.angle = output.get(0).angle;
                // 使冲压机提前放行产物
                transported.locked = false;
                for (int i = 1; i < output.size(); i++) {
                    inv.getToInsert().add(output.get(i));
                }
            } else {
                transported.stack.shrink(1);
            }
        }
        beltBE.notifyUpdate();
        if (transported.stack.isEmpty())
            cir.setReturnValue(BeltProcessingBehaviour.ProcessingResult.REMOVE);
        else
            cir.setReturnValue(BeltProcessingBehaviour.ProcessingResult.HOLD);
    }

    // 原先的冲压机无法处理提前输入的物品
    @WrapOperation(
        method = "whenItemHeld",
        at = @At(
            value = "FIELD",
            target = "Lcom/simibubi/create/content/kinetics/press/PressingBehaviour;running:Z",
            opcode = Opcodes.GETFIELD
        )
    )
    private static boolean startProcessing(
        PressingBehaviour behaviour, Operation<Boolean> original,
        @Local(argsOnly = true) TransportedItemStack transported
    ) {
        boolean running = original.call(behaviour);
        if (AllConfig.belt_processing_tweak && !running &&
            behaviour.specifics.tryProcessOnBelt(transported, null, true)
        ) {
            behaviour.start(PressingBehaviour.Mode.BELT);
            running = true;
        }
        return running;
    }
}
