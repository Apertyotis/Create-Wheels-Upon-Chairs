package net.apertyotis.createwheelsuponchairs.mixin.create.api;

import com.google.common.collect.ImmutableMap;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import net.minecraft.core.BlockPos;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MountedItemStorageWrapper.class, remap = false)
public abstract class MountedItemStorageWrapperMixin extends CombinedInvWrapper {
    // Lookup arrays
    @Unique
    private int[] caa$slotToStorage;   // Maps each slot to its storage index
    @Unique
    private int[] caa$slotOffsets;     // Starting slot for each storage

    @Inject(method = "<init>", at = @At("RETURN"))
    private void buildLookupArrays(ImmutableMap<BlockPos, MountedItemStorage> storages, CallbackInfo ci) {
        // Build lookup arrays
        int totalSlots = getSlots();
        caa$slotToStorage = new int[totalSlots];
        caa$slotOffsets = new int[itemHandler.length];

        int currentSlot = 0;
        for (int storageIdx = 0; storageIdx < itemHandler.length; storageIdx++) {
            caa$slotOffsets[storageIdx] = currentSlot;
            int slotsInStorage = itemHandler[storageIdx].getSlots();

            for (int i = 0; i < slotsInStorage; i++) {
                caa$slotToStorage[currentSlot + i] = storageIdx;
            }

            currentSlot += slotsInStorage;
        }
    }

    @Override
    protected int getIndexForSlot(int slot) {
        if (slot < 0 || slot >= caa$slotToStorage.length) {
            return -1;
        }
        return caa$slotToStorage[slot];
    }

    @Override
    protected int getSlotFromIndex(int slot, int index) {
        return slot - caa$slotOffsets[index];
    }
}
