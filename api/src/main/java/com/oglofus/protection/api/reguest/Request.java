package com.oglofus.protection.api.reguest;

import com.oglofus.protection.api.protector.Protector;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 28/05/2017.
 */
@SuppressWarnings("unused")
public class Request {
    /**
     * The Sender.
     */
    private final Player sender;
    /**
     * The Target.
     */
    private final Player target;
    /**
     * The Protector.
     */
    private final Protector protector;
    /**
     * The Type.
     */
    private final Type type;

    /**
     * Instantiates a new Request.
     *
     * @param sender    the sender
     * @param target    the target
     * @param protector the protector
     * @param type      the type
     */
    @SuppressWarnings("WeakerAccess")
    Request(Player sender, Player target, Protector protector, Type type) {
        this.sender = sender;
        this.target = target;
        this.protector = protector;
        this.type = type;
    }

    /**
     * Builder builder.
     *
     * @param sender the sender
     * @return the builder
     */
    public static Builder builder(Player sender) {
        return new Builder(sender);
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
     * Gets target.
     *
     * @return the target
     */
    public Player getTarget() {
        return target;
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
     * Gets type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * The enum Type.
     */
    public enum Type {
        /**
         * Demote type.
         */
        Demote,
        /**
         * Promote type.
         */
        Promote
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        /**
         * The Sender.
         */
        private final Player sender;
        /**
         * The Target.
         */
        private Player target;
        /**
         * The Protector.
         */
        private Protector protector;
        /**
         * The Type.
         */
        private Type type;

        /**
         * Instantiates a new Builder.
         *
         * @param sender the sender
         */
        Builder(Player sender) {
            this.sender = sender;
        }

        /**
         * Target builder.
         *
         * @param target the target
         * @return the builder
         */
        public Builder target(Player target) {
            this.target = target;

            return this;
        }

        /**
         * Protector builder.
         *
         * @param protector the protector
         * @return the builder
         */
        public Builder protector(Protector protector) {
            this.protector = protector;

            return this;
        }

        /**
         * Type builder.
         *
         * @param type the type
         * @return the builder
         */
        public Builder type(Type type) {
            this.type = type;

            return this;
        }

        /**
         * Build request.
         *
         * @return the request
         */
        public Request build() {
            Validate.notNull(target);
            Validate.notNull(protector);
            Validate.notNull(type);

            return new Request(
                    sender,
                    target,
                    protector,
                    type
            );
        }
    }
}
