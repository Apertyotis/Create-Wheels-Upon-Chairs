package net.apertyotis.createwheelsuponchairs.mixin.create.fluids;

import com.simibubi.create.content.fluids.FlowSource;
import net.createmod.catnip.math.BlockFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FlowSource.class, remap = false)
public interface FlowSourceAccessor {
    @Accessor("location")
    BlockFace getLocation();
}
