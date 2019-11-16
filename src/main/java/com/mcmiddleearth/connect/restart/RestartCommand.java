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
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
/**
 *
 * @author Eriol_Eandur
 */
public class RestartCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if(!cs.hasPermission(Permission.RESTART)) {
            cs.sendMessage(ChatColor.RED+"[MCME-Connect] You are not allowed to use that command.");
            return true;
        }
        if(args.length>0 && args[0].equalsIgnoreCase("reloadconfig")) {
            ConnectPlugin.getRestartScheduler().loadConfig();
            cs.sendMessage(ChatColor.AQUA+"[MCME-Connect] Configuration reloaded.");
            LocalDateTime now = LocalDateTime.now();
            cs.sendMessage(ChatColor.AQUA+"It's now: "+now.getDayOfWeek()+" "
                           +now.format(DateTimeFormatter.ISO_LOCAL_TIME));
            cs.sendMessage(ChatColor.AQUA+"Scheduled restarts: ");
            List<DayOfWeek> days = ConnectPlugin.getRestartScheduler().getRestartDays();
            List<LocalTime> times = ConnectPlugin.getRestartScheduler().getRestartTimes();
            for(int i = 0; i< days.size();i++) {
                cs.sendMessage(ChatColor.AQUA+"- "+days.get(i)+" "
                           +times.get(i).format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
            return true;
        } else if(args.length>0 && args[0].equalsIgnoreCase("cancel")) {
            ConnectPlugin.getRestartScheduler().cancelRestart();
            cs.sendMessage(ChatColor.AQUA+"[MCME-Connect] Announced restart was cancelled.");
            return true;
        } else {
            Bukkit.broadcastMessage(ChatColor.BOLD+"Restarting the server...");
            RestartHandler.restartServer();
            return true;
        }
    }
    
}
