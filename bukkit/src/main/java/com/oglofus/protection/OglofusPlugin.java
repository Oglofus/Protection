package com.oglofus.protection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oglofus.protection.api.Manager;
import com.oglofus.protection.api.ProtectionFlags;
import com.oglofus.protection.api.configuration.ConfigurationService;
import com.oglofus.protection.api.configuration.ConfigurationStatus;
import com.oglofus.protection.api.configuration.protector.ProtectorConfig;
import com.oglofus.protection.api.configuration.region.*;
import com.oglofus.protection.api.manager.ConfigurationManager;
import com.oglofus.protection.api.manager.ProtectorManager;
import com.oglofus.protection.api.manager.RequestManager;
import com.oglofus.protection.api.protector.Protector;
import com.oglofus.protection.api.reguest.Request;
import com.oglofus.protection.api.value.Value;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandException;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 03/06/2017.
 */
public class OglofusPlugin extends JavaPlugin implements Manager {
    /**
     * The Plugin.
     */
    private final OglofusPlugin plugin;
    /**
     * The Requests.
     */
    private final OglofusRequestManager requests;
    /**
     * The Protectors.
     */
    private final OglofusProtectorManager protectors;
    /**
     * The Configuration.
     */
    private final OglofusConfigurationManager configuration;

    /**
     * Instantiates a new Oglofus plugin.
     */
    public OglofusPlugin() {
        WorldGuard.getInstance().getFlagRegistry().register(ProtectionFlags.PROTECTOR_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(ProtectionFlags.RADIUS_FLAG);
        WorldGuard.getInstance().getFlagRegistry().register(ProtectionFlags.CREATOR);
        WorldGuard.getInstance().getFlagRegistry().register(ProtectionFlags.CREATED);
        WorldGuard.getInstance().getFlagRegistry().register(ProtectionFlags.EFFECTS);

        OglofusProtection.invoke(this.plugin = this);
        this.requests = new OglofusRequestManager();
        this.protectors = new OglofusProtectorManager();
        this.configuration = new OglofusConfigurationManager();
    }

    /**
     * On load.
     */
    @Override
    public void onLoad() {
        super.onLoad();

        saveDefaultConfig();

        if (!configuration.execute(ConfigurationService.Load)) {
            if (configuration.status.equals(ConfigurationStatus.Error)) {
                getServer().getPluginManager().disablePlugin(this);

                return;
            }
        }

        if (!configuration.execute(ConfigurationService.Save)) {
            if (configuration.status.equals(ConfigurationStatus.Error)) {
                getServer().getPluginManager().disablePlugin(this);

                return;
            }
        }
    }

    /**
     * On enable.
     */
    @Override
    public void onEnable() {
        super.onEnable();

        getServer().getPluginManager().registerEvents(new OglofusListener(this), this);

        OglofusCommand oglofusCommand = new OglofusCommand(this);
        PluginCommand command = getCommand("protection");

        command.setExecutor((sender, cmd, label, args) -> {
            try {
                if (!oglofusCommand.onCommand(sender, cmd, label, args)) {
                    TextComponent help = new TextComponent("Type ");
                    help.setColor(ChatColor.YELLOW);

                    TextComponent com = new TextComponent("/p help");
                    com.setColor(ChatColor.RESET);
                    com.setHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Click to suggest the command")
                                    .color(ChatColor.RED)
                                    .create()));
                    com.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p help"));
                    help.addExtra(com);

                    TextComponent more = new TextComponent(" for help.");
                    more.setColor(ChatColor.YELLOW);
                    help.addExtra(more);

                    OglofusUtils.sendMessage(sender, help);
                }
            } catch (CommandException e) {
                if (e.getMessage() != null && e.getCause() == null) {
                    OglofusUtils.sendMessage(sender, new ComponentBuilder(e.getMessage())
                            .color(ChatColor.RED)
                            .create());
                } else {
                    throw e;
                }
            }

            return true;
        });
        command.setTabCompleter(oglofusCommand);

