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
package com.mcmiddleearth.connect.bungee;

/**
 *
 * @author Eriol_Eandur
 */

import com.mcmiddleearth.connect.Channel;
import com.mcmiddleearth.connect.bungee.Handler.RestartHandler;
import com.mcmiddleearth.connect.bungee.Handler.TpaHandler;
import com.mcmiddleearth.connect.bungee.Handler.TpahereHandler;
import com.mcmiddleearth.connect.bungee.listener.CommandListener;
import com.mcmiddleearth.connect.bungee.listener.ConnectionListener;
import com.mcmiddleearth.connect.bungee.listener.PluginMessageListener;
import com.mcmiddleearth.connect.bungee.vanish.VanishHandler;
import com.mcmiddleearth.connect.bungee.vanish.VanishListener;
import com.mcmiddleearth.connect.bungee.warp.MyWarpDBConnector;
import com.mcmiddleearth.connect.bungee.watchdog.ServerWatchdog;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectBungeePlugin extends Plugin {
    
    private static ConnectBungeePlugin instance;
    
    private static int connectDelay = 200;
    private static Set<UUID> legacyPlayers = new HashSet<>();
    private static boolean legacyRedirectEnabled = true;
    private static String legacyRedirectFrom = "newplayerworld";
    private static String legacyRedirectTo = "world";
    
    private static ServerWatchdog watcher;
    
    private static YamlConfiguration config = new YamlConfiguration();
            
    private static File configFile;
    
    private static Set<String> noMVTP = new HashSet<>();
          
    private static MyWarpDBConnector myWarpConnector;
    
    private static boolean myWarpEnabled;
    
    private RestartScheduler restartScheduler;
    private ScheduledTask tpaCleanupScheduler;
    private ScheduledTask tpahereCleanupScheduler;
    
    @Override
    public void onEnable() {
        instance = this;
        configFile = new File(getDataFolder(),"config.yml");
        saveDefaultConfig();
        loadConfig();
        RestartHandler.init();
        tpaCleanupScheduler = TpaHandler.startCleanupScheduler();
        tpahereCleanupScheduler = TpahereHandler.startCleanupScheduler();
        restartScheduler = new RestartScheduler();
        if(config.getBoolean("serverWatchdog", true)) {
            watcher = new ServerWatchdog();
        }
        loadLegacyPlayers();
        VanishHandler.setPvSupport(config.getBoolean("premiumVanish", false));
        myWarpEnabled = (Boolean) getConfig().getSection("myWarp").get("enabled");
        if(myWarpEnabled) {
            myWarpConnector = new MyWarpDBConnector(getConfig().getSection("myWarp"));
        }
//Logger.getGlobal().info("HeY ");
        if(VanishHandler.isPvSupport()) {
//Logger.getGlobal().info("enable vanish support ");
            VanishHandler.loadVanished();
            getProxy().getPluginManager().registerListener(this, new VanishListener());
        }
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
        myWarpConnector.disconnect();
        restartScheduler.cancel();
        tpaCleanupScheduler.cancel();
        tpahereCleanupScheduler.cancel();
    }
    
    public static boolean isMvtpDisabled(String server) {
        return noMVTP.contains(server);
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

    public static ConnectBungeePlugin getInstance() {
        return instance;
    }

    public static int getConnectDelay() {
        return connectDelay;
    }

    public static Set<UUID> getLegacyPlayers() {
        return legacyPlayers;
    }

    public static boolean isLegacyRedirectEnabled() {
        return legacyRedirectEnabled;
    }

    public static String getLegacyRedirectFrom() {
        return legacyRedirectFrom;
    }

    public static String getLegacyRedirectTo() {
        return legacyRedirectTo;
    }

    public static ServerWatchdog getWatcher() {
        return watcher;
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static Set<String> getNoMVTP() {
        return noMVTP;
    }

    public static MyWarpDBConnector getMyWarpConnector() {
        return myWarpConnector;
    }

    public static boolean isMyWarpEnabled() {
        return myWarpEnabled;
    }
}