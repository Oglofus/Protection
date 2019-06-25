package com.oglofus.protection;

import com.google.common.collect.Lists;
import com.oglofus.protection.api.configuration.ConfigurationService;
import com.oglofus.protection.api.configuration.ConfigurationStatus;
import com.oglofus.protection.api.event.RankChangedEvent;
import com.oglofus.protection.api.event.RankChangingEvent;
import com.oglofus.protection.api.protector.Protector;
import com.oglofus.protection.api.protector.staff.Rank;
import com.oglofus.protection.api.reguest.Request;
import com.sk89q.worldedit.math.BlockVector3;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 28/05/2017.
 */
@SuppressWarnings("DuplicateBranchesInSwitch")
public class OglofusCommand implements CommandExecutor, TabExecutor {
    /**
     * The Plugin.
     */
    private final OglofusPlugin plugin;

    /**
     * Instantiates a new Oglofus command.
     *
     * @param plugin the plugin
     */
    @SuppressWarnings("WeakerAccess")
    public OglofusCommand(OglofusPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the given command, returning its success
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        testPermission(sender, command, "oglofus.protection.command");

        int length = args.length;

        if (length < 1) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "version":
            case "ver":
            case "v": {
                testPermission(sender, command, "oglofus.protection.command.version");

                TextComponent version = new TextComponent(plugin.getDescription().getName() +
                        " v" + plugin.getDescription().getVersion());
                version.setColor(ChatColor.YELLOW);
                version.addExtra("\n");

                TextComponent author = new TextComponent("Created by ");
                author.setColor(ChatColor.YELLOW);

                TextComponent name = new TextComponent("Nikolaos Grammatikos");
                name.setColor(ChatColor.RESET);
                name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to open my portfolio")
                                .color(ChatColor.RED)
                                .create()));
                name.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://nikosgram.oglofus.com/"));

                author.addExtra(name);

                version.addExtra(author);