        protectors.syncProtectors();
    }

    /**
     * On disable.
     */
    @Override
    public void onDisable() {
        super.onDisable();
    }

    /**
     * Gets requests.
     *
     * @return the requests
     */
    @Override
    public RequestManager getRequests() {
        return requests;
    }

    /**
     * Gets protectors.
     *
     * @return the protectors
     */
    @Override
    public ProtectorManager getProtectors() {
        return protectors;
    }

    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    @Override
    public ConfigurationManager getConfiguration() {
        return configuration;
    }

    /**
     * Create protector item stack.
     *
     * @param quantity the quantity
     * @return the item stack
     */
    @Override
    public ItemStack createProtector(int quantity) {
        ItemStack stack = new ItemStack(configuration.protector.material, quantity);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(ChatColor.BLUE + "SUPER " +
                ChatColor.GOLD + "MEGA " +
                ChatColor.DARK_AQUA + "AWESOME " +
                ChatColor.AQUA + "Protection Core");
        meta.setLore(Lists.newArrayList(configuration.protector.metadata));
        stack.setItemMeta(meta);

        return stack;
    }

    /**
     * The type Oglofus request manager.
     */
    class OglofusRequestManager implements RequestManager {
        /**
         * The Requests.
         */
        private final Map<UUID, Request> requests = Maps.newHashMap();

        /**
         * Gets requests.
         *
         * @return the requests
         */
        @Override
        public Collection<Request> getRequests() {
            return requests.values();
        }

        /**
         * Gets request.
         *
         * @param uuid the uuid
         * @return the request
         */
        @Override
        public Optional<Request> getRequest(UUID uuid) {
            return Optional.ofNullable(requests.getOrDefault(uuid, null));
        }

        /**
         * Append request request.
         *
         * @param request the request
         * @return the request
         */
        @Override
        public Request appendRequest(Request request) {

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Player sender = request.getSender();

                removeValue(sender.getUniqueId());

                if (sender.isOnline()) {
                    OglofusUtils.sendMessage(sender, new ComponentBuilder("Your request has canceled.")
                            .color(ChatColor.RED)
                            .create());
                }
            }, 6000);

            return requests.put(request.getSender().getUniqueId(), request);
        }

        /**
         * Remove value request.
         *
         * @param uuid the uuid
         * @return the request
         */
        @Override
        public Request removeValue(UUID uuid) {
            return requests.remove(uuid);
        }
    }

    /**
     * The type Oglofus protector manager.
     */
    class OglofusProtectorManager implements ProtectorManager {
        /**
         * The Protectors.
         */
        private final Map<String, Protector> protectors = Maps.newHashMap();

        /**
         * Gets protectors.
         *
         * @return the protectors
         */
        @Override
        public Collection<Protector> getProtectors() {
            return protectors.values();
        }

        /**
         * Gets protector.
         *
         * @param id the id
         * @return the protector
         */
        @Override
        public Optional<Protector> getProtector(String id) {
            return Optional.ofNullable(protectors.getOrDefault(id, null));
        }

        /**
         * Gets protector.
         *
         * @param vector the vector
         * @return the protector
         */
        @Override
        public Optional<Protector> getProtector(BlockVector3 vector) {
            for (Protector protector : this) {
                if (protector.getRegion().isTouching(vector)) {
                    return Optional.of(protector);
                }
            }

            return Optional.empty();
        }

        /**
         * Remove protector protector.
         *
         * @param protector the protector
         * @return the protector
         */
        @Override
        public Protector removeProtector(Protector protector) {
            protector.destroy();

            protectors.remove(protector.getId());

            Objects.requireNonNull(WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(protector.getRegion().getWorld()))
                    .removeRegion(protector.getId());

            return protector;
        }

        /**
         * Register protector protector.
         *
         * @param protector the protector
         * @return the protector
         */
        @Override
        public Protector registerProtector(Protector protector) {
            Objects.requireNonNull(WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(protector.getRegion().getWorld()))
                    .addRegion(protector.getRegion().getRegion());

            protectors.put(protector.getId(), protector);

            return protector;
        }

        /**
         * Sync protectors boolean.
         *
         * @return the boolean
         */
        @Override
        public boolean syncProtectors() {
            try {
                for (World world : Bukkit.getWorlds()) {

                    com.sk89q.worldedit.world.World world1 =
                            WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(world.getName());

                    if (world1 == null) {
                        getLogger().warning("Something got wrong with matching world '" + world.getName() + "'.");

                        continue;
                    }

                    RegionManager manager =
                            WorldGuard.getInstance().getPlatform().getRegionContainer().get(world1);

                    if (manager != null) {
                        for (ProtectedRegion region : manager.getRegions().values()) {

                            if (region.getFlag(ProtectionFlags.PROTECTOR_FLAG) != null) {
                                if (region.getFlag(ProtectionFlags.RADIUS_FLAG) != null) {
                                    if (region.getFlag(ProtectionFlags.CREATOR) != null) {
                                        Protector protector = new OglofusProtector(
                                                plugin,
                                                region,
                                                world1
                                        );

                                        protectors.put(region.getId(), protector);

                                        protector.getEffects().startBorder();
                                    } else {
                                        getLogger().warning("The region with id '" + region.getId() + "' is outdated.");
                                    }
                                } else {
                                    getLogger().warning("The region with id '" + region.getId() + "' is outdated.");
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
        }
    }

    /**
     * The type Oglofus configuration manager.
     */
    class OglofusConfigurationManager implements ConfigurationManager {
        /**
         * The Parent.
         */
        private final Path parent;
        /**
         * The Path.
         */
        private final Path path;
        /**
         * The Region.
         */
        private OglofusRegionConfig region;
        /**
         * The Protector.
         */
        private OglofusProtectorConfig protector;
        /**
         * The Status.
         */
        private ConfigurationStatus status;

        /**
         * Instantiates a new Oglofus configuration manager.
         */
        OglofusConfigurationManager() {
            this.parent = getDataFolder().toPath();
            this.path = Paths.get(parent.toString(), "config.old.yml");

            this.status = ConfigurationStatus.Loaded;

            this.region = new OglofusRegionConfig();
            this.protector = new OglofusProtectorConfig();
        }

        /**
         * Gets region.
         *
         * @return the region
         */
        @Override
        public RegionConfig getRegion() {
            return region;
        }

        /**
         * Gets protector.
         *
         * @return the protector
         */
        @Override
        public ProtectorConfig getProtector() {
            return protector;
        }

        /**
         * Gets status.
         *
         * @return the status
         */
        @Override
        public ConfigurationStatus getStatus() {
            return status;
        }

        /**
         * Execute boolean.
         *
         * @param service the service
         * @return the boolean
         */
        @Override
        public boolean execute(ConfigurationService service) {
            if (status.equals(ConfigurationStatus.Loading) || status.equals(ConfigurationStatus.Saving)) {
                return false;
            }

            if (Files.notExists(parent)) {
                try {
                    Files.createDirectories(parent);
                } catch (Exception e) {
                    e.printStackTrace();
                    status = ConfigurationStatus.Error;

                    return false;
                }
            }

            switch (service) {
                case Load:
                    try {
                        status = ConfigurationStatus.Loading;

                        if (Files.notExists(path)) {
                            saveDefaultConfig();
                        }

                        reloadConfig();

                        region.reload();
                        protector.reload();

                        status = ConfigurationStatus.Loaded;

                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        status = ConfigurationStatus.Error;

                        return false;
                    }
                case Save:
                    try {
                        status = ConfigurationStatus.Saving;

                        saveConfig();

                        status = ConfigurationStatus.Saved;

                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        status = ConfigurationStatus.Error;

                        return false;
                    }
            }

            return false;
        }

        /**
         * The type Oglofus protector config.
         */
        class OglofusProtectorConfig implements ProtectorConfig {

            /**
             * The Metadata.
             */
            private String metadata;
            /**
             * The Material.
             */
            private Material material;

            /**
             * Instantiates a new Oglofus protector config.
             */
            OglofusProtectorConfig() {
            }

            /**
             * Gets metadata.
             *
             * @return the metadata
             */
            @Override
            public String getMetadata() {
                return metadata;
            }

            /**
             * Gets material.
             *
             * @return the material
             */
            @Override
            public Material getMaterial() {
                return material;
            }

            /**
             * Sets metadata.
             *
             * @param metadata the metadata
             * @return the metadata
             */
            @Override
            public String setMetadata(String metadata) {
                this.metadata = metadata;

                getConfig().set("protector.metadata", metadata);

                return this.metadata;
            }

            /**
             * Sets material.
             *
             * @param material the material
             * @return the material
             */
            @Override
            public Material setMaterial(Material material) {
                this.material = material;

                getConfig().set("protector.material", material.name());

                return this.material;
            }

            /**
             * Reload.
             */
            private void reload() {
                metadata = getConfig().getString("protector.metadata", "protector");
                material = Material.matchMaterial(getConfig().getString("protector.material", "SPONGE"));
            }
        }

        /**
         * The type Oglofus region config.
         */
        class OglofusRegionConfig implements RegionConfig {

            /**
             * The Limits.
             */
            private final OglofusLimitsConfig limits;
            /**
             * The Radius.
             */
            private final OglofusRadiusConfig radius;
            /**
             * The Flags.
             */
            private final OglofusFlagsConfig flags;

            /**
             * Instantiates a new Oglofus region config.
             */
            OglofusRegionConfig() {
                this.limits = new OglofusLimitsConfig();
                this.radius = new OglofusRadiusConfig();
                this.flags = new OglofusFlagsConfig();
            }

            /**
             * Gets flags.
             *
             * @return the flags
             */
            @Override
            public FlagsConfig getFlags() {
                return flags;
            }

            /**
             * Gets radius.
             *
             * @return the radius
             */
            @Override
            public RadiusConfig getRadius() {
                return radius;
            }

            /**
             * Gets limits.
             *
             * @return the limits
             */
            @Override
            public LimitsConfig getLimits() {
                return limits;
            }

            /**
             * Reload.
             */
            private void reload() {
                limits.reload();
                radius.reload();
                flags.reload();
            }

            /**
             * The type Oglofus flags config.
             */
            class OglofusFlagsConfig implements FlagsConfig {
                /**
                 * The Values.
                 */
                private final Map<String, FlagEntity> values = Maps.newHashMap();

                /**
                 * Instantiates a new Oglofus flags config.
                 */
                OglofusFlagsConfig() {
                }

                /**
                 * Gets values.
                 *
                 * @return the values
                 */
                @Override
                public Collection<FlagEntity> getValues() {
                    return values.values();
                }

                /**
                 * Gets value.
                 *
                 * @param id the id
                 * @return the value
                 */
                @Override
                public Optional<FlagEntity> getValue(String id) {
                    return Optional.ofNullable(values.getOrDefault(id, null));
                }

                /**
                 * Append value flag entity.
                 *
                 * @param value the value
                 * @return the flag entity
                 */
                @Override
                public FlagEntity appendValue(FlagEntity value) {
                    ConfigurationSection section = getConfig().createSection("region.flags." + value.getName());

                    if (value.isDefault()) {
                        section.set("default", true);
                    }

                    value.getPermission().ifPresent(s -> section.set("permission", s));
                    value.getGroup().ifPresent(g -> section.set("group", g));

                    section.set("value", value.getValue());

                    return values.put(value.getName(), value);
                }

                /**
                 * Remove value flag entity.
                 *
                 * @param id the id
                 * @return the flag entity
                 */
                @Override
                public FlagEntity removeValue(String id) {
                    if (getConfig().contains("region.flags." + id)) {
                        getConfig().set("region.flags." + id, null);
                    }

                    return values.remove(id);
                }

                /**
                 * Reload.
                 */
                private void reload() {
                    this.reload(getConfig().getConfigurationSection("region.flags"));
                }
            }

            /**
             * The type Oglofus limits config.
             */
            class OglofusLimitsConfig implements LimitsConfig {
                /**
                 * The Values.
                 */
                private final Map<String, Value<Integer>> values = Maps.newHashMap();

                /**
                 * Instantiates a new Oglofus limits config.
                 */
                OglofusLimitsConfig() {
                }

                /**
                 * Gets values.
                 *
                 * @return the values
                 */
                @Override
                public Collection<Value<Integer>> getValues() {
                    return values.values();
                }

                /**
                 * Gets value.
                 *
                 * @param id the id
                 * @return the value
                 */
                @Override
                public Optional<Value<Integer>> getValue(String id) {
                    return Optional.ofNullable(values.getOrDefault(id, null));
                }

                /**
                 * Append value value.
                 *
                 * @param value the value
                 * @return the value
                 */
                @Override
                public Value<Integer> appendValue(Value<Integer> value) {
                    ConfigurationSection section = getConfig().createSection("region.limits." + value.getName());
                    Optional<String> permission = value.getPermission();

                    if (value.isDefault()) {
                        section.set("default", true);
                    }

                    permission.ifPresent(s -> section.set("permission", s));

                    section.set("value", value.getValue());

                    return values.put(value.getName(), value);
                }

                /**
                 * Remove value value.
                 *
                 * @param id the id
                 * @return the value
                 */
                @Override
                public Value<Integer> removeValue(String id) {
                    if (getConfig().contains("region.limits." + id)) {
                        getConfig().set("region.limits." + id, null);
                    }

                    return values.remove(id);
                }

                /**
                 * Reload.
                 */
                private void reload() {
                    this.reload(getConfig().getConfigurationSection("region.limits"));
                }
            }

            /**
             * The type Oglofus radius config.
             */
            class OglofusRadiusConfig implements RadiusConfig {
                /**
                 * The Values.
                 */
                private final Map<String, Value<Integer>> values = Maps.newHashMap();

                /**
                 * Instantiates a new Oglofus radius config.
                 */
                OglofusRadiusConfig() {
                }

                /**
                 * Gets values.
                 *
                 * @return the values
                 */
                @Override
                public Collection<Value<Integer>> getValues() {
                    return values.values();
                }

                /**
                 * Gets value.
                 *
                 * @param id the id
                 * @return the value
                 */
                @Override
                public Optional<Value<Integer>> getValue(String id) {
                    return Optional.ofNullable(values.getOrDefault(id, null));
                }

                /**
                 * Append value value.
                 *
                 * @param value the value
                 * @return the value
                 */
                @Override
                public Value<Integer> appendValue(Value<Integer> value) {
                    ConfigurationSection section = getConfig().createSection("region.radius." + value.getName());

                    if (value.isDefault()) {
                        section.set("default", true);
                    }

                    value.getPermission().ifPresent(s -> section.set("permission", s));

                    section.set("value", value.getValue());

                    return values.put(value.getName(), value);
                }

                /**
                 * Remove value value.
                 *
                 * @param id the id
                 * @return the value
                 */
                @Override
                public Value<Integer> removeValue(String id) {
                    if (getConfig().contains("region.radius." + id)) {
                        getConfig().set("region.radius." + id, null);
                    }

                    return values.remove(id);
                }

                /**
                 * Reload.
                 */
                private void reload() {
                    this.reload(getConfig().getConfigurationSection("region.radius"));
                }
            }
        }
    }
}
