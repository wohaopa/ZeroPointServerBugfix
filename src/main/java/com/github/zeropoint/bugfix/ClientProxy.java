package com.github.zeropoint.bugfix;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

    Thread inputDeviceUpdateThread;

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        inputDeviceUpdateThread = new InputDeviceUpdateThread();
        inputDeviceUpdateThread.start();
    }

}
