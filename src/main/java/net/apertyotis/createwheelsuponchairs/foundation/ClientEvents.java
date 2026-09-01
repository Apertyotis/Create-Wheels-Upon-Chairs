package net.apertyotis.createwheelsuponchairs.foundation;

import net.apertyotis.createwheelsuponchairs.content.filtering.EasyFilteringHandlerClient;
import net.apertyotis.createwheelsuponchairs.content.hachimiGlue.HachimiGlueHandler;
import net.apertyotis.createwheelsuponchairs.content.toolbox.BetterToolboxHandlerClient;
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
        EasyFilteringHandlerClient.EASY_FILTERING_HANDLER_CLIENT.tick();
        BetterToolboxHandlerClient.BETTER_TOOLBOX_HANDLER_CLIENT.tick();
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Pre event) {
        if (Minecraft.getInstance().screen != null)
            return;

        int button = event.getButton();
        boolean pressed = event.getAction() != 0;

        BetterToolboxHandlerClient.BETTER_TOOLBOX_HANDLER_CLIENT.onMouseInput(button, pressed);
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
