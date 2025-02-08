package foby.client.ui.clickgui;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import foby.client.module.Module;
import foby.client.module.modules.ModuleManager;
import foby.client.module.utils.Category;
import foby.client.themes.Theme;
import foby.client.themes.ThemesUtil;
import foby.client.ui.clickgui.setting.Setting;
import foby.client.ui.clickgui.setting.settings.BooleanSetting;
import foby.client.ui.clickgui.setting.settings.ListSetting;
import foby.client.ui.clickgui.setting.settings.ModeSetting;
import foby.client.ui.clickgui.setting.settings.NumberSetting;
import foby.client.utils.fonts.FontRenderers;
import foby.client.utils.render.DrawHelper;
import foby.client.utils.render.Shader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import static foby.client.Foby.mc;
import static foby.client.Foby.themesUtil;
import static foby.client.utils.fonts.FontRenderers.msSemi;

public class ClickGUI extends Screen {
    private static final Component GAME = Component.translatable("menu.game");
    private static final Component PAUSED = Component.translatable("menu.paused");
    private boolean blur = true;
    private List<Category> categories = List.of(Category.values());
    private float targetSettingsHeight;
    private float currentSettingsHeight;
    private boolean settingOpen = false;
    private boolean isThemesOpen = false;
    private Module selectedModule = null;
    private Map<Category, Float> currentHeights = new HashMap<>();
    private Map<Category, Float> targetHeights = new HashMap<>();
    private Map<Category, Float> currentSettingsHeights = new HashMap<>();
    private Map<Category, Float> targetSettingsHeights = new HashMap<>();
    private Map<Category, Boolean> isTransitioning = new HashMap<>();
    private Module bindingModule = null;
    private boolean isBinding = false;


    public ClickGUI(boolean isFullMenu) {
        super(isFullMenu ? GAME : PAUSED);
        themesUtil = new ThemesUtil();
        themesUtil.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int screenWidth = mc.getWindow().getScreenWidth();
        int screenHeight = mc.getWindow().getScreenHeight();

        if (blur) {super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);}


        // Рендер категорий
        int spacing = 500;
        int xOffset = spacing;
        List<Module> allModules = getAllModules();

        for (Category category : categories) {
            List<Module> categoryModules = getModulesForCategory(allModules, category);
            int rectWidth = 100;
            int rectHeight = 20;
            float rectX = xOffset / 2f;
            float rectY = (screenHeight - rectHeight) / 14f;

            int textWidth = (int) FontRenderers.info(msSemi, 20).getStringWidth(category.name());
            float textX = rectX + (rectWidth - textWidth) / 2f;
            float textY = rectY + (rectHeight - 10) / 2f;

            DrawHelper.rectangle(pGuiGraphics.pose(), rectX, rectY, rectWidth, rectHeight, 4, new Color(0x99121212, true).getRGB());

            FontRenderers.info(msSemi, 20).drawString(pGuiGraphics.pose(), category.name(), (int) textX, (int) textY, Color.WHITE.getRGB());

            renderModules(pGuiGraphics, pMouseX, pMouseY, categoryModules, rectX, rectY, rectWidth, rectHeight);

            xOffset += spacing - 260;
        }

        // Кнопка тем в левом верхнем углу
        float themeButtonX = 10;
        float themeButtonY = 10;
        int buttonWidth = 100;
        int buttonHeight = 20;

        DrawHelper.rectangle(
                pGuiGraphics.pose(),
                themeButtonX,
                themeButtonY,
                buttonWidth,
                buttonHeight,
                4,
                new Color(23, 25, 23, 168).getRGB()
        );

        String themesText = "Темы";
        float themesTextWidth = FontRenderers.info(msSemi, 20).getStringWidth(themesText);
        FontRenderers.info(msSemi, 20).drawString(
                pGuiGraphics.pose(),
                themesText,
                (int)(themeButtonX + (buttonWidth - themesTextWidth) / 2),
                (int)(themeButtonY + 5),
                Color.WHITE.getRGB()
        );

