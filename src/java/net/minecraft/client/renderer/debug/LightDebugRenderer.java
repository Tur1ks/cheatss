package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class LightDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int MAX_RENDER_DIST = 10;

    public LightDebugRenderer(Minecraft pMinecraft) {
        this.minecraft = pMinecraft;
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBufferSource, double pCamX, double pCamY, double pCamZ) {
        Level level = this.minecraft.level;
        BlockPos blockpos = BlockPos.containing(pCamX, pCamY, pCamZ);
        LongSet longset = new LongOpenHashSet();

        for (BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-10, -10, -10), blockpos.offset(10, 10, 10))) {
            int i = level.getBrightness(LightLayer.SKY, blockpos1);
            float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
            int j = Mth.hsvToRgb(f, 0.9F, 0.9F);
            long k = SectionPos.blockToSection(blockpos1.asLong());
            if (longset.add(k)) {
                DebugRenderer.renderFloatingText(
                    pPoseStack,
                    pBufferSource,
                    level.getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of(k)),
                    (double)SectionPos.sectionToBlockCoord(SectionPos.x(k), 8),
                    (double)SectionPos.sectionToBlockCoord(SectionPos.y(k), 8),
                    (double)SectionPos.sectionToBlockCoord(SectionPos.z(k), 8),
                    16711680,
                    0.3F
                );
            }

            if (i != 15) {
                DebugRenderer.renderFloatingText(
                    pPoseStack,
                    pBufferSource,
                    String.valueOf(i),
                    (double)blockpos1.getX() + 0.5,
                    (double)blockpos1.getY() + 0.25,
                    (double)blockpos1.getZ() + 0.5,
                    j
                );
            }
        }
    }
}