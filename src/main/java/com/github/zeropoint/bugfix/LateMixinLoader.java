package com.github.zeropoint.bugfix;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

@LateMixin
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.ZeroPointBugfix.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {

        List<String> mixins = new ArrayList<>();
        List<ModContainer> mods = Loader.instance()
            .getModList();

        ModsUtil.dumpMods(mods);
        ModsUtil.registry();
        ModsUtil.dumpDiff();

        for (ModContainer mod : mods) {
            String modid = mod.getModId(), version = mod.getVersion();
            switch (modid) {
                // 261
                case "appliedenergistics2":
                    if (version.equals("rv3-beta-357-GTNH")) {
                        mixins.add("GTNH261.CraftingCPUClusterMixin");
                    }
                    break;
                case "TwilightForest":
                    if (version.equals("2.5.25")) {
                        mixins.add("GTNH261.ComponentTFNagaCourtyardRotatedAbstractMixin");
                    }
                    break;
                case "bartworks":
                    if (version.equals("0.9.26")) {
                        if (FMLLaunchHandler.side()
                            .isClient()) {
                            mixins.add("GTNH261.RendererGlasBlockMixin");
                        }
                    }
                    break;
                default:
            }
        }

        return mixins;
    }

}
