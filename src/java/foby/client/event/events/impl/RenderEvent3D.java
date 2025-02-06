package foby.client.event.events.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.gametest.framework.GameTest;
import org.joml.Matrix4f;
import foby.client.misc.event.events.Event;

public class RenderEvent3D extends Event {

    private PoseStack poseStack;
    private float partialTicks;
    @Setter
    @Getter
    private Matrix4f matrix4f;

    public RenderEvent3D() {
        this.poseStack = new PoseStack();
        this.partialTicks = 1;
        this.matrix4f = null;
    }

    /*
     ESPUtil.setProjectionViewMatrix(getProjectionMatrix(d0),posestack.last().pose());
        ///// render 3D
        RenderEvent3D renderEvent3D = new RenderEvent3D();
        renderEvent3D.setPoseStack(posestack);
        renderEvent3D.setPartialTicks(f);
        EventManager.call(renderEvent3D);
     */


    public void setPoseStack(PoseStack poseStack) {
        this.poseStack = poseStack;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}
