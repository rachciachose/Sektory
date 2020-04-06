// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.listeners;

import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.event.EventHandler;
import pl.best241.ccsectors.data.PlayerData;
import pl.best241.ccsectors.config.SectorPartDataConfig;
import org.bukkit.entity.Player;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccsectors.api.CcSectorsAPI;
import java.util.Random;
import pl.best241.ccsectors.config.SectorDataConfig;
import pl.best241.ccsectors.data.DataStore;
import pl.best241.ccsectors.managers.RedirectManager;
import pl.best241.ccsectors.data.SectorType;
import org.bukkit.Location;
import pl.best241.ccsectors.managers.SectorManager;
import org.bukkit.Bukkit;
import pl.best241.ccsectors.data.DimSwitch;
import pl.best241.ccsectors.data.SafeTeleportType;
import pl.best241.ccsectors.api.TeleportLocation;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.json.simple.JSONObject;
import pl.best241.rdbplugin.pubsub.PubSub;
import org.bukkit.plugin.Plugin;
import pl.best241.ccsectors.messages.MessagesData;
import pl.best241.ccsectors.CcSectors;
import pl.best241.rdbplugin.events.PubSubRecieveMessageEvent;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.event.Listener;

public class PubSubRecieveMessageListener implements Listener
{
    private static final Gson gson;
    
