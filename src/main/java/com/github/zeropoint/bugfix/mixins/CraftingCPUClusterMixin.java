package com.github.zeropoint.bugfix.mixins;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import appeng.me.cluster.implementations.CraftingCPUCluster;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class CraftingCPUClusterMixin {

    @Redirect(
        method = "injectItems",
        at = @At(value = "INVOKE", target = "Ljava/lang/Object;equals(Ljava/lang/Object;)Z"))
    private boolean injectItems(Object instance, Object o) {
        return Objects.equals(instance, o);
    }

    @Redirect(
        method = "getItemStack",
        at = @At(value = "INVOKE", target = "Ljava/lang/Object;equals(Ljava/lang/Object;)Z"))
    private boolean getItemStack(Object instance, Object o) {
        return Objects.equals(instance, o);
    }

}
