package com.github.zeropoint.bugfix.mixins.GTNH261;

import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.structures.courtyard.ComponentTFNagaCourtyardRotatedAbstract;

@Mixin(value = ComponentTFNagaCourtyardRotatedAbstract.class, remap = false)
public class ComponentTFNagaCourtyardRotatedAbstractMixin {

    @Shadow
    protected ForgeDirection EtchedNagastoneNorth;
    @Shadow
    protected ForgeDirection EtchedNagastoneSouth;
    @Shadow
    protected ForgeDirection EtchedNagastoneWest;
    @Shadow
    protected ForgeDirection EtchedNagastoneEast;
    @Shadow
    protected BlockTFNagastone2.Yaw NagastoneNorth;
    @Shadow
    protected BlockTFNagastone2.Yaw NagastoneSouth;
    @Shadow
    protected BlockTFNagastone2.Yaw NagastoneWest;
    @Shadow
    protected BlockTFNagastone2.Yaw NagastoneEast;

    @Inject(method = "<init>()V", at = @At("TAIL"))
    private void injected(CallbackInfo ci) {
        EtchedNagastoneNorth = ForgeDirection.UP;
        EtchedNagastoneSouth = ForgeDirection.UP;
        EtchedNagastoneWest = ForgeDirection.UP;
        EtchedNagastoneEast = ForgeDirection.UP;

        NagastoneNorth = BlockTFNagastone2.Yaw.EAST;
        NagastoneSouth = BlockTFNagastone2.Yaw.EAST;
        NagastoneWest = BlockTFNagastone2.Yaw.EAST;
        NagastoneEast = BlockTFNagastone2.Yaw.EAST;
    }
}
