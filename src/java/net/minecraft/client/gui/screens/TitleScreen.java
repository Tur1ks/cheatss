package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import foby.client.utils.fonts.FontRenderer;
import foby.client.utils.fonts.FontRenderers;
import foby.client.utils.render.DrawHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;
import net.optifine.reflect.Reflector;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static foby.client.utils.fonts.FontRenderers.msSemi;

public class TitleScreen extends Screen {
    private static final Component TITLE = Component.translatable("narrator.screen.title");
    private static final Component COPYRIGHT_TEXT = Component.translatable("title.credits");
    private final Map<Button, Float> buttonAnimations = new HashMap<>();
    private final List<Button> animatedButtons = new ArrayList<>();
    private float logoScale = 1.0f;
    private float logoRotation = 0f;
    private float backgroundFade = 0f;

    @Nullable
    private SplashRenderer splash;
    private float panoramaFade = 1.0F;
    private boolean fading;
    private long fadeInStart;
    private final LogoRenderer logoRenderer;

    private static final float ANIMATION_SPEED = 0.1f;
    private static final float HOVER_SCALE = 1.1f;
    private static final Color BUTTON_COLOR = new Color(40, 40, 40, 255);
    private static final Color BUTTON_HOVER_COLOR = new Color(60, 60, 60, 255);

