package com.github.zeropoint.bugfix.mixins;

import javax.swing.JOptionPane;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.base.Strings;

@Mixin(GuiScreen.class)
public abstract class GuiScreenMixin {

    private static final boolean nonWindows = LWJGLUtil.getPlatform() != LWJGLUtil.PLATFORM_WINDOWS;

    // @Inject(method = "handleKeyboardInput()V", at = @At("HEAD"), cancellable = true)
    // private void injected(CallbackInfo ci) {
    // ci.cancel();

    /**
     * @author 初夏同学
     * @reason InputFix
     */
    @Overwrite
    public void handleKeyboardInput() {

        char c = Keyboard.getEventCharacter();
        int k = Keyboard.getEventKey();

        if (Keyboard.getEventKeyState() || (k == 0 && Character.isDefined(c))) {
            if (nonWindows && k == 88) {
                for (char c1 : Strings.nullToEmpty(JOptionPane.showInputDialog(""))
                    .toCharArray()) this.keyTyped(c1, 0);
                return;
            }

            this.keyTyped(c, k);
        }
    }

    @Shadow
    protected abstract void keyTyped(char typedChar, int keyCode);
}
