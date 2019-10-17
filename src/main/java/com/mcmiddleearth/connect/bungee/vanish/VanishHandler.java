/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.vanish;

import com.mcmiddleearth.connect.Permission;
import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import com.mcmiddleearth.connect.bungee.listener.ConnectionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Eriol_Eandur
 */
public class VanishHandler {
    
    @Getter
    @Setter
    private static boolean pvSupport;
    
    private static Set<UUID> vanishedPlayers = new HashSet<>();
    
    private static File vanishFile = new File(ConnectBungeePlugin.getInstance().getDataFolder(),"vanished.uid");
    
    public static void join(ProxiedPlayer player) {
        if(player.hasPermission(Permission.JOIN_VANISHED)) {
            vanishedPlayers.add(player.getUniqueId());
            saveVanished();
        }
        if(isVanished(player)) {
            ProxyServer.getInstance().getPlayers().stream()
                .filter(p -> p.hasPermission(Permission.VANISH_SEE)).forEach(p -> {
                p.sendMessage(new ComponentBuilder(player.getName()+" joined the MCME-Network while being vanished.")
                                            .color(ChatColor.GREEN).create());
            });
        } else {
            ConnectionListener.sendJoinMessage(player, false);
        }
    }
    
    public static void quit(ProxiedPlayer player) {
        if(isVanished(player)) {
            ProxyServer.getInstance().getPlayers().stream()
                    .filter(p -> p.hasPermission("pv.see")).forEach(p -> {
                    p.sendMessage(new ComponentBuilder(player.getName()+" left the MCME-Network while being vanished.")
                                                .color(ChatColor.GREEN).create());
            });
        } else {
            ConnectionListener.sendLeaveMessage(player, false);
        }
    }
    
    public static void vanish(ProxiedPlayer player) {
        vanishedPlayers.add(player.getUniqueId());
        saveVanished();
        ProxyServer.getInstance().getPlayers().stream()
                .filter(p -> p.hasPermission("pv.see")).forEach(p -> {
                p.sendMessage(new ComponentBuilder(player.getName()+" vanished.")
                                            .color(ChatColor.GREEN).create());
        });
        ConnectionListener.sendLeaveMessage(player,true);
    }
    
    public static void unvanish(ProxiedPlayer player) {
        vanishedPlayers.remove(player.getUniqueId());
        saveVanished();
        ProxyServer.getInstance().getPlayers().stream()
                .filter(p -> p.hasPermission("pv.see")).forEach(p -> {
                p.sendMessage(new ComponentBuilder(player.getName()+" unvanished.")
                                            .color(ChatColor.GREEN).create());
        });
        ConnectionListener.sendJoinMessage(player,true);
    }
    
    public static boolean isVanished(ProxiedPlayer player) {
        return pvSupport && vanishedPlayers.contains(player.getUniqueId());
    }
    
    public static void saveVanished() {
        try(PrintWriter out = new PrintWriter(new FileWriter(vanishFile))) {
            vanishedPlayers.forEach(uuid -> {
                out.println(uuid.toString());
            });
        } catch (IOException ex) {
            Logger.getLogger(VanishHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void loadVanished() {
        vanishedPlayers.clear();
        try(Scanner scanner = new Scanner(vanishFile)) {
            while(scanner.hasNext()) {
                vanishedPlayers.add(UUID.fromString(scanner.nextLine()));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VanishHandler.class.getName()).log(Level.WARNING, "No vanished player file found.");
        }
    }
}
