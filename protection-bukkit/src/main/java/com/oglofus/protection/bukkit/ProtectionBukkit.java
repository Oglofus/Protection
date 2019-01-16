package com.oglofus.protection.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * The type Protection bukkit.
 */
public class ProtectionBukkit extends JavaPlugin {
    /**
     * On load.
     */
    @Override
    public void onLoad() {
        super.onLoad();
    }

    /**
     * On enable.
     */
    @Override
    public void onEnable() {
        super.onEnable();
    }

    /**
     * On disable.
     */
    @Override
    public void onDisable() {
        super.onDisable();
    }

    /**
     * On command boolean.
     *
     * @param sender  the sender
     * @param command the command
     * @param label   the label
     * @param args    the args
     * @return the boolean
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    /**
     * On tab complete list.
     *
     * @param sender  the sender
     * @param command the command
     * @param alias   the alias
     * @param args    the args
     * @return the list
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return super.onTabComplete(sender, command, alias, args);
    }
}
