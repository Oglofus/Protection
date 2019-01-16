package com.oglofus.protection.sponge;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

/**
 * The type Protection sponge.
 */
@Plugin(id = "protection")
public class ProtectionSponge {
    /**
     * The Logger.
     */
    @Inject
    private Logger logger;

    /**
     * On server start.
     *
     * @param event the event
     */
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Successfully running ProtectionPlugin!!!");
    }
}
