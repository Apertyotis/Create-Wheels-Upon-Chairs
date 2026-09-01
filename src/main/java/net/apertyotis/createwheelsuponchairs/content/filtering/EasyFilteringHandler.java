package net.apertyotis.createwheelsuponchairs.content.filtering;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;

@EventBusSubscriber
public class EasyFilteringHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onWrenchClickFilterSlot(PlayerInteractEvent.RightClickBlock event) {
        if (!AllConfig.easy_filtering)
            return;
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        if (player.isSpectator() || player.isShiftKeyDown() || player.isFakePlayer())
            return;
        if (!AllItems.WRENCH.isIn(player.getMainHandItem()))
            return;

        if (event.getSide().isClient()) {
            MutableBoolean result = new MutableBoolean(false);
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () ->
                EasyFilteringHandlerClient.EASY_FILTERING_HANDLER_CLIENT.isInteracting(pos, result));

            if (result.booleanValue()) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }
        }

        FilteringBehaviour behaviour = BlockEntityBehaviour.get(world, pos, FilteringBehaviour.TYPE);
        if (behaviour == null || !behaviour.isActive() || behaviour instanceof FactoryPanelBehaviour)
            return;

        BlockHitResult ray = event.getHitVec();
        if (behaviour instanceof SidedFilteringBehaviour sidedBehaviour) {
            behaviour = sidedBehaviour.get(ray.getDirection());
            if (behaviour == null)
                return;
        }
        if (behaviour.testHit(ray.getLocation())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            if (event.getSide().isClient()) {
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () ->
                    EasyFilteringHandlerClient.EASY_FILTERING_HANDLER_CLIENT.startInteraction(pos, ray.getDirection()));
            }
        }
    }
}
