package com.oglofus.protection.api.protector.region;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Iterator;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 21/05/2017.
 */
@SuppressWarnings("unused")
public interface Region extends Iterable<BlockVector3> {
    /**
     * Gets world.
     *
     * @return the world
     */
    World getWorld();

    /**
     * Gets radius.
     *
     * @return the radius
     */
    Integer getRadius();

    /**
     * Gets center.
     *
     * @return the center
     */
    BlockVector3 getCenter();

    /**
     * Gets small.
     *
     * @return the small
     */
    CuboidRegion getSmall();

    /**
     * Gets vectors.
     *
     * @return the vectors
     */
    CuboidRegion getVectors();

    /**
     * Gets region.
     *
     * @return the region
     */
    ProtectedRegion getRegion();

    /**
     * Is touching boolean.
     *
     * @param region the region
     * @return the boolean
     */
    default boolean isTouching(CuboidRegion region) {
        BlockVector3 pos1 = region.getPos1();
        BlockVector3 pos2 = region.getPos2();

        //Fast check. Checking the corners.
        if (isTouching(pos1)
                || isTouching(pos2)
                || isTouching(BlockVector3.at(pos1.getBlockX(), pos2.getBlockY(), pos1.getBlockZ()))
                || isTouching(BlockVector3.at(pos1.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()))
                || isTouching(BlockVector3.at(pos2.getBlockX(), pos2.getBlockY(), pos1.getBlockZ()))
                || isTouching(BlockVector3.at(pos1.getBlockX(), pos1.getBlockY(), pos2.getBlockZ()))
                || isTouching(BlockVector3.at(pos2.getBlockX(), pos1.getBlockY(), pos1.getBlockZ()))
                || isTouching(BlockVector3.at(pos2.getBlockX(), pos1.getBlockY(), pos2.getBlockZ()))) {
            return true;
        }

        //Deep check. Checking all the blocks.
        for (BlockVector3 vector : region) {
            if (isTouching(vector)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Is touching boolean.
     *
     * @param vector the vector
     * @return the boolean
     */
    default boolean isTouching(BlockVector3 vector) {
        return getVectors().contains(vector);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    @SuppressWarnings("NullableProblems")
    default Iterator<BlockVector3> iterator() {
        return getVectors().iterator();
    }
}
