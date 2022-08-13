package com.optivat.manhunt;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.Arrays;

public class ManhuntEnd implements Listener {
    Manhunt main;

    int worldrestart;

    public ManhuntEnd(Manhunt manhunt) {
        this.main = manhunt;
        worldrestart = main.getConfig().getInt("world_restart_timer");
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if(main.speedrunners.containsKey(e.getEntity().getPlayer()) && main.manhuntStart) {
            Player p = e.getEntity().getPlayer();
            main.speedrunners.remove(p);
            p.sendMessage(ChatColor.RED + "You have died! You are now spectating and no longer a speedrunner.");
            p.setGameMode(GameMode.SPECTATOR);
            if(main.speedrunners.isEmpty()) {
                Bukkit.broadcastMessage(ChatColor.GREEN + "" + e.getEntity().getName() + " has died, hunter(s) win!");
                worldRestart();
            } else {
                Bukkit.broadcastMessage(ChatColor.GREEN + "" + e.getEntity().getName() + " has died! " + main.speedrunners.size() + " still remains!");
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if(e.getEntity() instanceof EnderDragon) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "" + e.getEntity().getName() + " has died, speedrunner(s) win!");
            worldRestart();
        }
    }

    //IF IT IS THE FIRST TIME THE SPEEDRUNNER IS ENTERING THE NETHER OR END
    @EventHandler
    public void onPortalEntry(PlayerPortalEvent e) {
        Player p = e.getPlayer();
        Server server = p.getServer();
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if(!Bukkit.getWorlds().contains(server.getWorld("Manhunt" + main.worldnumber + "_nether"))) {
                WorldCreator wc = new WorldCreator("Manhunt" + main.worldnumber + "_nether");
                wc.environment(World.Environment.NETHER);
                wc.createWorld();
            }
            World netherWorld = server.getWorld("Manhunt" + main.worldnumber + "_nether");
            World overworldWorld = server.getWorld("Manhunt" + main.worldnumber);

            e.setCanCreatePortal(true);
            Location location;
            if (p.getWorld() == overworldWorld) {
                location = new Location(netherWorld, e.getFrom().getBlockX() / 8, e.getFrom().getBlockY(), e.getFrom().getBlockZ() / 8);
            } else {
                location = new Location(overworldWorld, e.getFrom().getBlockX() * 8, e.getFrom().getBlockY(), e.getFrom().getBlockZ() * 8);
            }
            e.setTo(location);
        }
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if(!Bukkit.getWorlds().contains(server.getWorld("Manhunt" + main.worldnumber + "_the_end"))) {
                WorldCreator wc = new WorldCreator("Manhunt" + main.worldnumber + "_the_end");
                wc.environment(World.Environment.THE_END);
                wc.createWorld();
            }
            World endWorld = server.getWorld("Manhunt" + main.worldnumber + "_the_end");
            World overworldWorld = server.getWorld("Manhunt" + main.worldnumber);

            if (p.getWorld() == overworldWorld) {
                Location loc = new Location(endWorld, 100, 50, 0); // This is the vanilla location for obsidian platform.
                e.setTo(loc);
                Block block = loc.getBlock();
                for (int x = block.getX() - 2; x <= block.getX() + 2; x++) {
                    for (int z = block.getZ() - 2; z <= block.getZ() + 2; z++) {
                        Block platformBlock = loc.getWorld().getBlockAt(x, block.getY() - 1, z);
                        if (platformBlock.getType() != Material.OBSIDIAN) {
                            platformBlock.setType(Material.OBSIDIAN);
                        }
                        for (int yMod = 1; yMod <= 3; yMod++) {
                            Block b = platformBlock.getRelative(BlockFace.UP, yMod);
                            if (b.getType() != Material.AIR) {
                                b.setType(Material.AIR);
                            }
                        }
                    }
                }
            } else if (p.getWorld() == endWorld) {
                e.setTo(overworldWorld.getSpawnLocation());
            }
        }
    }


    private void worldRestart() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "Waiting " + worldrestart +  " seconds before restart...");
        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Everyone will be disconnected, you will have to re-add a speedrunner upon rejoin.");

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.setGameMode(GameMode.SURVIVAL);
                all.getInventory().clear();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + all.getName() + " everything");
                all.kickPlayer("World is restarting, please wait at least 20 seconds before rejoining.");
            }
            World world = Bukkit.getWorld("Manhunt"+ main.worldnumber);
            world.setKeepSpawnInMemory(false);
            Bukkit.unloadWorld(world, false);
            world.getWorldFolder().delete();

            World netherWorld = Bukkit.getWorld("Manhunt" + main.worldnumber + "_nether");
            netherWorld.setKeepSpawnInMemory(false);
            Bukkit.unloadWorld(netherWorld, false);
            netherWorld.getWorldFolder().delete();

            World endWorld = Bukkit.getWorld("Manhunt" + main.worldnumber + "_the_end");
            endWorld.setKeepSpawnInMemory(false);
            Bukkit.unloadWorld(endWorld, false);
            endWorld.getWorldFolder().delete();

            final File src = new File(Bukkit.getWorldContainer() + File.separator + world.getName());
            final File src1 = new File(Bukkit.getWorldContainer() + File.separator + netherWorld.getName());
            final File src2 = new File(Bukkit.getWorldContainer() + File.separator + endWorld.getName());
            deleteFile(src);
            deleteFile(src1);
            deleteFile(src2);
            main.worldnumber = main.worldnumber+1;

            WorldCreator.name("Manhunt"+ main.worldnumber).createWorld();

            WorldCreator wc = new WorldCreator("Manhunt" + main.worldnumber + "_nether");
            wc.environment(World.Environment.NETHER);
            wc.createWorld();

            WorldCreator wc1 = new WorldCreator("Manhunt" + main.worldnumber + "_the_end");
            wc1.environment(World.Environment.THE_END);
            wc1.createWorld();

            main.manhuntStart = false;
            Bukkit.broadcastMessage(ChatColor.GREEN + "World has been made!");
        }, 20 * worldrestart);
    }

    private boolean deleteFile(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                Arrays.stream(files).forEach(file -> deleteFile(file));
            }
        }
        return path.delete();
    }

}
