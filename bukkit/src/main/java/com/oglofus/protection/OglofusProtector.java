package com.oglofus.protection;

import com.google.common.collect.Lists;
import com.oglofus.protection.api.ProtectionFlags;
import com.oglofus.protection.api.event.ProtectorDestroyedEvent;
import com.oglofus.protection.api.event.ProtectorDestroyingEvent;
import com.oglofus.protection.api.protector.Protector;
import com.oglofus.protection.api.protector.region.Effects;
import com.oglofus.protection.api.protector.region.Region;
import com.oglofus.protection.api.protector.staff.Rank;
import com.oglofus.protection.api.protector.staff.Staff;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

//import org.bukkit.*;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 19/05/2017.
 */
class OglofusProtector implements Protector {
    /**
     * The Plugin.
     */
    private final OglofusPlugin plugin;
    /**
     * The Pregion.
     */
    private final ProtectedRegion pregion;
    /**
     * The Effects.
     */
    private final OglofusEffects effects;
    /**
     * The Region.
     */
    private final OglofusRegion region;
    /**
     * The Staff.
     */
    private final OglofusStaff staff;
    /**
     * The World.
     */
    private final World world;
    /**
     * The Created.
     */
    private final Date created;
    /**
     * The Creator.
     */
    private final OfflinePlayer creator;

    /**
     * Instantiates a new Oglofus protector.
     *
     * @param plugin the plugin
     * @param region the region
     * @param world  the world
     */
    OglofusProtector(OglofusPlugin plugin, ProtectedRegion region, World world) {
        this.plugin = plugin;
        this.pregion = region;
        this.world = world;
        this.effects = new OglofusEffects();
        this.region = new OglofusRegion();
        this.staff = new OglofusStaff();
        this.creator = Bukkit.getOfflinePlayer(Objects.requireNonNull(region.getFlag(ProtectionFlags.CREATOR)));

        Long created = region.getFlag(ProtectionFlags.CREATED);

        if (created != null) {
            this.created = new Date(created);
        } else {
            this.created = new Date();

            region.setFlag(ProtectionFlags.CREATED, this.created.getTime());
        }

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Override
    public String getId() {
        return pregion.getId();
    }

    /**
     * Gets staff.
     *
     * @return the staff
     */
    @Override
    public Staff getStaff() {
        return staff;
    }

    /**
     * Gets region.
     *
     * @return the region
     */
    @Override
    public Region getRegion() {
        return region;
    }

    /**
     * Gets effects.
     *
     * @return the effects
     */
    @Override
    public Effects getEffects() {
        return effects;
    }

    /**
     * Gets created.
     *
     * @return the created
     */
    @Override
    public Date getCreated() {
        return created;
    }

    /**
     * Gets creator.
     *
     * @return the creator
     */
    @Override
    public OfflinePlayer getCreator() {
        return creator;
    }

    /**
     * Secure.
     *
     * @param event the event
     */
    @EventHandler
    public void secure(BlockBurnEvent event) {
        if (isCenter(event)) event.setCancelled(true);
    }

    /**
     * Secure.
     *
     * @param event the event
     */
    @EventHandler
    public void secure(BlockFadeEvent event) {
        if (isCenter(event)) event.setCancelled(true);
    }

    /**
     * Secure.
     *
     * @param event the event
     */
    @EventHandler
    public void secure(BlockGrowEvent event) {
        if (isCenter(event)) event.setCancelled(true);
    }

    /**
     * Secure.
     *
     * @param event the event
     */
    @EventHandler
    public void secure(BlockDispenseEvent event) {
        if (isCenter(event)) event.setCancelled(true);
    }

    /**
     * Secure.
     *
     * @param event the event
     */
    @EventHandler
    public void secure(BlockExplodeEvent event) {
        if (isCenter(event)) event.setCancelled(true);
    }

    /**
     * Secure.
     *
     * @param event the event
     */
    @EventHandler
    public void secure(BlockIgniteEvent event) {
        if (isCenter(event)) event.setCancelled(true);
    }

    /**
     * Secure.
     *
     * @param event the event
     */
    @EventHandler
    public void secure(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (isCenter(block)) {
                event.setCancelled(true);

                return;
            }
        }
    }

