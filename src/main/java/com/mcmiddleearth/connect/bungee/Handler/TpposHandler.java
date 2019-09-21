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
public class TpposHandler {
    
    public static boolean handle(String sender, String server, String world, String location) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(sender);
        if(player!=null) {
            Callback<Boolean> callback = (connected, error) -> {
                if(connected) {
                    //ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
                    ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF(Channel.TPPOS);
                        out.writeUTF(sender);
                        out.writeUTF(world);
                        out.writeUTF(location);
                        ProxyServer.getInstance().getServerInfo(server).sendData(Channel.MAIN, out.toByteArray());
                    }, ConnectBungeePlugin.getConnectDelay(), TimeUnit.MILLISECONDS);
                    //Logger.getGlobal().info("sending teleport message!");
                }
            };
            if(!player.getServer().getInfo().getName().equals(server)) {
                ConnectHandler.handle(sender, server, callback);
            } else {
                callback.done(Boolean.TRUE, null);
            }
            //ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
            //}, ConnectBungeePlugin.getConnectDelay(), TimeUnit.MILLISECONDS);
        }
        return true;
    }
}
