/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.ConnectPlugin;
import java.util.logging.Logger;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.Location;

/**
 *
 * @author Eriol_Eandur
 */
public class ConnectUtil {
    
    public static void teleportPlayer(@NonNull Player player, 
                                      @NonNull String server, 
                                      @NonNull String world,
                                      @NonNull Location location) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Channel.TPPOS);
        out.writeUTF(server);
        out.writeUTF(player.getName());
        out.writeUTF(world);
        out.writeUTF(location.getX()+";"+location.getY()+";"+location.getZ()+";"+location.getYaw()+";"+location.getPitch());
Logger.getGlobal().info("teleport player "+player.getName()+" to server: "+server);
        player.sendPluginMessage(ConnectPlugin.getInstance(), Channel.MAIN, out.toByteArray());
    }
    
    public static void sendMessage(@NonNull Player sender,
                                   @NonNull String server, 
                                   @NonNull String recipient,
                                   @NonNull String message,
                                   int delay) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Channel.MESSAGE);
        out.writeUTF(server);
        out.writeUTF(recipient);
        out.writeUTF(message);
        out.writeInt(delay);
        sender.sendPluginMessage(ConnectPlugin.getInstance(), Channel.MAIN, out.toByteArray());
    }
   
    public static void sendTitle(Player sender, String server, String recipient, 
                                 String title, String subtitle, int intro, int show, int extro, int delay) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Channel.TITLE);
        out.writeUTF(server);
        out.writeUTF(recipient);
        out.writeUTF(title);
        out.writeUTF(subtitle);
        out.writeInt(intro);
        out.writeInt(show);
        out.writeInt(extro);
        out.writeInt(delay);
        sender.sendPluginMessage(ConnectPlugin.getInstance(), Channel.MAIN, out.toByteArray());
    }
}
