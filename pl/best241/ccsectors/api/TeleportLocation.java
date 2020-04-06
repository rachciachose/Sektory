// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.api;

import java.util.Iterator;
import org.bukkit.World;
import pl.best241.ccsectors.config.SectorDataConfig;
import pl.best241.ccsectors.config.SectorPartDataConfig;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import pl.best241.ccsectors.managers.SectorManager;
import org.bukkit.Location;
import java.util.LinkedHashMap;
import pl.best241.ccsectors.data.SectorType;

public class TeleportLocation implements Cloneable
{
    private SectorType sectorType;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    
    public static TeleportLocation fromLinkedHashMap(final LinkedHashMap<String, Object> map) {
        return new TeleportLocation(SectorType.valueOf(map.get("sectorType")), map.get("x"), map.get("y"), map.get("z"), (float)(Object)map.get("yaw"), (float)(Object)map.get("pitch"));
    }
    
    public TeleportLocation(final Location location) {
        final SectorType type = SectorManager.getCurrentSectorPart().getSectorType();
        this.sectorType = type;
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }
    
    public SectorType getSectorType() {
        return this.sectorType;
    }
    
    public void setSectorType(final SectorType sectorType) {
        this.sectorType = sectorType;
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }
    
    public TeleportLocation(final SectorType type, final double x, final double y, final double z, final float yaw, final float pitch) {
        this.sectorType = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public TeleportLocation(final SectorType type, final double x, final double y, final double z) {
        this.sectorType = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }
    
    public TeleportLocation(final SectorType type, final int x, final int y, final int z) {
        this.sectorType = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }
    
    public Block getBlock() throws Exception {
        final SectorPartDataConfig correctSector = SectorManager.getCorrectSectorSectorTypeSensive(this.x, this.z, this.sectorType);
        final SectorPartDataConfig currentSector = SectorManager.getCurrentSectorPart();
        if (correctSector.getSectorPartName().equals(currentSector.getSectorPartName())) {
            final SectorDataConfig currentSectorInstance = SectorManager.getCurrentSectorInstance();
            final World world = Bukkit.getWorld(currentSectorInstance.getWorldName());
            return new Location(world, this.x, this.y, this.z).getBlock();
        }
        throw new Exception("Invoked not on current sector");
    }
    
    public int getBlockX() {
        return (int)this.x;
    }
    
    public int getBlockY() {
        return (int)this.y;
    }
    
    public int getBlockZ() {
        return (int)this.z;
    }
    
    public Location toLocation(final World world) {
        return new Location(world, this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }
    
    public TeleportLocation clone() {
        TeleportLocation obj;
        try {
            obj = (TeleportLocation)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error();
        }
        return obj;
    }
    
    public World getWorld() {
        for (final World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL && this.sectorType == SectorType.WORLD) {
                return world;
            }
            if (world.getEnvironment() == World.Environment.NETHER && SectorType.NETHER == this.sectorType) {
                return world;
            }
            if (this.sectorType == SectorType.EVENT) {
                return world;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "sectorType:" + this.getSectorType() + " x:" + this.getX() + " y:" + this.getY() + " z:" + this.getZ() + " yaw:" + this.getYaw() + " pitch:" + this.getPitch();
    }
}
