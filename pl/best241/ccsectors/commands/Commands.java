// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.commands;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import pl.best241.ccsectors.data.DataStore;
import org.bukkit.entity.Player;
import pl.best241.ccsectors.data.PlayerData;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import pl.best241.ccsectors.CcSectors;
import org.bukkit.command.CommandSender;

public class Commands
{
    public static void commandPlayerInfo(final CommandSender player, final String nick) {
        try {
            final UUID uuid = CcSectors.getBackend().getPlayerLastUUID(nick);
            if (uuid == null) {
                player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Gracza nie ma w bazie!");
                return;
            }
            final PlayerData data = CcSectors.getBackend().getPlayerData(uuid);
            if (data == null) {
                player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Gracza nie ma w bazie!");
                return;
            }
            player.sendMessage(ChatColor.DARK_GRAY + "==============================");
            player.sendMessage(ChatColor.DARK_GREEN + "Nick: " + ChatColor.RED + nick);
            player.sendMessage(ChatColor.DARK_GREEN + "UUID: " + ChatColor.RED + uuid.toString());
            player.sendMessage(ChatColor.DARK_GREEN + "GameMode: " + ChatColor.RED + data.getGameMode());
            player.sendMessage(ChatColor.DARK_GREEN + "Lokalizacja: " + ChatColor.RED + "X:" + data.getLocation().getBlockX() + " Y:" + data.getLocation().getBlockY() + " Z:" + data.getLocation().getBlockZ());
            player.sendMessage(ChatColor.DARK_GREEN + "Poziom: " + ChatColor.RED + data.getLevel());
            player.sendMessage(ChatColor.DARK_GREEN + "Exp: " + ChatColor.RED + data.getExp());
            player.sendMessage(ChatColor.DARK_GREEN + "Moze latac: " + ChatColor.RED + data.getAllowFlight());
            player.sendMessage(ChatColor.DARK_GREEN + "Lata: " + ChatColor.RED + data.getIsFlying());
            player.sendMessage(ChatColor.DARK_GREEN + "Predkosc latania: " + ChatColor.RED + data.getFlySpeed());
            player.sendMessage(ChatColor.DARK_GREEN + "Predkosc chodzenia: " + ChatColor.RED + data.getWalkSpeed());
            player.sendMessage(ChatColor.DARK_GREEN + "Zycie: " + ChatColor.RED + data.getHealth());
            player.sendMessage(ChatColor.DARK_GREEN + "Glod: " + ChatColor.RED + data.getHunger());
            player.sendMessage(ChatColor.DARK_GREEN + "Safe loc type: " + ChatColor.RED + data.getSafeTeleportType());
            player.sendMessage(ChatColor.DARK_GREEN + "Sektor: " + ChatColor.RED + data.getLastSector());
            player.sendMessage(ChatColor.DARK_GRAY + "==============================");
        }
        catch (Exception ex) {
            Logger.getLogger(Commands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void commandStartkit(final CommandSender sender) {
        if (!(sender instanceof Player)) {
            return;
        }
        final Player player = (Player)sender;
    }
    
    public static void commandGlist(final CommandSender sender) {
        final ConcurrentHashMap<UUID, String> playersOnSectors = DataStore.getPlayersOnSectors();
        final HashMap<String, Integer> sectorPlayers = new HashMap<String, Integer>();
        final Enumeration<UUID> keys = playersOnSectors.keys();
        while (keys.hasMoreElements()) {
            final UUID nextElement = keys.nextElement();
            final String get = playersOnSectors.get(nextElement);
            Integer amount = sectorPlayers.get(get);
            if (amount == null) {
                amount = 0;
            }
            ++amount;
            sectorPlayers.put(get, amount);
        }
        String message = "";
        for (final String sector : sectorPlayers.keySet()) {
            final Integer players = sectorPlayers.get(sector);
            message = message + sector + ": " + players + " ";
        }
        sender.sendMessage(message);
    }
}
