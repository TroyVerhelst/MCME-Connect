/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.connect.bungee.Handler.ConnectHandler;
import com.mcmiddleearth.connect.bungee.Handler.TpposHandler;
import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.bungee.Handler.ChatMessageHandler;
import com.mcmiddleearth.connect.bungee.Handler.TitleHandler;
import java.util.logging.Logger;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Eriol_Eandur
 */
public class PluginMessageListener implements Listener {

    public PluginMessageListener() {
    }
    
    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        //if(event.getTag().equals("BungeeCord")) return;
//Logger.getGlobal().info("Plugin Message! "+event.getTag());
        if(event.getTag().equals(Channel.MAIN)) {
//Logger.getGlobal().info("Plugin Connect Message!");
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String subchannel = in.readUTF();
            switch (subchannel) {
                case Channel.CONNECT:
                    {
                        String server = in.readUTF();
                        String sender = in.readUTF();
                        ConnectHandler.handle(sender,server, (connected, error) -> {});
                        //tp to spawn
                        break;
                    }
                case Channel.TPPOS:
                    {
                        Logger.getGlobal().info("reading TPPOS message!");
                        String server = in.readUTF();
                        String sender = in.readUTF();
                        String world = in.readUTF();
                        String locLine = in.readUTF();
                        TpposHandler.handle(sender, server, world, locLine);
                        //String[] locData = locLine.split(";");
                        //connect to server
                        //Logger.getGlobal().info("found teleport data!");
                        break;
                    }
                case Channel.MESSAGE:
                    {
                        String server = in.readUTF();
                        String recipient = in.readUTF();
                        String message = in.readUTF();
                        int delay = in.readInt();
                        ChatMessageHandler.handle(server,recipient, message, delay);
                        break;
                    }
                case Channel.TITLE:
                    {
                        String server = in.readUTF();
                        String recipient = in.readUTF();
                        String title = in.readUTF();
                        String subtitle = in.readUTF();
                        int intro = in.readInt();
                        int show = in.readInt();
                        int extro = in.readInt();
                        int delay = in.readInt();
                        TitleHandler.handle(server,recipient, title, subtitle, intro, show, extro, delay);
                        break;
                    }
                default:
                    break;
            }
        }
    }
}
