package net.apertyotis.createwheelsuponchairs.mixin.create.fluids.spout;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.belt.BeltInventoryAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SpoutBlockEntity.class)
public abstract class SpoutBlockEntityMixin {
    @WrapOperation(
        method = "whenItemHeld",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour;handleProcessingOnItem(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$TransportedResult;)V"
        )
    )
    private void fastOutput(
        TransportedItemStackHandlerBehaviour handler, TransportedItemStack transported,
        TransportedItemStackHandlerBehaviour.TransportedResult processOutput, Operation<Void> original
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

        List<TransportedItemStack> output = processOutput.getOutputs();
        transported.stack = output.get(0).stack;
        transported.locked = false;
        if (processOutput.hasHeldOutput()) {
            TransportedItemStack held = processOutput.getHeldOutput();
            // noinspection DataFlowIssue
            if (!held.stack.isEmpty()) {
                held.beltPosition = belt.index + .5f - (inv.isPositive() ? 1 / 512f : -1 / 512f);
                inv.getToInsert().add(held);
            }
        }
        beltBE.notifyUpdate();
    }

    @Inject(method = "whenItemHeld", at = @At("TAIL"), cancellable = true)
    private void removeEmpty(
        TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler,
        CallbackInfoReturnable<BeltProcessingBehaviour.ProcessingResult> cir
    ) {
        if (AllConfig.belt_processing_tweak && transported.stack.isEmpty())
            cir.setReturnValue(BeltProcessingBehaviour.ProcessingResult.REMOVE);
    }
}
