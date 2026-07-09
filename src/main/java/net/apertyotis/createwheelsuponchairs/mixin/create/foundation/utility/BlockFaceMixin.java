package net.apertyotis.createwheelsuponchairs.mixin.create.foundation.utility;

import net.createmod.catnip.math.BlockFace;
import net.apertyotis.createwheelsuponchairs.foundation.BlockFaceEx;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockFace.class)
public abstract class BlockFaceMixin implements BlockFaceEx {
    @Unique
    private BlockPos caa$filterPos = null;

    @Unique
    @Override
    public BlockPos caa$getFilterPos() {
        return caa$filterPos;
    }

    @Unique
    @Override
    public void caa$withFilterPos(BlockPos pos) {
        caa$filterPos = pos;
    }
}
