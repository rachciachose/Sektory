// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.config;

public class SectorDataConfig
{
    private final String sectorInstanceName;
    private final String worldName;
    
    public SectorDataConfig(final String sectorName, final String worldName) {
        this.sectorInstanceName = sectorName;
        this.worldName = worldName;
    }
    
    public String getSectorInstanceName() {
        return this.sectorInstanceName;
    }
    
    public String getWorldName() {
        return this.worldName;
    }
}
