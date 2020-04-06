// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.config;

import pl.best241.ccsectors.data.SectorType;
import java.util.ArrayList;

public class DefaultConfigLoader
{
    public static SectorPluginConfig getDefaultSectorPluginConfig() {
        final SectorPluginConfig sectorPluginConfig = new SectorPluginConfig(25, 4, getDefaultSectorPartsList());
        return sectorPluginConfig;
    }
    
    private static ArrayList<SectorPartDataConfig> getDefaultSectorPartsList() {
        final SectorDataConfig sectorSpawn = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorN = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorN2 = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorN3 = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorS = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorS2 = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorS3 = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorW = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorW2 = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorW3 = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorE = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorE2 = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorE3 = new SectorDataConfig("0", "world");
        final SectorDataConfig sectorNether = new SectorDataConfig("0", "world_nether");
        final ArrayList<SectorPartDataConfig> sectorPartList = new ArrayList<SectorPartDataConfig>();
        final SectorPartDataConfig sectorSpawnPart = new SectorPartDataConfig("SPAWN", 500, -500, 500, -500, SectorType.WORLD, new SectorDataConfig[] { sectorSpawn });
        final SectorPartDataConfig sectorN0Part = new SectorPartDataConfig("N0", 500, -2500, -500, -2500, SectorType.WORLD, new SectorDataConfig[] { sectorN });
        final SectorPartDataConfig sectorS0Part = new SectorPartDataConfig("S0", -500, 2500, 500, 2500, SectorType.WORLD, new SectorDataConfig[] { sectorS });
        final SectorPartDataConfig sectorW0Part = new SectorPartDataConfig("W0", -2500, -500, -500, 2500, SectorType.WORLD, new SectorDataConfig[] { sectorW });
        final SectorPartDataConfig sectorE0Part = new SectorPartDataConfig("E0", 500, 2500, -2500, 500, SectorType.WORLD, new SectorDataConfig[] { sectorE });
        final SectorPartDataConfig sectorN1Part = new SectorPartDataConfig("N1", -4500, -1000, -4500, -2500, SectorType.WORLD, new SectorDataConfig[] { sectorN2 });
        final SectorPartDataConfig sectorS1Part = new SectorPartDataConfig("S1", 4500, 1000, 2500, 4500, SectorType.WORLD, new SectorDataConfig[] { sectorS2 });
        final SectorPartDataConfig sectorW1Part = new SectorPartDataConfig("W1", -4500, -2500, 4500, 1000, SectorType.WORLD, new SectorDataConfig[] { sectorW2 });
        final SectorPartDataConfig sectorE1Part = new SectorPartDataConfig("E1", 2500, 4500, -4500, -1000, SectorType.WORLD, new SectorDataConfig[] { sectorE2 });
        final SectorPartDataConfig sectorN2Part = new SectorPartDataConfig("N2", -1000, 2500, -4500, -2500, SectorType.WORLD, new SectorDataConfig[] { sectorN3 });
        final SectorPartDataConfig sectorS2Part = new SectorPartDataConfig("S2", 1000, -2500, 2500, 4500, SectorType.WORLD, new SectorDataConfig[] { sectorS3 });
        final SectorPartDataConfig sectorW2Part = new SectorPartDataConfig("W2", -4500, -2500, -2500, 1000, SectorType.WORLD, new SectorDataConfig[] { sectorW3 });
        final SectorPartDataConfig sectorE2Part = new SectorPartDataConfig("E2", 2500, 4500, -1000, 2500, SectorType.WORLD, new SectorDataConfig[] { sectorE3 });
        final SectorPartDataConfig sectorNetherPart = new SectorPartDataConfig("NETHER", 500, -500, 500, -500, SectorType.NETHER, new SectorDataConfig[] { sectorNether });
        sectorPartList.add(sectorSpawnPart);
        sectorPartList.add(sectorN0Part);
        sectorPartList.add(sectorS0Part);
        sectorPartList.add(sectorW0Part);
        sectorPartList.add(sectorE0Part);
        sectorPartList.add(sectorN1Part);
        sectorPartList.add(sectorS1Part);
        sectorPartList.add(sectorW1Part);
        sectorPartList.add(sectorE1Part);
        sectorPartList.add(sectorN2Part);
        sectorPartList.add(sectorS2Part);
        sectorPartList.add(sectorW2Part);
        sectorPartList.add(sectorE2Part);
        sectorPartList.add(sectorNetherPart);
        return sectorPartList;
    }
}
