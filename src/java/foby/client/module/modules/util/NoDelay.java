package foby.client.module.modules.util;

import foby.client.event.EventHandler;
import foby.client.event.events.impl.TickEvent;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;

@ModuleAnnotation(name = "NoJumpDelay", desc = "Убирает задержку в прыжке!", category = Category.MOVEMENT)
public class NoDelay extends Module {
    @EventHandler
    public void onUpdate(TickEvent event) {
        if (mc.level == null || mc.player == null) return;
        mc.player.noJumpDelay = 0;
    }
}
