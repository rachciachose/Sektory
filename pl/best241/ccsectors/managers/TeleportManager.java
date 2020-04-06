// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.managers;

import org.bukkit.plugin.Plugin;
import pl.best241.ccsectors.CcSectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.best241.ccsectors.api.CcSectorsAPI;
import pl.best241.ccsectors.data.DimSwitch;
import org.bukkit.World;
import org.bukkit.block.Block;
import pl.best241.ccsectors.data.SectorType;
import pl.best241.ccsectors.data.SafeTeleportType;
import pl.best241.ccsectors.api.TeleportLocation;
import org.bukkit.Material;

public class TeleportManager
{
    private static Material[] safeBlocks;
    
    public static boolean isBlockSafe(final Material material) {
        for (final Material safeBlock : TeleportManager.safeBlocks) {
            if (material == safeBlock) {
                return true;
            }
        }
        return false;
    }
    
    public static TeleportLocation getSafeLocation(final TeleportLocation loc, final SafeTeleportType safeType) {
        final TeleportLocation tempLoc = new TeleportLocation(loc.getSectorType(), loc.getX(), loc.getY(), loc.getZ());
        if (tempLoc.getX() < 0.0) {
            tempLoc.setX(tempLoc.getX() - 1.0);
        }
        if (tempLoc.getZ() < 0.0) {
            tempLoc.setZ(tempLoc.getZ() - 1.0);
        }
        System.out.println(loc + " " + safeType);
        if (safeType == SafeTeleportType.BOTTOM) {
            for (int y = 1; y < tempLoc.getWorld().getMaxHeight(); ++y) {
                final Block foodBlock = tempLoc.getWorld().getBlockAt(tempLoc.getBlockX(), y, tempLoc.getBlockZ());
                final Block headBlock = tempLoc.getWorld().getBlockAt(tempLoc.getBlockX(), y + 1, tempLoc.getBlockZ());
                if ((foodBlock.getType() == Material.AIR || isBlockSafe(foodBlock.getType())) && (headBlock.getType() == Material.AIR || isBlockSafe(headBlock.getType()))) {
                    final TeleportLocation safeLoc = new TeleportLocation(loc.getSectorType(), loc.getX(), y, loc.getZ(), loc.getYaw(), loc.getPitch());
                    return safeLoc;
                }
            }
            return getSafeLocation(loc, SafeTeleportType.TOP);
        }
        if (safeType == SafeTeleportType.TOP) {
            for (int y = tempLoc.getWorld().getMaxHeight(); y >= 1; --y) {
                final Block standBlock = tempLoc.getWorld().getBlockAt(tempLoc.getBlockX(), y - 1, tempLoc.getBlockZ());
                final Block foodBlock2 = tempLoc.getWorld().getBlockAt(tempLoc.getBlockX(), y, tempLoc.getBlockZ());
                final Block headBlock2 = tempLoc.getWorld().getBlockAt(tempLoc.getBlockX(), y + 1, tempLoc.getBlockZ());
                if (standBlock.getType() != Material.AIR && (SectorManager.getCurrentSectorPart().getSectorType() != SectorType.NETHER || standBlock.getType() != Material.BEDROCK) && (foodBlock2.getType() == Material.AIR || isBlockSafe(foodBlock2.getType())) && (headBlock2.getType() == Material.AIR || isBlockSafe(headBlock2.getType()))) {
                    System.out.println("Finding top safe loc at Y: " + y);
                    final TeleportLocation safeLoc2 = new TeleportLocation(loc.getSectorType(), loc.getX(), y, loc.getZ(), loc.getYaw(), loc.getPitch());
                    return safeLoc2;
                }
            }
        }
        else if (safeType == SafeTeleportType.SAFE) {
            final World world = tempLoc.getWorld();
            Block standBlock = world.getBlockAt(tempLoc.getBlockX(), tempLoc.getBlockY() - 1, tempLoc.getBlockZ());
            System.out.println("Stand block: " + standBlock);
            Block foodBlock2 = world.getBlockAt(tempLoc.getBlockX(), tempLoc.getBlockY(), tempLoc.getBlockZ());
            Block headBlock2 = world.getBlockAt(tempLoc.getBlockX(), tempLoc.getBlockY() + 1, tempLoc.getBlockZ());
            if (standBlock.getType() != Material.AIR && (SectorManager.getCurrentSectorPart().getSectorType() != SectorType.NETHER || standBlock.getType() != Material.BEDROCK) && (foodBlock2.getType() == Material.AIR || foodBlock2.isLiquid() || isBlockSafe(foodBlock2.getType())) && (headBlock2.getType() == Material.AIR || headBlock2.isLiquid() || isBlockSafe(headBlock2.getType()))) {
                return loc;
            }
            for (int y2 = tempLoc.getBlockY() - 2; y2 < standBlock.getWorld().getMaxHeight(); ++y2) {
                System.out.println("Finding safe loc at Y: " + y2);
                standBlock = tempLoc.getWorld().getBlockAt(tempLoc.getBlockX(), y2 - 1, tempLoc.getBlockZ());
                foodBlock2 = tempLoc.getWorld().getBlockAt(tempLoc.getBlockX(), y2, tempLoc.getBlockZ());
                headBlock2 = tempLoc.getWorld().getBlockAt(tempLoc.getBlockX(), y2 + 1, tempLoc.getBlockZ());
                if (standBlock.getType() != Material.AIR && (SectorManager.getCurrentSectorPart().getSectorType() != SectorType.NETHER || standBlock.getType() != Material.BEDROCK) && (foodBlock2.getType() == Material.AIR || foodBlock2.isLiquid() || isBlockSafe(foodBlock2.getType())) && (headBlock2.getType() == Material.AIR || headBlock2.isLiquid() || isBlockSafe(headBlock2.getType()))) {
                    final TeleportLocation safeLoc3 = new TeleportLocation(loc.getSectorType(), loc.getX(), y2, loc.getZ(), loc.getYaw(), loc.getPitch());
                    return safeLoc3;
                }
            }
            System.out.println("Not found, founding highest");
            return getSafeLocation(loc, SafeTeleportType.TOP);
        }
        return loc;
    }
    
