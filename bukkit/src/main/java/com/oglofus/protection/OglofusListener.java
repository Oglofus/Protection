package com.oglofus.protection;

import com.oglofus.protection.api.ProtectionFlags;
import com.oglofus.protection.api.configuration.region.FlagEntity;
import com.oglofus.protection.api.event.*;
import com.oglofus.protection.api.protector.Protector;
import com.oglofus.protection.api.protector.region.Region;
import com.oglofus.protection.api.protector.staff.Rank;
import com.oglofus.protection.api.reguest.Request;
import com.oglofus.protection.api.value.Value;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 19/05/2017.
 */
@SuppressWarnings("unused")
class OglofusListener implements Listener {
    /**
     * The Plugin.
     */
    private final OglofusPlugin plugin;

    /**
     * Instantiates a new Oglofus listener.
     *
     * @param plugin the plugin
     */
    OglofusListener(OglofusPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * On player chat.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage();

        Optional<Request> requestOptional = plugin.getRequests().getRequest(player.getUniqueId());

        if (requestOptional.isPresent()) {
            event.setCancelled(true);

            Request request = requestOptional.get();
            Player target = request.getTarget();
            Protector protector = request.getProtector();
            Request.Type type = request.getType();

            if (!(protector.getStaff().isOwner(player)
                    || player.hasPermission("oglofus.protection.bypass." + type.name().toLowerCase()))) {
                player.sendMessage(ChatColor.RED + "You don't have the right to "
                        + type.name().toLowerCase() + " this player.");
                plugin.getRequests().removeValue(player.getUniqueId());

                return;
            }

            if (!protector.getStaff().isStaff(target)) {
                player.sendMessage(ChatColor.RED + "The player must be a member of the region to "
                        + type.name().toLowerCase() + " him.");

                return;
            }

            try {
                if (OglofusUtils.toBoolean(message)) {
                    Rank rank = null;

                    switch (request.getType()) {
                        case Demote: {
                            if (!protector.getStaff().isOwner(target)) {
                                player.spigot().sendMessage(new ComponentBuilder(
                                        "Just kick him. Don't play with the commands,  is dangerous.")
                                        .color(ChatColor.RED)
                                        .create());

                                return;
                            }

                            rank = Rank.Member;

                            break;
                        }
                        case Promote: {
                            if (protector.getStaff().isOwner(target)) {
                                player.spigot().sendMessage(
                                        new ComponentBuilder("You make him GOD. Well done. ;)")
                                                .color(ChatColor.RED)
                                                .create());

                                return;
                            }

                            rank = Rank.Owner;
                            break;
                        }
                    }

                    RankChangingEvent changingEvent = new RankChangingEvent(
                            protector,
                            player,
                            target,
                            protector.getStaff().getRank(target),
                            rank
                    );

                    Bukkit.getPluginManager().callEvent(changingEvent);

                    if (changingEvent.isCancelled()) {
                        return;
                    }

                    protector.getStaff().setRank(target, changingEvent.getRank());

                    RankChangedEvent changedEvent = new RankChangedEvent(
                            protector,
                            player,
                            target,
                            changingEvent.getFrom(),
                            changingEvent.getRank(),
                            new ComponentBuilder("You have successfully changed " + target.getName() + "'s " +
                                    "rank to " + changingEvent.getRank().name() + ".")
                                    .color(ChatColor.GREEN)
                                    .create()
                    );

                    Bukkit.getPluginManager().callEvent(changedEvent);

                    BaseComponent[] baseComponents = changedEvent.getMessage();
                    if (baseComponents != null) {
                        player.spigot().sendMessage(baseComponents);
                    }
                } else {
                    player.spigot().sendMessage(new ComponentBuilder("Whatever you want.")
                            .color(ChatColor.GREEN)
                            .create());
                }

                plugin.getRequests().removeValue(player.getUniqueId());
            } catch (IllegalArgumentException e) {
                player.spigot().sendMessage(new ComponentBuilder("Not a yes/no value: " + message)
                        .color(ChatColor.RED)
                        .create());
            }
        }
    }

