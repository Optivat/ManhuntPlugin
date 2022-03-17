package com.optivat.manhunt;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class HunterCompass implements Listener {
    Manhunt main;
    public HunterCompass(Manhunt main) {
        this.main = main;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(main.speedrunners.containsKey(p)) {
            main.speedrunners.replace(p, p.getLocation());
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

    }
}

