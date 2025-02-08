package foby.client.module.modules.render;

import foby.client.event.EventHandler;
import foby.client.event.events.impl.RenderEvent2D;
import foby.client.module.Module;
import foby.client.module.modules.ModuleManager;
import foby.client.module.modules.combat.Aura;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.themes.Theme;
import foby.client.ui.clickgui.setting.settings.BooleanSetting;
import foby.client.utils.fonts.FontRenderers;
import foby.client.utils.render.DrawHelper;
import foby.client.utils.render.DraggableComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static foby.client.Foby.themesUtil;
import static foby.client.utils.fonts.FontRenderers.msSemi;

@ModuleAnnotation(name = "HUD", desc = "Отображение информации на экране", category = Category.RENDER)
public class HUD extends Module {
    private final Map<String, DraggableComponent> components = new HashMap<>();
    private boolean isDragging = false;
    private DraggableComponent currentDragging = null;
    private final Map<Module, Float> keyBindAnimations = new HashMap<>();
    private final Map<Module, Long> lastToggleTime = new HashMap<>();
    private final Map<Module, Float> exitAnimations = new HashMap<>();
    private final List<Module> removedModules = new ArrayList<>();

    BooleanSetting watermark = new BooleanSetting("Watermark", "Отображение ватермарки", true);
    BooleanSetting moduleList = new BooleanSetting("Module List", "Список активных модулей", true);
    BooleanSetting coordinates = new BooleanSetting("Coordinates", "Показывать координаты", true);
    BooleanSetting keybinds = new BooleanSetting("Keybinds", "Показывать кейбинды", true);
    BooleanSetting potionEffects = new BooleanSetting("Potion Effects", "Показывать эффекты зелий", true);
    BooleanSetting staffList = new BooleanSetting("Staff List", "Показывать список админов", true);
    BooleanSetting targetHud = new BooleanSetting("Target HUD", "Показывать информацию о цели", true);

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public HUD() {
        addSettings(watermark, moduleList, coordinates, keybinds, potionEffects, staffList, targetHud);

        components.put("watermark", new DraggableComponent(5, 5));
        components.put("moduleList", new DraggableComponent(mc.getWindow().getGuiScaledWidth() - 5, 5));
        components.put("coordinates", new DraggableComponent(5, mc.getWindow().getGuiScaledHeight() - 25));
        components.put("keybinds", new DraggableComponent(5, 30));
        components.put("potions", new DraggableComponent(5, mc.getWindow().getGuiScaledHeight() - 45));
        components.put("staff", new DraggableComponent(mc.getWindow().getGuiScaledWidth() - 5, mc.getWindow().getGuiScaledHeight() / 2f));
        components.put("targetHud", new DraggableComponent(mc.getWindow().getGuiScaledWidth() / 2f - 10, mc.getWindow().getGuiScaledHeight() / 2f - 10));
    }

    @EventHandler
    public void onRender(RenderEvent2D event) {
        GuiGraphics graphics = event.getGuiGraphics();
        if (graphics == null || mc.player == null) return;

        handleDragging();

        if (watermark.get()) renderWatermark(graphics, components.get("watermark"));
        if (moduleList.get()) renderModuleList(graphics, components.get("moduleList"));
        if (coordinates.get()) renderCoordinates(graphics, components.get("coordinates"));
        if (keybinds.get()) renderKeybinds(graphics, components.get("keybinds"));
        if (potionEffects.get()) renderPotionEffects(graphics, components.get("potions"));
        if (staffList.get()) renderStaffList(graphics, components.get("staff"));
        if (targetHud.get()) renderTargetHUD(graphics, components.get("targetHud"));
    }

    private void handleDragging() {
        double mouseX = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getWidth();
        double mouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getHeight();

        if (GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            if (!isDragging) {
                for (DraggableComponent component : components.values()) {
                    if (isInDraggableArea(mouseX, mouseY, component)) {
                        isDragging = true;
                        currentDragging = component;
                        component.startDragging((int) mouseX, (int) mouseY);
                        break;
                    }
                }
            } else if (currentDragging != null) {
                currentDragging.updateDragPosition((float) mouseX, (float) mouseY);
            }
        } else {
            isDragging = false;
            currentDragging = null;
        }
    }

