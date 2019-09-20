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
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

/**
 *
 * @author Eriol_Eandur
 */
public class TitleHandler {
    
    public static boolean handle(String server, String recipient, String title, String subtitle, 
                                 int intro, int show, int extro, int delay) {
        ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
            Collection<ServerInfo> servers = new HashSet<>();
            if(server.equals(Channel.ALL)) {
                servers = ProxyServer.getInstance().getServers().values();
            } else {
                servers.add(ProxyServer.getInstance().getServerInfo(server));
            }
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(Channel.TITLE);
            out.writeUTF(recipient);
            out.writeUTF(title);
            out.writeUTF(subtitle);
            out.writeInt(intro);
            out.writeInt(show);
            out.writeInt(extro);
            servers.forEach(info ->
                    info.sendData(Channel.MAIN, out.toByteArray(),false));
        }, delay, TimeUnit.MILLISECONDS);
        return true;
    }
}
