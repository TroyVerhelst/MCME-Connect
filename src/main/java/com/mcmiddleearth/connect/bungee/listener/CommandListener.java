/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.listener;

import com.mcmiddleearth.connect.Permission;
import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import com.mcmiddleearth.connect.bungee.Handler.TpHandler;
import com.mcmiddleearth.connect.bungee.Handler.MvtpHandler;
import java.util.Collection;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Eriol_Eandur
 */
public class CommandListener implements Listener {

    public CommandListener() {
    }
    
    @EventHandler
    public void onChat(ChatEvent event) {
//Logger.getGlobal().info("Has perms: "+((ProxiedPlayer)event.getSender()).hasPermission(Permission.TP)
// +" "+((ProxiedPlayer)event.getSender()).hasPermission(Permission.WORLD));
        if(event.isCommand()) {
//Logger.getGlobal().info("Is command!");
            String[] message = event.getMessage().split(" ");
//if(message.length>1) 
//Logger.getGlobal().info("server "+message[0]+" "+message[1]+" "+ProxyServer.getInstance().getServerInfo(message[1]));
            if(message[0].equalsIgnoreCase("/tp") && message.length>1) {
//Logger.getGlobal().info("/tp!");
                if(event.getSender() instanceof ProxiedPlayer
                        && ((ProxiedPlayer)event.getSender()).hasPermission(Permission.TP)) {
                    ProxiedPlayer destination = ProxyServer.getInstance().getPlayer(message[1]);
                    if(destination != null
                            && TpHandler.handle(((ProxiedPlayer) event.getSender()).getName(), 
                                     destination.getServer().getInfo().getName(),
                                     destination.getName())) {
                        event.setCancelled(true);
                    }
                }
            } else if((message[0].equalsIgnoreCase("/mvtp")
                       || message[0].equalsIgnoreCase("/world"))
                    && message.length>1
                    && ProxyServer.getInstance().getServerInfo(message[1])!=null) {
//Logger.getGlobal().info("/mvtp");
                if(event.getSender() instanceof ProxiedPlayer
                        && !((ProxiedPlayer)event.getSender()).getServer().getInfo().getName()
                                .equals(ConnectBungeePlugin.getLegacyRedirectFrom())
                        && ((ProxiedPlayer)event.getSender()).hasPermission(Permission.WORLD+"."+message[1])) {
//Logger.getGlobal().info("handle");
                    if(MvtpHandler.handle(((ProxiedPlayer) event.getSender()).getName(),message[1])) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String[] args = event.getCursor().split(" ");
        if(args.length==0) return;
        switch(args[0]) {
            case "/tp":
                Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
                if(args.length>1) {
                    players.stream().filter(player -> player.getName().startsWith(args[1]))
                                    .forEach(player -> event.getSuggestions().add(player.getName()));
                }
                break;
            case "/mvtp":
            case "/world":
                //Collection<ServerInfo> servers = ProxyServer.getInstance().
        }
    }
    
}
