package id.democracycore.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import id.democracycore.DemocracyCore;
import id.democracycore.models.Government;
import id.democracycore.utils.MessageUtils;

public class BuffManager {

    private final DemocracyCore plugin;

    // Modifier keys
    private static final String PRESIDENT_DAMAGE_KEY = "democracy_president_damage";
    private static final String PRESIDENT_DEFENSE_KEY = "democracy_president_defense";
    private static final String PRESIDENT_HEALTH_KEY = "democracy_president_health";
    private static final String CABINET_DAMAGE_KEY = "democracy_cabinet_damage";
    private static final String CABINET_DEFENSE_KEY = "democracy_cabinet_defense";
    private static final String CABINET_HEALTH_KEY = "democracy_cabinet_health";

    // Track applied buffs
    private final Map<UUID, Boolean> presidentBuffsApplied = new HashMap<>();
    private final Map<UUID, Government.CabinetPosition> cabinetBuffsApplied = new HashMap<>();

    public BuffManager(DemocracyCore plugin) {
        this.plugin = plugin;
    }

    public void applyPresidentBuffs(Player player) {
        if (player == null || !player.isOnline())
            return;

        UUID playerId = player.getUniqueId();

        // Remove any existing buffs first
        removePresidentBuffs(player);

        // Get buff values from config (adjusted for multiplier logic)
        double damageBonus = plugin.getConfig().getDouble("president.buffs.damage-multiplier", 1.15) - 1.0;
        double defenseBonus = plugin.getConfig().getDouble("president.buffs.defense-multiplier", 1.12) - 1.0;
        double extraHeartsBase = plugin.getConfig().getDouble("president.buffs.extra-hearts", 2.0);
        int extraHearts = (int) extraHeartsBase;

        // Apply damage buff (attack damage)
        AttributeInstance attackDamage = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attackDamage != null) {
            AttributeModifier damageModifier = new AttributeModifier(
                    new NamespacedKey(plugin, PRESIDENT_DAMAGE_KEY),
                    damageBonus,
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1);
            attackDamage.addModifier(damageModifier);
        }

        // Apply defense buff (armor toughness)
        AttributeInstance armorToughness = player.getAttribute(Attribute.ARMOR_TOUGHNESS);
        if (armorToughness != null) {
            AttributeModifier defenseModifier = new AttributeModifier(
                    new NamespacedKey(plugin, PRESIDENT_DEFENSE_KEY),
                    defenseBonus * 10, // Convert to armor toughness points
                    AttributeModifier.Operation.ADD_NUMBER);
            armorToughness.addModifier(defenseModifier);
        }

