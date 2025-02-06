package foby.client.module.modules.render;

import foby.client.event.EventHandler;
import foby.client.event.events.impl.TickEvent;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.utils.ClientUtil;
import foby.client.utils.color.ColorUtils;

@ModuleAnnotation(name = "Test", desc = "test", category = Category.RENDER)
public class Test extends Module {

    @EventHandler
    public void tick(TickEvent e) {
        if(mc.player.fallDistance > 0.1) {
            ClientUtil.sendMessage("test");
        }
    }
}
