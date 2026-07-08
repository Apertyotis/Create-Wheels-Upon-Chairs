package net.apertyotis.createwheelsuponchairs.foundation;

import net.apertyotis.createwheelsuponchairs.content.hachimiGlue.HachimiGlueHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        Level world = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (world == null || player == null)
            return;

        HachimiGlueHandler.HACHIMI_GLUE_HANDLER.tick();
    }

    @SubscribeEvent
    public static void onMouseScrolled(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().screen != null)
            return;

        double delta = event.getScrollDelta();

        if (HachimiGlueHandler.HACHIMI_GLUE_HANDLER.mouseScrolled(delta)) {
            event.setCanceled(true);
        }
    }
}