                OglofusUtils.sendMessage(sender, version);
                return true;
            }
            case "help":
            case "h":
            case "?": {
                testPermission(sender, command, "oglofus.protection.command.help");

                return onHelper(sender, command, label, args);
            }
            case "give":
            case "get": {
                testPermission(sender, command, "oglofus.protection.command.give");

                int quantity = 1;
                Player target;

                if (length >= 2) {
                    quantity = getInteger("quantity", args[1]);
                }

                if (length >= 3) {
                    target = getPlayer(args[2]);
                } else {
                    target = senderAsPlayer(sender);
                }

                boolean free = false;

                for (ItemStack itemStack : target.getInventory().getStorageContents()) {
                    if (itemStack == null) {
                        free = true;

                        break;
                    }
                }

                ItemStack stack = plugin.createProtector(quantity);

                if (free) {
                    target.getInventory().addItem(stack);

                    OglofusUtils.sendMessage(sender, new ComponentBuilder("Check your inventory. :D")
                            .color(ChatColor.GREEN)
                            .create());
                } else {

                    target.getWorld().dropItem(target.getLocation().add(0, 1, 0), stack);

                    OglofusUtils.sendMessage(sender, new ComponentBuilder("Ohh, I drop it. Look around. :D")
                            .color(ChatColor.GREEN)
                            .create());
                }

                return true;
            }
            case "information":
            case "info":
            case "i":
            case "!": {
                testPermission(sender, command, "oglofus.protection.command.info");

                OglofusUtils.sendMessage(sender, toMessage(getProtector(sender, length >= 2 ? args[1] : null)));

                return true;
            }
            case "append":
            case "invite":
            case "add":
            case "+": {
                testPermission(sender, command, "oglofus.protection.command.append");

                if (length < 2) {
                    return false;
                }

                Player target = getPlayer(args[1]);

                Protector protector = getProtector(sender, length >= 3 ? args[2] : null);

                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (target.getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.BLUE + "U " + ChatColor.GOLD + "mad " +
                                ChatColor.DARK_AQUA + "bro? " +
                                ChatColor.RED + "Of course, you can't append yourself.");

                        return true;
                    }

                    if (!(protector.getStaff().isOwner(player)
                            || player.hasPermission("oglofus.protection.bypass.append"))) {
                        player.spigot().sendMessage(
                                new ComponentBuilder("You don't have the right to append this player.")
                                        .color(ChatColor.RED)
                                        .create());

                        return true;
                    }
                }

                if (protector.getStaff().isStaff(target)) {
                    OglofusUtils.sendMessage(sender,
                            new ComponentBuilder("The target player is already a member of this region.")
                                    .color(ChatColor.RED)
                                    .create());

                    return true;
                }

                RankChangingEvent changingEvent = new RankChangingEvent(
                        protector,
                        sender,
                        target,
                        protector.getStaff().getRank(target),
                        Rank.Member
                );

                Bukkit.getPluginManager().callEvent(changingEvent);

                if (changingEvent.isCancelled()) {
                    return true;
                }

                protector.getStaff().setRank(target, changingEvent.getRank());

                RankChangedEvent changedEvent = new RankChangedEvent(
                        protector,
                        sender,
                        target,
                        changingEvent.getFrom(),
                        changingEvent.getRank(),
                        new ComponentBuilder("You have successfully appended " + target.getName() + " to the region.")
                                .color(ChatColor.GREEN)
                                .create()
                );

                Bukkit.getPluginManager().callEvent(changedEvent);

                BaseComponent[] message = changedEvent.getMessage();

                if (message != null) {
                    OglofusUtils.sendMessage(sender, message);
                }

                return true;
            }
            case "remove":
            case "kick":
            case "-": {
                testPermission(sender, command, "oglofus.protection.command.remove");

                if (length < 2) {
                    return false;
                }

                Player target = getPlayer(args[1]);

                Protector protector = getProtector(sender, length >= 3 ? args[2] : null);

                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (target.getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.BLUE + "U " + ChatColor.GOLD + "mad " +
                                ChatColor.DARK_AQUA + "bro? " +
                                ChatColor.RED + "Of course, you can't remove yourself.");

                        return true;
                    }

                    if (!(protector.getStaff().isOwner(player)
                            || player.hasPermission("oglofus.protection.bypass.remove"))) {
                        player.spigot().sendMessage(
                                new ComponentBuilder("You don't have the right to append this player.")
                                        .color(ChatColor.RED)
                                        .create());

                        return true;
                    }
                }

                if (protector.getStaff().isOwner(target) && protector.getStaff().getOwners().size() == 1) {
                    OglofusUtils.sendMessage(sender, new ComponentBuilder("You can't kick the last owner. " +
                            "You must destroy the protection block.")
                            .color(ChatColor.RED)
                            .create());

                    return true;
                }

                if (!protector.getStaff().isStaff(target)) {
                    OglofusUtils.sendMessage(sender,
                            new ComponentBuilder("The player must be a member of the region to remove him.")
                                    .color(ChatColor.RED)
                                    .create());

                    return true;
                }

                RankChangingEvent changingEvent = new RankChangingEvent(
                        protector,
                        sender,
                        target,
                        protector.getStaff().getRank(target),
                        Rank.None
                );

                Bukkit.getPluginManager().callEvent(changingEvent);

                if (changingEvent.isCancelled()) {
                    return true;
                }

                protector.getStaff().setRank(target, changingEvent.getRank());

                RankChangedEvent changedEvent = new RankChangedEvent(
                        protector,
                        sender,
                        target,
                        changingEvent.getFrom(),
                        changingEvent.getRank(),
                        new ComponentBuilder("You have successfully removed " + target.getName() + " from the region.")
                                .color(ChatColor.GREEN)
                                .create()
                );

                Bukkit.getPluginManager().callEvent(changedEvent);

                BaseComponent[] message = changedEvent.getMessage();

                if (message != null) {
                    OglofusUtils.sendMessage(sender, message);
                }

                return true;
            }
            case "promote":
            case "makeowner":
            case "setowner":
            case "^": {
                testPermission(sender, command, "oglofus.protection.command.promote");

                if (length < 2) {
                    return false;
                }

                Player target = getPlayer(args[1]);

                Protector protector = getProtector(sender, length >= 3 ? args[2] : null);

                if (!protector.getStaff().isStaff(target)) {
                    OglofusUtils.sendMessage(sender,
                            new ComponentBuilder("The player must be a member of the region to promote him.")
                                    .color(ChatColor.RED)
                                    .create());

                    return true;
                }

                if (protector.getStaff().isOwner(target)) {
                    OglofusUtils.sendMessage(sender, new ComponentBuilder("You make him GOD. Well done. ;)")
                            .color(ChatColor.GREEN)
                            .create());

                    return true;
                }

                if (sender instanceof Player) {
                    sendRequest((Player) sender, target, protector, Request.Type.Promote);

                    return true;
                }

                RankChangingEvent changingEvent = new RankChangingEvent(
                        protector,
                        sender,
                        target,
                        protector.getStaff().getRank(target),
                        Rank.Owner
                );

                Bukkit.getPluginManager().callEvent(changingEvent);

                if (changingEvent.isCancelled()) {
                    return true;
                }

                protector.getStaff().setRank(target, changingEvent.getRank());

                return sendRankChangedEvent(sender, target, protector, changingEvent);
            }
            case "demote":
            case "makemember":
            case "setmember":
            case "*": {
                testPermission(sender, command, "oglofus.protection.command.demote");

                if (length < 2) {
                    return false;
                }

                Player target = getPlayer(args[1]);

                Protector protector = getProtector(sender, length >= 3 ? args[2] : null);

                if (!protector.getStaff().isStaff(target)) {
                    OglofusUtils.sendMessage(sender,
                            new ComponentBuilder("The player must be a member of the region to demote him.")
                                    .color(ChatColor.RED)
                                    .create());

                    return true;
                }

                if (!protector.getStaff().isOwner(target)) {
                    OglofusUtils.sendMessage(sender,
                            new ComponentBuilder("Just kick him. Don't play with the commands, is dangerous.")
                                    .color(ChatColor.RED)
                                    .create());

                    return true;
                }

                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (target.getUniqueId().equals(player.getUniqueId())) {
                        sender.sendMessage(ChatColor.BLUE + "U " + ChatColor.GOLD + "mad " +
                                ChatColor.DARK_AQUA + "bro? " +
                                ChatColor.RED + "Of course, you can't demote yourself.");

                        return true;
                    }

                    if (!(protector.getStaff().isOwner(player)
                            || sender.hasPermission("oglofus.protection.bypass.demote"))) {
                        OglofusUtils.sendMessage(sender,
                                new ComponentBuilder("You don't have the right to demote this player.")
                                        .color(ChatColor.RED)
                                        .create());

                        return true;
                    }

                    sendRequest((Player) sender, target, protector, Request.Type.Demote);

                    return true;
                }

                RankChangingEvent changingEvent = new RankChangingEvent(
                        protector,
                        sender,
                        target,
                        protector.getStaff().getRank(target),
                        Rank.Member
                );

                Bukkit.getPluginManager().callEvent(changingEvent);

                if (changingEvent.isCancelled()) {
                    return true;
                }

                protector.getStaff().setRank(target, changingEvent.getRank());

                return sendRankChangedEvent(sender, target, protector, changingEvent);
            }
            case "configuration":
            case "config":
            case "conf":
            case "c": {
                testPermission(sender, "oglofus.protection.command.config");

                if (length < 2) {
                    return false;
                }

                switch (args[1]) {
                    case "load":
                    case "l": {
                        testPermission(sender, "oglofus.protection.command.config.load");

                        if (plugin.getConfiguration().execute(ConfigurationService.Load)) {
                            OglofusUtils.sendMessage(sender, new ComponentBuilder(
                                    "You have successfully loaded the configuration file.")
                                    .color(ChatColor.GREEN)
                                    .create());
                        } else {
                            if (plugin.getConfiguration().getStatus().equals(ConfigurationStatus.Error)) {
                                OglofusUtils.sendMessage(sender, new ComponentBuilder(
                                        "Caused a problem during loading. Check the console's output.")
                                        .color(ChatColor.RED)
                                        .create());
                            } else if (plugin.getConfiguration().getStatus().equals(ConfigurationStatus.Loading) ||
                                    plugin.getConfiguration().getStatus().equals(ConfigurationStatus.Saving)) {
                                OglofusUtils.sendMessage(sender, new ComponentBuilder(
                                        "The configuration system is busy, please try again later.")
                                        .color(ChatColor.RED)
                                        .create());
                            } else {
                                OglofusUtils.sendMessage(sender, new ComponentBuilder(
                                        "Something happened... Check the console's output.")
                                        .color(ChatColor.RED)
                                        .create());
                            }
                        }

                        return true;
                    }
                    case "save":
                    case "s": {
                        testPermission(sender, "oglofus.protection.command.config.save");

                        if (plugin.getConfiguration().execute(ConfigurationService.Save)) {
                            OglofusUtils.sendMessage(sender, new ComponentBuilder(
                                    "You have successfully saved the configuration file.")
                                    .color(ChatColor.GREEN)
                                    .create());
                        } else {
                            if (plugin.getConfiguration().getStatus().equals(ConfigurationStatus.Error)) {
                                OglofusUtils.sendMessage(sender, new ComponentBuilder(
                                        "Caused a problem during saving. Check the console's output.")
                                        .color(ChatColor.RED)
                                        .create());
                            } else if (plugin.getConfiguration().getStatus().equals(ConfigurationStatus.Loading) ||
                                    plugin.getConfiguration().getStatus().equals(ConfigurationStatus.Saving)) {
                                OglofusUtils.sendMessage(sender, new ComponentBuilder(
                                        "The configuration system is busy, please try again later.")
                                        .color(ChatColor.RED)
                                        .create());
                            } else {
                                OglofusUtils.sendMessage(sender, new ComponentBuilder(
                                        "Something happened... Check the console's output.")
                                        .color(ChatColor.RED)
                                        .create());
                            }
                        }

                        return true;
                    }
                }

                return true;
            }
        }

        return false;
    }

    private boolean sendRankChangedEvent(CommandSender sender, Player target, Protector protector, RankChangingEvent changingEvent) {
        RankChangedEvent changedEvent = new RankChangedEvent(
                protector,
                sender,
                target,
                changingEvent.getFrom(),
                changingEvent.getRank(),
                new ComponentBuilder("You have successfully changed " + target.getName() + "'s " +
                        "rank to " + changingEvent.getRank().name() + ".")
                        .create()
        );

        Bukkit.getPluginManager().callEvent(changedEvent);

        BaseComponent[] message = changedEvent.getMessage();

        if (message != null) {
            OglofusUtils.sendMessage(sender, message);
        }

        return true;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a                command inside of a command
     *                block, this will be the player, not                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final                partial argument to be
     *                completed and command label
     * @return A List of possible completions for the final argument, or null to default to the command executor
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!testPermission(sender, "oglofus.protection.command")) {
            return Lists.newArrayList();
        }

        switch (args.length) {
            case 1: {
                List<String> returned = Lists.newArrayList();

                String arg = args[0].toLowerCase();

                if (testPermission(sender, "oglofus.protection.command.version")) {
                    if ("ver".startsWith(arg)) {
                        returned.add("ver");
                    }

                    if ("version".startsWith(arg)) {
                        returned.add("version");
                    }
                }

                if (testPermission(sender, "oglofus.protection.command.help")) {
                    if ("help".startsWith(arg)) {
                        returned.add("help");
                    }
                }

                if (testPermission(sender, "oglofus.protection.command.give")) {
                    if ("give".startsWith(arg)) {
                        returned.add("give");
                    }

                    if ("get".startsWith(arg)) {
                        returned.add("get");
                    }
                }

                if (testPermission(sender, "oglofus.protection.command.info")) {
                    if ("info".startsWith(arg)) {
                        returned.add("info");
                    }

                    if ("information".startsWith(arg)) {
                        returned.add("information");
                    }
                }

                if (testPermission(sender, "oglofus.protection.command.append")) {
                    if ("append".startsWith(arg)) {
                        returned.add("append");
                    }

                    if ("invite".startsWith(arg)) {
                        returned.add("invite");
                    }

                    if ("add".startsWith(arg)) {
                        returned.add("add");
                    }
                }

                if (testPermission(sender, "oglofus.protection.command.remove")) {
                    if ("remove".startsWith(arg)) {
                        returned.add("remove");
                    }

                    if ("kick".startsWith(arg)) {
                        returned.add("kick");
                    }
                }

                if (testPermission(sender, "oglofus.protection.command.promote")) {
                    if ("promote".startsWith(arg)) {
                        returned.add("promote");
                    }

                    if ("makeowner".startsWith(arg)) {
                        returned.add("makeowner");
                    }

                    if ("setowner".startsWith(arg)) {
                        returned.add("setowner");
                    }
                }

                if (testPermission(sender, "oglofus.protection.command.demote")) {
                    if ("demote".startsWith(arg)) {
                        returned.add("demote");
                    }

                    if ("makemember".startsWith(arg)) {
                        returned.add("makemember");
                    }

                    if ("setmember".startsWith(arg)) {
                        returned.add("setmember");
                    }
                }

                if (testPermission(sender, "oglofus.protection.command.config")) {
                    if ("conf".startsWith(arg)) {
                        returned.add("conf");
                    }

                    if ("config".startsWith(arg)) {
                        returned.add("config");
                    }

                    if ("configuration".startsWith(arg)) {
                        returned.add("configuration");
                    }
                }

                return returned;
            }
            case 2: {
                String arg = args[1].toLowerCase();

                switch (args[0].toLowerCase()) {
                    case "information":
                    case "info":
                    case "i":
                    case "!": {
                        return getProtectorByStaff(sender, arg);
                    }
                    case "append":
                    case "invite":
                    case "add":
                    case "+": {
                        return getPlayersByRank(sender, arg, Rank.None);
                    }
                    case "remove":
                    case "kick":
                    case "-": {
                        return getPlayersByRank(sender, arg, Rank.Member);
                    }
                    case "promote":
                    case "makeowner":
                    case "setowner":
                    case "^": {
                        return getPlayersByRank(sender, arg, Rank.Member);
                    }
                    case "demote":
                    case "makemember":
                    case "setmember":
                    case "*": {
                        return getPlayersByRank(sender, arg, Rank.Owner);
                    }
                    case "configuration":
                    case "config":
                    case "conf":
                    case "c": {
                        List<String> returned = Lists.newArrayList();

                        if (testPermission(sender, "oglofus.protection.command.config")) {
                            if (testPermission(sender, "oglofus.protection.command.config.load")) {
                                if ("load".startsWith(arg)) {
                                    returned.add("load");
                                }
                            }

                            if (testPermission(sender, "oglofus.protection.command.config.save")) {
                                if ("save".startsWith(arg)) {
                                    returned.add("save");
                                }
                            }
                        }

                        return returned;
                    }
                }

                return Lists.newArrayList();
            }
            case 3: {
                String arg = args[2].toLowerCase();

                switch (args[1].toLowerCase()) {
                    case "append":
                    case "invite":
                    case "add":
                    case "+": {
                        return getProtectorByStaff(sender, arg);
                    }
                    case "remove":
                    case "kick":
                    case "-": {
                        return getProtectorByStaff(sender, arg);
                    }
                    case "promote":
                    case "makeowner":
                    case "setowner":
                    case "^": {
                        return getProtectorByStaff(sender, arg);
                    }
                    case "demote":
                    case "makemember":
                    case "setmember":
                    case "*": {
                        return getProtectorByStaff(sender, arg);
                    }
                }

                return Lists.newArrayList();
            }
        }
        return Lists.newArrayList();
    }

    /**
     * Send request.
     *
     * @param sender    the sender
     * @param target    the target
     * @param protector the protector
     * @param type      the type
     */
    private void sendRequest(Player sender, Player target, Protector protector, Request.Type type) {
        if (target.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.BLUE + "U " + ChatColor.GOLD + "mad " +
                    ChatColor.DARK_AQUA + "bro? " +
                    ChatColor.RED + "Of course, you can't " + type.name().toLowerCase() + " yourself.");

            return;
        }

        if (!(protector.getStaff().isOwner(sender)
                || sender.hasPermission("oglofus.protection.bypass." + type.name().toLowerCase()))) {
            sender.sendMessage(ChatColor.RED + "You don't have the right to " +
                    type.name().toLowerCase() + " this player.");

            return;
        }

        plugin.getRequests()
                .appendRequest(Request.builder(sender)
                        .target(target)
                        .protector(protector)
                        .type(type)
                        .build());

        sender.sendMessage(ChatColor.GREEN + "Are you sure that you want to "
                + type.name().toLowerCase() + " this player?\n" +
                (type.equals(Request.Type.Promote) ? "The owner has the access to destroy this region.\n" : "") +
                ChatColor.GREEN + "If you sure about that, type yes, else type no.\n" +
                "You have 5 minutes.");
    }

    /**
     * On helper boolean.
     *
     * @param sender  the sender
     * @param command the command
     * @param label   the label
     * @param args    the args
     * @return the boolean
     */
    @SuppressWarnings("unused")
    private boolean onHelper(CommandSender sender, Command command, String label, String[] args) {
        TextComponent help = new TextComponent(
                "Commands of " + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion()
        );
        help.setColor(ChatColor.YELLOW);

        if (sender.hasPermission("oglofus.protection.command.version")) {
            help.addExtra("\n");

            help.addExtra(generateCommand(
                    "p version",
                    "Returns the version of this plugin",
                    "/p version"
            ));
        }

        if (sender.hasPermission("oglofus.protection.command.give")) {
            help.addExtra("\n");

            help.addExtra(generateCommand(
                    "p give [quantity]",
                    "Gives the Protection core",
                    "/p give 1"
            ));
        }

        if (sender.hasPermission("oglofus.protection.command.info")) {
            help.addExtra("\n");

            help.addExtra(generateCommand(
                    "p info [region]",
                    "Returns info about a region",
                    "/p info"
            ));
        }

        if (sender.hasPermission("oglofus.protection.command.append")) {
            help.addExtra("\n");

            help.addExtra(generateCommand(
                    "p invite <player> [region]",
                    "Invites a player to a region",
                    "/p invite player"
            ));
        }

        if (sender.hasPermission("oglofus.protection.command.remove")) {
            help.addExtra("\n");

            help.addExtra(generateCommand(
                    "p kick <player> [region]",
                    "Kicks a player from a region",
                    "/p kick player"
            ));
        }

        if (sender.hasPermission("oglofus.protection.command.promote")) {
            help.addExtra("\n");

            help.addExtra(generateCommand(
                    "p promote <player> [region]",
                    "Promotes a member to owner",
                    "/p promote player"
            ));
        }

        if (sender.hasPermission("oglofus.protection.command.demote")) {
            help.addExtra("\n");

            help.addExtra(generateCommand(
                    "p demote <player> [region]",
                    "Demotes a owner to member",
                    "/p demote player"
            ));
        }

        if (sender.hasPermission("oglofus.protection.command.config")) {
            if (sender.hasPermission("oglofus.protection.command.config.load")) {
                help.addExtra("\n");

                help.addExtra(generateCommand(
                        "p config load",
                        "Forced load config file",
                        "/p config load"
                ));
            }

            if (sender.hasPermission("oglofus.protection.command.save")) {
                help.addExtra("\n");

                help.addExtra(generateCommand(
                        "p config save",
                        "Forced save config file",
                        "/p config save"
                ));
            }
        }

        OglofusUtils.sendMessage(sender, help);

        return true;
    }

    /**
     * Generate command base component.
     *
     * @param command     the command
     * @param description the description
     * @param suggest     the suggest
     * @return the base component
     */
    private BaseComponent generateCommand(String command, String description, @Nullable String suggest) {
        TextComponent com = new TextComponent(command);
        com.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click to suggest the command")
                        .color(ChatColor.RED)
                        .create()));
        com.setClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                suggest != null ? suggest : command));
        com.setColor(ChatColor.RESET);

        TextComponent desc = new TextComponent(" - " + description);
        desc.setColor(ChatColor.GREEN);

        com.addExtra(desc);

        return com;
    }

    /**
     * Gets protector by staff.
     *
     * @param sender the sender
     * @param arg    the arg
     * @return the protector by staff
     */
    private List<String> getProtectorByStaff(CommandSender sender, String arg) {
        List<String> returned = Lists.newArrayList();

        for (Protector protector : plugin.getProtectors()) {
            if (sender instanceof Player) {
                if (!protector.getStaff().isStaff((Player) sender)) {
                    continue;
                }
            }

            if (protector.getId().startsWith(arg)) {
                returned.add(protector.getId());
            }
        }

        return returned;
    }

    /**
     * Gets players by rank.
     *
     * @param sender the sender
     * @param arg    the arg
     * @param rank   the rank
     * @return the players by rank
     */
    private List<String> getPlayersByRank(CommandSender sender, String arg, Rank rank) {
        List<String> returned = Lists.newArrayList();

        Protector protector = null;

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();

            Optional<Protector> optional = plugin.getProtectors().getProtector(BlockVector3.at(
                    location.getX(),
                    location.getY(),
                    location.getZ()
            ));

            if (optional.isPresent()) {
                protector = optional.get();
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (protector != null) {
                if (!protector.getStaff().getRank(player).equals(rank)) {
                    continue;
                }
            }

            if (player.getName().startsWith(arg)) {
                returned.add(player.getName());
            }
        }

        return returned;
    }

    /**
     * Test permission.
     *
     * @param sender     the sender
     * @param command    the command
     * @param permission the permission
     */
    private void testPermission(CommandSender sender, Command command, String permission) {
        if (!testPermission(sender, permission)) {
            if (command.getPermissionMessage() == null) {
                throw new CommandException("I'm sorry, but you do not have permission to perform this command. " +
                        "Please contact the server administrators if you believe that this is in error.");
            } else if (command.getPermissionMessage().length() != 0) {
                throw new CommandException(command.getPermissionMessage().replace("<permission>", permission));
            }
        }
    }

    /**
     * Test permission boolean.
     *
     * @param sender     the sender
     * @param permission the permission
     * @return the boolean
     */
    private boolean testPermission(CommandSender sender, String permission) {
        if ((permission == null) || (permission.length() == 0)) {
            return true;
        }

        for (String p : permission.split(";")) {
            if (sender.hasPermission(p)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets integer.
     *
     * @param name  the name
     * @param value the value
     * @return the integer
     */
    @SuppressWarnings("SameParameterValue")
    private Integer getInteger(String name, String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new CommandException("The " + name + " argument must be integer!");
        }
    }

    /**
     * Gets player.
     *
     * @param name the name
     * @return the player
     */
    private Player getPlayer(String name) {
        Player returned = Bukkit.getPlayer(name);

        if (returned == null) {
            throw new CommandException("No celestial player by the value of '" + name + "' is known!");
        }

        return returned;
    }

    /**
     * Sender as player player.
     *
     * @param sender the sender
     * @return the player
     */
    private Player senderAsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }

        throw new CommandException("You must be a player to execute this command!");
    }

    /**
     * Gets protector.
     *
     * @param sender the sender
     * @param name   the name
     * @return the protector
     */
    private Protector getProtector(CommandSender sender, @Nullable String name) {
        if (name != null) {
            Optional<Protector> protector = plugin.getProtectors().getProtector(name);

            if (!protector.isPresent()) {
                throw new CommandException("No celestial protector by the id of '" + name + "' is known!");
            }

            return protector.get();
        }

        Player player = senderAsPlayer(sender);
        Location location = player.getLocation();

        Optional<Protector> protector = plugin.getProtectors().getProtector(BlockVector3.at(
                location.getX(),
                location.getY(),
                location.getZ()
        ));


        if (!protector.isPresent()) {
            throw new CommandException("There is no region here.");
        }

        return protector.get();
    }

    /**
     * To message base component.
     *
     * @param protector the protector
     * @return the base component
     */
    private BaseComponent toMessage(Protector protector) {
        TextComponent component = new TextComponent("Region Information");
        component.setColor(ChatColor.GREEN);
        component.addExtra("\n");

        TextComponent id = new TextComponent(" - ID: ");
        id.setColor(ChatColor.GREEN);
        id.addExtra(new ComponentBuilder(protector.getId()).color(ChatColor.RESET).create()[0]);
        component.addExtra(id);
        component.addExtra("\n");

        TextComponent created = new TextComponent(" - Created: ");
        created.setColor(ChatColor.GREEN);
        created.addExtra(new ComponentBuilder(
                TimeUtils.getRelativeTime(protector.getCreated().getTime()).toString()
        ).color(ChatColor.RESET).create()[0]);
        component.addExtra(created);
        component.addExtra("\n");

        TextComponent creator = new TextComponent(" - Creator: ");
        creator.setColor(ChatColor.GREEN);
        creator.addExtra(new ComponentBuilder(protector.getCreator().getName()).color(ChatColor.RESET).create()[0]);
        component.addExtra(creator);
        component.addExtra("\n");

        TextComponent owners = new TextComponent(" - Owner(s): ");
        owners.setColor(ChatColor.GREEN);
        owners.addExtra(new ComponentBuilder(
                StringUtils.join(protector.getRegion().getRegion().getOwners().getUniqueIds()
                        .parallelStream()
                        .map(Bukkit::getOfflinePlayer)
//                        .filter(Objects::nonNull) // They cannot be null
                        .map(OfflinePlayer::getName)
                        .collect(Collectors.toList()), ", ")
        ).color(ChatColor.RESET).create()[0]);
        component.addExtra(owners);
        component.addExtra("\n");

        TextComponent members = new TextComponent(" - Member(s): ");
        members.setColor(ChatColor.GREEN);
        members.addExtra(new ComponentBuilder(
                StringUtils.join(protector.getRegion().getRegion().getMembers().getUniqueIds()
                        .parallelStream()
                        .map(Bukkit::getOfflinePlayer)
//                        .filter(Objects::nonNull) // They cannot be null
                        .map(OfflinePlayer::getName)
                        .collect(Collectors.toList()), ", ")
        ).color(ChatColor.RESET).create()[0]);
        component.addExtra(members);

        return component;
    }
}