    public static TeleportLocation getDimSwitchLocation(final TeleportLocation loc, final DimSwitch dimSwitch) {
        if (dimSwitch == DimSwitch.SAME) {
            return loc;
        }
        if (dimSwitch == DimSwitch.NETHER) {
            final TeleportLocation returnLoc = loc.clone();
            returnLoc.setX(loc.getX() / CcSectorsAPI.getNetherXZFactor());
            returnLoc.setZ(loc.getZ() / CcSectorsAPI.getNetherXZFactor());
            returnLoc.setY(loc.getY() / CcSectorsAPI.getNetherYFactor());
            System.out.println(returnLoc);
            return returnLoc;
        }
        if (dimSwitch == DimSwitch.NORMAL) {
            final TeleportLocation returnLoc = loc.clone();
            returnLoc.setX(loc.getX() * CcSectorsAPI.getNetherXZFactor());
            returnLoc.setZ(loc.getZ() * CcSectorsAPI.getNetherXZFactor());
            returnLoc.setY(loc.getY() * CcSectorsAPI.getNetherYFactor());
            return returnLoc;
        }
        return loc;
    }
    
    public static void teleportLaterToCorrectLocation(final Player player, final TeleportLocation correctLoc) {
        Bukkit.getScheduler().runTaskLater((Plugin)CcSectors.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                player.teleport(correctLoc.toLocation(player.getWorld()));
                if (player.getLocation().equals((Object)correctLoc)) {
                    return;
                }
                System.out.println("Teleporting to " + correctLoc);
            }
        }, 1L);
    }
    
    static {
        TeleportManager.safeBlocks = new Material[] { Material.LONG_GRASS, Material.ACTIVATOR_RAIL, Material.BROWN_MUSHROOM, Material.CARPET, Material.COCOA, Material.CROPS, Material.DETECTOR_RAIL, Material.DIODE, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.DOUBLE_PLANT, Material.LEASH, Material.YELLOW_FLOWER, Material.WEB, Material.VINE, Material.TRIPWIRE, Material.SUGAR_CANE_BLOCK, Material.SUGAR_CANE, Material.STRING, Material.SNOW, Material.SIGN_POST, Material.SIGN, Material.SAPLING, Material.REDSTONE, Material.REDSTONE_WIRE, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON, Material.REDSTONE_COMPARATOR, Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.RAILS, Material.RED_ROSE, Material.RED_MUSHROOM, Material.POWERED_RAIL };
    }
}
