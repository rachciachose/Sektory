// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.data;

import pl.best241.ccsectors.parser.InventorySerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public final class ArmorData
{
    private String parsedHelmet;
    private String parsedChestplate;
    private String parsedLeggins;
    private String parsedBoots;
    
    public ArmorData(final Player player) {
        this.setHelmet(player.getInventory().getHelmet());
        this.setChestplate(player.getInventory().getChestplate());
        this.setLeggins(player.getInventory().getLeggings());
        this.setBoots(player.getInventory().getBoots());
    }
    
    public void setParsedHelmet(final String helmet) {
        this.parsedHelmet = helmet;
    }
    
    public void setParsedChestplate(final String chestplate) {
        this.parsedChestplate = chestplate;
    }
    
    public void setParsedLeggins(final String parsedLeggins) {
        this.parsedLeggins = parsedLeggins;
    }
    
    public void setParsedBoots(final String parsedBoots) {
        this.parsedBoots = parsedBoots;
    }
    
    public ItemStack getHelmet() {
        return InventorySerializer.deserializeItemStack(this.parsedHelmet);
    }
    
    public void setHelmet(final ItemStack item) {
        this.parsedHelmet = InventorySerializer.serializeItemStack(item);
    }
    
    public ItemStack getChestplate() {
        return InventorySerializer.deserializeItemStack(this.parsedChestplate);
    }
    
    public void setChestplate(final ItemStack item) {
        this.parsedChestplate = InventorySerializer.serializeItemStack(item);
    }
    
    public ItemStack getLeggins() {
        return InventorySerializer.deserializeItemStack(this.parsedLeggins);
    }
    
    public void setLeggins(final ItemStack item) {
        this.parsedLeggins = InventorySerializer.serializeItemStack(item);
    }
    
    public ItemStack getBoots() {
        return InventorySerializer.deserializeItemStack(this.parsedBoots);
    }
    
    public void setBoots(final ItemStack item) {
        this.parsedBoots = InventorySerializer.serializeItemStack(item);
    }
    
    public void applyValues(final Player player) {
        if (this.getHelmet() != null) {
            player.getInventory().setHelmet(this.getHelmet());
        }
        if (this.getChestplate() != null) {
            player.getInventory().setChestplate(this.getChestplate());
        }
        if (this.getLeggins() != null) {
            player.getInventory().setLeggings(this.getLeggins());
        }
        if (this.getBoots() != null) {
            player.getInventory().setBoots(this.getBoots());
        }
    }
}
