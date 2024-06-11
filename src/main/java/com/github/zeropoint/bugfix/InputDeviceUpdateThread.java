package com.github.zeropoint.bugfix;

import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputDeviceUpdateThread extends Thread {

    public static boolean enable = true;

    public InputDeviceUpdateThread() {
        this.setName("Input Device Update Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            if (enable) InputDeviceUpdateThread.pollDevices();
            else Thread.yield();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Bugfix.LOG.warn("ClientProxy thread warn: ", e);
            }
        }
    }

    /**
     * Copied from the org.lwjgl.opengl.Display.pollDevices()
     * Because he is a non-public method
     */
    static void pollDevices() {
        // Poll the input devices while we're here
        if (Mouse.isCreated()) {
            Mouse.poll();
            Mouse.updateCursor();
        }

        if (Keyboard.isCreated()) {
            Keyboard.poll();
        }

        if (Controllers.isCreated()) {
            Controllers.poll();
        }
    }

}
