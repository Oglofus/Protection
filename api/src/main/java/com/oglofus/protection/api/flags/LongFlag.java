package com.oglofus.protection.api.flags;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;

import javax.annotation.Nullable;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 10/06/2017.
 */
@SuppressWarnings("unused")
public class LongFlag extends Flag<Long> {
    /**
     * Instantiates a new Long flag.
     *
     * @param name         the name
     * @param defaultGroup the default group
     */
    public LongFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    /**
     * Parse input long.
     *
     * @param context the context
     * @return the long
     * @throws InvalidFlagFormat the invalid flag format
     */
    @Override
    public Long parseInput(FlagContext context) throws InvalidFlagFormat {
        try {
            return Long.parseLong(context.getUserInput());
        } catch (NumberFormatException e) {
            throw new InvalidFlagFormat("Not a number: " + context.getUserInput());
        }
    }

    /**
     * Unmarshal long.
     *
     * @param o the o
     * @return the long
     */
    @Override
    public Long unmarshal(@Nullable Object o) {
        try {
            return Long.parseLong(String.valueOf(o));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Marshal object.
     *
     * @param o the o
     * @return the object
     */
    @Override
    public Object marshal(Long o) {
        return o.toString();
    }
}
