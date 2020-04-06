// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccsectors.messages;

import org.bukkit.plugin.Plugin;

public class MessagesData
{
    public static String welcomeMessagePremium;
    public static String welcomeMessageNoPremium;
    private static MessagesConfig config;
    
    public static void loadMessages(final Plugin plugin) {
        (MessagesData.config = new MessagesConfig(plugin, "messages.yml")).saveDefaultConfig();
        MessagesData.config.reloadCustomConfig();
        MessagesData.welcomeMessagePremium = MessagesData.config.getString("welcomeMessagePremium");
        MessagesData.welcomeMessageNoPremium = MessagesData.config.getString("welcomeMessageNoPremium");
    }
}
