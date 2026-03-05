package id.democracycore.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import id.democracycore.DemocracyCore;
import id.democracycore.models.CabinetDecision;
import id.democracycore.models.Election;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.Government;
import id.democracycore.models.PlayerData;
import id.democracycore.models.RecallPetition;
import id.democracycore.models.TaxRecord;
import id.democracycore.models.Treasury;
import id.democracycore.utils.MessageUtils;

/**
 * Main Menu GUI - Entry point for all DemocracyCore features
 * Makes it easier for players to recognize and access plugin features
 */
public class MainMenuGUI {

    private final DemocracyCore plugin;

    // GUI Titles - loaded from config
    public static String MAIN_MENU_TITLE;

    /**
     * Items that are handled by dedicated Java methods and should NOT be rendered
     * by the generic config loader. Any item key NOT in this set will be loaded
     * automatically from gui.yml at runtime — no Java code needed.
     */
    private static final java.util.Set<String> HARDCODED_ITEM_KEYS = java.util.Set.of(
            "player_head",
            "president_item",
            "cabinet_item",
            "treasury_item",
            "active_effects_item",
            "election_item",
            "executive_orders_item",
            "arena_item",
            "recall_item",
            "history_item",
            "my_stats_item",
            "leaderboard_item",
            "help_item",
            "register_candidate",
            "vote_now",
            "rate_president",
            "tax_item",
            "close_button",
            "glass_pane",
            "corner_decoration");

    public MainMenuGUI(DemocracyCore plugin) {
        this.plugin = plugin;
        loadGUITitles();
    }

    public static void loadGUITitles(DemocracyCore plugin) {
        MAIN_MENU_TITLE = plugin.getGUIConfig().getString("gui.main_menu.title", "§6§l⚜ DEMOCRACY CORE ⚜");

    }

    private void loadGUITitles() {
        loadGUITitles(plugin);
    }

