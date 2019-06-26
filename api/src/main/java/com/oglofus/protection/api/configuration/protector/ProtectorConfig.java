package com.oglofus.protection.api.configuration.protector;

import org.bukkit.Material;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 03/06/2017.
 */
@SuppressWarnings({"unsued", "unused"})
public interface ProtectorConfig {
    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    String getMetadata();

    /**
     * Gets material.
     *
     * @return the material
     */
    Material getMaterial();

    /**
     * Sets metadata.
     *
     * @param metadata the metadata
     * @return the metadata
     */
    String setMetadata(String metadata);

    /**
     * Sets material.
     *
     * @param material the material
     * @return the material
     */
    Material setMaterial(Material material);
}
