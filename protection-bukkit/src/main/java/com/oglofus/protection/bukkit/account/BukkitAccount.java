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

package com.oglofus.protection.bukkit.account;

import com.oglofus.protection.OglofusProtection;
import com.oglofus.protection.api.TransformException;
import com.oglofus.protection.api.account.Account;
import com.oglofus.protection.api.cosmos.Cosmos;
import com.oglofus.protection.api.point.Point3d;
import com.oglofus.protection.bukkit.point.BukkitPoint3d;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class BukkitAccount implements Account {
    private final UUID   uuid;
    private       String name;

    public BukkitAccount(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public Optional<Cosmos> getCosmos() {
        return transformTo(Player.class).flatMap(player -> OglofusProtection.getPlatform()
                .getCosmoses()
                .get(player.getWorld().getUID()));
    }

    @Override
    public Optional<Point3d> getPosition() {
        return transformTo(Player.class).map(player -> new BukkitPoint3d(player.getLocation()));
    }

    @Override
    public void sendMessage(String message) {
        transformTo(Player.class).ifPresent(player -> player.sendMessage(message));
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
        if (tClass.isAssignableFrom(Player.class)) {
            return Optional.ofNullable((T) Bukkit.getPlayer(uuid));
        } else if (tClass.isAssignableFrom(OfflinePlayer.class)) {
            return Optional.ofNullable((T) Bukkit.getOfflinePlayer(uuid));
        } else {
            throw new TransformException(tClass);
        }
    }
}
