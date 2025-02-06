package foby.client.event.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import foby.client.misc.event.events.Event;
@Getter
@Setter
@AllArgsConstructor
public class EventKeyboardInput extends Event {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDownMultiplier;
}
