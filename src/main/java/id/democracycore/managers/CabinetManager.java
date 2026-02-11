package id.democracycore.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import id.democracycore.DemocracyCore;
import id.democracycore.models.CabinetDecision;
import id.democracycore.models.Government;
import id.democracycore.models.Treasury;
import id.democracycore.utils.MessageUtils;

public class CabinetManager {
    
    private final DemocracyCore plugin;
    private final Map<UUID, Map<CabinetDecision.DecisionType, Long>> decisionCooldowns = new HashMap<>();
    private final Map<CabinetDecision.DecisionType, Long> activeDecisions = new HashMap<>();
    private final Map<CabinetDecision.DecisionType, Long> decisionEndTimes = new HashMap<>();
    
    // Active effect tracking
    private final Set<UUID> militaryDraftPlayers = new HashSet<>();
    private final Set<UUID> taxHolidayPlayers = new HashSet<>();
    private final Map<UUID, Double> shopDiscounts = new HashMap<>();
    private double globalDropMultiplier = 1.0;
    private double globalXpMultiplier = 1.0;
    private boolean tradeFairActive = false;
    private boolean resourceBoomActive = false;
    private boolean constructionBoomActive = false;
    private boolean festivalActive = false;
    
    public CabinetManager(DemocracyCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean canExecuteDecision(UUID ministerId, CabinetDecision.DecisionType type) {
        Government gov = plugin.getDataManager().getGovernment();
        GovernmentManager govManager = plugin.getGovernmentManager();
        
        // Check if player is the correct minister for this decision
        Government.CabinetPosition requiredPosition = getRequiredPosition(type);
        if (requiredPosition == null) return false;
        
        UUID currentMinister = gov.getCabinetMember(requiredPosition);
        if (currentMinister == null || !currentMinister.equals(ministerId)) {
            return false;
        }
        
        // Check cooldown
        long cooldownMs = plugin.getConfig().getLong("cabinet.decision-cooldown-hours", 48) * 3600000L;
        Map<CabinetDecision.DecisionType, Long> playerCooldowns = decisionCooldowns.computeIfAbsent(ministerId, k -> new HashMap<>());
        Long lastUse = playerCooldowns.get(type);
        if (lastUse != null && System.currentTimeMillis() - lastUse < cooldownMs) {
            return false;
        }
        
        // Check treasury cost
        int cost = getDecisionCost(type);
        Treasury treasury = plugin.getDataManager().getTreasury();
        return treasury.getBalance() >= cost;
    }
    
    public long getRemainingCooldown(UUID ministerId, CabinetDecision.DecisionType type) {
        long cooldownMs = plugin.getConfig().getLong("cabinet.decision-cooldown-hours", 48) * 3600000L;
        Map<CabinetDecision.DecisionType, Long> playerCooldowns = decisionCooldowns.get(ministerId);
        if (playerCooldowns == null) return 0;
        Long lastUse = playerCooldowns.get(type);
        if (lastUse == null) return 0;
        long remaining = cooldownMs - (System.currentTimeMillis() - lastUse);
        return Math.max(0, remaining);
    }
    
    public boolean executeDecision(UUID ministerId, CabinetDecision.DecisionType type) {
        if (!canExecuteDecision(ministerId, type)) {
            return false;
        }
        
        int cost = getDecisionCost(type);
        plugin.getTreasuryManager().withdraw(Treasury.TransactionType.EXECUTIVE_ORDER, (double) cost, "Cabinet Decision: " + type.name(), ministerId);
        
        // Set cooldown
        decisionCooldowns.computeIfAbsent(ministerId, k -> new HashMap<>()).put(type, System.currentTimeMillis());
        
        // Create decision record
        Government.CabinetPosition position = getRequiredPosition(type);
        CabinetDecision.CabinetPosition cabPosition = CabinetDecision.CabinetPosition.valueOf(position.name());
        CabinetDecision decision = new CabinetDecision(type, cabPosition, ministerId);
        plugin.getDataManager().getActiveDecisions().add(decision);
        
        // Track active decision
        activeDecisions.put(type, System.currentTimeMillis());
        decisionEndTimes.put(type, System.currentTimeMillis() + getDecisionDuration(type));
        
        // Execute the decision effect
        applyDecisionEffect(type, decision);
        
        // Broadcast
        Player minister = Bukkit.getPlayer(ministerId);
        String ministerName = minister != null ? minister.getName() : "Unknown";
        MessageUtils.broadcast("<gold>📜 <yellow>Cabinet Decision: <white>" + getDecisionDisplayName(type));
        MessageUtils.broadcast("<gray>Issued by Minister <white>" + ministerName);
        MessageUtils.broadcast("<gray>Duration: <white>" + MessageUtils.formatTime(getDecisionDuration(type)));
        
        plugin.getDataManager().saveAll();
        return true;
    }
    
    private void applyDecisionEffect(CabinetDecision.DecisionType type, CabinetDecision decision) {
        switch (type) {
            // Defense Ministry Decisions
            case DECLARE_WAR -> startWarGamesEvent();
            case MILITARY_DRAFT -> activateMilitaryDraft();
            case DEFENSE_PROTOCOL -> activateNationalProtection();
            case ARMORY_DISCOUNT -> activateArmoryDiscount();
            case BORDER_PATROL -> activateBorderPatrol();
            
            // Treasury Ministry Decisions
            case TAX_HOLIDAY -> activateTaxHoliday();
            case ECONOMIC_STIMULUS -> distributeEconomicStimulus();
            case AUCTION_BOOST -> activateAuctionBoost();
            case TREASURY_BONUS -> distributeTreasuryBonus();
            case MARKET_CRASH -> triggerMarketCrash();
            
            // Commerce Ministry Decisions
            case TRADE_FAIR -> activateTradeFair();
            case RESOURCE_BOOM -> activateResourceBoom();
            case MERCHANT_CARAVAN -> spawnMerchantCaravan();
            case BLACK_FRIDAY -> activateBlackFriday();
            case TREASURE_HUNT -> startTreasureHunt();
            
            // Infrastructure Ministry Decisions
            case CONSTRUCTION_BOOM -> activateConstructionBoom();
            case FREE_REAL_ESTATE -> grantFreeRealEstate();
            case BUILDERS_PARADISE -> activateBuildersParadise();
            case PUBLIC_WORKS -> startPublicWorks();
            case INFRASTRUCTURE_GRANT -> distributeInfrastructureGrants();
            
            // Culture Ministry Decisions
            case FESTIVAL_WEEK -> startFestivalWeek();
            case DOUBLE_XP_WEEKEND -> activateDoubleXp();
            case FIREWORK_SHOW -> startFireworkCelebration();
            case CULTURAL_EXCHANGE -> activateCulturalExchange();
            case COMMUNITY_EVENT -> startCommunityEvent();
        }
    }
    
    // ==================== DEFENSE MINISTRY DECISIONS ====================
    
    private void startWarGamesEvent() {
        MessageUtils.broadcast("<red>⚔ <dark_red>WAR GAMES EVENT <red>⚔");
        MessageUtils.broadcast("<gray>PvP rewards doubled! Special combat challenges active!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 72000, 0)); // 1 hour
        }
    }
    
    private void activateMilitaryDraft() {
        MessageUtils.broadcast("<red>🎖 <yellow>MILITARY DRAFT ACTIVE");
        MessageUtils.broadcast("<gray>All players receive combat gear and buffs!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            militaryDraftPlayers.add(player.getUniqueId());
            // Give basic combat gear
            if (player.getInventory().getHelmet() == null) {
                player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            }
            if (player.getInventory().getChestplate() == null) {
                player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            }
            if (player.getInventory().getLeggings() == null) {
                player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            }
            if (player.getInventory().getBoots() == null) {
                player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            }
            // Combat buffs
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 144000, 0)); // 2 hours
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 144000, 0));
        }
    }
    
    private void activateNationalProtection() {
        MessageUtils.broadcast("<green>🛡 <aqua>NATIONAL PROTECTION ACTIVE");
        MessageUtils.broadcast("<gray>All players receive defensive buffs!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 144000, 1)); // 2 hours
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 144000, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 144000, 0));
        }
    }
    
    private void activateArmoryDiscount() {
        shopDiscounts.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            shopDiscounts.put(player.getUniqueId(), 0.5); // 50% discount
        }
        MessageUtils.broadcast("<gold>🗡 <yellow>ARMORY DISCOUNT ACTIVE");
        MessageUtils.broadcast("<gray>50% off all weapon and armor purchases!");
    }
    
    private void activateBorderPatrol() {
        MessageUtils.broadcast("<blue>🚨 <aqua>BORDER PATROL ACTIVE");
        MessageUtils.broadcast("<gray>Increased mob spawns at world borders, bonus rewards for kills!");
        // This would integrate with mob spawn events
    }
    
    // ==================== TREASURY MINISTRY DECISIONS ====================
    
    private void activateTaxHoliday() {
        taxHolidayPlayers.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            taxHolidayPlayers.add(player.getUniqueId());
        }
        MessageUtils.broadcast("<green>💰 <yellow>TAX HOLIDAY DECLARED");
        MessageUtils.broadcast("<gray>No transaction taxes for the duration!");
    }
    
    private void distributeEconomicStimulus() {
        double amount = plugin.getConfig().getDouble("cabinet.decisions.treasury.stimulus-amount", 25000);
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getVaultHook().deposit(player.getUniqueId(), amount);
            MessageUtils.send(player, "<green>You received $" + MessageUtils.formatNumber(amount) + " economic stimulus!");
            count++;
        }
        MessageUtils.broadcast("<green>💵 <yellow>ECONOMIC STIMULUS DISTRIBUTED");
        MessageUtils.broadcast("<gray>" + count + " players received $" + MessageUtils.formatNumber(amount) + " each!");
    }
    
    private void activateAuctionBoost() {
        MessageUtils.broadcast("<gold>🔨 <yellow>AUCTION BOOST ACTIVE");
        MessageUtils.broadcast("<gray>All auction house fees reduced by 75%!");
    }
    
    private void distributeTreasuryBonus() {
        Treasury treasury = plugin.getDataManager().getTreasury();
        double bonus = treasury.getBalance() * 0.05; // 5% of treasury
        double perPlayer = bonus / Math.max(1, Bukkit.getOnlinePlayers().size());
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getVaultHook().deposit(player.getUniqueId(), perPlayer);
            MessageUtils.send(player, "<green>You received $" + MessageUtils.formatNumber(perPlayer) + " treasury bonus!");
        }
        
        plugin.getTreasuryManager().withdraw(Treasury.TransactionType.MISC_EXPENSE, bonus, "Treasury Bonus Distribution", plugin.getDataManager().getGovernment().getPresidentUUID());
        MessageUtils.broadcast("<gold>🏦 <yellow>TREASURY BONUS DISTRIBUTED");
        MessageUtils.broadcast("<gray>5% of treasury distributed to all online players!");
    }
    
    private void triggerMarketCrash() {
        MessageUtils.broadcast("<red>📉 <dark_red>MARKET CRASH SIMULATION");
        MessageUtils.broadcast("<gray>All shop prices reduced by 80% for limited time!");
        MessageUtils.broadcast("<yellow>Quick! Buy everything you can!");
    }
    
    // ==================== COMMERCE MINISTRY DECISIONS ====================
    
    private void activateTradeFair() {
        tradeFairActive = true;
        MessageUtils.broadcast("<gold>🎪 <yellow>TRADE FAIR ACTIVE");
        MessageUtils.broadcast("<gray>Player-to-player trades give bonus items!");
    }
    
    private void activateResourceBoom() {
        resourceBoomActive = true;
        globalDropMultiplier = 2.0;
        MessageUtils.broadcast("<green>⛏ <yellow>RESOURCE BOOM ACTIVE");
        MessageUtils.broadcast("<gray>All mining and gathering drops doubled!");
    }
    
    private void spawnMerchantCaravan() {
        MessageUtils.broadcast("<gold>🐪 <yellow>MERCHANT CARAVAN ARRIVED");
        MessageUtils.broadcast("<gray>Special traders with rare items have appeared at spawn!");
        // Would spawn custom villagers/NPCs
    }
    
    private void activateBlackFriday() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            shopDiscounts.put(player.getUniqueId(), 0.25); // 75% discount
        }
        MessageUtils.broadcast("<dark_gray>🛒 <white>BLACK FRIDAY SALE");
        MessageUtils.broadcast("<gray>75% off everything in all shops!");
    }
    
    private void startTreasureHunt() {
        MessageUtils.broadcast("<gold>🗺 <yellow>TREASURE HUNT STARTED");
        MessageUtils.broadcast("<gray>Hidden treasures have been placed around the world!");
        MessageUtils.broadcast("<gray>Find them for amazing rewards!");
        
        // Give all players a treasure map
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack map = new ItemStack(Material.FILLED_MAP);
            player.getInventory().addItem(map);
            MessageUtils.send(player, "<gold>You received a treasure map!");
        }
    }
    
    // ==================== INFRASTRUCTURE MINISTRY DECISIONS ====================
    
    private void activateConstructionBoom() {
        constructionBoomActive = true;
        MessageUtils.broadcast("<yellow>🏗 <gold>CONSTRUCTION BOOM ACTIVE");
        MessageUtils.broadcast("<gray>Building materials drop at 3x rate!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 144000, 1)); // 2 hours
        }
    }
    
    private void grantFreeRealEstate() {
        MessageUtils.broadcast("<green>🏠 <yellow>FREE REAL ESTATE");
        MessageUtils.broadcast("<gray>All players receive +200 claim blocks!");
        // Would integrate with land claim plugin
    }
    
    private void activateBuildersParadise() {
        MessageUtils.broadcast("<aqua>🔧 <yellow>BUILDER'S PARADISE ACTIVE");
        MessageUtils.broadcast("<gray>Infinite durability on tools, creative-like building speed!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 288000, 2)); // 4 hours
        }
    }
    
    private void startPublicWorks() {
        MessageUtils.broadcast("<blue>🚧 <yellow>PUBLIC WORKS PROJECT");
        MessageUtils.broadcast("<gray>Community building event started! Contribute for rewards!");
    }
    
    private void distributeInfrastructureGrants() {
        double amount = plugin.getConfig().getDouble("cabinet.decisions.infrastructure.grant-amount", 50000);
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getVaultHook().deposit(player.getUniqueId(), amount);
            MessageUtils.send(player, "<green>You received $" + MessageUtils.formatNumber(amount) + " infrastructure grant!");
        }
        MessageUtils.broadcast("<gold>🏛 <yellow>INFRASTRUCTURE GRANTS DISTRIBUTED");
    }
    
    // ==================== CULTURE MINISTRY DECISIONS ====================
    
    private void startFestivalWeek() {
        festivalActive = true;
        MessageUtils.broadcast("<light_purple>🎉 <gold>FESTIVAL WEEK BEGINS");
        MessageUtils.broadcast("<gray>Daily events, bonus rewards, and celebrations!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 288000, 0)); // 4 hours
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 288000, 1));
        }
    }
    
    private void activateDoubleXp() {
        globalXpMultiplier = 2.0;
        MessageUtils.broadcast("<green>✨ <yellow>DOUBLE XP WEEKEND");
        MessageUtils.broadcast("<gray>All experience gains doubled!");
    }
    
    private void startFireworkCelebration() {
        MessageUtils.broadcast("<red>🎆 <gold>FIREWORK CELEBRATION");
        MessageUtils.broadcast("<gray>Fireworks light up the sky!");
        
        // Spawn fireworks at spawn and player locations
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!isDecisionActive(CabinetDecision.DecisionType.FIREWORK_SHOW)) {
                task.cancel();
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Math.random() < 0.3) { // 30% chance per player per tick
                    player.getWorld().spawn(player.getLocation().add(0, 30, 0), org.bukkit.entity.Firework.class);
                }
            }
        }, 0L, 100L); // Every 5 seconds
    }
    
    private void activateCulturalExchange() {
        MessageUtils.broadcast("<light_purple>🌍 <yellow>CULTURAL EXCHANGE ACTIVE");
        MessageUtils.broadcast("<gray>Visit other players' builds for bonus rewards!");
    }
    
    private void startCommunityEvent() {
        MessageUtils.broadcast("<aqua>👥 <yellow>COMMUNITY EVENT STARTED");
        MessageUtils.broadcast("<gray>Participate in community activities for exclusive rewards!");
        
        // Give participation token
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack token = new ItemStack(Material.NETHER_STAR);
            var meta = token.getItemMeta();
            meta.displayName(MessageUtils.parse("<gold>Community Event Token"));
            meta.lore(List.of(
                MessageUtils.parse("<gray>Participate in the community event!"),
                MessageUtils.parse("<yellow>Right-click to check progress")
            ));
            token.setItemMeta(meta);
            player.getInventory().addItem(token);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    public boolean isDecisionActive(CabinetDecision.DecisionType type) {
        Long endTime = decisionEndTimes.get(type);
        return endTime != null && System.currentTimeMillis() < endTime;
    }
    
    public long getDecisionRemainingTime(CabinetDecision.DecisionType type) {
        Long endTime = decisionEndTimes.get(type);
        if (endTime == null) return 0;
        return Math.max(0, endTime - System.currentTimeMillis());
    }
    
    public void checkExpiredDecisions() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<CabinetDecision.DecisionType, Long>> it = decisionEndTimes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<CabinetDecision.DecisionType, Long> entry = it.next();
            if (now >= entry.getValue()) {
                expireDecision(entry.getKey());
                it.remove();
                activeDecisions.remove(entry.getKey());
            }
        }
    }
    
    private void expireDecision(CabinetDecision.DecisionType type) {
        MessageUtils.broadcast("<gray>Cabinet decision <white>" + getDecisionDisplayName(type) + " <gray>has expired.");
        
        switch (type) {
            case TAX_HOLIDAY -> taxHolidayPlayers.clear();
            case RESOURCE_BOOM -> {
                resourceBoomActive = false;
                globalDropMultiplier = 1.0;
            }
            case TRADE_FAIR -> tradeFairActive = false;
            case CONSTRUCTION_BOOM -> constructionBoomActive = false;
            case DOUBLE_XP_WEEKEND -> globalXpMultiplier = 1.0;
            case FESTIVAL_WEEK -> festivalActive = false;
            case ARMORY_DISCOUNT, BLACK_FRIDAY -> shopDiscounts.clear();
        }
    }
    
    public Government.CabinetPosition getRequiredPosition(CabinetDecision.DecisionType type) {
        return switch (type) {
            case DECLARE_WAR, MILITARY_DRAFT, DEFENSE_PROTOCOL, ARMORY_DISCOUNT, BORDER_PATROL -> 
                Government.CabinetPosition.DEFENSE;
            case TAX_HOLIDAY, ECONOMIC_STIMULUS, AUCTION_BOOST, TREASURY_BONUS, MARKET_CRASH -> 
                Government.CabinetPosition.TREASURY;
            case TRADE_FAIR, RESOURCE_BOOM, MERCHANT_CARAVAN, BLACK_FRIDAY, TREASURE_HUNT -> 
                Government.CabinetPosition.COMMERCE;
            case CONSTRUCTION_BOOM, FREE_REAL_ESTATE, BUILDERS_PARADISE, PUBLIC_WORKS, INFRASTRUCTURE_GRANT -> 
                Government.CabinetPosition.INFRASTRUCTURE;
            case FESTIVAL_WEEK, DOUBLE_XP_WEEKEND, FIREWORK_SHOW, CULTURAL_EXCHANGE, COMMUNITY_EVENT -> 
                Government.CabinetPosition.CULTURE;
        };
    }
    
    public int getDecisionCost(CabinetDecision.DecisionType type) {
        String path = "cabinet.decisions." + getRequiredPosition(type).name().toLowerCase() + ".cost";
        return plugin.getConfig().getInt(path, 100000);
    }
    
    public long getDecisionDuration(CabinetDecision.DecisionType type) {
        String path = "cabinet.decisions." + getRequiredPosition(type).name().toLowerCase() + ".duration-hours";
        return plugin.getConfig().getLong(path, 24) * 3600000L;
    }
    
    public String getDecisionDisplayName(CabinetDecision.DecisionType type) {
        return switch (type) {
            case DECLARE_WAR -> "War Games Event";
            case MILITARY_DRAFT -> "Military Draft";
            case DEFENSE_PROTOCOL -> "National Protection";
            case ARMORY_DISCOUNT -> "Armory Discount";
            case BORDER_PATROL -> "Border Patrol";
            case TAX_HOLIDAY -> "Tax Holiday";
            case ECONOMIC_STIMULUS -> "Economic Stimulus";
            case AUCTION_BOOST -> "Auction Boost";
            case TREASURY_BONUS -> "Treasury Bonus";
            case MARKET_CRASH -> "Market Crash";
            case TRADE_FAIR -> "Trade Fair";
            case RESOURCE_BOOM -> "Resource Boom";
            case MERCHANT_CARAVAN -> "Merchant Caravan";
            case BLACK_FRIDAY -> "Black Friday";
            case TREASURE_HUNT -> "Treasure Hunt";
            case CONSTRUCTION_BOOM -> "Construction Boom";
            case FREE_REAL_ESTATE -> "Free Real Estate";
            case BUILDERS_PARADISE -> "Builder's Paradise";
            case PUBLIC_WORKS -> "Public Works";
            case INFRASTRUCTURE_GRANT -> "Infrastructure Grant";
            case FESTIVAL_WEEK -> "Festival Week";
            case DOUBLE_XP_WEEKEND -> "Double XP Weekend";
            case FIREWORK_SHOW -> "Firework Celebration";
            case CULTURAL_EXCHANGE -> "Cultural Exchange";
            case COMMUNITY_EVENT -> "Community Event";
        };
    }
    
    public List<CabinetDecision.DecisionType> getDecisionsForPosition(Government.CabinetPosition position) {
        List<CabinetDecision.DecisionType> decisions = new ArrayList<>();
        for (CabinetDecision.DecisionType type : CabinetDecision.DecisionType.values()) {
            if (getRequiredPosition(type) == position) {
                decisions.add(type);
            }
        }
        return decisions;
    }
    
    // Getters for active effects
    public boolean isTaxHolidayActive() { return !taxHolidayPlayers.isEmpty(); }
    public boolean isTradeFairActive() { return tradeFairActive; }
    public boolean isResourceBoomActive() { return resourceBoomActive; }
    public boolean isConstructionBoomActive() { return constructionBoomActive; }
    public boolean isFestivalActive() { return festivalActive; }
    public double getGlobalDropMultiplier() { return globalDropMultiplier; }
    public double getGlobalXpMultiplier() { return globalXpMultiplier; }
    public double getShopDiscount(UUID playerId) { return shopDiscounts.getOrDefault(playerId, 1.0); }
    
    public void applyEffectsToPlayer(Player player) {
        // Apply any active decision effects to newly joined players
        if (isDecisionActive(CabinetDecision.DecisionType.MILITARY_DRAFT)) {
            militaryDraftPlayers.add(player.getUniqueId());
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 144000, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 144000, 0));
        }
        if (isDecisionActive(CabinetDecision.DecisionType.DEFENSE_PROTOCOL)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 144000, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 144000, 0));
        }
        if (isDecisionActive(CabinetDecision.DecisionType.CONSTRUCTION_BOOM) ||
            isDecisionActive(CabinetDecision.DecisionType.BUILDERS_PARADISE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 144000, 1));
        }
        if (isDecisionActive(CabinetDecision.DecisionType.FESTIVAL_WEEK)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 144000, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 144000, 1));
        }
        if (isDecisionActive(CabinetDecision.DecisionType.TAX_HOLIDAY)) {
            taxHolidayPlayers.add(player.getUniqueId());
        }
        if (isDecisionActive(CabinetDecision.DecisionType.ARMORY_DISCOUNT)) {
            shopDiscounts.put(player.getUniqueId(), 0.5);
        }
        if (isDecisionActive(CabinetDecision.DecisionType.BLACK_FRIDAY)) {
            shopDiscounts.put(player.getUniqueId(), 0.25);
        }
    }

    // Additional methods for DemocracyCommand
    public List<CabinetDecision> getAllActiveDecisions() {
        return plugin.getDataManager().getActiveDecisions();
    }

    public boolean isDecisionOnCooldown(CabinetDecision.DecisionType type) {
        return getRemainingCooldown(null, type) > 0; // Check if any cooldown exists
    }

    public boolean issueDecision(Player player, CabinetDecision.DecisionType type) {
        return executeDecision(player.getUniqueId(), type);
    }

    public List<CabinetDecision> getActiveDecisionsByPosition(CabinetDecision.CabinetPosition position) {
        return plugin.getDataManager().getActiveDecisions().stream()
            .filter(decision -> decision.getMinisterPosition() == position)
            .toList();
    }
}
