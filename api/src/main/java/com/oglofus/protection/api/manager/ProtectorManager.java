package com.oglofus.protection.api.manager;

import com.oglofus.protection.api.protector.Protector;
import com.sk89q.worldedit.math.BlockVector3;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 03/06/2017.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ProtectorManager extends Iterable<Protector> {
    /**
     * Gets protectors.
     *
     * @return the protectors
     */
    Collection<Protector> getProtectors();

    /**
     * Gets protector.
     *
     * @param id the id
     * @return the protector
     */
    Optional<Protector> getProtector(String id);

    /**
     * Gets protector.
     *
     * @param vector the vector
     * @return the protector
     */
    Optional<Protector> getProtector(BlockVector3 vector);

    /**
     * Remove protector protector.
     *
     * @param protector the protector
     * @return the protector
     */
    Protector removeProtector(Protector protector);

    /**
     * Register protector protector.
     *
     * @param protector the protector
     * @return the protector
     */
    Protector registerProtector(Protector protector);

    /**
     * Sync protectors boolean.
     *
     * @return the boolean
     */
    boolean syncProtectors();

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    @SuppressWarnings("NullableProblems")
    default Iterator<Protector> iterator() {
        return getProtectors().iterator();
    }
}
