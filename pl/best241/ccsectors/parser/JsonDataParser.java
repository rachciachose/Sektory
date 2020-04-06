// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.parser;

import java.io.FileWriter;
import java.io.Reader;
import java.io.FileReader;
import pl.best241.ccsectors.config.SectorPluginConfig;
import pl.best241.ccsectors.data.PlayerData;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;

public class JsonDataParser
{
    private static Gson gson;
    
    public static void loadGson() {
        final GsonBuilder config = new GsonBuilder();
        JsonDataParser.gson = config.setPrettyPrinting().create();
    }
    
    public static String convertPlayerDataToJson(final PlayerData object) {
        return JsonDataParser.gson.toJson((Object)object);
    }
    
    public static PlayerData convertJsonToPlayerData(final String json) {
        return (PlayerData)JsonDataParser.gson.fromJson(json, (Class)PlayerData.class);
    }
    
    public static String convertSectorPluginConfigToJson(final SectorPluginConfig object) {
        return JsonDataParser.gson.toJson((Object)object);
    }
    
    public static SectorPluginConfig convertJsonToSectorPluginConfig(final String json) {
        return (SectorPluginConfig)JsonDataParser.gson.fromJson(json, (Class)SectorPluginConfig.class);
    }
    
    public static SectorPluginConfig convertFileToSectorPluginConfig(final FileReader reader) {
        return (SectorPluginConfig)JsonDataParser.gson.fromJson((Reader)reader, (Class)SectorPluginConfig.class);
    }
    
    public static void saveSectorPluginConfigToFile(final FileWriter writer, final SectorPluginConfig object) {
        JsonDataParser.gson.toJson((Object)object, (Appendable)writer);
    }
    
    public static Gson getGson() {
        return JsonDataParser.gson;
    }
}
