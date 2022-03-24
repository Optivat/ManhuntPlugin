package com.optivat.manhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedrunnerCommand implements CommandExecutor {

    private Manhunt main;
    public SpeedrunnerCommand(Manhunt main) {this.main = main;}

@Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(sender instanceof Player) {
        Player p = (Player)sender;
        switch (args[0]) {
            case "add":
                if(Bukkit.getPlayer(args[1]) != null) {
                    main.speedrunners.put(Bukkit.getPlayer(args[1]), p.getLocation());
                    p.sendMessage(ChatColor.GREEN + args[1] + " is now a speedrunner!");
                    //Removing the compass since they are no longer a hunter
                    main.compassSelection.remove(Bukkit.getPlayer(args[1]));
                    if(Bukkit.getPlayer(args[1]).getInventory().contains(Material.COMPASS)) {
                        Bukkit.getPlayer(args[1]).getInventory().remove(Material.COMPASS);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Player isn't online or doesn't exist.");
                }
                break;
            case "remove":
                if(Bukkit.getPlayer(args[1]) != null) {
                    main.speedrunners.remove(Bukkit.getPlayer(args[1]));
                    p.sendMessage(ChatColor.GREEN + args[1] + " is no longer a speedrunner!");
                    //Giving the compass since they are now a hunter
                    main.compassSelection.put(Bukkit.getPlayer(args[1]), 0);
                    HunterCompass.giveHunterCompass(Bukkit.getPlayer(args[1]));
                } else {
                    p.sendMessage(ChatColor.RED + "Player isn't online or doesn't exist.");
                }
                break;
            default:
                return false;
        }
    }
    return false;
}

}
