// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.listeners;

import pl.best241.ccsectors.data.CuboidData;
import pl.best241.ccsectors.managers.SectorManager;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import org.bukkit.block.Block;
import pl.best241.rdbplugin.JedisFactory;
import org.bukkit.Material;
import pl.best241.ccsectors.managers.TeleportManager;
import pl.best241.ccsectors.data.SafeTeleportType;
import pl.best241.ccsectors.api.TeleportLocation;
import org.bukkit.World;
import pl.best241.ccsectors.data.SectorType;
import pl.best241.ccsectors.api.CcSectorsAPI;
import java.util.HashSet;
import org.bukkit.ChatColor;
import pl.best241.ccsectors.data.DataStore;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Event;
import pl.best241.ccsectors.events.SectorsServerQuitEvent;
import org.bukkit.Bukkit;
import pl.best241.ccsectors.CcSectors;
import org.bukkit.Location;
import java.util.HashMap;
import java.util.UUID;
import java.util.ArrayList;
import redis.clients.jedis.JedisPubSub;

public class PubSubListener extends JedisPubSub
{
    public static ArrayList<UUID> toRandomTeleport;
    public static HashMap<UUID, Location> toRandomTeleportGroup;
    public static ArrayList<UUID> toSendWelcomeMessagePremium;
    public static ArrayList<UUID> toSendWelcomeMessageNoPremium;
    
