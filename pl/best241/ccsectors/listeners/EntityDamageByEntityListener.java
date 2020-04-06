// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.best241.ccsectors.data.DamageData;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class EntityDamageByEntityListener implements Listener
{
    public static HashMap<String, DamageData> antiRelog;
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void entityDamageByEntityListener(final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            Player damager = null;
            if (event.getDamager() instanceof Player) {
                damager = (Player)event.getDamager();
            }
            else if (event.getDamager() instanceof Arrow) {
                final Arrow arrow = (Arrow)event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    damager = (Player)arrow.getShooter();
                }
            }
            if (damager == null) {
                return;
            }
            EntityDamageByEntityListener.antiRelog.put(damager.getName(), new DamageData((Entity)player));
            EntityDamageByEntityListener.antiRelog.put(player.getName(), new DamageData((Entity)damager));
        }
    }
    
    static {
        EntityDamageByEntityListener.antiRelog = new HashMap<String, DamageData>();
    }
}
