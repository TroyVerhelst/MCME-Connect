/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.statistics;

import com.mcmiddleearth.connect.ConnectPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Statistic;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class StatisticListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ConnectPlugin.getStatisticStorage().loadStatistic(event.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {
                ConnectPlugin.getStatisticStorage().saveStatistic(event.getPlayer());
            }
        }.runTaskLater(ConnectPlugin.getInstance(), 60);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onStatisticIncrease(PlayerStatisticIncrementEvent event) {
        Statistic stat = event.getStatistic();
        switch(stat.getType()) {
            case BLOCK:
            case ITEM:
                Material mat = event.getMaterial();
                ConnectPlugin.getStatisticStorage()
                             .saveMaterialStats(event.getPlayer(),stat,
                                                mat,event.getNewValue());
                break;
            case ENTITY:
                EntityType entity = event.getEntityType();
                ConnectPlugin.getStatisticStorage()
                             .saveEntityStats(event.getPlayer(),stat,
                                                entity,event.getNewValue());
        }
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        ConnectPlugin.getStatisticStorage().saveStatistic(event.getPlayer());
    }
    
}
