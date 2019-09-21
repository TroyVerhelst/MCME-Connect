/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.connect.bungee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Eriol_Eandur
 */
public class YamlConfiguration {
    
    private Map<String,Object> map = new HashMap<>();
    
    public void load(File file) {
        Yaml yaml = new Yaml();
        try {
            map = yaml.load(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(YamlConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = getValue(key);
        return (value!=null?(Boolean)value:defaultValue);
    }
    
    public int getInt(String key, int defaultValue) {
        Object value = getValue(key);
        return (value!=null?(Integer)value:defaultValue);
    }
    
    public double getDouble(String key, double defaultValue) {
        Object value = getValue(key);
        return (value!=null?(Double)value:defaultValue);
    }
    
    public String getString(String key, String defaultValue) {
        Object value = getValue(key);
        return (value!=null?(String)value:defaultValue);
    }
    
    public List<String> getStringList(String key) {
        return (List<String>)getValue(key);
    }
    
    private Object getValue(String key) {
//Logger.getGlobal().info("Key: "+key);
//Logger.getGlobal().info("Keysplit: "+key.split("\\.").length);
        return getValue(map, key.split("\\."));
    }
    
    private Object getValue(Map<String,Object> submap, String[] subkeys) {
//Logger.getGlobal().info("length "+subkeys.length);
        if(subkeys.length>1) {
            if(submap.containsKey(subkeys[0])) {
                return getValue((Map<String,Object>)submap.get(subkeys[0]),
                                Arrays.copyOfRange(subkeys, 1, subkeys.length));
            } else {
                return null;
            }
        } else {
            return submap.get(subkeys[0]);
        }
    }
}
