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
    public static String QUICK_ACTIONS_TITLE;

    public MainMenuGUI(DemocracyCore plugin) {
        this.plugin = plugin;
        loadGUITitles();
    }

    public static void loadGUITitles(DemocracyCore plugin) {
        MAIN_MENU_TITLE = plugin.getGUIConfig().getString("gui.main_menu.title", "§6§l⚜ DEMOCRACY CORE ⚜");
        QUICK_ACTIONS_TITLE = plugin.getGUIConfig().getString("gui.quick_actions.title", "§e§l⚡ QUICK ACTIONS ⚡");
    }

    private void loadGUITitles() {
        loadGUITitles(plugin);
    }

    /**
     * Open Main Menu GUI for player
     */
    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, MAIN_MENU_TITLE);

        Government gov = plugin.getDataManager().getGovernment();
        Election election = plugin.getDataManager().getElection();
        Treasury treasury = plugin.getDataManager().getTreasury();
        PlayerData playerData = plugin.getDataManager().getOrCreatePlayerData(player.getUniqueId(), player.getName());
        playerData.updateLastSeen();

        // === ROW 1: Header Info ===

        // Player Head (Info Player)
        ItemStack playerHead = createPlayerHead(player, playerData);
        int playerHeadSlot = getGUISlot("gui.main_menu.items.player_head.gui_slot", 4);
        inv.setItem(playerHeadSlot, playerHead);

        // === ROW 2: Government Status ===

        // President Info
        ItemStack presidentItem = createPresidentItem(gov, player);
        int presidentSlot = getGUISlot("gui.main_menu.items.president_item.gui_slot", 10);
        inv.setItem(presidentSlot, presidentItem);

        // Cabinet Info
        ItemStack cabinetItem = createCabinetItem(gov, player);
        int cabinetSlot = getGUISlot("gui.main_menu.items.cabinet_item.gui_slot", 12);
        inv.setItem(cabinetSlot, cabinetItem);

        // Treasury Info
        ItemStack treasuryItem = createTreasuryItem(treasury, player);
        int treasurySlot = getGUISlot("gui.main_menu.items.treasury_item.gui_slot", 14);
        inv.setItem(treasurySlot, treasuryItem);

        // Active Effects
        ItemStack effectsItem = createActiveEffectsItem(player);
        int effectsSlot = getGUISlot("gui.main_menu.items.active_effects_item.gui_slot", 16);
        inv.setItem(effectsSlot, effectsItem);

        // === ROW 3: Main Features ===

        // Election
        ItemStack electionItem = createElectionItem(election, player);
        int electionSlot = getGUISlot("gui.main_menu.items.election_item.gui_slot", 19);
        inv.setItem(electionSlot, electionItem);

        // Executive Orders
        ItemStack ordersItem = createExecutiveOrdersItem(gov, player);
        int ordersSlot = getGUISlot("gui.main_menu.items.executive_orders_item.gui_slot", 21);
        inv.setItem(ordersSlot, ordersItem);

        // Presidential Arena
        ItemStack arenaItem = createArenaItem(playerData, player);
        int arenaSlot = getGUISlot("gui.main_menu.items.arena_item.gui_slot", 23);
        inv.setItem(arenaSlot, arenaItem);

        // Recall System
        ItemStack recallItem = createRecallItem(player);
        int recallSlot = getGUISlot("gui.main_menu.items.recall_item.gui_slot", 25);
        inv.setItem(recallSlot, recallItem);

        // === ROW 4: Info & Statistics ===

        // President History
        ItemStack historyItem = createHistoryItem(player);
        int historySlot = getGUISlot("gui.main_menu.items.history_item.gui_slot", 28);
        inv.setItem(historySlot, historyItem);

        // My Stats
        ItemStack statsItem = createMyStatsItem(playerData, player);
        int statsSlot = getGUISlot("gui.main_menu.items.my_stats_item.gui_slot", 30);
        inv.setItem(statsSlot, statsItem);

        // Leaderboard
        ItemStack leaderboardItem = createLeaderboardItem(player);
        int leaderboardSlot = getGUISlot("gui.main_menu.items.leaderboard_item.gui_slot", 32);
        inv.setItem(leaderboardSlot, leaderboardItem);

        // Guide/Help
        ItemStack helpItem = createHelpItem(player);
        int helpSlot = getGUISlot("gui.main_menu.items.help_item.gui_slot", 34);
        inv.setItem(helpSlot, helpItem);

        // === ROW 5: Quick Actions (if eligible) ===

        // Register Candidate (if registration phase)
        if (election.getPhase() == Election.ElectionPhase.REGISTRATION) {
            if (!election.getCandidates().containsKey(player.getUniqueId())) {

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

                ItemStack registerItem = createQuickItem(Material.EMERALD, "§a§l📝 REGISTER CANDIDATE",
                        "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                        "§7Requirements:",
                        String.format("§7• Level: §f%d§7/§a%d %s", pLevel, minLevel, levelStatus),
                        String.format("§7• Playtime: §f%.1fh§7/§a%.0fh %s", pPlaytime, minPlaytime, playStatus),
                        String.format("§7• Balance: §f%s§7/§a%s %s", MessageUtils.formatNumber(pBalance),
                                MessageUtils.formatNumber(minBalance), balStatus),
                        "",
                        "§7Registration Cost: §6" + MessageUtils.formatNumber(fee),
                        "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                        "§aClick to register!");
                int registerSlot = getGUISlot("gui.main_menu.items.register_candidate.gui_slot", 38);
                inv.setItem(registerSlot, registerItem);
            }
        }

        // Vote (if voting phase and haven't voted)
        if (election.getPhase() == Election.ElectionPhase.VOTING) {
            if (!election.hasVoted(player.getUniqueId())) {
                ItemStack voteItem = createQuickItem(Material.LIME_CONCRETE, "§a§l🗳 VOTE NOW!",
                        "§7Cast your vote",
                        "§7for your chosen candidate!",
                        "",
                        "§c⚠ Cannot be undone!",
                        "",
                        "§aClick to vote!");
                addGlow(voteItem);
                int voteSlot = getGUISlot("gui.main_menu.items.vote_now.gui_slot", 40);
                inv.setItem(voteSlot, voteItem);
            }
        }

        // Rate President (if there is a president)
        if (gov.hasPresident() && !gov.getPresidentUUID().equals(player.getUniqueId())) {
            ItemStack rateItem = createQuickItem(Material.NETHER_STAR, "§e§l⭐ RATE PRESIDENT",
                    "§7Give a rating for",
                    "§7current president's performance",
                    "",
                    "§7Current rating: §e" + String.format("%.1f", gov.getApprovalRating()) + "/5.0",
                    "",
                    "§eClick to rate!");
            int rateSlot = getGUISlot("gui.main_menu.items.rate_president.gui_slot", 42);
            inv.setItem(rateSlot, rateItem);
        }

        // === ROW 6: Footer ===

        // Close Button
        ItemStack closeItem = createItem(Material.BARRIER, "§c§lClose Menu", "§7Click to close");
        int closeSlot = getGUISlot("gui.main_menu.items.close_button.gui_slot", 49);
        inv.setItem(closeSlot, closeItem);

        // Fill empty slots with glass
        fillGlass(inv, player);

        // Decorative corners
        // Decorative corners
        Material cornerMat = getGUIMaterial("gui.main_menu.items.corner_decoration.material");

        // Only set corners if not AIR
        if (cornerMat != Material.AIR) {
            String cornerName = getGUIString(player, "gui.main_menu.items.corner_decoration.display_name");
            ItemStack corner = createConfiguredItem(cornerMat, cornerName, "gui.main_menu.items.corner_decoration");

            int[] corners = { 0, 8, 45, 53 };
            for (int c : corners) {
                inv.setItem(c, corner);
            }
        }

        player.openInventory(inv);
    }

    /**
     * Open Quick Actions Menu
     */
    public void openQuickActionsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, QUICK_ACTIONS_TITLE);

        Election election = plugin.getDataManager().getElection();
        Government gov = plugin.getDataManager().getGovernment();

        int slot = 10;

        // Register (if possible)
        if (election.getPhase() == Election.ElectionPhase.REGISTRATION) {
            if (!election.getCandidates().containsKey(player.getUniqueId())) {
                // Requirements & Stats
                PlayerData data = plugin.getDataManager().getOrCreatePlayerData(player.getUniqueId(), player.getName());

                int minLevel = plugin.getConfig().getInt("president.requirements.min-level", 100);
                double minPlaytime = plugin.getConfig().getDouble("president.requirements.min-playtime-hours", 100);
                double minBalance = plugin.getConfig().getDouble("president.requirements.min-vault-balance", 500000);
                double fee = plugin.getConfig().getDouble("election.registration-fee", 500000);

                int pLevel = player.getLevel();
                double pPlaytime = data.getPlaytimeHours();
                double pBalance = plugin.getVaultHook().getBalance(player.getUniqueId());

                String levelStatus = pLevel >= minLevel ? "§a✔" : "§c✗";
                String playStatus = pPlaytime >= minPlaytime ? "§a✔" : "§c✗";
                String balStatus = pBalance >= minBalance ? "§a✔" : "§c✗";

                ItemStack item = createQuickItem(Material.EMERALD, "§a§lRegister Candidate",
                        "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                        "§7Requirements:",
                        String.format("§7• Level: §f%d§7/§a%d %s", pLevel, minLevel, levelStatus),
                        String.format("§7• Playtime: §f%.1fh§7/§a%.0fh %s", pPlaytime, minPlaytime, playStatus),
                        String.format("§7• Balance: §f%s§7/§a%s %s", MessageUtils.formatNumber(pBalance),
                                MessageUtils.formatNumber(minBalance), balStatus),
                        "",
                        "§7Registration Cost: §6" + MessageUtils.formatNumber(fee),
                        "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                        "§aClick to register!");
                inv.setItem(slot++, item);
            }
        }

        // Vote (if possible)
        if (election.getPhase() == Election.ElectionPhase.VOTING) {
            if (!election.hasVoted(player.getUniqueId())) {
                ItemStack item = createQuickItem(Material.LIME_WOOL, "§a§lVote Candidate",
                        "§7Choose presidential candidate",
                        "§aClick to vote!");
                addGlow(item);
                inv.setItem(slot++, item);
            }
        }

        // Endorse (if campaign/registration phase)
        if (election.getPhase() == Election.ElectionPhase.REGISTRATION ||
                election.getPhase() == Election.ElectionPhase.CAMPAIGN) {
            ItemStack item = createQuickItem(Material.GOLDEN_APPLE, "§6§lEndorse Candidate",
                    "§7Support chosen candidate",
                    "§eClick to endorse!");
            inv.setItem(slot++, item);
        }

        // Rate President
        if (gov.hasPresident()) {
            ItemStack item = createQuickItem(Material.NETHER_STAR, "§e§lRate President",
                    "§7Rate the president",
                    "§eClick to rate!");
            inv.setItem(slot++, item);
        }

        // Donate to Treasury
        ItemStack donateItem = createQuickItem(Material.GOLD_INGOT, "§6§lTreasury Donation",
                "§7Donate to state treasury",
                "§eClick for donation info!");
        inv.setItem(slot++, donateItem);

        // Join Arena
        if (plugin.getArenaManager().isArenaActive()) {
            ItemStack arenaItem = createQuickItem(Material.IRON_SWORD, "§c§lJoin Arena",
                    "§7Presidential Games active!",
                    "§aClick to join!");
            inv.setItem(slot++, arenaItem);
        }

        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lBack", "§7Back to main menu");
        inv.setItem(18, backItem);

        fillGlass(inv, player);
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
        return plugin.getGUIConfig().getInt(path, defaultSlot);
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
        ItemStack item = createItem(Material.LECTERN, "§e§l📋 CABINET");
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        int filled = 0;
        for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
            UUID minister = gov.getCabinetMember(pos.toGovernmentPosition());
            if (minister != null) {
                String name = Bukkit.getOfflinePlayer(minister).getName();
                lore.add("§7" + pos.getDisplayName() + ": §f" + name);
                filled++;
            } else {
                lore.add("§7" + pos.getDisplayName() + ": §8Empty");
            }
        }

        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Filled: §f" + filled + "/5");
        lore.add("");
        lore.add("");
        lore.add("");
        lore.add("§aClick for cabinet details");

        meta.setLore(lore);
        applyConfigAttributes(meta, "gui.main_menu.items.cabinet_item");
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createTreasuryItem(Treasury treasury, Player player) {
        ItemStack item = createItem(Material.GOLD_BLOCK, "§6§l💰 TREASURY");
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Balance: §6" + MessageUtils.formatNumber(treasury.getBalance()));
        lore.add("§7Income: §a+" + MessageUtils.formatNumber(treasury.getTotalIncome()));
        lore.add("§7Expenses: §c-" + MessageUtils.formatNumber(treasury.getTotalExpenses()));
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aClick for treasury details");

        meta.setLore(lore);
        applyConfigAttributes(meta, "gui.main_menu.items.treasury_item");
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createActiveEffectsItem(Player player) {
        var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
        var activeDecisions = plugin.getDataManager().getActiveDecisions();
        int total = activeOrders.size() + activeDecisions.size();

        Material mat = total > 0 ? Material.BEACON : Material.GLASS;
        ItemStack item = createItem(mat, "§a§l✨ ACTIVE EFFECTS: " + total);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        if (total == 0) {
            lore.add("§7No active effects");
        } else {
            if (!activeOrders.isEmpty()) {
                lore.add("§c§lExecutive Orders:");
                for (ExecutiveOrder order : activeOrders) {
                    lore.add("§7- " + order.getType().getDisplayName());
                    lore.add("  §8(" + MessageUtils.formatTime(order.getRemainingTime()) + ")");
                }
            }

            if (!activeDecisions.isEmpty()) {
                if (!activeOrders.isEmpty())
                    lore.add("");
                lore.add("§e§lCabinet Decisions:");
                for (var decision : activeDecisions) {
                    lore.add("§7- " + decision.getType().getDisplayName());
                    lore.add("  §8(" + MessageUtils.formatTime(decision.getRemainingTime()) + ")");
                }
            }
        }

        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        meta.setLore(lore);
        applyConfigAttributes(meta, "gui.main_menu.items.active_effects_item");
        item.setItemMeta(meta);

        if (total > 0)
            addGlow(item);

        return item;
    }

    private ItemStack createElectionItem(Election election, Player player) {
        ItemStack item = createItem(Material.PAPER, "§b§l🗳 ELECTION");
        ItemMeta meta = item.getItemMeta();

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
        applyConfigAttributes(meta, "gui.main_menu.items.election_item");
        item.setItemMeta(meta);

        if (election.isActive() && election.getPhase() == Election.ElectionPhase.VOTING
                && !election.hasVoted(player.getUniqueId())) {
            addGlow(item);
        }

        return item;
    }

    private ItemStack createExecutiveOrdersItem(Government gov, Player player) {
        ItemStack item = createItem(Material.WRITABLE_BOOK, "§c§l📜 EXECUTIVE ORDERS");
        ItemMeta meta = item.getItemMeta();

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
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createArenaItem(PlayerData data, Player player) {
        boolean isActive = plugin.getArenaManager().isArenaActive();
        Material mat = isActive ? Material.DIAMOND_SWORD : Material.IRON_SWORD;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§4§l⚔ PRESIDENTIAL ARENA");

        // Apply attributes from config based on state
        String statePath = isActive ? "active" : "inactive";
        applyConfigAttributes(meta, "gui.main_menu.items.arena_item." + statePath);

        List<String> lore = new ArrayList<>();
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

        Material mat = isActive ? Material.REDSTONE_TORCH : Material.TORCH;
        ItemStack item = createItem(mat, "§c§l⚠ RECALL SYSTEM");
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
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
        lore.add("§7Deposit: §650,000");
        lore.add("§7Threshold: §f30% online");
        lore.add("");
        lore.add("§eClick for recall info");

        meta.setLore(lore);

        String statePath = isActive ? "active" : "inactive";
        applyConfigAttributes(meta, "gui.main_menu.items.recall_item." + statePath);

        item.setItemMeta(meta);

        if (isActive)
            addGlow(item);

        return item;
    }

    private ItemStack createHistoryItem(Player player) {
        var history = plugin.getDataManager().getAllPresidentHistory();

        ItemStack item = createItem(Material.BOOK, "§5§l📚 PRESIDENT HISTORY");
        ItemMeta meta = item.getItemMeta();

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
        applyConfigAttributes(meta, "gui.main_menu.items.history_item");
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createMyStatsItem(PlayerData data, Player player) {
        ItemStack item = createItem(Material.COMPASS, "§b§l📊 MY STATS");
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Votes Cast: §f" + data.getTotalVotesCast());
        lore.add("§7Endorsements: §f" + data.getEndorsementsGiven());
        lore.add("§7Served as President: §f" + data.getTimesServedAsPresident() + "x");
        lore.add("§7Served as Minister: §f" + data.getTimesServedAsCabinet() + "x");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aClick for full details");

        meta.setLore(lore);
        applyConfigAttributes(meta, "gui.main_menu.items.my_stats_item");
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createLeaderboardItem(Player player) {
        ItemStack item = createItem(Material.GOLDEN_APPLE, "§6§l🏆 LEADERBOARD");
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7View top players:");
        lore.add("§7- Arena Kills");
        lore.add("§7- Playtime");
        lore.add("§7- Best President");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aClick to view leaderboard");

        meta.setLore(lore);
        applyConfigAttributes(meta, "gui.main_menu.items.leaderboard_item");
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createHelpItem(Player player) {
        ItemStack item = createItem(Material.KNOWLEDGE_BOOK, "§a§l❓ GUIDE & HELP");
        ItemMeta meta = item.getItemMeta();

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
        applyConfigAttributes(meta, "gui.main_menu.items.help_item");
        item.setItemMeta(meta);

        return item;
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
     * Play click sound for specific GUI slot based on gui.yml configuration
     * 
     * @param player The player to play sound for
     * @param slot   The slot that was clicked
     */
    public void playClickSound(Player player, int slot) {
        String soundPath = getClickSoundPath(slot);
        if (soundPath != null) {
            Sound sound = getGUISound(soundPath);
            MessageUtils.playSound(player, sound);
        }
    }

    /**
     * Get the click_sound configuration path for a specific slot
     * 
     * @param slot The inventory slot
     * @return The path to click_sound in gui.yml, or null if not configured
     */
    private String getClickSoundPath(int slot) {
        // Map slots to their item configuration paths in gui.yml
        switch (slot) {
            case 4: // Player Head
                return "gui.main_menu.items.player_head.click_sound";
            case 10: // President Item
                Government gov = plugin.getDataManager().getGovernment();
                if (gov.hasPresident()) {
                    return "gui.main_menu.items.president_item.default.click_sound";
                } else {
                    return "gui.main_menu.items.president_item.no_president.click_sound";
                }
            case 12: // Cabinet
                return "gui.main_menu.items.cabinet_item.click_sound";
            case 14: // Treasury
                return "gui.main_menu.items.treasury_item.click_sound";
            case 16: // Active Effects
                var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
                var activeDecisions = plugin.getDataManager().getActiveDecisions();
                if (activeOrders.size() + activeDecisions.size() > 0) {
                    return "gui.main_menu.items.active_effects_item.active.click_sound";
                } else {
                    return "gui.main_menu.items.active_effects_item.inactive.click_sound";
                }
            case 19: // Election
                return "gui.main_menu.items.election_item.click_sound";
            case 21: // Executive Orders
                return "gui.main_menu.items.executive_orders_item.click_sound";
            case 23: // Arena
                boolean isActive = plugin.getArenaManager().isArenaActive();
                if (isActive) {
                    return "gui.main_menu.items.arena_item.active.click_sound";
                } else {
                    return "gui.main_menu.items.arena_item.inactive.click_sound";
                }
            case 25: // Recall
                RecallPetition petition = plugin.getDataManager().getRecallPetition();
                boolean hasActivePetition = petition != null &&
                        petition.getPhase() != RecallPetition.RecallPhase.COMPLETED &&
                        petition.getPhase() != RecallPetition.RecallPhase.FAILED;
                if (hasActivePetition) {
                    return "gui.main_menu.items.recall_item.active.click_sound";
                } else {
                    return "gui.main_menu.items.recall_item.inactive.click_sound";
                }
            case 28: // History
                return "gui.main_menu.items.history_item.click_sound";
            case 30: // My Stats
                return "gui.main_menu.items.my_stats_item.click_sound";
            case 32: // Leaderboard
                return "gui.main_menu.items.leaderboard_item.click_sound";
            case 34: // Help
                return "gui.main_menu.items.help_item.click_sound";
            case 38: // Register Candidate
                return "gui.main_menu.items.register_candidate.click_sound";
            case 40: // Vote Now
                return "gui.main_menu.items.vote_now.click_sound";
            case 42: // Rate President
                return "gui.main_menu.items.rate_president.click_sound";
            case 49: // Close Button
                return "gui.main_menu.items.close_button.click_sound";
            default:
                return null; // No sound for this slot
        }
    }

    /**
     * Play click sound for Quick Actions menu items
     * 
     * @param player          The player to play sound for
     * @param clickedMaterial The material of the item that was clicked
     */
    public void playQuickActionsSound(Player player, Material clickedMaterial) {
        String soundPath = null;

        switch (clickedMaterial) {
            case EMERALD:
                soundPath = "gui.quick_actions.items.register_candidate_qa.click_sound";
                break;
            case LIME_WOOL:
                soundPath = "gui.quick_actions.items.vote_candidate.click_sound";
                break;
            case GOLDEN_APPLE:
                soundPath = "gui.quick_actions.items.endorse_candidate.click_sound";
                break;
            case NETHER_STAR:
                soundPath = "gui.quick_actions.items.rate_president_qa.click_sound";
                break;
            case GOLD_INGOT:
                soundPath = "gui.quick_actions.items.donate_treasury.click_sound";
                break;
            case IRON_SWORD:
                soundPath = "gui.quick_actions.items.join_arena.click_sound";
                break;
            case ARROW:
                soundPath = "gui.quick_actions.items.back_button.click_sound";
                break;
            default:
                break;
        }

        if (soundPath != null) {
            Sound sound = getGUISound(soundPath);
            MessageUtils.playSound(player, sound);
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

        // PlaceholderAPI support (if available)
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            command = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, command);
        }

        return command;
    }
}
