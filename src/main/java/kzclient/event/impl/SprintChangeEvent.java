package kzclient.event.impl;

import kzclient.event.Cancellable;
import kzclient.event.Event;
import lombok.Getter;
import lombok.Setter;

public class SprintChangeEvent extends Event implements Cancellable {

    @Getter private boolean oldValue;
    @Setter @Getter private boolean newValue;

    public SprintChangeEvent(boolean oldValue, boolean newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
