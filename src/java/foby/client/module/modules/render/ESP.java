package foby.client.module.modules.render;

import com.mojang.blaze3d.vertex.*;
import foby.client.event.EventHandler;
import foby.client.event.events.impl.RenderEvent2D;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.themes.Theme;
import foby.client.ui.clickgui.setting.settings.BooleanSetting;
import foby.client.utils.fonts.FontRenderers;
import foby.client.utils.render.DrawHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static foby.client.Foby.mc;
import static foby.client.Foby.themesUtil;
import static foby.client.utils.fonts.FontRenderers.msSemi;

@ModuleAnnotation(name = "ESP", desc = "2D Player ESP", category = Category.RENDER)
public class ESP extends Module {
    private final BooleanSetting showHealth = new BooleanSetting("Health", "Show health value", true);
    private final BooleanSetting showDistance = new BooleanSetting("Distance", "Show distance", true);

    private static final float MAX_RENDER_DISTANCE = 64.0f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 1.5f;

    public ESP() {
        addSettings(showHealth, showDistance);
    }

    @EventHandler
    public void onRender2D(RenderEvent2D event) {
        if (mc.level == null || mc.player == null) return;

        mc.level.players().forEach(player -> {
            if (player != mc.player && player.getTeam() != null) {
                player.getTeam().setNameTagVisibility(Team.Visibility.NEVER);
            }
        });
        mc.level.entitiesForRendering().forEach(entity -> {
            if (entity instanceof ArmorStand) {
                entity.setCustomNameVisible(false);
                entity.setCustomName(null);
            }
        });

        GuiGraphics graphics = event.getGuiGraphics();
        PoseStack poseStack = graphics.pose();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Player player) || player == mc.player) continue;

            double distance = mc.player.distanceTo(player);
            if (distance > MAX_RENDER_DISTANCE) continue;

            Vector2f screenPos = worldToScreen(entity);
            if (screenPos == null) continue;

            float scale = calculateScale(distance);

            poseStack.pushPose();
            poseStack.translate(screenPos.x, screenPos.y, 0);
            poseStack.scale(scale, scale, 1.0f);
            poseStack.translate(-screenPos.x, -screenPos.y, 0);

            renderNametag(poseStack, player, screenPos);
            if (showHealth.get()) {
                renderHealthBar(poseStack, player, screenPos);
            }
            if (showDistance.get()) {
                renderDistance(poseStack, player, screenPos);
            }

            poseStack.popPose();
        }
    }

    private float calculateScale(double distance) {
        // Базовый скейл от дистанции
        float scale = (float) Math.max(MIN_SCALE,
                1.0f - (distance / MAX_RENDER_DISTANCE) * (1.0f - MIN_SCALE));

        // Оптифайн зум
        if (mc.options.fov().get() < 30) { // Сильный зум
            scale *= 2.5f;
        } else if (mc.options.fov().get() < 50) { // Средний зум
            scale *= 1.8f;
        } else if (mc.options.fov().get() < 70) { // Слабый зум
            scale *= 1.3f;
        }

        // Состояния игрока
        if (mc.player.isSprinting()) {
            scale *= 0.85f; // Уменьшаем при спринте
        }
        if (mc.player.isFallFlying()) {
            scale *= 0.7f; // Сильнее уменьшаем при полёте
        }
        if (mc.player.isSwimming()) {
            scale *= 0.9f; // Уменьшаем при плавании
        }
        if (mc.player.isCrouching()) {
            scale *= 1.2f; // Увеличиваем при присяде
        }

        // Финальное ограничение
        return Math.min(MAX_SCALE, Math.max(MIN_SCALE, scale));
    }

    private Vector2f worldToScreen(Entity entity) {
        Vec3 pos = entity.position().add(0, entity.getBbHeight() + 0.5f, 0);

        // Player state position adjustments
        if (entity instanceof Player player) {
            if (player.isFallFlying()) {
                pos = pos.add(0, -0.8, 0);
            }
            if (player.isCrouching()) {
                pos = pos.add(0, 0.35, 0);
            }
            if (player.isSwimming()) {
                pos = pos.add(0, 0.4, 0);
            }
            if (player.isSprinting()) {
                pos = pos.add(0, 0.2, 0);
            }
        }

        Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();

        // FOV adjustments
        float fov = mc.options.fov().get();
        float adjustedFov = fov;
        if (fov < 30) { // Strong zoom
            adjustedFov *= 0.4f;
        } else if (fov < 50) { // Medium zoom
            adjustedFov *= 0.6f;
        } else if (fov < 70) { // Light zoom
            adjustedFov *= 0.8f;
        }

        Matrix4f viewMatrix = new Matrix4f();
        float yaw = mc.gameRenderer.getMainCamera().getYRot();
        float pitch = mc.gameRenderer.getMainCamera().getXRot();

        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(yaw + 180), new Vector3f(0, 1, 0));
        viewMatrix.translate(new Vector3f(
                (float) -camera.x,
                (float) -camera.y,
                (float) -camera.z
        ));

        Matrix4f projectionMatrix = new Matrix4f();
        float aspectRatio = (float) mc.getWindow().getGuiScaledWidth() / mc.getWindow().getGuiScaledHeight();

        projectionMatrix.identity();
        projectionMatrix.perspective((float) Math.toRadians(adjustedFov), aspectRatio, 0.05f, 1000.0f);

        Vector4f vector = new Vector4f(
                (float) pos.x,
                (float) pos.y,
                (float) pos.z,
                1.0f
        );

        vector.mul(viewMatrix);
        vector.mul(projectionMatrix);

        if (vector.w <= 0.0f) return null;

        vector.mul(1.0f / vector.w);

        float screenX = (vector.x * 0.5f + 0.5f) * mc.getWindow().getGuiScaledWidth();
        float screenY = (1.0f - (vector.y * 0.5f + 0.5f)) * mc.getWindow().getGuiScaledHeight();

        return new Vector2f(screenX, screenY);
    }

    private void renderNametag(PoseStack poseStack, Player player, Vector2f pos) {
        String name = player.getGameProfile().getName();
        float textWidth = FontRenderers.info(msSemi, 18).getStringWidth(name);
        Theme currentTheme = themesUtil.getCurrentStyle();

        DrawHelper.drawShadow(
                poseStack,
                pos.x - textWidth / 2 - 4,
                pos.y - 22,
                textWidth + 8,
                16,
                4,
                10f,
                8,
                currentTheme.colors[3]
        );

        FontRenderers.info(msSemi, 18).drawString(
                poseStack,
                name,
                (int)(pos.x - textWidth / 2),
                (int)(pos.y - 20),
                currentTheme.colors[1]
        );
    }

    private void renderHealthBar(PoseStack poseStack, Player player, Vector2f pos) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healthPercent = health / maxHealth;
        String healthText = String.format("%.1f HP", health);
        float textWidth = FontRenderers.info(msSemi, 16).getStringWidth(healthText);
        Theme currentTheme = themesUtil.getCurrentStyle();

        DrawHelper.drawShadow(
                poseStack,
                pos.x - 20,
                pos.y - 2,
                40,
                4,
                2,
                8f,
                6,
                currentTheme.colors[3]
        );

        DrawHelper.drawShadow(
                poseStack,
                pos.x - 20,
                pos.y - 2,
                40 * healthPercent,
                4,
                2,
                8f,
                6,
                currentTheme.colors[1]
        );

        FontRenderers.info(msSemi, 16).drawString(
                poseStack,
                healthText,
                (int)(pos.x - textWidth / 2),
                (int)(pos.y + 4),
                currentTheme.colors[1]
        );
    }

    private void renderDistance(PoseStack poseStack, Player player, Vector2f pos) {
        double distance = mc.player.distanceTo(player);
        String text = String.format("%.1fm", distance);
        float textWidth = FontRenderers.info(msSemi, 16).getStringWidth(text);
        Theme currentTheme = themesUtil.getCurrentStyle();

        DrawHelper.drawShadow(
                poseStack,
                pos.x - textWidth / 2 - 4,
                pos.y + 14,
                textWidth + 8,
                14,
                3,
                8f,
                6,
                currentTheme.colors[3]
        );

        FontRenderers.info(msSemi, 16).drawString(
                poseStack,
                text,
                (int)(pos.x - textWidth / 2),
                (int)(pos.y + 16),
                currentTheme.colors[1]
        );
    }
}
