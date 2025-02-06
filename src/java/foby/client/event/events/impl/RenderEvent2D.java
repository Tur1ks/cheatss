package foby.client.event.events.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import foby.client.misc.event.events.Event;
import net.minecraft.client.gui.GuiGraphics;

public class RenderEvent2D extends Event {

    public GuiGraphics guiGraphics;
    public PoseStack poseStack;
    public DeltaTracker deltaTracker;

    public RenderEvent2D() {
        this.guiGraphics = null;
        this.poseStack = null;
        this.deltaTracker = null;
    }

    public void setGuiGraphics(GuiGraphics guiGraphics) {
        this.guiGraphics = guiGraphics;
    }

    public void setDeltaTracker(DeltaTracker deltaTracker) {
        this.deltaTracker = deltaTracker;
    }

    public DeltaTracker getDeltaTracker() {
        return deltaTracker;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }
}
