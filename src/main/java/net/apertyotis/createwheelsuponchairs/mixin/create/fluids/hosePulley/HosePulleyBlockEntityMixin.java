package net.apertyotis.createwheelsuponchairs.mixin.create.fluids.hosePulley;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler;
import com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour;
import com.simibubi.create.content.fluids.transfer.FluidFillingBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.CreateWheelsUponChairs;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = HosePulleyBlockEntity.class, remap = false)
public abstract class HosePulleyBlockEntityMixin {

    @Shadow
    private FluidDrainingBehaviour drainer;
    @Shadow
    private FluidFillingBehaviour filler;
    @Shadow
    private HosePulleyFluidHandler handler;

    @Unique
    public boolean caa$fillerInfinite;
    @Unique
    public boolean caa$drainerInfinite;

    @Inject(method = "sendData", at = @At("TAIL"))
    private void sendInfinite(CallbackInfo ci) {
        caa$fillerInfinite = filler.isInfinite();
        caa$drainerInfinite = drainer.isInfinite();
    }

    // 向客户端发送更详细的 infinite 信息，以便于显示准确的护目镜提示
    @Inject(method = "write", at = @At("TAIL"))
    private void writeInfinite(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (clientPacket && AllConfig.hose_pulley_fix) {
            compound.putBoolean("FillerInfinite", caa$fillerInfinite);
            compound.putBoolean("DrainerInfinite", caa$drainerInfinite);
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void readInfinite(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (clientPacket && AllConfig.hose_pulley_fix) {
            caa$fillerInfinite = compound.getBoolean("FillerInfinite");
            caa$drainerInfinite = compound.getBoolean("DrainerInfinite");
        }
    }

    @WrapOperation(
        method = "addToGoggleTooltip",
        at = @At(
            value = "FIELD",
            target = "Lcom/simibubi/create/content/fluids/hosePulley/HosePulleyBlockEntity;infinite:Z",
            opcode = Opcodes.GETFIELD
        )
    )
    private boolean addDetailedTooltip(
        HosePulleyBlockEntity instance, Operation<Boolean> original,
        @Local(argsOnly = true) List<Component> tooltip
    ) {
        if (!AllConfig.hose_pulley_fix)
            return original.call(instance);
        FluidStack fluid = handler.getFluidInTank(0);
        if (fluid.isEmpty()) {
            return false;
        } else if (!((FluidManipulationBehaviourAccessor) drainer).invokeCanDrainInfinitely(fluid.getFluid())) {
            Component hint = Component.translatable("cwuc.goggle.hose_pulley.cant_infinite")
                .withStyle(ChatFormatting.RED);
            for (Component line: TooltipHelper.cutTextComponent(hint, FontHelper.Palette.RED)) {
                new LangBuilder(CreateWheelsUponChairs.MOD_ID).add(line).forGoggles(tooltip);
            }
            return false;
        } else if (caa$drainerInfinite) {
            return true;
        } else if (caa$fillerInfinite) {
            Component hint = Component.translatable("cwuc.goggle.hose_pulley.lower_hose")
                .withStyle(ChatFormatting.GOLD);
            for (Component line: TooltipHelper.cutTextComponent(hint, FontHelper.Palette.YELLOW)) {
                new LangBuilder(CreateWheelsUponChairs.MOD_ID).add(line).forGoggles(tooltip);
            }
            return false;
        }
        return original.call(instance);
    }
}
