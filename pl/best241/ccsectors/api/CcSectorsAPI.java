// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.api;

import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import java.util.Iterator;
import pl.best241.ccsectors.backend.RedisBackend;
import pl.best241.rdbplugin.pubsub.PubSub;
import org.json.simple.JSONObject;
import pl.best241.ccsectors.managers.TeleportManager;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.manager.GuildManager;
import redis.clients.jedis.Jedis;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.rdbplugin.JedisFactory;
import org.bukkit.inventory.Inventory;
import pl.best241.ccsectors.CcSectors;
import org.bukkit.Location;
import pl.best241.ccsectors.config.SectorDataConfig;
import pl.best241.ccsectors.config.SectorPartDataConfig;
import pl.best241.ccsectors.managers.RedirectManager;
import pl.best241.ccsectors.data.DimSwitch;
import pl.best241.ccsectors.data.SectorType;
import pl.best241.ccsectors.data.PlayerData;
import java.util.UUID;
import org.bukkit.World;
import pl.best241.ccsectors.data.SafeTeleportType;
import pl.best241.ccsectors.data.DataStore;
import org.bukkit.Bukkit;
import pl.best241.ccsectors.managers.SectorManager;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import java.util.Random;

public class CcSectorsAPI
{
    private static final int netherXZFactor = 5;
    private static final int netherYFactor = 2;
    private static Random random;
    private static int minBorder;
    private static final Gson gson;
    
    public static int getNetherXZFactor() {
        return 5;
    }
    
    public static int getNetherYFactor() {
        return 2;
    }
    
    public static void safeTeleportPlayerOnTopBlock(final Player player, final TeleportLocation loc) {
        final World world = Bukkit.getWorld(SectorManager.getCurrentSectorInstance().getWorldName());
        final UUID uuid = player.getUniqueId();
        final PlayerData data = DataStore.getPlayerData(player.getUniqueId());
        data.setSafeTeleprotType(SafeTeleportType.TOP);
        data.setLocation(loc);
        data.setNeedingSave(true);
        DataStore.setPlayerData(uuid, data);
        player.teleport(loc.toLocation(world));
        DataStore.setPlayerData(uuid, data);
    }
    
    public static void safeTeleportPlayerOnBottomBlock(final Player player, final TeleportLocation loc) {
        final World world = Bukkit.getWorld(SectorManager.getCurrentSectorInstance().getWorldName());
        final UUID uuid = player.getUniqueId();
        final PlayerData data = DataStore.getPlayerData(player.getUniqueId());
        data.setSafeTeleprotType(SafeTeleportType.BOTTOM);
        data.setLocation(loc);
        data.setNeedingSave(true);
        DataStore.setPlayerData(uuid, data);
        player.teleport(loc.toLocation(world));
        DataStore.setPlayerData(uuid, data);
    }
    
    public static void safeTeleportPlayer(final Player player, final TeleportLocation loc) {
        final World world = Bukkit.getWorld(SectorManager.getCurrentSectorInstance().getWorldName());
        final UUID uuid = player.getUniqueId();
        final PlayerData data = DataStore.getPlayerData(uuid);
        data.setSafeTeleprotType(SafeTeleportType.SAFE);
        data.setLocation(loc);
        data.setNeedingSave(true);
        DataStore.setPlayerData(uuid, data);
        player.teleport(loc.toLocation(world));
        DataStore.setPlayerData(uuid, data);
    }
    
