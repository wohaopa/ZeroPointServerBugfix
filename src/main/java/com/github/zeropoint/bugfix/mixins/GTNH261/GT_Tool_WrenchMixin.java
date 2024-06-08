package com.github.zeropoint.bugfix.mixins.GTNH261;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.world.BlockEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import appeng.api.parts.IPartHost;
import appeng.block.AEBaseTileBlock;
import appeng.parts.PartPlacement;
import appeng.util.Platform;
import gregtech.common.tools.GT_Tool_Wrench;
import ic2.api.tile.IWrenchable;

@Mixin(value = GT_Tool_Wrench.class, remap = false)
public abstract class GT_Tool_WrenchMixin {

    @Shadow
    private ItemStack wrenchableDrop;
    @Shadow
    private float wrenchableDropRate;
    @Shadow
    private boolean LastEventFromThis;

    /**
     * @author 初夏同学
     * @reason fixNPE
     */
    @Overwrite
    public void onBreakBlock(@Nonnull EntityPlayer player, int x, int y, int z, @Nonnull Block block, byte metadata,
        TileEntity tile, @Nonnull BlockEvent.BreakEvent event) {
        if (tile instanceof IWrenchable wrenchable) {
            if (!wrenchable.wrenchCanRemove(player)) {
                event.setCanceled(true);
                return;
            }
            wrenchableDrop = wrenchable.getWrenchDrop(player);
            wrenchableDropRate = wrenchable.getWrenchDropRate();
        }
        if (block instanceof AEBaseTileBlock aeBaseTileBlock) {
            if (LastEventFromThis) {
                return;
            }
            final boolean sneak = player.isSneaking();
            try {
                LastEventFromThis = true;
                player.setSneaking(true);
                MovingObjectPosition movingObjectPosition = Platform.rayTrace(player, true, false);
                if (movingObjectPosition == null) return;
                final int sideHit = movingObjectPosition.sideHit;
                if (tile instanceof IPartHost) {
                    if (sneak && PartPlacement.place(
                        player.getHeldItem(),
                        x,
                        y,
                        z,
                        sideHit,
                        player,
                        player.worldObj,
                        PartPlacement.PlaceType.INTERACT_FIRST_PASS,
                        0)) {
                        event.setCanceled(true);
                    }
                    return;
                }
                if (aeBaseTileBlock.onBlockActivated(event.world, x, y, z, player, sideHit, x, y, z)) {
                    event.setCanceled(true);
                }
            } finally {
                LastEventFromThis = false;
                player.setSneaking(sneak);
            }
        }
    }
}
