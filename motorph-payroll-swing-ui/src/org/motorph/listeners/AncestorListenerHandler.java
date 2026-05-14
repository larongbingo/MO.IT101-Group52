package org.motorph.listeners;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;


public class AncestorListenerHandler implements AncestorListener {
    private final Runnable action;

    public AncestorListenerHandler(Runnable action) {
        this.action = action;
    }

    @Override
    public void ancestorAdded(AncestorEvent event) {
        action.run();
    }

    @Override
    public void ancestorRemoved(AncestorEvent event) {

    }

    @Override
    public void ancestorMoved(AncestorEvent event) {

    }
}
