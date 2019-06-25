package com.oglofus.protection.api.event;

import com.oglofus.protection.api.protector.Protector;
import com.oglofus.protection.api.protector.staff.Rank;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 10/06/2017.
 */
@SuppressWarnings("unused")
public abstract class RankEvent extends Event {
    /**
     * The constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();
    /**
     * The Protector.
     */
    private final Protector protector;
    /**
     * The Target.
     */
    private final OfflinePlayer target;
    /**
     * The From.
     */
    private final Rank from;
    /**
     * The Rank.
     */
    private Rank rank;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     *
     * @param protector the protector
     * @param target    the target
     * @param from      the from
     * @param rank      the rank
     */
    public RankEvent(Protector protector, OfflinePlayer target, Rank from, Rank rank) {
        this.protector = protector;
        this.target = target;
        this.from = from;
        this.rank = rank;
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
     * Gets target.
     *
     * @return the target
     */
    public OfflinePlayer getTarget() {
        return target;
    }

    /**
     * Gets from.
     *
     * @return the from
     */
    public Rank getFrom() {
        return from;
    }

    /**
     * Gets rank.
     *
     * @return the rank
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Sets rank.
     *
     * @param rank the rank
     */
    public void setRank(Rank rank) {
        this.rank = rank;
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
