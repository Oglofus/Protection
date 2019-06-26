package com.oglofus.protection.api.event;

import com.oglofus.protection.api.protector.Protector;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 19/05/2017.
 */
@SuppressWarnings("unused")
public abstract class ProtectorEvent extends Event {
    /**
     * The constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();
    /**
     * The Protector.
     */
    private final Protector protector;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     *
     * @param protector the protector
     */
    public ProtectorEvent(Protector protector) {
        this.protector = protector;
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
     * Gets protector.
     *
     * @return the protector
     */
    public Protector getProtector() {
        return protector;
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
