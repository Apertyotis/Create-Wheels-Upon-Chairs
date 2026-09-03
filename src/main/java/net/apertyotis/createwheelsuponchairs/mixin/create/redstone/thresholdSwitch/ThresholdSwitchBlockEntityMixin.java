package net.apertyotis.createwheelsuponchairs.mixin.create.redstone.thresholdSwitch;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ThresholdSwitchBlockEntity.class)
public abstract class ThresholdSwitchBlockEntityMixin implements ClipboardCloneable {
    @Shadow
    private InvManipulationBehaviour observedInventory;
    @Shadow
    private VersionedInventoryTrackerBehaviour invVersionTracker;
    @Shadow
    private boolean inverted;

    @Override
    public String getClipboardKey() {
        return "ThresholdSwitch";
    }

    @Override
    public boolean writeToClipboard(
        @NotNull HolderLookup.Provider registries,
        CompoundTag tag, Direction side
    ) {
        ThresholdSwitchBlockEntity be = (ThresholdSwitchBlockEntity)(Object) this;
        tag.putInt("OnAbove", be.onWhenAbove);
        tag.putInt("OffBelow", be.offWhenBelow);
        tag.putBoolean("InStacks", be.inStacks);
        tag.putBoolean("Inverted", inverted);
        return true;
    }

    @Override
    public boolean readFromClipboard(
        @NotNull HolderLookup.Provider registries,
        CompoundTag tag, Player player, Direction side, boolean simulate
    ) {
        if (!tag.contains("OnAbove") || !tag.contains("OffBelow"))
            return false;
        if (simulate)
            return true;

        ThresholdSwitchBlockEntity be = ((ThresholdSwitchBlockEntity)(Object) this);
        be.onWhenAbove = tag.getInt("OnAbove");
        be.offWhenBelow = tag.getInt("OffBelow");
        be.inStacks = tag.getBoolean("InStacks");
        be.setInverted(tag.getBoolean("Inverted"));
        invVersionTracker.awaitNewVersion(observedInventory);
        be.notifyUpdate();
        return true;
    }
}
