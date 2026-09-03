package net.apertyotis.createwheelsuponchairs.mixin.create.fluids.hosePulley;

import com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(HosePulleyFluidHandler.class)
public interface HosePulleyFluidHandlerAccessor {
    @Accessor("rootPosGetter")
    Supplier<BlockPos> getRootPosGetter();
}
