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
package com.mcmiddleearth.connect.statistics;

import com.mcmiddleearth.connect.ConnectPlugin;
import com.mcmiddleearth.connect.bungee.ConnectBungeePlugin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.mariadb.jdbc.MySQLDataSource;
import org.bukkit.Statistic;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class StatisticDBConnector {
    
    private final String dbUser;
    private final String dbPassword;
    private final String dbName;
    private final String dbIp;
    private final int port;
    
    private final MySQLDataSource dataBase;
    
    private Connection dbConnection;
    
    private PreparedStatement updatePlayerStats;
    private PreparedStatement insertPlayerStats;
    private PreparedStatement selectPlayerStats;
    
    //private PreparedStatement updatePlayerMatStats;
    //private PreparedStatement insertPlayerMatStats;
    private PreparedStatement selectPlayerMatStats;
    private PreparedStatement selectPlayerAllMatStats;

    //private PreparedStatement updatePlayerEntityStats;
    //private PreparedStatement insertPlayerEntityStats;
    private PreparedStatement selectPlayerEntityStats;
    private PreparedStatement selectPlayerAllEntityStats;
    
    private PreparedStatement selectPlayerId;
    
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private ScheduledTask keepAliveTask;
    private boolean connected; 
    
    public StatisticDBConnector(ConfigurationSection config) {
        if(config==null) {
            config = new MemoryConfiguration();
        }
        dbUser = config.getString("user","development");
        dbPassword = config.getString("password","development");
        dbName = config.getString("dbName","development");
        dbIp = config.getString("ip", "localhost");
        port = config.getInt("port",3306);
        dataBase = new MySQLDataSource(dbIp,port,dbName);
        connect();
        checkConnection();
        keepAliveTask = ProxyServer.getInstance().getScheduler()
                .schedule(ConnectBungeePlugin.getInstance(), () -> {
            checkConnection();
        },30,60,TimeUnit.SECONDS);
    }
    
    private void executeAsync(Consumer<Player> method, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!connected) {
                    connect();
                }
                method.accept(player);
            }
        }.runTaskAsynchronously(ConnectPlugin.getInstance());
    }
    
    private synchronized boolean checkConnection() {
        try {
            if(connected && dbConnection.isValid(5)) {
                return true;
            } else {
                //throw new SQLException("No connection to statistic database!");
                if(dbConnection!=null) {
                    dbConnection.close();
                }
                connect();
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
            return false;
        }
    }
    
    private synchronized void connect() {
        try {
            dbConnection = dataBase.getConnection(dbUser, dbPassword);
            
            checkTables();
            
            String insertGeneral = "INSERT INTO mcmeconnect_statistic (uuid";
            String updateGeneral = "UPDATE mcmeconnect_statistic SET ";
            String selectGeneral = "SELECT ";
            
            //String insertMat = "INSERT INTO mcmeconnect_statistic_material (id, material";
            //String updateMat = "UPDATE mcmeconnect_statistic_material SET ";
            String selectMat = "SELECT ";
            String selectAllMat = "SELECT material";
            
            //String insertEntity = "INSERT INTO mcmeconnect_statistic_entity (id, entity, ?)";
            //String updateEntity = "UPDATE mcmeconnect_statistic_entity SET ? = ?";
            String selectEntity = "SELECT ";
            String selectAllEntity = "SELECT entity";
            
            String generalSeparator = "";
            String materialSeparator = "";
            String entitySeparator = "";
            for(Statistic stat : Statistic.values()) {
                switch(stat.getType()) {
                    case UNTYPED:
                        insertGeneral = insertGeneral + ", "      + stat.name();
                        updateGeneral = updateGeneral + generalSeparator + stat.name()+"=?";
                        selectGeneral = selectGeneral + generalSeparator + stat.name();
                        generalSeparator = ", ";
                        break;
                    case BLOCK:
                    case ITEM:
                        selectMat = selectMat + materialSeparator + getName(stat);
                        selectAllMat = selectAllMat + ", " + getName(stat);
                        materialSeparator = ", ";
                        break;
                    case ENTITY:
                        selectEntity = selectEntity + entitySeparator + stat.name();
                        selectAllEntity = selectAllEntity + ", " + stat.name();
                        entitySeparator = ", ";
                        break;
                }
            }
            updateGeneral = updateGeneral + " WHERE uuid = ?";
            insertGeneral = insertGeneral + ") VALUES (?";
            selectGeneral = selectGeneral + " FROM mcmeconnect_statistic WHERE uuid = ?";
            
            //updateMat = updateMat + " WHERE id = ?, material = ?";
            //insertMat = insertMat + ") VALUES (?, ?";
            selectMat = selectMat + " FROM mcmeconnect_statistic_material WHERE id = ? AND material = ?";
            selectAllMat = selectAllMat + " FROM mcmeconnect_statistic_material WHERE id = ?";
            
            selectEntity = selectEntity + " FROM mcmeconnect_statistic_entity WHERE id = ? AND entity = ?";
            selectAllEntity = selectAllEntity + " FROM mcmeconnect_statistic_entity WHERE id = ?";
            
            for(Statistic stat : Statistic.values()) {
                switch(stat.getType()) {
                    case UNTYPED:
                        insertGeneral = insertGeneral + ", ?";
                        break;
                }
            }
            insertGeneral = insertGeneral + ")";
            //insertMat = insertMat + ")";
            insertPlayerStats = dbConnection.prepareStatement(insertGeneral);
            updatePlayerStats = dbConnection.prepareStatement(updateGeneral);
            selectPlayerStats = dbConnection.prepareStatement(selectGeneral);
            
            //insertPlayerMatStats = dbConnection.prepareStatement(insertMat);
            //updatePlayerMatStats = dbConnection.prepareStatement(updateMat);
            selectPlayerMatStats = dbConnection.prepareStatement(selectMat);
            selectPlayerAllMatStats = dbConnection.prepareStatement(selectAllMat);
            
            selectPlayerEntityStats = dbConnection.prepareStatement(selectEntity);
            selectPlayerAllEntityStats = dbConnection.prepareStatement(selectAllEntity);
            
            selectPlayerId = dbConnection
                    .prepareStatement("SELECT id FROM mcmeconnect_statistic WHERE uuid = ?");
            selectPlayerId.setFetchSize(1);
            connected = true;
        } catch (SQLException ex) {
            Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }
    }
    
    public synchronized void disconnect() {
        connected = false;
        if(keepAliveTask!=null) {
            keepAliveTask.cancel();
        }
        if(dbConnection!=null) {
            try {
                dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private synchronized void checkTablesSync(){
        try {
            Logger.getLogger(ConnectPlugin.class.getName()).info("checking tables...");
            String statement = "CREATE TABLE IF NOT EXISTS mcmeconnect_statistic (uuid VARCHAR(50), id INT AUTO_INCREMENT";
            for(Statistic stat : Statistic.values()) {
                if(stat.getType().equals(Statistic.Type.UNTYPED)) {
                    statement = statement + ", " + stat.name()+" INT";
                }
            }
            statement = statement + ", KEY(id))";
            dbConnection.createStatement().execute(statement);
            PreparedStatement checkColumns = dbConnection.prepareStatement("SELECT * FROM mcmeconnect_statistic");
            checkColumns.setFetchSize(1);
            ResultSet result = checkColumns.executeQuery();
            if(result.next()) {
                for(Statistic stat: Statistic.values()) {
                    if(stat.getType().equals(Statistic.Type.UNTYPED)) {
                        try {
                            //Logger.getLogger(ConnectPlugin.class.getName()).info("checking... "+stat.name());
                            result.findColumn(stat.name());
                        } catch(SQLException ex) {
                            Logger.getLogger(ConnectPlugin.class.getName()).info("add column "+stat.name());
                            statement = "ALTER TABLE mcmeconnect_statistic ADD COLUMN "
                                    + stat.name()+" INT";
                            dbConnection.createStatement().execute(statement);
                        }
                    }
                }
            }

            statement = "CREATE TABLE IF NOT EXISTS mcmeconnect_statistic_material (id INT, material VARCHAR(50)";
            for(Statistic stat : Statistic.values()) {
                switch(stat.getType()) {
                    case BLOCK:
                    case ITEM:
                        statement = statement + ", " + getName(stat)+" INT";
                        break;
                }
            }
            statement = statement + ")";
            dbConnection.createStatement().execute(statement);
            checkColumns = dbConnection.prepareStatement("SELECT * FROM mcmeconnect_statistic_material");
            checkColumns.setFetchSize(1);
            result = checkColumns.executeQuery();
            if(result.next()) {
                for(Statistic stat: Statistic.values()) {
                    if(stat.getType().equals(Statistic.Type.BLOCK)
                            || stat.getType().equals(Statistic.Type.ITEM)) {
                        try {
                            //Logger.getLogger(ConnectPlugin.class.getName()).info("material checking... "+stat.name());
                            result.findColumn(getName(stat));
                        } catch(SQLException ex) {
                            Logger.getLogger(ConnectPlugin.class.getName()).info("add column "+stat.name());
                            statement = "ALTER TABLE mcmeconnect_statistic_material ADD COLUMN "
                                    + getName(stat)+" INT";
                            dbConnection.createStatement().execute(statement);
                        }
                    }
                }
            }

            statement = "CREATE TABLE IF NOT EXISTS mcmeconnect_statistic_entity (id INT, entity VARCHAR(50)";
            for(Statistic stat : Statistic.values()) {
                switch(stat.getType()) {
                    case ENTITY:
                        statement = statement + ", " + stat.name()+" INT";
                        break;
                }
            }
            statement = statement + ")";
            dbConnection.createStatement().execute(statement);
            checkColumns = dbConnection.prepareStatement("SELECT * FROM mcmeconnect_statistic_entity");
            checkColumns.setFetchSize(1);
            result = checkColumns.executeQuery();
            if(result.next()) {
                for(Statistic stat: Statistic.values()) {
                    if(stat.getType().equals(Statistic.Type.ENTITY)) {
                        try {
                            //Logger.getLogger(ConnectPlugin.class.getName()).info("entity checking... "+stat.name());
                            result.findColumn(stat.name());
                        } catch(SQLException ex) {
                            Logger.getLogger(ConnectPlugin.class.getName()).info("add column "+stat.name());
                            statement = "ALTER TABLE mcmeconnect_statistic_entity ADD COLUMN "
                                    + stat.name()+" INT";
                            dbConnection.createStatement().execute(statement);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void checkTables(){
        executeAsync(player -> {
            checkTablesSync();
        },null);
    }
    
    private synchronized void loadStatisticSync(Player player) {
            try {
//Logger.getLogger(ConnectPlugin.class.getName()).info("load Statistic for "+player.getName());
                selectPlayerStats.setString(1, player.getUniqueId().toString());
                ResultSet result = selectPlayerStats.executeQuery();
                if(result.next()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                for(Statistic stat : Statistic.values()) {
                                    if(stat.getType().equals(Statistic.Type.UNTYPED)) {
                                        int value = result.getInt(stat.name());
                                        player.setStatistic(stat, value);
                                    }
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }.runTask(ConnectPlugin.getInstance());
                }
                int id = getPlayerId(player.getUniqueId());
//Logger.getGlobal().warning("Load Statistic for: "+player.getName()+ " "+player.getUniqueId()+" stats id: "+id);
                if(id>=0) {
                    selectPlayerAllMatStats.setInt(1, id);
                    ResultSet matResult = selectPlayerAllMatStats.executeQuery();
                    if(matResult.next()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    do {
                                        Material mat = Material.valueOf(matResult.getString("material"));
                                        for(Statistic stat : Statistic.values()) {
                                            if(stat.getType().equals(Statistic.Type.BLOCK)
                                                    ||stat.getType().equals(Statistic.Type.ITEM)) {
                                                int value = matResult.getInt(getName(stat));
//Logger.getGlobal().info("Set Statistic: "+stat.name()+ " for material "+mat.name() + " to "+value);
                                                if(value > 0) {
                                                    try {
                                                        player.setStatistic(stat, mat, value);
                                                    } catch(IllegalArgumentException ex) {
                                                        Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.WARNING, null, ex);                                        
                                                    }
                                                }
                                            }
                                        }
                                    } while(matResult.next());
                                } catch (SQLException ex) {
                                    Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }.runTask(ConnectPlugin.getInstance());
                    }
                    selectPlayerAllEntityStats.setInt(1, id);
                    ResultSet entityResult = selectPlayerAllEntityStats.executeQuery();
                    if(entityResult.next()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    do {
                                        EntityType entity = EntityType.valueOf(entityResult.getString("entity"));
                                        for(Statistic stat : Statistic.values()) {
                                            if(stat.getType().equals(Statistic.Type.ENTITY)) {
                                                int value = entityResult.getInt(stat.name());
                                                if(value>0) {
                                                    player.setStatistic(stat, entity, value);   
                                                }
                                            }
                                        }
                                    } while(entityResult.next());
                                } catch (SQLException ex) {
                                    Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }.runTask(ConnectPlugin.getInstance());
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
                connected = false;
            }
    }
    
    public void loadStatistic(Player p) {
        executeAsync(player -> {
            loadStatisticSync(player);
        }, p);
    }
    
    public void loadStaticstic(Player p, Consumer<Player> callback) {
        executeAsync(player -> {
            loadStatisticSync(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    callback.accept(player);
                }
            }.runTask(ConnectPlugin.getInstance());
        },p);
    }
    
    public synchronized void saveStatisticSync(Player player) {
        try {
//Logger.getLogger(StatisticDBConnector.class.getName()).info("save Statistic for "+player.getName());
            selectPlayerStats.setString(1, player.getUniqueId().toString());
            ResultSet result = selectPlayerStats.executeQuery();
            if(result.next()) {
//Logger.getLogger(StatisticDBConnector.class.getName()).info("update");
                updateStats(player);
            } else {
//Logger.getLogger(StatisticDBConnector.class.getName()).info("insert");
                insertStats(player);
            }
        } catch (SQLException ex) {
            Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }
    }
    
    public void saveStatistic(Player p) {
        executeAsync(player -> {
            saveStatisticSync(player);
        },p);
    }

    public void saveStaticstic(Player p, Consumer<Player> callback) {
        executeAsync(player -> {
            saveStatisticSync(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    callback.accept(player);
                }
            }.runTask(ConnectPlugin.getInstance());
        },p);
    }
    
    private synchronized void updateStats(Player player) throws SQLException {
        int i = 1;
//Logger.getGlobal().warning("update Statistic for: "+player.getName()+ " "+player.getUniqueId());
        for(Statistic stat : Statistic.values()) {
            if(stat.getType().equals(Statistic.Type.UNTYPED)) {
                updatePlayerStats.setInt(i, player.getStatistic(stat));
                i++;
            }
        }
        updatePlayerStats.setString(i, player.getUniqueId().toString());
        updatePlayerStats.executeUpdate();
    }
    
    private synchronized void insertStats(Player player) throws SQLException {
//Logger.getGlobal().warning("update Statistic for: "+player.getName()+ " "+player.getUniqueId());
        insertPlayerStats.setString(1, player.getUniqueId().toString());
        int i = 2;
        for(Statistic stat : Statistic.values()) {
            if(stat.getType().equals(Statistic.Type.UNTYPED)) {
                insertPlayerStats.setInt(i, player.getStatistic(stat));
                i++;
            }
        }
        insertPlayerStats.executeUpdate();
    }
    
    private synchronized void saveMaterialStatsSync(Player player, Statistic stat, 
                                      Material mat, int value) {
        try {
//Logger.getLogger(StatisticDBConnector.class.getName()).info("save Statistic for "+player.getName());
            int id = getPlayerId(player.getUniqueId());
//Logger.getGlobal().warning("Save mat Statistic for: "+player.getName()+ " "+player.getUniqueId()+" stats id: "+id);
            if(id>=0) {
                selectPlayerMatStats.setInt(1, id);
                selectPlayerMatStats.setString(2, mat.name());
                ResultSet result = selectPlayerMatStats.executeQuery();
                if(result.next()) {
//Logger.getLogger(StatisticDBConnector.class.getName()).info("update");
                    updateMatStat(id, stat, mat, value);
                } else {
//Logger.getLogger(StatisticDBConnector.class.getName()).info("insert");
                    insertMatStat(id, stat, mat, value);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }
    }
    
    public void saveMaterialStats(Player p, Statistic stat, 
                                      Material mat, int value) {
        executeAsync(player -> {
            saveMaterialStatsSync(player,stat,mat,value);
        },p);
    }
    
    private synchronized void updateMatStat(int id, Statistic stat, Material mat, int value) throws SQLException {
        String statement = "UPDATE mcmeconnect_statistic_material SET "+getName(stat)
                +" = " + value + " WHERE id = "+id+" AND material = '"+mat.name()+"'";
        dbConnection.createStatement().execute(statement);
    }
    
    private synchronized void insertMatStat(int id, Statistic stat, Material mat, int value) throws SQLException {
        String statement = "INSERT INTO mcmeconnect_statistic_material (id, material, "+getName(stat)
                +") VALUES (" + id + ", '"+ mat.name()+"', "+ value + ")";
//Logger.getGlobal().info("insertMatStat "+statement);
        dbConnection.createStatement().execute(statement);
    }
    
    private synchronized void saveEntityStatsSync(Player player, Statistic stat,
                                      EntityType entity, int value) {
        try {
//Logger.getLogger(StatisticDBConnector.class.getName()).info("save Statistic for "+player.getName());
            int id = getPlayerId(player.getUniqueId());
//Logger.getGlobal().warning("Save entity Statistic for: "+player.getName()+ " "+player.getUniqueId()+" stats id: "+id);
            if(id>=0) {
                selectPlayerEntityStats.setInt(1, id);
                selectPlayerEntityStats.setString(2, entity.name());
                ResultSet result = selectPlayerEntityStats.executeQuery();
                if(result.next()) {
//Logger.getLogger(StatisticDBConnector.class.getName()).info("update");
                    updateEntityStat(id, stat, entity, value);
                } else {
//Logger.getLogger(StatisticDBConnector.class.getName()).info("insert");
                    insertEntityStat(id, stat, entity, value);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StatisticDBConnector.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }
    }
    
    public void saveEntityStats(Player p, Statistic stat, 
                                      EntityType entity, int value) {
        executeAsync(player -> {
            saveEntityStatsSync(player,stat,entity,value);
        },p);
    }
    
    private synchronized void updateEntityStat(int id, Statistic stat, EntityType entity, int value) throws SQLException {
        String statement = "UPDATE mcmeconnect_statistic_entity SET "+stat.name()
                +" = " + value + " WHERE id = "+id+" AND entity = '"+entity.name()+"'";
        dbConnection.createStatement().execute(statement);
    }
    
    private synchronized void insertEntityStat(int id, Statistic stat, EntityType entity, int value) throws SQLException {
        String statement = "INSERT INTO mcmeconnect_statistic_entity (id, entity, "+stat.name()
                +") VALUES (" + id + ", '"+ entity.name()+"', "+ value + ")";
        dbConnection.createStatement().execute(statement);
    }
    
    private synchronized int getPlayerId(UUID uuid) throws SQLException {
        selectPlayerId.setString(1, uuid.toString());
        ResultSet result = selectPlayerId.executeQuery();
        if(result.next()) {
//Logger.getGlobal().warning("Get id for: "+uuid.toString()+" stats id: "+result.getInt("id"));
            return result.getInt("id");
        } else {
            return -1;
        }
    }
    
    public String getName(Statistic stat) {
        return (stat.name().equals("DROP")?"DROP_":stat.name());
    }
}
