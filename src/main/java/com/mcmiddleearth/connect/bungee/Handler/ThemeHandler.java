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
package com.mcmiddleearth.connect.bungee.Handler;

import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Eriol_Eandur
 */
public class ThemeHandler {
    
    public static boolean handle(ProxiedPlayer sender, String server, String command) {
        Callback<Boolean> callback = (connected, error) -> {
//Logger.getGlobal().info("Handle Theme");
            if(connected) {
//Logger.getGlobal().info("Connected sender: "+sender+" server "+sender.getServer().getInfo().getName()+" command "+command);
                ProxyServer.getInstance().getScheduler().schedule(ConnectBungeePlugin.getInstance(), () -> {
                    sender.sendMessage(new ComponentBuilder("All Themed-build commands need to be issued from Themed-build world. You were teleported there.")
                                            .color(ChatColor.RED).create());
                    CommandHandler.handle(server, sender.getName(),command);
                }, ConnectBungeePlugin.getConnectDelay(), TimeUnit.MILLISECONDS);
            }
        };
        return (ConnectHandler.handle(sender.getName(), server, callback)); //if {
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
