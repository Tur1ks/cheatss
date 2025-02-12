package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

public class GameTestDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final float PADDING = 0.02F;
    private final Map<BlockPos, GameTestDebugRenderer.Marker> markers = Maps.newHashMap();

    public void addMarker(BlockPos pPos, int pColor, String pText, int pRemoveAfter) {
        this.markers.put(pPos, new GameTestDebugRenderer.Marker(pColor, pText, Util.getMillis() + (long)pRemoveAfter));
    }

    @Override
    public void clear() {
        this.markers.clear();
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBufferSource, double pCamX, double pCamY, double pCamZ) {
        long i = Util.getMillis();
        this.markers.entrySet().removeIf(p_113517_ -> i > p_113517_.getValue().removeAtTime);
        this.markers.forEach((p_269737_, p_269738_) -> this.renderMarker(pPoseStack, pBufferSource, p_269737_, p_269738_));
    }

    private void renderMarker(PoseStack pPoseStack, MultiBufferSource pBuffer, BlockPos pPos, GameTestDebugRenderer.Marker pMarker) {
        DebugRenderer.renderFilledBox(
            pPoseStack, pBuffer, pPos, 0.02F, pMarker.getR(), pMarker.getG(), pMarker.getB(), pMarker.getA() * 0.75F
        );
        if (!pMarker.text.isEmpty()) {
            double d0 = (double)pPos.getX() + 0.5;
            double d1 = (double)pPos.getY() + 1.2;
            double d2 = (double)pPos.getZ() + 0.5;
            DebugRenderer.renderFloatingText(pPoseStack, pBuffer, pMarker.text, d0, d1, d2, -1, 0.01F, true, 0.0F, true);
        }
    }

    static class Marker {
        public int color;
        public String text;
        public long removeAtTime;

        public Marker(int pColor, String pText, long pRemoveAtTime) {
            this.color = pColor;
            this.text = pText;
            this.removeAtTime = pRemoveAtTime;
        }

        public float getR() {
            return (float)(this.color >> 16 & 0xFF) / 255.0F;
        }

        public float getG() {
            return (float)(this.color >> 8 & 0xFF) / 255.0F;
        }

        public float getB() {
            return (float)(this.color & 0xFF) / 255.0F;
        }

        public float getA() {
            return (float)(this.color >> 24 & 0xFF) / 255.0F;
        }
    }
}