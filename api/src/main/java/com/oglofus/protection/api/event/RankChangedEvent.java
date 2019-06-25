package com.oglofus.protection.api.event;

import com.oglofus.protection.api.protector.Protector;
import com.oglofus.protection.api.protector.staff.Rank;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 10/06/2017.
 */
@SuppressWarnings("unused")
public class RankChangedEvent extends RankEvent {
    /**
     * The constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The Sender.
     */
    private final CommandSender sender;
    /**
     * The Message.
     */
    private BaseComponent[] message;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     *
     * @param protector the protector
     * @param sender    the sender
     * @param target    the target
     * @param from      the from
     * @param rank      the rank
     * @param message   the message
     */
    public RankChangedEvent(Protector protector, CommandSender sender, OfflinePlayer target, Rank from, Rank rank,
                            BaseComponent[] message) {
        super(protector, target, from, rank);
        this.sender = sender;
        this.message = message;
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
     * Sets rank.
     *
     * @param rank the rank
     */
    @Override
    @Deprecated
    public void setRank(Rank rank) {
        throw new RuntimeException("You can't change the rank now.");
    }

    /**
     * Get message base component [ ].
     *
     * @return the base component [ ]
     */
    public BaseComponent[] getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(BaseComponent[] message) {
        this.message = message;
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
