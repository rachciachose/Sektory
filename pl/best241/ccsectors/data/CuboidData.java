// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.data;

import org.bukkit.Material;
import pl.best241.ccguilds.manager.GuildManager;
import org.bukkit.World;
import org.bukkit.Location;
import java.util.Random;

public class CuboidData
{
    private final int maxX;
    private final int maxZ;
    private final int minX;
    private final int minZ;
    private static final Random random;
    
    public CuboidData(final int x1, final int x2, final int z1, final int z2) {
        System.out.println(x1 + " " + x2 + " " + z1 + " " + z2);
        this.maxX = Math.max(x1, x2);
        this.minX = Math.min(x1, x2);
        this.maxZ = Math.max(z1, z2);
        this.minZ = Math.min(z1, z2);
    }
    
    public boolean isOnCuboid(final Location loc) {
        return this.isOnCuboid(loc, 0);
    }
    
    public boolean isOnCuboid(final double x, final double z) {
        return this.isOnCuboid(x, z, 0);
    }
    
    public boolean isOnCuboid(final Location loc, final int modifier) {
        return loc.getBlockX() <= this.maxX + modifier && loc.getBlockZ() <= this.maxZ + modifier && loc.getBlockX() >= this.minX - modifier && loc.getBlockZ() >= this.minZ - modifier;
    }
    
    public boolean isOnCuboid(final double x, final double z, final int modifier) {
        return x <= this.maxX + modifier && z <= this.maxZ + modifier && x >= this.minX - modifier && z >= this.minZ - modifier;
    }
    
    public int getMaxX() {
        return this.maxX;
    }
    
    public int getMinX() {
        return this.minX;
    }
    
    public int getMaxZ() {
        return this.maxZ;
    }
    
    public int getMinZ() {
        return this.minZ;
    }
    
    public Location randomLocationInCuboid(final World world) {
        final int tries = 15;
        Location lastLoc = null;
        for (int i = 0; i < tries; ++i) {
            final int randomX = this.getRandom(this.getMinX(), this.getMaxX());
            final int randomZ = this.getRandom(this.getMinZ(), this.getMaxZ());
            final int y = world.getHighestBlockYAt(randomX, randomZ);
            final Location loc = lastLoc = new Location(world, (double)randomX, (double)y, (double)randomZ);
            if (!lastLoc.getBlock().isLiquid() && GuildManager.getGuildDataByBlock(lastLoc.getBlock()) == null && lastLoc.getBlock().getType() != Material.LEAVES) {
                break;
            }
        }
        return lastLoc;
    }
    
    private int getRandom(final int lower, final int upper) {
        return CuboidData.random.nextInt(upper - lower + 1) + lower;
    }
    
    static {
        random = new Random();
    }
}
