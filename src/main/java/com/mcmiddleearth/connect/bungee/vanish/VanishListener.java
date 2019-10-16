/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee.vanish;

import com.mcmiddleearth.connect.bungee.listener.ConnectionListener;
import de.myzelyam.api.vanish.BungeePlayerHideEvent;
import de.myzelyam.api.vanish.BungeePlayerShowEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Eriol_Eandur
 */
public class VanishListener implements Listener {
    
    @EventHandler
    public void onVanish(BungeePlayerHideEvent event) {
//Logger.getGlobal().info("vanish "+event.getPlayer().getName());
        VanishHandler.vanish(event.getPlayer());
    }
    
    @EventHandler
    public void onVanish(BungeePlayerShowEvent event) {
//Logger.getGlobal().info("unvanish "+event.getPlayer().getName());
        VanishHandler.unvanish(event.getPlayer());
    }
    
    
}
