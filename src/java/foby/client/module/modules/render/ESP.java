package foby.client.module.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foby.client.event.EventHandler;
import foby.client.event.events.impl.RenderEvent3D;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.ui.clickgui.setting.settings.BooleanSetting;
import foby.client.ui.clickgui.setting.settings.ModeSetting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static foby.client.Foby.mc;

@ModuleAnnotation(name = "ESP", desc = "Renders entities through walls", category = Category.RENDER)
public class ESP extends Module {
    private final BooleanSetting players = new BooleanSetting("Players", "Show players", true);
    private final ModeSetting mode = new ModeSetting("Mode", () -> true, "Box", "2D", "Outline");
    private final BooleanSetting showHealth = new BooleanSetting("Health", "Show entity health", true);

    public ESP() {
        addSettings(players, mode, showHealth);
    }

    @EventHandler
    public void onRender3D(RenderEvent3D event) {
        if (mc.level == null || mc.player == null) return;

        List<Entity> entities = new ArrayList<>();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (shouldRenderESP(entity)) {
                entities.add(entity);
            }
        }

        for (Entity entity : entities) {
            double x = entity.xOld + (entity.getX() - entity.xOld) * event.getPartialTicks();
            double y = entity.yOld + (entity.getY() - entity.yOld) * event.getPartialTicks();
            double z = entity.zOld + (entity.getZ() - entity.zOld) * event.getPartialTicks();

            Vec3 renderPos = new Vec3(x, y, z);
            AABB box = entity.getBoundingBox()
                    .move(-entity.getX(), -entity.getY(), -entity.getZ())
                    .move(renderPos);

            Color color = getEntityColor(entity);

            switch (mode.getMode()) {
                case "Box" -> renderBox(event.getPoseStack(), event.getMatrix4f(), box, color);
                case "2D" -> render2D(event.getPoseStack(), event.getMatrix4f(), box, color);
                case "Outline" -> renderOutline(event.getPoseStack(), event.getMatrix4f(), box, color);
            }

            if (showHealth.get() && entity instanceof Player player) {
                renderHealthBar(event.getPoseStack(), event.getMatrix4f(), box, player);
            }
        }
    }

    private boolean shouldRenderESP(Entity entity) {
        if (entity == mc.player) return false;
        return entity instanceof Player && players.get();
    }

    private Color getEntityColor(Entity entity) {
        if (entity instanceof Player) {
            return new Color(255, 0, 0, 150);
        }
        return new Color(255, 255, 255, 150);
    }

    private void renderBox(PoseStack poseStack, Matrix4f matrix, AABB box, Color color) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        // Bottom
        drawLine(poseStack, matrix, minX, minY, minZ, maxX, minY, minZ, color);
        drawLine(poseStack, matrix, maxX, minY, minZ, maxX, minY, maxZ, color);
        drawLine(poseStack, matrix, maxX, minY, maxZ, minX, minY, maxZ, color);
        drawLine(poseStack, matrix, minX, minY, maxZ, minX, minY, minZ, color);

        // Top
        drawLine(poseStack, matrix, minX, maxY, minZ, maxX, maxY, minZ, color);
        drawLine(poseStack, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, color);
        drawLine(poseStack, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, color);
        drawLine(poseStack, matrix, minX, maxY, maxZ, minX, maxY, minZ, color);

        // Connections
        drawLine(poseStack, matrix, minX, minY, minZ, minX, maxY, minZ, color);
        drawLine(poseStack, matrix, maxX, minY, minZ, maxX, maxY, minZ, color);
        drawLine(poseStack, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, color);
        drawLine(poseStack, matrix, minX, minY, maxZ, minX, maxY, maxZ, color);
    }

    private void render2D(PoseStack poseStack, Matrix4f matrix, AABB box, Color color) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;

        drawLine(poseStack, matrix, minX, minY, minZ, maxX, minY, minZ, color);
        drawLine(poseStack, matrix, maxX, minY, minZ, maxX, maxY, minZ, color);
        drawLine(poseStack, matrix, maxX, maxY, minZ, minX, maxY, minZ, color);
        drawLine(poseStack, matrix, minX, maxY, minZ, minX, minY, minZ, color);
    }

    private void renderOutline(PoseStack poseStack, Matrix4f matrix, AABB box, Color color) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        float width = (maxX - minX) * 0.2f;
        float height = (maxY - minY) * 0.2f;

        // Corners
        drawLine(poseStack, matrix, minX, minY, minZ, minX + width, minY, minZ, color);
        drawLine(poseStack, matrix, minX, minY, minZ, minX, minY + height, minZ, color);

        drawLine(poseStack, matrix, maxX, minY, minZ, maxX - width, minY, minZ, color);
        drawLine(poseStack, matrix, maxX, minY, minZ, maxX, minY + height, minZ, color);

        drawLine(poseStack, matrix, minX, maxY, minZ, minX + width, maxY, minZ, color);
        drawLine(poseStack, matrix, minX, maxY, minZ, minX, maxY - height, minZ, color);

        drawLine(poseStack, matrix, maxX, maxY, minZ, maxX - width, maxY, minZ, color);
        drawLine(poseStack, matrix, maxX, maxY, minZ, maxX, maxY - height, minZ, color);
    }

    private void renderHealthBar(PoseStack poseStack, Matrix4f matrix, AABB box, Player player) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healthPercent = health / maxHealth;

        Color healthColor = new Color(
                (int) (255 * (1 - healthPercent)),
                (int) (255 * healthPercent),
                0,
                200
        );

        float barWidth = (float) (box.maxX - box.minX);
        float barHeight = 0.1f;
        float barY = (float) box.maxY + 0.2f;

        drawLine(poseStack, matrix,
                (float) box.minX, barY, (float) box.minZ,
                (float) box.minX + (barWidth * healthPercent), barY, (float) box.minZ,
                healthColor);
    }

    private void drawLine(PoseStack poseStack, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, Color color) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        int packedColor = color.getRGB();

        bufferBuilder.vertex(matrix, x1, y1, z1)
                .color(packedColor);
        bufferBuilder.vertex(matrix, x2, y2, z2)
                .color(packedColor);

        BufferUploader.drawWithShader(bufferBuilder.end());
    }

}