    private static final FontRenderer titleFont = FontRenderers.info(msSemi, 24);
    private static final FontRenderer buttonFont = FontRenderers.info(msSemi, 20);

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean pFading) {
        this(pFading, null);
    }

    public TitleScreen(boolean pFading, @Nullable LogoRenderer pLogoRenderer) {
        super(TITLE);
        this.fading = pFading;
        this.logoRenderer = Objects.requireNonNullElseGet(pLogoRenderer, () -> new LogoRenderer(false));
    }

    @Override
    protected void init() {
        if (this.splash == null) {
            this.splash = this.minecraft.getSplashManager().getSplash();
        }

        int k = this.height / 4 + 48;
        createMenuButtons(k);
        initializeButtonAnimations();
    }

    private void createMenuButtons(int startY) {
        addAnimatedButton("menu.singleplayer", startY, btn ->
                this.minecraft.setScreen(new SelectWorldScreen(this)));

        addAnimatedButton("menu.multiplayer", startY + 24, btn -> {
            Screen screen = this.minecraft.options.skipMultiplayerWarning ?
                    new JoinMultiplayerScreen(this) : new SafetyScreen(this);
            this.minecraft.setScreen(screen);
        });

        addAnimatedButton("menu.options", startY + 48, btn ->
                this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options)));

        addAnimatedButton("menu.quit", startY + 72, btn ->
                this.minecraft.stop());
    }

    private void addAnimatedButton(String text, int y, Button.OnPress onPress) {
        Button button = Button.builder(Component.translatable(text), onPress)
                .bounds(this.width / 2 - 100, y, 200, 20)
                .build();
        this.addRenderableWidget(button);
        animatedButtons.add(button);
        buttonAnimations.put(button, 0f);
    }

    private void initializeButtonAnimations() {
        for (Button button : animatedButtons) {
            buttonAnimations.put(button, 0f);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateAnimations(partialTick);
        renderBackground(graphics);
        renderButtons(graphics, mouseX, mouseY);
        renderLogo(graphics);
        renderVersion(graphics);
    }

    private float animationTime = 0f;

    private void updateAnimations(float partialTick) {
        animationTime += partialTick * 0.05f;
        logoScale = 1.0f + (float)Math.sin(animationTime) * 0.03f;
        logoRotation += partialTick * 0.5f;
        backgroundFade = Math.min(1.0f, backgroundFade + 0.05f);
    }

    private void renderBackground(GuiGraphics graphics) {
        DrawHelper.rectangle(graphics.pose(), 0, 0, width, height, 0f,
                new Color(20, 20, 20, (int)(255 * backgroundFade)).getRGB());

        // Animated background elements
        for (int i = 0; i < 5; i++) {
            float offset = (float)Math.sin(logoRotation * 0.5f + i) * 20;
            DrawHelper.drawShadow(graphics.pose(),
                    width / 2f - 150 + offset,
                    height / 2f - 150 + offset,
                    300, 300, 15f, 30f, 10,
                    new Color(30, 30, 30, 50).getRGB());
        }
    }

    private void renderButtons(GuiGraphics graphics, int mouseX, int mouseY) {
        for (Button button : animatedButtons) {
            boolean isHovered = isHovered(button, mouseX, mouseY);
            float targetScale = isHovered ? HOVER_SCALE : 1.0f;
            float currentScale = buttonAnimations.get(button);
            float newScale = Mth.lerp(ANIMATION_SPEED, currentScale, targetScale);
            buttonAnimations.put(button, newScale);

            DrawHelper.scale(graphics.pose(), button.getX(), button.getY(),
                    button.getWidth(), button.getHeight(), newScale, () -> {

                        DrawHelper.drawShadow(graphics.pose(),
                                button.getX(), button.getY(),
                                button.getWidth(), button.getHeight(),
                                5f, 10f, 8,
                                isHovered ? BUTTON_HOVER_COLOR.getRGB() : BUTTON_COLOR.getRGB()
                        );


                        // Use custom font renderer instead of vanilla font
                        buttonFont.drawCenteredString(
                                graphics.pose(),
                                button.getMessage().getString(),
                                button.getX() + button.getWidth() / 2,
                                button.getY() + (button.getHeight() - 10) / 2,
                                isHovered ? 0xFFFFFFFF : 0xFFAAAAAA
                        );
                    });
        }
    }

    private void renderLogo(GuiGraphics graphics) {
        String logoText = "SkyFlow";
        float centerX = width / 2f - titleFont.getStringWidth(logoText) / 2f;
        float centerY = height / 4f;

        DrawHelper.scale(graphics.pose(), centerX, centerY, titleFont.getStringWidth(logoText), titleFont.getFontHeight(logoText), logoScale, () -> {
            DrawHelper.rotate(graphics.pose(), centerX, centerY, titleFont.getStringWidth(logoText), titleFont.getFontHeight(logoText),
                    (float)Math.sin(logoRotation) * 2, () -> {
                        titleFont.drawString(
                                graphics.pose(),
                                logoText,
                                centerX,
                                centerY,
                                new Color(255, 255, 255, (int)(255 * backgroundFade)).getRGB()
                        );
                    });
        });
    }

    public static CompletableFuture<Void> preloadResources(TextureManager pTexMngr, Executor pBackgroundExecutor) {
        return CompletableFuture.allOf(
                pTexMngr.preload(LogoRenderer.MINECRAFT_LOGO, pBackgroundExecutor),
                pTexMngr.preload(LogoRenderer.MINECRAFT_EDITION, pBackgroundExecutor),
                pTexMngr.preload(PanoramaRenderer.PANORAMA_OVERLAY, pBackgroundExecutor),
                CUBE_MAP.preload(pTexMngr, pBackgroundExecutor)
        );
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
        this.clearWidgets(); // Clear existing widgets
        this.animatedButtons.clear(); // Clear our custom button list
        this.buttonAnimations.clear(); // Clear animations map
        this.init(); // Reinitialize everything
    }


    private void renderVersion(GuiGraphics graphics) {  // Renamed to avoid conflict
        String version = "Minecraft " + SharedConstants.getCurrentVersion().getName();
        buttonFont.drawString(
                graphics.pose(),
                version,
                2,
                height - buttonFont.getStringHeight(version) - 2,
                new Color(255, 255, 255, (int)(255 * backgroundFade)).getRGB()
        );
    }

    private boolean isHovered(Button button, int mouseX, int mouseY) {
        return mouseX >= button.getX() && mouseX <= button.getX() + button.getWidth()
                && mouseY >= button.getY() && mouseY <= button.getY() + button.getHeight();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
