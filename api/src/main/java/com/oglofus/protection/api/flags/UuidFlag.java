package com.oglofus.protection.api.flags;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 10/06/2017.
 */
@SuppressWarnings("unused")
public class UuidFlag extends Flag<UUID> {
    /**
     * Instantiates a new Uuid flag.
     *
     * @param name         the name
     * @param defaultGroup the default group
     */
    public UuidFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    /**
     * Parse input uuid.
     *
     * @param context the context
     * @return the uuid
     * @throws InvalidFlagFormat the invalid flag format
     */
    @Override
    public UUID parseInput(FlagContext context) throws InvalidFlagFormat {
        try {
            return UUID.fromString(context.getUserInput());
        } catch (IllegalArgumentException e) {
            throw new InvalidFlagFormat(e.getMessage());
        }
    }

    /**
     * Unmarshal uuid.
     *
     * @param o the o
     * @return the uuid
     */
    @Override
    public UUID unmarshal(@Nullable Object o) {
        try {
            return UUID.fromString(String.valueOf(o));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Marshal object.
     *
     * @param uuid the uuid
     * @return the object
     */
    @Override
    public Object marshal(UUID uuid) {
        return uuid.toString();
    }
}
