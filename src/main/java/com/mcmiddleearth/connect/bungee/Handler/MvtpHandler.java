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
import java.util.logging.Logger;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Eriol_Eandur
 */
public class MvtpHandler {
    
    public static boolean handle(String sender, String server) {
        Callback<Boolean> callback = (connected, error) -> {
            if(connected) {
                ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
                    ServerInfo dest = ProxyServer.getInstance().getServerInfo(server);
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(sender);
//Logger.getGlobal().info("mvtp player "+player.getName()+" to "+dest.getName()+" at "+player.getServer().getInfo().getName());
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF(Channel.SPAWN);
                    out.writeUTF(sender);
//Logger.getGlobal().info("mctp to "+server+" done!");
                    ProxyServer.getInstance().getServerInfo(server).sendData(Channel.MAIN, out.toByteArray(),true);   
                }, ConnectBungeePlugin.getConnectDelay(), TimeUnit.MILLISECONDS);
            }
        };
        return (ConnectHandler.handle(sender, server, callback)); //if {
            /*ServerInfo dest = ProxyServer.getInstance().getServerInfo(server);
            ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
Logger.getGlobal().info("onChat tp to player");
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(Channel.SPAWN);
                out.writeUTF(sender);
                dest.sendData(Channel.MAIN, out.toByteArray(),true);   
            }, ConnectBungeePlugin.getConnectDelay(), TimeUnit.MILLISECONDS);*
            return true;
        }
        return false;*/
    }
}
