package id.democracycore.models;

import java.util.UUID;

public class CabinetDecision {
    
    // Re-export CabinetPosition untuk backward compatibility dengan DemocracyCommand
    public static enum CabinetPosition {
        DEFENSE("Minister of Defense", "&4[MoD]"),
        TREASURY("Minister of Treasury", "&2[MoT]"),
        COMMERCE("Minister of Commerce", "&9[MoC]"),
        INFRASTRUCTURE("Minister of Infrastructure", "&7[MoI]"),
        CULTURE("Minister of Culture", "&d[MoK]");
        
        private final String displayName;
        private final String prefix;
        
        CabinetPosition(String displayName, String prefix) {
            this.displayName = displayName;
            this.prefix = prefix;
        }
        
        public String getDisplayName() { return displayName; }
        public String getPrefix() { return prefix; }
        
        public Government.CabinetPosition toGovernmentPosition() {
            return Government.CabinetPosition.valueOf(this.name());
        }
    }
    
    public static enum DecisionType {
        // Defense Minister Decisions
        DECLARE_WAR("Declare War Event", CabinetPosition.DEFENSE, 24 * 60 * 60 * 1000L,
            "Opens PvP war zone with doubled loot drops"),
        MILITARY_DRAFT("Military Draft", CabinetPosition.DEFENSE, 0L,
            "Starts mandatory PvP tournament"),
        DEFENSE_PROTOCOL("Defense Protocol", CabinetPosition.DEFENSE, 12 * 60 * 60 * 1000L,
            "-20% damage taken server-wide"),
        ARMORY_DISCOUNT("Armory Discount", CabinetPosition.DEFENSE, 24 * 60 * 60 * 1000L,
            "30% off weapons & armor at NPC shops"),
        BORDER_PATROL("Border Patrol", CabinetPosition.DEFENSE, 48 * 60 * 60 * 1000L,
            "Elite guards protect spawn area"),
        
        // Treasury Minister Decisions
        TAX_HOLIDAY("Tax Holiday", CabinetPosition.TREASURY, 24 * 60 * 60 * 1000L,
            "No server tax for 24 hours"),
        ECONOMIC_STIMULUS("Economic Stimulus", CabinetPosition.TREASURY, 0L,
            "10k vault points to all online players"),
        AUCTION_BOOST("Auction Boost", CabinetPosition.TREASURY, 48 * 60 * 60 * 1000L,
            "Double auction limits, reduced fees"),
        TREASURY_BONUS("Treasury Bonus", CabinetPosition.TREASURY, 12 * 60 * 60 * 1000L,
            "+50% vault points from mobs & mining"),
        MARKET_CRASH("Market Crash", CabinetPosition.TREASURY, 6 * 60 * 60 * 1000L,
            "40% discount at admin shop"),
        
        // Commerce Minister Decisions
        TRADE_FAIR("Trade Fair", CabinetPosition.COMMERCE, 24 * 60 * 60 * 1000L,
            "Special merchant NPCs with exclusive items"),
        RESOURCE_BOOM("Resource Boom", CabinetPosition.COMMERCE, 12 * 60 * 60 * 1000L,
            "Double drops from mining, farming, fishing"),
        MERCHANT_CARAVAN("Merchant Caravan", CabinetPosition.COMMERCE, 6 * 60 * 60 * 1000L,
            "Special NPC caravan with rare cheap items"),
        BLACK_FRIDAY("Black Friday", CabinetPosition.COMMERCE, 48 * 60 * 60 * 1000L,
            "Free shop advertising, boosted visibility"),
        TREASURE_HUNT("Treasure Hunt", CabinetPosition.COMMERCE, 0L,
            "Spawns 20 hidden treasure chests"),
        
        // Infrastructure Minister Decisions
        CONSTRUCTION_BOOM("Construction Boom", CabinetPosition.INFRASTRUCTURE, 24 * 60 * 60 * 1000L,
            "Haste III + Jump Boost II server-wide"),
        FREE_REAL_ESTATE("Free Real Estate", CabinetPosition.INFRASTRUCTURE, 48 * 60 * 60 * 1000L,
            "50% off claim blocks"),
        BUILDERS_PARADISE("Builder's Paradise", CabinetPosition.INFRASTRUCTURE, 12 * 60 * 60 * 1000L,
            "Double stone, wood, terracotta drops"),
        PUBLIC_WORKS("Public Works", CabinetPosition.INFRASTRUCTURE, 30 * 60 * 1000L,
            "Building materials rain at spawn"),
        INFRASTRUCTURE_GRANT("Infrastructure Grant", CabinetPosition.INFRASTRUCTURE, 0L,
            "50k to random 10 players for building"),
        
        // Culture Minister Decisions
        FESTIVAL_WEEK("Festival Week", CabinetPosition.CULTURE, 72 * 60 * 60 * 1000L,
            "Mini-games tournament with prizes"),
        DOUBLE_XP_WEEKEND("Double XP Weekend", CabinetPosition.CULTURE, 48 * 60 * 60 * 1000L,
            "2x XP gain server-wide"),
        FIREWORK_SHOW("Firework Show", CabinetPosition.CULTURE, 24 * 60 * 60 * 1000L,
            "Hourly firework shows at spawn"),
        CULTURAL_EXCHANGE("Cultural Exchange", CabinetPosition.CULTURE, 24 * 60 * 60 * 1000L,
            "NPC trading rare decorative items"),
        COMMUNITY_EVENT("Community Event", CabinetPosition.CULTURE, 0L,
            "Building/PvP competition with 500k prize");
        
        private final String displayName;
        private final CabinetPosition position;
        private final long durationMillis;
        private final String description;
        
        DecisionType(String displayName, CabinetPosition position, 
                           long durationMillis, String description) {
            this.displayName = displayName;
            this.position = position;
            this.durationMillis = durationMillis;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public CabinetPosition getPosition() { return position; }
        public long getDurationMillis() { return durationMillis; }
        public String getDescription() { return description; }
    }
    
    private DecisionType type;
    private CabinetPosition ministerPosition;
    private UUID issuedBy;
    private long issuedTime;
    private long expirationTime;
    private boolean active;
    
    public CabinetDecision(DecisionType type, CabinetPosition ministerPosition, UUID issuedBy) {
        this.type = type;
        this.ministerPosition = ministerPosition;
        this.issuedBy = issuedBy;
        this.issuedTime = System.currentTimeMillis();
        this.expirationTime = this.issuedTime + type.getDurationMillis();
        this.active = true;
    }

    public DecisionType getType() { return type; }
    public void setType(DecisionType type) { this.type = type; }
    public CabinetPosition getMinisterPosition() { return ministerPosition; }
    public void setMinisterPosition(CabinetPosition ministerPosition) { this.ministerPosition = ministerPosition; }
    public UUID getIssuedBy() { return issuedBy; }
    public void setIssuedBy(UUID issuedBy) { this.issuedBy = issuedBy; }
    public long getIssuedTime() { return issuedTime; }
    public void setIssuedTime(long issuedTime) { this.issuedTime = issuedTime; }
    public long getExpirationTime() { return expirationTime; }
    public void setExpirationTime(long expirationTime) { this.expirationTime = expirationTime; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isExpired() {
        if (type.getDurationMillis() == 0) return true;
        return System.currentTimeMillis() > expirationTime;
    }
    
    public long getRemainingTime() {
        return Math.max(0, expirationTime - System.currentTimeMillis());
    }
}
