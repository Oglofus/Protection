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

package com.oglofus.protection.bukkit.cosmos;

import com.oglofus.protection.api.TransformException;
import com.oglofus.protection.api.cosmos.Cosmos;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;
import java.util.UUID;

public class BukkitCosmos implements Cosmos {
    private final UUID   uuid;
    private       String name;

    public BukkitCosmos(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> transformTo(Class<T> tClass) throws TransformException {
        if (tClass.isAssignableFrom(World.class)) {
            return Optional.ofNullable((T) Bukkit.getWorld(uuid));
        }

        throw new TransformException(tClass);
    }
}
