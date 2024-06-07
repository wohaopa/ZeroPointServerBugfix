package com.github.zeropoint.bugfix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class FMLLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.ZeroPointBugfix.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> mixins = new ArrayList<>();

        for (FixEnum fixEnum : FixEnum.values())
            if (fixEnum.phase == FixEnum.Phase.Early && fixEnum.apply.apply(null)) {
                Bugfix.LOG.info("Load Early Mixins: {} from {}", fixEnum.mixins, fixEnum.name());
                mixins.addAll(fixEnum.mixins);
            }

        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
