// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.data;

import org.bukkit.Location;
import pl.best241.ccsectors.managers.TeleportManager;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import java.util.Collection;
import org.bukkit.inventory.Inventory;
import java.math.BigDecimal;
import pl.best241.ccsectors.parser.InventorySerializer;
import pl.best241.ccsectors.api.TeleportLocation;
import org.bukkit.entity.Player;
import java.util.UUID;

public final class PlayerData
{
    private final UUID uuid;
    private String parsedInventory;
    private ArmorData armor;
    private String parsedEnderchest;
    private String parsedPotions;
    private final int hunger;
    private final double health;
    private final int exp;
    private final int level;
    private final String gamemode;
    private final boolean allowFlight;
    private final boolean isFlying;
    private final float walkSpeed;
    private final float flySpeed;
    private String dimSwitch;
    private String loc;
    private String safeTeleportType;
    private String lastSector;
    private boolean needSave;
    
    public PlayerData(final Player player, final DimSwitch dimSwitch, final SafeTeleportType type) {
        this(player, dimSwitch, new TeleportLocation(player.getLocation()), type);
    }
    
    public PlayerData(final Player player, final DimSwitch dimSwitch, final TeleportLocation loc, final SafeTeleportType type) {
        this.uuid = player.getUniqueId();
        this.updatePlayerInventory(player);
        this.parsedPotions = InventorySerializer.serializePotionEffects(player);
        this.hunger = player.getFoodLevel();
        this.health = player.getHealth();
        this.exp = player.getTotalExperience();
        this.level = player.getLevel();
        this.gamemode = player.getGameMode().toString();
        this.allowFlight = player.getAllowFlight();
        this.isFlying = player.isFlying();
        this.walkSpeed = player.getWalkSpeed();
        this.flySpeed = player.getFlySpeed();
        this.dimSwitch = dimSwitch.toString();
        this.safeTeleportType = type.toString();
        this.loc = InventorySerializer.serializeLocation(loc);
    }
    
    public PlayerData(final Player player, final DimSwitch dimSwitch, final String serializedLoc, final SafeTeleportType type) {
        this.uuid = player.getUniqueId();
        this.updatePlayerInventory(player);
        this.parsedPotions = InventorySerializer.serializePotionEffects(player);
        this.hunger = player.getFoodLevel();
        this.health = player.getHealth();
        this.exp = this.getTotalExperience(player);
        this.level = player.getLevel();
        this.gamemode = player.getGameMode().toString();
        this.allowFlight = player.getAllowFlight();
        this.isFlying = player.isFlying();
        this.walkSpeed = player.getWalkSpeed();
        this.flySpeed = player.getFlySpeed();
        this.dimSwitch = dimSwitch.toString();
        this.safeTeleportType = type.toString();
        this.loc = serializedLoc;
    }
    
    public int getTotalExperience(final Player player) {
        int experience = 0;
        final int level = player.getLevel();
        if (level >= 0 && level <= 15) {
            experience = (int)Math.ceil(Math.pow(level, 2.0) + 6 * level);
            final int requiredExperience = 2 * level + 7;
            final double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += (int)Math.ceil(currentExp * requiredExperience);
            return experience;
        }
        if (level > 15 && level <= 30) {
            experience = (int)Math.ceil(2.5 * Math.pow(level, 2.0) - 40.5 * level + 360.0);
            final int requiredExperience = 5 * level - 38;
            final double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += (int)Math.ceil(currentExp * requiredExperience);
            return experience;
        }
        experience = (int)Math.ceil(4.5 * Math.pow(level, 2.0) - 162.5 * level + 2220.0);
        final int requiredExperience = 9 * level - 158;
        final double currentExp = Double.parseDouble(Float.toString(player.getExp()));
        experience += (int)Math.ceil(currentExp * requiredExperience);
        return experience;
    }
    
