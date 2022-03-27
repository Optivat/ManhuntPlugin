package com.optivat.manhunt;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.ArrayList;
import java.util.HashMap;

public final class Manhunt extends JavaPlugin {
    public HashMap<Player, Location> speedrunners = new HashMap<>();
    public HashMap<Player, Integer> compassSelection = new HashMap<>();
    public ArrayList<Player> teamchattoggle = new ArrayList<>();
    public boolean manhuntStart = false;
    public int worldnumber;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        //Variable initalization ZONE
        worldnumber = getConfig().getInt("number_worlds");

        Bukkit.getPluginManager().registerEvents(new HunterCompass(this), this);
        Bukkit.getPluginManager().registerEvents(new ManhuntStart(this), this);
        Bukkit.getPluginManager().registerEvents(new ManhuntEnd(this), this);
        getCommand("speedrunner").setExecutor(new SpeedrunnerCommand(this));
        getCommand("teamchattoggle").setExecutor(new TeamChatToggle(this));
        getCommand("manhunt").setExecutor(new ManhuntStartCommand(this));

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[MANHUNT] Please consider reviewing the plugin!"));

        //If this is the first time the plugin is initiallized it means that the Manhunt world isn't created, so that is what is going to happen.
        if(Bukkit.getWorld("Manhunt"+ getConfig().getInt("number_worlds")) == null) {
            worldnumber = worldnumber+1;
            WorldCreator.name("Manhunt"+ worldnumber).createWorld();

            WorldCreator wc = new WorldCreator("Manhunt" + worldnumber + "_nether");
            wc.environment(World.Environment.NETHER);
            wc.createWorld();

            WorldCreator wc1 = new WorldCreator("Manhunt" + worldnumber + "_the_end");
            wc1.environment(World.Environment.THE_END);
            wc1.createWorld();
        }

    }

    @Override
    public void onDisable() {
        getConfig().set("number_worlds", worldnumber);
        saveConfig();
    }
}
