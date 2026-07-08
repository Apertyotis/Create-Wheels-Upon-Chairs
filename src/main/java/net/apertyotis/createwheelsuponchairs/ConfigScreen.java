package net.apertyotis.createwheelsuponchairs;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Optional;
import java.util.function.Supplier;

import static net.apertyotis.createwheelsuponchairs.AllConfig.COMMON;


public class ConfigScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory qol = builder.getOrCreateCategory(Component.translatable("cwuc.config.common.qol.title"));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.DEPLOYER_INSTANT_OUTPUT));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.BELT_FUNNEL_DETECTION_TWEAK));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.NO_CHUTE_LEAKING));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.NO_DEPOT_OVERFLOW_DROP));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.REPLACE_ANY_FLOWING_FLUID));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.BETTER_PSI_ON_CARRIAGE));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.HACHIMI_GLUE));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.SMART_FLUID_PIPE));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.EASY_BELT));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.FAST_CONTRAPTION_STORAGE));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.FAST_LOGISTICS));
        qol.addEntry(booleanEntry(entryBuilder, COMMON.BASIN_FAUCET_VIEW));

        ConfigCategory bugfix = builder.getOrCreateCategory(Component.translatable("cwuc.config.common.bugfix.title"));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.PSI_FIX));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.HOSE_PULLEY_FIX));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.VAULT_AND_TANK_SCHEMATIC_FIX));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.FLUID_NETWORK_FIX));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.BELT_FIX));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.PROCESSING_FIX));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.TRAIN_FIX));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.LOGISTICS_FIX));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.FIX_9729));
        bugfix.addEntry(booleanEntry(entryBuilder, COMMON.FIX_9803));

        ConfigCategory misc = builder.getOrCreateCategory(Component.translatable("cwuc.config.common.misc.title"));
        misc.addEntry(booleanEntry(entryBuilder, COMMON.PLAYER_CAN_BREATH_UNDERWATER));
        misc.addEntry(booleanEntry(entryBuilder, COMMON.DISABLE_DIG_SPEED_PENALTY));
        misc.addEntry(booleanEntry(entryBuilder, COMMON.ALWAYS_ALLOW_FLYING));
        misc.addEntry(booleanEntry(entryBuilder, COMMON.KEEP_FLYING_ON_GROUND));
        misc.addEntry(booleanEntry(entryBuilder, COMMON.HEURISTIC_ROTATION));

        builder.setSavingRunnable(() -> {
            AllConfig.modConfig.save();
            AllConfig.onLoading();
        });
        return builder.build();
    }

    private static BooleanListEntry booleanEntry(ConfigEntryBuilder entryBuilder, ForgeConfigSpec.BooleanValue entry) {
        StringBuilder sb = new StringBuilder("cwuc.config");
        for (String path: entry.getPath()) {
            sb.append('.');
            sb.append(path);
        }
        String key = sb.toString();
        return entryBuilder.startBooleanToggle(Component.translatable(key), entry.get())
            .setTooltipSupplier(tooltipFactory(key))
            .setDefaultValue(entry.getDefault())
            .setSaveConsumer(entry::set)
            .build();
    }

    private static Supplier<Optional<Component[]>> tooltipFactory(String key) {
        return () -> {
            int num = 0;
            while (num < 10) {
                if (!I18n.exists("%s.%d".formatted(key, num + 1)))
                    break;
                num++;
            }
            if (num == 0)
                return Optional.empty();
            Component[] tooltips = new Component[num];
            for (int i = 0; i < num; i++)
                tooltips[i] = Component.translatable("%s.%d".formatted(key, i + 1));
            return Optional.of(tooltips);
        };
    }
}
