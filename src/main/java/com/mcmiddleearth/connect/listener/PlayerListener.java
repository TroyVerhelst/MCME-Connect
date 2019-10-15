/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.listener;

import com.mcmiddleearth.connect.ConnectPlugin;
import com.mcmiddleearth.connect.statistics.StatisticDBConnector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class PlayerListener implements Listener {

    @EventHandler(priority=EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
    }
    
}
