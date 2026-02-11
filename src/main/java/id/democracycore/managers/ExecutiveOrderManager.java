package id.democracycore.managers;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import id.democracycore.DemocracyCore;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.ExecutiveOrder.ExecutiveOrderType;
import id.democracycore.models.PlayerData;
import id.democracycore.models.PresidentHistory.PresidentRecord;
import id.democracycore.models.Treasury.TransactionType;
import id.democracycore.utils.MessageUtils;

public class ExecutiveOrderManager {
    
    private final DemocracyCore plugin;
    
    public ExecutiveOrderManager(DemocracyCore plugin) {
        this.plugin = plugin;
    }
    
    public List<ExecutiveOrder> getActiveOrders() {
        return plugin.getDataManager().getActiveOrders();
    }
    
    public boolean isOrderActive(ExecutiveOrderType type) {
        return getActiveOrders().stream()
            .anyMatch(o -> o.getType() == type && o.isActive() && !o.isExpired());
    }

    public boolean isOrderOnCooldown(ExecutiveOrderType type) {
        long cooldownDays = plugin.getConfig().getLong("executive-orders.cooldown-days", 7);
        long cooldownMillis = cooldownDays * 24 * 60 * 60 * 1000;
        long lastOrderTime = plugin.getDataManager().getLastExecutiveOrderTime();
        return System.currentTimeMillis() - lastOrderTime < cooldownMillis;
    }

    public long getOrderCooldownRemaining(ExecutiveOrderType type) {
        long cooldownDays = plugin.getConfig().getLong("executive-orders.cooldown-days", 7);
        long cooldownMillis = cooldownDays * 24 * 60 * 60 * 1000;
        long lastOrderTime = plugin.getDataManager().getLastExecutiveOrderTime();
        long elapsed = System.currentTimeMillis() - lastOrderTime;
        return Math.max(0, cooldownMillis - elapsed);
    }
    
    public ExecutiveOrder getActiveOrder(ExecutiveOrderType type) {
        return getActiveOrders().stream()
            .filter(o -> o.getType() == type && o.isActive() && !o.isExpired())
            .findFirst()
            .orElse(null);
    }
    
    public boolean issueOrder(Player president, ExecutiveOrderType type) {
        UUID uuid = president.getUniqueId();
        
        // Check if president
        if (!plugin.getGovernmentManager().isPresident(uuid)) {
            MessageUtils.send(president, "executive_orders.only_president");
            return false;
        }
        
        // Check cooldown
        long cooldownDays = plugin.getConfig().getLong("executive-orders.cooldown-days", 7);
        long cooldownMillis = cooldownDays * 24 * 60 * 60 * 1000;
        long lastOrderTime = plugin.getDataManager().getLastExecutiveOrderTime();
        
        if (System.currentTimeMillis() - lastOrderTime < cooldownMillis) {
            long remaining = cooldownMillis - (System.currentTimeMillis() - lastOrderTime);
            MessageUtils.send(president, "executive_orders.cooldown", "time", MessageUtils.formatTime(remaining));
            return false;
        }
        
        // Check if order already active
        if (isOrderActive(type)) {
            MessageUtils.send(president, "executive_orders.already_active");
            return false;
        }
        
        // Check treasury
        double cost = plugin.getConfig().getDouble("executive-orders.cost", 1000000);
        if (!plugin.getTreasuryManager().canAfford(cost)) {
            MessageUtils.send(president, "executive_orders.insufficient_funds", "amount", plugin.getVaultHook().format(cost));
            return false;
        }
        
        // Withdraw from treasury
        plugin.getTreasuryManager().withdraw(TransactionType.EXECUTIVE_ORDER, cost,
            "Executive Order: " + type.getDisplayName(), uuid);
        
        // Create and activate order
        ExecutiveOrder order = new ExecutiveOrder(type, uuid, type.getDefaultDuration());
        getActiveOrders().add(order);
        plugin.getDataManager().setLastExecutiveOrderTime(System.currentTimeMillis());
        
        // Update history record
        PresidentRecord record = plugin.getDataManager().getPresidentHistory().getLatestRecord();
        if (record != null) {
            record.setExecutiveOrdersIssued(record.getExecutiveOrdersIssued() + 1);
        }
        
        // Apply effects
        applyOrderEffects(order);
        
        // Broadcast
        MessageUtils.broadcastAnnouncement("EXECUTIVE ORDER: " + type.getDisplayName(),
            "<italic><yellow>\"" + type.getFlavorText() + "\"</yellow></italic>\n\n" +
            "<gray>Effect: " + type.getEffectDescription() + "</gray>\n" +
            "<gray>Duration: " + MessageUtils.formatTimeShort(type.getDefaultDuration()) + "</gray>");
        
        MessageUtils.broadcastTitle("<gold>⚡ EXECUTIVE ORDER ⚡</gold>", 
            "<yellow>" + type.getDisplayName() + "</yellow>", 20, 100, 20);
        MessageUtils.broadcastSound(Sound.ENTITY_ENDER_DRAGON_GROWL);
        
        // Spawn lightning effect at president's location for dramatic effect
        president.getWorld().strikeLightningEffect(president.getLocation());
        
        return true;
    }
    
