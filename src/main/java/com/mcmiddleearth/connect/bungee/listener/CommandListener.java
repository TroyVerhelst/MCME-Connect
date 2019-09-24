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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
        if(event.isCommand() && event.getSender() instanceof ProxiedPlayer) {
//Logger.getGlobal().info("Is command!");
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            String[] message = event.getMessage().split(" ");
//if(message.length>1) 
//Logger.getGlobal().info("server "+message[0]+" "+message[1]+" "+ProxyServer.getInstance().getServerInfo(message[1]));
            if(message[0].equalsIgnoreCase("/tp") && message.length>1) {
//Logger.getGlobal().info("/tp!");
                if(player.hasPermission(Permission.TP)) {
                    ProxiedPlayer destination = ProxyServer.getInstance().getPlayer(message[1]);
                    if(destination != null 
                            && !destination.getServer().getInfo().getName()
                                .equals(player.getServer().getInfo().getName())) {
                        if(player.hasPermission(Permission.WORLD+"."+destination.getServer()
                                                                       .getInfo().getName())) {
                            if(!TpHandler.handle(player.getName(), 
                                     destination.getServer().getInfo().getName(),
                                     destination.getName())) {
                                sendError(player);
                            }
                        } else {
                            player.sendMessage(new ComponentBuilder("You don't have permission to enter "
                                                                      +destination+"'s world.")
                                                    .color(ChatColor.RED).create());
                        }
                        event.setCancelled(true);
                    }
                }
            } else if(message[0].equalsIgnoreCase("/tphere") && message.length>1) {
//Logger.getGlobal().info("/tphere!");
                if(player.hasPermission(Permission.TPHERE)) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(message[1]);
//Logger.getGlobal().info("/tphere! 2");
                    if(target != null 
                            && !target.getServer().getInfo().getName()
                                     .equals(player.getServer().getInfo().getName())) {
                        if((target.hasPermission(Permission.WORLD+"."
                                                   +player.getServer().getInfo().getName()))) {
                            if(!TpHandler.handle(target.getName(), 
                                                player.getServer().getInfo().getName(),
                                                player.getName())) {
                                sendError(player);
                            }
                        } else {
                            player.sendMessage(new ComponentBuilder(target.getName()
                                                    +" has no permission to enter your world.")
                                                    .color(ChatColor.RED).create());
                        }
                        event.setCancelled(true);
                    }
                }
            } else if((message[0].equalsIgnoreCase("/theme"))) {
                String themedWorld = ConnectBungeePlugin.getConfig().getString("themedbuildWorld", "themedbuilds");
                if(!player.getServer().getInfo().getName()
                        .equals(themedWorld)) {
                    if(player.getServer().getInfo().getName()
                                    .equals(ConnectBungeePlugin.getLegacyRedirectFrom())) {
                        player.sendMessage(new ComponentBuilder(
                                                "/theme isn't allowed to bypass the quiz.")
                                                .color(ChatColor.RED).create());
                    } else {
                        if(player.hasPermission(Permission.WORLD+"."+themedWorld)) {
    //Logger.getGlobal().info("handle");
                            if(!MvtpHandler.handle(player.getName(),themedWorld)) {
                                sendError(player);
                            } else {
                                player.sendMessage(new ComponentBuilder("All Themed-build commands need to be issues from Themed-build world. You were teleported there.")
                                                        .color(ChatColor.RED).create());
                            }
                        } else {
                            player.sendMessage(new ComponentBuilder("You don't have permission to enter world '"
                                                                     +themedWorld+"'.")
                                                    .color(ChatColor.RED).create());
                        }
                    }
                    event.setCancelled(true);
                }
            } else if((message[0].equalsIgnoreCase("/mvtp")
                       || message[0].equalsIgnoreCase("/world"))
                    && message.length>1
                    && ProxyServer.getInstance().getServerInfo(message[1])!=null) {
//Logger.getGlobal().info("/mvtp");
                String target = message[1];
                if(!player.getServer().getInfo().getName().equals(target)) {
                    if(player.getServer().getInfo().getName()
                                    .equals(ConnectBungeePlugin.getLegacyRedirectFrom())) {
                        player.sendMessage(new ComponentBuilder(
                                                "/mvtp isn't allowed to bypass the quiz.")
                                                .color(ChatColor.RED).create());
                    } else {
                        if(player.hasPermission(Permission.WORLD+"."+target)) {
    //Logger.getGlobal().info("handle");
                            if(!MvtpHandler.handle(player.getName(),target)) {
                                sendError(player);
                            }
                        } else {
                            player.sendMessage(new ComponentBuilder("You don't have permission to enter world '"
                                                                     +target+"'.")
                                                    .color(ChatColor.RED).create());
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String[] args = event.getCursor().split(" ");
        if(args.length>0) {
            switch(args[0]) {
                case "/tp":
                case "/tphere":
                    Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
                    if(args.length>1) {
                        players.stream().filter(player -> player.getName().toLowerCase()
                                                                .startsWith(args[1].toLowerCase()))
                                        .forEach(player -> event.getSuggestions().add(player.getName()));
                    }
                    break;
                case "/mvtp":
                case "/world":
                    Collection<String> servers = ProxyServer.getInstance().getServers().keySet();
                    if(args.length>1) {
                        servers.stream().filter(server -> server.toLowerCase()
                                                       .startsWith(args[1].toLowerCase()))
                                .forEach(server -> event.getSuggestions().add(server));
                    } else {
                        event.getSuggestions().addAll(servers);
                    }
            }
        } 
        if(event.getSuggestions().isEmpty()) {
            Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
            players.forEach(player -> event.getSuggestions().add(player.getName()));
        }
    }
    
    private void sendError(ProxiedPlayer player) {
        player.sendMessage(new ComponentBuilder("There was an error!")
                            .color(ChatColor.RED).create());    
    }
    
}
