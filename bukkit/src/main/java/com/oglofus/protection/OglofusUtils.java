package com.oglofus.protection;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.managers.RegionManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.BaseComponentSerializer;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 20/05/2017.
 */
final class OglofusUtils {
    /**
     * Gets material.
     *
     * @param value the value
     * @return the material
     */
    @SuppressWarnings("unused")
    static Material getMaterial(String value) {
        Material returned = Material.getMaterial(value);

        return returned == null ? Material.AIR : returned;
    }

    /**
     * Send message.
     *
     * @param sender     the sender
     * @param components the components
     */
    static void sendMessage(CommandSender sender, BaseComponent... components) {
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(components);
        } else {
            sender.sendMessage(Arrays.asList(components)
                    .parallelStream()
                    .map(comps -> BaseComponent.toPlainText(comps))
                    .toArray(String[]::new));
        }
    }

    /**
     * To boolean boolean.
     *
     * @param input the input
     * @return the boolean
     * @throws IllegalArgumentException the illegal argument exception
     */
    static boolean toBoolean(String input) throws IllegalArgumentException {
        if (!input.equalsIgnoreCase("true")
                && !input.equalsIgnoreCase("yes")
                && !input.equalsIgnoreCase("allow")
                && !input.equalsIgnoreCase("allows")
                && !input.equalsIgnoreCase("on")
                && !input.equalsIgnoreCase("y")
                && !input.equalsIgnoreCase("1")) {
            if (!input.equalsIgnoreCase("false")
                    && !input.equalsIgnoreCase("no")
                    && !input.equalsIgnoreCase("off")
                    && !input.equalsIgnoreCase("deny")
                    && !input.equalsIgnoreCase("denies")
                    && !input.equalsIgnoreCase("n")
                    && !input.equalsIgnoreCase("0")) {
                throw new IllegalArgumentException();
            } else {
                return Boolean.FALSE;
            }
        } else {
            return Boolean.TRUE;
        }
    }

    /**
     * Generate id string.
     *
     * @param manager the manager
     * @return the string
     */
    static String generateId(RegionManager manager) {
        String returned = RandomStringUtils.random(15, true, true);

        if (manager.getRegion(returned) != null) {
            return generateId(manager);
        }

        return returned;
    }

    /**
     * Has regions boolean.
     *
     * @param manager the manager
     * @param target  the target
     * @return the boolean
     */
    static boolean hasRegions(RegionManager manager, CuboidRegion target) {
        BlockVector3 pos1 = target.getPos1();
        BlockVector3 pos2 = target.getPos2();

        if (hasRegions(manager, pos1)
                || hasRegions(manager, pos2)
                || hasRegions(manager, BlockVector3.at(pos1.getBlockX(), pos2.getBlockY(), pos1.getBlockZ()))
                || hasRegions(manager, BlockVector3.at(pos1.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()))
                || hasRegions(manager, BlockVector3.at(pos2.getBlockX(), pos2.getBlockY(), pos1.getBlockZ()))
                || hasRegions(manager, BlockVector3.at(pos1.getBlockX(), pos1.getBlockY(), pos2.getBlockZ()))
                || hasRegions(manager, BlockVector3.at(pos2.getBlockX(), pos1.getBlockY(), pos1.getBlockZ()))
                || hasRegions(manager, BlockVector3.at(pos2.getBlockX(), pos1.getBlockY(), pos2.getBlockZ()))) {
            return true;
        }

        //Deep check. Checking all the blocks.
        for (BlockVector3 vector : target) {
            if (hasRegions(manager, vector)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Has regions boolean.
     *
     * @param manager the manager
     * @param target  the target
     * @return the boolean
     */
    private static boolean hasRegions(RegionManager manager, BlockVector3 target) {
        return manager.getApplicableRegionsIDs(target).size() > 0;
    }
}
