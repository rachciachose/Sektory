// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.events;

import org.bukkit.entity.Player;
import java.util.UUID;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class SectorsServerQuitEvent extends PlayerEvent
{
    private static final HandlerList handlers;
    private final UUID uuid;
    private final String name;
    
    public static HandlerList getHandlerList() {
        return SectorsServerQuitEvent.handlers;
    }
    
    public SectorsServerQuitEvent(final Player player, final UUID uuid, final String name) {
        super(player);
        this.uuid = uuid;
        this.name = name;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public HandlerList getHandlers() {
        return SectorsServerQuitEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
