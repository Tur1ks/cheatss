package foby.client.module.modules.util;

import foby.client.event.EventHandler;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;

import static foby.client.Foby.mc;

@ModuleAnnotation(name = "ItemScroller", desc = "Fast item movement in containers", category = Category.UTIL)
public class ItemScroller extends Module {

    @EventHandler
    public void onTick() {
        if (!(mc.screen instanceof AbstractContainerScreen<?> containerScreen)) return;

        if (mc.player.isShiftKeyDown() && mc.mouseHandler.isLeftPressed()) {
            double mouseX = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
            double mouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

            int guiLeft = (mc.getWindow().getGuiScaledWidth() - 176) / 2;
            int guiTop = (mc.getWindow().getGuiScaledHeight() - 166) / 2;

            Slot hoveredSlot = null;
            for (Slot slot : containerScreen.getMenu().slots) {
                int slotX = guiLeft + slot.x;
                int slotY = guiTop + slot.y;
                if (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
                    hoveredSlot = slot;
                    break;
                }
            }

            if (hoveredSlot != null && hoveredSlot.hasItem()) {
                mc.gameMode.handleInventoryMouseClick(
                        containerScreen.getMenu().containerId,
                        hoveredSlot.index,
                        0,
                        ClickType.QUICK_MOVE,
                        mc.player
                );
            }
        }
    }
}
