package foby.client.module.modules.movement;

import com.mojang.blaze3d.platform.InputConstants;
import foby.client.event.EventHandler;
import foby.client.event.events.impl.TickEvent;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;

@ModuleAnnotation(name = "InventoryMove", desc = "Дает возможность передвигаться в инвенторе!", category = Category.MOVEMENT)
public class Inventory extends Module {
    @EventHandler
    public void onUpdate(TickEvent event) {
        if (mc.screen != null && !(mc.screen instanceof ChatScreen)) {
            for (KeyMapping k : new KeyMapping[]{mc.options.keyUp, mc.options.keyDown, mc.options.keyLeft, mc.options.keyRight, mc.options.keyJump, mc.options.keySprint})
                k.setDown(isKeyPressed(InputConstants.getKey(k.saveString()).getValue()));
        }
    }
    public boolean isKeyPressed(int keyCode) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keyCode);
    }
}
