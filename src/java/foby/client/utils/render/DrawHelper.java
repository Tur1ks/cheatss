package foby.client.utils.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import foby.client.utils.color.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import foby.client.utils.Mine;
import lombok.experimental.UtilityClass;
import org.joml.Matrix4f;
import org.joml.Quaternionf;


import java.awt.*;
import java.util.Objects;

import static foby.client.utils.render.lowrender.RenderMcd.setupRender;


@UtilityClass
public class DrawHelper implements Mine {
    Shader RECTANGLE_SHADER = Shader.create("rectangle", DefaultVertexFormat.POSITION_TEX);


    public static void scale(PoseStack ms, float posX, float posY, float width, float height, float scale, Runnable runnable) {
        float centerX = posX + width / 2;
        float centerY = posY + height / 2;

        ms.pushPose();
        ms.translate(centerX, centerY, 0);
        ms.scale(scale, scale, scale);
        ms.translate(-centerX, -centerY, 0);
        runnable.run();
        ms.popPose();
    }

    public static void rotate(PoseStack ms, float posX, float posY, float width, float height, float angleDegrees, Runnable runnable) {
        float centerX = posX + width / 2;
        float centerY = posY + height / 2;

        ms.translate(centerX, centerY, 0);
        ms.mulPose(Axis.ZP.rotationDegrees(angleDegrees));
        ms.translate(-centerX, -centerY, 0);
        runnable.run();
    }

    public static void scale(Matrix4f ms, float posX, float posY, float width, float height, float scale, Runnable runnable) {
        float centerX = posX + width / 2;
        float centerY = posY + height / 2;

       // ms.pushPose();
        ms.translate(centerX, centerY, 0);
        ms.scale(scale, scale, scale);
        ms.translate(-centerX, -centerY, 0);
        runnable.run();
        //ms.popPose();
    }

    public static void rotate(Matrix4f ms, float posX, float posY, float width, float height, float angleDegrees, Runnable runnable) {
        float centerX = posX + width / 2;
        float centerY = posY + height / 2;

        ms.translate(centerX, centerY, 0);
        ms.rotate(Axis.ZP.rotationDegrees(angleDegrees));
        ms.translate(-centerX, -centerY, 0);
        runnable.run();
    }

