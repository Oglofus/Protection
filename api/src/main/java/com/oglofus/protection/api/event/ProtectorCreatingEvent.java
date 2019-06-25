package com.oglofus.protection.api.event;

import com.oglofus.protection.api.protector.Protector;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 20/05/2017.
 */
@SuppressWarnings("unused")
public class ProtectorCreatingEvent extends ProtectorEvent implements Cancellable {
    /**
     * The constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The Sender.
     */
    private final Player sender;
    /**
     * The Cancelled.
     */
    private boolean cancelled = false;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     *
     * @param protector the protector
     * @param sender    the sender
     */
    public ProtectorCreatingEvent(Protector protector, Player sender) {
        super(protector);
        this.sender = sender;
    }

    /**
     * Gets handler list.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets sender.
     *
     * @return the sender
     */
    public Player getSender() {
        return sender;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets handlers.
     *
     * @return the handlers
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public HandlerList getHandlers() {
        return handlers;
    }
}
