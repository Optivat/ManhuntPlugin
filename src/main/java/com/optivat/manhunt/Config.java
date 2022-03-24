package com.optivat.manhunt;

public class Config {
    public Config(Manhunt main) {
        main.getConfig().addDefault("compass_cooldown", 2);
        main.getConfig().options().copyDefaults();
        main.saveDefaultConfig();
    }
}
