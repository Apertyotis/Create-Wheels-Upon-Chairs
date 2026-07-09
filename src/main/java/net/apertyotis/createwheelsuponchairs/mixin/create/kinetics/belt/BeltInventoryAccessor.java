package net.apertyotis.createwheelsuponchairs.mixin.create.kinetics.belt;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeltInventory.class)
public interface BeltInventoryAccessor {
    @Accessor("beltMovementPositive")
    boolean isPositive();

    @Accessor("belt")
    BeltBlockEntity getBelt();
}