package id.democracycore.models;

import java.util.UUID;

public class ExecutiveOrder {

    private ExecutiveOrderType type;
    private UUID issuedBy;
    private long issuedTime;
    private long expirationTime;
    private boolean active;

    public ExecutiveOrder(ExecutiveOrderType type, UUID issuedBy, long durationMillis) {
        this.type = type;
        this.issuedBy = issuedBy;
        this.issuedTime = System.currentTimeMillis();
        this.expirationTime = this.issuedTime + durationMillis;
        this.active = true;
    }

    public enum ExecutiveOrderType {
        GOLDEN_AGE("Golden Age Decree",
                "The President declares a Golden Age of prosperity!",
                48 * 60 * 60 * 1000L,
                "+25% XP gain, +25% vault points, +15% rare drops"),

        STATE_OF_EMERGENCY("State of Emergency",
                "For the safety of all citizens, PvP has been temporarily suspended.",
                24 * 60 * 60 * 1000L,
                "PvP disabled server-wide (except arena)"),

        NATIONAL_HOLIDAY("National Holiday",
                "The President declares a national celebration day!",
                24 * 60 * 60 * 1000L,
                "Job cooldowns removed, daily rewards 2x claimable"),

        WAR_ECONOMY("War Economy",
                "The nation calls for its warriors! To arms!",
                72 * 60 * 60 * 1000L,
                "Double PvP kill rewards, +50% PvP damage"),

        ECONOMIC_RECOVERY("Economic Recovery Plan",
                "The Treasury opens its coffers to aid citizens in need.",
                36 * 60 * 60 * 1000L,
                "50k stimulus per player, 25% shop discount"),

        INFRASTRUCTURE_INITIATIVE("Infrastructure Initiative",
                "Build the nation! The President supports your vision.",
                72 * 60 * 60 * 1000L,
                "+100 free claim blocks, Haste II server-wide"),

        ENVIRONMENTAL_PROTECTION("Environmental Protection Act",
                "The land's bounty is blessed by presidential decree.",
                48 * 60 * 60 * 1000L,
                "Triple farming/fishing/breeding drops"),

        EDUCATION_ADVANCEMENT("Education Advancement",
                "Knowledge is power! The President invests in your growth.",
                48 * 60 * 60 * 1000L,
                "3x XP gain, -50% enchanting cost, free anvil"),

        PURGE_PROTOCOL("Purge Protocol",
                "Survival of the fittest. May the strongest prevail.",
                6 * 60 * 60 * 1000L,
                "Full PvP enabled, random respawn, no keep inventory"),

        PRESIDENTIAL_PARDON("Presidential Pardon",
                "In the spirit of mercy, past transgressions are forgiven.",
                0L,
                "Clears 1 punishment from all online players"),

        TAX_SUSPENSION("Tax Suspension",
                "By presidential decree, all tax obligations are hereby suspended for the people!",
                48 * 60 * 60 * 1000L,
                "All tax collection is disabled for the duration"),

        TAX_SURGE("Tax Surge",
                "The nation demands more! Tax rates are raised to fund the treasury!",
                24 * 60 * 60 * 1000L,
                "Tax rate increased to 5x the base rate for the duration");

        private final String displayName;
        private final String flavorText;
        private final long defaultDuration;
        private final String effectDescription;

        ExecutiveOrderType(String displayName, String flavorText, long defaultDuration, String effectDescription) {
            this.displayName = displayName;
            this.flavorText = flavorText;
            this.defaultDuration = defaultDuration;
            this.effectDescription = effectDescription;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getFlavorText() {
            return flavorText;
        }

        public long getDefaultDuration() {
            return defaultDuration;
        }

        public String getEffectDescription() {
            return effectDescription;
        }

        public String getDescription() {
            return effectDescription;
        }
    }

    public ExecutiveOrderType getType() {
        return type;
    }

    public void setType(ExecutiveOrderType type) {
        this.type = type;
    }

    public UUID getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(UUID issuedBy) {
        this.issuedBy = issuedBy;
    }

    public long getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime(long issuedTime) {
        this.issuedTime = issuedTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isExpired() {
        if (type.getDefaultDuration() == 0)
            return true;
        return System.currentTimeMillis() > expirationTime;
    }

    public long getRemainingTime() {
        return Math.max(0, expirationTime - System.currentTimeMillis());
    }
}
