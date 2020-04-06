// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.listeners;

import pl.best241.ccsectors.events.SectorsRedirectEvent;
import org.bukkit.entity.Arrow;
import pl.best241.ccsectors.data.DamageData;
import org.bukkit.util.Vector;
import pl.best241.addonapi.bossbar.BossBarApi;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.best241.ccsectors.events.SectorsServerQuitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.World;
import pl.best241.ccsectors.api.CcSectorsAPI;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import java.util.Arrays;
import org.bukkit.event.player.PlayerKickEvent;
import pl.best241.ccsectors.data.CuboidData;
import pl.best241.cctools.commands.Border;
import org.bukkit.event.EventPriority;
import pl.best241.rdbplugin.SafeJedisInstructions;
import pl.best241.ccsectors.messages.MessagesData;
import pl.best241.ccsectors.tickers.SectorTeleportTicker;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import pl.best241.ccsectors.data.SafeTeleportType;
import pl.best241.ccsectors.data.DimSwitch;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventHandler;
import pl.best241.ccsectors.data.PlayerData;
import pl.best241.ccsectors.config.SectorPartDataConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccsectors.CcSectors;
import pl.best241.ccsectors.managers.RedirectManager;
import pl.best241.ccsectors.api.TeleportLocation;
import java.util.Random;
import pl.best241.ccsectors.config.SectorDataConfig;
import pl.best241.ccsectors.data.DataStore;
import pl.best241.ccsectors.config.ConfigManager;
import pl.best241.ccsectors.data.SectorType;
import pl.best241.ccsectors.managers.SectorManager;
import org.bukkit.event.player.PlayerRespawnEvent;
import java.util.UUID;
import java.util.ArrayList;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener
{
    private static final ArrayList<UUID> itemsApplied;
    private static final int maxSearchCount = 20;
    private static final float showDistance = 100.0f;
    private static ArrayList<UUID> redirecting;
    
    @EventHandler
    public static void onPlayerRespawn(final PlayerRespawnEvent event) {
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
            return;
        }
        final Player player = event.getPlayer();
        if (!player.isOnline()) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        final Location from = player.getLocation();
        final Location to = from.getWorld().getSpawnLocation();
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.NETHER) {
            final Location spawn = from.getWorld().getSpawnLocation();
            event.setRespawnLocation(spawn);
        }
        else if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.WORLD) {
            if (!SectorManager.isInRightSectorPart(to, ConfigManager.getConfig().getNoMoveDistnace())) {
                final SectorPartDataConfig toSectorPart = SectorManager.getCorrectSector(to);
                if (toSectorPart != null) {
                    final PlayerData data = DataStore.getPlayerData(uuid);
                    final SectorDataConfig toSectorInstance = toSectorPart.getSectorInstances().get(new Random().nextInt(toSectorPart.getSectorInstances().size()));
                    final String sectorName = toSectorPart.getSectorPartName() + "_" + toSectorInstance.getSectorInstanceName();
                    data.setLocation(new TeleportLocation(to));
                    data.setNeedingSave(true);
                    DataStore.setPlayerData(uuid, data);
                    RedirectManager.redirectRequest(sectorName, player, true);
                    try {
                        CcSectors.getBackend().setPlayerLastSector(uuid, sectorName);
                    }
                    catch (Exception ex) {
                        Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            else {
                event.setRespawnLocation(to);
            }
        }
    }
    
    @EventHandler
    public static void onDeath(final PlayerDeathEvent event) {
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
            return;
        }
        final Player player = event.getEntity();
        EntityDamageByEntityListener.antiRelog.remove(player.getName());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onJoinLowest(final PlayerJoinEvent event) {
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
            return;
        }
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        PlayerData data = null;
        try {
            data = CcSectors.getBackend().getPlayerData(uuid);
        }
        catch (Exception ex) {
            player.kickPlayer("Blad podczas wczytywania ekwipunku!\nSprobuj wejsc ponownie!");
            Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        if (data == null) {
            data = new PlayerData(player, DimSwitch.SAME, SafeTeleportType.SAFE);
            System.out.println("New player " + player.getName());
            boolean randomSpawn = false;
            try {
                randomSpawn = CcSectors.getBackend().getRandomSpawn();
            }
            catch (Exception ex2) {
                Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex2);
            }
            if (randomSpawn) {
                final Location randomLoc = getRandomLocation(player, 0);
                if (randomLoc == null) {
                    System.out.println("Random loc not found on this sector! Spawning " + player.getName() + " on spawn");
                    player.teleport(player.getWorld().getSpawnLocation());
                }
                else {
                    player.teleport(randomLoc.getWorld().getHighestBlockAt(randomLoc).getLocation());
                    System.out.println("Spawning " + player.getName() + " random " + randomLoc.getBlockX() + " " + randomLoc.getBlockZ());
                }
            }
            else {
                System.out.println("Not randoming " + player.getName());
                player.teleport(player.getWorld().getSpawnLocation());
            }
            player.getInventory().setItem(0, new ItemStack(Material.STONE_PICKAXE));
            player.getInventory().setItem(1, new ItemStack(Material.STONE_AXE));
            player.getInventory().setItem(2, new ItemStack(Material.STONE_SPADE));
            player.getInventory().setItem(3, new ItemStack(Material.WOOD, 48));
            player.getInventory().setItem(4, new ItemStack(Material.COOKED_BEEF, 64));
            player.getInventory().setItem(5, new ItemStack(Material.COOKED_BEEF, 64));
            player.getInventory().setItem(6, new ItemStack(Material.BREAD, 64));
            player.getInventory().setItem(7, new ItemStack(Material.ENDER_CHEST));
            data.setNeedingSave(true);
            DataStore.setPlayerData(uuid, data);
        }
        else {
            data.applyHealth(player);
            data.applyInventory(player);
            data.applyGamemode(player);
            data.applyValues(player);
            data.setNeedingSave(true);
            player.getInventory().setHelmet(data.getArmor().getHelmet());
            player.getInventory().setChestplate(data.getArmor().getChestplate());
            player.getInventory().setLeggings(data.getArmor().getLeggins());
            player.getInventory().setBoots(data.getArmor().getBoots());
            data.teleport(player);
            DataStore.setPlayerData(uuid, data);
            SectorTeleportTicker.removeTeleported(uuid);
        }
        DataStore.setPlayerData(uuid, data);
        final SectorPartDataConfig sectorPart = SectorManager.getCurrentSectorPart();
        final SectorDataConfig sectorInstance = SectorManager.getCurrentSectorInstance();
        final String sectorName = sectorPart.getSectorPartName() + "_" + sectorInstance.getSectorInstanceName();
        SectorTeleportTicker.addPlayer(player);
        if (PubSubListener.toSendWelcomeMessagePremium.contains(player.getUniqueId())) {
            PubSubListener.toSendWelcomeMessagePremium.remove(player.getUniqueId());
            player.sendMessage(MessagesData.welcomeMessagePremium.replace("%nick", player.getName()));
        }
        else if (PubSubListener.toSendWelcomeMessageNoPremium.contains(player.getUniqueId())) {
            PubSubListener.toSendWelcomeMessageNoPremium.remove(player.getUniqueId());
            player.sendMessage(MessagesData.welcomeMessageNoPremium.replace("%nick", player.getName()));
        }
        try {
            SafeJedisInstructions.publish("ccSectors.removeWelcomeMessage", player.getUniqueId().toString());
        }
        catch (Exception ex3) {
            Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex3);
        }
        PlayerListener.itemsApplied.add(player.getUniqueId());
        PubSubListener.broadcastPlayerJoined(uuid, player.getName(), sectorName);
    }
    
    public static Location getRandomLocation(final Player player, int searchCount) {
        final CuboidData cuboidData = SectorManager.getCurrentSectorPart().getCuboidData();
        final Location randomLoc = cuboidData.randomLocationInCuboid(player.getWorld());
        if (Math.abs(randomLoc.getBlockX()) <= Border.getBorderRadius() && Math.abs(randomLoc.getBlockZ()) <= Border.getBorderRadius()) {
            return randomLoc;
        }
        if (searchCount > 20) {
            return null;
        }
        return getRandomLocation(player, searchCount++);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onKick(final PlayerKickEvent event) {
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
            return;
        }
        final Throwable e = new Throwable();
        final StackTraceElement[] elements = e.getStackTrace();
        System.out.println("Fireing kick event for player " + event.getPlayer().getName() + " " + Arrays.asList(elements));
        final Player player = event.getPlayer();
        PlayerListener.redirecting.remove(player.getUniqueId());
        final UUID uuid = player.getUniqueId();
        if (player.getHealth() <= 0.0) {
            for (final ItemStack is : player.getInventory().getContents()) {
                if (is != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), is);
                    player.getInventory().removeItem(new ItemStack[] { is });
                }
            }
            for (final ItemStack is : player.getInventory().getArmorContents()) {
                if (is.getAmount() != 0) {
                    player.getWorld().dropItemNaturally(player.getLocation(), is);
                }
            }
            player.getInventory().setArmorContents(new ItemStack[4]);
            final Location loc = player.getWorld().getSpawnLocation();
            final int orbs = (int)player.getExp();
            final int level = player.getLevel();
            final int total = (2 * level * level + 5 * orbs) / 5;
            final World world = player.getWorld();
            ((ExperienceOrb)world.spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB)).setExperience(total);
            player.setExp(0.0f);
            player.setLevel(0);
            player.setFoodLevel(20);
            player.setHealth(20.0);
            player.getActivePotionEffects().clear();
            player.setFireTicks(0);
        }
        final SectorPartDataConfig fromSectorPart = SectorManager.getCurrentSectorPart();
        final SectorDataConfig fromSectorInstance = SectorManager.getCurrentSectorInstance();
        final String fromSectorName = fromSectorPart.getSectorPartName() + "_" + fromSectorInstance.getSectorInstanceName();
        SectorTeleportTicker.removePlayer(player);
        SectorTeleportTicker.removeTeleported(player.getUniqueId());
        final PlayerData oldData = DataStore.getPlayerData(player.getUniqueId());
        if (oldData != null && oldData.isNeedingSave()) {
            final PlayerData newData = new PlayerData(player, oldData.getDimSwitch(), SafeTeleportType.UNSAFE);
            if (!player.isDead()) {
                newData.setLastSector(fromSectorName);
            }
            else {
                newData.setLocation(new TeleportLocation(player.getWorld().getSpawnLocation()));
                newData.setLastSector(CcSectorsAPI.getCurrentSectorName());
            }
            newData.setNeedingSave(false);
            try {
                CcSectors.getBackend().setPlayerData(player.getUniqueId(), newData);
            }
            catch (Exception ex) {
                Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                CcSectors.getBackend().setPlayerLastSector(player.getUniqueId(), fromSectorName);
            }
            catch (Exception ex) {
                Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                CcSectors.getBackend().setPlayerLastLocation(player.getUniqueId(), new TeleportLocation(player.getLocation()));
            }
            catch (Exception ex) {
                Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            PlayerListener.itemsApplied.remove(player.getUniqueId());
            DataStore.removePlayerData(uuid);
            System.out.println("Saving data on kick " + player.getName());
        }
        else {
            System.out.println("Not saving data on kick " + player.getName());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onQuit(final PlayerQuitEvent event) {
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
            return;
        }
        final Player player = event.getPlayer();
        PlayerListener.redirecting.remove(player.getUniqueId());
        final UUID uuid = player.getUniqueId();
        if (player.getHealth() <= 0.0) {
            for (final ItemStack is : player.getInventory().getContents()) {
                if (is != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), is);
                    player.getInventory().removeItem(new ItemStack[] { is });
                }
            }
            for (final ItemStack is : player.getInventory().getArmorContents()) {
                if (is.getAmount() != 0) {
                    player.getWorld().dropItemNaturally(player.getLocation(), is);
                }
            }
            player.getInventory().setArmorContents(new ItemStack[4]);
            final Location loc = player.getWorld().getSpawnLocation();
            final int orbs = (int)player.getExp();
            final int level = player.getLevel();
            final int total = (2 * level * level + 5 * orbs) / 5;
            final World world = player.getWorld();
            ((ExperienceOrb)world.spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB)).setExperience(total);
            player.setExp(0.0f);
            player.setLevel(0);
            player.setFoodLevel(20);
            player.setHealth(20.0);
            player.getActivePotionEffects().clear();
            player.setFireTicks(0);
        }
        final SectorPartDataConfig fromSectorPart = SectorManager.getCurrentSectorPart();
        final SectorDataConfig fromSectorInstance = SectorManager.getCurrentSectorInstance();
        final String fromSectorName = fromSectorPart.getSectorPartName() + "_" + fromSectorInstance.getSectorInstanceName();
        SectorTeleportTicker.removePlayer(player);
        SectorTeleportTicker.removeTeleported(player.getUniqueId());
        final PlayerData oldData = DataStore.getPlayerData(player.getUniqueId());
        if (oldData != null && oldData.isNeedingSave()) {
            final PlayerData newData = new PlayerData(player, oldData.getDimSwitch(), SafeTeleportType.UNSAFE);
            if (!player.isDead()) {
                newData.setLastSector(fromSectorName);
            }
            else {
                newData.setLocation(new TeleportLocation(player.getWorld().getSpawnLocation()));
                newData.setLastSector(CcSectorsAPI.getCurrentSectorName());
            }
            newData.setNeedingSave(false);
            try {
                CcSectors.getBackend().setPlayerData(player.getUniqueId(), newData);
            }
            catch (Exception ex) {
                Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                CcSectors.getBackend().setPlayerLastSector(player.getUniqueId(), fromSectorName);
            }
            catch (Exception ex) {
                Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                CcSectors.getBackend().setPlayerLastLocation(player.getUniqueId(), new TeleportLocation(player.getLocation()));
            }
            catch (Exception ex) {
                Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            PlayerListener.itemsApplied.remove(player.getUniqueId());
            DataStore.removePlayerData(uuid);
            System.out.println("Saving data on quit " + player.getName());
        }
        else {
            System.out.println("Not saving data on quit " + player.getName());
        }
    }
    
    @EventHandler
    public static void onServerQuit(final SectorsServerQuitEvent event) {
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onTeleportLowest(final PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onTeleportLowest(final PlayerPortalEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTeleport(final PlayerTeleportEvent event) {
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final Location from = event.getFrom();
        final Location to = event.getTo();
        System.out.println("Teleporting " + player.getName() + " to " + to);
        final PlayerData data = DataStore.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        if (!SectorManager.isInRightSectorPart(to, ConfigManager.getConfig().getNoMoveDistnace())) {
            if (player.isInsideVehicle()) {
                player.leaveVehicle();
            }
            final SectorPartDataConfig toSectorPart = SectorManager.getCorrectSector(to);
            final boolean isInWar = checkRelog(player);
            if (toSectorPart == null || isInWar) {
                if (toSectorPart == null) {
                    System.out.println("Sector unknow " + toSectorPart);
                    event.setCancelled(true);
                }
                if (isInWar) {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Jestes podczas walki! Nie mozesz przejsc przez sektor!");
                    event.setCancelled(true);
                }
            }
            else if (!PlayerListener.redirecting.contains(player.getUniqueId())) {
                PlayerListener.redirecting.add(player.getUniqueId());
                final SectorDataConfig toSectorInstance = toSectorPart.getSectorInstances().get(new Random().nextInt(toSectorPart.getSectorInstances().size()));
                final String sectorName = toSectorPart.getSectorPartName() + "_" + toSectorInstance.getSectorInstanceName();
                data.setLastSector(sectorName);
                if (from.getWorld().getEnvironment() == World.Environment.NETHER && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
                    data.setDimSwitch(DimSwitch.NORMAL);
                    data.setSafeTeleprotType(SafeTeleportType.SAFE);
                }
                else if (from.getWorld().getEnvironment() == World.Environment.NORMAL && to.getWorld().getEnvironment() == World.Environment.NETHER) {
                    data.setDimSwitch(DimSwitch.NETHER);
                    data.setSafeTeleprotType(SafeTeleportType.SAFE);
                }
                else {
                    data.setDimSwitch(DimSwitch.SAME);
                }
                data.setLocation(new TeleportLocation(to));
                data.setNeedingSave(true);
                DataStore.setPlayerData(uuid, data);
                RedirectManager.redirectRequest(sectorName, player, true);
            }
            else {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onMove(final PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final long startTime = System.currentTimeMillis();
        final Player player = event.getPlayer();
        if (!player.isOnline()) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (!SectorManager.isInRightSectorPart(to, ConfigManager.getConfig().getNoMoveDistnace())) {
            if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.WORLD) {
                final SectorPartDataConfig toSectorPart = SectorManager.getCorrectSectorSectorTypeSensive(to.getX(), to.getZ(), SectorManager.getCurrentSectorPart().getSectorType());
                final boolean isInWar = checkRelog(player);
                if (toSectorPart == null || isInWar) {
                    if (toSectorPart == null) {
                        System.out.println("Sector unknow " + toSectorPart);
                        event.setTo(event.getFrom());
                    }
                    if (isInWar) {
                        player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Jestes podczas walki! Nie mozesz przejsc przez sektor!");
                        event.setTo(event.getFrom());
                    }
                }
                else {
                    if (player.isInsideVehicle()) {
                        player.leaveVehicle();
                    }
                    final PlayerData data = DataStore.getPlayerData(uuid);
                    if (data == null) {
                        System.out.println("Wut? PlayerData == null? online: " + player.isOnline());
                    }
                    else if (!PlayerListener.redirecting.contains(player.getUniqueId())) {
                        PlayerListener.redirecting.add(player.getUniqueId());
                        final SectorDataConfig toSectorInstance = toSectorPart.getRandomSectorInstance();
                        final String sectorName = toSectorPart.getSectorPartName() + "_" + toSectorInstance.getSectorInstanceName();
                        data.setLocation(new TeleportLocation(to));
                        data.setNeedingSave(true);
                        DataStore.setPlayerData(uuid, data);
                        RedirectManager.redirectRequest(sectorName, player, true);
                        try {
                            CcSectors.getBackend().setPlayerLastSector(uuid, sectorName);
                        }
                        catch (Exception ex) {
                            Logger.getLogger(PlayerListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else {
                        event.setCancelled(true);
                    }
                }
            }
            else {
                event.setTo(event.getFrom());
            }
        }
        else {
            final Double distance = SectorManager.getDistanceToNearestSector(to, ConfigManager.getConfig().getNoMoveDistnace());
            final boolean isInWar = checkRelog(player);
            if (distance <= 10.0 && isInWar) {
                final CuboidData currentCuboidData = SectorManager.getCurrentSectorPart().getCuboidData();
                final int knockX = currentCuboidData.getMinX() + (currentCuboidData.getMaxX() - currentCuboidData.getMinX()) / 2;
                final double knockY = player.getLocation().getY();
                final int knockZ = currentCuboidData.getMinZ() + (currentCuboidData.getMaxZ() - currentCuboidData.getMinZ()) / 2;
                final Location sectorCenter = new Location(player.getLocation().getWorld(), (double)knockX, knockY, (double)knockZ);
                final Vector unitVector = sectorCenter.toVector().subtract(player.getLocation().toVector()).normalize();
                player.setVelocity(unitVector.multiply(1.5));
                player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Jestes podczas walki! Nie mozesz przejsc przez sektor!");
            }
            if (distance > 100.0) {
                BossBarApi.removeBar(player);
                return;
            }
            final float part = 1.0f - (float)(Object)distance / 100.0f;
            BossBarApi.setName(player, ChatColor.AQUA + "Do konca sektora pozostalo:" + ChatColor.RED.toString() + Math.round(distance), part);
        }
    }
    
    public static boolean checkRelog(final Player player) {
        if (player == null || player.getName() == null || EntityDamageByEntityListener.antiRelog == null) {
            return false;
        }
        final DamageData damageData = EntityDamageByEntityListener.antiRelog.get(player.getName());
        if (damageData != null && System.currentTimeMillis() - damageData.time <= 15000L) {
            if (damageData.damager instanceof Player) {
                final Player damager = (Player)damageData.damager;
            }
            else {
                if (!(damageData instanceof Arrow)) {
                    return false;
                }
                final Arrow arrow = (Arrow)damageData;
                if (!(arrow.getShooter() instanceof Player)) {
                    return false;
                }
                final Player damager = (Player)arrow.getShooter();
            }
            return true;
        }
        return false;
    }
    
    @EventHandler
    public static void onRedirect(final SectorsRedirectEvent event) {
    }
    
    @EventHandler
    public static void onQuit(final SectorsServerQuitEvent event) {
        DataStore.removePlayerOnSector(event.getUUID());
        DataStore.removePlayerName(event.getName());
    }
    
    public static void main(final String... args) {
    }
    
    static {
        itemsApplied = new ArrayList<UUID>();
        PlayerListener.redirecting = new ArrayList<UUID>();
    }
}
