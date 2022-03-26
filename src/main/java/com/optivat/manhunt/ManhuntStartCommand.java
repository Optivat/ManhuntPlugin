package com.optivat.manhunt;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class ManhuntStartCommand implements CommandExecutor {
    Manhunt main;
    public ManhuntStartCommand(Manhunt manhunt) {this.main = manhunt;}

    int timer = main.getConfig().getInt("manhunt_start_countdown");
    int taskID;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (main.speedrunners.isEmpty()) {
                p.sendMessage(ChatColor.DARK_RED + "There is no speedrunner, please do /speedrunner add [player]");
            } else {
                if (args.length == 1) {
                    switch (args[0]) {
                        case "start":
                            p.sendMessage(ChatColor.GREEN + "Starting...");
                            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                            taskID = scheduler.scheduleSyncRepeatingTask(main, () -> {
                                if (timer > 0) {
                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        all.sendMessage(ChatColor.GREEN + "Starting in " + timer + " seconds!");
                                        all.setHealth(20);
                                        all.setFoodLevel(20);
                                        all.setSaturation(15);
                                        Location locplayer = all.getLocation();
                                        all.sendTitle("Starts in...", timer + " seconds", 10, 20, 10);
                                        all.playSound(locplayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                    }
                                    timer = timer - 1;
                                }
                                if (timer <= 0) {
                                    main.manhuntStart = true;
                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        all.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lGO!"));
                                        all.setHealth(20);
                                        all.setFoodLevel(20);
                                        all.setSaturation(15);
                                        Location locplayer = all.getLocation();
                                        all.sendTitle("Manhunt", "GO!", 10, 20, 10);
                                        all.playSound(locplayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                    }
                                    stopTimer();
                                }
                            }, 0L, 20L);
                            break;
                        case "stop":
                            main.manhuntStart = false;
                            p.sendMessage(ChatColor.GREEN + "Stopping...");
                            break;
                        default:
                            p.sendMessage(ChatColor.RED + "Usage Error: /manhunt (start/stop)");
                            return false;

                    }
                } else {
                    p.sendMessage(ChatColor.DARK_RED + "Usage Error: /manhunt (start/stop)");
                }

            }
        }
        return false;
    }
    public void stopTimer() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