    /**
     * On protector destroyed.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onProtectorDestroyed(ProtectorDestroyedEvent event) {
        Protector protector = event.getProtector();
        Region region = protector.getRegion();
        BlockVector3 vector = region.getCenter();
        World world = region.getWorld();

        org.bukkit.World world1 = Bukkit.getWorld(world.getName());

        if (world1 == null) {
            plugin.getLogger().warning("Something got wrong with matching world '" + world.getName() + "'.");

            return;
        }

        Block block = world1.getBlockAt(vector.getX(), vector.getY(), vector.getZ());

        block.setType(Material.AIR);

        if (event.getSender().getGameMode().equals(GameMode.SURVIVAL)) {
            block.getWorld().dropItem(
                    event.getSender().getLocation(),
                    plugin.createProtector(1)
            );
        }
    }

    /**
     * On protector destroying.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onProtectorDestroying(ProtectorDestroyingEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getSender();
        Protector protector = event.getProtector();

        if (!(protector.getStaff().isOwner(player)
                || player.hasPermission("oglofus.protection.bypass.destroy"))) {
            player.spigot().sendMessage(new ComponentBuilder("You don't have the right to do that.")
                    .color(ChatColor.RED)
                    .create());

            event.setCancelled(true);
        }
    }

    /**
     * On protector creating.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProtectorCreating(ProtectorPreCreateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        RegionManager manager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(event.getWorld());

        if (OglofusUtils.hasRegions(manager, event.getRegion())) {
            event.getSender().spigot().sendMessage(
                    new ComponentBuilder("Sorry but there is a region near to you.")
                            .color(ChatColor.RED)
                            .create()
            );

            event.setCancelled(true);
        }
    }

    /**
     * On protector created.
     *
     * @param event the event
     * @throws EventException the event exception
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProtectorCreated(ProtectorCreatedEvent event) throws EventException {
        ProtectedRegion region = event.getProtector().getRegion().getRegion();
        CommandSender sender = event.getSender();

        for (FlagEntity flag : plugin.getConfiguration().getRegion().getFlags().getDefaults()) {
            setFlag(flag, region, sender);
        }

        for (FlagEntity flag : plugin.getConfiguration().getRegion().getFlags().byPermission(sender)) {
            setFlag(flag, region, sender);
        }
    }

    /**
     * Sets flag.
     *
     * @param flagEntity the flag entity
     * @param region     the region
     * @param sender     the sender
     * @throws EventException the event exception
     */
    private void setFlag(FlagEntity flagEntity, ProtectedRegion region, CommandSender sender) throws EventException {
        Flag flag = Flags.fuzzyMatchFlag(
                WorldGuard.getInstance().getFlagRegistry(),
                flagEntity.getName());

        FlagContext context = FlagContext.create()
                .setSender(WorldGuardPlugin.inst().wrapCommandSender(sender))
                .setInput(flagEntity.getValue())
                .build();

        if (context == null) {
            throw new EventException("Region flag '" + flagEntity.getName()
                    + "' value '" + flagEntity.getValue() + "' is invalid");
        }

        try {
            //noinspection unchecked
            region.setFlag(flag, flag.parseInput(context));
        } catch (InvalidFlagFormat invalidFlagFormat) {
            invalidFlagFormat.printStackTrace();

            throw new EventException("Region flag '" + flagEntity.getName()
                    + "' value '" + flagEntity.getValue() + "' is invalid");
        }

        Optional<String> groupOptional = flagEntity.getGroup();

        if (groupOptional.isPresent()) {
            String group = groupOptional.get();
            RegionGroupFlag groupFlag = flag.getRegionGroupFlag();

            if (groupFlag == null) {
                throw new EventException("Region flag '" + flagEntity.getName()
                        + "' does not have a group flag!");
            }

            switch (group.toLowerCase()) {
                case "non_members":
                case "nonmembers": {
                    region.setFlag(groupFlag, RegionGroup.NON_MEMBERS);

                    break;
                }
                case "non_owners":
                case "nonowners": {
                    region.setFlag(groupFlag, RegionGroup.NON_OWNERS);

                    break;
                }
                case "all": {
                    region.setFlag(groupFlag, RegionGroup.ALL);

                    break;
                }
                case "members": {
                    region.setFlag(groupFlag, RegionGroup.MEMBERS);

                    break;
                }
                case "owners": {
                    region.setFlag(groupFlag, RegionGroup.OWNERS);

                    break;
                }
                case "none": {
                    region.setFlag(groupFlag, RegionGroup.NONE);

                    break;
                }
                default: {
                    throw new EventException("The flag " + flagEntity.getName() + " group is invalid.");
                }
            }
        }
    }

