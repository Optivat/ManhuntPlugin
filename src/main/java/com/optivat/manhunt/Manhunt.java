package com.optivat.manhunt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Manhunt extends JavaPlugin {
    public HashMap<Player, Location> speedrunners = new HashMap<>();
    public HashMap<Player, Integer> compassSelection = new HashMap<>();
    public Config;

    @Override
    public void onEnable() {
        config = new Config(this);
        Bukkit.getPluginManager().registerEvents(new HunterCompass(this), this);
        this.getCommand("speedrunner").setExecutor(new SpeedrunnerCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
