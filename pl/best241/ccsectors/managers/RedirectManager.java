// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.managers;

import pl.best241.ccsectors.api.TeleportLocation;
import java.util.UUID;
import org.bukkit.plugin.Plugin;
import pl.best241.lotosconnect.connect.request.Request;
import pl.best241.lotosconnect.connect.request.impl.RedirectRequest;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccsectors.CcSectors;
import pl.best241.ccsectors.data.PlayerData;
import pl.best241.ccsectors.data.SafeTeleportType;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import pl.best241.ccsectors.events.SectorsRedirectEvent;
import pl.best241.ccsectors.data.DataStore;
import java.util.Arrays;
import org.bukkit.entity.Player;
import pl.best241.lotosconnect.LotosConnect;
import pl.best241.lotosconnect.connect.Connect;

public class RedirectManager
{
    private static Connect connect;
    
    public static Connect getBukkitConnect() {
        if (RedirectManager.connect == null) {
            RedirectManager.connect = LotosConnect.getConnect();
        }
        return RedirectManager.connect;
    }
    
    public static void redirectRequest(final String server, final Player player, final boolean saveData) {
        final Throwable e = new Throwable();
        final StackTraceElement[] elements = e.getStackTrace();
        System.out.println("Redirect fired for player " + player.getName() + "in: \n" + Arrays.asList(elements));
        final UUID uuid = player.getUniqueId();
        final PlayerData oldData = DataStore.getPlayerData(uuid);
        if (saveData && oldData != null) {
            final TeleportLocation location = oldData.getLocation();
            final SectorsRedirectEvent event = new SectorsRedirectEvent(player.getUniqueId(), player.getName());
            Bukkit.getPluginManager().callEvent((Event)event);
            final PlayerData data = new PlayerData(player, oldData.getDimSwitch(), oldData.getSerializedLocation(), SafeTeleportType.SAFE);
            try {
                CcSectors.getBackend().setPlayerData(uuid, data);
            }
            catch (Exception ex) {
                Logger.getLogger(RedirectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                CcSectors.getBackend().setPlayerLastSector(player.getUniqueId(), server);
            }
            catch (Exception ex) {
                Logger.getLogger(RedirectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            data.setNeedingSave(false);
            DataStore.setPlayerData(uuid, data);
            System.out.println("Saving data on redirect!");
        }
        try {
            final Connect c = getBukkitConnect();
            c.request((Request)new RedirectRequest(server, player.getUniqueId()));
        }
        catch (Exception exception) {
            Bukkit.getScheduler().runTask((Plugin)CcSectors.getPlugin(), () -> player.kickPlayer("Blad podczas laczenia sie z sektorem " + server + "!\nJezeli to sie powtorzy zglos to na yt4u.pl"));
            exception.printStackTrace();
        }
    }
}
