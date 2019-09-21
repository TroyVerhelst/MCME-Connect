/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.ConnectPlugin;
import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class ConnectListener implements PluginMessageListener {
    
    
    public ConnectListener() {
    }
    

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
Logger.getGlobal().info("Pugin Message! "+player);
        if (!channel.equals(Channel.MAIN)) {
          return;
        }
Logger.getGlobal().info("BungeeCord!");
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals(Channel.TPPOS)) {
Logger.getGlobal().info("TPPOS!");
            String playerData = in.readUTF();
            String worldData = in.readUTF();
            String[] locData = in.readUTF().split(";");
            Player source = Bukkit.getPlayer(playerData);
            source.sendMessage(ChatColor.GOLD+"Teleporting ...");
            World world = Bukkit.getWorld(worldData);
            if(world!=null) {
                Location location = new Location(world,Double.parseDouble(locData[0]),
                                                     Double.parseDouble(locData[1]),
                                                     Double.parseDouble(locData[2]),
                                                     Float.parseFloat(locData[3]),
                                                     Float.parseFloat(locData[4]));
                source.teleport(location);
Logger.getGlobal().info("Teleport to!"+location);
            }
        } else if (subchannel.equals(Channel.TP)) {
Logger.getGlobal().info("TP!");
            String sourceData = in.readUTF();
            String name = in.readUTF();
Logger.getGlobal().info("Source: "+sourceData+" target: "+name);
            Player source = Bukkit.getPlayer(sourceData);
            source.sendMessage(ChatColor.GOLD+"Teleporting to "+ChatColor.RED+name+ChatColor.GOLD+".");
            Player destination = Bukkit.getPlayer(name);
Logger.getGlobal().info("Teleport to!"+destination);
            if(destination!=null) {
                source.teleport(destination);
            }
        } else if (subchannel.equals(Channel.TITLE)) {
Logger.getGlobal().info("Title!");
            String recipient = in.readUTF();
            String title = in.readUTF();
            String subtitle = in.readUTF();
            int intro = in.readInt();
            int show = in.readInt();
            int extro = in.readInt();
            Collection<Player> players = new HashSet<>();
            if(recipient.equals(Channel.ALL)) {
                players.addAll(Bukkit.getOnlinePlayers());
            } else {
                players.add(Bukkit.getPlayer(recipient));
            }
            players.forEach(p -> {
                p.sendTitle(title, subtitle, intro, show, extro);
            });
        } else if (subchannel.equals(Channel.SPAWN)) {
Logger.getGlobal().info("Spawn!");
            String name = in.readUTF();
            Player p = Bukkit.getPlayer(name);
Logger.getGlobal().info("Player: "+p);
Logger.getGlobal().info("World: "+p.getWorld());
            int delay = (p==null?ConnectBungeePlugin.getConnectDelay():1);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player p = Bukkit.getPlayer(name);
                    p.teleport(p.getWorld().getSpawnLocation().add(0.5,0,0.5));
                }
            }.runTaskLater(ConnectPlugin.getInstance(), delay);
        }
    }

}