    /**
     * Open Main Menu GUI for player
     */
    public void openMainMenu(Player player) {
        // Load custom inventory size (default 54, must be multiple of 9)
        int size = plugin.getGUIConfig().getInt("gui.main_menu.inventory_slot", 54);
        // Validate size to prevent errors
        if (size < 9 || size > 54 || size % 9 != 0) {
            size = 54;
        }

        Inventory inv = Bukkit.createInventory(null, size, MAIN_MENU_TITLE);

        Government gov = plugin.getDataManager().getGovernment();
        Election election = plugin.getDataManager().getElection();
        Treasury treasury = plugin.getDataManager().getTreasury();
        PlayerData playerData = plugin.getDataManager().getOrCreatePlayerData(player.getUniqueId(), player.getName());
        playerData.updateLastSeen();

        // Helper to safely set item
        final int finalSize = size;
        java.util.function.BiConsumer<Integer, ItemStack> setItemSafe = (slot, item) -> {
            if (slot >= 0 && slot < finalSize) {
                inv.setItem(slot, item);
            }
        };

        // === ROW 1: Header Info ===

        // Player Head (Info Player)
        if (plugin.getGUIConfig().contains("gui.main_menu.items.player_head")) {
            ItemStack playerHead = createPlayerHead(player, playerData);
            getGUISlots("gui.main_menu.items.player_head.gui_slot", 4).forEach(s -> setItemSafe.accept(s, playerHead));
        }

        // === ROW 2: Government Status ===

        // President Info
        if (plugin.getGUIConfig().contains("gui.main_menu.items.president_item")) {
            ItemStack presidentItem = createPresidentItem(gov, player);
            getGUISlots("gui.main_menu.items.president_item.gui_slot", 10)
                    .forEach(s -> setItemSafe.accept(s, presidentItem));
        }

        // Cabinet Info
        if (plugin.getGUIConfig().contains("gui.main_menu.items.cabinet_item")) {
            ItemStack cabinetItem = createCabinetItem(gov, player);
            getGUISlots("gui.main_menu.items.cabinet_item.gui_slot", 12)
                    .forEach(s -> setItemSafe.accept(s, cabinetItem));
        }

        // Treasury Info
        if (plugin.getGUIConfig().contains("gui.main_menu.items.treasury_item")) {
            ItemStack treasuryItem = createTreasuryItem(treasury, player);
            getGUISlots("gui.main_menu.items.treasury_item.gui_slot", 14)
                    .forEach(s -> setItemSafe.accept(s, treasuryItem));
        }

        // Active Effects
        if (plugin.getGUIConfig().contains("gui.main_menu.items.active_effects_item")) {
            ItemStack effectsItem = createActiveEffectsItem(player);
            getGUISlots("gui.main_menu.items.active_effects_item.gui_slot", 16)
                    .forEach(s -> setItemSafe.accept(s, effectsItem));
        }

        // === ROW 3: Main Features ===

        // Election
        if (plugin.getGUIConfig().contains("gui.main_menu.items.election_item")) {
            ItemStack electionItem = createElectionItem(election, player);
            getGUISlots("gui.main_menu.items.election_item.gui_slot", 19)
                    .forEach(s -> setItemSafe.accept(s, electionItem));
        }

        // Executive Orders
        if (plugin.getGUIConfig().contains("gui.main_menu.items.executive_orders_item")) {
            ItemStack ordersItem = createExecutiveOrdersItem(gov, player);
            getGUISlots("gui.main_menu.items.executive_orders_item.gui_slot", 21)
                    .forEach(s -> setItemSafe.accept(s, ordersItem));
        }

        // Presidential Arena
        if (plugin.getGUIConfig().contains("gui.main_menu.items.arena_item")) {
            ItemStack arenaItem = createArenaItem(playerData, player);
            getGUISlots("gui.main_menu.items.arena_item.gui_slot", 23).forEach(s -> setItemSafe.accept(s, arenaItem));
        }

        // Recall System
        if (plugin.getGUIConfig().contains("gui.main_menu.items.recall_item")) {
            ItemStack recallItem = createRecallItem(player);
            getGUISlots("gui.main_menu.items.recall_item.gui_slot", 25).forEach(s -> setItemSafe.accept(s, recallItem));
        }

        // === ROW 4: Info & Statistics ===

        // President History
        if (plugin.getGUIConfig().contains("gui.main_menu.items.history_item")) {
            ItemStack historyItem = createHistoryItem(player);
            getGUISlots("gui.main_menu.items.history_item.gui_slot", 28)
                    .forEach(s -> setItemSafe.accept(s, historyItem));
        }

        // My Stats
        if (plugin.getGUIConfig().contains("gui.main_menu.items.my_stats_item")) {
            ItemStack statsItem = createMyStatsItem(playerData, player);
            getGUISlots("gui.main_menu.items.my_stats_item.gui_slot", 30)
                    .forEach(s -> setItemSafe.accept(s, statsItem));
        }

        // Leaderboard
        if (plugin.getGUIConfig().contains("gui.main_menu.items.leaderboard_item")) {
            ItemStack leaderboardItem = createLeaderboardItem(player);
            getGUISlots("gui.main_menu.items.leaderboard_item.gui_slot", 32)
                    .forEach(s -> setItemSafe.accept(s, leaderboardItem));
        }

        // Guide/Help
        if (plugin.getGUIConfig().contains("gui.main_menu.items.help_item")) {
            ItemStack helpItem = createHelpItem(player);
            getGUISlots("gui.main_menu.items.help_item.gui_slot", 34).forEach(s -> setItemSafe.accept(s, helpItem));
        }

        // === ROW 5: Quick Actions (if eligible) ===

        // Register Candidate (if registration phase)
        if (election.getPhase() == Election.ElectionPhase.REGISTRATION) {
            String path = "gui.main_menu.items.register_candidate";
            if (!election.getCandidates().containsKey(player.getUniqueId()) && plugin.getGUIConfig().contains(path)) {

                int minLevel = plugin.getConfig().getInt("president.requirements.min-level", 100);
                double minPlaytime = plugin.getConfig().getDouble("president.requirements.min-playtime-hours", 100);
                double minBalance = plugin.getConfig().getDouble("president.requirements.min-vault-balance", 500000);
                double fee = plugin.getConfig().getDouble("election.registration-fee", 500000);

                int pLevel = player.getLevel();
                double pPlaytime = playerData.getPlaytimeHours();
                double pBalance = plugin.getVaultHook().getBalance(player.getUniqueId());

                String levelStatus = pLevel >= minLevel ? "§a✔" : "§c✗";
                String playStatus = pPlaytime >= minPlaytime ? "§a✔" : "§c✗";
                String balStatus = pBalance >= minBalance ? "§a✔" : "§c✗";

                // Formatted Requirement Strings
                String reqLevelStr = String.format("§7• Level: §f%d§7/§a%d %s", pLevel, minLevel, levelStatus);
                String reqPlaytimeStr = String.format("§7• Playtime: §f%.1fh§7/§a%.0fh %s", pPlaytime, minPlaytime,
                        playStatus);
                String reqBalanceStr = String.format("§7• Balance: §f%s§7/§a%s %s", MessageUtils.formatNumber(pBalance),
                        MessageUtils.formatNumber(minBalance), balStatus);

                // Create Item from Config
                Material material = getGUIMaterial(path + ".material");
                ItemStack registerItem = new ItemStack(material);
                ItemMeta meta = registerItem.getItemMeta();

                meta.setDisplayName(getGUIString(player, path + ".display_name"));

                List<String> lore = getGUILore(player, path + ".lore",
                        "req_level", reqLevelStr,
                        "req_playtime", reqPlaytimeStr,
                        "req_balance", reqBalanceStr,
                        "cost", MessageUtils.formatNumber(fee));

                meta.setLore(lore);
                applyConfigAttributes(meta, path);
                registerItem.setItemMeta(meta);

                getGUISlots(path + ".gui_slot", 38).forEach(s -> setItemSafe.accept(s, registerItem));
            }
        }

        // Vote (if voting phase and haven't voted)
        if (election.getPhase() == Election.ElectionPhase.VOTING) {
            String path = "gui.main_menu.items.vote_now";
            if (!election.hasVoted(player.getUniqueId()) && plugin.getGUIConfig().contains(path)) {
                Material material = getGUIMaterial(path + ".material");
                ItemStack voteItem = new ItemStack(material);
                ItemMeta meta = voteItem.getItemMeta();

                meta.setDisplayName(getGUIString(player, path + ".display_name"));
                meta.setLore(getGUILore(player, path + ".lore"));

                applyConfigAttributes(meta, path);
                voteItem.setItemMeta(meta);
                addGlow(voteItem);

                getGUISlots(path + ".gui_slot", 40).forEach(s -> setItemSafe.accept(s, voteItem));
            }
        }

        // Rate President (if there is a president)
        if (gov.hasPresident() && !gov.getPresidentUUID().equals(player.getUniqueId())) {
            String path = "gui.main_menu.items.rate_president";
            if (plugin.getGUIConfig().contains(path)) {
                Material material = getGUIMaterial(path + ".material");
                ItemStack rateItem = new ItemStack(material);
                ItemMeta meta = rateItem.getItemMeta();

                meta.setDisplayName(getGUIString(player, path + ".display_name"));

                List<String> lore = getGUILore(player, path + ".lore",
                        "current_rating", String.format("%.1f", gov.getApprovalRating()));

                meta.setLore(lore);
                applyConfigAttributes(meta, path);
                rateItem.setItemMeta(meta);

                getGUISlots(path + ".gui_slot", 42).forEach(s -> setItemSafe.accept(s, rateItem));
            }
        }

        // === Global Tax (Row 5) ===
        if (plugin.getGUIConfig().contains("gui.main_menu.items.tax_item")) {
            ItemStack taxItem = createTaxItem(player);
            getGUISlots("gui.main_menu.items.tax_item.gui_slot", 37).forEach(s -> setItemSafe.accept(s, taxItem));
        }

        // === ROW 6: Footer ===

        // Close Button
        if (plugin.getGUIConfig().contains("gui.main_menu.items.close_button")) {
            String path = "gui.main_menu.items.close_button";
            Material closeMat = getGUIMaterial(path + ".material");
            if (closeMat == Material.STONE && !plugin.getGUIConfig().contains(path + ".material")) {
                closeMat = Material.BARRIER;
            }
            String closeName = getGUIString(player, path + ".display_name");
            if (closeName.isEmpty())
                closeName = "§c§lClose Menu";

            List<String> closeLore = getGUILore(player, path + ".lore");
            if (closeLore.isEmpty()) {
                closeLore = new ArrayList<>();
                closeLore.add("§7Click to close");
            }

            ItemStack closeItem = createConfiguredItem(closeMat, closeName, path, closeLore.toArray(new String[0]));
            getGUISlots(path + ".gui_slot", 49).forEach(s -> setItemSafe.accept(s, closeItem));
        }

        // === GENERIC CONFIG ITEMS ===
        // Any extra item added to gui.yml under gui.main_menu.items that is NOT in
        // HARDCODED_ITEM_KEYS will be loaded and placed automatically here.
        loadGenericConfigItems(inv, player, size);

        // Fill empty slots with glass
        if (plugin.getGUIConfig().contains("gui.main_menu.items.glass_pane")) {
            fillGlass(inv, player);
        }

        // Decorative corners
        if (plugin.getGUIConfig().contains("gui.main_menu.items.corner_decoration")) {
            Material cornerMat = getGUIMaterial("gui.main_menu.items.corner_decoration.material");

            // Only set corners if not AIR
            if (cornerMat != Material.AIR) {
                String cornerName = getGUIString(player, "gui.main_menu.items.corner_decoration.display_name");
                ItemStack corner = createConfiguredItem(cornerMat, cornerName, "gui.main_menu.items.corner_decoration");

                int[] corners = { 0, 8, size - 9, size - 1 };
                for (int c : corners) {
                    setItemSafe.accept(c, corner);
                }
            }
        }

        player.openInventory(inv);
    }

