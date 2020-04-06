// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.messages;

import org.bukkit.ChatColor;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.Configuration;
import java.io.InputStreamReader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class MessagesConfig
{
    private FileConfiguration customConfig;
    private File customConfigFile;
    private final Plugin plugin;
    private final String configName;
    
    public MessagesConfig(final Plugin plugin, final String configName) {
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
            Logger.getLogger(MessagesConfig.class.getName()).log(Level.SEVERE, null, ex);
            try {
                defConfigStream.close();
            }
            catch (IOException ex2) {
                Logger.getLogger(MessagesConfig.class.getName()).log(Level.SEVERE, null, ex2);
            }
        }
        finally {
            try {
                defConfigStream.close();
            }
            catch (IOException ex3) {
                Logger.getLogger(MessagesConfig.class.getName()).log(Level.SEVERE, null, ex3);
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
        return ChatColor.translateAlternateColorCodes('&', this.getCustomConfig().getString(path));
    }
}
