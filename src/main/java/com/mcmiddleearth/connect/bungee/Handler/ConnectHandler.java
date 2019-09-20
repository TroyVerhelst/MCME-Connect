/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.Handler;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

/**
 *
 * @author Eriol_Eandur
 */
public class ConnectHandler {
    
    public static boolean handle(String sender, String server, Callback<Boolean> callback) {
        ProxiedPlayer source = ProxyServer.getInstance().getPlayer(sender);
        Server origin = source.getServer();
        if(!origin.getInfo().getName().equals(server)) {
//Logger.getGlobal().info("onChat connect to other server");
            source.connect(ProxyServer.getInstance().getServerInfo(server),callback);
            return true;
        }
        return false;
    }
}