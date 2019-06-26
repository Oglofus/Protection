package com.oglofus.protection.api.configuration;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 14/06/2017.
 */
@SuppressWarnings("unused")
public enum ConfigurationStatus {
    /**
     * Loading configuration status.
     */
    Loading,
    /**
     * Loaded configuration status.
     */
    Loaded,
    /**
     * Saving configuration status.
     */
    Saving,
    /**
     * Saved configuration status.
     */
    Saved,
    /**
     * Error configuration status.
     */
    Error
}