    private boolean isInDraggableArea(double mouseX, double mouseY, DraggableComponent component) {
        float expandedArea = 5f;
        return mouseX >= (component.getX() - expandedArea) &&
                mouseX <= (component.getX() + component.getWidth() + expandedArea) &&
                mouseY >= (component.getY() - expandedArea) &&
                mouseY <= (component.getY() + component.getHeight() + expandedArea);
    }

    private void renderWatermark(GuiGraphics graphics, DraggableComponent pos) {
        String time = LocalTime.now().format(timeFormatter);
        String clientName = "SkyFlow Client | " + time;
        float width = FontRenderers.info(msSemi, 20).getStringWidth(clientName) + 10;
        Theme currentTheme = themesUtil.getCurrentStyle();

        DrawHelper.drawShadow(
                graphics.pose(),
                pos.getX(),
                pos.getY(),
                width,
                20,
                4,
                10f,
                8,
                currentTheme.colors[3]
        );

        FontRenderers.info(msSemi, 20).drawString(
                graphics.pose(),
                clientName,
                (int) pos.getX() + 5,
                (int) pos.getY() + 5,
                currentTheme.colors[1]
        );

        pos.setWidth(width);
        pos.setHeight(20);
    }

    private void renderModuleList(GuiGraphics graphics, DraggableComponent pos) {
        List<Module> enabledModules = ModuleManager.getModules().stream()
                .filter(Module::isEnabled)
                .sorted(Comparator.comparing(module ->
                        -FontRenderers.info(msSemi, 18).getStringWidth(module.name)))
                .toList();

        float currentY = pos.getY();
        float maxWidth = 0;
        Theme currentTheme = themesUtil.getCurrentStyle();

        for (Module module : enabledModules) {
            String name = module.name;
            float width = FontRenderers.info(msSemi, 18).getStringWidth(name);
            maxWidth = Math.max(maxWidth, width);

            DrawHelper.drawShadow(
                    graphics.pose(),
                    pos.getX() - width - 10,
                    currentY,
                    width + 8,
                    16,
                    4,
                    10f,
                    8,
                    currentTheme.colors[3]
            );

            FontRenderers.info(msSemi, 18).drawString(
                    graphics.pose(),
                    name,
                    (int) (pos.getX() - width - 6),
                    (int) currentY + 4,
                    currentTheme.colors[1]
            );

            currentY += 18;
        }

        pos.setWidth(maxWidth + 10);
        pos.setHeight(currentY - pos.getY());
    }

    private void renderCoordinates(GuiGraphics graphics, DraggableComponent pos) {
        String coords = String.format("X: %.1f Y: %.1f Z: %.1f",
                mc.player.getX(), mc.player.getY(), mc.player.getZ());

        float width = FontRenderers.info(msSemi, 18).getStringWidth(coords) + 10;
        Theme currentTheme = themesUtil.getCurrentStyle();

        DrawHelper.drawShadow(
                graphics.pose(),
                pos.getX(),
                pos.getY(),
                width,
                20,
                4,
                10f,
                8,
                currentTheme.colors[3]
        );

        FontRenderers.info(msSemi, 18).drawString(
                graphics.pose(),
                coords,
                (int) pos.getX() + 5,
                (int) pos.getY() + 6,
                currentTheme.colors[1]
        );

        pos.setWidth(width);
        pos.setHeight(20);
    }

