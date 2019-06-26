package com.oglofus.protection.api.event;

import com.oglofus.protection.api.protector.Protector;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 20/05/2017.
 */
@SuppressWarnings("unused")
public class ProtectorCreatedEvent extends ProtectorEvent {
    /**
     * The constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The Sender.
     */
    private final Player sender;
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
     * @param message   the message
     */
    public ProtectorCreatedEvent(Protector protector, Player sender, BaseComponent[] message) {
        super(protector);
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
    public Player getSender() {
        return sender;
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
    @Nonnull
    public HandlerList getHandlers() {
        return handlers;
    }
}