    public void setTotalExperience(final int xp, final Player player) {
        if (xp >= 0 && xp < 351) {
            final int a = 1;
            final int b = 6;
            final int c = -xp;
            final int level = (int)(-b + Math.sqrt(Math.pow(b, 2.0) - 4 * a * c)) / (2 * a);
            final int xpForLevel = (int)(Math.pow(level, 2.0) + 6 * level);
            final int remainder = xp - xpForLevel;
            final int experienceNeeded = 2 * level + 7;
            float experience = remainder / experienceNeeded;
            experience = this.round(experience, 2);
            System.out.println("xpForLevel: " + xpForLevel);
            System.out.println(experience);
            player.setLevel(level);
            player.setExp(experience);
        }
        else if (xp >= 352 && xp < 1507) {
            final double a2 = 2.5;
            final double b2 = -40.5;
            final int c2 = -xp + 360;
            final double dLevel = (-b2 + Math.sqrt(Math.pow(b2, 2.0) - 4.0 * a2 * c2)) / (2.0 * a2);
            final int level2 = (int)Math.floor(dLevel);
            final int xpForLevel2 = (int)(2.5 * Math.pow(level2, 2.0) - 40.5 * level2 + 360.0);
            final int remainder2 = xp - xpForLevel2;
            final int experienceNeeded2 = 5 * level2 - 38;
            float experience2 = remainder2 / experienceNeeded2;
            experience2 = this.round(experience2, 2);
            System.out.println("xpForLevel: " + xpForLevel2);
            System.out.println(experience2);
            player.setLevel(level2);
            player.setExp(experience2);
        }
        else {
            final double a2 = 4.5;
            final double b2 = -162.5;
            final int c2 = -xp + 2220;
            final double dLevel = (-b2 + Math.sqrt(Math.pow(b2, 2.0) - 4.0 * a2 * c2)) / (2.0 * a2);
            final int level2 = (int)Math.floor(dLevel);
            final int xpForLevel2 = (int)(4.5 * Math.pow(level2, 2.0) - 162.5 * level2 + 2220.0);
            final int remainder2 = xp - xpForLevel2;
            final int experienceNeeded2 = 9 * level2 - 158;
            float experience2 = remainder2 / experienceNeeded2;
            experience2 = this.round(experience2, 2);
            System.out.println("xpForLevel: " + xpForLevel2);
            System.out.println(experience2);
            player.setLevel(level2);
            player.setExp(experience2);
        }
    }
    
    private float round(final float d, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, 5);
        return bd.floatValue();
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public void updatePlayerInventory(final Player player) {
        this.parsedInventory = InventorySerializer.serializeInventory(player, "inventory");
        this.armor = new ArmorData(player);
        this.parsedEnderchest = InventorySerializer.serializeInventory(player, "enderchest");
    }
    
    public void applyGamemode(final Player player) {
        player.setGameMode(this.getGameMode());
    }
    
    public void updateInventory(final Inventory inventory) {
        this.parsedInventory = InventorySerializer.serializeInventory(inventory);
    }
    
    public void updateEnderchest(final Inventory inventory) {
        this.parsedEnderchest = InventorySerializer.serializeInventory(inventory);
    }
    
    public Inventory getInventory() {
        return InventorySerializer.deserializeInventory(this.parsedInventory);
    }
    
    public Inventory getEnderchest() {
        return InventorySerializer.deserializeInventory(this.parsedEnderchest);
    }
    
    public ArmorData getArmor() {
        return this.armor;
    }
    
    public Collection<PotionEffect> getPotionEffects(final Player player) {
        return InventorySerializer.deserializePotionEffects(this.parsedPotions, player);
    }
    
    public void updatePotionEffects(final Player player) {
        this.parsedPotions = InventorySerializer.serializePotionEffects(player);
    }
    
    public int getHunger() {
        return this.hunger;
    }
    
    public double getHealth() {
        return this.health;
    }
    