    /**
     * Secure.
     *
     * @param event the event
     */
    @EventHandler
    public void secure(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (isCenter(block)) {
                event.setCancelled(true);

                return;
            }
        }
    }

    /**
     * Destroy.
     *
     * @param event the event
     */
    @EventHandler
    public void destroy(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (isCenter(event)) {
            event.setCancelled(true);

            ProtectorDestroyingEvent destroyingEvent = new ProtectorDestroyingEvent(
                    this,
                    player
            );

            Bukkit.getPluginManager().callEvent(destroyingEvent);

            if (destroyingEvent.isCancelled()) {
                return;
            }

            plugin.getProtectors().removeProtector(this);

            ProtectorDestroyedEvent destroyedEvent = new ProtectorDestroyedEvent(this, player,
                    new ComponentBuilder("You have successfully destroyed this region.")
                            .color(ChatColor.GREEN)
                            .create());

            Bukkit.getPluginManager().callEvent(destroyedEvent);

            if (destroyedEvent.getMessage() != null) {
                player.spigot().sendMessage(destroyedEvent.getMessage());
            }
        }
    }

    /**
     * Is center boolean.
     *
     * @param event the event
     * @return the boolean
     */
    private boolean isCenter(BlockEvent event) {
        return isCenter(event.getBlock());
    }

    /**
     * Is center boolean.
     *
     * @param block the block
     * @return the boolean
     */
    private boolean isCenter(Block block) {
        return isCenter(BlockVector3.at(block.getX(), block.getY(), block.getZ()));
    }

    /**
     * Is center boolean.
     *
     * @param location the location
     * @return the boolean
     */
    private boolean isCenter(BlockVector3 location) {
        return region.center.equals(BlockVector3.at(
                location.getX(),
                location.getY(),
                location.getZ()
        ));
    }

    /**
     * The type Oglofus staff.
     */
    class OglofusStaff implements Staff {
        /**
         * Gets rank.
         *
         * @param player the player
         * @return the rank
         */
        @Override
        public Rank getRank(OfflinePlayer player) {
            return pregion.getOwners().contains(player.getUniqueId()) ? Rank.Owner : (
                    pregion.getMembers().contains(player.getUniqueId()) ? Rank.Member : Rank.None
            );
        }

        /**
         * Sets rank.
         *
         * @param player the player
         * @param rank   the rank
         */
        @Override
        public void setRank(OfflinePlayer player, Rank rank) {
            Rank target = getRank(player);

            switch (rank) {
                case Owner:
                    if (target == Rank.Member) {
                        pregion.getMembers().removePlayer(player.getUniqueId());
                    }
                    pregion.getOwners().addPlayer(player.getUniqueId());
                    break;
                case Member:
                    if (target == Rank.Owner) {
                        pregion.getOwners().removePlayer(player.getUniqueId());
                    }
                    pregion.getMembers().addPlayer(player.getUniqueId());
                    break;
                case None:
                    if (target == Rank.Member) {
                        pregion.getMembers().removePlayer(player.getUniqueId());
                    } else if (target == Rank.Owner) {
                        pregion.getOwners().removePlayer(player.getUniqueId());
                    }
                    break;
            }
        }

        /**
         * Gets owners.
         *
         * @return the owners
         */
        @Override
        public Collection<OfflinePlayer> getOwners() {
            return pregion.getOwners().getUniqueIds()
                    .parallelStream()
                    .map(Bukkit::getPlayer)
                    .collect(Collectors.toList());
        }

        /**
         * Gets members.
         *
         * @return the members
         */
        @Override
        public Collection<OfflinePlayer> getMembers() {
            return pregion.getMembers().getUniqueIds()
                    .parallelStream()
                    .map(Bukkit::getPlayer)
                    .collect(Collectors.toList());
        }
    }

    /**
     * The type Oglofus region.
     */
    class OglofusRegion implements Region {
        /**
         * The Vectors.
         */
        private final CuboidRegion vectors;
        /**
         * The Center.
         */
        private final BlockVector3 center;
        /**
         * The Radius.
         */
        private final Integer radius;
        /**
         * The Small.
         */
        private final CuboidRegion small;

        /**
         * Instantiates a new Oglofus region.
         */
        @SuppressWarnings("all")
        OglofusRegion() {
            this.vectors = new CuboidRegion(pregion.getMinimumPoint(), pregion.getMaximumPoint());
            this.center = pregion.getFlag(ProtectionFlags.PROTECTOR_FLAG).toBlockPoint();
            this.radius = pregion.getFlag(ProtectionFlags.RADIUS_FLAG);
            this.small = CuboidRegion.fromCenter(this.center, this.radius);
        }

        /**
         * Gets world.
         *
         * @return the world
         */
        @Override
        public World getWorld() {
            return world;
        }

        /**
         * Gets radius.
         *
         * @return the radius
         */
        @Override
        public Integer getRadius() {
            return radius;
        }

        /**
         * Gets center.
         *
         * @return the center
         */
        @Override
        public BlockVector3 getCenter() {
            return center;
        }

        /**
         * Gets small.
         *
         * @return the small
         */
        @Override
        public CuboidRegion getSmall() {
            return small;
        }

        /**
         * Gets vectors.
         *
         * @return the vectors
         */
        @Override
        public CuboidRegion getVectors() {
            return vectors;
        }

        /**
         * Gets region.
         *
         * @return the region
         */
        @Override
        public ProtectedRegion getRegion() {
            return pregion;
        }
    }

    /**
     * The type Oglofus effects.
     */
    class OglofusEffects implements Effects {
        /**
         * The Effects.
         */
        private final List<BukkitTask> effects = Lists.newArrayList();
        /**
         * The Refresher.
         */
        private final BukkitTask refresher;
        /**
         * The Enabled.
         */
        private boolean enabled;

        /**
         * Instantiates a new Oglofus effects.
         */
        OglofusEffects() {
            Boolean allowed = pregion.getFlag(ProtectionFlags.EFFECTS);

            if (allowed == null) {
                pregion.setFlag(ProtectionFlags.EFFECTS, true);

                this.enabled = true;
            } else {
                this.enabled = allowed;
            }

            this.refresher = Bukkit
                    .getScheduler()
                    .runTaskTimerAsynchronously(plugin, this::refresh, 0, 6000);
        }

        /**
         * Is enabled boolean.
         *
         * @return the boolean
         */
        @Override
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Start creating.
         */
        @Override
        @SuppressWarnings("Duplicates")
        public void startCreating() {
            if (enabled) {
                org.bukkit.World world1 = Bukkit.getWorld(world.getName());

                region.getSmall().forEach(vector -> {
                    Location location = new Location(
                            world1,
                            vector.getX() + 0.5,
                            vector.getY(),
                            vector.getZ() + 0.5
                    );

                    if (!location.getBlock().getType().equals(Material.AIR)) {
                        return;
                    }

                    if (location.clone().subtract(0, 5, 0).getBlock().getType().equals(Material.AIR)) {
                        return;
                    }

                    Bukkit.getScheduler().runTaskLaterAsynchronously(
                            plugin,
                            () -> Objects.requireNonNull(world1).spawnParticle(Particle.CLOUD, location, 1),
                            ThreadLocalRandom.current().nextInt(0, 100 + 1));
                });
            }
        }

        /**
         * Start destroying.
         */
        @Override
        @SuppressWarnings("Duplicates")
        public void startDestroying() {
            if (enabled) {
                org.bukkit.World world1 = Bukkit.getWorld(world.getName());

                region.getSmall().forEach(vector -> {
                    Location location = new Location(
                            world1,
                            vector.getX() + 0.5,
                            vector.getY(),
                            vector.getZ() + 0.5
                    );

                    if (!location.getBlock().getType().equals(Material.AIR)) {
                        return;
                    }

                    if (location.clone().subtract(0, 5, 0).getBlock().getType().equals(Material.AIR)) {
                        return;
                    }

                    Bukkit.getScheduler().runTaskLaterAsynchronously(
                            plugin,
                            () -> Objects.requireNonNull(world1).spawnParticle(Particle.FLAME, location, 1),
                            ThreadLocalRandom.current().nextInt(0, 100 + 1));
                });
            }
        }

        /**
         * Start border.
         */
        @Override
        public void startBorder() {
            if (enabled) {
                org.bukkit.World world1 = Bukkit.getWorld(world.getName());

                region.getSmall().getWalls().forEach(vector -> {
                    Location location = new Location(
                            world1,
                            vector.getX() + 0.5,
                            vector.getY(),
                            vector.getZ() + 0.5
                    );

                    if (!location.getBlock().getType().equals(Material.AIR)) {
                        return;
                    }

                    if (location.clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                        return;
                    }

                    effects.add(Bukkit.getScheduler().runTaskTimerAsynchronously(
                            plugin,
                            () -> Objects.requireNonNull(world1).spawnParticle(Particle.VILLAGER_HAPPY, location, 1),
                            0,
                            100));
                });
            }
        }

        /**
         * Cancel.
         */
        @Override
        public void cancel() {
            effects.forEach(BukkitTask::cancel);
        }

        /**
         * Refresh.
         */
        @Override
        public void refresh() {
            cancel();
            startBorder();
        }

        /**
         * Sets enable.
         *
         * @param enable the enable
         */
        @Override
        public void setEnable(boolean enable) {
            this.enabled = enable;

            pregion.setFlag(ProtectionFlags.EFFECTS, enable);

            if (!enable) {
                cancel();
            } else {
                startBorder();
            }
        }

        /**
         * Gets refresher.
         *
         * @return the refresher
         */
        @Override
        public BukkitTask getRefresher() {
            return refresher;
        }
    }
}
