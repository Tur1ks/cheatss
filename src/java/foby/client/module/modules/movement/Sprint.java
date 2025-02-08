package foby.client.module.modules.movement;

import foby.client.event.EventHandler;
import foby.client.event.events.impl.TickEvent;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;

@ModuleAnnotation(name = "Sprint", desc = "Нажимает спринт вместо вас!", category = Category.MOVEMENT)
public class Sprint extends Module {
    @EventHandler
    public void onUpdate(TickEvent event) {
        if (mc.level == null || mc.player == null) return;
        mc.player.setSprinting(
                mc.player.getFoodData().getFoodLevel() > 6
                        && !mc.player.horizontalCollision
                        && mc.player.input.forwardImpulse > 0
                        && (!mc.player.isShiftKeyDown() || (false))
                        && (!mc.player.isUsingItem() || !true)
        );
    }
}
