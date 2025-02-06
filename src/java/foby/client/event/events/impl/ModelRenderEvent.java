package foby.client.event.events.impl;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import foby.client.misc.event.events.Event;


public class ModelRenderEvent extends Event {

    public PlayerRenderer renderer;
    private Runnable entityRenderer;

    public ModelRenderEvent(PlayerRenderer renderer, Runnable entityRenderer) {
        this.renderer = renderer;
        this.entityRenderer = entityRenderer;
    }

    public void render() {
        entityRenderer.run();
    }

}
