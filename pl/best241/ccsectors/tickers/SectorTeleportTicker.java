// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.tickers;

import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccsectors.api.CcSectorsAPI;
import pl.best241.ccsectors.messages.MessagesManager;
import pl.best241.ccsectors.api.TeleportLocation;
import org.bukkit.Material;
import pl.best241.ccsectors.config.SectorDataConfig;
import pl.best241.ccsectors.config.SectorPartDataConfig;
import redis.clients.jedis.Jedis;
import pl.best241.ccsectors.backend.RedisBackend;
import pl.best241.ccsectors.data.SafeTeleportType;
import pl.best241.ccsectors.data.DimSwitch;
import pl.best241.ccsectors.data.PlayerData;
import pl.best241.ccsectors.data.DataStore;
import org.bukkit.ChatColor;
import pl.best241.rdbplugin.JedisFactory;
import pl.best241.ccsectors.data.SectorType;
import pl.best241.ccsectors.managers.SectorManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import pl.best241.ccsectors.CcSectors;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import org.bukkit.Location;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class SectorTeleportTicker implements Listener
{
    private static final HashMap<UUID, Location> fromLocs;
    private static final ArrayList<UUID> teleported;
    private static final HashMap<UUID, Integer> secondsInPortal;
    private static final int minSecondsInPortalToTeleport = 4;
    
    @EventHandler
    public static void startTicker() {
        Bukkit.getScheduler().runTaskTimer((Plugin)CcSectors.getPlugin(), () -> {}, 100L, 100L);
    }
    
    public static void addPlayer(final Player player) {
        SectorTeleportTicker.fromLocs.put(player.getUniqueId(), player.getLocation());
    }
    
    public static void removePlayer(final Player player) {
        SectorTeleportTicker.fromLocs.remove(player.getUniqueId());
    }
    
    public static void removeTeleported(final UUID uuid) {
        SectorTeleportTicker.teleported.remove(uuid);
    }
    
    public static boolean isTeleported(final UUID uuid) {
        return SectorTeleportTicker.teleported.contains(uuid);
    }
    
    public static void addTeleported(final UUID uuid) {
        SectorTeleportTicker.teleported.add(uuid);
    }
    
    public static void removeTeleportedAfter(final UUID uuid, final int time) {
        Bukkit.getScheduler().runTaskLater((Plugin)CcSectors.getPlugin(), () -> removeTeleported(uuid), (long)time);
    }
    
    public static void savePlayerDataTicker() {
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
            return;
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)CcSectors.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                final Jedis jedis = JedisFactory.getInstance().getJedis();
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.DARK_GREEN + "Zapis ekwipunku graczy rozpoczety!");
                final long startTime = System.currentTimeMillis();
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final SectorPartDataConfig fromSectorPart = SectorManager.getCurrentSectorPart();
                    final SectorDataConfig fromSectorInstance = SectorManager.getCurrentSectorInstance();
                    final String fromSectorName = fromSectorPart.getSectorPartName() + "_" + fromSectorInstance.getSectorInstanceName();
                    final PlayerData oldData = DataStore.getPlayerData(player.getUniqueId());
                    PlayerData newData;
                    if (oldData != null) {
                        newData = new PlayerData(player, oldData.getDimSwitch(), oldData.getSafeTeleportType());
                    }
                    else {
                        newData = new PlayerData(player, DimSwitch.NORMAL, SafeTeleportType.SAFE);
                    }
                    newData.setLastSector(fromSectorName);
                    newData.setNeedingSave(true);
                    RedisBackend.setPlayerData(player.getUniqueId(), newData, jedis);
                    RedisBackend.setPlayerLastSector(player.getUniqueId(), fromSectorName, jedis);
                    if (player.hasPermission("ccSectors.viewSave")) {
                        player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.DARK_GREEN + "Zapis ekwipunku graczy!");
                    }
                }
                JedisFactory.getInstance().returnJedis(jedis);
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.DARK_GREEN + "Zapis ekwipunku graczy zakonczony! " + (System.currentTimeMillis() - startTime) + "ms");
            }
        }, 6000L, 6000L);
    }
    
    public static void runNetherTicker() {
        final Player[] array;
        int length;
        int i;
        Player online;
        UUID uuid;
        Integer time;
        TeleportLocation from;
        TeleportLocation to;
        Integer time2;
        Bukkit.getScheduler().runTaskTimer((Plugin)CcSectors.getPlugin(), () -> {
            Bukkit.getOnlinePlayers();
            for (length = array.length; i < length; ++i) {
                online = array[i];
                uuid = online.getUniqueId();
                if (online.getLocation().getBlock().getType() == Material.PORTAL) {
                    System.out.println(online.getName() + " is portal");
                    time = SectorTeleportTicker.secondsInPortal.get(uuid);
                    if (time == null) {
                        time = 0;
                    }
                    if (time >= 4) {
                        from = new TeleportLocation(SectorManager.getCurrentSectorPart().getSectorType(), online.getLocation().getX(), online.getLocation().getY(), online.getLocation().getZ(), online.getLocation().getYaw(), online.getLocation().getPitch());
                        to = new TeleportLocation(null, online.getLocation().getX(), online.getLocation().getY(), online.getLocation().getZ(), online.getLocation().getYaw(), online.getLocation().getPitch());
                        if (from.getSectorType() == SectorType.WORLD) {
                            to.setSectorType(SectorType.NETHER);
                            if (!CcSectors.isNetherOn() && !online.hasPermission("ccSectors.nether")) {
                                online.sendMessage(MessagesManager.getMessage("netherIsClosedMessage", ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nether jest wylaczony! Zostanie wlaczony 6 listopada!"));
                                return;
                            }
                        }
                        else {
                            to.setSectorType(SectorType.WORLD);
                        }
                        try {
                            CcSectorsAPI.teleportPlayerAuto(uuid, from, to, SafeTeleportType.SAFE, DimSwitch.AUTO, true);
                        }
                        catch (Exception ex) {
                            Logger.getLogger(SectorTeleportTicker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        SectorTeleportTicker.secondsInPortal.remove(uuid);
                        return;
                    }
                    else {
                        time2 = time + 1;
                        SectorTeleportTicker.secondsInPortal.put(uuid, time2);
                    }
                }
                else {
                    SectorTeleportTicker.secondsInPortal.remove(uuid);
                }
            }
        }, 20L, 20L);
    }
    
    static {
        fromLocs = new HashMap<UUID, Location>();
        teleported = new ArrayList<UUID>();
        secondsInPortal = new HashMap<UUID, Integer>();
    }
}
