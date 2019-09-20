/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee;

/**
 *
 * @author Eriol_Eandur
 */
import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.bungee.listener.PluginMessageListener;
import com.mcmiddleearth.connect.bungee.listener.ConnectionListener;
import com.mcmiddleearth.connect.bungee.listener.CommandListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class ConnectBungeePlugin extends Plugin {
    
    @Getter
    private static ConnectBungeePlugin instance;
    
    @Getter
    private static int connectDelay = 200;

    @Getter
    private static Set<UUID> legacyPlayers = new HashSet<>();
    
    @Override
    public void onEnable() {
        instance = this;
        loadLegacyPlayers();
        ProxyServer.getInstance().registerChannel(Channel.MAIN);
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener());
        getProxy().getPluginManager().registerListener(this, new CommandListener());
        getProxy().getPluginManager().registerListener(this, new ConnectionListener());
    }
    
    private void loadLegacyPlayers() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(),"legacyPlayer.uid");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ConnectBungeePlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try(Scanner scanner = new Scanner(file))
        {
            while(scanner.hasNext()) {
                legacyPlayers.add(UUID.fromString(scanner.nextLine()));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConnectBungeePlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}