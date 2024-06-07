package com.github.zeropoint.bugfix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

@LateMixin
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.ZeroPointBugfix.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {

        List<String> mixins = new ArrayList<>();

        Map<String, ModContainer> map = Loader.instance()
            .getIndexedModList();

        ModsUtil.dumpMods(map.values());
        ModsUtil.registry();
        ModsUtil.dumpDiff();

        for (FixEnum fixEnum : FixEnum.values())
            if (fixEnum.phase == FixEnum.Phase.Late && fixEnum.apply.apply(map.get(fixEnum.target))) {
                Bugfix.LOG.info("Load Late Mixins: {} from {}", fixEnum.mixins, fixEnum.name());
                mixins.addAll(fixEnum.mixins);
            }

        return mixins;
    }

}
