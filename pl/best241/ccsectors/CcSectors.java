// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors;

import pl.best241.ccsectors.data.CuboidData;
import java.util.Iterator;
import pl.best241.ccsectors.data.DimSwitch;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.rdbplugin.pubsub.PubSub;
import pl.best241.ccsectors.api.CcSectorsAPI;
import java.util.UUID;
import pl.best241.ccsectors.commands.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.best241.ccsectors.config.SectorDataConfig;
import pl.best241.ccsectors.config.SectorPartDataConfig;
import org.bukkit.entity.Player;
import pl.best241.ccsectors.data.PlayerData;
import pl.best241.ccsectors.data.SafeTeleportType;
import pl.best241.ccsectors.data.DataStore;
import pl.best241.ccsectors.data.SectorType;
import pl.best241.ccsectors.managers.SectorManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccsectors.managers.PubSubManager;
import pl.best241.ccsectors.tickers.SectorTeleportTicker;
import pl.best241.ccsectors.backend.RedisBackend;
import pl.best241.ccsectors.config.YamlConfigManager;
import pl.best241.ccsectors.config.ConfigManager;
import pl.best241.ccsectors.listeners.SectorListeners;
import pl.best241.ccsectors.listeners.PubSubRecieveMessageListener;
import pl.best241.ccsectors.listeners.BlockListener;
import pl.best241.ccsectors.listeners.EntityDamageByEntityListener;
import pl.best241.ccsectors.listeners.EntityExplodeListener;
import org.bukkit.event.Listener;
import pl.best241.ccsectors.listeners.PlayerListener;
import pl.best241.ccsectors.parser.JsonDataParser;
import org.bukkit.plugin.Plugin;
import pl.best241.ccsectors.messages.MessagesData;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import pl.best241.ccsectors.backend.Backend;
import org.bukkit.plugin.java.JavaPlugin;

public class CcSectors extends JavaPlugin
{
    private static Backend backend;
    private static CcSectors plugin;
    private static boolean isNetherOn;
    private static final ArrayList<String> needConfirmSoftRestart;
    
