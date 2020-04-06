// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.config;

import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileReader;
import java.io.File;
import pl.best241.ccsectors.CcSectors;
import pl.best241.ccsectors.parser.JsonDataParser;

public class ConfigManager
{
    private static SectorPluginConfig config;
    
    public static String parseConfig(final SectorPluginConfig config) {
        return JsonDataParser.convertSectorPluginConfigToJson(config);
    }
    
    public static void loadConfig(final String fileName) {
        final File file = new File(CcSectors.getPlugin().getDataFolder(), fileName);
        if (file.exists() || file.length() != 0L) {
            FileReader reader = null;
            try {
                reader = new FileReader(file);
                ConfigManager.config = JsonDataParser.convertFileToSectorPluginConfig(reader);
                System.out.println("Config was readen");
            }
            catch (FileNotFoundException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    reader.close();
                }
                catch (IOException ex2) {
                    Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex2);
                }
            }
            finally {
                try {
                    reader.close();
                }
                catch (IOException ex3) {
                    Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex3);
                }
            }
        }
        else {
            try {
                file.createNewFile();
                try {
                    ConfigManager.config = DefaultConfigLoader.getDefaultSectorPluginConfig();
                    final String json = JsonDataParser.convertSectorPluginConfigToJson(ConfigManager.config);
                    final FileWriter fw = new FileWriter(file);
                    fw.write(json);
                    fw.flush();
                    fw.close();
                }
                catch (IOException ex4) {
                    Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex4);
                }
            }
            catch (IOException ex4) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex4);
            }
        }
    }
    
    public static SectorPluginConfig getConfig() {
        return ConfigManager.config;
    }
}
