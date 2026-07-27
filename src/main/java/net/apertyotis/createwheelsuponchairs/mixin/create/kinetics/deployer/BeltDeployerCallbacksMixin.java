package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.deployer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.deployer.BeltDeployerCallbacks;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.belt.BeltInventoryAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BeltDeployerCallbacks.class)
public abstract class BeltDeployerCallbacksMixin {
    @Inject(
        method = "whenItemHeld",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/deployer/BeltDeployerCallbacks;activate(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour;Lcom/simibubi/create/content/kinetics/deployer/DeployerBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private static void removeEmpty(
        TransportedItemStack s, TransportedItemStackHandlerBehaviour i, DeployerBlockEntity blockEntity,
        CallbackInfoReturnable<BeltProcessingBehaviour.ProcessingResult> cir
    ) {
        if (s.stack.isEmpty() && AllConfig.belt_processing_tweak)
            cir.setReturnValue(BeltProcessingBehaviour.ProcessingResult.REMOVE);
    }

    @WrapOperation(
        method = "activate",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour;handleProcessingOnItem(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$TransportedResult;)V"
        )
    )
    private static void fastOutput(
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

        if (processOutput.hasHeldOutput()) {
            TransportedItemStack left = processOutput.getHeldOutput();
            // noinspection DataFlowIssue
            if (!left.stack.isEmpty()) {
                left.beltPosition = belt.index + .5f - (inv.isPositive() ? 1 / 512f : -1 / 512f);
                inv.getToInsert().add(left);
            }

            // processOutput.hasHeldOutput() 为真足以保证 output 非空
            // 将第一份产出直接替换已有的物品堆，使得产出能立即输出到当前格漏斗，并节约性能
            List<TransportedItemStack> output = processOutput.getOutputs();
            transported.stack = output.getFirst().stack;
            transported.angle = output.getFirst().angle;
            transported.locked = false;
            for (int i = 1; i < output.size(); i++) {
                inv.getToInsert().add(output.get(i));
            }
        } else {
            transported.stack.shrink(1);
        }
        beltBE.notifyUpdate();
    }
}
