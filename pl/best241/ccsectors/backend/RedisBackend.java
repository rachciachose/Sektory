// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.backend;

import pl.best241.ccsectors.parser.InventorySerializer;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.rdbplugin.JedisFactory;
import redis.clients.jedis.Jedis;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccsectors.parser.JsonDataParser;
import pl.best241.rdbplugin.SafeJedisInstructions;
import pl.best241.ccsectors.data.PlayerData;
import java.util.UUID;

public class RedisBackend implements Backend
{
    @Override
    public PlayerData getPlayerData(final UUID uuid) throws Exception {
        try {
            final boolean exists = SafeJedisInstructions.hexists("ccSectors.playerData", uuid.toString());
            if (!exists) {
                return null;
            }
            final String json = SafeJedisInstructions.hget("ccSectors.playerData", uuid.toString());
            if (json == null) {
                return null;
            }
            final PlayerData data = JsonDataParser.convertJsonToPlayerData(json);
            return data;
        }
        catch (Exception ex) {
            Logger.getLogger(RedisBackend.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    @Override
    public void setPlayerData(final UUID uuid, final PlayerData data) throws Exception {
        final String json = JsonDataParser.convertPlayerDataToJson(data);
        try {
            SafeJedisInstructions.hset("ccSectors.playerData", uuid.toString(), json);
        }
        catch (Exception ex) {
            Logger.getLogger(RedisBackend.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    public static void setPlayerData(final UUID uuid, final PlayerData data, final Jedis jedis) {
        final String json = JsonDataParser.convertPlayerDataToJson(data);
        jedis.hset("ccSectors.playerData", uuid.toString(), json);
    }
    
    @Override
    public void removePlayerData(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.hdel("ccSectors.playerData", new String[] { uuid.toString() });
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    @Override
    public String getPlayerLastSector(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String lastSector = jedis.hget("ccSectors.sectorByPlayer", uuid.toString());
        JedisFactory.getInstance().returnJedis(jedis);
        return lastSector;
    }
    
    @Override
    public void setPlayerLastSector(final UUID uuid, final String sector) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.hset("ccSectors.sectorByPlayer", uuid.toString(), sector);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void setPlayerLastSector(final UUID uuid, final String sector, final Jedis jedis) {
        jedis.hset("ccSectors.sectorByPlayer", uuid.toString(), sector);
    }
    
    @Override
    public UUID getPlayerLastUUID(final String nick) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String uuid = jedis.hget("nick2uuid", nick);
        JedisFactory.getInstance().returnJedis(jedis);
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid);
    }
    
    @Override
    public String getPlayerLastNick(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String nick = jedis.hget("uuid2nick", uuid.toString());
        JedisFactory.getInstance().returnJedis(jedis);
        return nick;
    }
    
    @Override
    public void setPlayerLastLocation(final UUID uuid, final TeleportLocation loc) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.hset("ccSectors.playerLastLocation", uuid.toString(), InventorySerializer.serializeLocation(loc));
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void setPlayerLastLocation(final UUID uuid, final TeleportLocation loc, final Jedis jedis) {
        jedis.hset("ccSectors.playerLastLocation", uuid.toString(), InventorySerializer.serializeLocation(loc));
    }
    
    @Override
    public TeleportLocation getPlayerLastLocation(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String rawData = jedis.hget("ccSectors.playerLastLocation", uuid.toString());
        JedisFactory.getInstance().returnJedis(jedis);
        if (rawData == null) {
            return null;
        }
        return InventorySerializer.deserializeLocation(rawData);
    }
    
    @Override
    public void setRandomSpawn(final boolean value) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.set("ccSectors.randomSpawn", Boolean.toString(value));
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    @Override
    public boolean getRandomSpawn() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String get = jedis.get("ccSectors.randomSpawn");
        JedisFactory.getInstance().returnJedis(jedis);
        return get != null && Boolean.valueOf(get);
    }
    
    @Override
    public boolean isNetherOn() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String get = jedis.get("ccSectors.isNetherOn");
        JedisFactory.getInstance().returnJedis(jedis);
        return get != null && Boolean.valueOf(get);
    }
    
    @Override
    public void setNetherOn(final Boolean isOn) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.set("ccSectors.isNetherOn", isOn.toString());
        JedisFactory.getInstance().returnJedis(jedis);
    }
}
