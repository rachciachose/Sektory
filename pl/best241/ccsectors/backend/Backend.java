// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.backend;

import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.ccsectors.data.PlayerData;
import java.util.UUID;

public interface Backend
{
    PlayerData getPlayerData(final UUID p0) throws Exception;
    
    void setPlayerData(final UUID p0, final PlayerData p1) throws Exception;
    
    void removePlayerData(final UUID p0) throws Exception;
    
    String getPlayerLastSector(final UUID p0) throws Exception;
    
    void setPlayerLastSector(final UUID p0, final String p1) throws Exception;
    
    UUID getPlayerLastUUID(final String p0) throws Exception;
    
    String getPlayerLastNick(final UUID p0) throws Exception;
    
    void setPlayerLastLocation(final UUID p0, final TeleportLocation p1) throws Exception;
    
    TeleportLocation getPlayerLastLocation(final UUID p0) throws Exception;
    
    void setRandomSpawn(final boolean p0) throws Exception;
    
    boolean getRandomSpawn() throws Exception;
    
    void setNetherOn(final Boolean p0) throws Exception;
    
    boolean isNetherOn() throws Exception;
}
