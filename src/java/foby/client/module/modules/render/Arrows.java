package foby.client.module.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import foby.client.event.EventHandler;
import foby.client.event.events.impl.RenderEvent2D;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.ui.clickgui.setting.settings.NumberSetting;
import foby.client.ui.clickgui.setting.settings.BooleanSetting;
import foby.client.utils.render.DrawHelper;
import foby.client.utils.fonts.FontRenderers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.stream.Collectors;

import static foby.client.Foby.themesUtil;
import static foby.client.utils.fonts.FontRenderers.msSemi;

@ModuleAnnotation(name = "Arrows", desc = "Shows arrows pointing to players", category = Category.RENDER)
public class Arrows extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", "Distance from center", 60, 30, 100, 1);
    private final NumberSetting size = new NumberSetting("Size", "Arrow size", 10, 5, 20, 1);
    private final NumberSetting maxDistance = new NumberSetting("Max Distance", "Maximum tracking distance", 100, 10, 200, 5);
    private final BooleanSetting showDistance = new BooleanSetting("Show Distance", "Display distance to players", true);

    public Arrows() {
        addSettings(radius, size, maxDistance, showDistance);
    }

    @EventHandler
    public void onRender(RenderEvent2D event) {
        if (mc.player == null) return;
        GuiGraphics graphics = event.getGuiGraphics();

        List<Player> players = mc.level.players().stream()
                .filter(player -> player != mc.player)
                .filter(Player::isAlive)
                .filter(player -> mc.player.distanceTo(player) <= maxDistance.getFloatValue())
                .collect(Collectors.toList());

        int middleX = mc.getWindow().getGuiScaledWidth() / 2;
        int middleY = mc.getWindow().getGuiScaledHeight() / 2;

        for (Player player : players) {
            Vec3 pos = player.position().subtract(mc.player.position());
            double angle = Math.toDegrees(Math.atan2(pos.x, pos.z));
            angle = Mth.wrapDegrees(angle - mc.player.getYRot());

            double rad = Math.toRadians(-angle);
            double x = middleX + radius.getFloatValue() * Math.sin(rad);
            double y = middleY - radius.getFloatValue() * Math.cos(rad);

            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(x, y, 0);

            float rotationAngle = (float) Math.toRadians(-angle);
            Matrix4f rotationMatrix = new Matrix4f().rotate(rotationAngle, 0, 0, 1);
            poseStack.last().pose().mul(rotationMatrix);

            DrawHelper.drawShadowedTriangle(
                    poseStack,
                    0, -size.getFloatValue() + 5,
                    size.getFloatValue() / 2, size.getFloatValue() / 2,
                    -size.getFloatValue() / 2, size.getFloatValue() / 2,
                    5f,
                    8,
                    getPlayerColor(player)
            );

            poseStack.popPose();

            if (showDistance.get()) {
                String distance = String.format("%.1f", mc.player.distanceTo(player));
                poseStack.pushPose();
                FontRenderers.info(msSemi, 18).drawString(
                        poseStack,
                        distance,
                        (float) x - FontRenderers.info(msSemi, 18).getStringWidth(distance) / 2,
                        (float) y + size.getFloatValue() + 2,
                        themesUtil.getCurrentStyle().colors[1]
                );
                poseStack.popPose();
            }
        }
    }

    private int getPlayerColor(Player player) {
        if (mc.player.isAlliedTo(player)) {
            return themesUtil.getCurrentStyle().colors[2];
        }
        return themesUtil.getCurrentStyle().colors[3];
    }
}
