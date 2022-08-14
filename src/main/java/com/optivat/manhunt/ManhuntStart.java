package com.optivat.manhunt;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ManhuntStart implements Listener {
    Manhunt main;
    private int piglinEnderpearlChance;

    public ManhuntStart(Manhunt main) {
        this.main = main;
        piglinEnderpearlChance = main.getConfig().getInt("piglin_enderpearl_chance");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(!main.manhuntStart) {
            e.setCancelled(true);
            e.getPlayer().sendTitle(ChatColor.RED + "NO MOVING", ChatColor.DARK_RED + "until the manhunt starts.", 10, 20, 10);
            if(!(main.speedrunners.containsKey(e.getPlayer()) && main.compassSelection.containsKey(e.getPlayer()))) {
                main.compassSelection.put(e.getPlayer(), 0);
            }
        }
    }

    //I KNOW THAT THESE TWO EVENTS ARE DUPLICATES AND ARE GOING TO HINDER SERVER PERFORMANCE, I WILL MAKE THESE MORE EFFICIENT IN THE NEXT UPDATE

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!main.manhuntStart) {
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ()) {
                Location loc = e.getFrom();
                e.getPlayer().teleport(loc.setDirection(e.getTo().getDirection()));
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Server server = p.getServer();
        World world = server.getWorld("Manhunt" + main.worldnumber);
        if (main.speedrunners.containsKey(p) || main.compassSelection.containsKey(p)) {

        } else {
            Location spawn = world.getSpawnLocation();
            double x = spawn.getX();
            double y = spawn.getY();
            double z = spawn.getZ();
            Location loc = new Location(world, x, y, z);
            p.teleport(loc);
            if (!main.manhuntStart) {
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(!main.manhuntStart) {
            e.setCancelled(true);
            e.getPlayer().sendTitle(ChatColor.RED + "NO PLACING", ChatColor.DARK_RED + "until the manhunt starts.", 10, 20, 10);
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(!main.manhuntStart) {
            e.setCancelled(true);
            e.getPlayer().sendTitle(ChatColor.RED + "NO BREAKING", ChatColor.DARK_RED + "until the manhunt starts.", 10, 20, 10);
        }
    }
    
    //Not related at all to the start of the manhunt, however, we know that the chance of getting enderperals is the chances
    //of me getting a girlfriend so I think I should give the people an option to have increased pearl chances.
    
    @EventHandler
    public void onBarter(PiglinBarterEvent e) {
        Random random = new Random();
        if (random.nextInt(100) > piglinEnderpearlChance) {
            e.getOutcome().clear();
            ItemStack EnderPearl = new ItemStack(Material.ENDER_PEARL, random.nextInt(3)+1);
            e.getOutcome().add(EnderPearl);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!main.manhuntStart) {
            if(e.getEntity() instanceof Player) {
                e.setCancelled(true);
                Player p = (Player) e.getEntity();
                p.sendTitle(ChatColor.RED + "NO HITTING", ChatColor.DARK_RED + "until the manhunt starts.", 10, 20, 10);
            } else {
                e.setCancelled(true);
            }
        }
    }
    //THIS IS FOR THE TEAMCHATTOGGLE COMMAND
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (main.teamchattoggle.contains(p)) {
            if (main.compassSelection.containsKey(p)) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (main.compassSelection.containsKey(all)) {
                        all.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[HUNTER] " + e.getPlayer().getName() + " &6&l>&7 " + e.getMessage()));
                    }
                }
                e.setCancelled(true);
            }
            if (main.speedrunners.containsKey(p)) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (main.speedrunners.containsKey(all)) {
                        all.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[SPEEDRUNNER] " + e.getPlayer().getName() + " &6&l>&7 " + e.getMessage()));
                    }
                }
                e.setCancelled(true);
            }
        } else {
            e.setFormat(ChatColor.translateAlternateColorCodes('&', "&e[ALL] " + e.getPlayer().getName() + " &6&l>&7 " + e.getMessage()));
        }
    }
}