    /**
     * Check limits.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void checkLimits(ProtectorPreCreateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getSender();

        RegionManager manager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(event.getWorld());

        if (manager == null) {
            player.spigot().sendMessage(new ComponentBuilder(
                    "There is not region manager for this world.")
                    .color(ChatColor.RED)
                    .create());

            event.setCancelled(true);

            return;
        }

        Optional<Value<Integer>> def = plugin.getConfiguration().getRegion().getLimits().getDefault();

        if (!def.isPresent()) {
            player.spigot().sendMessage(new ComponentBuilder(
                    "The default limit value is not exists. Send this message to Owner of this server.")
                    .color(ChatColor.RED)
                    .create());

            event.setCancelled(true);

            return;
        }

        int limit = def.get().getValue();

        List<Value<Integer>> perm = plugin.getConfiguration()
                .getRegion()
                .getLimits()
                .getValues()
                .parallelStream()
                .filter(value -> value.getPermission().isPresent()
                        && player.hasPermission(value.getPermission().get()))
                .collect(Collectors.toList());

        for (Value<Integer> value : perm) {
            if (value.getValue() > limit) {
                limit = value.getValue();
            }
        }

        if (!(manager.getRegionCountOfPlayer(WorldGuardPlugin.inst().wrapPlayer(player)) < limit
                || player.hasPermission("oglofus.protection.bypass.limit"))) {
            player.spigot().sendMessage(
                    new ComponentBuilder("Sorry but you have already touched the limit.")
                            .color(ChatColor.RED)
                            .create()
            );

            event.setCancelled(true);
        }
    }

    /**
     * Create protector.
     *
     * @param event the event
     */
    @EventHandler
    public void createProtector(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
        Location location = block.getLocation();
        org.bukkit.World bukkitWorld = block.getWorld();
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(bukkitWorld.getName());

        if (world == null) {
            plugin.getLogger().warning("Something got wrong with matching world '" + bukkitWorld.getName() + "'.");

            return;
        }

        ItemStack stack = event.getItemInHand();

        if (stack.getItemMeta() != null
                && stack.getItemMeta().getLore() != null
                && stack.getItemMeta().getLore().contains(
                plugin.getConfiguration().getProtector().getMetadata())) {
            Optional<Value<Integer>> def = plugin.getConfiguration().getRegion().getRadius().getDefault();

            if (!def.isPresent()) {
                player.spigot().sendMessage(new ComponentBuilder(
                        "The default radius value is not exists. Send this message to Owner of this server.")
                        .color(ChatColor.RED)
                        .create());

                event.setCancelled(true);

                return;
            }

            int radius = def.get().getValue();

            List<Value<Integer>> perm = plugin.getConfiguration()
                    .getRegion()
                    .getRadius()
                    .getValues()
                    .parallelStream()
                    .filter(value -> value.getPermission().isPresent()
                            && (player.hasPermission(value.getPermission().get())
                            || player.hasPermission("oglofus.protection.bypass.radius")))
                    .collect(Collectors.toList());

            for (Value<Integer> value : perm) {
                if (value.getValue() > radius) {
                    radius = value.getValue();
                }
            }

            RegionManager manager = WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(world);

            if (manager == null) {
                player.spigot().sendMessage(new ComponentBuilder(
                        "There is not region manager for this world.")
                        .color(ChatColor.RED)
                        .create());

                return;
            }

            BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            CuboidRegion cuboidRegion = CuboidRegion.fromCenter(vector, radius);

            ProtectorPreCreateEvent protectorPreCreateEvent = new ProtectorPreCreateEvent(world, player, cuboidRegion);

            Bukkit.getPluginManager().callEvent(protectorPreCreateEvent);

            if (protectorPreCreateEvent.isCancelled()) {
                event.setCancelled(true);

                return;
            }

            ProtectedRegion region = new ProtectedCuboidRegion(
                    OglofusUtils.generateId(manager),
                    cuboidRegion.getPos1(),
                    cuboidRegion.getPos2()
            );

            region.setFlag(ProtectionFlags.PROTECTOR_FLAG, vector.toVector3());
            region.setFlag(ProtectionFlags.RADIUS_FLAG, radius);
            region.setFlag(ProtectionFlags.CREATOR, player.getUniqueId());
            region.setFlag(ProtectionFlags.CREATED, new Date().getTime());
            region.getOwners().addPlayer(player.getUniqueId());

            Protector protector = new OglofusProtector(
                    plugin,
                    region,
                    world
            );

            //execute the handler before...
            ProtectorCreatingEvent creatingEvent = new ProtectorCreatingEvent(protector, player);

            Bukkit.getPluginManager().callEvent(creatingEvent);

            if (creatingEvent.isCancelled()) {
                event.setCancelled(true);

                return;
            }

            plugin.getProtectors().registerProtector(protector);

            protector.getEffects().startBorder();
            protector.getEffects().startCreating();

            ProtectorCreatedEvent createdEvent = new ProtectorCreatedEvent(protector, player,
                    new ComponentBuilder("You have successfully created your region.")
                            .color(ChatColor.GREEN)
                            .create());

            //execute the handler after
            Bukkit.getPluginManager().callEvent(createdEvent);

            BaseComponent[] message = createdEvent.getMessage();

            if (message != null) {
                player.spigot().sendMessage(message);
            }
        }
    }
}
