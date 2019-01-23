/*
 * Copyright 2019 Nikolaos Grammatikos <nikosgram@oglofus.com>
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

package com.oglofus.protection.bukkit.providers;

import com.oglofus.protection.api.TransformException;
import com.oglofus.protection.api.cosmos.Cosmos;
import com.oglofus.protection.api.providers.CosmosProvider;
import com.oglofus.protection.bukkit.cosmos.BukkitCosmos;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.*;

public class BukkitCosmosProvider implements CosmosProvider, Listener {
    private final Map<UUID, BukkitCosmos> cosmoses = new HashMap<>();

    public BukkitCosmosProvider() {
        Bukkit.getWorlds().forEach(world -> cosmoses.put(world.getUID(), new BukkitCosmos(
                world.getUID(),
                world.getName()
        )));
    }

    @EventHandler
    public void onWorldRegister(WorldLoadEvent event) {
        World world = event.getWorld();

        if (cosmoses.containsKey(world.getUID())) {
            cosmoses.get(world.getUID()).setName(world.getName());
        } else {
            cosmoses.put(world.getUID(), new BukkitCosmos(world.getUID(), world.getName()));
        }
    }

    @EventHandler
    public void onWorldRegister(WorldUnloadEvent event) {
        cosmoses.remove(event.getWorld().getUID());
    }

    @Override
    public Collection<UUID> keys() {
        return cosmoses.keySet();
    }

    @Override
    public Collection<Cosmos> values() {
        return new ArrayList<>(cosmoses.values());
    }

    @Override
    public boolean contains(UUID key) {
        return cosmoses.containsKey(key);
    }

    @Override
    public Optional<Cosmos> get(UUID key) {
        return Optional.ofNullable(cosmoses.getOrDefault(key, null));
    }

    @Override
    public <E> Optional<Cosmos> get(E target, Class<E> eClass) throws TransformException {
        if (target instanceof World) {
            return get(((World) target).getUID());
        }

        throw new TransformException(eClass);
    }

    @Override
    public int size() {
        return cosmoses.size();
    }

    @Override
    public boolean isEmpty() {
        return cosmoses.isEmpty();
    }
}
