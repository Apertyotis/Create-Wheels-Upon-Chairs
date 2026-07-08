package net.apertyotis.createwheelsuponchairs.mixin;

import com.electronwill.nightconfig.core.file.FileConfig;
import net.apertyotis.createwheelsuponchairs.compat.Mods;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CWUCMixinPlugin implements IMixinConfigPlugin {
    private static final String PATH = "net.apertyotis.createwhaleuponclouds.mixin";
    private static Set<String> blacklist;

    private void init() {
        if (blacklist == null) {
            blacklist = new HashSet<>();
            for (Mods mod: Mods.values()) {
                if (!mod.isLoaded() && mod.getMixins() != null) {
                    for (String mixin: mod.getMixins()) {
                        blacklist.add(String.format("%s.%s.%s", PATH, mod.getPath(), mixin));
                    }
                }
            }
            Path path = Paths.get("config/createwhaleuponclouds-common.toml");
            if (!Files.exists(path))
                return;
            try (FileConfig config = FileConfig.of(path)) {
                config.load();
                if (!config.getOrElse("common.fast_contraption_storage", true))
                    blacklist.add("%s.%s.%s".formatted(PATH, "create", "api.MountedItemStorageWrapperMixin"));
            }
        }
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        init();
        return !blacklist.contains(mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
