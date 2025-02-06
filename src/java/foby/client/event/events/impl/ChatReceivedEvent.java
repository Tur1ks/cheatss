package foby.client.event.events.impl;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import foby.client.misc.event.events.Event;

public class ChatReceivedEvent extends Event {
    @Getter
    private Component component;

    public ChatReceivedEvent(Component component) {
        this.component = component;
    }
}
