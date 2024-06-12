package com.github.zeropoint.bugfix;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.ChatComponentText;

import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

    Thread inputDeviceUpdateThread;

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        inputDeviceUpdateThread = new InputDeviceUpdateThread();
        inputDeviceUpdateThread.start();
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent playerEvent) {
        if (Updater.canUpdate) {
            msg(playerEvent, "==========================================");
            msg(playerEvent, "ZeroPoint Bugfix has a new version! ");
            msg(playerEvent, "Current: " + Tags.VERSION + " -> Latest: " + Updater.newVersion);
            if (Updater.downloaded) {
                msg(playerEvent, "It has been automatically downloaded to the mods directory");
                msg(playerEvent, "and will be replaced automatically next time you start.");
                msg(playerEvent, "New Mod: " + Updater.newMod.getAbsolutePath());
            } else {
                msg(playerEvent, "Automatic download is not available, please update manually.");
            }
            msg(playerEvent, "==========================================");
        }
    }

    private static void msg(PlayerEvent.PlayerLoggedInEvent playerEvent, String msg) {
        playerEvent.player.addChatMessage(new ChatComponentText(msg));
    }
}
