package com.optivat.manhunt;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedrunnerCommand implements CommandExecutor {

    private Manhunt main;
    public SpeedrunnerCommand(Manhunt main) {this.main = main;}

public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if(sender instanceof Player) {
        switch (args[0]) {
            case "add":
                if(Bukkit.getPlayer(args[1]) != null) {

                }

            case "remove":
                break;
            default:
                return false;
        }
    }
    return false;
}

}
