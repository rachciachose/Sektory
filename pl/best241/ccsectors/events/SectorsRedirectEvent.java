// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.events;

import java.util.UUID;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;

public class SectorsRedirectEvent extends ServerEvent
{
    private static final HandlerList handlers;
    private final UUID uuid;
    private final String name;
    
    public static HandlerList getHandlerList() {
        return SectorsRedirectEvent.handlers;
    }
    
    public SectorsRedirectEvent(final UUID uuid, final String name) {
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
        return SectorsRedirectEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
