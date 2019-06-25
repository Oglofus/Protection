package com.oglofus.protection.api.protector;

import com.oglofus.protection.api.protector.region.Effects;
import com.oglofus.protection.api.protector.region.Region;
import com.oglofus.protection.api.protector.staff.Staff;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Date;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 19/05/2017.
 */
@SuppressWarnings("unused")
public interface Protector extends Listener {

    /**
     * Gets id.
     *
     * @return the id
     */
    String getId();

    /**
     * Gets staff.
     *
     * @return the staff
     */
    Staff getStaff();

    /**
     * Gets region.
     *
     * @return the region
     */
    Region getRegion();

    /**
     * Gets effects.
     *
     * @return the effects
     */
    Effects getEffects();

    /**
     * Gets created.
     *
     * @return the created
     */
    Date getCreated();

    /**
     * Gets creator.
     *
     * @return the creator
     */
    OfflinePlayer getCreator();

    /**
     * Destroy.
     */
    default void destroy() {
        HandlerList.unregisterAll(this);

        getEffects().getRefresher().cancel();
        getEffects().cancel();
    }
}