    private void renderKeybinds(GuiGraphics graphics, DraggableComponent pos) {
        List<Module> boundModules = ModuleManager.getModules().stream()
                .filter(module -> module.getKey() != GLFW.GLFW_KEY_UNKNOWN && (module.isEnabled() || exitAnimations.containsKey(module)))
                .sorted(Comparator.comparing(module -> -FontRenderers.info(msSemi, 18)
                        .getStringWidth(module.name + " [" + GLFW.glfwGetKeyName(module.getKey(), 0).toUpperCase() + "]")))
                .toList();

        ModuleManager.getModules().stream()
                .filter(module -> module.getKey() != GLFW.GLFW_KEY_UNKNOWN)
                .forEach(module -> {
                    if (!keyBindAnimations.containsKey(module)) {
                        keyBindAnimations.put(module, 0f);
                    }

                    float animation = keyBindAnimations.get(module);
                    boolean enabled = module.isEnabled();

                    if (enabled && animation < 1) {
                        animation = Math.min(1, animation + 0.1f);
                        lastToggleTime.put(module, System.currentTimeMillis());
                        if (exitAnimations.containsKey(module)) {
                            exitAnimations.remove(module);
                        }
                    } else if (!enabled && animation > 0) {
                        animation = Math.max(0, animation - 0.1f);
                        if (animation == 0 && !exitAnimations.containsKey(module)) {
                            exitAnimations.put(module, 1f);
                            lastToggleTime.put(module, System.currentTimeMillis());
                        }
                    }

                    keyBindAnimations.put(module, animation);
                });

        removedModules.clear();
        exitAnimations.forEach((module, animation) -> {
            float newAnimation = Math.max(0, animation - 0.1f);
            exitAnimations.put(module, newAnimation);
            if (newAnimation == 0) {
                removedModules.add(module);
            }
        });
        removedModules.forEach(exitAnimations::remove);

        float currentY = pos.getY();
        Theme currentTheme = themesUtil.getCurrentStyle();

        String header = "Keybinds: " + boundModules.stream().filter(Module::isEnabled).count();
        float headerWidth = FontRenderers.info(msSemi, 18).getStringWidth(header) + 10;
        float maxWidth = headerWidth;

        DrawHelper.drawShadow(
                graphics.pose(),
                pos.getX(),
                currentY,
                headerWidth,
                16,
                4,
                10f,
                8,
                currentTheme.colors[3]
        );

        FontRenderers.info(msSemi, 18).drawString(
                graphics.pose(),
                header,
                (int) pos.getX() + 5,
                (int) currentY + 4,
                currentTheme.colors[1]
        );

        currentY += 18;

        for (Module module : boundModules) {
            String keyName = GLFW.glfwGetKeyName(module.getKey(), 0);
            if (keyName == null) continue;

            float animation = keyBindAnimations.get(module);
            float exitAnimation = exitAnimations.getOrDefault(module, 1f);

            String text = module.name + " [" + keyName.toUpperCase() + "]";
            float width = FontRenderers.info(msSemi, 18).getStringWidth(text) + 10;
            maxWidth = Math.max(maxWidth, width);

            float slideOffset = (1 - animation) * 20;
            float exitOffset = (1 - exitAnimation) * 20;
            int textColor = interpolateColors(0x00FFFFFF, currentTheme.colors[1], animation * exitAnimation);

            long timeSinceToggle = System.currentTimeMillis() - lastToggleTime.getOrDefault(module, 0L);
            float pulseScale = 1 + (float) Math.max(0, Math.sin(timeSinceToggle * 0.01) * 0.1 * Math.exp(-timeSinceToggle * 0.003));

            DrawHelper.drawShadow(
                    graphics.pose(),
                    pos.getX() + slideOffset - exitOffset,
                    currentY,
                    width * pulseScale,
                    16,
                    4,
                    10f * animation * exitAnimation,
                    8,
                    currentTheme.colors[3]
            );

            FontRenderers.info(msSemi, 18).drawString(
                    graphics.pose(),
                    text,
                    (int) (pos.getX() + 5 + slideOffset - exitOffset),
                    (int) currentY + 4,
                    textColor
            );

            currentY += 18;
        }

        pos.setWidth(maxWidth);
        pos.setHeight(boundModules.isEmpty() ? 18 : currentY - pos.getY());
    }

