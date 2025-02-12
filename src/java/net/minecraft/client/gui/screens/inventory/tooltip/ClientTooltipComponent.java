package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;

public interface ClientTooltipComponent {
    static ClientTooltipComponent create(FormattedCharSequence pText) {
        return new ClientTextTooltip(pText);
    }

    static ClientTooltipComponent create(TooltipComponent pVisualTooltipComponent) {
        if (pVisualTooltipComponent instanceof BundleTooltip bundletooltip) {
            return new ClientBundleTooltip(bundletooltip.contents());
        } else if (pVisualTooltipComponent instanceof ClientActivePlayersTooltip.ActivePlayersTooltip clientactiveplayerstooltip$activeplayerstooltip) {
            return new ClientActivePlayersTooltip(clientactiveplayerstooltip$activeplayerstooltip);
        } else {
            throw new IllegalArgumentException("Unknown TooltipComponent");
        }
    }

    int getHeight();

    int getWidth(Font pFont);

    default void renderText(Font pFont, int pMouseX, int pMouseY, Matrix4f pMatrix, MultiBufferSource.BufferSource pBufferSource) {
    }

    default void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
    }
}