    public static void endRender() {
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public void rectangle(PoseStack matrices, float x, float y, float width, float height, float rounding, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tessellator = RenderSystem.renderThreadTesselator();

        Window window = mc.getWindow();
        float guiScale = (float) window.getGuiScale();

        RECTANGLE_SHADER.uniform("position").set(x * guiScale, window.getHeight() - (y * guiScale) - (height * guiScale));

        RECTANGLE_SHADER.uniform("size").set(width * guiScale, height * guiScale);
        RECTANGLE_SHADER.uniform("rounding").set(rounding * guiScale, rounding * guiScale, rounding * guiScale, rounding * guiScale);

        RECTANGLE_SHADER.uniform("smoothness").set(0F, 2F);

        RECTANGLE_SHADER.uniform("color1").set(ColorUtil.getRed(color) / 255F, ColorUtil.getGreen(color) / 255F, ColorUtil.getBlue(color) / 255F, ColorUtil.getAlpha(color) / 255F);

        RECTANGLE_SHADER.uniform("color2").set(ColorUtil.getRed(color) / 255F, ColorUtil.getGreen(color) / 255F, ColorUtil.getBlue(color) / 255F, ColorUtil.getAlpha(color) / 255F);

        RECTANGLE_SHADER.uniform("color3").set(ColorUtil.getRed(color) / 255F, ColorUtil.getGreen(color) / 255F, ColorUtil.getBlue(color) / 255F, ColorUtil.getAlpha(color) / 255F);

        RECTANGLE_SHADER.uniform("color4").set(ColorUtil.getRed(color) / 255F, ColorUtil.getGreen(color) / 255F, ColorUtil.getBlue(color) / 255F, ColorUtil.getAlpha(color) / 255F);

        RECTANGLE_SHADER.bind();

        Matrix4f model = matrices.last().pose();
        BufferBuilder bufferBuilder = tessellator.getBuilder(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);


        bufferBuilder.vertex(model, x, y, 0);
        bufferBuilder.vertex(model, x, y + height, 0);
        bufferBuilder.vertex(model, x + width, y + height, 0);
        bufferBuilder.vertex(model, x + width, y, 0);

        tessellator.draw(bufferBuilder);

        RECTANGLE_SHADER.unbind();

        RenderSystem.disableBlend();
    }

    public static void drawRect(GuiGraphics matrices, float x, float y, float width, float height, Color c) {
        Matrix4f matrix = matrices.pose().last().pose();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin( VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.addVertex(matrix, x, y + height, 0.0F).setColor(c.getRGB());
        bufferbuilder.addVertex(matrix, x + width, y + height, 0.0F).setColor(c.getRGB());
        bufferbuilder.addVertex(matrix, x + width, y, 0.0F).setColor(c.getRGB());
        bufferbuilder.addVertex(matrix, x, y, 0.0F).setColor(c.getRGB());
        BufferUploader.drawWithShader(bufferbuilder.build());
        endRender();
    }

    public void drawSemiRoundRect(PoseStack matrices, float x, float y, float width, float height, float rounding1, float rounding2,float rounding3,float rounding4, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tessellator = RenderSystem.renderThreadTesselator();

        Window window = mc.getWindow();
        float guiScale = (float) window.getGuiScale();

        RECTANGLE_SHADER.uniform("position").set(x * guiScale, window.getHeight() - (y * guiScale) - (height * guiScale));

        RECTANGLE_SHADER.uniform("size").set(width * guiScale, height * guiScale);
        RECTANGLE_SHADER.uniform("rounding").set(rounding1 * guiScale, rounding2 * guiScale, rounding3 * guiScale, rounding4 * guiScale);

        RECTANGLE_SHADER.uniform("smoothness").set(0F, 2F);

        RECTANGLE_SHADER.uniform("color1").set(
                ColorUtil.getRed(color) / 255F,
                ColorUtil.getGreen(color) / 255F,
                ColorUtil.getBlue(color) / 255F,
                ColorUtil.getAlpha(color) / 255F
        );

        RECTANGLE_SHADER.uniform("color2").set(
                ColorUtil.getRed(color) / 255F,
                ColorUtil.getGreen(color) / 255F,
                ColorUtil.getBlue(color) / 255F,
                ColorUtil.getAlpha(color) / 255F
        );

        RECTANGLE_SHADER.uniform("color3").set(
                ColorUtil.getRed(color) / 255F,
                ColorUtil.getGreen(color) / 255F,
                ColorUtil.getBlue(color) / 255F,
                ColorUtil.getAlpha(color) / 255F
        );

        RECTANGLE_SHADER.uniform("color4").set(
                ColorUtil.getRed(color) / 255F,
                ColorUtil.getGreen(color) / 255F,
                ColorUtil.getBlue(color) / 255F,
                ColorUtil.getAlpha(color) / 255F
        );

        RECTANGLE_SHADER.bind();

        Matrix4f model = matrices.last().pose();
        BufferBuilder bufferBuilder = tessellator.getBuilder(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);


        bufferBuilder.vertex(model, x, y, 0);
        bufferBuilder.vertex(model, x, y + height, 0);
        bufferBuilder.vertex(model, x + width, y + height, 0);
        bufferBuilder.vertex(model, x + width, y, 0);

        tessellator.draw(bufferBuilder);

        RECTANGLE_SHADER.unbind();

        RenderSystem.disableBlend();
    }



    public static void drawTexture(ResourceLocation resourceLocation, Matrix4f matrix4f, float x, float y, float width, float height) {
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        drawQuadsTex(matrix4f, x, y, width, height);
    }

    public static void drawQuadsTex(Matrix4f matrix, float x, float y, float width, float height) {
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tesselator.getBuilder(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, x, y, 0).setUv(0.0F, 1.0F);
        bufferBuilder.vertex(matrix, x, y + height, 0).setUv(0.0F, 0.0F);
        bufferBuilder.vertex(matrix, x + width, y + height, 0).setUv(1.0F, 0.0F);
        bufferBuilder.vertex(matrix, x + width, y, 0).setUv(1.0F, 1.0F);
        tesselator.draw(bufferBuilder);
    }



    public static void drawHead(PoseStack ms, Player player, float x, float y, float width, float height) {
        ResourceLocation texture = Objects.requireNonNull((Objects.requireNonNull(mc.getConnection())).getPlayerInfo(player.getUUID())).getSkin().texture();
        RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.blendFunc(770, 771);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = ms.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, x, y, 0).setUv(1.125f, 1.125f);
        bufferbuilder.vertex(matrix4f, x, y + height, 0).setUv(1.125f, 1.25f);
        bufferbuilder.vertex(matrix4f, x + width, y + height, 0).setUv(1.25f, 1.25f);
        bufferbuilder.vertex(matrix4f, x + width, y, 0).setUv(1.25f, 1.125f);
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    public void rectRGB(PoseStack matrices, float x, float y, float width, float height, float rounding, int color, int color2, int color3, int color4) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tessellator = RenderSystem.renderThreadTesselator();

        Window window = mc.getWindow();
        float guiScale = (float) window.getGuiScale();

        RECTANGLE_SHADER.uniform("position").set(x * guiScale, window.getHeight() - (y * guiScale) - (height * guiScale));

        RECTANGLE_SHADER.uniform("size").set(width * guiScale, height * guiScale);
        RECTANGLE_SHADER.uniform("rounding").set(rounding * guiScale, rounding * guiScale, rounding * guiScale, rounding * guiScale);

        RECTANGLE_SHADER.uniform("smoothness").set(0F, 2F);

        RECTANGLE_SHADER.uniform("color1").set(
                ColorUtil.getRed(color) / 255F,
                ColorUtil.getGreen(color) / 255F,
                ColorUtil.getBlue(color) / 255F,
                ColorUtil.getAlpha(color) / 255F
        );

        RECTANGLE_SHADER.uniform("color2").set(
                ColorUtil.getRed(color2) / 255F,
                ColorUtil.getGreen(color2) / 255F,
                ColorUtil.getBlue(color2) / 255F,
                ColorUtil.getAlpha(color2) / 255F
        );

        RECTANGLE_SHADER.uniform("color3").set(
                ColorUtil.getRed(color3) / 255F,
                ColorUtil.getGreen(color3) / 255F,
                ColorUtil.getBlue(color3) / 255F,
                ColorUtil.getAlpha(color3) / 255F
        );

        RECTANGLE_SHADER.uniform("color4").set(
                ColorUtil.getRed(color4) / 255F,
                ColorUtil.getGreen(color4) / 255F,
                ColorUtil.getBlue(color4) / 255F,
                ColorUtil.getAlpha(color4) / 255F
        );

        RECTANGLE_SHADER.bind();

        Matrix4f model = matrices.last().pose();
        BufferBuilder bufferBuilder = tessellator.getBuilder(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);


        bufferBuilder.vertex(model, x, y, 0);
        bufferBuilder.vertex(model, x, y + height, 0);
        bufferBuilder.vertex(model, x + width, y + height, 0);
        bufferBuilder.vertex(model, x + width, y, 0);

        tessellator.draw(bufferBuilder);

        RECTANGLE_SHADER.unbind();

        RenderSystem.disableBlend();
    }


    public static int reAlphaInt(final int color,
                                 final int alpha) {
        return (Mth.clamp(alpha, 0, 255) << 24) | (color & 16777215);
    }



    public static boolean isInRegion(final double mouseX,
                                     final double mouseY,
                                     final float x,
                                     final float y,
                                     final float width,
                                     final float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }




    public void drawShadow(PoseStack matrices, float x, float y, float width, float height, float radius, float shadowRadius, int shadowStrength, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Draw multiple layers of shadows with decreasing alpha
        for (int i = 0; i < shadowStrength; i++) {
            float spread = i * (shadowRadius / shadowStrength);
            int shadowColor = reAlphaInt(color, (int)(255 * (1 - (float)i / shadowStrength) * 0.3f));

            rectangle(matrices, x - spread, y - spread, width + (spread * 2), height + (spread * 2), radius + spread, shadowColor);
        }

        // Draw main rectangle
        rectangle(matrices, x, y, width, height, radius, color);

        RenderSystem.disableBlend();
    }


    public static void drawShadowedTriangle(PoseStack matrices, float x1, float y1, float x2, float y2, float x3, float y3, float shadowRadius, int shadowStrength, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Draw shadow layers
        for (int i = 0; i < shadowStrength; i++) {
            float spread = i * (shadowRadius / shadowStrength);
            int shadowColor = reAlphaInt(color, (int)(255 * (1 - (float)i / shadowStrength) * 0.3f));

            // Draw shadow triangle with offset
            drawFilledTriangle(
                    matrices,
                    x1 - spread, y1 - spread,
                    x2 - spread, y2 - spread,
                    x3 - spread, y3 - spread,
                    shadowColor
            );
        }

        // Draw main triangle
        drawFilledTriangle(matrices, x1, y1, x2, y2, x3, y3, color);

        RenderSystem.disableBlend();
    }


    public static void drawFilledTriangle(PoseStack matrices, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        Matrix4f matrix = matrices.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.defaultBlendFunc();

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(color);
        bufferBuilder.vertex(matrix, x3, y3, 0.0F).color(color);

        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void drawEntity(GuiGraphics graphics, float x, float y, int size, LivingEntity entity) {
        float f = (float)Math.atan((x / 40.0F));
        float f1 = (float)Math.atan((y / 40.0F));
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, 1050.0F);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        poseStack.translate(0.0F, 0.0F, 1000.0F);
        poseStack.scale((float)size, (float)size, (float)size);
        Quaternionf quaternion = new Quaternionf().rotationZ((float)Math.PI);
        Quaternionf quaternion1 = new Quaternionf().rotationX(f1 * 20.0F * ((float)Math.PI / 180F));
        quaternion.mul(quaternion1);
        poseStack.mulPose(quaternion);
        float f2 = entity.yBodyRot;
        float f3 = entity.getYRot();
        float f4 = entity.getXRot();
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;
        entity.yBodyRot = 180.0F + f * 20.0F;
        entity.setYRot(180.0F + f * 40.0F);
        entity.setXRot(-f1 * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conjugate();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack, bufferSource, 15728880);
        });
        bufferSource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        entity.yBodyRot = f2;
        entity.setYRot(f3);
        entity.setXRot(f4);
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;
        poseStack.popPose();
    }
}
