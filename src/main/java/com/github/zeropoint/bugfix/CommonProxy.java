package com.github.zeropoint.bugfix;

import net.minecraft.launchwrapper.Launch;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        if (Updater.debug || !((boolean) Launch.blackboard.getOrDefault("fml.deobfuscatedEnvironment", false)))
            Updater.checkUpdate();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {}

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    // register server commands in this event handler (Remove if not needed)
    public void serverStarted(FMLServerStartedEvent event) {
        if (Updater.canUpdate) {
            Bugfix.LOG.info("==========================================");
            Bugfix.LOG.info("ZeroPoint Bugfix has a new version! ");
            Bugfix.LOG.info("Current: " + Tags.VERSION + " -> Latest: " + Updater.newVersion);
            if (Updater.downloaded) {
                Bugfix.LOG.info("It has been automatically downloaded to the mods directory");
                Bugfix.LOG.info("and will be replaced automatically next time you start.");
                Bugfix.LOG.info("New Mod: " + Updater.newMod.getAbsolutePath());
            } else {
                Bugfix.LOG.info("Automatic download is not available, please update manually.");
            }
            Bugfix.LOG.info("==========================================");
        }
    }
}
