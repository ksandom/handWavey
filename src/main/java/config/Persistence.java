// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Save/Load configuration to/from files.
*/

package config;

import org.yaml.snakeyaml.*;
import java.io.InputStream;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import debug.Debug;

public class Persistence {
    Debug debug;
    
    public Persistence() {
        this.debug = Debug.getDebug("Persistence");
    }

    public void save(String fileName, HashMap<String, Group> groups, HashMap<String, Item> items) {
        save(fileName, groups, items, new HashMap<String, Boolean>());
    }
    
    public void save(String fileName, HashMap<String, Group> groups, HashMap<String, Item> items, HashMap<String, Boolean> exclusions) {
        this.debug.out(1, "Save config file: " + fileName);
        Map tree = buildTree(groups, items, exclusions);
        
        try {
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(fileName);
            yaml.dump(tree, writer);
            
        } catch (IOException e) {
            this.debug.out(0, "Failed to save config to " + fileName + ". Here is a stack trace for debugging:");
            e.printStackTrace();
            return ;
        }
    }
    
    private Map buildTree(HashMap<String, Group> groups, HashMap<String, Item> items) {
        return buildTree(groups, items, new HashMap<String, Boolean>());
    }
    
    private Map buildTree(HashMap<String, Group> groups, HashMap<String, Item> items, HashMap<String, Boolean> exclusions) {
        HashMap result = new HashMap();
        
        HashMap itemMap = new HashMap();
        for (String key : items.keySet()) {
            // Work out if we can hide unused entries.
            if (items.get(key).canHideIfUnchanged()) {
                String value = items.get(key).get();
                if (value == items.get(key).getDefaultValue()) {
                    if (value == "") continue;
                }
            }
            
            // Store the item.
            HashMap item = new HashMap();
            item.put("value", items.get(key).get());
            item.put("yDefaultValue", items.get(key).getDefaultValue());
            item.put("yOldValue", items.get(key).getOldValue());
            item.put("zDescription", items.get(key).getDescription());
            itemMap.put(key, item);
            item.put("zzzEmpty", new HashMap());
        }
        result.put("items", itemMap);
        
        HashMap groupMap = new HashMap();
        for (String key : groups.keySet()) {
            if (!exclusions.containsKey(key)) {
                groupMap.put(key, buildTree(groups.get(key)._getGroups(), groups.get(key)._getItems()));
            }
        }
        result.put("groups", groupMap);
        
        return result;
    }
    
    public void load(String fileName, HashMap<String, Group> groups, HashMap<String, Item> items) {
        load(fileName, groups, items, new HashMap<String, Boolean>());
    }
    
    public void load(String fileName, HashMap<String, Group> groups, HashMap<String, Item> items, HashMap<String, Boolean> exclusions) {
        if (!new File(fileName).exists()) {
            this.debug.out(0, "Could not find \"" + fileName + "\". Skipping. If this is your first time running the application, or you've just updated it, this is completely normal.");
            return;
        }
        
        this.debug.out(1, "Load config file: " + fileName);
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(new File(fileName));
            Map<String, Object> obj = yaml.load(inputStream);
            
            walkInput(fileName, obj, groups, items, exclusions);
        } catch (IOException e) {
            this.debug.out(0, "Failed to load config from " + fileName + ". Here is a stack trace for debugging:");
            e.printStackTrace();
            return ;
        }
    }
    
    private void walkInput(String path, Map obj, HashMap<String, Group> outputGroups, HashMap<String, Item> outputItems) {
        walkInput(path, obj, outputGroups, outputItems, new HashMap<String, Boolean>());
    }
    
    private void walkInput(String path, Map obj, HashMap<String, Group> outputGroups, HashMap<String, Item> outputItems, HashMap<String, Boolean> exclusions) {
        Map inputItems = new HashMap();
        if (obj.containsKey("items")) inputItems = (Map) obj.get("items");
        else {
            this.debug.out(1, "No items in " + path + ". This will be fixed on save.");
        }
        for (Object rawKey : inputItems.keySet()) {
            String key = (String) rawKey;
            String fullPath = path + "." + key;
            
            if (!outputItems.containsKey(key)) {
                this.debug.out(0, "Error: " + fullPath + " references a config item that does not exist. You probably won't get the output that you're expecting.");
                continue;
            }
            
            Map inputItem = (Map) inputItems.get(key);
            
            String inputValue = safeGet(inputItem, fullPath, "value", "");
            String oldInputValue = safeGet(inputItem, fullPath, "yOldValue", inputValue);
            String inputDefaultValue = safeGet(inputItem, fullPath, "yDefaultValue", inputValue);
            String outputDefaultValue = outputItems.get(key).getDefaultValue();
            
            if (!inputDefaultValue.equals(inputValue)) {
                if (!inputDefaultValue.equals(outputDefaultValue)) {
                    this.debug.out(0, "Warning: The defaultValue for " + fullPath + " has been updated from \"" + inputDefaultValue + "\" to \"" + outputDefaultValue + "\". But the value has been changed from the default to \"" + inputValue + "\", so the change in default is not going to take effect. This message will not show again.");
                }
                
                outputItems.get(key).set(oldInputValue);
                outputItems.get(key).set(inputValue);
                outputItems.get(key).makeClean();
            }
        }
        
        Map inputGroups = new HashMap();
        if (obj.containsKey("groups")) inputGroups = (Map) obj.get("groups");
        else {
            this.debug.out(1, "No groups in " + path + ". This will be fixed on save.");
        }
        for (Object rawKey : inputGroups.keySet()) {
            String key = (String) rawKey;
            if (exclusions.containsKey(key)) continue;
            
            String fullPath = path + "." + key;
            Map inputGroup = (Map) inputGroups.get(key);
            Group outputGroup = outputGroups.get(key);
            
            if (!outputGroups.containsKey(key)) {
                this.debug.out(0, "Error: " + fullPath + " references a config group that does not exist. You probably won't get the output that you're expecting.");
                continue;
            }
            
            walkInput(fullPath, inputGroup, outputGroup._getGroups(), outputGroup._getItems());
            outputGroup.makeClean();
        }
    }
    
    private String safeGet(Map map, String path, String key, String defaultValue) {
        if (map.containsKey(key)) {
            return (String) map.get(key);
        } 
        
        this.debug.out(1, "No " + key + " entry in " + path + ". Assuming value of " + defaultValue + ".");
        return defaultValue;
    }
}
