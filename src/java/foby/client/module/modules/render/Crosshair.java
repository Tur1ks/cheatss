package foby.client.module.modules.render;

import foby.client.event.EventHandler;
import foby.client.event.events.impl.RenderEvent2D;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.utils.render.DrawHelper;

import java.awt.*;

@ModuleAnnotation(name = "Crosshair", desc = "Ресует лучший прицел!", category = Category.RENDER)
public class Crosshair extends Module {
    @EventHandler
    public void onRender(RenderEvent2D event) {
        float one_x = sr.getWidth() / 4;
        float one_y = sr.getHeight() / 4;
        DrawHelper.drawRect(event.getGuiGraphics(), one_x - 1, one_y + 1, 0.5f, 2, new Color(255,255,255,255));
        DrawHelper.drawRect(event.getGuiGraphics(), one_x - 1, one_y - 2.5f, 0.5f, 2, new Color(255,255,255,255));

        DrawHelper.drawRect(event.getGuiGraphics(), one_x, one_y, 2, 0.5f, new Color(255,255,255,255));
        DrawHelper.drawRect(event.getGuiGraphics(), one_x - 3.5f, one_y, 2, 0.5f, new Color(255,255,255,255));
    }
}