    @EventHandler
    public static void pubSubRecieveMessageListener(final PubSubRecieveMessageEvent event) throws Exception {
        if (event.getChannel().equals("reloadAllMessagesRequest")) {
            MessagesData.loadMessages((Plugin)CcSectors.getPlugin());
            PubSub.broadcast("reloadAllMessagesResponse", CcSectors.getPlugin().getName());
        }
        else if (event.getChannel().equals("ccSectors.remoteSectorTeleport")) {
            final String parsedData = event.getMessage();
            final JSONObject decodedData = (JSONObject)PubSubRecieveMessageListener.gson.fromJson(parsedData, (Class)JSONObject.class);
            final String teleportType = (String)decodedData.get((Object)"teleportType");
            final UUID uuid = UUID.fromString((String)decodedData.get((Object)"uuid"));
            final TeleportLocation toTeleportLocation = TeleportLocation.fromLinkedHashMap((LinkedHashMap<String, Object>)decodedData.get((Object)"toTeleportLocation"));
            final SafeTeleportType safeTeleportType = SafeTeleportType.valueOf((String)decodedData.get((Object)"safeTeleportType"));
            final DimSwitch dimSwitch = DimSwitch.valueOf((String)decodedData.get((Object)"dimSwitch"));
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                if (teleportType.equals("single_server_teleport")) {
                    final Location bukkitLocation = new Location(Bukkit.getWorld(SectorManager.getCurrentSectorInstance().getWorldName()), toTeleportLocation.getX(), toTeleportLocation.getY(), toTeleportLocation.getZ(), toTeleportLocation.getYaw(), toTeleportLocation.getPitch());
                    Bukkit.getScheduler().runTask((Plugin)CcSectors.getPlugin(), () -> player.teleport(bukkitLocation));
                }
                else if (teleportType.equals("cross_server_teleport")) {
                    final SectorPartDataConfig sectorTo = SectorManager.getCorrectSectorSectorTypeSensive(toTeleportLocation.getX(), toTeleportLocation.getZ(), toTeleportLocation.getSectorType());
                    final SectorDataConfig toSectorInstance = sectorTo.getRandomSectorInstance();
                    final String sectorName = sectorTo.getSectorPartName() + "_" + toSectorInstance.getSectorInstanceName();
                    if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
                        RedirectManager.redirectRequest(sectorName, player, false);
                    }
                    else {
                        final PlayerData data = DataStore.getPlayerData(uuid);
                        data.setLastSector(sectorName);
                        data.setDimSwitch(dimSwitch);
                        data.setSafeTeleprotType(safeTeleportType);
                        data.setSerializedLocation(sectorTo.getSectorType(), toTeleportLocation.getX(), toTeleportLocation.getY(), toTeleportLocation.getZ(), toTeleportLocation.getYaw(), toTeleportLocation.getPitch());
                        data.setNeedingSave(true);
                        DataStore.setPlayerData(uuid, data);
                        RedirectManager.redirectRequest(sectorName, player, true);
                    }
                }
                else if (teleportType.equals("cross_server_dimension_teleport")) {
                    final SectorType type = toTeleportLocation.getSectorType();
                    if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.WORLD) {
                        final PlayerData data2 = DataStore.getPlayerData(uuid);
                        final Location from = player.getLocation();
                        final SectorPartDataConfig toSectorPart = SectorManager.getNetherSectorPart();
                        final SectorDataConfig toSectorInstance2 = toSectorPart.getSectorInstances().get(new Random().nextInt(toSectorPart.getSectorInstances().size()));
                        final String sectorName2 = toSectorPart.getSectorPartName() + "_" + toSectorInstance2.getSectorInstanceName();
                        data2.setLastSector(sectorName2);
                        data2.setDimSwitch(dimSwitch);
                        data2.setSafeTeleprotType(safeTeleportType);
                        data2.setSerializedLocation(toSectorPart.getSectorType(), toTeleportLocation.getX(), toTeleportLocation.getY(), toTeleportLocation.getZ(), toTeleportLocation.getYaw(), toTeleportLocation.getPitch());
                        data2.setNeedingSave(true);
                        DataStore.setPlayerData(uuid, data2);
                        RedirectManager.redirectRequest(sectorName2, player, true);
                    }
                    else if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.NETHER) {
                        final PlayerData data2 = DataStore.getPlayerData(uuid);
                        final Location from = player.getLocation();
                        final SectorPartDataConfig toSectorPart = SectorManager.getCorrectSectorSectorTypeSensive(toTeleportLocation.getX(), toTeleportLocation.getZ(), SectorType.WORLD);
                        final SectorDataConfig toSectorInstance2 = toSectorPart.getSectorInstances().get(new Random().nextInt(toSectorPart.getSectorInstances().size()));
                        final String sectorName2 = toSectorPart.getSectorPartName() + "_" + toSectorInstance2.getSectorInstanceName();
                        data2.setLastSector(sectorName2);
                        data2.setDimSwitch(dimSwitch);
                        data2.setSafeTeleprotType(safeTeleportType);
                        data2.setSerializedLocation(toSectorPart.getSectorType(), toTeleportLocation.getX(), toTeleportLocation.getY(), toTeleportLocation.getZ(), toTeleportLocation.getYaw(), toTeleportLocation.getPitch());
                        data2.setNeedingSave(true);
                        DataStore.setPlayerData(uuid, data2);
                        RedirectManager.redirectRequest(sectorName2, player, true);
                    }
                }
            }
        }
        else if (event.getChannel().equals("ccSectors.initRemoteSectorAutoTeleport")) {
            final JSONObject jsonObj = (JSONObject)PubSubRecieveMessageListener.gson.fromJson(event.getMessage(), (Class)JSONObject.class);
            final UUID uuid2 = UUID.fromString((String)jsonObj.get((Object)"uuid"));
            final Player player2 = Bukkit.getPlayer(uuid2);
            if (player2 != null && player2.isOnline()) {
                final Location playerLoc = player2.getLocation();
                final TeleportLocation teleportFromLoc = new TeleportLocation(SectorManager.getCurrentSectorPart().getSectorType(), playerLoc.getX(), playerLoc.getY(), playerLoc.getZ(), playerLoc.getYaw(), playerLoc.getPitch());
                final TeleportLocation teleportToLoc = TeleportLocation.fromLinkedHashMap((LinkedHashMap<String, Object>)jsonObj.get((Object)"teleportToLoc"));
                final SafeTeleportType safeTeleportType2 = SafeTeleportType.valueOf((String)jsonObj.get((Object)"safeTeleportType"));
                final DimSwitch dimSwitch2 = DimSwitch.valueOf((String)jsonObj.get((Object)"dimSwitch"));
                final boolean teleportByPortal = (boolean)jsonObj.get((Object)"teleportByPortal");
                final UUID uuid3;
                final TeleportLocation from2;
                final TeleportLocation to;
                final SafeTeleportType safeTeleportType4;
                final DimSwitch dimSwitch4;
                final boolean teleportByPortal3;
                Bukkit.getScheduler().runTask((Plugin)CcSectors.getPlugin(), () -> {
                    try {
                        CcSectorsAPI.teleportPlayerAuto(uuid3, from2, to, safeTeleportType4, dimSwitch4, teleportByPortal3);
                    }
                    catch (Exception ex) {
                        Logger.getLogger(PubSubRecieveMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        }
        else if (event.getChannel().equals("ccSectors.initRemoteSectorAutoTeleportUuid2uuid")) {
            final JSONObject jsonObj = (JSONObject)PubSubRecieveMessageListener.gson.fromJson(event.getMessage(), (Class)JSONObject.class);
            final UUID toUUID = UUID.fromString((String)jsonObj.get((Object)"toUUID"));
            final Player player2 = Bukkit.getPlayer(toUUID);
            if (player2 != null && player2.isOnline()) {
                final UUID fromUUID = UUID.fromString((String)jsonObj.get((Object)"fromUUID"));
                final SafeTeleportType safeTeleportType3 = SafeTeleportType.valueOf((String)jsonObj.get((Object)"safeTeleportType"));
                final DimSwitch dimSwitch3 = DimSwitch.valueOf((String)jsonObj.get((Object)"dimSwitch"));
                final boolean teleportByPortal2 = (boolean)jsonObj.get((Object)"teleportByPortal");
                CcSectorsAPI.teleportPlayerAuto(fromUUID, new TeleportLocation(SectorManager.getCurrentSectorPart().getSectorType(), player2.getLocation().getX(), player2.getLocation().getY(), player2.getLocation().getZ(), player2.getLocation().getYaw(), player2.getLocation().getPitch()), safeTeleportType3, dimSwitch3, teleportByPortal2);
            }
        }
        else if (event.getChannel().equals("ccSectors.setNetherOn")) {
            final String message = event.getMessage();
            final Boolean value = Boolean.valueOf(message);
            CcSectors.setNetherOn(value);
        }
    }
    
    static {
        gson = new GsonBuilder().create();
    }
}
