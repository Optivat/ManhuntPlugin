package com.optivat.manhunt;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.HashMap;

public class HunterCompass implements Listener {
    HashMap<Player, Integer> compassSelection = new HashMap<>();


    Manhunt main;
    public HunterCompass(Manhunt main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!(main.speedrunners.containsKey(e.getPlayer()) && compassSelection.containsKey(e.getPlayer()))) {
            compassSelection.put(e.getPlayer(), null);
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
        if(p.getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) {
            ItemStack compass = p.getInventory().getItemInMainHand();
            CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
            //Checking if left or right-clicked
            switch(e.getAction()) {
                //On left click, basically change which speedrunner is being tracked.
                case LEFT_CLICK_AIR:
                    //Rotates between each speedrunner and changes what the hunter is tracking.
                    if(compassSelection.get(p) == main.speedrunners.size()) {
                        compassSelection.replace(p, 0);
                    } else {
                        compassSelection.replace(p, compassSelection.get(p)+1);
                    }
                    p.sendMessage(ChatColor.GOLD + "You are now tracking " + main.speedrunners.keySet().toArray()[compassSelection.get(p)]);
                    break;
                    //On right click update the compass to the player's location
                case RIGHT_CLICK_AIR:
                    //Setting the compass to point towards the player
                    compassMeta.setLodestoneTracked(false);
                    compassMeta.setLodestone(main.speedrunners.get(main.speedrunners.keySet().toArray()[compassSelection.get(p)]));
                    compass.setItemMeta(compassMeta);
                    p.sendMessage(ChatColor.GREEN + "You've tracked the speedrunner!");

                    break;
                default:
                    p.sendMessage(ChatColor.RED + "Interaction error!");
                    return;
            }
        }
        //The same exact code as the previous if statement except it is in off hand, there is probably a more efficient way of doing this, go back later.
        if(p.getInventory().getItemInOffHand().getType().equals(Material.COMPASS)) {

        }
    }
}

