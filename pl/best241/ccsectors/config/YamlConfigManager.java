// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.config;

import java.io.File;
import pl.best241.ccsectors.CcSectors;

public class YamlConfigManager
{
    private static CcSectors plugin;
    public static String currentSectorPartName;
    public static String currentSectorName;
    
    public static void load(final String file) {
        YamlConfigManager.plugin = CcSectors.getPlugin();
        final File fileConfig = new File(YamlConfigManager.plugin.getDataFolder(), file);
        if (!fileConfig.exists()) {
            System.out.println("Nie znaleziono config.yml! Generowanie!");
            YamlConfigManager.plugin.getConfig().options().copyDefaults(true);
            YamlConfigManager.plugin.saveConfig();
        }
        YamlConfigManager.plugin.getConfig();
        YamlConfigManager.currentSectorPartName = YamlConfigManager.plugin.getConfig().getString("currentSectorPartName");
        YamlConfigManager.currentSectorName = YamlConfigManager.plugin.getConfig().getString("currentSectorName");
    }
}
