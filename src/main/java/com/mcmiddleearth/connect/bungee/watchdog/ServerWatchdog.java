/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.watchdog;

import com.mcmiddleearth.connect.Permission;
import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.scheduler.ScheduledTask;

/**
 *
 * @author Eriol_Eandur
 */
public class ServerWatchdog {

    ScheduledTask watchdog;
    
    public ServerWatchdog() {
        watchdog = ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
             ProxyServer.getInstance().getServers().forEach((name,info) -> {
                 info.ping((result, error) -> {
                    if(error!=null && !error.getMessage().equals("")) {
                        ProxyServer.getInstance().getPlayers().stream()
                                   .filter(player -> player.hasPermission(Permission.WATCHDOG))
                                   .forEach(player -> player.sendMessage(
                                           new ComponentBuilder("WARNING! Server '"+ChatColor.DARK_RED
                                                                        +name+ChatColor.RED+"' seems to be down.")
                                                               .color(ChatColor.RED).create()));
                    }
                 });
             });
        }, 2, 2, TimeUnit.MINUTES);
    }  
    
    public void stopWatchdog() {
        watchdog.cancel();
    }
}
