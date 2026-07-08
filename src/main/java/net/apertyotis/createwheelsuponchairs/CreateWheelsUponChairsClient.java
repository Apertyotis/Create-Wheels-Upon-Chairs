package net.apertyotis.createwheelsuponchairs;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CreateWheelsUponChairs.MOD_ID, dist = Dist.CLIENT)
public class CreateWheelsUponChairsClient {

    public CreateWheelsUponChairsClient(ModContainer container) {
        if (ModList.get().isLoaded("cloth_config")) {
            container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (minecraft, parent) -> ConfigScreen.create(parent)
            );
        }
    }
}