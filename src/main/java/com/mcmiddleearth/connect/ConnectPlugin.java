/*
 * Copyright (C) 2019 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        Bukkit.getOnlinePlayers().forEach(player-> {
            ConnectPlugin.getStatisticStorage().saveStatisticSync(player);
        });
        //statisticUpdater.cancel();
        //if(statisticStorage != null) 
            //statisticStorage.disconnect();
    }

}
