// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.listeners;

import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import pl.best241.ccsectors.managers.SectorManager;
import pl.best241.ccsectors.config.ConfigManager;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class BlockListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (!SectorManager.isInRightSectorPart(block.getLocation(), -ConfigManager.getConfig().getNoBuildDistance())) {
            event.setBuild(false);
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Znajdujesz sie za blisko sektora!");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (!SectorManager.isInRightSectorPart(block.getLocation(), -ConfigManager.getConfig().getNoBuildDistance())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Znajdujesz sie za blisko sektora!");
        }
    }
    
    @EventHandler
    public static void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();
        if (!SectorManager.isInRightSectorPart(block.getLocation(), -ConfigManager.getConfig().getNoBuildDistance())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Znajdujesz sie za blisko sektora!");
        }
    }
    
    @EventHandler
    public static void onBucketFill(final PlayerBucketFillEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();
        if (!SectorManager.isInRightSectorPart(block.getLocation(), -ConfigManager.getConfig().getNoBuildDistance())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Znajdujesz sie za blisko sektora!");
        }
    }
    
    @EventHandler
    public static void onBlockPhysic(final BlockPhysicsEvent event) {
        final Block block = event.getBlock();
        if (!SectorManager.isInRightSectorPart(block.getLocation(), -ConfigManager.getConfig().getNoBuildDistance())) {
            event.setCancelled(true);
        }
    }
}
