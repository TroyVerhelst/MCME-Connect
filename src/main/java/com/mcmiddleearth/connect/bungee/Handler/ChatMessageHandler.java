/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.Handler;

import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Eriol_Eandur
 */
public class ChatMessageHandler {
    
    public static boolean handle(String server, String recipient, String message, int delay) {
        ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), new Runnable() {
            @Override    
            public void run() {
                Collection<ProxiedPlayer> players = new HashSet<>();
                if(recipient.equals(Channel.ALL)) {
                    if(server.equals(Channel.ALL)) {
                        players = ProxyServer.getInstance().getPlayers();
                    } else {
                        players = ProxyServer.getInstance().getServerInfo(server).getPlayers();
                    }
                } else {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(recipient);
                    if(server.equals(Channel.ALL)
                            || player.getServer().getInfo().getName().equals(server)) {
                        players.add(player);
                    }
                }
                players.forEach(player -> 
                    player.sendMessage(new ComponentBuilder(message).create()));
            }
        }, delay, TimeUnit.MILLISECONDS);
        return true;
    }
}
