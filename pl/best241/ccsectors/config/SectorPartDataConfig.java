// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.config;

import java.util.Collection;
import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;
import pl.best241.ccsectors.data.SectorType;
import pl.best241.ccsectors.data.CuboidData;

public class SectorPartDataConfig
{
    private final String sectorPartName;
    private final CuboidData cuboid;
    private final SectorType type;
    private final ArrayList<SectorDataConfig> sectorInstances;
    private static final Random random;
    
    public SectorPartDataConfig(final String sectorPartName, final int x1, final int x2, final int z1, final int z2, final SectorType sectorType, final SectorDataConfig... sectorInstances) {
        this.sectorInstances = new ArrayList<SectorDataConfig>();
        this.sectorPartName = sectorPartName;
        this.cuboid = new CuboidData(x1, x2, z1, z2);
        this.type = sectorType;
        this.sectorInstances.addAll(Arrays.asList(sectorInstances));
    }
    
    public String getSectorPartName() {
        return this.sectorPartName;
    }
    
    public CuboidData getCuboidData() {
        return this.cuboid;
    }
    
    public SectorType getSectorType() {
        return this.type;
    }
    
    public ArrayList<SectorDataConfig> getSectorInstances() {
        return this.sectorInstances;
    }
    
    public SectorDataConfig getRandomSectorInstance() {
        return this.getSectorInstances().get(SectorPartDataConfig.random.nextInt(this.getSectorInstances().size()));
    }
    
    static {
        random = new Random();
    }
}