    public void onEnable() {
        if (!this.getServer().getPluginManager().isPluginEnabled("rdbPlugin")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "rdbPlugin not enabled! ccSectors not started!");
            this.setEnabled(false);
        }
        MessagesData.loadMessages((Plugin)(CcSectors.plugin = this));
        JsonDataParser.loadGson();
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new EntityExplodeListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new EntityDamageByEntityListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new BlockListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PubSubRecieveMessageListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new SectorListeners(), (Plugin)this);
        ConfigManager.loadConfig("config.json");
        YamlConfigManager.load("config.yml");
        CcSectors.backend = new RedisBackend();
        SectorTeleportTicker.savePlayerDataTicker();
        SectorTeleportTicker.startTicker();
        SectorTeleportTicker.runNetherTicker();
        PubSubManager.listen();
        try {
            Thread.sleep(500L);
        }
        catch (InterruptedException ex) {
            Logger.getLogger(CcSectors.class.getName()).log(Level.SEVERE, null, ex);
        }
        PubSubManager.broadcastRequestAllOnlinePlayers();
        PubSubManager.broadcastRequestAllOnlinePlayerNames();
        try {
            CcSectors.isNetherOn = CcSectors.backend.isNetherOn();
        }
        catch (Exception ex2) {
            Logger.getLogger(CcSectors.class.getName()).log(Level.SEVERE, null, ex2);
        }
    }
    
    public void onDisable() {
        if (SectorManager.getCurrentSectorPart().getSectorType() == SectorType.EVENT) {
            return;
        }
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final SectorPartDataConfig fromSectorPart = SectorManager.getCurrentSectorPart();
            final SectorDataConfig fromSectorInstance = SectorManager.getCurrentSectorInstance();
            final String fromSectorName = fromSectorPart.getSectorPartName() + "_" + fromSectorInstance.getSectorInstanceName();
            SectorTeleportTicker.removePlayer(player);
            SectorTeleportTicker.removeTeleported(player.getUniqueId());
            final PlayerData oldData = DataStore.getPlayerData(player.getUniqueId());
            final PlayerData newData = new PlayerData(player, oldData.getDimSwitch(), SafeTeleportType.UNSAFE);
            newData.setLastSector(fromSectorName);
            try {
                getBackend().setPlayerData(player.getUniqueId(), newData);
            }
            catch (Exception ex) {
                Logger.getLogger(CcSectors.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                getBackend().setPlayerLastSector(player.getUniqueId(), fromSectorName);
            }
            catch (Exception ex) {
                Logger.getLogger(CcSectors.class.getName()).log(Level.SEVERE, null, ex);
            }
            DataStore.removePlayerData(player.getUniqueId());
        }
    }
    
    public static boolean isNetherOn() {
        return CcSectors.isNetherOn;
    }
    
    public static void setNetherOn(final boolean isEnabled) {
        CcSectors.isNetherOn = isEnabled;
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String lable, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("playerinfo")) {
            if (sender.hasPermission("ccSectors.playerInfo") || sender.isOp()) {
                if (args.length == 1) {
                    Commands.commandPlayerInfo(sender, args[0]);
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Uzycie: /playerinfo nick");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("softrestart")) {
            if (sender.hasPermission("ccSectors.softRestart")) {
                if (CcSectors.needConfirmSoftRestart.contains(sender.getName()) && args.length == 1 && args[0].equals("confirm")) {
                    CcSectors.needConfirmSoftRestart.remove(sender.getName());
                    PubSubManager.broadcastSoftRestart(sender.getName());
                }
                else if (!CcSectors.needConfirmSoftRestart.contains(sender.getName()) && args.length == 0) {
                    CcSectors.needConfirmSoftRestart.add(sender.getName());
                    Bukkit.getScheduler().runTaskLater((Plugin)this, (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            if (CcSectors.needConfirmSoftRestart.contains(sender.getName())) {
                                CcSectors.needConfirmSoftRestart.remove(sender.getName());
                                sender.sendMessage(ChatColor.RED + "Potwierdzenie miekkiego restartu wygaslo!");
                            }
                        }
                    }, 200L);
                    sender.sendMessage(ChatColor.BLUE + "Aby potwierdzic miekki restart wpisz /softrestart confirm");
                }
                else if (CcSectors.needConfirmSoftRestart.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Uzycie: /softrestart confirm");
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Uzycie: /softrestart");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("hardrestart")) {
            if (!sender.hasPermission("ccSectors.hardRestart")) {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("glist")) {
            if (sender.hasPermission("ccSectors.glist")) {
                Commands.commandGlist(sender);
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("randomspawn")) {
            if (sender.hasPermission("ccSectors.randomspawn")) {
                if (args.length == 1 && (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("false"))) {
                    final boolean value = Boolean.valueOf(args[0]);
                    try {
                        getBackend().setRandomSpawn(value);
                    }
                    catch (Exception ex) {
                        Logger.getLogger(CcSectors.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.DARK_GREEN + "Ustawiono random spawn: " + value);
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Uzycie: /randomspawn true|false!");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("debugplayers")) {
            if (sender.hasPermission("ccSectors.debugplayers")) {
                for (final UUID uuid : DataStore.getOnlinePlayersList()) {
                    try {
                        final String nick = CcSectorsAPI.getNick(uuid);
                        sender.sendMessage("Nick: " + nick + " (" + uuid + ")");
                    }
                    catch (Exception ex2) {
                        Logger.getLogger(CcSectors.class.getName()).log(Level.SEVERE, null, ex2);
                    }
                }
            }
        }
        else if (cmd.getName().equalsIgnoreCase("netheron")) {
            if (sender.hasPermission("ccSectors.netheron")) {
                PubSub.broadcast("ccSectors.setNetherOn", "true");
                try {
                    getBackend().setNetherOn(true);
                }
                catch (Exception ex3) {
                    Logger.getLogger(CcSectors.class.getName()).log(Level.SEVERE, null, ex3);
                }
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.DARK_GREEN + "Nether zostal wlaczony!");
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("netheroff")) {
            if (sender.hasPermission("ccSectors.netheron")) {
                PubSub.broadcast("ccSectors.setNetherOn", "false");
                try {
                    getBackend().setNetherOn(false);
                }
                catch (Exception ex3) {
                    Logger.getLogger(CcSectors.class.getName()).log(Level.SEVERE, null, ex3);
                }
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.DARK_GREEN + "Nether zostal wylaczony!");
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
            }
        }
        else if (!cmd.getName().equalsIgnoreCase("sectorsautosave")) {
            if (cmd.getName().equalsIgnoreCase("configreload")) {
                if (sender.hasPermission("ccSectors.configReload")) {
                    ConfigManager.loadConfig("config.json");
                    YamlConfigManager.load("config.yml");
                    sender.sendMessage("Configs reloaded sucessfully");
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
                }
            }
            else if (cmd.getName().equalsIgnoreCase("tpsector")) {
                if (sender instanceof Player) {
                    final Player player = (Player)sender;
                    if (sender.hasPermission("ccSectors.tpsector")) {
                        String targetSectorName = null;
                        if (args.length == 0) {
                            targetSectorName = CcSectorsAPI.getSectorName();
                        }
                        else if (args.length == 1) {
                            targetSectorName = args[0];
                        }
                        else {
                            sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Uzycie: /tpsector <SEKTOR>");
                        }
                        final SectorPartDataConfig sectorPartByName = SectorManager.getSectorPartByName(targetSectorName);
                        if (sectorPartByName == null) {
                            sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Sektor " + ChatColor.BLUE + targetSectorName + ChatColor.RED + " nie istnieje!");
                            return false;
                        }
                        final CuboidData currentCuboidData = sectorPartByName.getCuboidData();
                        final int sectorX = currentCuboidData.getMinX() + (currentCuboidData.getMaxX() - currentCuboidData.getMinX()) / 2;
                        final double y = player.getLocation().getY();
                        final int sectorZ = currentCuboidData.getMinZ() + (currentCuboidData.getMaxZ() - currentCuboidData.getMinZ()) / 2;
                        final TeleportLocation targetSectorCenter = new TeleportLocation(sectorPartByName.getSectorType(), sectorX, y, sectorZ);
                        player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.DARK_GREEN + "Teleportacja do srodka sektora " + targetSectorName);
                        try {
                            CcSectorsAPI.teleportPlayerAuto(player.getUniqueId(), new TeleportLocation(player.getLocation()), targetSectorCenter, SafeTeleportType.TOP, DimSwitch.AUTO, false);
                        }
                        catch (Exception ex4) {
                            Logger.getLogger(CcSectors.class.getName()).log(Level.SEVERE, null, ex4);
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla graczy!");
                }
            }
        }
        return false;
    }
    
    public static CcSectors getPlugin() {
        return CcSectors.plugin;
    }
    
    public static Backend getBackend() {
        return CcSectors.backend;
    }
    
    static {
        needConfirmSoftRestart = new ArrayList<String>();
    }
}
