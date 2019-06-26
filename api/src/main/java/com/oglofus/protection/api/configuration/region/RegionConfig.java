package com.oglofus.protection.api.configuration.region;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 03/06/2017.
 */
@SuppressWarnings("unused")
public interface RegionConfig {

    /**
     * Gets flags.
     *
     * @return the flags
     */
    FlagsConfig getFlags();

    /**
     * Gets radius.
     *
     * @return the radius
     */
    RadiusConfig getRadius();

    /**
     * Gets limits.
     *
     * @return the limits
     */
    LimitsConfig getLimits();
}
