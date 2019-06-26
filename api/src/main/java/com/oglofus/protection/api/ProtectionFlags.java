package com.oglofus.protection.api;

import com.oglofus.protection.api.flags.LongFlag;
import com.oglofus.protection.api.flags.UuidFlag;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.VectorFlag;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 03/06/2017.
 */
@SuppressWarnings("unused")
public final class ProtectionFlags {
    /**
     * The constant PROTECTOR_FLAG.
     */
    public static final VectorFlag PROTECTOR_FLAG = new VectorFlag("protector", RegionGroup.NONE);
    /**
     * The constant RADIUS_FLAG.
     */
    public static final IntegerFlag RADIUS_FLAG = new IntegerFlag("protector-radius", RegionGroup.NONE);
    /**
     * The constant CREATOR.
     */
    public static final UuidFlag CREATOR = new UuidFlag("protector-creator", RegionGroup.NONE);
    /**
     * The constant CREATED.
     */
    public static final LongFlag CREATED = new LongFlag("protector-created", RegionGroup.NONE);
    /**
     * The constant EFFECTS.
     */
    public static final BooleanFlag EFFECTS = new BooleanFlag("protector-effects", RegionGroup.NONE);
}
