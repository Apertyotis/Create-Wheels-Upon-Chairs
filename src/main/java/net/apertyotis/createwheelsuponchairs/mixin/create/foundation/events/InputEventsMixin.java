package net.apertyotis.createwheelsuponchairs.mixin.create.foundation.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxEquipPacket;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.foundation.events.InputEvents;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.content.toolbox.BetterToolboxHandlerClient;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;
import java.util.List;

@Mixin(value = InputEvents.class, remap = false)
public abstract class InputEventsMixin {
    @WrapOperation(
        method = "onKeyInput",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/equipment/toolbox/ToolboxHandlerClient;onKeyInput(IZ)V"
        )
    )
    private static void wrapToolbeltKeyInput(int key, boolean pressed, Operation<Void> original) {
        if (!AllKeys.TOOLBELT.doesModifierAndCodeMatch(key))
            return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null)
            return;

        if (AllKeys.TOOL_MENU.doesModifierAndCodeMatch(key)) {
            ItemStack item = player.getMainHandItem();
            if (AllItems.SCHEMATIC.isIn(item))
                return;
            if (BuiltInRegistries.ITEM.getKey(item.getItem()).getPath().contains("simple_schematic"))
                return;
        }

        BetterToolboxHandlerClient handler = BetterToolboxHandlerClient.BETTER_TOOLBOX_HANDLER_CLIENT;
        if (!pressed && AllConfig.quick_unequip_items && !handler.keyBlocked &&
            handler.keyPressed && handler.holdTicks <= AllConfig.toolbelt_hold_delay
        ) {
            List<ToolboxBlockEntity> toolboxes = ToolboxHandler.getNearest(player.level(), player, 8);
            toolboxes.sort(Comparator.comparing(ToolboxBlockEntity::getUniqueId));

            CompoundTag compound = player.getPersistentData().getCompound("CreateToolboxData");

            String slotKey = String.valueOf(player.getInventory().selected);
            boolean equipped = compound.contains(slotKey);

            if (equipped) {
                BlockPos pos = NbtUtils.readBlockPos(compound.getCompound(slotKey), "Pos").orElse(null);
                double max = ToolboxHandler.getMaxRange(player);
                boolean canReachToolbox = pos != null && ToolboxHandler.distance(player.position(), pos) < max * max;
                if (canReachToolbox) {
                    CatnipServices.NETWORK.sendToServer(new ToolboxEquipPacket(
                        pos, -5, player.getInventory().selected));
                }
            }
        }

        handler.keyPressed = pressed;
        if (!pressed) {
            handler.keyBlocked = false;
            handler.holdTicks = 0;
        }
    }
}
