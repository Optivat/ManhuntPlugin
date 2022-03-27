package com.optivat.manhunt;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamChatToggle implements CommandExecutor {
    Manhunt main;

    public TeamChatToggle(Manhunt manhunt) {
        this.main = manhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (main.teamchattoggle.contains(player)) {
            main.teamchattoggle.remove(player);
            player.sendMessage(ChatColor.GREEN + "You are now talking in all chat");
        } else {
            main.teamchattoggle.add(player);
            player.sendMessage(ChatColor.GREEN + "You are now talking in team chat.");
        }
        return false;
    }
}
