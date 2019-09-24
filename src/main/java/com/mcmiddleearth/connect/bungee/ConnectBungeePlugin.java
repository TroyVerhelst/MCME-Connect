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
import com.mcmiddleearth.connect.bungee.Handler.VanishHandler;
import com.mcmiddleearth.connect.bungee.listener.PluginMessageListener;
import com.mcmiddleearth.connect.bungee.listener.ConnectionListener;
import com.mcmiddleearth.connect.bungee.listener.CommandListener;
import com.mcmiddleearth.connect.bungee.watchdog.ServerWatchdog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

public class ConnectBungeePlugin extends Plugin {
    
    @Getter
    private static ConnectBungeePlugin instance;
    
    @Getter
    private static int connectDelay = 200;

    @Getter
    private static Set<UUID> legacyPlayers = new HashSet<>();
    
    @Getter 
    private static boolean legacyRedirectEnabled = true;
    @Getter 
    private static String legacyRedirectFrom = "newplayerworld";
    @Getter 
    private static String legacyRedirectTo = "world";
    
    private static ServerWatchdog watcher;
    
    @Getter
    private static YamlConfiguration config = new YamlConfiguration();
            
    private static File configFile;
    
    @Getter
    private static Set<String> noMVTP = new HashSet<>();
            
    @Override
    public void onEnable() {
        instance = this;
        watcher = new ServerWatchdog();
        configFile = new File(getDataFolder(),"config.yml");
        saveDefaultConfig();
        loadConfig();
        loadLegacyPlayers();
        VanishHandler.setPvSupport(config.getBoolean("premiumVanish", false));
        VanishHandler.loadVanished();
        //loadLegacyRedirect();
        ProxyServer.getInstance().registerChannel(Channel.MAIN);
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener());
        getProxy().getPluginManager().registerListener(this, new CommandListener());
        getProxy().getPluginManager().registerListener(this, 
                         new ConnectionListener());
    }
    
    @Override
    public void onDisable() {
        watcher.stopWatchdog();
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
    
    private void loadConfig() {
        config.load(configFile);
        legacyRedirectEnabled = config.getBoolean("legacyRedirect.enabled",true);
        legacyRedirectFrom = config.getString("legacyRedirect.from","newPlayer");
        legacyRedirectTo = config.getString("legacyRedirect.to","build");
        noMVTP.addAll(config.getStringList("disableMVTP"));
        connectDelay = config.getInt("connectDelay",200);
        Logger.getGlobal().info("legacyREdirectEnabled: "+legacyRedirectEnabled);
        Logger.getGlobal().info("legacyREdirectFrom: "+legacyRedirectFrom);
        Logger.getGlobal().info("legacyREdirectTo: "+legacyRedirectTo);
        Logger.getGlobal().info("connectDelay: "+connectDelay);
        Logger.getGlobal().info("noMVTP length: "+noMVTP.size());
    }
    
    /*private void loadLegacyRedirect() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(),"legacyRedirect.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
                try(FileWriter fw = new FileWriter(file)) {
                    fw.write("legacyRedirect: \n");
                    fw.write("  enabled: "+legacyRedirectEnabled+"\n");
                    fw.write("  from: "+legacyRedirectFrom+"\n");
                    fw.write("  to: "+legacyRedirectTo+"\n");
                }
            } catch (IOException ex) {
                Logger.getLogger(ConnectBungeePlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try(Scanner scanner = new Scanner(file))
            {
                scanner.nextLine();
                String str = scanner.nextLine().split(":")[1].trim();
//Logger.getGlobal().info(str);
                legacyRedirectEnabled = Boolean.parseBoolean(str);
                legacyRedirectFrom = scanner.nextLine().split(":")[1].trim();
                legacyRedirectTo = scanner.nextLine().split(":")[1].trim();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConnectBungeePlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }*/
    
    private void saveDefaultConfig() {
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
                try(InputStreamReader in = new InputStreamReader(getResourceAsStream("config.yml"));
                    FileWriter fw = new FileWriter(configFile)) {
                    char[] buf = new char[1024];
                    int read = 1;
                    while(read > 0) {
                        read = in.read(buf);
                        if(read>0) 
                            fw.write(buf,0,read);
                    }
                    fw.flush();
                    fw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ConnectBungeePlugin.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    
}