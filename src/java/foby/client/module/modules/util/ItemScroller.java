package foby.client.module.modules.util;

import foby.client.event.EventHandler;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import org.lwjgl.glfw.GLFW;

import static foby.client.Foby.mc;

@ModuleAnnotation(name = "ItemScroller", desc = "Fast item movement in containers", category = Category.UTIL)
public class ItemScroller extends Module {

    @EventHandler
    public void onTick() {
        if (!(mc.screen instanceof AbstractContainerScreen<?> containerScreen)) return;

        if (mc.player.isShiftKeyDown() && mc.mouseHandler.isLeftPressed()) {
            Slot slot = (Slot) ((AbstractContainerScreen<?>) mc.screen).getFocused();
            if (slot != null && slot.hasItem()) {
                mc.gameMode.handleInventoryMouseClick(
                        containerScreen.getMenu().containerId,
                        slot.index,
                        0,
                        ClickType.QUICK_MOVE,
                        mc.player
                );
            }
        }
    }
}
