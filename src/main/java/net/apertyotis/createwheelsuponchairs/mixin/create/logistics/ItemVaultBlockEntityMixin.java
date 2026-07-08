package net.apertyotis.createwheelsuponchairs.mixin.create.logistics;

import com.simibubi.create.api.schematic.nbt.PartialSafeNBT;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ItemVaultBlockEntity.class, remap = false)
public abstract class ItemVaultBlockEntityMixin implements PartialSafeNBT {
    /**
     * 修复蓝图打印单格保险库容量为 0 的问题<br>
     * 详见 Create PR <a href="https://github.com/Creators-of-Create/Create/pull/10084">#10084</a><br>
     * 另见 Create PR <a href="https://github.com/Creators-of-Create/Create/pull/10525">#10525</a>
     */
    @Override
    public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
        if (!AllConfig.vault_and_tank_schematic_fix)
            return;
        ItemVaultBlockEntity vault = (ItemVaultBlockEntity)(Object) this;
        if (vault.isController()) {
            compound.putInt("Size", 1);
            compound.putInt("Length", 1);
        }
    }
}
