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

package com.oglofus.protection;

import com.oglofus.protection.api.Platform;
import com.oglofus.protection.api.cosmos.Cosmos;
import com.oglofus.protection.api.providers.AccountsProvider;
import com.oglofus.protection.api.providers.CosmosProvider;
import com.oglofus.protection.api.providers.ProtectionsProvider;
import com.oglofus.protection.bukkit.providers.BukkitAccountProvider;
import com.oglofus.protection.bukkit.providers.BukkitCosmosProvider;
import com.oglofus.protection.bukkit.providers.BukkitProtectionProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ProtectionBukkit extends JavaPlugin implements Platform {
    private BukkitAccountProvider    accountProvider;
    private BukkitCosmosProvider     cosmosProvider;
    private BukkitProtectionProvider protectionProvider;

    @Override
    public void onLoad() {
        super.onLoad();

        OglofusProtection.init(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.cosmosProvider = new BukkitCosmosProvider();
        this.accountProvider = new BukkitAccountProvider();
        this.protectionProvider = new BukkitProtectionProvider();

        Bukkit.getPluginManager().registerEvents(cosmosProvider, this);
        Bukkit.getPluginManager().registerEvents(accountProvider, this);
        Bukkit.getPluginManager().registerEvents(protectionProvider, this);

        cosmosProvider.stream().map(Cosmos::getName).forEach(getLogger()::info);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return super.onTabComplete(sender, command, alias, args);
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public AccountsProvider getAccounts() {
        return null;
    }

    @Override
    public ProtectionsProvider getProtections() {
        return null;
    }

    @Override
    public CosmosProvider getCosmoses() {
        return cosmosProvider;
    }
}
