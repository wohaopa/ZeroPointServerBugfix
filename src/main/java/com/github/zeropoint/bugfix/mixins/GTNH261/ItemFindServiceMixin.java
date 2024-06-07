package com.github.zeropoint.bugfix.mixins.GTNH261;

import net.minecraftforge.fluids.FluidTankInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.gtnh.findit.service.itemfinder.ItemFindService;

@Mixin(value = ItemFindService.class, remap = false)
public abstract class ItemFindServiceMixin {

    @ModifyVariable(method = "findItemInInventory", at = @At("STORE"), ordinal = 0)
    private FluidTankInfo[] injected(FluidTankInfo[] tankInfo) {
        return tankInfo == null ? new FluidTankInfo[0] : tankInfo;
    }
}
