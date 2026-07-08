package net.apertyotis.createwheelsuponchairs;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreateWheelsUponChairs.MOD_ID)
public class CreateWheelsUponChairs {
    public static final String MOD_ID = "createwheelsuponchairs";

    public static final Logger LOGGER = LogUtils.getLogger();

    static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    @SuppressWarnings("removal")
    public CreateWheelsUponChairs() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRATE.registerEventListeners(modEventBus);

        AllPackets.registerPackets();

        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, AllConfig.COMMON_SPEC);

        if (ModList.get().isLoaded("cloth_config")) {
            context.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                    (mc, parent) -> ConfigScreen.create(parent)
                )
            );
        }
    }
}