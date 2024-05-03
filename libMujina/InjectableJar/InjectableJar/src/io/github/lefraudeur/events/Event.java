package io.github.lefraudeur.events;

public abstract class Event //!!!! every event related stuff is hardcoded, don't rename
{
    private boolean cancelled = false;

    public abstract void dispatch();

    public boolean isCancelled()
    {
        return cancelled;
    }

    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public void cancel() { this.setCancelled(true); }
}
