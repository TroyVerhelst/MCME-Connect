/*
 * Copyright (C) 2020 MCME
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
package com.mcmiddleearth.connect.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Eriol_Eandur
 */
public class PlayerConnectEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();

    @Getter
    Player player;
    
    @Getter
    ConnectReason reason;
    
    public PlayerConnectEvent(Player player, ConnectReason reason) {
        this.player = player;
        this.reason = reason;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public enum ConnectReason {
        JOIN_PROXY,
        COMMAND,
        KICK_REDIRECT,
        LOBBY_FALLBACK,
        PLUGIN,
        PLUGIN_MESSAGE,
        SERVER_DOWN_REDIRECT,
        UNKNOWN
    }
}
