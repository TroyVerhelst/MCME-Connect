/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.watchdog;

import com.mcmiddleearth.connect.Permission;
import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            List<String> downList = new ArrayList<>();
            ProxyServer.getInstance().getServers().forEach((name,info) -> {
                String finalName = name;
                info.ping((result, error) -> {
                    if(error!=null && !error.getMessage().equals("")) {
                        downList.add(finalName);
                    }
                });
            });
            ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(),() -> {
                if(!downList.isEmpty()) {
                    String downserver = "";
                    String separator = "";
                    downList.sort((one,two) -> one.compareToIgnoreCase(two));
                    for(int i=0; i<downList.size(); i++) {
                        downserver = downserver + separator + ChatColor.DARK_RED+downList.get(i);
                        separator = ", ";
                    }
                    int last = downserver.lastIndexOf(',');
                    if(last>0) {
                        downserver = downserver.substring(0, last)+ChatColor.RED+" and"
                                    +ChatColor.DARK_RED+downserver.substring(last+1);
                    }
                    String finalDown = downserver;
                    String single = (downList.size()>1?"":"s");
                    ProxyServer.getInstance().getPlayers().stream()
                               .filter(player -> player.hasPermission(Permission.WATCHDOG))
                               .forEach(player -> player.sendMessage(
                                       new ComponentBuilder("WARNING! Server "+ChatColor.DARK_RED
                                                                    +finalDown+ChatColor.RED+" seem"
                                                                    +single+ " to be down.")
                                                           .color(ChatColor.RED).create()));
                }
            }, 10, TimeUnit.SECONDS);
        }, 2, 2, TimeUnit.MINUTES);
    }  
    
    public void stopWatchdog() {
        watchdog.cancel();
    }
}
