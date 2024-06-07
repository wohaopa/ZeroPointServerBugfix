package com.github.zeropoint.bugfix;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

public enum FixEnum {

    // spotless off
    ae2(Phase.Late, "appliedenergistics2", "rv3-beta-357-GTNH", Side.Both, tL("GTNH261.CraftingCPUClusterMixin")),
    TwilightForest(Phase.Late, "TwilightForest", "2.5.25", Side.Both,
        tL("GTNH261.ComponentTFNagaCourtyardRotatedAbstractMixin")),
    bartworks(Phase.Late, "bartworks", "0.9.26", Side.ClientOnly, tL("GTNH261.RendererGlasBlockMixin")),
    inputfix(Phase.Normal, "minecraft", modContainer -> {
        try {
            Class.forName("lain.mods.inputfix.InputFix");
            return false;
        } catch (ClassNotFoundException ignored) {}
        try {
            Class.forName("me.eigenraven.lwjgl3ify.core.Lwjgl3ifyCoremod");
            return false;
        } catch (ClassNotFoundException ignored) {}
        return true;
    }, tL("GuiScreenMixin")),;

    // spotless on
    public enum Phase {
        Late,
        Normal,
        Early
    }

    private enum Side {

        ClientOnly,
        ServerOnly,
        Both;

        private static boolean canLoad(Side side) {
            return side == Both || side == ClientOnly && FMLLaunchHandler.side()
                .isClient()
                || side == ServerOnly && FMLLaunchHandler.side()
                    .isServer();
        }
    }

    private static List<String> tL(String... string) {
        if (string.length == 0) return Collections.emptyList();
        if (string.length == 1) return Collections.singletonList(string[0]);
        return Arrays.asList(string);
    };

    FixEnum(Phase phase, String target, Function<ModContainer, Boolean> apply, List<String> mixins) {
        this.target = target;
        this.apply = apply;
        this.mixins = mixins;
        this.phase = phase;
    }

    FixEnum(Phase phase, String target, String version, Side side, List<String> mixins) {
        this.target = target;
        this.apply = modContainer -> Side.canLoad(side) && modContainer != null
            && modContainer.getVersion()
                .equals(version);
        this.mixins = mixins;
        this.phase = phase;
    }

    final Phase phase;
    final String target;
    final Function<ModContainer, Boolean> apply;
    final List<String> mixins;

}