        // Выпадающий список тем
        if (isThemesOpen && !themesUtil.themes.isEmpty()) {
            float listY = themeButtonY + buttonHeight + 5;
            float listHeight = themesUtil.themes.size() * 25;

            DrawHelper.rectangle(
                    pGuiGraphics.pose(),
                    themeButtonX,
                    listY,
                    buttonWidth,
                    listHeight,
                    4,
                    new Color(0x99121212, true).getRGB()
            );

            for (int i = 0; i < themesUtil.themes.size(); i++) {
                Theme theme = themesUtil.themes.get(i);
                float itemY = listY + (i * 25);

                if (pMouseX >= themeButtonX && pMouseX <= themeButtonX + buttonWidth &&
                        pMouseY >= itemY && pMouseY <= itemY + 25) {
                    DrawHelper.rectangle(
                            pGuiGraphics.pose(),
                            themeButtonX,
                            itemY,
                            buttonWidth,
                            25,
                            4,
                            theme.getColorLowSpeed(1)
                    );
                }

                FontRenderers.info(msSemi, 18).drawString(
                        pGuiGraphics.pose(),
                        theme.getName(),
                        (int)themeButtonX + 10,
                        (int)itemY + 8,
                        theme == themesUtil.getCurrentStyle() ? Color.WHITE.getRGB() : Color.GRAY.getRGB()
                );
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float themeButtonX = 10;
        float themeButtonY = 10;
        int buttonWidth = 100;
        int buttonHeight = 20;

        // Клик по кнопке тем
        if (mouseX >= themeButtonX && mouseX <= themeButtonX + buttonWidth &&
                mouseY >= themeButtonY && mouseY <= themeButtonY + buttonHeight) {
            isThemesOpen = !isThemesOpen;
            return true;
        }

        // Клик по списку тем
        if (isThemesOpen) {
            float listY = themeButtonY + buttonHeight + 5;

            if (mouseX >= themeButtonX && mouseX <= themeButtonX + buttonWidth &&
                    mouseY >= listY && mouseY <= listY + (themesUtil.themes.size() * 25)) {

                int index = (int)((mouseY - listY) / 25);
                if (index >= 0 && index < themesUtil.themes.size()) {
                    themesUtil.setCurrentStyle(themesUtil.themes.get(index));
                    return true;
                }
            }
        }

        return handleRegularCategories(mouseX, mouseY, button);
    }

    private void renderKeyBind(GuiGraphics pGuiGraphics, Module module, float rectX, float currentModuleY, int rectWidth, float heightProgress) {
        String bindText = isBinding && module == bindingModule ?
                "Press a key..." :
                "Bind: " + (module.getKey() == -1 ? "None" : module.getKey());

        float bindTextWidth = FontRenderers.info(msSemi, 14).getStringWidth(bindText);
        float bindTextX = rectX + rectWidth - bindTextWidth - 10;

        FontRenderers.info(msSemi, 14).drawString(
                pGuiGraphics.pose(),
                bindText,
                (int) bindTextX,
                (int) (currentModuleY + 4 * heightProgress),
                Color.GRAY.getRGB()
        );
    }

    private void renderModules(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, List<Module> categoryModules, float rectX, float rectY, int rectWidth, int rectHeight) {
        if (!categoryModules.isEmpty()) {
            Category category = categoryModules.get(0).category;

            currentHeights.putIfAbsent(category, 0f);
            targetHeights.putIfAbsent(category, 0f);
            currentSettingsHeights.putIfAbsent(category, 0f);
            targetSettingsHeights.putIfAbsent(category, 0f);
            isTransitioning.putIfAbsent(category, false);

            float baseHeight = categoryModules.size() * 17 + 4;
            float settingsHeight = 0;

            if (settingOpen && selectedModule != null && selectedModule.category == category) {
                settingsHeight = calculateSettingsHeight(selectedModule);
                targetSettingsHeights.put(category, settingsHeight);
            } else {
                targetSettingsHeights.put(category, 0f);
            }

            float currentSettingsHeight = currentSettingsHeights.get(category);
            currentSettingsHeight += (targetSettingsHeights.get(category) - currentSettingsHeight) * 0.1f;
            currentSettingsHeights.put(category, currentSettingsHeight);

            float targetHeight = baseHeight + currentSettingsHeight;
            targetHeights.put(category, targetHeight);

            float currentHeight = currentHeights.get(category);
            currentHeight += (targetHeight - currentHeight) * 0.1f;
            currentHeights.put(category, currentHeight);

            float moduleRectY = rectY + rectHeight + 5;
            DrawHelper.rectangle(pGuiGraphics.pose(), rectX, moduleRectY, rectWidth, currentHeight, 4, new Color(0x99121212, true).getRGB());

            float currentModuleY = moduleRectY + 5;
            float heightProgress = currentHeight / targetHeight;

            for (Module module : categoryModules) {
                renderModule(pGuiGraphics, pMouseX, pMouseY, module, rectX, currentModuleY, rectWidth, heightProgress);

                if (settingOpen && module == selectedModule) {
                    currentModuleY = renderSettings(pGuiGraphics, pMouseX, pMouseY, module, rectX, currentModuleY, rectWidth, heightProgress);
                } else {
                    currentModuleY += 17 * heightProgress;
                }
            }

        }
    }

    private void renderModule(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, Module module, float rectX, float currentModuleY, int rectWidth, float heightProgress) {
        String moduleName = module.name;
        int moduleTextWidth = (int) FontRenderers.info(msSemi, 15).getStringWidth(moduleName);
        float moduleTextX = rectX + 10;

        // Background hover effect
        if (pMouseX >= rectX && pMouseX <= rectX + rectWidth &&
                pMouseY >= currentModuleY && pMouseY <= currentModuleY + 15 * heightProgress) {
            DrawHelper.rectangle(pGuiGraphics.pose(), rectX + 5, currentModuleY, rectWidth - 10, 15 * heightProgress, 4, new Color(0x40FFFFFF, true).getRGB());

            // Show description tooltip
            int descWidth = (int) FontRenderers.info(msSemi, 14).getStringWidth(module.desc);
            DrawHelper.rectangle(pGuiGraphics.pose(), 450, 28, descWidth + 10, 10, 4, new Color(0x3D3D3D).getRGB());
            FontRenderers.info(msSemi, 14).drawString(pGuiGraphics.pose(), module.desc, 455, 30, new Color(0x80FFFFFF, true).getRGB());
        }

        // Module name
        FontRenderers.info(msSemi, 15).drawString(
                pGuiGraphics.pose(),
                moduleName,
                (int) moduleTextX,
                (int) (currentModuleY + 4 * heightProgress),
                module.isEnabled() ? Color.WHITE.getRGB() : Color.GRAY.getRGB()
        );

        // Key bind display
        String bindText = isBinding && module == bindingModule ?
                "Press a key..." :
                "[" + (module.getKey() == -1 ? "None" : InputConstants.getKey(module.getKey(), -1).getDisplayName().getString()) + "]";

        float bindTextWidth = FontRenderers.info(msSemi, 14).getStringWidth(bindText);
        float bindTextX = rectX + rectWidth - bindTextWidth - 10;

        FontRenderers.info(msSemi, 14).drawString(
                pGuiGraphics.pose(),
                bindText,
                (int) bindTextX,
                (int) (currentModuleY + 4 * heightProgress),
                new Color(0x80FFFFFF, true).getRGB()
        );
    }

    private float renderSettings(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, Module module, float rectX, float currentModuleY, int rectWidth, float heightProgress) {
        currentModuleY += 17 * heightProgress;

        if (module.settings != null && !module.settings.isEmpty()) {
            targetSettingsHeight = calculateSettingsHeight(module);

            if (currentSettingsHeight < targetSettingsHeight) {
                currentSettingsHeight += (targetSettingsHeight - currentSettingsHeight) * 0.1f;
            } else if (currentSettingsHeight > targetSettingsHeight) {
                currentSettingsHeight = targetSettingsHeight;
            }

            float settingsProgress = currentSettingsHeight / targetSettingsHeight * heightProgress;

            for (Setting setting : module.settings) {
                if (setting.getVisibility().get()) {
                    if (setting instanceof BooleanSetting booleanSetting) {
                        renderBooleanSetting(pGuiGraphics, rectX, currentModuleY, rectWidth, booleanSetting, settingsProgress);
                        currentModuleY += 17 * settingsProgress;
                    }
                    else if (setting instanceof ModeSetting modeSetting) {
                        renderModeSetting(pGuiGraphics, rectX, currentModuleY, rectWidth, modeSetting, settingsProgress);
                        currentModuleY += 17 * settingsProgress;
                    }
                    else if (setting instanceof ListSetting listSetting) {
                        renderListSetting(pGuiGraphics, rectX, currentModuleY, rectWidth, listSetting, settingsProgress);
                        currentModuleY += 17 * settingsProgress * (1 + listSetting.getBooleanSliders().size());
                    }
                    else if (setting instanceof NumberSetting numberSetting) {
                        renderNumberSetting(pGuiGraphics, rectX, currentModuleY, rectWidth, numberSetting, settingsProgress);
                        currentModuleY += 17 * settingsProgress;
                    }
                }
            }
        }

        return currentModuleY;
    }

    private void renderNumberSetting(GuiGraphics pGuiGraphics, float rectX, float currentModuleY, int rectWidth, NumberSetting setting, float progress) {
        DrawHelper.rectangle(pGuiGraphics.pose(), rectX + 5, currentModuleY, (rectWidth - 10), 15 * progress, 4, new Color(0x80121212, true).getRGB());

        // Draw setting name and value
        String display = String.format("%s: %.1f", setting.getName(), setting.getValue());
        FontRenderers.info(msSemi, 14).drawString(
                pGuiGraphics.pose(),
                display,
                (int)(rectX + 8),
                (int)(currentModuleY + 4 * progress),
                Color.WHITE.getRGB()
        );

        // Draw slider
        float sliderWidth = rectWidth - 20;
        float sliderX = rectX + 10;
        float sliderY = currentModuleY + 12 * progress;

        // Background bar
        DrawHelper.rectangle(pGuiGraphics.pose(), sliderX, sliderY, sliderWidth, 2, 1, new Color(0x80FFFFFF, true).getRGB());

        // Value indicator
        float valuePercentage = (float) ((setting.getValue() - setting.getMinimum()) / (setting.getMaximum() - setting.getMinimum()));
        float indicatorX = sliderX + (sliderWidth * valuePercentage);
        DrawHelper.rectangle(pGuiGraphics.pose(), indicatorX - 2, sliderY - 2, 4, 6, 2, Color.WHITE.getRGB());
    }

    private void renderBooleanSetting(GuiGraphics pGuiGraphics, float rectX, float currentModuleY, int rectWidth, BooleanSetting setting, float progress) {
        DrawHelper.rectangle(pGuiGraphics.pose(), rectX + 5, currentModuleY, (rectWidth - 10), 15 * progress, 4, new Color(0x80121212, true).getRGB());
        FontRenderers.info(msSemi, 14).drawString(
                pGuiGraphics.pose(),
                setting.getName(),
                (int)(rectX + 8),
                (int)(currentModuleY + 4 * progress),
                setting.isValue() ? Color.WHITE.getRGB() : Color.GRAY.getRGB()
        );
    }

    private void renderModeSetting(GuiGraphics pGuiGraphics, float rectX, float currentModuleY, int rectWidth, ModeSetting setting, float progress) {
        DrawHelper.rectangle(pGuiGraphics.pose(), rectX + 5, currentModuleY, (rectWidth - 10), 15 * progress, 4, new Color(0x80121212, true).getRGB());
        FontRenderers.info(msSemi, 14).drawString(
                pGuiGraphics.pose(),
                setting.getName() + ": " + setting.getMode(),
                (int)(rectX + 8),
                (int)(currentModuleY + 4 * progress),
                Color.WHITE.getRGB()
        );
    }

    private void renderListSetting(GuiGraphics pGuiGraphics, float rectX, float currentModuleY, int rectWidth, ListSetting setting, float progress) {
        DrawHelper.rectangle(pGuiGraphics.pose(), rectX + 5, currentModuleY, (rectWidth -
                10), 15 * progress, 4, new Color(0x80121212, true).getRGB());
        FontRenderers.info(msSemi, 14).drawString(
                pGuiGraphics.pose(),
                setting.getName(),
                (int)(rectX + 8),
                (int)(currentModuleY + 4 * progress),
                Color.WHITE.getRGB()
        );
        currentModuleY += 17 * progress;

        for (BooleanSetting booleanSetting : setting.getBooleanSliders()) {
            DrawHelper.rectangle(pGuiGraphics.pose(), rectX + 10, currentModuleY, (rectWidth - 15), 15 * progress, 4, new Color(0x80121212, true).getRGB());
            FontRenderers.info(msSemi, 14).drawString(
                    pGuiGraphics.pose(),
                    booleanSetting.getName(),
                    (int)(rectX + 13),
                    (int)(currentModuleY + 4 * progress),
                    booleanSetting.isValue() ? Color.WHITE.getRGB() : Color.GRAY.getRGB()
            );
            currentModuleY += 17 * progress;
        }
    }

    private float calculateSettingsHeight(Module module) {
        float height = 0;
        for (Setting setting : module.settings) {
            if (setting.getVisibility().get()) {
                if (setting instanceof ListSetting listSetting) {
                    height += 17 * (1 + listSetting.getBooleanSliders().size());
                } else {
                    height += 17;
                }
            }
        }
        return height;
    }

    private List<Module> getAllModules() {
        return ModuleManager.getModules();
    }

    private List<Module> getModulesForCategory(List<Module> modules, Category category) {
        return modules.stream().filter(module -> module.category == category).toList();
    }

    private boolean handleRegularCategories(double mouseX, double mouseY, int button) {
        int screenHeight = mc.getWindow().getScreenHeight();
        int spacing = 500;
        int xOffset = spacing;
        List<Module> allModules = getAllModules();

        for (Category category : categories) {
            List<Module> categoryModules = getModulesForCategory(allModules, category);
            float rectX = xOffset / 2f;
            float rectY = (screenHeight - 20) / 14f;

            if (handleModuleClick(mouseX, mouseY, button, categoryModules, rectX, rectY, 100, 20)) {
                return true;
            }
            xOffset += spacing - 260;
        }

        return false;
    }

    private boolean handleModuleClick(double mouseX, double mouseY, int button, List<Module> modules, float rectX, float rectY, int rectWidth, int rectHeight) {
        if (!modules.isEmpty()) {
            float moduleRectY = rectY + rectHeight + 5;
            float currentModuleY = moduleRectY + 5;
            float heightProgress = currentHeights.get(modules.get(0).category) / targetHeights.get(modules.get(0).category);

            for (Module module : modules) {
                if (mouseX >= rectX && mouseX <= rectX + rectWidth &&
                        mouseY >= currentModuleY && mouseY <= currentModuleY + 15 * heightProgress) {
                    if (button == 0) {
                        module.toggle();
                        return true;
                    } else if (button == 1 && module.isSetting()) {
                        settingOpen = !settingOpen;
                        selectedModule = settingOpen ? module : null;
                        if (!settingOpen) {
                            currentSettingsHeight = 0;
                        }
                        return true;
                    }
                }

                if (settingOpen && module == selectedModule && module.settings != null && !module.settings.isEmpty()) {
                    currentModuleY = handleSettingsClick(mouseX, mouseY, button, module, rectX, currentModuleY, rectWidth, heightProgress);
                } else {
                    currentModuleY += 17 * heightProgress;
                }
            }
        }
        return false;
    }

    private float handleSettingsClick(double mouseX, double mouseY, int button, Module module, float rectX, float currentModuleY, int rectWidth, float heightProgress) {
        currentModuleY += 17 * heightProgress;
        float settingsProgress = currentSettingsHeight / targetSettingsHeight * heightProgress;

        for (Setting setting : module.settings) {
            if (setting.getVisibility().get()) {
                if (setting instanceof BooleanSetting booleanSetting) {
                    if (mouseX >= rectX + 5 && mouseX <= rectX + rectWidth - 5 &&
                            mouseY >= currentModuleY && mouseY <= currentModuleY + 15 * settingsProgress) {
                        booleanSetting.setValue(!booleanSetting.isValue());
                    }
                    currentModuleY += 17 * settingsProgress;
                }
                else if (setting instanceof ModeSetting modeSetting) {
                    if (mouseX >= rectX + 5 && mouseX <= rectX + rectWidth - 5 &&
                            mouseY >= currentModuleY && mouseY <= currentModuleY + 15 * settingsProgress) {
                        modeSetting.updateValue();
                    }
                    currentModuleY += 17 * settingsProgress;
                }
                else if (setting instanceof ListSetting listSetting) {
                    currentModuleY += 17 * settingsProgress;
                    for (BooleanSetting booleanSetting : listSetting.getBooleanSliders()) {
                        if (mouseX >= rectX + 10 && mouseX <= rectX + rectWidth - 5 &&
                                mouseY >= currentModuleY && mouseY <= currentModuleY + 15 * settingsProgress) {
                            booleanSetting.setValue(!booleanSetting.isValue());
                        }
                        currentModuleY += 17 * settingsProgress;
                    }
                }
                else if (setting instanceof NumberSetting numberSetting) {
                    if (mouseX >= rectX + 5 && mouseX <= rectX + rectWidth - 5 &&
                            mouseY >= currentModuleY && mouseY <= currentModuleY + 15 * settingsProgress) {
                        float sliderWidth = rectWidth - 20;
                        float sliderX = rectX + 10;
                        float relativeX = (float) (mouseX - sliderX);
                        float percentage = Math.max(0, Math.min(1, relativeX / sliderWidth));
                        double newValue = numberSetting.getMinimum() + (percentage * (numberSetting.getMaximum() - numberSetting.getMinimum()));
                        numberSetting.setValue(newValue);
                    }
                    currentModuleY += 17 * settingsProgress;
                }
            }
        }
        return currentModuleY;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isBinding && bindingModule != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                bindingModule.setKey(-1);
            } else if (keyCode != GLFW.GLFW_KEY_UNKNOWN) {
                bindingModule.setKey(keyCode);
            }
            isBinding = false;
            bindingModule = null;
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int spacing = 500;
        int xOffset = spacing;
        int screenHeight = mc.getWindow().getScreenHeight();
        List<Module> allModules = getAllModules();

        for (Category category : categories) {
            List<Module> categoryModules = getModulesForCategory(allModules, category);
            float rectX = xOffset / 2f;
            float rectY = (screenHeight - 20) / 14f;
            float moduleRectY = rectY + 25;
            float currentModuleY = moduleRectY + 5;
            float heightProgress = currentHeights.get(category) / targetHeights.get(category);

            for (Module module : categoryModules) {
                if (mouseX >= rectX && mouseX <= rectX + 100 &&
                        mouseY >= currentModuleY && mouseY <= currentModuleY + 15 * heightProgress) {
                    bindingModule = module;
                    isBinding = true;
                    return true;
                }

                if (settingOpen && module == selectedModule) {
                    currentModuleY += 17 * heightProgress + (module.settings != null ? calculateSettingsHeight(module) : 0);
                } else {
                    currentModuleY += 17 * heightProgress;
                }
            }
            xOffset += spacing - 260;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
