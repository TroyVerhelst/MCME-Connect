/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.listener;

import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Eriol_Eandur
 */
public class ConnectionListener implements Listener {

    public ConnectionListener() {
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        
        /*Logger.getGlobal().info("server: "+event.getPlayer().getServer());
        if(event.getPlayer().getServer()!=null) 
            Logger.getGlobal().info(event.getPlayer().getServer().getInfo().getName());
        Logger.getGlobal().info("reconnect: "+event.getPlayer().getReconnectServer());*/
        //TODO: join messages
    }
    
    @EventHandler
    public void onConnect(ServerConnectEvent event) {
//Logger.getGlobal().info("target: "+event.getTarget()+" "+event.getReason());
//Logger.getGlobal().info(""+ ConnectBungeePlugin.getLegacyPlayers().contains(event.getPlayer().getUniqueId()));
        if(event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
            if(ConnectBungeePlugin.getLegacyPlayers().contains(event.getPlayer().getUniqueId())
                    && event.getTarget().getName().equals("newPlayer")) {
                event.setTarget(ProxyServer.getInstance().getServerInfo("main"));
            }
        }
        //TODO: Ensure correct server (new players / others)
    }
    
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        //TODO: leave messages
    }

}
