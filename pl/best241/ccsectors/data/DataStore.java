// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.data;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore
{
    private static final ConcurrentHashMap<UUID, PlayerData> players;
    private static final ConcurrentHashMap<UUID, String> playersOnSectors;
    private static final List<String> onlineNames;
    
    public static PlayerData getPlayerData(final UUID uuid) {
        return DataStore.players.get(uuid);
    }
    
    public static void removePlayerData(final UUID uuid) {
        DataStore.players.remove(uuid);
    }
    
    public static void setPlayerData(final UUID uuid, final PlayerData data) {
        DataStore.players.put(uuid, data);
    }
    
    public static boolean isPlayerSectorData(final UUID uuid) {
        return DataStore.players.containsKey(uuid);
    }
    
    public static String getPlayerOnSector(final UUID uuid) {
        return DataStore.playersOnSectors.get(uuid);
    }
    
    public static void addPlayerOnSector(final String sector, final UUID uuid) {
        DataStore.playersOnSectors.put(uuid, sector);
    }
    
    public static void clearPlayerOnSector() {
        DataStore.playersOnSectors.clear();
    }
    
    public static void clearPlayerNames() {
        DataStore.onlineNames.clear();
    }
    
    public static void setPlayersOnSector(final String sector, final HashSet<UUID> players) {
        for (final UUID player : players) {
            DataStore.playersOnSectors.put(player, sector);
        }
    }
    
    public static void removePlayerOnSector(final UUID uuid) {
        DataStore.playersOnSectors.remove(uuid);
    }
    
    public static void clearAllPlayers() {
        DataStore.playersOnSectors.clear();
    }
    
    public static boolean isPlayerOnSector(final String sector, final UUID uuid) {
        final String playerSector = DataStore.playersOnSectors.get(uuid);
        return playerSector != null && playerSector.equals(sector);
    }
    
    public static ConcurrentHashMap<UUID, String> getPlayersOnSectors() {
        return DataStore.playersOnSectors;
    }
    
    public static HashSet<UUID> getOnlinePlayers() {
        final HashSet<UUID> players = new HashSet<UUID>();
        final Enumeration<UUID> keys = DataStore.playersOnSectors.keys();
        while (keys.hasMoreElements()) {
            players.add(keys.nextElement());
        }
        return players;
    }
    
    public static ArrayList<UUID> getOnlinePlayersList() {
        final ArrayList<UUID> onlinePlayers = new ArrayList<UUID>();
        final Enumeration<UUID> keys = DataStore.playersOnSectors.keys();
        while (keys.hasMoreElements()) {
            onlinePlayers.add(keys.nextElement());
        }
        return onlinePlayers;
    }
    
    public static void addPlayerName(final String nick) {
        if (!DataStore.onlineNames.contains(nick)) {
            DataStore.onlineNames.add(nick);
        }
    }
    
    public static void removePlayerName(final String nick) {
        DataStore.onlineNames.remove(nick);
    }
    
    public static List<String> getPlayerNames() {
        return DataStore.onlineNames;
    }
    
    static {
        players = new ConcurrentHashMap<UUID, PlayerData>();
        playersOnSectors = new ConcurrentHashMap<UUID, String>();
        onlineNames = Collections.synchronizedList(new ArrayList<String>());
    }
}
