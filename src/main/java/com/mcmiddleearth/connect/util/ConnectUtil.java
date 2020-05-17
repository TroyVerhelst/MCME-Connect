/*
 * Copyright (C) 2019 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.connect.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.ConnectPlugin;
import java.util.UUID;
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

    public static void sendWorldUUID(Player sender, UUID uid, String name) {
Logger.getGlobal().info("Sending world uuid: "+uid.toString()+" "+name);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(Channel.WORLD_UUID);
        out.writeUTF(uid.toString());
        out.writeUTF(name);
        sender.sendPluginMessage(ConnectPlugin.getInstance(), Channel.MAIN, out.toByteArray());
    }
}
