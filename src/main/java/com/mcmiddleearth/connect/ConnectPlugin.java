/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect;

import com.mcmiddleearth.connect.listener.PlayerListener;
import com.mcmiddleearth.connect.listener.ConnectPluginListener;
import com.mcmiddleearth.connect.statistics.StatisticDBConnector;
import com.mcmiddleearth.connect.statistics.StatisticListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Eriol_Eandur
 */
public class ConnectPlugin extends JavaPlugin {
    
    @Getter
    private static JavaPlugin instance;
    
    @Getter
    private static StatisticDBConnector statisticStorage;
    
    @Getter
    private static String discordChannel;
    
    private BukkitTask statisticUpdater;
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        if(getConfig().getBoolean("syncStatistic",true)) {
            statisticStorage = new StatisticDBConnector(getConfig().getConfigurationSection("database"));
            Bukkit.getPluginManager().registerEvents(new StatisticListener(), this);
        }
        discordChannel = getConfig().getString("discordChannel","");
        Bukkit.getServer().getMessenger()
                .registerOutgoingPluginChannel(this, Channel.MAIN);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getServer().getMessenger()
                .registerIncomingPluginChannel(this, Channel.MAIN, new ConnectPluginListener());
        //Bukkit.getServer().getMessenger()
        //        .registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordListener());
        //new StatisticsUpdater().runTaskTimer(this, 600, 600);
    }
    
    @Override
    public void onDisable() {
        //statisticUpdater.cancel();
        if(statisticStorage != null) 
            statisticStorage.disconnect();
    }

}
