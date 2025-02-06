package foby.client.event.events.impl;

import lombok.Getter;
import net.minecraft.world.entity.Entity;
import foby.client.misc.event.events.Event;

public class EventKill extends Event {

    @Getter
    private Entity killer;
    @Getter
    private Entity killed;

    public EventKill(Entity killer, Entity killed) {
        this.killed = killed;
        this.killer = killer;
    }

}