    public void onMessage(final String channel, final String message) {
        try {
            if (channel.equalsIgnoreCase("ccSectors.playersOnSectorJoin")) {
                System.out.println(message);
                final String[] split = message.split(";");
                final String sector = split[0];
                UUID.fromString(split[1]);
            }
            else if (channel.equalsIgnoreCase("ccSectors.quitPlayerFromServer")) {
                System.out.println("Channel quit player from sector");
                final String[] parts = message.split(";");
                final UUID uuid = UUID.fromString(parts[0]);
                final String name = parts[1];
                if (CcSectors.getPlugin().isEnabled()) {
                    final SectorsServerQuitEvent quitEvent = new SectorsServerQuitEvent(Bukkit.getPlayer(uuid), uuid, name);
                    Bukkit.getScheduler().runTask((Plugin)CcSectors.getPlugin(), () -> Bukkit.getPluginManager().callEvent((Event)quitEvent));
                }
            }
            else if (channel.equalsIgnoreCase("ccSectors.joinPlayerOnSector")) {
                final String[] parts = message.split(";");
                final String nick = parts[0];
                final UUID uuid2 = UUID.fromString(parts[1]);
                Boolean.valueOf(parts[2]);
            }
            if (channel.equalsIgnoreCase("ccSectors.clearAllPlayers")) {
                DataStore.clearAllPlayers();
            }
            if (channel.equalsIgnoreCase("ccSectors.softrestart")) {
                final String messageToPlayers = ChatColor.RED + "Restart serwera przez " + message + " za 5 sekund...";
                for (final Player online : Bukkit.getOnlinePlayers()) {
                    if (online.hasPermission("ccSectors.softRestart")) {
                        online.sendMessage(messageToPlayers);
                    }
                }
                Bukkit.getConsoleSender().sendMessage(messageToPlayers);
                Bukkit.getScheduler().runTaskLater((Plugin)CcSectors.getPlugin(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.shutdown();
                    }
                }, 100L);
            }
            if (channel.equalsIgnoreCase("ccSectors.playersOnSectorOnlineResponse")) {
                final String[] split2 = message.split(";");
                final String sector2 = split2[0];
                final HashSet<UUID> players = new HashSet<UUID>();
                for (int i = 1; i < split2.length; ++i) {
                    players.add(UUID.fromString(split2[i]));
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.joinPlayerOnServer")) {
                final String[] data = message.split(";");
                final String nick = data[0];
                DataStore.addPlayerName(nick);
                final UUID uuid2 = UUID.fromString(data[1]);
                final Boolean value = Boolean.valueOf(data[2]);
                if (value) {
                    PubSubListener.toSendWelcomeMessagePremium.add(uuid2);
                }
                else {
                    PubSubListener.toSendWelcomeMessageNoPremium.add(uuid2);
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.playersOnlineNamesResponse")) {
                final String[] names = message.split(";");
                DataStore.clearPlayerNames();
                for (final String nick2 : names) {
                    DataStore.addPlayerName(nick2);
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.playersOnlineResponse")) {
                if (message.isEmpty()) {
                    return;
                }
                final String[] rawUUIDs = message.split(";");
                DataStore.clearPlayerOnSector();
                for (int j = 0; j < rawUUIDs.length; j += 2) {
                    final UUID uuid2 = UUID.fromString(rawUUIDs[j]);
                    final String sector3 = rawUUIDs[j + 1];
                    DataStore.addPlayerOnSector(sector3, uuid2);
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.randomTpAskForCoords")) {
                final String[] parts = message.split(":");
                final String uuidParsed = parts[0];
                final String senderSectorName = parts[1];
                final String targetSectorName = parts[2];
                if (CcSectorsAPI.getCurrentSectorName().equals(targetSectorName)) {
                    final int border = Integer.parseInt(parts[3]);
                    final SectorType sectorType = SectorType.valueOf(parts[4]);
                    final int x = Integer.parseInt(parts[5]);
                    final int y = Integer.parseInt(parts[6]);
                    final int z = Integer.parseInt(parts[7]);
                    Bukkit.getScheduler().runTask((Plugin)CcSectors.getPlugin(), (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            World world = null;
                            if (sectorType == SectorType.WORLD) {
                                world = Bukkit.getWorlds().get(0);
                            }
                            final TeleportLocation teleportLoc = new TeleportLocation(sectorType, x, y, z);
                            final TeleportLocation safeLocation = TeleportManager.getSafeLocation(teleportLoc, SafeTeleportType.TOP);
                            final Block block = safeLocation.toLocation(world).add(0.0, -1.0, 0.0).getBlock();
                            System.out.println("Block at coords: " + block.getType() + " " + block.isLiquid());
                            if (!block.isLiquid() && block.getType() != Material.LEAVES && !targetSectorName.contains("SPAWN")) {
                                final Jedis jedis = JedisFactory.getInstance().getJedis();
                                jedis.publish("ccSectors.randomTpAcceptCoords", uuidParsed + ":" + senderSectorName + ":" + targetSectorName + ":" + sectorType.toString() + ":" + x + ":" + safeLocation.getBlockY() + ":" + z);
                                JedisFactory.getInstance().returnJedis(jedis);
                            }
                            else {
                                final Jedis jedis = JedisFactory.getInstance().getJedis();
                                jedis.publish("ccSectors.randomTpNotAcceptCoords", uuidParsed + ":" + senderSectorName + ":" + targetSectorName + ":" + border);
                                JedisFactory.getInstance().returnJedis(jedis);
                            }
                        }
                    });
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.randomTpAcceptCoords")) {
                final String[] parts = message.split(":");
                final String uuidParsed = parts[0];
                final String senderSectorName = parts[1];
                final String targetSectorName = parts[2];
                if (CcSectorsAPI.getCurrentSectorName().equals(senderSectorName)) {
                    final SectorType sectorType2 = SectorType.valueOf(parts[3]);
                    final int x2 = Integer.parseInt(parts[4]);
                    final int y2 = Integer.parseInt(parts[5]);
                    final int z2 = Integer.parseInt(parts[6]);
                    final UUID uuid3 = UUID.fromString(uuidParsed);
                    final Player player = Bukkit.getPlayer(uuid3);
                    final TeleportLocation teleportLoc = new TeleportLocation(sectorType2, x2, y2, z2);
                    Bukkit.getScheduler().runTask((Plugin)CcSectors.getPlugin(), (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            CcSectorsAPI.teleportRandomAccepted(player, teleportLoc);
                        }
                    });
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.randomTpNotAcceptCoords")) {
                final String[] parts = message.split(":");
                final String uuidParsed = parts[0];
                final String senderSectorName = parts[1];
                final int border2 = Integer.parseInt(parts[3]);
                if (CcSectorsAPI.getCurrentSectorName().equals(senderSectorName)) {
                    final UUID uuid4 = UUID.fromString(uuidParsed);
                    final Player player2 = Bukkit.getPlayer(uuid4);
                    CcSectorsAPI.teleportRandom(player2, border2);
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.randomTeleportGroupAsk")) {
                final String[] parts = message.split(";");
                final String toSector = parts[0];
                final String[] uuids = parts[1].split(":");
                if (toSector.equalsIgnoreCase(SectorManager.getCurrentSectorPart().getSectorPartName() + "_" + SectorManager.getCurrentSectorInstance().getSectorInstanceName())) {
                    final CuboidData cuboidData = SectorManager.getCurrentSectorPart().getCuboidData();
                    final Location randomLoc = cuboidData.randomLocationInCuboid(Bukkit.getWorld(SectorManager.getCurrentSectorInstance().getWorldName()));
                    for (final String rawUUID : uuids) {
                        PubSubListener.toRandomTeleportGroup.put(UUID.fromString(rawUUID), randomLoc);
                    }
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.removeWelcomeMessage")) {
                final UUID uuid5 = UUID.fromString(message);
                PubSubListener.toSendWelcomeMessagePremium.remove(uuid5);
                PubSubListener.toSendWelcomeMessageNoPremium.remove(uuid5);
            }
            if (channel.equalsIgnoreCase("ccSectors.playerJoined")) {
                final String[] parts = message.split(";");
                final UUID uuid = UUID.fromString(parts[0]);
                final String name = parts[1];
                final String sector3 = parts[2];
                DataStore.addPlayerName(name);
                DataStore.addPlayerOnSector(sector3, uuid);
            }
            if (channel.equalsIgnoreCase("ccSectors.randomTpPlayersAskForCoords")) {
                final String[] parts = message.split(":");
                final String uuidParsed = parts[0];
                final String senderSectorName = parts[1];
                final String targetSectorName = parts[2];
                if (CcSectorsAPI.getCurrentSectorName().equals(targetSectorName)) {
                    final int border = Integer.parseInt(parts[3]);
                    final SectorType sectorType = SectorType.valueOf(parts[4]);
                    final int x = Integer.parseInt(parts[5]);
                    final int y = Integer.parseInt(parts[6]);
                    final int z = Integer.parseInt(parts[7]);
                    Bukkit.getScheduler().runTask((Plugin)CcSectors.getPlugin(), (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            World world = null;
                            if (sectorType == SectorType.WORLD) {
                                world = Bukkit.getWorlds().get(0);
                            }
                            final TeleportLocation teleportLoc = new TeleportLocation(sectorType, x, y, z);
                            final TeleportLocation safeLocation = TeleportManager.getSafeLocation(teleportLoc, SafeTeleportType.TOP);
                            final Block block = safeLocation.toLocation(world).add(0.0, -1.0, 0.0).getBlock();
                            System.out.println("Block at coords: " + block.getType() + " " + block.isLiquid());
                            if (!block.isLiquid() && block.getType() != Material.LEAVES && !targetSectorName.contains("SPAWN")) {
                                final Jedis jedis = JedisFactory.getInstance().getJedis();
                                jedis.publish("ccSectors.randomTpPlayersAcceptCoords", uuidParsed + ":" + senderSectorName + ":" + targetSectorName + ":" + sectorType.toString() + ":" + x + ":" + y + ":" + z);
                                JedisFactory.getInstance().returnJedis(jedis);
                            }
                            else {
                                final Jedis jedis = JedisFactory.getInstance().getJedis();
                                jedis.publish("ccSectors.randomTpPlayersNotAcceptCoords", uuidParsed + ":" + senderSectorName + ":" + targetSectorName + ":" + border);
                                JedisFactory.getInstance().returnJedis(jedis);
                            }
                        }
                    });
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.randomTpPlayersAcceptCoords")) {
                final String[] parts = message.split(":");
                final String uuidParsed = parts[0];
                final String senderSectorName = parts[1];
                final String targetSectorName = parts[2];
                if (CcSectorsAPI.getCurrentSectorName().equals(senderSectorName)) {
                    final SectorType sectorType2 = SectorType.valueOf(parts[3]);
                    final int x2 = Integer.parseInt(parts[4]);
                    final int y2 = Integer.parseInt(parts[5]);
                    final int z2 = Integer.parseInt(parts[6]);
                    final TeleportLocation teleportLoc2 = new TeleportLocation(sectorType2, x2, y2, z2);
                    for (final String uuidSingleParsed : uuidParsed.split(";")) {
                        if (!uuidSingleParsed.isEmpty()) {
                            final UUID uuid6 = UUID.fromString(uuidSingleParsed);
                            final Player player3 = Bukkit.getPlayer(uuid6);
                            if (player3 != null && player3.isOnline()) {
                                CcSectorsAPI.teleportRandomAccepted(player3, teleportLoc2);
                            }
                        }
                    }
                }
            }
            if (channel.equalsIgnoreCase("ccSectors.randomTpPlayersNotAcceptCoords")) {
                final String[] parts = message.split(":");
                final String uuidParsed = parts[0];
                final String senderSectorName = parts[1];
                final int border2 = Integer.parseInt(parts[3]);
                if (CcSectorsAPI.getCurrentSectorName().equals(senderSectorName)) {
                    final ArrayList<Player> players2 = new ArrayList<Player>();
                    for (final String uuidSingle : uuidParsed.split(";")) {
                        final Player player = Bukkit.getPlayer(uuidSingle);
                        if (player != null && player.isOnline()) {
                            players2.add(player);
                        }
                    }
                    final Player[] playersArray = new Player[players2.size()];
                    for (int k = 0; k < players2.size(); ++k) {
                        playersArray[k] = players2.get(k);
                    }
                    CcSectorsAPI.teleportRandomPlayers(playersArray, border2);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void onPMessage(final String pattern, final String channel, final String message) {
        System.out.println(channel + " " + message);
        if (channel.equalsIgnoreCase("ccSectors.playersOnSectorJoin")) {
            final String[] split = message.split(";");
            final String sector = split[0];
            UUID.fromString(split[1]);
        }
        else if (channel.equalsIgnoreCase("ccSectors.quitPlayerFromServer")) {
            final String[] parts = message.split(";");
            final UUID uuid = UUID.fromString(parts[0]);
            final String name = parts[1];
            if (CcSectors.getPlugin().isEnabled()) {
                final SectorsServerQuitEvent quitEvent = new SectorsServerQuitEvent(Bukkit.getPlayer(uuid), uuid, name);
                Bukkit.getScheduler().runTask((Plugin)CcSectors.getPlugin(), () -> Bukkit.getPluginManager().callEvent((Event)quitEvent));
            }
        }
        else if (channel.equalsIgnoreCase("ccSectors.joinPlayerOnSector")) {
            final String[] parts = message.split(";");
            final String nick = parts[0];
            final UUID uuid2 = UUID.fromString(parts[1]);
            Boolean.valueOf(parts[2]);
        }
        if (channel.equalsIgnoreCase("ccSectors.clearAllPlayers")) {
            DataStore.clearAllPlayers();
        }
        if (channel.equalsIgnoreCase("ccSectors.softrestart")) {
            final String messageToPlayers = ChatColor.RED + "Restart serwera przez " + message + " za 5 sekund...";
            for (final Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("ccSectors.softRestart")) {
                    online.sendMessage(messageToPlayers);
                }
            }
            Bukkit.getConsoleSender().sendMessage(messageToPlayers);
            Bukkit.getScheduler().runTaskLater((Plugin)CcSectors.getPlugin(), (Runnable)new Runnable() {
                @Override
                public void run() {
                    Bukkit.shutdown();
                }
            }, 100L);
        }
        if (channel.equalsIgnoreCase("ccSectors.playersOnSectorOnlineResponse")) {
            final String[] split2 = message.split(";");
            final String sector2 = split2[0];
            final HashSet<UUID> players = new HashSet<UUID>();
            for (int i = 1; i < split2.length; ++i) {
                players.add(UUID.fromString(split2[i]));
            }
            DataStore.setPlayersOnSector(sector2, players);
        }
        if (channel.equalsIgnoreCase("ccSectors.joinPlayerOnServer")) {
            final String[] data = message.split(";");
            final String nick = data[0];
            DataStore.addPlayerName(nick);
            final UUID uuid2 = UUID.fromString(data[1]);
            final Boolean value = Boolean.valueOf(data[2]);
            if (value) {
                PubSubListener.toSendWelcomeMessagePremium.add(uuid2);
            }
            else {
                PubSubListener.toSendWelcomeMessageNoPremium.add(uuid2);
            }
        }
        if (channel.equalsIgnoreCase("ccSectors.playersOnlineNamesResponse")) {
            final String[] split3;
            final String[] names = split3 = message.split(";");
            for (final String nick2 : split3) {
                DataStore.addPlayerName(nick2);
            }
        }
        if (channel.equalsIgnoreCase("ccSectors.playersOnlineResponse")) {
            final String[] rawUUIDs = message.split(";");
            for (int j = 0; j < rawUUIDs.length; j += 2) {
                final UUID uuid2 = UUID.fromString(rawUUIDs[j]);
                final String sector3 = rawUUIDs[j + 1];
                DataStore.addPlayerOnSector(sector3, uuid2);
            }
        }
        if (channel.equalsIgnoreCase("ccSectors.randomTeleportPlayer")) {
            final String[] parts = message.split(";");
            final String sector = parts[0];
            if (sector.equalsIgnoreCase(SectorManager.getCurrentSectorPart().getSectorPartName() + "_" + SectorManager.getCurrentSectorInstance().getSectorInstanceName())) {
                final UUID uuid2 = UUID.fromString(parts[1]);
                PubSubListener.toRandomTeleport.add(uuid2);
            }
        }
        if (channel.equalsIgnoreCase("ccSectors.removeWelcomeMessage")) {
            final UUID uuid3 = UUID.fromString(message);
            PubSubListener.toSendWelcomeMessagePremium.remove(uuid3);
            PubSubListener.toSendWelcomeMessageNoPremium.remove(uuid3);
        }
        if (channel.equalsIgnoreCase("ccSectors.playerJoined")) {
            System.out.println("ccSectors.playerJoined " + message);
            final String[] parts = message.split(";");
            final UUID uuid = UUID.fromString(parts[0]);
            final String name = parts[1];
            final String sector3 = parts[2];
            DataStore.addPlayerName(name);
            DataStore.addPlayerOnSector(sector3, uuid);
        }
    }
    
    public void onSubscribe(final String channel, final int subscribedChannels) {
    }
    
    public void onUnsubscribe(final String channel, final int subscribedChannels) {
    }
    
    public void onPUnsubscribe(final String pattern, final int subscribedChannels) {
    }
    
    public void onPSubscribe(final String pattern, final int subscribedChannels) {
    }
    
    public static void broadcastPlayerJoined(final UUID uuid, final String name, final String sector) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccSectors.playerJoined", uuid + ";" + name + ";" + sector);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    static {
        PubSubListener.toRandomTeleport = new ArrayList<UUID>();
        PubSubListener.toRandomTeleportGroup = new HashMap<UUID, Location>();
        PubSubListener.toSendWelcomeMessagePremium = new ArrayList<UUID>();
        PubSubListener.toSendWelcomeMessageNoPremium = new ArrayList<UUID>();
    }
}
