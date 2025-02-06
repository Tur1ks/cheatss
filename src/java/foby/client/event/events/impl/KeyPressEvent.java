package foby.client.event.events.impl;

import foby.client.misc.event.events.Event;

public class KeyPressEvent extends Event {

    private final long pWindowPointer;
    private final int pButton;
    private final int pAction;
    private final int pModifiers;

    public KeyPressEvent(long pWindowPointer, int pButton, int pAction, int pModifiers) {
        this.pWindowPointer = pWindowPointer;
        this.pButton = pButton;
        this.pAction = pAction;
        this.pModifiers = pModifiers;
    }


    public int getModifiers() {
        return pModifiers;
    }

    public int getAction() {
        return pAction;
    }

    public int getButton() {
        return pButton;
    }

    public long getWindowPointer() {
        return pWindowPointer;
    }

}
