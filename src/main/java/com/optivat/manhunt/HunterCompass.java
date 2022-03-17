package com.optivat.manhunt;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class HunterCompass implements Listener {
    HashMap<Player, Player> compassSelection = new HashMap<>();


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

    }
}

