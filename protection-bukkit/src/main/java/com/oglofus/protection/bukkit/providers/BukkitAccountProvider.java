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
import com.oglofus.protection.api.account.Account;
import com.oglofus.protection.api.providers.AccountsProvider;
import com.oglofus.protection.bukkit.account.BukkitAccount;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class BukkitAccountProvider implements AccountsProvider, Listener {
    private final Map<UUID, BukkitAccount> accounts = new HashMap<>();

    public BukkitAccountProvider() {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            accounts.put(offlinePlayer.getUniqueId(), new BukkitAccount(
                    offlinePlayer.getUniqueId(),
                    offlinePlayer.getName()
            ));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (accounts.containsKey(player.getUniqueId())) {
            accounts.get(player.getUniqueId()).setName(player.getName());
        } else {
            accounts.put(player.getUniqueId(), new BukkitAccount(player.getUniqueId(), player.getName()));
        }
    }

    @Override
    public Collection<UUID> keys() {
        return accounts.keySet();
    }

    @Override
    public Collection<Account> values() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public boolean contains(UUID key) {
        return accounts.containsKey(key);
    }

    @Override
    public Optional<Account> get(UUID key) {
        return Optional.ofNullable(accounts.getOrDefault(key, null));
    }

    @Override
    public <E> Optional<Account> get(E target, Class<E> eClass) throws TransformException {
        if (target instanceof OfflinePlayer) {
            return get(((OfflinePlayer) target).getUniqueId());
        }

        throw new TransformException(eClass);
    }

    @Override
    public int size() {
        return accounts.size();
    }

    @Override
    public boolean isEmpty() {
        return accounts.isEmpty();
    }
}
