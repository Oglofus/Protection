package com.oglofus.protection;

import com.oglofus.protection.api.Manager;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 04/06/2017.
 */
@SuppressWarnings("unused")
public final class OglofusProtection {
    /**
     * The constant manager.
     */
    private static Manager manager;

    /**
     * Invoke.
     *
     * @param manager the manager
     */
    static void invoke(Manager manager) {
        if (OglofusProtection.manager != null) throw new RuntimeException();

        OglofusProtection.manager = manager;
    }

    /**
     * Gets manager.
     *
     * @return the manager
     */
    public static Manager getManager() {
        return manager;
    }
}
