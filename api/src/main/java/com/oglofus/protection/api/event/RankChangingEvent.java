package com.oglofus.protection.api.event;

import com.oglofus.protection.api.protector.Protector;
import com.oglofus.protection.api.protector.staff.Rank;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 10/06/2017.
 */
@SuppressWarnings("unused")
public class RankChangingEvent extends RankEvent implements Cancellable {
    /**
     * The constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The Sender.
     */
    private final CommandSender sender;
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
     * @param target    the target
     * @param from      the from
     * @param rank      the rank
     */
    public RankChangingEvent(Protector protector, CommandSender sender, OfflinePlayer target, Rank from, Rank rank) {
        super(protector, target, from, rank);
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
    public CommandSender getSender() {
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
