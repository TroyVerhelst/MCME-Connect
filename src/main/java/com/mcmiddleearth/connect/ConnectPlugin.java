/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect;

import com.mcmiddleearth.connect.listener.ConnectListener;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

/**
 *
 * @author Eriol_Eandur
 */
public class ConnectPlugin extends JavaPlugin {
    
    @Getter
    private static JavaPlugin instance;
    
    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getServer().getMessenger()
                .registerOutgoingPluginChannel(this, Channel.MAIN);
        Logger.getGlobal().info("Registation incoming channel: "+Bukkit.getServer().getMessenger()
                .registerIncomingPluginChannel(this, Channel.MAIN, new ConnectListener()).isValid());
    }

}
