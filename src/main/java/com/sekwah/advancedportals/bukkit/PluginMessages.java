package com.sekwah.advancedportals.bukkit;

import org.bukkit.command.CommandSender;

public class PluginMessages {
    private AdvancedPortalsPlugin plugin;
    public boolean useCustomPrefix = false;
    public static String customPrefix = "\u00A7a[\u00A7eAdvancedPortals\u00A7a]";
    public static String customPrefixFail = "\u00A7c[\u00A77AdvancedPortals\u00A7c]";

    public PluginMessages (AdvancedPortalsPlugin plugin) {
        this.plugin = plugin;
        ConfigAccessor config = new ConfigAccessor(this.plugin, "config.yml");
        this.useCustomPrefix = config.getConfig().getBoolean("UseCustomPrefix");
        if (useCustomPrefix) {
            PluginMessages.customPrefix = config.getConfig().getString("CustomPrefix").replaceAll("&(?=[0-9a-fk-or])", "\u00A7");
            PluginMessages.customPrefixFail = config.getConfig().getString("CustomPrefixFail").replaceAll("&(?=[0-9a-fk-or])", "\u00A7");
        }
    }

    // This class is so then the common messages in commands or just messages over the commands are the same and can be
    //  easily changed.

    public static void UnknownCommand(CommandSender sender, String command) {
        sender.sendMessage(customPrefixFail + " Necesitas escribir algo antes /" + command + "\n");
        sender.sendMessage("\u00A7cSi no sabes que escribir o necesitas ayuda con los comandos escribe \u00A7e" + '"' + "\u00A7e/" + command + " help" + '"' + "\u00A7c\n");
    }

    public static void NoPermission(CommandSender sender, String command) {
        sender.sendMessage(customPrefixFail + " No tienes permiso para utilizar este comando!");
    }
}