    private void renderPotionEffects(GuiGraphics graphics, DraggableComponent pos) {
        List<MobEffectInstance> effects = mc.player.getActiveEffects().stream().toList();
        if (effects.isEmpty()) return;

        float currentY = pos.getY();
        float maxWidth = 0;
        Theme currentTheme = themesUtil.getCurrentStyle();

        String header = "Active Effects: " + effects.size();
        float headerWidth = FontRenderers.info(msSemi, 18).getStringWidth(header) + 10;
        maxWidth = Math.max(maxWidth, headerWidth);

        DrawHelper.drawShadow(
                graphics.pose(),
                pos.getX(),
                currentY,
                headerWidth,
                16,
                4,
                10f,
                8,
                currentTheme.colors[3]
        );

        FontRenderers.info(msSemi, 18).drawString(
                graphics.pose(),
                header,
                (int) pos.getX() + 5,
                (int) currentY + 4,
                currentTheme.colors[1]
        );

        currentY += 18;

        for (MobEffectInstance effect : effects) {
            String text = effect.getEffect().getRegisteredName() + " " +
                    (effect.getAmplifier() + 1) + " (" +
                    effect.getDuration() / 20 + "s)";

            float width = FontRenderers.info(msSemi, 18).getStringWidth(text) + 10;
            maxWidth = Math.max(maxWidth, width);

            DrawHelper.drawShadow(
                    graphics.pose(),
                    pos.getX(),
                    currentY,
                    width,
                    16,
                    4,
                    10f,
                    8,
                    currentTheme.colors[3]
            );

            FontRenderers.info(msSemi, 18).drawString(
                    graphics.pose(),
                    text,
                    (int) pos.getX() + 5,
                    (int) currentY + 4,
                    currentTheme.colors[1]
            );

            currentY += 18;
        }

        pos.setWidth(maxWidth);
        pos.setHeight(currentY - pos.getY());
    }

    private void renderStaffList(GuiGraphics graphics, DraggableComponent pos) {
        List<String> staffPlayers = mc.level.players().stream()
                .filter(player -> player.hasPermissions(2))
                .map(player -> player.getGameProfile().getName())
                .sorted()
                .toList();

        if (staffPlayers.isEmpty()) return;

        float currentY = pos.getY();
        float maxWidth = 0;
        Theme currentTheme = themesUtil.getCurrentStyle();

        String header = "Staff Online: " + staffPlayers.size();
        float headerWidth = FontRenderers.info(msSemi, 18).getStringWidth(header) + 10;
        maxWidth = Math.max(maxWidth, headerWidth);

        DrawHelper.drawShadow(
                graphics.pose(),
                pos.getX() - headerWidth,
                currentY,
                headerWidth,
                16,
                4,
                10f,
                8,
                currentTheme.colors[3]
        );

        FontRenderers.info(msSemi, 18).drawString(
                graphics.pose(),
                header,
                (int) (pos.getX() - headerWidth + 5),
                (int) currentY + 4,
                currentTheme.colors[1]
        );

        currentY += 18;

        for (String staffName : staffPlayers) {
            float width = FontRenderers.info(msSemi, 18).getStringWidth(staffName) + 10;
            maxWidth = Math.max(maxWidth, width);

            DrawHelper.drawShadow(
                    graphics.pose(),
                    pos.getX() - width,
                    currentY,
                    width,
                    16,
                    4,
                    10f,
                    8,
                    currentTheme.colors[3]
            );

            FontRenderers.info(msSemi, 18).drawString(
                    graphics.pose(),
                    staffName,
                    (int) (pos.getX() - width + 5),
                    (int) currentY + 4,
                    currentTheme.colors[1]
            );

            currentY += 18;
        }

        pos.setWidth(maxWidth);
        pos.setHeight(currentY - pos.getY());
    }

    private void renderTargetHUD(GuiGraphics graphics, DraggableComponent pos) {
        Aura aura = Aura.singleton.get();
        LivingEntity target = aura.findTarget();
        System.out.println(target);

        float width = 120;
        float height = 45;
        Theme currentTheme = themesUtil.getCurrentStyle();

        // Background
        DrawHelper.drawRect(
                graphics,
                pos.getX(),
                pos.getY(),
                width,
                height,
                new Color(0, 0, 0, 160)
        );


        pos.setWidth(width);
        pos.setHeight(height);
    }

    private int interpolateColors(int color1, int color2, float factor) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a1 = (color1 >> 24) & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF;

        int r = (int) (r1 + factor * (r2 - r1));
        int g = (int) (g1 + factor * (g2 - g1));
        int b = (int) (b1 + factor * (b2 - b1));
        int a = (int) (a1 + factor * (a2 - a1));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
