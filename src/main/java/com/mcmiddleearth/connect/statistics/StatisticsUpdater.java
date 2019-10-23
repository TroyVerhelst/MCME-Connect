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
package com.mcmiddleearth.connect.statistics;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.ConnectPlugin;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class StatisticsUpdater extends BukkitRunnable {

    @Override
    public void run() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if(!players.isEmpty()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward"); // So BungeeCord knows to forward it
            out.writeUTF("ONLINE");
            out.writeUTF(Channel.STATISTIC); // The channel name to check if this your data

            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            try {
                msgout.writeShort(players.size());
                for(Player player: players) {
                    msgout.writeUTF(player.getUniqueId().toString()); // You can do anything you want with msgout
                    for(Statistic stat : Statistic.values()) {
                        if(stat.getType().equals(Statistic.Type.UNTYPED)) {
                            msgout.writeInt(player.getStatistic(stat));
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(StatisticsUpdater.class.getName()).log(Level.SEVERE, null, ex);
            }

            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            
            players.iterator().next().sendPluginMessage(ConnectPlugin.getInstance(), 
                                                        "BungeeCord", out.toByteArray());            
        }
    }
    
    public static void updateStatistic(byte[] msgbytes) {
        try {
            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            int count = msgin.readShort();
            for(int i=0; i< count; i++) {
                //Player player = Bukkit.getOfflinePlayer(id) = msgin.readUTF(); // Read the data in the same way you wrote it
                short somenumber = msgin.readShort();
            }   } catch (IOException ex) {
            Logger.getLogger(StatisticsUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
