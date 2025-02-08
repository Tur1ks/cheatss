package foby.client.module.modules.combat;

import foby.client.event.EventHandler;
import foby.client.event.events.impl.TickEvent;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.ui.clickgui.setting.settings.BooleanSetting;
import foby.client.utils.Singleton;
import foby.client.utils.math.MathUtil;
import foby.client.utils.math.MathUtils;
import foby.client.utils.math.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@ModuleAnnotation(name = "Aura", desc = "Автоматически атакует", category = Category.COMBAT)
public class Aura extends Module {
    public static final Singleton<Aura> singleton = Singleton.create(() -> Module.link(Aura.class));
    private final TimerUtil attackTimer = new TimerUtil();
    private LivingEntity target;
    @EventHandler
    public void onUpdate(TickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || player.isDeadOrDying()) {
            return;
        }

        target = findTarget();
        //System.out.println("onUpdate - Found target: " + (target != null ? target.getName().getString() : "null"));

        if (target != null && isInRange(player, target)) {
            updateRotation(true, 70, 30);

            if (shouldPlayerFalling() && attackTimer.isReached(500)) {
                attackTarget(player, target);
            }
        }
    }



    private boolean shouldPlayerFalling() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player.fallDistance > 0 && !player.isInWater() && !player.isInLava() && !player.isFallFlying() && !player.onGround();
    }

    public LivingEntity findTarget() {
        List<Entity> potentialTargets = new ArrayList<>();
        for (Entity entity : Minecraft.getInstance().level.entitiesForRendering()) {
            if (entity instanceof LivingEntity livingEntity && isValidTarget(livingEntity) && entity != Minecraft.getInstance().player) {
                potentialTargets.add(entity);
            }
        }

        if (potentialTargets.isEmpty()) {
            return null;
        }

        if (true && target != null && isValidTarget(target)) {
            return target;
        }

        return (LivingEntity) potentialTargets.stream()
                .min(Comparator.comparingDouble(Minecraft.getInstance().player::distanceTo))
                .orElse(null);
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (!true && !false && !false && !false && !false && !false) {
            return false;
        }

        if (entity.getScoreboardName().length() < 2)
            return false;
        if (entity.isDeadOrDying() || entity.getHealth() <= 0.0f)
            return false;
        if (entity instanceof Player) {
            if (entity.getArmorValue() == 0 && !true) return false;
            if (!entity.getUUID().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + entity.getScoreboardName()).getBytes(StandardCharsets.UTF_8))) && !true) return false;
        }

        return true;
    }


    public LivingEntity getCurrentTarget() {
        LivingEntity currentTarget = findTarget();
        System.out.println("getCurrentTarget called - Current target: " + (currentTarget != null ? currentTarget.getName().getString() : "null"));
        return currentTarget;
    }

    private boolean isInRange(LocalPlayer player, Entity target) {
        return player.distanceTo(target) <= 3;
    }

    private void attackTarget(LocalPlayer player, Entity target) {
        boolean SprintStop = false;

        if (true && player.isSprinting()) {
            player.setSprinting(false);
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));
            SprintStop = true;
        }

        Vec3 targetPosition = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.9, target.getZ());
        player.attack(target);
        Minecraft.getInstance().gameMode.attack(player, target);
        player.swing(InteractionHand.MAIN_HAND);

        if (SprintStop) {
            player.setSprinting(true);
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_SPRINTING));
        }

        attackTimer.reset();
    }


    private void updateRotation(boolean attack, float rotationYawSpeed, float rotationPitchSpeed) {
        Vec3 targetPosition = target.getPosition(mc.getFrameTimeNs());
        Vec3 playerPosition = mc.player.getEyePosition(mc.getFps());

        Vec3 vec3 = targetPosition.subtract(playerPosition).normalize();

        float yToTarget = (float) Math.toDegrees(Math.atan2(vec3.x, vec3.z));
        float xToTarget = (float) Math.toDegrees(Math.asin(vec3.y));

        double d4 = this.mc.options.sensitivity().get() * 0.6F + 0.2F;
        float d5 = (float) (d4 * d4 * d4);

        float yaw = (yToTarget - mc.player.yRotO) * d5;
        float pitch = (xToTarget - mc.player.xRotO) * d5;

        yaw = MathUtils.clamp(yaw, -rotationYawSpeed, rotationYawSpeed);
        pitch = MathUtils.clamp(pitch, -rotationPitchSpeed, rotationPitchSpeed);

        mc.player.setYRot(mc.player.getYRot() + yaw);
        mc.player.setXRot(mc.player.getXRot() + pitch);
        mc.player.setYHeadRot(mc.player.getYRot());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        target = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        attackTimer.reset();
        target = null;
    }
}