    private void applyOrderEffects(ExecutiveOrder order) {
        switch (order.getType()) {
            case GOLDEN_AGE -> {
                // Buffs applied through buff manager
            }
            case STATE_OF_EMERGENCY -> {
                MessageUtils.broadcast("<yellow>PvP has been disabled server-wide!</yellow>");
            }
            case NATIONAL_HOLIDAY -> {
                // Clear cooldowns handled elsewhere
            }
            case WAR_ECONOMY -> {
                MessageUtils.broadcast("<red>War Economy is now active! PvP rewards doubled!</red>");
            }
            case ECONOMIC_RECOVERY -> {
                // Give stimulus to all online players
                double stimulus = 50000;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData data = plugin.getDataManager().getOrCreatePlayerData(
                        player.getUniqueId(), player.getName());
                    if (!data.isClaimedStimulus()) {
                        plugin.getVaultHook().deposit(player.getUniqueId(), stimulus);
                        data.setClaimedStimulus(true);
                        MessageUtils.send(player, "managers.executive_order.stimulus_received", "amount", plugin.getVaultHook().format(stimulus));
                    }
                }
            }
            case INFRASTRUCTURE_INITIATIVE -> {
                // Apply Haste II to all players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(new PotionEffect(
                        PotionEffectType.HASTE, Integer.MAX_VALUE, 1, false, false));
                }
            }
            case ENVIRONMENTAL_PROTECTION -> {
                // Multipliers handled in listeners
            }
            case EDUCATION_ADVANCEMENT -> {
                // Multipliers handled in listeners
            }
            case PURGE_PROTOCOL -> {
                MessageUtils.broadcastTitle("<dark_red>⚠️ PURGE ACTIVE ⚠️</dark_red>", 
                    "<red>Full PvP enabled everywhere!</red>", 20, 100, 20);
                MessageUtils.broadcastSound(Sound.ENTITY_WITHER_SPAWN);
            }
            case PRESIDENTIAL_PARDON -> {
                // Clear one punishment from all online players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
                    if (data != null && !data.getPunishments().isEmpty()) {
                        data.getPunishments().remove(data.getPunishments().size() - 1);
                        MessageUtils.send(player, "managers.executive_order.punishment_cleared");
                    }
                }
            }
        }
    }
    
    public void checkExpirations() {
        List<ExecutiveOrder> orders = getActiveOrders();
        orders.removeIf(order -> {
            if (order.isExpired() && order.isActive()) {
                order.setActive(false);
                expireOrder(order);
                return true;
            }
            return false;
        });
    }
    
    private void expireOrder(ExecutiveOrder order) {
        MessageUtils.broadcast("<gray>Executive Order <yellow>" + order.getType().getDisplayName() + 
            "</yellow> has expired.</gray>");
        
        // Remove effects
        switch (order.getType()) {
            case INFRASTRUCTURE_INITIATIVE -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.removePotionEffect(PotionEffectType.HASTE);
                }
            }
            case PURGE_PROTOCOL -> {
                MessageUtils.broadcast("<green>The Purge has ended. Normal rules restored.</green>");
            }
            default -> {}
        }
    }
    
    // Effect getters for listeners
    public double getXPMultiplier() {
        double multiplier = 1.0;
        if (isOrderActive(ExecutiveOrderType.GOLDEN_AGE)) {
            multiplier *= 1.25;
        }
        if (isOrderActive(ExecutiveOrderType.EDUCATION_ADVANCEMENT)) {
            multiplier *= 3.0;
        }
        return multiplier;
    }
    
    public double getVaultMultiplier() {
        double multiplier = 1.0;
        if (isOrderActive(ExecutiveOrderType.GOLDEN_AGE)) {
            multiplier *= 1.25;
        }
        return multiplier;
    }
    
    public double getRareDropMultiplier() {
        double multiplier = 1.0;
        if (isOrderActive(ExecutiveOrderType.GOLDEN_AGE)) {
            multiplier *= 1.15;
        }
        return multiplier;
    }
    
    public double getFarmingMultiplier() {
        if (isOrderActive(ExecutiveOrderType.ENVIRONMENTAL_PROTECTION)) {
            return 3.0;
        }
        return 1.0;
    }
    
    public boolean isPvPDisabled() {
        return isOrderActive(ExecutiveOrderType.STATE_OF_EMERGENCY);
    }
    
    public boolean isPurgeActive() {
        return isOrderActive(ExecutiveOrderType.PURGE_PROTOCOL);
    }
    
    public double getPvPDamageMultiplier() {
        if (isOrderActive(ExecutiveOrderType.WAR_ECONOMY)) {
            return 1.5;
        }
        return 1.0;
    }
    
    public double getShopDiscount() {
        if (isOrderActive(ExecutiveOrderType.ECONOMIC_RECOVERY)) {
            return 0.25; // 25% discount
        }
        return 0.0;
    }

    public boolean stopOrder(ExecutiveOrderType type) {
        ExecutiveOrder order = getActiveOrder(type);
        if (order == null) {
            return false;
        }

        order.setActive(false);
        expireOrder(order);
        getActiveOrders().remove(order);
        return true;
    }
}
