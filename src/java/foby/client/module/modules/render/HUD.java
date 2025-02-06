package foby.client.module.modules.render;

import foby.client.event.EventHandler;
import foby.client.event.events.impl.RenderEvent2D;
import foby.client.module.Module;
import foby.client.module.modules.ModuleManager;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.ui.clickgui.setting.settings.BooleanSetting;
import foby.client.ui.clickgui.setting.settings.ModeSetting;
import foby.client.utils.fonts.FontRenderers;
import foby.client.utils.render.DrawHelper;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static foby.client.utils.fonts.FontRenderers.msSemi;

@ModuleAnnotation(name = "HUD", desc = "Отображение информации на экране", category = Category.RENDER)
public class HUD extends Module {
    BooleanSetting watermark = new BooleanSetting("Watermark", "Отображение ватермарки", true);
    BooleanSetting moduleList = new BooleanSetting("Module List", "Список активных модулей", true);
    BooleanSetting coordinates = new BooleanSetting("Coordinates", "Показывать координаты", true);
    ModeSetting colorMode = new ModeSetting("Color", () -> true, "Rainbow", "Static", "Gradient");

    public HUD() {
        addSettings(watermark, moduleList, coordinates, colorMode);
    }

    @EventHandler
    public void onRender(RenderEvent2D event) {
        GuiGraphics graphics = event.getGuiGraphics();
        if (graphics == null || mc.player == null) return;

        if (watermark.get()) {
            renderWatermark(graphics);
        }

        if (moduleList.get()) {
            renderModuleList(graphics);
        }

        if (coordinates.get()) {
            renderCoordinates(graphics);
        }
    }

    private void renderWatermark(GuiGraphics graphics) {
        String clientName = "Foby Client";
        float watermarkX = 5;
        float watermarkY = 5;
        float width = FontRenderers.info(msSemi, 20).getStringWidth(clientName) + 10;

        DrawHelper.rectangle(
                graphics.pose(),
                watermarkX,
                watermarkY,
                width,
                20,
                4,
                new Color(0, 0, 0, 160).getRGB()
        );

        FontRenderers.info(msSemi, 20).drawString(
                graphics.pose(),
                clientName,
                (int) watermarkX + 5,
                (int) watermarkY + 5,
                getColor()
        );
    }

    private void renderModuleList(GuiGraphics graphics) {
        List<Module> enabledModules = ModuleManager.getModules().stream()
                .filter(Module::isEnabled)
                .sorted(Comparator.comparing(module ->
                        -FontRenderers.info(msSemi, 18).getStringWidth(module.name)))
                .toList();

        float moduleX = mc.getWindow().getGuiScaledWidth() - 5;
        float moduleY = 5;

        for (Module module : enabledModules) {
            String name = module.name;
            float width = FontRenderers.info(msSemi, 18).getStringWidth(name);

            DrawHelper.rectangle(
                    graphics.pose(),
                    moduleX - width - 10,
                    moduleY,
                    width + 8,
                    16,
                    4,
                    new Color(0, 0, 0, 160).getRGB()
            );

            FontRenderers.info(msSemi, 18).drawString(
                    graphics.pose(),
                    name,
                    (int) (moduleX - width - 6),
                    (int) moduleY + 4,
                    getColor()
            );

            moduleY += 18;
        }
    }

    private void renderCoordinates(GuiGraphics graphics) {
        String coords = String.format("X: %.1f Y: %.1f Z: %.1f",
                mc.player.getX(), mc.player.getY(), mc.player.getZ());

        float coordsX = 5;
        float coordsY = mc.getWindow().getGuiScaledHeight() - 25;
        float width = FontRenderers.info(msSemi, 18).getStringWidth(coords) + 10;

        DrawHelper.rectangle(
                graphics.pose(),
                coordsX,
                coordsY,
                width,
                20,
                4,
                new Color(0, 0, 0, 160).getRGB()
        );

        FontRenderers.info(msSemi, 18).drawString(
                graphics.pose(),
                coords,
                (int) coordsX + 5,
                (int) coordsY + 6,
                getColor()
        );
    }

    private int getColor() {
        switch (colorMode.getMode()) {
            case "Rainbow":
                return getRainbowColor();
            case "Gradient":
                return getGradientColor();
            default:
                return Color.WHITE.getRGB();
        }
    }

    private int getRainbowColor() {
        float hue = (System.currentTimeMillis() % 3000) / 3000f;
        return Color.HSBtoRGB(hue, 1, 1);
    }

    private int getGradientColor() {
        long time = System.currentTimeMillis();
        float pos = (time % 2000) / 1000f;
        if (pos > 1) pos = 2 - pos;

        return new Color(
                (int) (pos * 255),
                100,
                255 - (int) (pos * 255)
        ).getRGB();
    }
}
