/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.listener;

import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import com.mcmiddleearth.connect.bungee.Handler.VanishHandler;
import de.myzelyam.api.vanish.BungeePlayerHideEvent;
import de.myzelyam.api.vanish.BungeePlayerShowEvent;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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

    //private Map<String,String> playerServers = new HashMap<>();
    
    @EventHandler
    public void onJoin(PostLoginEvent event) {
//Logger.getGlobal().info("onJoin");
        ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
            ProxiedPlayer player = event.getPlayer();
//    Logger.getGlobal().info("connect "+VanishHandler.isPvSupport()+" "
  //                                    +VanishHandler.isVanished(event.getPlayer())+" "
    //                                  +event.getPlayer().hasPermission("pv.joinvanished"));
            if(event.getPlayer().hasPermission("pv.joinvanished")) {
                VanishHandler.vanish(player);
            }
            if(!VanishHandler.isVanished(event.getPlayer())) {
    //Logger.getGlobal().info("onJoin send");
                sendJoinMessage(player);
            }
        }, ConnectBungeePlugin.getConnectDelay(), TimeUnit.MILLISECONDS);
        /*Logger.getGlobal().info("server: "+event.getPlayer().getServer());
        if(event.getPlayer().getServer()!=null) 
            Logger.getGlobal().info(event.getPlayer().getServer().getInfo().getName());
        Logger.getGlobal().info("reconnect: "+event.getPlayer().getReconnectServer());*/
        //TODO: join messages
    }
    
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
//Logger.getGlobal().info("disconnect "+pvSupport+" "+BungeeVanishAPI.isInvisible(player));
        if(!VanishHandler.isVanished(player)) {
            sendLeaveMessage(player);
        }
    }
    
    @EventHandler
    public void onVanish(BungeePlayerHideEvent event) {
//Logger.getGlobal().info("vanish "+event.getPlayer().getName());
        VanishHandler.vanish(event.getPlayer());
        sendLeaveMessage(event.getPlayer());
    }
    
    @EventHandler
    public void onVanish(BungeePlayerShowEvent event) {
//Logger.getGlobal().info("unvanish "+event.getPlayer().getName());
        VanishHandler.unvanish(event.getPlayer());
        sendJoinMessage(event.getPlayer());
    }
    
    @EventHandler
    public void handleLegacyPlayers(ServerConnectEvent event) {
//Logger.getGlobal().info("target: "+event.getTarget()+" "+event.getReason());
//Logger.getGlobal().info(""+ ConnectBungeePlugin.getLegacyPlayers().contains(event.getPlayer().getUniqueId()));
//Logger.getGlobal().info(""+ ConnectBungeePlugin.getLegacyRedirectFrom()+" "+ConnectBungeePlugin.getLegacyRedirectTo()+" "+ConnectBungeePlugin.isLegacyRedirectEnabled());
        if(event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
            if(!ConnectBungeePlugin.isLegacyRedirectEnabled()) {
                return;
            }
//Logger.getGlobal().info("1");
            if(ConnectBungeePlugin.getLegacyPlayers().contains(event.getPlayer().getUniqueId())
                    && event.getTarget().getName().equals(ConnectBungeePlugin.getLegacyRedirectFrom())) {
//Logger.getGlobal().info("2");
                event.setTarget(ProxyServer.getInstance().getServerInfo(ConnectBungeePlugin.getLegacyRedirectTo()));
            }
        }
    }
    
    private void sendJoinMessage(ProxiedPlayer player) {
        ProxyServer.getInstance().getPlayers().forEach(p -> {
//Logger.getGlobal().info("onJoin send message");
                p.sendMessage(new ComponentBuilder(player.getName()+" joined the MCME-Network.")
                                            .color(ChatColor.YELLOW).create());
        });
    }
    
    private void sendLeaveMessage(ProxiedPlayer player) {
        ProxyServer.getInstance().getPlayers().forEach(p -> {
//Logger.getGlobal().info("onJoin send message");
                p.sendMessage(new ComponentBuilder(player.getName()+" left the MCME-Network.")
                                            .color(ChatColor.YELLOW).create());
        });
    }

}
