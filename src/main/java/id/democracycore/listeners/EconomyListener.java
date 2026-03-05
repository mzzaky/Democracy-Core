package id.democracycore.listeners;

import id.democracycore.DemocracyCore;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.Government;
import id.democracycore.models.Treasury;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This listener would integrate with economy plugins to handle:
 * - President vault bonus
 * - Cabinet treasury bonus
 * - Tax collection
 * - Economic executive orders
 * 
 * Note: Actual integration depends on the economy plugin being used.
 * This provides the logic framework for when economy events are available.
 */
public class EconomyListener implements Listener {

    private final DemocracyCore plugin;
    private final Map<UUID, Long> lastBonusTime = new HashMap<>();

    public EconomyListener(DemocracyCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Calculate and apply vault bonus for a player
     * Called when player receives money through various means
     */
    public double applyVaultBonus(Player player, double amount) {
        if (amount <= 0)
            return amount;

        Government gov = plugin.getDataManager().getGovernment();
        double bonus = 0;

        // President vault bonus
        if (gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId())) {
            bonus += (plugin.getConfig().getDouble("president.buffs.vault-multiplier", 1.20) - 1.0);
        }

        // Treasury minister bonus
        UUID treasuryMinister = gov.getCabinetMember(Government.CabinetPosition.TREASURY);
        if (treasuryMinister != null && treasuryMinister.equals(player.getUniqueId())) {
            bonus += (plugin.getConfig().getDouble("cabinet.treasury-minister.vault-multiplier", 1.25) - 1.0);
        }

        // Golden Age executive order
        if (plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.GOLDEN_AGE)) {
            bonus += 0.25;
        }

        // Economic Recovery executive order
        if (plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.ECONOMIC_RECOVERY)) {
            bonus += 0.10;
        }

        return amount * (1.0 + bonus);
    }

    /**
     * Calculate tax amount for a transaction
     */
    public double calculateTax(Player player, double amount) {
        // Check for tax holiday (cabinet decision)
        if (plugin.getCabinetManager().isTaxHolidayActive()) {
            return 0;
        }

        double taxRate = plugin.getConfig().getDouble("treasury.tax-rate", 0.05);
        return amount * taxRate;
    }

    /**
     * Process a transaction with tax
     */
    public void processTransaction(Player from, Player to, double amount, String reason) {
        double tax = calculateTax(from, amount);
        double netAmount = amount - tax;

        // Collect tax to treasury
        if (tax > 0) {
            plugin.getTreasuryManager().deposit(Treasury.TransactionType.TAX_INCOME, tax, "Transaction tax: " + reason,
                    null);
        }

        // The actual transfer would be handled by the economy plugin
        // This is for tracking purposes
    }

    /**
     * Get shop discount for a player
     */
    public double getShopDiscount(Player player) {
        double discount = 0;

        // Cabinet decision discounts
        discount = Math.max(discount, 1.0 - plugin.getCabinetManager().getShopDiscount(player.getUniqueId()));

        // Commerce minister gets base discount
        Government gov = plugin.getDataManager().getGovernment();
        UUID commerceMinister = gov.getCabinetMember(Government.CabinetPosition.COMMERCE);
        if (commerceMinister != null && commerceMinister.equals(player.getUniqueId())) {
            discount = Math.max(discount, 0.10);
        }

        // Economic Recovery order
        if (plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.ECONOMIC_RECOVERY)) {
            discount = Math.max(discount, 0.25);
        }

        return discount;
    }

    /**
     * Check if player should receive double rewards (War Economy)
     */
    public boolean hasDoubleRewards(Player player) {
        return plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.WAR_ECONOMY);
    }

    /**
     * Get XP multiplier for a player
     */
    public double getXpMultiplier(Player player) {
        double multiplier = 1.0;

        Government gov = plugin.getDataManager().getGovernment();

        // President XP bonus
        if (gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId())) {
            multiplier += (plugin.getConfig().getDouble("president.buffs.xp-multiplier", 1.10) - 1.0);
        }

        // Culture minister XP bonus
        UUID cultureMinister = gov.getCabinetMember(Government.CabinetPosition.CULTURE);
        if (cultureMinister != null && cultureMinister.equals(player.getUniqueId())) {
            multiplier += (plugin.getConfig().getDouble("cabinet.culture.xp-multiplier", 1.15) - 1.0);
        }

        // Golden Age order
        if (plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.GOLDEN_AGE)) {
            multiplier += 0.25;
        }

        // Education Initiative order
        if (plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.EDUCATION_ADVANCEMENT)) {
            multiplier += 2.0; // 3x total
        }

        // Double XP cabinet decision
        multiplier *= plugin.getCabinetManager().getGlobalXpMultiplier();

        return multiplier;
    }

    /**
     * Get drop multiplier for a player
     */
    public double getDropMultiplier(Player player) {
        double multiplier = 1.0;

        // Golden Age order
        if (plugin.getExecutiveOrderManager().isOrderActive(ExecutiveOrder.ExecutiveOrderType.GOLDEN_AGE)) {
            multiplier += 0.25;
        }

        // Environmental Protection order
        if (plugin.getExecutiveOrderManager()
                .isOrderActive(ExecutiveOrder.ExecutiveOrderType.ENVIRONMENTAL_PROTECTION)) {
            multiplier += 2.0; // 3x for farming/fishing
        }

        // Resource Boom cabinet decision
        multiplier *= plugin.getCabinetManager().getGlobalDropMultiplier();

        return multiplier;
    }
}
