/*
 * Copyright 2020 Nikolaos Grammatikos <nikosgram@oglofus.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oglofus.protection.api.event;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 29/03/2020.
 */
@SuppressWarnings("unused")
public class ProtectorPreCreateEvent extends Event implements Cancellable {
    /**
     * The constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The Sender.
     */
    private final Player sender;
    /**
     * The Region.
     */
    private final CuboidRegion region;
    private final World world;
    /**
     * The Cancelled.
     */
    private boolean cancelled = false;

    /**
     * Instantiates a new Protector pre create event.
     *
     * @param world  the world
     * @param sender the sender
     * @param region the region
     */
    public ProtectorPreCreateEvent(@NotNull World world, @NotNull Player sender, @NotNull CuboidRegion region) {
        this.world = world;
        this.sender = sender;
        this.region = region;
    }

    /**
     * Gets handler list.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }


    /**
     * Gets world.
     *
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets sender.
     *
     * @return the sender
     */
    public Player getSender() {
        return sender;
    }

    /**
     * Gets region.
     *
     * @return the region
     */
    public CuboidRegion getRegion() {
        return region;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets handlers.
     *
     * @return the handlers
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
