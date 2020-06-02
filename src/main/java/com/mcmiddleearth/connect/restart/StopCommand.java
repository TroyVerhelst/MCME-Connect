/*
 * Copyright (C) 2019 Eriol_Eandur
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
import com.mcmiddleearth.connect.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class StopCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if (!cs.hasPermission(Permission.STOP)) {
            cs.sendMessage(ChatColor.RED + "[MCME-Connect] You are not allowed to use that command.");
            return true;
        }
        String shutdownMessage = "Shutting down the server...";
        if (args.length > 0) {
            if (args[0].equals("crashtest")) {
                RestartHandler.testServerCrash();
                return true;
            }
            shutdownMessage = args[0];
            for (int i = 1; i < args.length; i++) {
                shutdownMessage = shutdownMessage + " " + args[i];
            }
        }
        Bukkit.broadcastMessage(ChatColor.BOLD + shutdownMessage);
        RestartHandler.stopServer();
        return true;
    }
    
}