        // Apply extra health
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            AttributeModifier healthModifier = new AttributeModifier(
                    new NamespacedKey(plugin, PRESIDENT_HEALTH_KEY),
                    extraHearts * 2, // Hearts to health points
                    AttributeModifier.Operation.ADD_NUMBER);
            maxHealth.addModifier(healthModifier);
        }

        // Apply potion effects
        applyPresidentPotionEffects(player);

        presidentBuffsApplied.put(playerId, true);
    }

    public void removePresidentBuffs(Player player) {
        if (player == null)
            return;

        UUID playerId = player.getUniqueId();

        // Remove attribute modifiers
        removeModifier(player, Attribute.ATTACK_DAMAGE, PRESIDENT_DAMAGE_KEY);
        removeModifier(player, Attribute.ARMOR_TOUGHNESS, PRESIDENT_DEFENSE_KEY);
        removeModifier(player, Attribute.MAX_HEALTH, PRESIDENT_HEALTH_KEY);

        // Remove potion effects
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.SATURATION);

        presidentBuffsApplied.remove(playerId);

        // Ensure health doesn't exceed new max
        if (player.getHealth() > player.getAttribute(Attribute.MAX_HEALTH).getValue()) {
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
        }
    }

    public void applyPresidentPotionEffects(Player player) {
        if (player == null || !player.isOnline())
            return;

        // Night vision (permanent while president)
        if (plugin.getConfig().getBoolean("president.buffs.night-vision", true)) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    Integer.MAX_VALUE,
                    0,
                    false,
                    false,
                    true));
        }

        // Hunger immunity via saturation
        if (plugin.getConfig().getBoolean("president.buffs.hunger-immunity", true)) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.SATURATION,
                    Integer.MAX_VALUE,
                    0,
                    false,
                    false,
                    false));
        }
    }

    public void applyCabinetBuffs(Player player, Government.CabinetPosition position) {
        if (player == null || !player.isOnline() || position == null)
            return;

        UUID playerId = player.getUniqueId();

        // Remove any existing cabinet buffs first
        removeCabinetBuffs(player);

        // Get position-specific buffs from config
        String configKey = position == Government.CabinetPosition.TREASURY ? "treasury-minister"
                : position.name().toLowerCase();
        String basePath = "cabinet." + configKey + ".";

        double damageBonus = plugin.getConfig().getDouble(basePath + "damage-multiplier", 1.0) - 1.0;
        double defenseBonus = plugin.getConfig().getDouble(basePath + "defense-multiplier", 1.0) - 1.0;
        double extraHeartsBase = plugin.getConfig().getDouble(basePath + "extra-hearts", 0.0);
        int extraHearts = (int) extraHeartsBase;

        // Apply damage buff if any
        if (damageBonus > 0) {
            AttributeInstance attackDamage = player.getAttribute(Attribute.ATTACK_DAMAGE);
            if (attackDamage != null) {
                AttributeModifier damageModifier = new AttributeModifier(
                        new NamespacedKey(plugin, CABINET_DAMAGE_KEY),
                        damageBonus,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1);
                attackDamage.addModifier(damageModifier);
            }
        }

        // Apply defense buff if any
        if (defenseBonus > 0) {
            AttributeInstance armorToughness = player.getAttribute(Attribute.ARMOR_TOUGHNESS);
            if (armorToughness != null) {
                AttributeModifier defenseModifier = new AttributeModifier(
                        new NamespacedKey(plugin, CABINET_DEFENSE_KEY),
                        defenseBonus * 10,
                        AttributeModifier.Operation.ADD_NUMBER);
                armorToughness.addModifier(defenseModifier);
            }
        }

        // Apply extra health if any
        if (extraHearts > 0) {
            AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealth != null) {
                AttributeModifier healthModifier = new AttributeModifier(
                        new NamespacedKey(plugin, CABINET_HEALTH_KEY),
                        extraHearts * 2,
                        AttributeModifier.Operation.ADD_NUMBER);
                maxHealth.addModifier(healthModifier);
            }
        }

        // Apply position-specific potion effects
        applyCabinetPotionEffects(player, position);

        cabinetBuffsApplied.put(playerId, position);
    }

    public void removeCabinetBuffs(Player player) {
        if (player == null)
            return;

        UUID playerId = player.getUniqueId();
        Government.CabinetPosition position = cabinetBuffsApplied.get(playerId);

        // Remove attribute modifiers
        removeModifier(player, Attribute.ATTACK_DAMAGE, CABINET_DAMAGE_KEY);
        removeModifier(player, Attribute.ARMOR_TOUGHNESS, CABINET_DEFENSE_KEY);
        removeModifier(player, Attribute.MAX_HEALTH, CABINET_HEALTH_KEY);

        // Remove position-specific potion effects
        if (position != null) {
            removeCabinetPotionEffects(player, position);
        }

        cabinetBuffsApplied.remove(playerId);

        // Ensure health doesn't exceed new max
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null && player.getHealth() > maxHealth.getValue()) {
            player.setHealth(maxHealth.getValue());
        }
    }

    private void applyCabinetPotionEffects(Player player, Government.CabinetPosition position) {
        switch (position) {
            case DEFENSE -> {
                // Defense minister gets combat buffs
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0, false, false, true));
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 0, false, false, true));
            }
            case TREASURY -> {
                // Treasury minister gets luck for better drops
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 1, false, false, true));
            }
            case COMMERCE -> {
                // Commerce minister gets speed for trading
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false, true));
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 0, false, false, true));
            }
            case INFRASTRUCTURE -> {
                // Infrastructure minister gets haste for building
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.HASTE, Integer.MAX_VALUE, 1, false, false, true));
            }
            case CULTURE -> {
                // Culture minister gets heroic presence
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false, true));
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 0, false, false, true));
            }
        }
    }

    private void removeCabinetPotionEffects(Player player, Government.CabinetPosition position) {
        switch (position) {
            case DEFENSE -> {
                player.removePotionEffect(PotionEffectType.STRENGTH);
                player.removePotionEffect(PotionEffectType.RESISTANCE);
            }
            case TREASURY -> {
                player.removePotionEffect(PotionEffectType.LUCK);
            }
            case COMMERCE -> {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.LUCK);
            }
            case INFRASTRUCTURE -> {
                player.removePotionEffect(PotionEffectType.HASTE);
            }
            case CULTURE -> {
                player.removePotionEffect(PotionEffectType.GLOWING);
                player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
            }
        }
    }

    private void removeModifier(Player player, Attribute attribute, String key) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null)
            return;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        for (AttributeModifier modifier : instance.getModifiers()) {
            if (modifier.getKey().equals(namespacedKey)) {
                instance.removeModifier(modifier);
            }
        }
    }

    public void refreshAllBuffs() {
        Government gov = plugin.getDataManager().getGovernment();

        // Refresh president buffs
        if (gov.hasPresident()) {
            Player president = Bukkit.getPlayer(gov.getPresidentUUID());
            if (president != null && president.isOnline()) {
                if (!presidentBuffsApplied.containsKey(president.getUniqueId())) {
                    applyPresidentBuffs(president);
                } else {
                    // Just refresh potion effects
                    applyPresidentPotionEffects(president);
                }
            }
        }

        // Refresh cabinet buffs
        for (Government.CabinetPosition position : Government.CabinetPosition.values()) {
            UUID memberId = gov.getCabinetMember(position);
            if (memberId != null) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    if (!cabinetBuffsApplied.containsKey(member.getUniqueId())) {
                        applyCabinetBuffs(member, position);
                    } else {
                        // Just refresh potion effects
                        applyCabinetPotionEffects(member, position);
                    }
                }
            }
        }
    }

    public void handlePlayerJoin(Player player) {
        Government gov = plugin.getDataManager().getGovernment();
        UUID playerId = player.getUniqueId();

        // Check if player is president
        if (gov.hasPresident() && gov.getPresidentUUID().equals(playerId)) {
            applyPresidentBuffs(player);
            MessageUtils.send(player, "<gold>🎖 Presidential buffs applied!");
            return;
        }

        // Check if player is cabinet member
        for (Government.CabinetPosition position : Government.CabinetPosition.values()) {
            UUID memberId = gov.getCabinetMember(position);
            if (memberId != null && memberId.equals(playerId)) {
                applyCabinetBuffs(player, position);
                MessageUtils.send(player, "<gold>🎖 Cabinet buffs applied!");
                return;
            }
        }
    }

    public void handlePlayerQuit(Player player) {
        UUID playerId = player.getUniqueId();

        // Clear tracking (buffs will be re-applied on join)
        presidentBuffsApplied.remove(playerId);
        cabinetBuffsApplied.remove(playerId);
    }

    public void removeAllBuffs(Player player) {
        removePresidentBuffs(player);
        removeCabinetBuffs(player);
    }

    public boolean hasPresidentBuffs(UUID playerId) {
        return presidentBuffsApplied.containsKey(playerId);
    }

    public boolean hasCabinetBuffs(UUID playerId) {
        return cabinetBuffsApplied.containsKey(playerId);
    }

    public Government.CabinetPosition getCabinetPosition(UUID playerId) {
        return cabinetBuffsApplied.get(playerId);
    }

    public String getPresidentBuffDescription() {
        double damageBonus = (plugin.getConfig().getDouble("president.buffs.damage-multiplier", 1.15) - 1.0) * 100;
        double defenseBonus = (plugin.getConfig().getDouble("president.buffs.defense-multiplier", 1.12) - 1.0) * 100;
        double extraHeartsBase = plugin.getConfig().getDouble("president.buffs.extra-hearts", 2.0);
        int extraHearts = (int) extraHeartsBase;
        double vaultBonus = (plugin.getConfig().getDouble("president.buffs.vault-multiplier", 1.20) - 1.0) * 100;
        double xpBonus = (plugin.getConfig().getDouble("president.buffs.xp-multiplier", 1.10) - 1.0) * 100;

        return String.format(
                "+" + "%.0f%% Damage, +%.0f%% Defense, +%d Hearts, +%.0f%% Vault, +%.0f%% XP, Night Vision, Hunger Immunity",
                damageBonus, defenseBonus, extraHearts, vaultBonus, xpBonus);
    }

    public String getCabinetBuffDescription(Government.CabinetPosition position) {
        return switch (position) {
            case DEFENSE -> "+10% Damage, +8% Defense, Strength I, Resistance I";
            case TREASURY -> "+15% Vault Bonus, Luck II";
            case COMMERCE -> "+10% Shop Discount, Speed I, Luck I";
            case INFRASTRUCTURE -> "Haste II, +50 Claim Blocks/day";
            case CULTURE -> "+10% XP Bonus, Glowing, Dolphin's Grace";
        };
    }
}
