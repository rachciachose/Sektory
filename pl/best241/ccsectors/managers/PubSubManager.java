// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.managers;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.UUID;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import pl.best241.rdbplugin.JedisFactory;
import pl.best241.ccsectors.listeners.PubSubListener;

public class PubSubManager
{
    private static final PubSubListener psl;
    
    public static void listen() {
        Jedis jedis;
        final Thread listenThread = new Thread(() -> {
            while (true) {
                jedis = JedisFactory.getInstance().getNewUnpooledJedis();
                jedis.clientSetname("CcSectors_subscriber");
                System.out.println("Starting subscriber");
                jedis.subscribe((JedisPubSub)PubSubManager.psl, new String[] { "ccSectors.playerJoined", "ccSectors.removeWelcomeMessage", "ccSectors.playersOnlineNamesResponse", "ccSectors.playersOnlineResponse", "ccSectors.playersOnSectorJoin", "ccSectors.quitPlayerFromServer", "ccSectors.clearAllPlayers", "ccSectors.softrestart", "ccSectors.playersOnSectorOnlineResponse", "ccSectors.joinPlayerOnServer", "ccSectors.randomTpAskForCoords", "ccSectors.randomTpAcceptCoords", "ccSectors.randomTpNotAcceptCoords", "ccSectors.randomTpPlayersAskForCoords", "ccSectors.randomTpPlayersAcceptCoords", "ccSectors.randomTpPlayersNotAcceptCoords" });
                System.out.println("Closing subscriber");
                jedis.quit();
            }
        });
        listenThread.start();
    }
    
    public static void stopListen() {
        PubSubManager.psl.unsubscribe();
    }
    
    public static void broadcastRequestAllOnlinePlayers(final String sector) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccSectors.playersOnSectorOnlineRequest", sector);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void broadcastSoftRestart(final String user) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccSectors.softrestart", user);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void broadcastRequestAllOnlinePlayers() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccSectors.playersOnlineRequest", "none");
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void broadcastRequestAllOnlinePlayerNames() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccSectors.playersOnlineNamesRequest", "none");
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void broadcastRandomTeleport(final String toSectorName, final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccSectors.randomTeleportPlayer", toSectorName + ";" + uuid.toString());
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void broadcastRandomTeleportAskGroup(final String toSectorName, final ArrayList<UUID> uuids) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        String parsedUUIDs = "";
        for (final UUID uuid : uuids) {
            if (!parsedUUIDs.isEmpty()) {
                parsedUUIDs += ":";
            }
            parsedUUIDs += uuid.toString();
        }
        final String data = toSectorName + ";" + parsedUUIDs;
        jedis.publish("ccSectors.randomTeleportGroupAsk", data);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    static {
        psl = new PubSubListener();
    }
}
