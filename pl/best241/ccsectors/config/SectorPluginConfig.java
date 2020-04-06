// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.config;

import java.util.ArrayList;

public class SectorPluginConfig
{
    private final int noBuildDistance;
    private final int noMoveDistance;
    private final ArrayList<SectorPartDataConfig> sectorParts;
    
    public SectorPluginConfig(final int noBuildDistance, final int noMoveDistance, final ArrayList<SectorPartDataConfig> sectorParts) {
        this.noBuildDistance = noBuildDistance;
        this.noMoveDistance = noMoveDistance;
        this.sectorParts = sectorParts;
    }
    
    public int getNoBuildDistance() {
        return this.noBuildDistance;
    }
    
    public int getNoMoveDistnace() {
        return this.noMoveDistance;
    }
    
    public ArrayList<SectorPartDataConfig> getSectorParts() {
        return this.sectorParts;
    }
}
