// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.listeners;

import org.bukkit.event.EventHandler;
import pl.best241.ccsectors.data.DataStore;
import pl.best241.ccsectors.events.SectorsServerQuitEvent;
import org.bukkit.event.Listener;

public class SectorListeners implements Listener
{
    @EventHandler
    public static void onSectorQuit(final SectorsServerQuitEvent event) {
        DataStore.removePlayerName(event.getName());
        DataStore.removePlayerOnSector(event.getUUID());
    }
}
