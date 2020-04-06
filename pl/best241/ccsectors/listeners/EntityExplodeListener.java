// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.listeners;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import pl.best241.ccsectors.managers.SectorManager;
import pl.best241.ccsectors.config.ConfigManager;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.Listener;

public class EntityExplodeListener implements Listener
{
    @EventHandler
    public static void onExplode(final EntityExplodeEvent event) {
        for (final Block block : event.blockList()) {
            if (!SectorManager.isInRightSectorPart(block.getLocation(), -ConfigManager.getConfig().getNoBuildDistance())) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
