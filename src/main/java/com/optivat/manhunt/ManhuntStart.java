package com.optivat.manhunt;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ManhuntStart implements Listener {
    Manhunt main;
    public ManhuntStart(Manhunt main) {
        this.main = main;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(!main.manhuntStart) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot move until the manhunt start!");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!main.manhuntStart) {
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ()) {
                Location loc = e.getFrom();
                e.getPlayer().teleport(loc.setDirection(e.getTo().getDirection()));
                return;
            }
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot move until the manhunt start!");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(!main.manhuntStart) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot place blocks until the manhunt start!");
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(!main.manhuntStart) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot break blocks until the manhunt start");
        }
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!main.manhuntStart) {
            if(e.getEntity() instanceof Player) {
                e.setCancelled(true);
                Player p = (Player) e.getEntity();
                p.sendMessage(ChatColor.DARK_RED + "You cannot hit anything until the manhunt start");
            } else {
                e.setCancelled(true);
            }
        }
    }
}
