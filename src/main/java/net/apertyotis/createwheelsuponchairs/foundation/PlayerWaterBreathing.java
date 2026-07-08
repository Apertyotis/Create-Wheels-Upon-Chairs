package net.apertyotis.createwheelsuponchairs.foundation;

import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.CreateWheelsUponChairs;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

@EventBusSubscriber(modid = CreateWheelsUponChairs.MOD_ID)
public class PlayerWaterBreathing {
    // 玩家不会在水下窒息
    @SubscribeEvent
    public static void onLivingBreath(LivingBreatheEvent event) {
        if (!AllConfig.player_can_breath_underwater)
            return;

        if (event.getEntity() instanceof Player)
            event.setCanBreathe(true);
    }
}