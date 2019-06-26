package com.oglofus.protection.api.protector.region;

import org.bukkit.scheduler.BukkitTask;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 21/05/2017.
 */
@SuppressWarnings("unused")
public interface Effects {
    /**
     * Cancel.
     */
    void cancel();

    /**
     * Refresh.
     */
    void refresh();

    /**
     * Start border.
     */
    void startBorder();

    /**
     * Start creating.
     */
    void startCreating();

    /**
     * Start destroying.
     */
    void startDestroying();

    /**
     * Sets enable.
     *
     * @param enable the enable
     */
    void setEnable(boolean enable);

    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    boolean isEnabled();

    /**
     * Gets refresher.
     *
     * @return the refresher
     */
    BukkitTask getRefresher();
}
