package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

public class SolidFaceRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;

    public SolidFaceRenderer(Minecraft pMinecraft) {
        this.minecraft = pMinecraft;
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBufferSource, double pCamX, double pCamY, double pCamZ) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        BlockGetter blockgetter = this.minecraft.player.level();
        BlockPos blockpos = BlockPos.containing(pCamX, pCamY, pCamZ);

        for (BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-6, -6, -6), blockpos.offset(6, 6, 6))) {
            BlockState blockstate = blockgetter.getBlockState(blockpos1);
            if (!blockstate.is(Blocks.AIR)) {
                VoxelShape voxelshape = blockstate.getShape(blockgetter, blockpos1);

                for (AABB aabb : voxelshape.toAabbs()) {
                    AABB aabb1 = aabb.move(blockpos1).inflate(0.002);
                    float f = (float)(aabb1.minX - pCamX);
                    float f1 = (float)(aabb1.minY - pCamY);
                    float f2 = (float)(aabb1.minZ - pCamZ);
                    float f3 = (float)(aabb1.maxX - pCamX);
                    float f4 = (float)(aabb1.maxY - pCamY);
                    float f5 = (float)(aabb1.maxZ - pCamZ);
                    int i = -2130771968;
                    if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.WEST)) {
                        VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.debugFilledBox());
                        vertexconsumer.addVertex(matrix4f, f, f1, f2).setColor(-2130771968);
                        vertexconsumer.addVertex(matrix4f, f, f1, f5).setColor(-2130771968);
                        vertexconsumer.addVertex(matrix4f, f, f4, f2).setColor(-2130771968);
                        vertexconsumer.addVertex(matrix4f, f, f4, f5).setColor(-2130771968);
                    }

                    if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.SOUTH)) {
                        VertexConsumer vertexconsumer1 = pBufferSource.getBuffer(RenderType.debugFilledBox());
                        vertexconsumer1.addVertex(matrix4f, f, f4, f5).setColor(-2130771968);
                        vertexconsumer1.addVertex(matrix4f, f, f1, f5).setColor(-2130771968);
                        vertexconsumer1.addVertex(matrix4f, f3, f4, f5).setColor(-2130771968);
                        vertexconsumer1.addVertex(matrix4f, f3, f1, f5).setColor(-2130771968);
                    }

                    if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.EAST)) {
                        VertexConsumer vertexconsumer2 = pBufferSource.getBuffer(RenderType.debugFilledBox());
                        vertexconsumer2.addVertex(matrix4f, f3, f1, f5).setColor(-2130771968);
                        vertexconsumer2.addVertex(matrix4f, f3, f1, f2).setColor(-2130771968);
                        vertexconsumer2.addVertex(matrix4f, f3, f4, f5).setColor(-2130771968);
                        vertexconsumer2.addVertex(matrix4f, f3, f4, f2).setColor(-2130771968);
                    }

                    if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.NORTH)) {
                        VertexConsumer vertexconsumer3 = pBufferSource.getBuffer(RenderType.debugFilledBox());
                        vertexconsumer3.addVertex(matrix4f, f3, f4, f2).setColor(-2130771968);
                        vertexconsumer3.addVertex(matrix4f, f3, f1, f2).setColor(-2130771968);
                        vertexconsumer3.addVertex(matrix4f, f, f4, f2).setColor(-2130771968);
                        vertexconsumer3.addVertex(matrix4f, f, f1, f2).setColor(-2130771968);
                    }

                    if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.DOWN)) {
                        VertexConsumer vertexconsumer4 = pBufferSource.getBuffer(RenderType.debugFilledBox());
                        vertexconsumer4.addVertex(matrix4f, f, f1, f2).setColor(-2130771968);
                        vertexconsumer4.addVertex(matrix4f, f3, f1, f2).setColor(-2130771968);
                        vertexconsumer4.addVertex(matrix4f, f, f1, f5).setColor(-2130771968);
                        vertexconsumer4.addVertex(matrix4f, f3, f1, f5).setColor(-2130771968);
                    }

                    if (blockstate.isFaceSturdy(blockgetter, blockpos1, Direction.UP)) {
                        VertexConsumer vertexconsumer5 = pBufferSource.getBuffer(RenderType.debugFilledBox());
                        vertexconsumer5.addVertex(matrix4f, f, f4, f2).setColor(-2130771968);
                        vertexconsumer5.addVertex(matrix4f, f, f4, f5).setColor(-2130771968);
                        vertexconsumer5.addVertex(matrix4f, f3, f4, f2).setColor(-2130771968);
                        vertexconsumer5.addVertex(matrix4f, f3, f4, f5).setColor(-2130771968);
                    }
                }
            }
        }
    }
}