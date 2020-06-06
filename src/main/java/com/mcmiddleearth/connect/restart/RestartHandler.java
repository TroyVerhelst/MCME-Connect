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
package com.mcmiddleearth.connect.restart;

import com.mcmiddleearth.connect.ConnectPlugin;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class RestartHandler {

    private static File restartFile = new File(ConnectPlugin.getInstance().getDataFolder(),"restart.nfo");
    private static File pluginRestartFile = new File(ConnectPlugin.getInstance().getDataFolder(),"connectPlugin.nfo");

    public static void init() {
        if(!restartFile.exists()) {
            try {
                restartFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(RestartHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(pluginRestartFile.exists()) {
            pluginRestartFile.delete();
        }
    }
    
    public static void restartServer() {
        try {
            if(!restartFile.exists()) {
                restartFile.createNewFile();
            }
            if (!pluginRestartFile.exists()) {
                pluginRestartFile.createNewFile();
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player->player.kickPlayer("Server is restarting..."));
                    Bukkit.shutdown();
                }
            }.runTaskLater(ConnectPlugin.getInstance(),60);
        } catch (IOException ex) {
            Logger.getLogger(RestartHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void stopServer() {
        if (restartFile.exists()) {
            restartFile.delete();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player->player.kickPlayer("Server is shutting down..."));
                Bukkit.shutdown();
            }
        }.runTaskLater(ConnectPlugin.getInstance(),60);
    }

    public static void testServerCrash() {
        Bukkit.shutdown();
    }
}
