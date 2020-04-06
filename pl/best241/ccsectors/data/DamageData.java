// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.data;

import org.bukkit.entity.Entity;

public class DamageData
{
    public long time;
    public Entity damager;
    
    public DamageData(final Entity damager) {
        this.time = System.currentTimeMillis();
        this.damager = damager;
    }
}