    public int getExp() {
        return this.exp;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public GameMode getGameMode() {
        return GameMode.valueOf(this.gamemode);
    }
    
    public boolean getAllowFlight() {
        return this.allowFlight;
    }
    
    public boolean getIsFlying() {
        return this.isFlying;
    }
    
    public float getWalkSpeed() {
        return this.walkSpeed;
    }
    
    public float getFlySpeed() {
        return this.flySpeed;
    }
    
    public DimSwitch getDimSwitch() {
        return DimSwitch.valueOf(this.dimSwitch);
    }
    
    public void setDimSwitch(final DimSwitch dimSwitch) {
        this.dimSwitch = dimSwitch.toString();
    }
    
    public TeleportLocation getLocation() {
        return InventorySerializer.deserializeLocation(this.loc);
    }
    
    public TeleportLocation getTeleportLocation() {
        final TeleportLocation teleportLoc = TeleportManager.getSafeLocation(TeleportManager.getDimSwitchLocation(this.getLocation(), this.getDimSwitch()), this.getSafeTeleportType());
        return teleportLoc;
    }
    
    public void setLocation(final TeleportLocation loc) {
        this.loc = InventorySerializer.serializeLocation(loc);
    }
    
    public void setSerializedLocation(final SectorType sectorType, final double x, final double y, final double z, final float yaw, final float pitch) {
        this.loc = InventorySerializer.serializeLocation(sectorType.toString(), x, y, z, yaw, pitch);
    }
    
    public SafeTeleportType getSafeTeleportType() {
        if (this.safeTeleportType == null) {
            return SafeTeleportType.SAFE;
        }
        return SafeTeleportType.valueOf(this.safeTeleportType);
    }
    
    public void setSafeTeleprotType(final SafeTeleportType type) {
        this.safeTeleportType = type.toString();
    }
    
    public void applyValues(final Player player) {
        player.getActivePotionEffects().clear();
        player.getActivePotionEffects().addAll(this.getPotionEffects(player));
        player.setFoodLevel(this.getHunger());
        this.setTotalExperience(this.getExp(), player);
        player.setAllowFlight(this.getAllowFlight());
        player.setFlying(this.getIsFlying());
        player.setWalkSpeed(this.getWalkSpeed());
        player.setFlySpeed(this.getFlySpeed());
        this.armor.applyValues(player);
    }
    
    public void applyHealth(final Player player) {
        if (player.getHealth() != this.getHealth()) {
            player.setHealth(this.getHealth());
        }
    }
    
    public void applyInventory(final Player player) {
        final Inventory inventory = this.getInventory();
        if (inventory.getContents().length <= 36) {
            player.getInventory().setContents(inventory.getContents());
        }
        else {
            System.out.println(player.getName() + " failed inventory load... Size:" + inventory.getContents().length);
        }
        final Inventory enderchest = this.getEnderchest();
        if (enderchest.getContents().length <= 27) {
            player.getEnderChest().setContents(enderchest.getContents());
        }
        else {
            System.out.println(player.getName() + " failed inventory load... Size:" + enderchest.getContents().length);
        }
        this.getArmor().applyValues(player);
    }
    
    public TeleportLocation teleport(final Player player) {
        if (this.safeTeleportType == null) {
            this.safeTeleportType = SafeTeleportType.SAFE.toString();
        }
        final TeleportLocation teleportLoc = this.getTeleportLocation();
        System.out.println(this.dimSwitch + " " + this.safeTeleportType + " " + teleportLoc);
        final Location locTp = teleportLoc.toLocation(player.getWorld());
        player.teleport(locTp);
        System.out.println("Teleported " + player.getName() + " to " + locTp);
        this.safeTeleportType = SafeTeleportType.SAFE.toString();
        this.dimSwitch = DimSwitch.SAME.toString();
        this.setLocation(teleportLoc);
        return teleportLoc;
    }
    
    public void clearTeleportOptions() {
        this.setDimSwitch(DimSwitch.SAME);
        this.setSafeTeleprotType(SafeTeleportType.SAFE);
    }
    
    public void setLastSector(final String name) {
        this.lastSector = name;
    }
    
    public String getLastSector() {
        return this.lastSector;
    }
    
    public boolean isNeedingSave() {
        return this.needSave;
    }
    
    public void setNeedingSave(final boolean value) {
        this.needSave = value;
    }
    
    public String getSerializedLocation() {
        return this.loc;
    }
    
    public void setParsedInventory(final String inv) {
        this.parsedInventory = inv;
    }
    
    public void setParsedEnderchest(final String ender) {
        this.parsedEnderchest = ender;
    }
    
    public void setParsedArmor(final String helmet, final String chest, final String leggings, final String boots) {
        this.armor.setParsedHelmet(helmet);
        this.armor.setParsedChestplate(chest);
        this.armor.setParsedLeggins(leggings);
        this.armor.setParsedBoots(boots);
    }
}
