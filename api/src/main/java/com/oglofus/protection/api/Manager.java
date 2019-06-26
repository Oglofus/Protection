package com.oglofus.protection.api;

import com.oglofus.protection.api.manager.ConfigurationManager;
import com.oglofus.protection.api.manager.ProtectorManager;
import com.oglofus.protection.api.manager.RequestManager;
import org.bukkit.inventory.ItemStack;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 22/05/2017.
 */
@SuppressWarnings("unused")
public interface Manager {
    /**
     * Gets requests.
     *
     * @return the requests
     */
    RequestManager getRequests();

    /**
     * Gets protectors.
     *
     * @return the protectors
     */
    ProtectorManager getProtectors();

    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    ConfigurationManager getConfiguration();

    /**
     * Create protector item stack.
     *
     * @param quantity the quantity
     * @return the item stack
     */
    ItemStack createProtector(int quantity);
}
