// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.managers;

import pl.best241.ccsectors.data.CuboidData;
import pl.best241.ccsectors.config.SectorPluginConfig;
import pl.best241.ccsectors.config.YamlConfigManager;
import pl.best241.ccsectors.data.SectorType;
import pl.best241.ccsectors.api.TeleportLocation;
import java.util.Iterator;
import pl.best241.ccsectors.config.ConfigManager;
import org.bukkit.Location;
import pl.best241.ccsectors.config.SectorDataConfig;
import pl.best241.ccsectors.config.SectorPartDataConfig;

public class SectorManager
{
    private static SectorPartDataConfig rightSector;
    private static SectorDataConfig rightSectorInstance;
    
    public static SectorPartDataConfig getCorrectSector(final Location loc) {
        for (final SectorPartDataConfig part : ConfigManager.getConfig().getSectorParts()) {
            if (part.getCuboidData().isOnCuboid(loc)) {
                return part;
            }
        }
        return null;
    }
    
    public static SectorPartDataConfig getCorrectSectorTypeSensive(final TeleportLocation loc) {
        for (final SectorPartDataConfig part : ConfigManager.getConfig().getSectorParts()) {
            if (loc.getSectorType() == part.getSectorType() && part.getCuboidData().isOnCuboid(loc.getX(), loc.getZ())) {
                return part;
            }
        }
        return null;
    }
    
    public static SectorPartDataConfig getCorrectSector(final TeleportLocation loc) {
        for (final SectorPartDataConfig part : ConfigManager.getConfig().getSectorParts()) {
            if (part.getCuboidData().isOnCuboid(loc.getX(), loc.getZ())) {
                return part;
            }
        }
        return null;
    }
    
    public static SectorPartDataConfig getCorrectSectorSectorTypeSensive(final double x, final double z, final SectorType type) {
        for (final SectorPartDataConfig part : ConfigManager.getConfig().getSectorParts()) {
            if (part.getCuboidData().isOnCuboid(x, z) && part.getSectorType() == type) {
                return part;
            }
        }
        return null;
    }
    
    public static boolean isInRightSectorPart(final Location loc) {
        return isInRightSectorPart(loc, 0);
    }
    
    public static boolean isInRightSectorPart(final Location loc, final int modifier) {
        final SectorPartDataConfig sector = getCurrentSectorPart();
        return sector != null && sector.getCuboidData().isOnCuboid(loc, modifier);
    }
    
    public static SectorPartDataConfig getCurrentSectorPart() {
        if (SectorManager.rightSector != null) {
            return SectorManager.rightSector;
        }
        final SectorPluginConfig config = ConfigManager.getConfig();
        for (final SectorPartDataConfig part : config.getSectorParts()) {
            if (part.getSectorPartName().equalsIgnoreCase(YamlConfigManager.currentSectorPartName)) {
                return SectorManager.rightSector = part;
            }
        }
        return null;
    }
    
    public static SectorPartDataConfig getNetherSectorPart() {
        final SectorPluginConfig config = ConfigManager.getConfig();
        for (final SectorPartDataConfig part : config.getSectorParts()) {
            if (part.getSectorType() == SectorType.NETHER) {
                return part;
            }
        }
        return null;
    }
    
    public static SectorPartDataConfig getEventSectorPart() {
        final SectorPluginConfig config = ConfigManager.getConfig();
        for (final SectorPartDataConfig part : config.getSectorParts()) {
            if (part.getSectorType() == SectorType.EVENT) {
                return part;
            }
        }
        return null;
    }
    
    public static SectorDataConfig getCurrentSectorInstance() {
        if (SectorManager.rightSectorInstance != null) {
            return SectorManager.rightSectorInstance;
        }
        final SectorPluginConfig config = ConfigManager.getConfig();
        final SectorPartDataConfig part = getCurrentSectorPart();
        for (final SectorDataConfig instance : part.getSectorInstances()) {
            if (instance.getSectorInstanceName().equalsIgnoreCase(YamlConfigManager.currentSectorName)) {
                return SectorManager.rightSectorInstance = instance;
            }
        }
        return null;
    }
    
    public static SectorPartDataConfig getSectorPartByName(final String sectorName) {
        String sectorPartName = null;
        if (sectorName.contains("_")) {
            final String[] splittedNames = sectorName.split("_");
            if (splittedNames.length != 2) {
                return null;
            }
            sectorPartName = splittedNames[0];
        }
        else {
            sectorPartName = sectorName;
        }
        for (final SectorPartDataConfig sectorPartDataConfig : ConfigManager.getConfig().getSectorParts()) {
            if (sectorPartDataConfig.getSectorPartName().equalsIgnoreCase(sectorPartName)) {
                return sectorPartDataConfig;
            }
        }
        return null;
    }
    
    public static double getDistanceToNearestSector(final Location loc, final int modifier) {
        final SectorPartDataConfig currentSectorPart = getCurrentSectorPart();
        final CuboidData cuboidData = currentSectorPart.getCuboidData();
        final double xToMaxX = Math.abs(cuboidData.getMaxX() + modifier - loc.getX());
        final double xToMinX = Math.abs(cuboidData.getMinX() - modifier - loc.getX());
        final double zToMaxZ = Math.abs(cuboidData.getMaxZ() + modifier - loc.getZ());
        final double zToMinZ = Math.abs(cuboidData.getMinZ() - modifier - loc.getZ());
        final double x = Math.abs(Math.min(xToMaxX, xToMinX));
        final double z = Math.abs(Math.min(zToMaxZ, zToMinZ));
        final double distance = Math.abs(Math.min(x, z));
        return distance;
    }
}
