// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.messages;

import java.util.Iterator;
import java.util.Set;
import org.bukkit.ChatColor;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.Configuration;
import java.io.InputStreamReader;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.plugin.Plugin;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class MessagesManager
{
    private FileConfiguration customConfig;
    private File customConfigFile;
    private final Plugin plugin;
    private final String configName;
    private static MessagesManager config;
    private static final HashMap<String, String> pathMessage;
    private static final HashMap<String, ArrayList<String>> pathMessageList;
    
    public MessagesManager(final Plugin plugin, final String configName) {
        this.customConfig = null;
        this.customConfigFile = null;
        this.plugin = plugin;
        this.configName = configName;
    }
    
    public void reloadCustomConfig() {
        Reader defConfigStream = null;
        try {
            if (this.customConfigFile == null) {
                this.customConfigFile = new File(this.plugin.getDataFolder(), this.configName);
            }
            this.customConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(this.customConfigFile);
            defConfigStream = new InputStreamReader(this.plugin.getResource(this.configName), "UTF8");
            if (defConfigStream != null) {
                final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                this.customConfig.setDefaults((Configuration)defConfig);
            }
        }
        catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MessagesManager.class.getName()).log(Level.SEVERE, null, ex);
            try {
                defConfigStream.close();
            }
            catch (IOException ex2) {
                Logger.getLogger(MessagesManager.class.getName()).log(Level.SEVERE, null, ex2);
            }
        }
        finally {
            try {
                defConfigStream.close();
            }
            catch (IOException ex3) {
                Logger.getLogger(MessagesManager.class.getName()).log(Level.SEVERE, null, ex3);
            }
        }
    }
    
    public FileConfiguration getCustomConfig() {
        if (this.customConfig == null) {
            this.reloadCustomConfig();
        }
        return this.customConfig;
    }
    
    public void saveCustomConfig() {
        if (this.customConfig == null || this.customConfigFile == null) {
            return;
        }
        try {
            this.getCustomConfig().save(this.customConfigFile);
        }
        catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.customConfigFile, ex);
        }
    }
    
    public void saveDefaultConfig() {
        if (this.customConfigFile == null) {
            this.customConfigFile = new File(this.plugin.getDataFolder(), this.configName);
        }
        if (!this.customConfigFile.exists()) {
            this.plugin.saveResource(this.configName, false);
        }
    }
    
    public String getString(final String path) {
        final String data = this.getCustomConfig().getString(path);
        if (data == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', data);
    }
    
    public static void loadMessages(final Plugin plugin) {
        (MessagesManager.config = new MessagesManager(plugin, "messages.yml")).saveDefaultConfig();
        MessagesManager.config.reloadCustomConfig();
        final Set<String> keys = (Set<String>)MessagesManager.config.getCustomConfig().getKeys(true);
        MessagesManager.pathMessage.clear();
        MessagesManager.pathMessageList.clear();
    }
    
    public static String getMessage(final String path) {
        if (MessagesManager.pathMessage.containsKey(path)) {
            return MessagesManager.pathMessage.get(path);
        }
        final String message = MessagesManager.config.getString(path);
        MessagesManager.pathMessage.put(path, message);
        return message;
    }
    
    public static String getMessage(final String path, final String defaultValue) {
        final String message = getMessage(path);
        if (message == null) {
            return defaultValue;
        }
        return message;
    }
    
    public static ArrayList<String> getMessageList(final String path) {
        if (MessagesManager.pathMessageList.containsKey(path)) {
            return MessagesManager.pathMessageList.get(path);
        }
        final ArrayList<String> list = (ArrayList<String>)MessagesManager.config.getCustomConfig().getList(path);
        final ArrayList<String> coloredList = new ArrayList<String>();
        for (final String message : list) {
            coloredList.add(ChatColor.translateAlternateColorCodes('&', message));
        }
        MessagesManager.pathMessageList.put(path, coloredList);
        return coloredList;
    }
    
    static {
        pathMessage = new HashMap<String, String>();
        pathMessageList = new HashMap<String, ArrayList<String>>();
    }
}
