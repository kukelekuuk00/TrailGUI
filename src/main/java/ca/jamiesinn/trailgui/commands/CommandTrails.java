package ca.jamiesinn.trailgui.commands;

import ca.jamiesinn.trailgui.Main;
import ca.jamiesinn.trailgui.Methods;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTrails
        implements CommandExecutor
{
    Main main;

    public CommandTrails(Main main)
    {
        this.main = main;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("Trails"))
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage(ChatColor.DARK_RED + "[TrailGUI] Only players can perform this command.");
                return true;
            }
            Player player = (Player) sender;
            for(String string : Main.disabledWorlds)
            {
                string = string.replace("[", "");
                string = string.replace("]", "");
                if (string.equals(player.getWorld().getName()))
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "TrailGUI" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "You cannot use this command in this world.");
                    return false;
                }
                if (!player.hasPermission("trailgui.commands.trails"))
                {
                    player.sendMessage(Main.getPlugin().getConfig().getString("Commands-denyPermissionMessage").replaceAll("&", "\u00A7"));
                    if (Main.getPlugin().getConfig().getBoolean("closeInventoryOnDenyPermission"))
                    {
                        player.closeInventory();
                    }
                    return false;
                }
                Methods.openGUI(player);
            }
        }
        return false;
    }
}