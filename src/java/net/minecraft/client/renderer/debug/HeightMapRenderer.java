package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector3f;

public class HeightMapRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int CHUNK_DIST = 2;
    private static final float BOX_HEIGHT = 0.09375F;

    public HeightMapRenderer(Minecraft pMinecraft) {
        this.minecraft = pMinecraft;
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBufferSource, double pCamX, double pCamY, double pCamZ) {
        LevelAccessor levelaccessor = this.minecraft.level;
        VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.debugFilledBox());
        BlockPos blockpos = BlockPos.containing(pCamX, 0.0, pCamZ);

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                ChunkAccess chunkaccess = levelaccessor.getChunk(blockpos.offset(i * 16, 0, j * 16));

                for (Entry<Heightmap.Types, Heightmap> entry : chunkaccess.getHeightmaps()) {
                    Heightmap.Types heightmap$types = entry.getKey();
                    ChunkPos chunkpos = chunkaccess.getPos();
                    Vector3f vector3f = this.getColor(heightmap$types);

                    for (int k = 0; k < 16; k++) {
                        for (int l = 0; l < 16; l++) {
                            int i1 = SectionPos.sectionToBlockCoord(chunkpos.x, k);
                            int j1 = SectionPos.sectionToBlockCoord(chunkpos.z, l);
                            float f = (float)(
                                (double)((float)levelaccessor.getHeight(heightmap$types, i1, j1) + (float)heightmap$types.ordinal() * 0.09375F) - pCamY
                            );
                            LevelRenderer.addChainedFilledBoxVertices(
                                pPoseStack,
                                vertexconsumer,
                                (double)((float)i1 + 0.25F) - pCamX,
                                (double)f,
                                (double)((float)j1 + 0.25F) - pCamZ,
                                (double)((float)i1 + 0.75F) - pCamX,
                                (double)(f + 0.09375F),
                                (double)((float)j1 + 0.75F) - pCamZ,
                                vector3f.x(),
                                vector3f.y(),
                                vector3f.z(),
                                1.0F
                            );
                        }
                    }
                }
            }
        }
    }

    private Vector3f getColor(Heightmap.Types pTypes) {
        return switch (pTypes) {
            case WORLD_SURFACE_WG -> new Vector3f(1.0F, 1.0F, 0.0F);
            case OCEAN_FLOOR_WG -> new Vector3f(1.0F, 0.0F, 1.0F);
            case WORLD_SURFACE -> new Vector3f(0.0F, 0.7F, 0.0F);
            case OCEAN_FLOOR -> new Vector3f(0.0F, 0.0F, 0.5F);
            case MOTION_BLOCKING -> new Vector3f(0.0F, 0.3F, 0.3F);
            case MOTION_BLOCKING_NO_LEAVES -> new Vector3f(0.0F, 0.5F, 0.5F);
        };
    }
}