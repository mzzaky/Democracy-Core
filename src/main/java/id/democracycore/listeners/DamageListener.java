package id.democracycore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import id.democracycore.DemocracyCore;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.Government;

public class DamageListener implements Listener {
    
    private final DemocracyCore plugin;
    
    public DamageListener(DemocracyCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Check for PvP
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            
            // Check State of Emergency - PvP disabled
            if (plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.STATE_OF_EMERGENCY)) {
                // Disable PvP unless in arena
                if (!plugin.getArenaManager().isInArena(attacker.getUniqueId()) ||
                    !plugin.getArenaManager().isInArena(victim.getUniqueId())) {
                    event.setCancelled(true);
                    return;
                }
            }
            
            // Modify damage for War Economy
            if (plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.WAR_ECONOMY)) {
                // +50% PvP damage
                event.setDamage(event.getDamage() * 1.5);
            }
            
            // Modify damage for Purge Protocol
            if (plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.PURGE_PROTOCOL)) {
                // Full damage, no protection
                event.setDamage(event.getDamage() * 1.25);
            }
            
            // President damage bonus (additional on top of attribute modifiers)
            Government gov = plugin.getDataManager().getGovernment();
            if (gov.hasPresident() && gov.getPresidentUUID().equals(attacker.getUniqueId())) {
                // Vault bonus damage is handled through economy events
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        
        // Check if in arena - arena has its own damage handling
        if (plugin.getArenaManager().isInArena(player.getUniqueId())) {
            return;
        }
        
        // President defense bonus (additional on top of attribute modifiers)
        Government gov = plugin.getDataManager().getGovernment();
        if (gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId())) {
            // Additional 5% damage reduction beyond attribute modifiers
            double reduction = plugin.getConfig().getDouble("president.buffs.defense-bonus", 0.12);
            event.setDamage(event.getDamage() * (1.0 - (reduction * 0.5))); // Half of the bonus as additional reduction
        }
        
        // Cabinet defense minister gets extra protection
        Government.CabinetPosition position = plugin.getBuffManager().getCabinetPosition(player.getUniqueId());
        if (position == Government.CabinetPosition.DEFENSE) {
            event.setDamage(event.getDamage() * 0.92); // 8% additional reduction
        }
    }
}
