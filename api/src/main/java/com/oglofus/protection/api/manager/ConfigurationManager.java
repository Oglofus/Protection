package com.oglofus.protection.api.manager;

import com.oglofus.protection.api.configuration.ConfigurationService;
import com.oglofus.protection.api.configuration.ConfigurationStatus;
import com.oglofus.protection.api.configuration.protector.ProtectorConfig;
import com.oglofus.protection.api.configuration.region.RegionConfig;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 03/06/2017.
 */
@SuppressWarnings("unused")
public interface ConfigurationManager {

    /**
     * Gets region.
     *
     * @return the region
     */
    RegionConfig getRegion();

    /**
     * Gets protector.
     *
     * @return the protector
     */
    ProtectorConfig getProtector();

    /**
     * Gets status.
     *
     * @return the status
     */
    ConfigurationStatus getStatus();

    /**
     * Execute boolean.
     *
     * @param service the service
     * @return the boolean
     */
    boolean execute(ConfigurationService service);
}
