package com.oglofus.protection.api.protector.staff;

import com.google.common.collect.Iterators;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.Iterator;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 21/05/2017.
 */
@SuppressWarnings("unused")
public interface Staff extends Iterable<OfflinePlayer> {
    /**
     * Gets rank.
     *
     * @param player the player
     * @return the rank
     */
    Rank getRank(OfflinePlayer player);

    /**
     * Sets rank.
     *
     * @param player the player
     * @param rank   the rank
     */
    void setRank(OfflinePlayer player, Rank rank);

    /**
     * Is staff boolean.
     *
     * @param player the player
     * @return the boolean
     */
    default boolean isStaff(OfflinePlayer player) {
        return !getRank(player).equals(Rank.None);
    }

    /**
     * Is owner boolean.
     *
     * @param player the player
     * @return the boolean
     */
    default boolean isOwner(OfflinePlayer player) {
        return getRank(player).equals(Rank.Owner);
    }

    /**
     * Is member boolean.
     *
     * @param player the player
     * @return the boolean
     */
    default boolean isMember(OfflinePlayer player) {
        return getRank(player).equals(Rank.Member);
    }

    /**
     * Gets owners.
     *
     * @return the owners
     */
    Collection<OfflinePlayer> getOwners();

    /**
     * Gets members.
     *
     * @return the members
     */
    Collection<OfflinePlayer> getMembers();

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    @SuppressWarnings("NullableProblems")
    default Iterator<OfflinePlayer> iterator() {
        return Iterators.concat(getOwners().iterator(), getMembers().iterator());
    }
}