    // === GUI Config Helper Methods ===

    private String getGUIString(Player player, String path, Object... args) {
        String value = plugin.getGUIConfig().getString(path, "");
        for (int i = 0; i < args.length; i += 2) {
            String placeholder = "{" + args[i] + "}";
            String replacement = String.valueOf(args[i + 1]);
            value = value.replace(placeholder, replacement);
        }
        if (player != null) {
            return processPlaceholders(value, player);
        }
        return value;
    }

    private List<String> getGUILore(Player player, String path, Object... args) {
        List<String> lore = plugin.getGUIConfig().getStringList(path);
        List<String> processed = new ArrayList<>();
        for (String line : lore) {
            for (int i = 0; i < args.length; i += 2) {
                String placeholder = "{" + args[i] + "}";
                String replacement = String.valueOf(args[i + 1]);
                line = line.replace(placeholder, replacement);
            }

            // Global List Expansion
            if (line.contains("{cabinet_members}")) {
                Government gov = plugin.getDataManager().getGovernment();
                for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
                    UUID minister = gov.getCabinetMember(pos.toGovernmentPosition());
                    if (minister != null) {
                        processed.add(
                                "§7" + pos.getDisplayName() + ": §f" + Bukkit.getOfflinePlayer(minister).getName());
                    } else {
                        processed.add("§7" + pos.getDisplayName() + ": §8Empty");
                    }
                }
                continue;
            }
            if (line.contains("{executive_orders}")) {
                var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
                for (ExecutiveOrder order : activeOrders) {
                    processed.add("§7- " + order.getType().getDisplayName());
                    processed.add("  §8(" + MessageUtils.formatTime(order.getRemainingTime()) + ")");
                }
                continue;
            }
            if (line.contains("{cabinet_decisions}")) {
                var activeDecisions = plugin.getDataManager().getActiveDecisions();
                for (var decision : activeDecisions) {
                    processed.add("§7- " + decision.getType().getDisplayName());
                    processed.add("  §8(" + MessageUtils.formatTime(decision.getRemainingTime()) + ")");
                }
                continue;
            }
            if (line.contains("{recent_presidents}")) {
                var history = plugin.getDataManager().getAllPresidentHistory();
                int show = Math.min(3, history.size());
                for (int i = 0; i < show; i++) {
                    var record = history.get(i);
                    String name = Bukkit.getOfflinePlayer(record.getPlayerId()).getName();
                    processed.add("§7- " + name + " §8(⭐" + String.format("%.1f", record.getApproval()) + ")");
                }
                continue;
            }

            if (player != null) {
                line = processPlaceholders(line, player);
            }
            processed.add(line);
        }
        return processed;
    }

    private Material getGUIMaterial(String path) {
        String matName = plugin.getGUIConfig().getString(path, "STONE");
        try {
            return Material.valueOf(matName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.STONE;
        }
    }

    private int getGUICustomModelData(String path) {
        return plugin.getGUIConfig().getInt(path, 0);
    }

    private int getGUISlot(String path, int defaultSlot) {
        List<Integer> slots = getGUISlots(path, defaultSlot);
        return slots.isEmpty() ? defaultSlot : slots.get(0);
    }

    private List<Integer> getGUISlots(String path, int defaultSlot) {
        List<Integer> slots = new ArrayList<>();
        if (plugin.getGUIConfig().isList(path)) {
            for (String s : plugin.getGUIConfig().getStringList(path)) {
                try {
                    slots.add(Integer.parseInt(s.trim()));
                } catch (Exception ignored) {
                }
            }
        } else if (plugin.getGUIConfig().isString(path)) {
            String val = plugin.getGUIConfig().getString(path);
            if (val != null) {
                for (String s : val.split(",")) {
                    try {
                        slots.add(Integer.parseInt(s.trim()));
                    } catch (Exception ignored) {
                    }
                }
            }
        } else if (plugin.getGUIConfig().isInt(path)) {
            slots.add(plugin.getGUIConfig().getInt(path));
        }
        if (slots.isEmpty() && defaultSlot >= 0) {
            slots.add(defaultSlot);
        }
        return slots;
    }

    public boolean isItemSlot(String itemKey, int slot, int defaultSlot) {
        return getGUISlots("gui.main_menu.items." + itemKey + ".gui_slot", defaultSlot).contains(slot);
    }

    private Sound getGUISound(String path) {
        String soundName = plugin.getGUIConfig().getString(path, "ui.button.click");
        try {
            return Sound.valueOf(soundName.toUpperCase().replace(".", "_"));
        } catch (IllegalArgumentException e) {
            return Sound.UI_BUTTON_CLICK;
        }
    }

    /**
     * Get the configured item flags to hide from gui.yml
     * 
     * @param path The path to hide_attributes in gui.yml
     * @return Array of ItemFlags to hide
     */
    private ItemFlag[] getGUIItemFlags(String path) {
        List<ItemFlag> flags = new ArrayList<>();

        // Check if the value is a string (single value or "ALL")
        if (plugin.getGUIConfig().isString(path)) {
            String value = plugin.getGUIConfig().getString(path, "").trim().toUpperCase();
            if (value.equals("ALL")) {
                // Return all item flags
                return ItemFlag.values();
            } else if (!value.isEmpty()) {
                // Try to parse single flag
                try {
                    flags.add(ItemFlag.valueOf("HIDE_" + value));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid hide_attributes value at " + path + ": " + value);
                }
            }
        }
        // Check if the value is a list
        else if (plugin.getGUIConfig().isList(path)) {
            List<String> values = plugin.getGUIConfig().getStringList(path);
            for (String value : values) {
                String upperValue = value.trim().toUpperCase();
                if (upperValue.equals("ALL")) {
                    // Return all item flags
                    return ItemFlag.values();
                }
                try {
                    flags.add(ItemFlag.valueOf("HIDE_" + upperValue));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid hide_attributes value at " + path + ": " + value);
                }
            }
        }

        return flags.toArray(new ItemFlag[0]);
    }

    // === Item Creation Methods ===

    private ItemStack createPlayerHead(Player player, PlayerData data) {
        Material material = getGUIMaterial("gui.main_menu.items.player_head.material");
        ItemStack head = new ItemStack(material);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(player);
        meta.setDisplayName(
                getGUIString(player, "gui.main_menu.items.player_head.display_name", "player_name", player.getName()));

        List<String> lore = getGUILore(player, "gui.main_menu.items.player_head.lore",
                "level", calculateLevel(data),
                "playtime", MessageUtils.formatTime(data.getTotalPlaytime()),
                "balance", MessageUtils.formatNumber(plugin.getVaultHook().getBalance(player.getUniqueId())),
                "status_lines", getStatusLines(player));

        meta.setLore(lore);
        applyConfigAttributes(meta, "gui.main_menu.items.player_head");
        head.setItemMeta(meta);

        return head;
    }

    private String getStatusLines(Player player) {
        Government gov = plugin.getDataManager().getGovernment();
        if (gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId())) {
            return "§6§l👑 PRESIDENT";
        } else {
            for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
                UUID minister = gov.getCabinetMember(pos.toGovernmentPosition());
                if (minister != null && minister.equals(player.getUniqueId())) {
                    return "§e§l🎖 " + pos.getDisplayName().toUpperCase();
                }
            }
        }
        return "";
    }

    private ItemStack createPresidentItem(Government gov, Player player) {
        ItemStack item;

        if (gov.hasPresident()) {
            Material material = getGUIMaterial("gui.main_menu.items.president_item.default.material");
            item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            String presName = Bukkit.getOfflinePlayer(gov.getPresidentUUID()).getName();
            meta.setDisplayName(getGUIString(player, "gui.main_menu.items.president_item.default.display_name"));

            List<String> lore = getGUILore(player, "gui.main_menu.items.president_item.default.lore",
                    "president_name", presName,
                    "term", gov.getCurrentTerm(),
                    "remaining_time", MessageUtils.formatTime(gov.getTermEndTime() - System.currentTimeMillis()),
                    "rating", String.format("%.1f", gov.getApprovalRating()));

            meta.setLore(lore);
            applyConfigAttributes(meta, "gui.main_menu.items.president_item.default");
            item.setItemMeta(meta);
            addGlow(item);
        } else {
            Material material = getGUIMaterial("gui.main_menu.items.president_item.no_president.material");
            item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(getGUIString(player, "gui.main_menu.items.president_item.no_president.display_name"));

            List<String> lore = getGUILore(player, "gui.main_menu.items.president_item.no_president.lore");

            meta.setLore(lore);
            applyConfigAttributes(meta, "gui.main_menu.items.president_item.no_president");
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createCabinetItem(Government gov, Player player) {
        String path = "gui.main_menu.items.cabinet_item";
        Material material = getGUIMaterial(path + ".material");
        if (material == Material.STONE)
            material = Material.LECTERN;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getGUIString(player, path + ".display_name"));

        int filled = 0;
        List<String> cabinetMembersList = new ArrayList<>();
        for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
            UUID minister = gov.getCabinetMember(pos.toGovernmentPosition());
            if (minister != null) {
                String name = Bukkit.getOfflinePlayer(minister).getName();
                cabinetMembersList.add("§7" + pos.getDisplayName() + ": §f" + name);
                filled++;
            } else {
                cabinetMembersList.add("§7" + pos.getDisplayName() + ": §8Empty");
            }
        }

        List<String> configLore = plugin.getGUIConfig().getStringList(path + ".lore");
        List<String> lore = new ArrayList<>();
        if (configLore.isEmpty()) {
            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            lore.addAll(cabinetMembersList);
            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            lore.add("§7Filled: §f" + filled + "/5");
            lore.add("");
            lore.add("§aClick for cabinet details");
        } else {
            for (String line : configLore) {
                if (line.contains("{cabinet_members}")) {
                    lore.addAll(cabinetMembersList);
                } else {
                    line = line.replace("{filled}", String.valueOf(filled));
                    line = line.replace("{total}", "5");
                    if (player != null) {
                        line = processPlaceholders(line, player);
                    }
                    lore.add(line);
                }
            }
        }

        meta.setLore(lore);
        applyConfigAttributes(meta, path);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createTreasuryItem(Treasury treasury, Player player) {
        String path = "gui.main_menu.items.treasury_item";
        Material material = getGUIMaterial(path + ".material");
        if (material == Material.STONE && !plugin.getGUIConfig().contains(path + ".material"))
            material = Material.GOLD_BLOCK;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String displayName = getGUIString(player, path + ".display_name");
        if (displayName.isEmpty())
            displayName = "§6§l💰 TREASURY";
        meta.setDisplayName(displayName);

        List<String> configLore = plugin.getGUIConfig().getStringList(path + ".lore");
        List<String> lore;
        if (!configLore.isEmpty()) {
            lore = getGUILore(player, path + ".lore",
                    "balance", MessageUtils.formatNumber(treasury.getBalance()),
                    "income", MessageUtils.formatNumber(treasury.getTotalIncome()),
                    "expenses", MessageUtils.formatNumber(treasury.getTotalExpenses()));
        } else {
            lore = new ArrayList<>();
            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            lore.add("§7Balance: §6" + MessageUtils.formatNumber(treasury.getBalance()));
            lore.add("§7Income: §a+" + MessageUtils.formatNumber(treasury.getTotalIncome()));
            lore.add("§7Expenses: §c-" + MessageUtils.formatNumber(treasury.getTotalExpenses()));
            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            lore.add("§aClick for treasury details");
        }

        meta.setLore(lore);
        applyConfigAttributes(meta, path);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createActiveEffectsItem(Player player) {
        var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
        var activeDecisions = plugin.getDataManager().getActiveDecisions();
        int total = activeOrders.size() + activeDecisions.size();

        String state = total > 0 ? "active" : "inactive";
        String path = "gui.main_menu.items.active_effects_item." + state;

        Material mat = getGUIMaterial(path + ".material");
        // Fallback if config is missing material (though getGUIMaterial defaults to
        // STONE)
        if (mat == Material.STONE && !plugin.getGUIConfig().contains(path + ".material")) {
            mat = total > 0 ? Material.BEACON : Material.GLASS;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        // Get Display Name from config
        String displayName = getGUIString(player, path + ".display_name", "total", total);
        if (displayName.isEmpty()) {
            displayName = "§a§l✨ ACTIVE EFFECTS: " + total;
        }
        meta.setDisplayName(displayName);

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        if (total == 0) {
            lore.add("§7No active effects");
        } else {
            if (!activeOrders.isEmpty()) {
                lore.add("§c§lExecutive Orders:");
                for (ExecutiveOrder order : activeOrders) {
                    lore.add("§7- " + order.getType().getDisplayName());
                    long remaining = order.getRemainingTime();
                    lore.add("  §8(" + MessageUtils.formatTime(remaining) + ")");
                }
            }

            if (!activeDecisions.isEmpty()) {
                if (!activeOrders.isEmpty())
                    lore.add("");
                lore.add("§e§lCabinet Decisions:");
                for (var decision : activeDecisions) {
                    lore.add("§7- " + decision.getType().getDisplayName());
                    long remaining = decision.getRemainingTime();
                    lore.add("  §8(" + MessageUtils.formatTime(remaining) + ")");
                }
            }
        }

        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        meta.setLore(lore);

        // Apply attributes from the correct path (active/inactive)
        applyConfigAttributes(meta, path);

        item.setItemMeta(meta);

        if (total > 0)
            addGlow(item);

        return item;
    }

    private ItemStack createElectionItem(Election election, Player player) {
        String path = "gui.main_menu.items.election_item";
        Material material = getGUIMaterial(path + ".material");
        if (material == Material.STONE)
            material = Material.PAPER;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getGUIString(player, path + ".display_name"));

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Phase: §f" + election.getPhase().getDisplayName());

        if (election.isActive()) {
            lore.add("§7Time remaining: §f"
                    + MessageUtils.formatTime(election.getPhaseEndTime() - System.currentTimeMillis()));
            lore.add("§7Candidates: §f" + election.getCandidates().size());
            lore.add("§7Total votes: §f" + election.getTotalVotes());
            lore.add("");

            if (election.getPhase() == Election.ElectionPhase.VOTING) {
                if (election.hasVoted(player.getUniqueId())) {
                    lore.add("§a✓ You have voted");
                } else {
                    lore.add("§c✗ You have not voted!");
                }
            } else if (election.getPhase() == Election.ElectionPhase.REGISTRATION) {
                if (election.getCandidates().containsKey(player.getUniqueId())) {
                    lore.add("§a✓ You are a registered candidate");
                } else {
                    lore.add("§e→ Register as candidate!");
                }
            }
        } else {
            lore.add("§7No active election");
        }

        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aClick to open voting GUI");

        meta.setLore(lore);
        applyConfigAttributes(meta, path);
        item.setItemMeta(meta);

        if (election.isActive() && election.getPhase() == Election.ElectionPhase.VOTING
                && !election.hasVoted(player.getUniqueId())) {
            addGlow(item);
        }

        return item;
    }

    private ItemStack createExecutiveOrdersItem(Government gov, Player player) {
        String path = "gui.main_menu.items.executive_orders_item";
        Material material = getGUIMaterial(path + ".material");
        if (material == Material.STONE)
            material = Material.WRITABLE_BOOK;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getGUIString(player, path + ".display_name"));

        var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
        boolean isPresident = gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId());

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Active: §f" + activeOrders.size() + " orders");
        lore.add("§7Cost: §6" + MessageUtils.formatNumber(1000000L));
        lore.add("§7Cooldown: §f7 days");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        if (isPresident) {
            lore.add("§a→ You can issue orders!");
        } else {
            lore.add("§7Only the President can");
            lore.add("§7issue Executive Orders");
        }

        lore.add("");
        lore.add("§aClick to view orders");

        meta.setLore(lore);
        applyConfigAttributes(meta, path);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createArenaItem(PlayerData data, Player player) {
        boolean isActive = plugin.getArenaManager().isArenaActive();
        String statePath = isActive ? "active" : "inactive";
        String path = "gui.main_menu.items.arena_item." + statePath;

        Material mat = getGUIMaterial(path + ".material");
        // Fallback if config missing
        if (mat == Material.STONE && !plugin.getGUIConfig().contains(path + ".material")) {
            mat = isActive ? Material.DIAMOND_SWORD : Material.IRON_SWORD;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        String displayName = getGUIString(player, path + ".display_name");
        if (displayName.isEmpty())
            displayName = "§4§l⚔ PRESIDENTIAL ARENA";
        meta.setDisplayName(displayName);

        applyConfigAttributes(meta, path);

        List<String> configLore = plugin.getGUIConfig().getStringList(path + ".lore");
        List<String> lore = new ArrayList<>();

        if (!configLore.isEmpty()) {
            lore = getGUILore(player, path + ".lore",
                    "kills", data.getArenaKills(),
                    "deaths", data.getArenaDeaths(),
                    "streak", data.getBestKillstreak(),
                    "players", plugin.getArenaManager().getArenaPlayers().size());
        } else {
            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

            if (isActive) {
                lore.add("§a§lACTIVE SESSION!");
                lore.add("§7Players in arena: §f" + plugin.getArenaManager().getArenaPlayers().size());
            } else {
                lore.add("§7Status: §cInactive");
            }

            lore.add("");
            lore.add("§7§lYour Statistics:");
            lore.add("§7Kills: §a" + data.getArenaKills());
            lore.add("§7Deaths: §c" + data.getArenaDeaths());
            lore.add("§7Best Streak: §6" + data.getBestKillstreak());
            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            lore.add("§aClick for arena info");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        if (isActive)
            addGlow(item);

        return item;
    }

    private ItemStack createRecallItem(Player player) {
        RecallPetition petition = plugin.getDataManager().getRecallPetition();
        boolean isActive = petition != null &&
                petition.getPhase() != RecallPetition.RecallPhase.COMPLETED &&
                petition.getPhase() != RecallPetition.RecallPhase.FAILED;

        String statePath = isActive ? "active" : "inactive";
        String path = "gui.main_menu.items.recall_item." + statePath;

        Material mat = getGUIMaterial(path + ".material");
        // Fallback if config missing
        if (mat == Material.STONE && !plugin.getGUIConfig().contains(path + ".material")) {
            mat = isActive ? Material.REDSTONE_TORCH : Material.TORCH;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        String displayName = getGUIString(player, path + ".display_name");
        if (displayName.isEmpty())
            displayName = "§c§l⚠ RECALL SYSTEM";
        meta.setDisplayName(displayName);

        double dep = plugin.getConfig().getDouble("recall.deposit-amount", 50000);
        double threshOnline = plugin.getConfig().getDouble("recall.signatures.threshold", 0.3);
        String depositStr = MessageUtils.formatNumber(dep);
        String thresholdStr = Math.round(threshOnline * 100) + "% online";

        List<String> configLore = plugin.getGUIConfig().getStringList(path + ".lore");
        List<String> lore = new ArrayList<>();

        if (!configLore.isEmpty()) {
            for (String line : configLore) {
                line = line.replace("{deposit}", depositStr);
                line = line.replace("{threshold}", thresholdStr);
                if (isActive) {
                    line = line.replace("{phase}", petition.getPhase().name());
                    line = line.replace("{signatures}", String.valueOf(petition.getSignatureCount()));

                    if (line.contains("{voting_details}")) {
                        if (petition.getPhase() == RecallPetition.RecallPhase.VOTING) {
                            lore.add("§7Vote Remove: §c" + petition.getRemoveVotes());
                            lore.add("§7Vote Keep: §a" + petition.getKeepVotes());
                        }
                        continue;
                    }
                }
                if (player != null) {
                    line = processPlaceholders(line, player);
                }
                lore.add(line);
            }
        } else {
            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            if (isActive) {
                lore.add("§c§lACTIVE PETITION!");
                lore.add("§7Phase: §f" + petition.getPhase().name());
                lore.add("§7Signatures: §f" + petition.getSignatureCount());

                if (petition.getPhase() == RecallPetition.RecallPhase.VOTING) {
                    lore.add("§7Vote Remove: §c" + petition.getRemoveVotes());
                    lore.add("§7Vote Keep: §a" + petition.getKeepVotes());
                }
            } else {
                lore.add("§7No active petition");
                lore.add("");
                lore.add("§7Start petition to");
                lore.add("§7recall president");
            }

            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            lore.add("§7Deposit: §6" + depositStr);
            lore.add("§7Threshold: §f" + thresholdStr);
            lore.add("");
            lore.add("§eClick for recall info");
        }

        meta.setLore(lore);
        applyConfigAttributes(meta, path);

        item.setItemMeta(meta);

        if (isActive)
            addGlow(item);

        return item;
    }

    private ItemStack createHistoryItem(Player player) {
        var history = plugin.getDataManager().getAllPresidentHistory();

        String path = "gui.main_menu.items.history_item";
        Material material = getGUIMaterial(path + ".material");
        if (material == Material.STONE)
            material = Material.BOOK;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getGUIString(player, path + ".display_name"));

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Total presidents: §f" + history.size());

        if (!history.isEmpty()) {
            lore.add("");
            lore.add("§7§lLast President:");
            int show = Math.min(3, history.size());
            for (int i = 0; i < show; i++) {
                var record = history.get(i);
                String name = Bukkit.getOfflinePlayer(record.getPlayerId()).getName();
                lore.add("§7- " + name + " §8(⭐" + String.format("%.1f", record.getApproval()) + ")");
            }
        }

        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aClick to view history");

        meta.setLore(lore);
        applyConfigAttributes(meta, path);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createMyStatsItem(PlayerData data, Player player) {
        String path = "gui.main_menu.items.my_stats_item";
        Material material = getGUIMaterial(path + ".material");
        if (material == Material.STONE)
            material = Material.COMPASS;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getGUIString(player, path + ".display_name"));

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Votes Cast: §f" + data.getTotalVotesCast());
        lore.add("§7Endorsements: §f" + data.getEndorsementsGiven());
        lore.add("§7Served as President: §f" + data.getTimesServedAsPresident() + "x");
        lore.add("§7Served as Minister: §f" + data.getTimesServedAsCabinet() + "x");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aClick for full details");

        meta.setLore(lore);
        applyConfigAttributes(meta, path);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createLeaderboardItem(Player player) {
        String path = "gui.main_menu.items.leaderboard_item";
        Material material = getGUIMaterial(path + ".material");
        if (material == Material.STONE && !plugin.getGUIConfig().contains(path + ".material"))
            material = Material.GOLDEN_APPLE;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String displayName = getGUIString(player, path + ".display_name");
        if (displayName.isEmpty())
            displayName = "§6§l🏆 LEADERBOARD";
        meta.setDisplayName(displayName);

        List<String> configLore = plugin.getGUIConfig().getStringList(path + ".lore");
        List<String> lore;
        if (!configLore.isEmpty()) {
            lore = getGUILore(player, path + ".lore");
        } else {
            lore = new ArrayList<>();
            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            lore.add("§7View top players:");
            lore.add("§7- Arena Kills");
            lore.add("§7- Playtime");
            lore.add("§7- Best President");
            lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            lore.add("§aClick to view leaderboard");
        }

        meta.setLore(lore);
        applyConfigAttributes(meta, path);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createHelpItem(Player player) {
        String path = "gui.main_menu.items.help_item";
        Material material = getGUIMaterial(path + ".material");
        if (material == Material.STONE)
            material = Material.KNOWLEDGE_BOOK;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getGUIString(player, path + ".display_name"));

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Learn how to play:");
        lore.add("§7- Election System");
        lore.add("§7- Becoming President");
        lore.add("§7- Cabinet & Ministers");
        lore.add("§7- Executive Orders");
        lore.add("§7- And more!");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aClick to open guide");

        meta.setLore(lore);
        applyConfigAttributes(meta, path);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createTaxItem(Player player) {
        String path = "gui.main_menu.items.tax_item";
        Material material = getGUIMaterial(path + ".material");
        if (material == Material.STONE)
            material = Material.SUNFLOWER;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(getGUIString(player, path + ".display_name"));

        // Build tax info
        double taxAmount = plugin.getTaxManager().getTaxAmount();
        long remaining = plugin.getTaxManager().getTimeUntilNextCollection();
        String nextCollection = remaining > 0 ? MessageUtils.formatTime(remaining) : "Pending...";

        String uuidStr = player.getUniqueId().toString();
        TaxRecord record = plugin.getTaxManager().getTaxRecord();
        TaxRecord.PlayerTaxData taxData = record.getPlayerTaxData(uuidStr);
        String debtStatus;
        if (taxData != null && taxData.getOutstandingDebt() > 0) {
            debtStatus = "\u00a7c$" + MessageUtils.formatNumber(taxData.getOutstandingDebt());
        } else {
            debtStatus = "\u00a7a$0";
        }

        List<String> lore = getGUILore(player, path + ".lore",
                "tax_amount", "$" + MessageUtils.formatNumber(taxAmount),
                "next_collection", nextCollection,
                "debt_status", debtStatus);

        meta.setLore(lore);
        applyConfigAttributes(meta, path);
        item.setItemMeta(meta);

        // Glow if player has debt
        if (taxData != null && taxData.getOutstandingDebt() > 0) {
            addGlow(item);
        }

        return item;
    }

    // === Generic Config Item Loader ===

    /**
     * Scans ALL items under gui.main_menu.items in gui.yml and places any item
     * that:
     * 1) Is NOT in HARDCODED_ITEM_KEYS (those are handled by dedicated methods),
     * AND
     * 2) Has a direct "gui_slot" field (not a sub-state parent like
     * president_item), AND
     * 3) The target slot is currently empty.
     *
     * This means you can add any new custom item to gui.yml and it will appear in
     * the menu automatically — no Java code required.
     */
    private void loadGenericConfigItems(Inventory inv, Player player, int inventorySize) {
        org.bukkit.configuration.ConfigurationSection itemsSection = plugin.getGUIConfig()
                .getConfigurationSection("gui.main_menu.items");
        if (itemsSection == null)
            return;

        for (String key : itemsSection.getKeys(false)) {
            // Skip items that have dedicated Java rendering logic
            if (HARDCODED_ITEM_KEYS.contains(key))
                continue;

            String basePath = "gui.main_menu.items." + key;

            // Only process items that declare gui_slot at the top level
            if (!plugin.getGUIConfig().contains(basePath + ".gui_slot"))
                continue;

            List<Integer> slots = getGUISlots(basePath + ".gui_slot", -1);
            for (int slot : slots) {
                if (slot < 0 || slot >= inventorySize)
                    continue;

                // Don't overwrite slots already filled by hardcoded items
                ItemStack existing = inv.getItem(slot);
                if (existing != null && existing.getType() != Material.AIR)
                    continue;

                // Build the item purely from config
                ItemStack item = buildItemFromConfig(player, basePath);
                if (item != null) {
                    inv.setItem(slot, item);
                }
            }
        }
    }

    /**
     * Builds an ItemStack entirely from a gui.yml config path.
     * Supports: material, display_name, lore, custom_model_data, hide_attributes.
     * Placeholders in display_name and lore are also processed.
     *
     * @param player   The player (for placeholder processing)
     * @param basePath Config path, e.g. "gui.main_menu.items.my_custom_item"
     * @return Configured ItemStack, or null if material is AIR or invalid
     */
    private ItemStack buildItemFromConfig(Player player, String basePath) {
        String matName = plugin.getGUIConfig().getString(basePath + ".material", "STONE");
        Material material;
        try {
            material = Material.valueOf(matName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("[GUI] Invalid material '" + matName + "' at " + basePath);
            material = Material.STONE;
        }
        if (material == Material.AIR)
            return null;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return item;

        // Display name
        String displayName = getGUIString(player, basePath + ".display_name");
        if (!displayName.isEmpty()) {
            meta.setDisplayName(displayName);
        }

        // Lore
        List<String> lore = getGUILore(player, basePath + ".lore");
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }

        // custom_model_data & hide_attributes
        applyConfigAttributes(meta, basePath);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Returns the item key in gui.yml for the given slot, if it is a generic
     * (non-hardcoded) item. Used by GUIListener to dispatch click actions.
     *
     * @param slot Inventory slot that was clicked
     * @return The item key (e.g. "my_custom_item") or null if not a generic item
     */
    public String getGenericItemKeyForSlot(int slot) {
        org.bukkit.configuration.ConfigurationSection itemsSection = plugin.getGUIConfig()
                .getConfigurationSection("gui.main_menu.items");
        if (itemsSection == null)
            return null;

        for (String key : itemsSection.getKeys(false)) {
            if (HARDCODED_ITEM_KEYS.contains(key))
                continue;
            String basePath = "gui.main_menu.items." + key;
            if (!plugin.getGUIConfig().contains(basePath + ".gui_slot"))
                continue;
            if (getGUISlots(basePath + ".gui_slot", -1).contains(slot))
                return key;
        }
        return null;
    }

    // === Utility Methods ===

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create an item with configuration support for custom_model_data and
     * hide_attributes
     * 
     * @param material   Material type
     * @param name       Display name
     * @param configPath Path to item config in gui.yml (e.g.,
     *                   "gui.main_menu.items.player_head")
     * @param lore       Lore lines
     * @return Configured ItemStack
     */
    private ItemStack createConfiguredItem(Material material, String name, String configPath, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }

        // Apply configured attributes
        if (configPath != null && !configPath.isEmpty()) {
            applyConfigAttributes(meta, configPath);
        }

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createQuickItem(Material material, String name, String... lore) {
        ItemStack item = createItem(material, name, lore);
        return item;
    }

    private void addGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    /**
     * Apply common config attributes (hide_attributes, custom_model_data) to
     * ItemMeta
     */
    private void applyConfigAttributes(ItemMeta meta, String path) {
        if (path == null || path.isEmpty())
            return;

        // Custom Model Data
        int modelData = getGUICustomModelData(path + ".custom_model_data");
        if (modelData != 0) {
            meta.setCustomModelData(modelData);
        }

        // Hide Attributes
        ItemFlag[] hideFlags = getGUIItemFlags(path + ".hide_attributes");
        if (hideFlags.length > 0) {
            meta.addItemFlags(hideFlags);
        }
    }

    private void fillGlass(Inventory inv, Player player) {
        Material material = getGUIMaterial("gui.main_menu.items.glass_pane.material");

        // If AIR, don't fill anything
        if (material == Material.AIR) {
            return;
        }

        String name = getGUIString(player, "gui.main_menu.items.glass_pane.display_name");
        ItemStack glass = createConfiguredItem(material, name, "gui.main_menu.items.glass_pane");

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                inv.setItem(i, glass);
            }
        }
    }

    private int calculateLevel(PlayerData data) {
        double hours = data.getTotalPlaytime() / (1000.0 * 60 * 60);
        return (int) (hours / 10) + 1;
    }

    /**
     * Check if an item is defined in the configuration.
     * 
     * @param itemKey The item key in gui.yml
     * @return true if defined, false otherwise
     */
    public boolean hasItem(String itemKey) {
        return plugin.getGUIConfig().contains("gui.main_menu.items." + itemKey);
    }

    /**
     * Play click sound for specific GUI slot based on gui.yml configuration.
     * Slot-to-sound mapping is built dynamically from the config, so custom
     * gui_slot and click_sound values are always respected.
     */
    public void playClickSound(Player player, int slot) {
        // Build slot → sound-path mapping from config at runtime
        String[][] slotItems = {
                { "player_head", "4" },
                { "president_item.default", "10" }, // sound paths have sub-states
                { "president_item.no_president", "10" },
                { "cabinet_item", "12" },
                { "treasury_item", "14" },
                { "active_effects_item.active", "16" },
                { "active_effects_item.inactive", "16" },
                { "election_item", "19" },
                { "executive_orders_item", "21" },
                { "tax_item", "22" },
                { "arena_item.active", "23" },
                { "arena_item.inactive", "23" },
                { "recall_item.active", "25" },
                { "recall_item.inactive", "25" },
                { "history_item", "28" },
                { "my_stats_item", "30" },
                { "leaderboard_item", "32" },
                { "help_item", "34" },
                { "register_candidate", "38" },
                { "vote_now", "40" },
                { "rate_president", "42" },
                { "close_button", "49" },
        };

        for (String[] entry : slotItems) {
            String itemKey = entry[0];
            int defaultSlot = Integer.parseInt(entry[1]);

            // The slot key may have a sub-state (e.g. "president_item.default")
            // Strip the sub-state for the gui_slot lookup (gui_slot is on the parent key)
            String slotKey = itemKey.contains(".") ? itemKey.substring(0, itemKey.indexOf('.')) : itemKey;

            if (!hasItem(slotKey))
                continue;

            int configuredSlot = getGUISlot("gui.main_menu.items." + slotKey + ".gui_slot", defaultSlot);

            if (slot == configuredSlot) {
                String soundPath = "gui.main_menu.items." + itemKey + ".click_sound";
                Sound sound = getGUISound(soundPath);
                MessageUtils.playSound(player, sound);
                return;
            }
        }

        // Fallback: check generic config-driven items for a click_sound
        String genericKey = getGenericItemKeyForSlot(slot);
        if (genericKey != null) {
            String soundPath = "gui.main_menu.items." + genericKey + ".click_sound";
            if (plugin.getGUIConfig().contains(soundPath)) {
                Sound sound = getGUISound(soundPath);
                MessageUtils.playSound(player, sound);
            }
        }
    }

    /**
     * Get the configured slot for a specific item
     * 
     * @param itemKey     The item key in gui.yml (e.g., "player_head",
     *                    "president_item")
     * @param defaultSlot The default slot if not configured
     * @return The configured slot number
     */
    public int getItemSlot(String itemKey, int defaultSlot) {
        return getGUISlot("gui.main_menu.items." + itemKey + ".gui_slot", defaultSlot);
    }

    /**
     * Get the configured GUI action for a specific item
     * 
     * @param itemKey The item key in gui.yml (e.g., "player_head",
     *                "president_item")
     * @param state   Optional state (e.g., "default", "no_president", "active",
     *                "inactive")
     * @return The GUIAction enum value, or UNKNOWN if not configured
     */
    public GUIAction getGUIAction(String itemKey, String state) {
        String path = "gui.main_menu.items." + itemKey;
        if (state != null && !state.isEmpty()) {
            path += "." + state;
        }
        path += ".on_click";

        String actionStr = plugin.getGUIConfig().getString(path, "");
        return GUIAction.fromConfig(actionStr);
    }

    /**
     * Get the configured GUI action for a specific item without state
     */
    public GUIAction getGUIAction(String itemKey) {
        return getGUIAction(itemKey, null);
    }

    /**
     * Get the command value for action_console_command
     * 
     * @param itemKey The item key in gui.yml
     * @param state   Optional state (e.g., "default", "active", etc.)
     * @return The command string, or null if not configured
     */
    public String getGUICommandValue(String itemKey, String state) {
        String path = "gui.main_menu.items." + itemKey;
        if (state != null && !state.isEmpty()) {
            path += "." + state;
        }
        path += ".value";

        return plugin.getGUIConfig().getString(path, null);
    }

    /**
     * Get the command value without state
     */
    public String getGUICommandValue(String itemKey) {
        return getGUICommandValue(itemKey, null);
    }

    /**
     * Process placeholders in command string
     * 
     * @param command Command string with placeholders
     * @param player  Player to replace placeholders for
     * @return Processed command string
     */
    public String processPlaceholders(String command, Player player) {
        if (command == null || command.isEmpty()) {
            return command;
        }

        // Replace %player_name% with actual player name
        command = command.replace("%player_name%", player.getName());
        command = command.replace("%player%", player.getName());
        command = command.replace("%player_uuid%", player.getUniqueId().toString());

        command = replaceGlobalPlaceholders(command, player);

        // PlaceholderAPI support (if available)
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            command = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, command);
        }

        return command;
    }

    private String replaceGlobalPlaceholders(String text, Player player) {
        if (text == null || text.isEmpty())
            return text;

        if (text.contains("{player_name}"))
            text = text.replace("{player_name}", player.getName());

        if (text.contains("{level}") || text.contains("{playtime}") || text.contains("{kills}")
                || text.contains("{deaths}") || text.contains("{streak}") || text.contains("{votes_cast}")
                || text.contains("{endorsements}") || text.contains("{president_times}")
                || text.contains("{minister_times}")) {
            PlayerData data = plugin.getDataManager().getOrCreatePlayerData(player.getUniqueId(), player.getName());
            if (text.contains("{level}"))
                text = text.replace("{level}", String.valueOf(calculateLevel(data)));
            if (text.contains("{playtime}"))
                text = text.replace("{playtime}", MessageUtils.formatTime(data.getTotalPlaytime()));
            if (text.contains("{kills}"))
                text = text.replace("{kills}", String.valueOf(data.getArenaKills()));
            if (text.contains("{deaths}"))
                text = text.replace("{deaths}", String.valueOf(data.getArenaDeaths()));
            if (text.contains("{streak}"))
                text = text.replace("{streak}", String.valueOf(data.getBestKillstreak()));
            if (text.contains("{votes_cast}"))
                text = text.replace("{votes_cast}", String.valueOf(data.getTotalVotesCast()));
            if (text.contains("{endorsements}"))
                text = text.replace("{endorsements}", String.valueOf(data.getEndorsementsGiven()));
            if (text.contains("{president_times}"))
                text = text.replace("{president_times}", String.valueOf(data.getTimesServedAsPresident()));
            if (text.contains("{minister_times}"))
                text = text.replace("{minister_times}", String.valueOf(data.getTimesServedAsCabinet()));
        }

        if (text.contains("{balance}")) {
            text = text.replace("{balance}",
                    MessageUtils.formatNumber(plugin.getVaultHook().getBalance(player.getUniqueId())));
        }
        if (text.contains("{status_lines}") && player != null) {
            text = text.replace("{status_lines}", getStatusLines(player));
        }

        Government gov = plugin.getDataManager().getGovernment();
        if (text.contains("{president_name}")) {
            String pName = gov.hasPresident() ? Bukkit.getOfflinePlayer(gov.getPresidentUUID()).getName()
                    : "No President";
            text = text.replace("{president_name}", pName != null ? pName : "No President");
        }
        if (text.contains("{term}"))
            text = text.replace("{term}", String.valueOf(gov.getCurrentTerm()));
        if (text.contains("{remaining_time}")) {
            String rt = gov.hasPresident() ? MessageUtils.formatTime(gov.getTermEndTime() - System.currentTimeMillis())
                    : "0s";
            text = text.replace("{remaining_time}", rt);
        }
        if (text.contains("{rating}") || text.contains("{current_rating}")) {
            String rtStr = gov.hasPresident() ? String.format("%.1f", gov.getApprovalRating()) : "0.0";
            text = text.replace("{rating}", rtStr).replace("{current_rating}", rtStr);
        }
        if (text.contains("{filled}")) {
            int filled = 0;
            for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
                if (gov.getCabinetMember(pos.toGovernmentPosition()) != null)
                    filled++;
            }
            text = text.replace("{filled}", String.valueOf(filled));
        }

        Election election = plugin.getDataManager().getElection();
        if (text.contains("{phase}"))
            text = text.replace("{phase}", election.getPhase().getDisplayName());
        if (text.contains("{candidates}"))
            text = text.replace("{candidates}", String.valueOf(election.getCandidates().size()));
        if (text.contains("{votes}"))
            text = text.replace("{votes}", String.valueOf(election.getTotalVotes()));

        if (text.contains("{income}"))
            text = text.replace("{income}",
                    MessageUtils.formatNumber(plugin.getDataManager().getTreasury().getTotalIncome()));
        if (text.contains("{expenses}"))
            text = text.replace("{expenses}",
                    MessageUtils.formatNumber(plugin.getDataManager().getTreasury().getTotalExpenses()));

        if (text.contains("{players}"))
            text = text.replace("{players}", String.valueOf(plugin.getArenaManager().getArenaPlayers().size()));

        RecallPetition petition = plugin.getDataManager().getRecallPetition();
        if (text.contains("{signatures}"))
            text = text.replace("{signatures}", String.valueOf(petition != null ? petition.getSignatureCount() : 0));

        return text;
    }
}
