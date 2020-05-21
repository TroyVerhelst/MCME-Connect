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
package com.mcmiddleearth.connect.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.ConnectPlugin;
import com.mcmiddleearth.connect.events.PlayerConnectEvent;
import com.mcmiddleearth.connect.restart.RestartHandler;
import com.onarandombox.MultiverseCore.MultiverseCore;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
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
public class ConnectPluginListener implements PluginMessageListener {
    
    
    public ConnectPluginListener() {
    }
    
    

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(Channel.MAIN)) {
          return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
//Logger.getGlobal().info("Pugin Message! "+player+" channel "+subchannel);
        if (subchannel.equals(Channel.TPPOS)) {
            String playerData = in.readUTF();
            String worldData = in.readUTF();
            String[] locData = in.readUTF().split(";");
            runAfterArrival(playerData, source -> {
//Logger.getGlobal().info("TPPOS! "+source+" ");
                source.sendMessage(ChatColor.GOLD+"Teleporting ...");
                World world = Bukkit.getWorld(worldData);
//Logger.getGlobal().info("TPPOS to "+worldData+" "+locData[0]+" "+locData[1]+" "+locData[2]);
                if(world!=null) {
                    Location location = new Location(world,Double.parseDouble(locData[0]),
                                                         Double.parseDouble(locData[1]),
                                                         Double.parseDouble(locData[2]),
                                                         Float.parseFloat(locData[3]),
                                                         Float.parseFloat(locData[4]));
                    source.teleport(location);
                }
            });
        } else if (subchannel.equals(Channel.TP)) {
            String sourceData = in.readUTF();
            String name = in.readUTF();
            runAfterArrival(sourceData, source -> {
                source.sendMessage(ChatColor.GOLD+"Teleporting to "+ChatColor.RED+name+ChatColor.GOLD+".");
                Player destination = Bukkit.getPlayer(name);
                if(destination!=null) {
                    source.teleport(destination);
                }
            });
        } else if (subchannel.equals(Channel.TITLE)) {
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
        } else if (subchannel.equals(Channel.COMMAND)) {
//Logger.getGlobal().info("COMMAND");
            String recipient = in.readUTF();
            String command = in.readUTF();
//Logger.getGlobal().info("recipient: "+recipient+ " "+command.substring(1));
            runAfterArrival(recipient, source -> {
//Logger.getGlobal().info("dispatch: "+source.getName()+" "+command);
                Bukkit.dispatchCommand(source, command.substring(1));
            });
        } else if (subchannel.equals(Channel.SPAWN)) {
            String name = in.readUTF();
            /*Player p = Bukkit.getPlayer(name);
            int delay = (p==null?ConnectBungeePlugin.getConnectDelay():1);
            new BukkitRunnable() {
                @Override
                public void run() {*/
            runAfterArrival(name, p -> {
                Location spawn = p.getWorld().getSpawnLocation().clone();
                try {
                    spawn = ((MultiverseCore)Bukkit.getPluginManager().getPlugin("Multiverse-Core"))
                        .getMVWorldManager().getMVWorld(p.getWorld().getName())
                        .getSpawnLocation().clone();
                } catch (NullPointerException ex) {}
                p.teleport(spawn);//.add(0.5,0,0.5));
            });
            //}.runTaskLater(ConnectPlugin.getInstance(), delay);
        } else if(subchannel.equals(Channel.DISCORD)) {
            String name = in.readUTF();
            String event = in.readUTF();
//Logger.getGlobal().info("Recieved discord message: "+name+" - "+event);
            //TextChannel discordChannel = DiscordUtil.getTextChannelById("global");
            if(event.equals("join")) {
                sendDiscord(":bangbang: **"+name+" joined the game.**");
            } else if(event.equals("leave")) {
                sendDiscord(":x: **"+name+" left the game.**");
            }
        } else if(subchannel.equals(Channel.LEGACY)) {
            String playerName = in.readUTF();
            String target = in.readUTF();
//Logger.getGlobal().info("Recieved LEGACY message: "+playerName+" - "+target);
            runAfterArrival(playerName, p -> {
                if(ConnectPlugin.getStatisticStorage()!=null) {
                    ConnectPlugin.getStatisticStorage().saveStaticstic(p, pp-> {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF(Channel.CONNECT);
                        out.writeUTF(target);
    //Logger.getGlobal().info("Sending forward message after stats: "+playerName+" - "+target);
                        pp.sendPluginMessage(ConnectPlugin.getInstance(), "BungeeCord", out.toByteArray());
                    });
                } else {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF(Channel.CONNECT);
                    out.writeUTF(target);
//Logger.getGlobal().info("Sending forward message: "+playerName+" - "+target);
                    p.sendPluginMessage(ConnectPlugin.getInstance(), "BungeeCord", out.toByteArray());
                }
            });
        } else if(subchannel.equals(Channel.RESTART)) {
            boolean shutdown = in.readBoolean();
            String playerName = in.readUTF();
            String servers = in.readUTF();
            runAfterArrival(playerName, p -> {
                if(servers.length()>0) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF(Channel.RESTART);
                    out.writeBoolean(shutdown);
                    out.writeUTF(playerName);
                    out.writeUTF(servers);
                    p.sendPluginMessage(ConnectPlugin.getInstance(), Channel.MAIN, out.toByteArray());
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!shutdown) {
                            RestartHandler.restartServer();
                        } else {
                            Bukkit.shutdown();
                        }
                    }
                }.runTaskLater(ConnectPlugin.getInstance(), 60);
            });
        } else if(subchannel.equals(Channel.JOIN)) {
            String playerName = in.readUTF();
            Player arrivingPlayer = Bukkit.getPlayer(playerName);
            if(arrivingPlayer==null) return;
            String reason = in.readUTF();
            runAfterArrival(playerName, p -> {
                Bukkit.getServer().getPluginManager()
                      .callEvent(new PlayerConnectEvent(arrivingPlayer,PlayerConnectEvent.ConnectReason.valueOf(reason)));
            });
        }
    }
    
    private void sendDiscord(String message) {
        String discordChannel = ConnectPlugin.getDiscordChannel();
        if ((discordChannel != null) && (!discordChannel.equals("")))
        {
          DiscordSRV discordPlugin = DiscordSRV.getPlugin();
          if (discordPlugin != null)
          {
            TextChannel channel = discordPlugin
                    .getDestinationTextChannelForGameChannelName(discordChannel);
            if (channel != null) {
              DiscordUtil.sendMessage(channel, message, 0, false);
            } else {
              Logger.getLogger("ConnectPlugin").warning("Discord channel not found.");
            }
          }
          else
          {
            Logger.getLogger("ConnectPlugin").warning("DiscordSRV plugin not found.");
          }
        }
    }

    private void runAfterArrival(String playerName, Consumer<Player> callback) {
        new BukkitRunnable() {
            int counter = 40;
            @Override
            public void run() {
//Logger.getGlobal().info("try "+counter);
                Player source = Bukkit.getPlayer(playerName);
                if(source==null) {
                    if(counter==0) {
                        Logger.getGlobal().info("WARNING! Expected player didn't arrive!");
                        cancel();
                    } else {
                        counter--;
                    }
                } else {
                    callback.accept(source);
                    cancel();
                }
            }
        }.runTaskTimer(ConnectPlugin.getInstance(), 1, 10);
    }
}
