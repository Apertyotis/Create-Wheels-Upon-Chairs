package net.apertyotis.createwheelsuponchairs.mixin.create.contraption.glue;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SuperGlueSelectionHandler.class)
public interface SuperGlueSelectionHandlerAccessor {
    @Accessor("firstPos")
    BlockPos getFirstPos();

    @Accessor("selected")
    SuperGlueEntity getSelected();
}
