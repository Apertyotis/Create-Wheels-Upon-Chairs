package net.apertyotis.createwheelsuponchairs;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(CreateWheelsUponChairs.MOD_ID)
public class CreateWheelsUponChairs {
    public static final String MOD_ID = "createwheelsuponchairs";

    public static final Logger LOGGER = LogUtils.getLogger();

    static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public CreateWheelsUponChairs(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);
        AllPackets.register();
        modContainer.registerConfig(ModConfig.Type.COMMON, AllConfig.COMMON_SPEC);
    }
}