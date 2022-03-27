package com.optivat.manhunt;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.concurrent.TimeUnit;

public class HunterCompass implements Listener {

    private Cache<Player, Long> cooldown;
    private int compassCooldown;
    Manhunt main;
    public HunterCompass(Manhunt main) {
        this.main = main;
        cooldown = CacheBuilder.newBuilder().expireAfterWrite(main.getConfig().getInt("compass_cooldown_time"), TimeUnit.SECONDS).build();
        compassCooldown = main.getConfig().getInt("compass_cooldown_time");
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
        if(!(main.speedrunners.containsKey(e.getPlayer()))) {
            giveHunterCompass(e.getPlayer());
        }
        //Because they are in the manhunt world, if they respawn
        Player p = e.getPlayer();
        if (p.getBedSpawnLocation() == null) {
            Server server = p.getServer();
            World world = server.getWorld("Manhunt" + main.worldnumber);
            Location spawn = world.getSpawnLocation();
            double x = spawn.getX();
            double y = spawn.getY();
            double z = spawn.getZ();
            Location loc = new Location(world, x, y, z);
            e.setRespawnLocation(loc);
        } else {
            e.setRespawnLocation(p.getBedSpawnLocation());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (main.compassSelection.containsKey(p)) {
            main.compassSelection.remove(p);
        } else if(main.speedrunners.containsKey(p)) {
            main.speedrunners.remove(p);
        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(main.speedrunners.containsKey(e.getPlayer())) {
            main.speedrunners.replace(e.getPlayer(), e.getPlayer().getLocation());
        }
        if(!(main.speedrunners.containsKey(e.getPlayer()) && main.compassSelection.containsKey(e.getPlayer()))) {
            main.compassSelection.put(e.getPlayer(), 0);
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
                if (main.speedrunners.isEmpty()) {
                    p.sendMessage(ChatColor.GOLD + "There are no speedrunners!");
                } else {
                    if (main.compassSelection.get(p) == main.speedrunners.size()-1) {
                        main.compassSelection.replace(p, 0);
                    } else {
                        main.compassSelection.replace(p, main.compassSelection.get(p) + 1);
                    }
                    p.sendMessage(ChatColor.GOLD + "You are now tracking " + ((Player)main.speedrunners.keySet().toArray()[main.compassSelection.get(p)]).getName() + "!");
                }
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (main.speedrunners.isEmpty()) {
                    p.sendMessage(ChatColor.RED + "There are no speedrunners for you to track.");
                } else {
                    if (main.speedrunners.keySet().toArray()[main.compassSelection.get(p)] != null) {
                        Player speedrunner = (Player) main.speedrunners.keySet().toArray()[main.compassSelection.get(p)];
                        //A cooldown
                        if(!cooldown.asMap().containsKey(p)) {
                            cooldown.put(p, System.currentTimeMillis() + (compassCooldown*1000));
                            //Testing to see if the speedrunner is in a different plane of existence (aka the nether or the end)
                            if(p.getWorld().getEnvironment() != speedrunner.getWorld().getEnvironment()) {
                                p.sendMessage(ChatColor.RED + "The speedrunner is in a different dimension");
                            } else {
                                //Setting the compass to point towards the player
                                compassMeta.setLodestoneTracked(false);
                                compassMeta.setLodestone(main.speedrunners.get(main.speedrunners.keySet().toArray()[main.compassSelection.get(p)]));
                                compass.setItemMeta(compassMeta);
                                p.sendMessage(ChatColor.GREEN + "You've tracked " + speedrunner.getName() + "!");
                            }
                        } else {
                            long distance = cooldown.asMap().get(p) - System.currentTimeMillis();
                            p.sendMessage(ChatColor.RED + "You must wait " + TimeUnit.MILLISECONDS.toSeconds(distance) + " seconds before you can track " + speedrunner.getName() + " again.");
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "That player is no longer a speedrunner!");
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

