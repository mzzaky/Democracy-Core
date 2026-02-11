package id.democracycore.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import id.democracycore.DemocracyCore;
import id.democracycore.models.ArenaSession;
import id.democracycore.models.Government;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ChatListener implements Listener {
    
    private final DemocracyCore plugin;
    
    public ChatListener(DemocracyCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Government gov = plugin.getDataManager().getGovernment();
        
        // Determine prefix based on government position
        Component prefix = getGovernmentPrefix(uuid, gov);
        
        if (prefix != null) {
            // Modify the renderer to include prefix
            event.renderer((source, sourceDisplayName, message, viewer) -> {
                return prefix
                    .append(Component.space())
                    .append(sourceDisplayName)
                    .append(Component.text(": ", NamedTextColor.GRAY))
                    .append(message);
            });
        }
    }
    
    private Component getGovernmentPrefix(UUID uuid, Government gov) {
        // Check if president
        if (gov.hasPresident() && gov.getPresidentUUID().equals(uuid)) {
            return Component.text("👑", NamedTextColor.GOLD)
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text("Presiden", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text("]", NamedTextColor.GRAY));
        }
        
        // Check cabinet positions
        for (Government.CabinetPosition pos : Government.CabinetPosition.values()) {
            UUID minister = gov.getCabinetMember(pos);
            if (minister != null && minister.equals(uuid)) {
                return getCabinetPrefix(pos);
            }
        }
        
        // Check if in arena
        if (plugin.getArenaManager().isInArena(uuid)) {
            ArenaSession session = plugin.getArenaManager().getCurrentSession();
            int kills = 0;
            if (session != null) {
                ArenaSession.ArenaStats stats = session.getPlayerStats().get(uuid);
                if (stats != null) {
                    kills = stats.getKills();
                }
            }
            if (kills >= 10) {
                return Component.text("⚔", NamedTextColor.RED)
                    .append(Component.text("[", NamedTextColor.GRAY))
                    .append(Component.text("Arena Champion", NamedTextColor.RED))
                    .append(Component.text("]", NamedTextColor.GRAY));
            } else if (kills >= 5) {
                return Component.text("⚔", NamedTextColor.YELLOW)
                    .append(Component.text("[", NamedTextColor.GRAY))
                    .append(Component.text("Arena Fighter", NamedTextColor.YELLOW))
                    .append(Component.text("]", NamedTextColor.GRAY));
            }
        }
        
        return null;
    }
    
    private Component getCabinetPrefix(Government.CabinetPosition position) {
        NamedTextColor color;
        String icon;
        
        switch (position) {
            case DEFENSE -> {
                color = NamedTextColor.RED;
                icon = "🛡";
            }
            case TREASURY -> {
                color = NamedTextColor.GOLD;
                icon = "💰";
            }
            case COMMERCE -> {
                color = NamedTextColor.GREEN;
                icon = "📦";
            }
            case INFRASTRUCTURE -> {
                color = NamedTextColor.GRAY;
                icon = "🏗";
            }
            case CULTURE -> {
                color = NamedTextColor.LIGHT_PURPLE;
                icon = "🎭";
            }
            default -> {
                color = NamedTextColor.WHITE;
                icon = "📋";
            }
        }
        
        return Component.text(icon, color)
            .append(Component.text("[", NamedTextColor.GRAY))
            .append(Component.text(position.getDisplayName(), color))
            .append(Component.text("]", NamedTextColor.GRAY));
    }
}
