package com.github.zeropoint.bugfix.mixins.GTNH261;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.github.bartimaeusnek.bartworks.client.renderer.RendererGlasBlock;
import com.github.bartimaeusnek.bartworks.common.blocks.BW_GlasBlocks;
import com.github.bartimaeusnek.bartworks.common.blocks.BW_GlasBlocks2;
import com.github.bartimaeusnek.bartworks.common.loaders.ItemRegistry;

@Mixin(value = RendererGlasBlock.class, remap = false)
public abstract class RendererGlasBlockMixin {

    // spotless:off
    /**
     * @author 初夏同学
     * @reason fix <a href=
     *         "https://github.com/GTNewHorizons/GT5-Unofficial/pull/2640">GTNewHorizons/GT5-Unofficial/pull/2640</a>
     */
    @Overwrite
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        boolean flag = false;
        if (block instanceof BW_GlasBlocks) {
            flag |= renderer.renderStandardBlock(ItemRegistry.bw_fake_glasses, x, y, z);
            flag |= renderer.renderStandardBlockWithColorMultiplier(block, x, y, z,
                ((BW_GlasBlocks) block).getColor(world.getBlockMetadata(x, y, z))[0] / 255f,
                ((BW_GlasBlocks) block).getColor(world.getBlockMetadata(x, y, z))[1] / 255f,
                ((BW_GlasBlocks) block).getColor(world.getBlockMetadata(x, y, z))[2] / 255f);
        }
        if (block instanceof BW_GlasBlocks2) {
            flag |= renderer.renderStandardBlock(ItemRegistry.bw_fake_glasses2, x, y, z);
            flag |= renderer.renderStandardBlockWithColorMultiplier(block, x, y, z,
                ((BW_GlasBlocks2) block).getColor(world.getBlockMetadata(x, y, z))[0] / 255f,
                ((BW_GlasBlocks2) block).getColor(world.getBlockMetadata(x, y, z))[1] / 255f,
                ((BW_GlasBlocks2) block).getColor(world.getBlockMetadata(x, y, z))[2] / 255f);
        }
        return flag;
    }
    // spotless:on
}
