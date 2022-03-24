package com.optivat.manhunt;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.HashMap;

public class HunterCompass implements Listener {

    Manhunt main;
    HashMap<Player, Long> cooldown = new HashMap<>();
    public HunterCompass(Manhunt main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        /*When someone who hasn't been assigned speedrunner or hunter (basically joining the server for the first time while it is running
         it will automatically assign them to the hunter team and will give them a compass.)
         */
        if (!(main.speedrunners.containsKey(e.getPlayer()) && main.compassSelection.containsKey(e.getPlayer()))) {
            main.compassSelection.put(e.getPlayer(), 0);
            giveHunterCompass(e.getPlayer());
        }
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        //On hunter's death give the hunter a brand-new tracking compass
        if(main.compassSelection.containsKey(e.getPlayer())) {
            giveHunterCompass(e.getPlayer());
        }
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        //Gets speedrunner's location
        if(main.speedrunners.containsKey(p)) {
            main.speedrunners.replace(p, p.getLocation());
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        //Changes the compass's meta
        if((p.getInventory().getItemInMainHand().getType().equals(Material.COMPASS) || p.getInventory().getItemInOffHand().getType().equals(Material.COMPASS)) && main.compassSelection.containsKey(p)) {
            ItemStack compass = p.getInventory().getItemInMainHand();
            CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
            //Checking if left or right-clicked
            if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                //On left click, basically change which speedrunner is being tracked.
                if (main.compassSelection.isEmpty()) {
                    p.sendMessage(ChatColor.RED + "There are no hunters!");
                } else {
                    if (main.compassSelection.get(p) == main.speedrunners.size()) {
                        main.compassSelection.replace(p, 0);
                    } else {
                        main.compassSelection.replace(p, main.compassSelection.get(p) + 1);
                    }
                    p.sendMessage(ChatColor.GOLD + "You are now tracking " + main.speedrunners.keySet().toArray()[main.compassSelection.get(p)]);
                }
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Player speedrunnerSelection = (Player) main.speedrunners.keySet().toArray()[main.compassSelection.get(p)];
                if (main.speedrunners.isEmpty()) {
                    p.sendMessage(ChatColor.RED + "There are no speedrunners for you to track.");
                } else {
                    //Testing to see if the speedrunner is in a different plane of existence (aka the nether or the end)
                    if(p.getWorld().getEnvironment() != speedrunnerSelection.getWorld().getEnvironment()) {
                        p.sendMessage(ChatColor.RED + "The speedrunner is in a different dimension");
                    } else {
                        //Setting the compass to point towards the player
                        if((cooldown.get(p) + 5) >= (System.currentTimeMillis() / 1000)){
                            compassMeta.setLodestoneTracked(false);
                            compassMeta.setLodestone(main.speedrunners.get(main.speedrunners.keySet().toArray()[main.compassSelection.get(p)]));
                            compass.setItemMeta(compassMeta);
                            p.sendMessage(ChatColor.GREEN + "You've tracked the speedrunner!");
                            cooldown.put(p, (System.currentTimeMillis() / 1000));
                        } else {
                            p.sendMessage(ChatColor.RED + "There is a " + ((System.currentTimeMillis()-cooldown.get(p))/1000) + "cooldown before you can track!");
                        }
                    }
                }
            }
        }
    }
    public static void giveHunterCompass(Player p) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta)compass.getItemMeta();
        compassMeta.setLodestoneTracked(false);
        compassMeta.setLodestone(p.getLocation());
        compassMeta.setDisplayName("Hunter's Compass");
        compass.setItemMeta(compassMeta);
        p.getInventory().addItem(compass);
    }
}

