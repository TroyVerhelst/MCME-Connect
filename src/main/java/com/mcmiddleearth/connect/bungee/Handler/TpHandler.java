/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.Handler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Eriol_Eandur
 */
public class TpHandler {
    
    public static boolean handle(String sender, String server, String target) {
        Callback<Boolean> callback = (connected, error) -> {
            if(connected) {
                ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(sender);
                    //ProxiedPlayer destination = ProxyServer.getInstance().getPlayer(target);
    //Logger.getGlobal().info("onChat tp to player: "+player.getName());
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF(Channel.TP);
                    out.writeUTF(sender);
                    out.writeUTF(target);
                    ProxyServer.getInstance().getServerInfo(server).sendData(Channel.MAIN, out.toByteArray(),true);   
                }, ConnectBungeePlugin.getConnectDelay(), TimeUnit.MILLISECONDS);
            }
        };
        return (ConnectHandler.handle(sender, server, callback));/*if {
            ProxiedPlayer destination = ProxyServer.getInstance().getPlayer(target);
            ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
Logger.getGlobal().info("onChat tp to player");
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(Channel.TP);
                out.writeUTF(sender);
                out.writeUTF(target);
                destination.getServer().sendData(Channel.MAIN, out.toByteArray());   
            }, ConnectBungeePlugin.getConnectDelay(), TimeUnit.MILLISECONDS);
            return true;
        }
        return false;*/
    }
}