    public static void safeTeleportPlayerDimension(final Player player, final TeleportLocation loc, final SectorType type) {
        final UUID uuid = player.getUniqueId();
        System.out.println("Dimension teleport player " + player.getName() + " " + type);
        if (type == SectorManager.getCurrentSectorPart().getSectorType()) {
            safeTeleportPlayer(player, loc);
        }
        else if (type == SectorType.WORLD) {
            final PlayerData data = DataStore.getPlayerData(uuid);
            final SectorPartDataConfig toSectorPart = SectorManager.getCorrectSectorSectorTypeSensive(loc.getX(), loc.getY(), SectorType.WORLD);
            final SectorDataConfig toSectorInstance = toSectorPart.getRandomSectorInstance();
            final String sectorName = toSectorPart.getSectorPartName() + "_" + toSectorInstance.getSectorInstanceName();
            data.setLastSector(sectorName);
            data.setDimSwitch(DimSwitch.SAME);
            data.setSafeTeleprotType(SafeTeleportType.SAFE);
            System.out.println(toSectorInstance.getWorldName());
            data.setSerializedLocation(toSectorPart.getSectorType(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            data.setNeedingSave(true);
            DataStore.setPlayerData(uuid, data);
            RedirectManager.redirectRequest(sectorName, player, true);
        }
        else if (type == SectorType.NETHER) {
            final PlayerData data = DataStore.getPlayerData(uuid);
            final SectorPartDataConfig toSectorPart = SectorManager.getNetherSectorPart();
            final SectorDataConfig toSectorInstance = toSectorPart.getRandomSectorInstance();
            final String sectorName = toSectorPart.getSectorPartName() + "_" + toSectorInstance.getSectorInstanceName();
            data.setLastSector(sectorName);
            data.setDimSwitch(DimSwitch.SAME);
            data.setSafeTeleprotType(SafeTeleportType.SAFE);
            System.out.println(toSectorInstance.getWorldName());
            data.setSerializedLocation(toSectorPart.getSectorType(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            data.setNeedingSave(true);
            DataStore.setPlayerData(uuid, data);
            RedirectManager.redirectRequest(sectorName, player, true);
        }
    }
    
    public static boolean isPlayerOnline(final UUID uuid) {
        return DataStore.getOnlinePlayers().contains(uuid);
    }
    
    public static int getOnlinePlayersNumber() {
        return DataStore.getOnlinePlayers().size();
    }
    
    public static double getDistanceToNearestSector(final Location loc) {
        return SectorManager.getDistanceToNearestSector(loc, 0);
    }
    
    public static UUID getUUID(final String nick) throws Exception {
        return CcSectors.getBackend().getPlayerLastUUID(nick);
    }
    
    public static String getNick(final UUID uuid) throws Exception {
        return CcSectors.getBackend().getPlayerLastNick(uuid);
    }
    
    public static boolean canMakeCuboids(final Location loc) {
        return !SectorManager.getCurrentSectorPart().getSectorPartName().equalsIgnoreCase("SPAWN");
    }
    
    public static SectorType getSectorType() {
        return SectorManager.getCurrentSectorPart().getSectorType();
    }
    
    public static String getSectorName() {
        return SectorManager.getCurrentSectorPart().getSectorPartName() + "_" + SectorManager.getCurrentSectorInstance().getSectorInstanceName();
    }
    
    public static String getCorrectSectorName(final TeleportLocation to) {
        final SectorPartDataConfig toSectorPart = SectorManager.getCorrectSectorTypeSensive(to);
        final SectorDataConfig toSectorInstance = toSectorPart.getSectorInstances().get(new Random().nextInt(toSectorPart.getSectorInstances().size()));
        final String sectorName = toSectorPart.getSectorPartName() + "_" + toSectorInstance.getSectorInstanceName();
        return sectorName;
    }
    
    public static Inventory getPlayerInventory(final UUID uuid) throws Exception {
        final PlayerData data = CcSectors.getBackend().getPlayerData(uuid);
        if (data == null) {
            return null;
        }
        return data.getInventory();
    }
    
    public static Inventory getPlayerEnderchest(final UUID uuid) throws Exception {
        final PlayerData data = CcSectors.getBackend().getPlayerData(uuid);
        if (data == null) {
            return null;
        }
        return data.getEnderchest();
    }
    
    public static void savePlayerInventory(final UUID uuid, final Inventory inv) throws Exception {
        final PlayerData playerData = CcSectors.getBackend().getPlayerData(uuid);
        playerData.updateInventory(inv);
        CcSectors.getBackend().setPlayerData(uuid, playerData);
    }
    
    public static void savePlayerEnderchest(final UUID uuid, final Inventory inv) throws Exception {
        final PlayerData playerData = CcSectors.getBackend().getPlayerData(uuid);
        playerData.updateEnderchest(inv);
        CcSectors.getBackend().setPlayerData(uuid, playerData);
    }
    
    public static String getPlayerLastSector(final UUID uuid) throws Exception {
        final String lastSector = CcSectors.getBackend().getPlayerLastSector(uuid);
        return lastSector;
    }
    
    public static void teleportRandom(final Player player, final int border) {
        try {
            final TeleportLocation loc = getRandomLocation(SectorManager.getCurrentSectorPart().getSectorType(), border, true);
            final SectorPartDataConfig correctSectorPartName = SectorManager.getCorrectSector(loc);
            final SectorDataConfig randomSectorInstance = correctSectorPartName.getRandomSectorInstance();
            final String targetName = correctSectorPartName.getSectorPartName() + "_" + randomSectorInstance.getSectorInstanceName();
            final String senderName = SectorManager.getCurrentSectorPart().getSectorPartName() + "_" + SectorManager.getCurrentSectorInstance().getSectorInstanceName();
            final Jedis jedis = JedisFactory.getInstance().getJedis();
            jedis.publish("ccSectors.randomTpAskForCoords", player.getUniqueId() + ":" + senderName + ":" + targetName + ":" + border + ":" + loc.getSectorType().toString() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ());
            JedisFactory.getInstance().returnJedis(jedis);
        }
        catch (Exception ex) {
            Logger.getLogger(CcSectorsAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void teleportRandomAccepted(final Player player, final TeleportLocation loc) {
        try {
            teleportPlayerAuto(player.getUniqueId(), new TeleportLocation(player.getLocation()), loc, SafeTeleportType.TOP, DimSwitch.SAME, false);
        }
        catch (Exception ex) {
            Logger.getLogger(CcSectorsAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static TeleportLocation getRandomLocation(final SectorType sectorType, final int border, final boolean onGuilds) {
        int x = CcSectorsAPI.random.nextInt(border - CcSectorsAPI.minBorder) + CcSectorsAPI.minBorder;
        int z = CcSectorsAPI.random.nextInt(border - CcSectorsAPI.minBorder) + CcSectorsAPI.minBorder;
        System.out.println(x + " " + z);
        if (CcSectorsAPI.random.nextBoolean()) {
            x *= -1;
        }
        if (CcSectorsAPI.random.nextBoolean()) {
            z *= -1;
        }
        final TeleportLocation teleportLoc = new TeleportLocation(sectorType, x + 0.5, -1.0, z + 0.5);
        if (onGuilds) {
            boolean canTeleport = false;
            for (int i = 0; i < 15; ++i) {
                final GuildData guildDataByLocation = GuildManager.getGuildDataByLocation(teleportLoc);
                if (guildDataByLocation == null) {
                    canTeleport = true;
                    break;
                }
            }
            if (!canTeleport) {
                return null;
            }
        }
        return teleportLoc;
    }
    
    private static TeleportLocation getRandomLocation(final SectorType sectorType, final int border) {
        return getRandomLocation(sectorType, border, true);
    }
    
    public static void teleportRandomPlayers(final Player[] players, final int border) {
        if (players.length == 0) {
            return;
        }
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        try {
            final TeleportLocation loc = getRandomLocation(SectorManager.getCurrentSectorPart().getSectorType(), border, true);
            final SectorPartDataConfig correctSectorPartName = SectorManager.getCorrectSector(loc);
            final SectorDataConfig randomSectorInstance = correctSectorPartName.getRandomSectorInstance();
            final String targetName = correctSectorPartName.getSectorPartName() + "_" + randomSectorInstance.getSectorInstanceName();
            final String senderName = SectorManager.getCurrentSectorPart().getSectorPartName() + "_" + SectorManager.getCurrentSectorInstance().getSectorInstanceName();
            String playersUUID = "";
            for (final Player player : players) {
                playersUUID = playersUUID + ";" + player.getUniqueId();
            }
            playersUUID = playersUUID.substring(1);
            jedis.publish("ccSectors.randomTpPlayersAskForCoords", playersUUID + ":" + senderName + ":" + targetName + ":" + border + ":" + loc.getSectorType().toString() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ());
        }
        catch (Exception ex) {
            Logger.getLogger(CcSectorsAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            JedisFactory.getInstance().returnJedis(jedis);
        }
    }
    
    public static void teleportRandomAccepted(final Player[] players, final TeleportLocation loc) {
        try {
            for (final Player player : players) {
                teleportPlayerAuto(player.getUniqueId(), new TeleportLocation(player.getLocation()), loc, SafeTeleportType.TOP, DimSwitch.SAME, false);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(CcSectorsAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void teleportPlayerAuto(final UUID uuid, final TeleportLocation from, final TeleportLocation to, final SafeTeleportType safeTeleportType, DimSwitch dimSwitch, final boolean teleportByPortal) throws Exception {
        final SectorPartDataConfig currentSectorPart = SectorManager.getCurrentSectorPart();
        final SectorDataConfig currentInstance = SectorManager.getCurrentSectorInstance();
        final SectorPartDataConfig sectorFrom = SectorManager.getCorrectSectorSectorTypeSensive(from.getX(), from.getZ(), from.getSectorType());
        final SectorPartDataConfig sectorTo = SectorManager.getCorrectSectorSectorTypeSensive(to.getX(), to.getZ(), to.getSectorType());
        System.out.println(sectorFrom.getSectorType() + " " + sectorTo.getSectorType());
        if (from.getSectorType() == to.getSectorType()) {
            if (currentSectorPart.getSectorPartName().equals(sectorFrom.getSectorPartName())) {
                boolean fromOnTheSameInstance = false;
                for (final SectorDataConfig instance : sectorFrom.getSectorInstances()) {
                    if (instance.getSectorInstanceName().equals(currentInstance.getSectorInstanceName())) {
                        fromOnTheSameInstance = true;
                    }
                }
                if (fromOnTheSameInstance) {
                    final Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !player.isOnline()) {
                        throw new Exception("Player should be on the same sector when it was invoked, but he is offline");
                    }
                    if (sectorTo.getSectorPartName().equals(sectorFrom.getSectorPartName())) {
                        final World world = Bukkit.getWorld(currentInstance.getWorldName());
                        if (world == null) {
                            throw new Exception("World doesn't exists! Name:" + currentInstance.getWorldName());
                        }
                        final TeleportLocation safeLocation = TeleportManager.getSafeLocation(to, safeTeleportType);
                        player.teleport(safeLocation.toLocation(world));
                    }
                    else {
                        if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                            dimSwitch = DimSwitch.SAME;
                        }
                        final SectorDataConfig toSectorInstance = sectorTo.getRandomSectorInstance();
                        final String sectorName = sectorTo.getSectorPartName() + "_" + toSectorInstance.getSectorInstanceName();
                        final PlayerData data = DataStore.getPlayerData(uuid);
                        data.setLastSector(sectorName);
                        data.setDimSwitch(dimSwitch);
                        data.setSafeTeleprotType(safeTeleportType);
                        data.setSerializedLocation(SectorManager.getCurrentSectorPart().getSectorType(), to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
                        data.setNeedingSave(true);
                        DataStore.setPlayerData(uuid, data);
                        RedirectManager.redirectRequest(sectorName, player, true);
                    }
                }
                else if (sectorTo.getSectorPartName().equals(sectorFrom.getSectorPartName())) {
                    if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                        dimSwitch = DimSwitch.SAME;
                    }
                    final JSONObject jsonFields = new JSONObject();
                    jsonFields.put((Object)"teleportType", (Object)"single_server_teleport");
                    jsonFields.put((Object)"uuid", (Object)uuid.toString());
                    jsonFields.put((Object)"toTeleportLocation", (Object)to);
                    jsonFields.put((Object)"safeTeleportType", (Object)safeTeleportType);
                    jsonFields.put((Object)"dimSwitch", (Object)dimSwitch);
                    PubSub.broadcast("ccSectors.remoteSectorTeleport", CcSectorsAPI.gson.toJson((Object)jsonFields));
                }
                else {
                    if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                        dimSwitch = DimSwitch.SAME;
                    }
                    final JSONObject jsonFields = new JSONObject();
                    jsonFields.put((Object)"teleportType", (Object)"cross_server_teleport");
                    jsonFields.put((Object)"uuid", (Object)uuid.toString());
                    jsonFields.put((Object)"toTeleportLocation", (Object)to);
                    jsonFields.put((Object)"safeTeleportType", (Object)safeTeleportType);
                    jsonFields.put((Object)"dimSwitch", (Object)dimSwitch);
                    PubSub.broadcast("ccSectors.remoteSectorTeleport", CcSectorsAPI.gson.toJson((Object)jsonFields));
                }
            }
            else if (sectorTo.getSectorPartName().equals(sectorFrom.getSectorPartName())) {
                if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                    dimSwitch = DimSwitch.SAME;
                }
                final JSONObject jsonFields2 = new JSONObject();
                jsonFields2.put((Object)"teleportType", (Object)"single_server_teleport");
                jsonFields2.put((Object)"uuid", (Object)uuid.toString());
                jsonFields2.put((Object)"toTeleportLocation", (Object)to);
                jsonFields2.put((Object)"safeTeleportType", (Object)safeTeleportType);
                jsonFields2.put((Object)"dimSwitch", (Object)dimSwitch);
                PubSub.broadcast("ccSectors.remoteSectorTeleport", CcSectorsAPI.gson.toJson((Object)jsonFields2));
            }
            else {
                if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                    dimSwitch = DimSwitch.SAME;
                }
                final JSONObject jsonFields2 = new JSONObject();
                jsonFields2.put((Object)"teleportType", (Object)"cross_server_teleport");
                jsonFields2.put((Object)"uuid", (Object)uuid.toString());
                jsonFields2.put((Object)"toTeleportLocation", (Object)to);
                jsonFields2.put((Object)"safeTeleportType", (Object)safeTeleportType);
                jsonFields2.put((Object)"dimSwitch", (Object)dimSwitch);
                PubSub.broadcast("ccSectors.remoteSectorTeleport", CcSectorsAPI.gson.toJson((Object)jsonFields2));
            }
        }
        else if (currentSectorPart.getSectorPartName().equals(sectorFrom.getSectorPartName())) {
            boolean fromOnTheSameInstance = false;
            for (final SectorDataConfig instance : sectorFrom.getSectorInstances()) {
                if (instance.getSectorInstanceName().equals(currentInstance.getWorldName())) {
                    fromOnTheSameInstance = true;
                }
            }
            if (fromOnTheSameInstance) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) {
                    throw new Exception("Player should be on the same sector when it was invoked, but he is offline");
                }
                if (from.getSectorType() == SectorType.WORLD && to.getSectorType() == SectorType.NETHER) {
                    if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                        dimSwitch = DimSwitch.NETHER;
                    }
                    final PlayerData data2 = DataStore.getPlayerData(uuid);
                    final SectorPartDataConfig toSectorPart = SectorManager.getNetherSectorPart();
                    final SectorDataConfig toSectorInstance2 = toSectorPart.getRandomSectorInstance();
                    final String sectorName2 = toSectorPart.getSectorPartName() + "_" + toSectorInstance2.getSectorInstanceName();
                    data2.setLastSector(sectorName2);
                    data2.setDimSwitch(dimSwitch);
                    data2.setSafeTeleprotType(safeTeleportType);
                    data2.setSerializedLocation(SectorManager.getCurrentSectorPart().getSectorType(), from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch());
                    data2.setNeedingSave(true);
                    DataStore.setPlayerData(uuid, data2);
                    RedirectManager.redirectRequest(sectorName2, player, true);
                }
                else if (from.getSectorType() == SectorType.NETHER && to.getSectorType() == SectorType.WORLD) {
                    if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                        dimSwitch = DimSwitch.NORMAL;
                    }
                    final PlayerData data2 = DataStore.getPlayerData(uuid);
                    final SectorPartDataConfig toSectorPart = SectorManager.getCorrectSectorSectorTypeSensive(player.getLocation().getX() * getNetherXZFactor(), player.getLocation().getZ() * getNetherXZFactor(), SectorType.WORLD);
                    final SectorDataConfig toSectorInstance2 = toSectorPart.getRandomSectorInstance();
                    final String sectorName2 = toSectorPart.getSectorPartName() + "_" + toSectorInstance2.getSectorInstanceName();
                    data2.setLastSector(sectorName2);
                    data2.setDimSwitch(dimSwitch);
                    data2.setSafeTeleprotType(safeTeleportType);
                    data2.setSerializedLocation(SectorManager.getCurrentSectorPart().getSectorType(), from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch());
                    data2.setNeedingSave(true);
                    DataStore.setPlayerData(uuid, data2);
                    RedirectManager.redirectRequest(sectorName2, player, true);
                }
                else if ((from.getSectorType() == SectorType.WORLD || from.getSectorType() == SectorType.NETHER) && to.getSectorType() == SectorType.EVENT) {
                    final Jedis jedis = JedisFactory.getInstance().getJedis();
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
                    newData.setNeedingSave(false);
                    RedisBackend.setPlayerData(player.getUniqueId(), newData, jedis);
                    RedisBackend.setPlayerLastSector(player.getUniqueId(), fromSectorName, jedis);
                    JedisFactory.getInstance().returnJedis(jedis);
                    RedirectManager.redirectRequest(SectorManager.getEventSectorPart().getSectorPartName() + "_" + SectorManager.getEventSectorPart().getRandomSectorInstance().getSectorInstanceName(), player, false);
                }
                else if (from.getSectorType() == SectorType.EVENT && (to.getSectorType() == SectorType.WORLD || to.getSectorType() == SectorType.NETHER)) {
                    final SectorPartDataConfig correctSectorSectorTypeSensive = SectorManager.getCorrectSectorSectorTypeSensive(to.getX(), to.getZ(), to.getSectorType());
                    RedirectManager.redirectRequest(correctSectorSectorTypeSensive.getSectorPartName() + "_" + correctSectorSectorTypeSensive.getRandomSectorInstance().getSectorInstanceName(), player, false);
                }
            }
            else {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) {
                    throw new Exception("Player should be on the same sector when it was invoked, but he is offline");
                }
                if (from.getSectorType() == SectorType.WORLD && to.getSectorType() == SectorType.NETHER) {
                    if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                        dimSwitch = DimSwitch.NETHER;
                    }
                    final JSONObject jsonFields3 = new JSONObject();
                    jsonFields3.put((Object)"teleportType", (Object)"cross_server_dimension_teleport");
                    jsonFields3.put((Object)"uuid", (Object)uuid.toString());
                    jsonFields3.put((Object)"toTeleportLocation", (Object)to);
                    jsonFields3.put((Object)"safeTeleportType", (Object)safeTeleportType);
                    jsonFields3.put((Object)"dimSwitch", (Object)dimSwitch);
                    PubSub.broadcast("ccSectors.remoteSectorTeleport", CcSectorsAPI.gson.toJson((Object)jsonFields3));
                }
                else if (from.getSectorType() == SectorType.NETHER && to.getSectorType() == SectorType.WORLD) {
                    if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                        dimSwitch = DimSwitch.NORMAL;
                    }
                    final JSONObject jsonFields3 = new JSONObject();
                    jsonFields3.put((Object)"teleportType", (Object)"cross_server_dimension_teleport");
                    jsonFields3.put((Object)"uuid", (Object)uuid.toString());
                    jsonFields3.put((Object)"toTeleportLocation", (Object)to);
                    jsonFields3.put((Object)"safeTeleportType", (Object)safeTeleportType);
                    jsonFields3.put((Object)"dimSwitch", (Object)dimSwitch);
                    PubSub.broadcast("ccSectors.remoteSectorTeleport", CcSectorsAPI.gson.toJson((Object)jsonFields3));
                }
                else if ((from.getSectorType() == SectorType.WORLD || from.getSectorType() == SectorType.NETHER) && to.getSectorType() == SectorType.EVENT) {
                    final Jedis jedis = JedisFactory.getInstance().getJedis();
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
                    newData.setNeedingSave(false);
                    RedisBackend.setPlayerData(player.getUniqueId(), newData, jedis);
                    RedisBackend.setPlayerLastSector(player.getUniqueId(), fromSectorName, jedis);
                    JedisFactory.getInstance().returnJedis(jedis);
                    RedirectManager.redirectRequest(SectorManager.getEventSectorPart().getSectorPartName() + "_" + SectorManager.getEventSectorPart().getRandomSectorInstance().getSectorInstanceName(), player, false);
                }
                else if (from.getSectorType() == SectorType.EVENT && (to.getSectorType() == SectorType.WORLD || to.getSectorType() == SectorType.NETHER)) {
                    final SectorPartDataConfig correctSectorSectorTypeSensive = SectorManager.getCorrectSectorSectorTypeSensive(to.getX(), to.getZ(), to.getSectorType());
                    RedirectManager.redirectRequest(correctSectorSectorTypeSensive.getSectorPartName() + "_" + correctSectorSectorTypeSensive.getRandomSectorInstance().getSectorInstanceName(), player, false);
                }
            }
        }
        else {
            final Player player2 = Bukkit.getPlayer(uuid);
            if (player2 == null || !player2.isOnline()) {
                throw new Exception("Player should be on the same sector when it was invoked, but he is offline");
            }
            if (from.getSectorType() == SectorType.WORLD && to.getSectorType() == SectorType.NETHER) {
                if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                    dimSwitch = DimSwitch.NETHER;
                }
                final JSONObject jsonFields = new JSONObject();
                jsonFields.put((Object)"teleportType", (Object)"cross_server_dimension_teleport");
                jsonFields.put((Object)"uuid", (Object)uuid.toString());
                jsonFields.put((Object)"toTeleportLocation", (Object)to);
                jsonFields.put((Object)"safeTeleportType", (Object)safeTeleportType);
                jsonFields.put((Object)"dimSwitch", (Object)dimSwitch);
                PubSub.broadcast("ccSectors.remoteSectorTeleport", CcSectorsAPI.gson.toJson((Object)jsonFields));
            }
            else if (from.getSectorType() == SectorType.NETHER && to.getSectorType() == SectorType.WORLD) {
                if (dimSwitch == DimSwitch.AUTO && teleportByPortal) {
                    dimSwitch = DimSwitch.NORMAL;
                }
                final JSONObject jsonFields = new JSONObject();
                jsonFields.put((Object)"teleportType", (Object)"cross_server_dimension_teleport");
                jsonFields.put((Object)"uuid", (Object)uuid.toString());
                jsonFields.put((Object)"toTeleportLocation", (Object)to);
                jsonFields.put((Object)"safeTeleportType", (Object)safeTeleportType);
                jsonFields.put((Object)"dimSwitch", (Object)dimSwitch);
                PubSub.broadcast("ccSectors.remoteSectorTeleport", CcSectorsAPI.gson.toJson((Object)jsonFields));
            }
            else if ((from.getSectorType() == SectorType.WORLD || from.getSectorType() == SectorType.NETHER) && to.getSectorType() == SectorType.EVENT) {
                final Jedis jedis2 = JedisFactory.getInstance().getJedis();
                final SectorPartDataConfig fromSectorPart2 = SectorManager.getCurrentSectorPart();
                final SectorDataConfig fromSectorInstance2 = SectorManager.getCurrentSectorInstance();
                final String fromSectorName2 = fromSectorPart2.getSectorPartName() + "_" + fromSectorInstance2.getSectorInstanceName();
                final PlayerData oldData2 = DataStore.getPlayerData(player2.getUniqueId());
                PlayerData newData2;
                if (oldData2 != null) {
                    newData2 = new PlayerData(player2, oldData2.getDimSwitch(), oldData2.getSafeTeleportType());
                }
                else {
                    newData2 = new PlayerData(player2, DimSwitch.NORMAL, SafeTeleportType.SAFE);
                }
                newData2.setLastSector(fromSectorName2);
                newData2.setNeedingSave(false);
                RedisBackend.setPlayerData(player2.getUniqueId(), newData2, jedis2);
                RedisBackend.setPlayerLastSector(player2.getUniqueId(), fromSectorName2, jedis2);
                JedisFactory.getInstance().returnJedis(jedis2);
                RedirectManager.redirectRequest(SectorManager.getEventSectorPart().getSectorPartName() + "_" + SectorManager.getEventSectorPart().getRandomSectorInstance().getSectorInstanceName(), player2, false);
            }
            else if (from.getSectorType() == SectorType.EVENT && (to.getSectorType() == SectorType.WORLD || to.getSectorType() == SectorType.NETHER)) {
                final SectorPartDataConfig correctSectorSectorTypeSensive2 = SectorManager.getCorrectSectorSectorTypeSensive(to.getX(), to.getZ(), to.getSectorType());
                RedirectManager.redirectRequest(correctSectorSectorTypeSensive2.getSectorPartName() + "_" + correctSectorSectorTypeSensive2.getRandomSectorInstance().getSectorInstanceName(), player2, false);
            }
        }
    }
    
    public static void teleportPlayerAuto(final UUID uuid, final TeleportLocation to, final SafeTeleportType safeTeleportType, final DimSwitch dimSwitch, final boolean teleportByPortal) throws Exception {
        final JSONObject obj = new JSONObject();
        obj.put((Object)"uuid", (Object)uuid.toString());
        obj.put((Object)"teleportToLoc", (Object)to);
        obj.put((Object)"safeTeleportType", (Object)safeTeleportType);
        obj.put((Object)"dimSwitch", (Object)dimSwitch);
        obj.put((Object)"teleportByPortal", (Object)teleportByPortal);
        PubSub.broadcast("ccSectors.initRemoteSectorAutoTeleport", CcSectorsAPI.gson.toJson((Object)obj));
    }
    
    public static void teleportPlayerAuto(final UUID fromUUID, final UUID toUUID, final SafeTeleportType safeTeleportType, final DimSwitch dimSwitch, final boolean teleportByPortal) throws Exception {
        final JSONObject obj = new JSONObject();
        obj.put((Object)"fromUUID", (Object)fromUUID.toString());
        obj.put((Object)"toUUID", (Object)toUUID.toString());
        obj.put((Object)"safeTeleportType", (Object)safeTeleportType);
        obj.put((Object)"dimSwitch", (Object)dimSwitch);
        obj.put((Object)"teleportByPortal", (Object)teleportByPortal);
        PubSub.broadcast("ccSectors.initRemoteSectorAutoTeleportUuid2uuid", CcSectorsAPI.gson.toJson((Object)obj));
    }
    
    public static String getCurrentSectorName() {
        final SectorPartDataConfig sectorPart = SectorManager.getCurrentSectorPart();
        final SectorDataConfig sectorInstance = SectorManager.getCurrentSectorInstance();
        final String sectorName = sectorPart.getSectorPartName() + "_" + sectorInstance.getSectorInstanceName();
        return sectorName;
    }
    
    static {
        CcSectorsAPI.random = new Random();
        CcSectorsAPI.minBorder = 100;
        gson = new GsonBuilder().create();
    }
}
