package id.democracycore.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import id.democracycore.DemocracyCore;
import id.democracycore.models.CabinetDecision;
import id.democracycore.models.Election;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.Government;
import id.democracycore.models.PresidentHistory;
import id.democracycore.models.Treasury;
import id.democracycore.utils.MessageUtils;

public class GovernmentGUI {

    private final DemocracyCore plugin;
    public static final String GOVERNMENT_GUI_TITLE = "§6§l🏛 GOVERNMENT 🏛";
    public static final String ORDERS_GUI_TITLE = "§c§l📜 EXECUTIVE ORDERS 📜";
    public static final String CABINET_GUI_TITLE = "§e§l📋 CABINET 📋";
    public static final String CABINET_DECISIONS_TITLE = "§d§l⚖ CABINET DECISIONS ⚖";

    public GovernmentGUI(DemocracyCore plugin) {
        this.plugin = plugin;
    }

    public void openGovernmentMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, GOVERNMENT_GUI_TITLE);
        Government gov = plugin.getDataManager().getGovernment();

        // President section
        if (gov.hasPresident()) {
            ItemStack presidentHead = createPresidentHead(gov);
            inv.setItem(13, presidentHead);
        } else {
            ItemStack noPresident = createItem(Material.BARRIER, "§c§lNo President",
                    "§7Election in progress",
                    "",
                    "§eClick for election info");
            inv.setItem(13, noPresident);
        }

        // Cabinet button
        ItemStack cabinetItem = createItem(Material.LECTERN, "§e§lCabinet",
                "§7View cabinet members",
                "§7and active decisions",
                "",
                "§aClick to view");
        inv.setItem(20, cabinetItem);

        // Executive Orders button
        ItemStack ordersItem = createItem(Material.WRITABLE_BOOK, "§c§lExecutive Orders",
                "§7View and manage",
                "§7executive orders",
                "",
                "§aClick to view");
        inv.setItem(22, ordersItem);

        // Treasury button
        ItemStack treasuryItem = createItem(Material.GOLD_BLOCK, "§6§lTreasury",
                "§7State finances",
                "",
                "§aClick to view");
        inv.setItem(24, treasuryItem);

        // Election info
        Election election = plugin.getDataManager().getElection();
        ItemStack electionItem = createItem(Material.PAPER, "§b§lElection Info",
                "§7Phase: §f" + election.getPhase().getDisplayName(),
                "§7Candidates: §f" + election.getCandidates().size(),
                "",
                "§aClick for voting GUI");
        inv.setItem(30, electionItem);

        // History
        ItemStack historyItem = createItem(Material.BOOK, "§5§lPresident History",
                "§7View previous",
                "§7presidents",
                "",
                "§aClick to view");
        inv.setItem(32, historyItem);

        // Active effects indicator
        var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
        List<CabinetDecision> activeDecisions = plugin.getDataManager().getActiveDecisions();
        int totalActive = activeOrders.size() + activeDecisions.size();

        // Main Menu Shortcut
        ItemStack mainMenu = createItem(Material.COMPASS, "§b§lMain Menu", "§7Return to Main Menu");
        inv.setItem(36, mainMenu);
        ItemStack effectsItem = createItem(
                totalActive > 0 ? Material.BEACON : Material.GLASS,
                "§a§lActive Effects: §f" + totalActive,
                "§7Orders: §f" + activeOrders.size(),
                "§7Decisions: §f" + activeDecisions.size());
        inv.setItem(4, effectsItem);

        // Salary Claim Button
        ItemStack salaryItem = createItem(Material.EMERALD, "§a§lSalary Claim",
                "§7President & Cabinet",
                "§7Daily Salary Claim",
                "",
                "§eClick to open");
        inv.setItem(31, salaryItem);

        // Close button
        ItemStack closeItem = createItem(Material.BARRIER, "§c§lClose", "§7Click to close");
        inv.setItem(40, closeItem);

        // Fill glass
        fillGlass(inv);

        player.openInventory(inv);
    }

    public void openExecutiveOrdersMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ORDERS_GUI_TITLE);
        Government gov = plugin.getDataManager().getGovernment();
        boolean isPresident = gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId());
        boolean isAdmin = player.hasPermission("democracy.admin");

        int slot = 10;
        for (ExecutiveOrder.ExecutiveOrderType type : ExecutiveOrder.ExecutiveOrderType.values()) {
            if (slot >= 44)
                break;

            boolean active = plugin.getExecutiveOrderManager().isOrderActive(type);
            boolean onCooldown = plugin.getExecutiveOrderManager().isOrderOnCooldown(type);

            Material material;
            String status;
            List<String> lore = new ArrayList<>();

            if (active) {
                material = Material.LIME_CONCRETE;
                ExecutiveOrder order = plugin.getExecutiveOrderManager().getActiveOrder(type);
                status = "§a[ACTIVE]";
                lore.add("§7Time left: §f" + MessageUtils.formatTime(order.getRemainingTime()));
            } else if (onCooldown) {
                material = Material.RED_CONCRETE;
                status = "§c[COOLDOWN]";
                long remaining = plugin.getExecutiveOrderManager().getOrderCooldownRemaining(type);
                lore.add("§7Cooldown: §f" + MessageUtils.formatTime(remaining));
            } else {
                material = Material.YELLOW_CONCRETE;
                status = "§e[AVAILABLE]";
            }

            lore.add("");
            lore.add("§7" + type.getDescription());
            lore.add("");
            lore.add("§7Duration: §f" + MessageUtils.formatTime(type.getDefaultDuration()));
            lore.add("§7Cost: §6" + MessageUtils.formatNumber(1000000L));

            if ((isPresident || isAdmin) && !active && !onCooldown) {
                lore.add("");
                lore.add("§aClick to issue!");
            }

            ItemStack item = createItem(material, "§6§l" + type.getDisplayName() + " " + status,
                    lore.toArray(new String[0]));
            inv.setItem(slot, item);

            slot++;
            if ((slot + 1) % 9 == 0)
                slot += 2;
        }

        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lBack", "§7Back to main menu");
        inv.setItem(45, backItem);

        // Info
        Treasury treasury = plugin.getDataManager().getTreasury();
        ItemStack infoItem = createItem(Material.BOOK, "§e§lInfo",
                "§7Treasury: §6" + MessageUtils.formatNumber(treasury.getBalance()),
                "§7Order Cost: §61,000,000",
                "§7Cooldown: §f7 days");
        inv.setItem(49, infoItem);

        fillGlass(inv);
        player.openInventory(inv);
    }

    public void openCabinetMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, CABINET_GUI_TITLE);
        Government gov = plugin.getDataManager().getGovernment();

        // Cabinet positions
        int[] slots = { 11, 12, 13, 14, 15 };
        int i = 0;

        for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
            if (i >= slots.length)
                break;

            UUID ministerUUID = gov.getCabinetMember(Government.CabinetPosition.valueOf(pos.name()));
            ItemStack item;

            if (ministerUUID != null) {
                item = createMinisterHead(ministerUUID, pos);
            } else {
                item = createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                        "§7§l" + pos.getDisplayName(),
                        "§cEmpty Position",
                        "",
                        "§7Waiting to be appointed",
                        "§7by President");
            }

            inv.setItem(slots[i], item);
            i++;
        }

        // Active decisions
        List<CabinetDecision> activeDecisions = plugin.getDataManager().getActiveDecisions();
        if (!activeDecisions.isEmpty()) {
            ItemStack decisionsItem = createItem(Material.ENCHANTED_BOOK,
                    "§d§lActive Decisions: " + activeDecisions.size(),
                    "§7Click to view details");
            inv.setItem(31, decisionsItem);
        }

        // My position (if player is minister)
        CabinetDecision.CabinetPosition myPos = null;
        for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
            UUID minister = gov.getCabinetMember(Government.CabinetPosition.valueOf(pos.name()));
            if (minister != null && minister.equals(player.getUniqueId())) {
                myPos = pos;
                break;
            }
        }

        if (myPos != null) {
            ItemStack myPosItem = createItem(Material.DIAMOND,
                    "§b§lYour Position: " + myPos.getDisplayName(),
                    "§7Click to manage decisions");
            inv.setItem(29, myPosItem);
        }

        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lBack", "§7Back to main menu");
        inv.setItem(36, backItem);

        fillGlass(inv);
        player.openInventory(inv);
    }

    public void openCabinetDecisionsMenu(Player player, CabinetDecision.CabinetPosition position) {
        Inventory inv = Bukkit.createInventory(null, 36, CABINET_DECISIONS_TITLE);
        Government gov = plugin.getDataManager().getGovernment();

        UUID minister = gov.getCabinetMember(Government.CabinetPosition.valueOf(position.name()));
        boolean canIssue = minister != null && minister.equals(player.getUniqueId());

        int slot = 10;
        for (CabinetDecision.DecisionType type : CabinetDecision.DecisionType.values()) {
            if (type.getPosition() != position)
                continue;
            if (slot >= 26)
                break;

            boolean active = plugin.getCabinetManager().isDecisionActive(type);
            boolean onCooldown = false; // Cooldown tracking not yet implemented

            Material material;
            String status;
            List<String> lore = new ArrayList<>();

            if (active) {
                material = Material.LIME_WOOL;
                // Find the active decision of this type
                CabinetDecision decision = plugin.getDataManager().getActiveDecisions().stream()
                        .filter(d -> d.getType() == type)
                        .findFirst()
                        .orElse(null);
                status = "§a[ACTIVE]";
                if (decision != null) {
                    lore.add("§7Time left: §f" + MessageUtils.formatTime(decision.getRemainingTime()));
                }
            } else if (onCooldown) {
                material = Material.RED_WOOL;
                status = "§c[CD]";
                // Cooldown functionality not yet implemented
                lore.add("§7Cooldown: §fN/A");
            } else {
                material = Material.YELLOW_WOOL;
                status = "§e[OK]";
            }

            lore.add("");
            lore.add("§7" + type.getDescription());
            lore.add("");
            lore.add("§7Duration: §f" + MessageUtils.formatTime(type.getDurationMillis()));
            lore.add("§7Cost: §6500,000");

            if (canIssue && !active && !onCooldown) {
                lore.add("");
                lore.add("§aClick to issue!");
            }

            ItemStack item = createItem(material, "§e§l" + type.name() + " " + status,
                    lore.toArray(new String[0]));
            inv.setItem(slot, item);

            slot++;
            if ((slot + 1) % 9 == 0)
                slot += 2;
        }

        // Position info
        String ministerName = minister != null ? Bukkit.getOfflinePlayer(minister).getName() : "Empty";
        ItemStack infoItem = createItem(Material.PAPER,
                "§6§l" + position.getDisplayName(),
                "§7Minister: §f" + ministerName);
        inv.setItem(4, infoItem);

        // Back
        ItemStack backItem = createItem(Material.ARROW, "§7§lBack");
        inv.setItem(27, backItem);

        fillGlass(inv);
        player.openInventory(inv);
    }

    public void openTreasuryMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§6§l💰 TREASURY 💰");
        Treasury treasury = plugin.getDataManager().getTreasury();

        // Main balance
        ItemStack balanceItem = createItem(Material.GOLD_BLOCK,
                "§6§lTreasury Balance",
                "§7" + MessageUtils.formatNumber(treasury.getBalance()));
        inv.setItem(13, balanceItem);

        // Income stats
        ItemStack incomeItem = createItem(Material.EMERALD,
                "§a§lTotal Income",
                "§7" + MessageUtils.formatNumber(treasury.getTotalIncome()));
        inv.setItem(20, incomeItem);

        // Expense stats
        ItemStack expenseItem = createItem(Material.REDSTONE,
                "§c§lTotal Expenses",
                "§7" + MessageUtils.formatNumber(treasury.getTotalExpenses()));
        inv.setItem(24, expenseItem);

        // Donate button
        ItemStack donateItem = createItem(Material.HOPPER,
                "§e§lDonate to Treasury",
                "§7Use command:",
                "§f/dc treasury donate <amount>");
        inv.setItem(22, donateItem);

        // Transaction History button
        ItemStack historyItem = createItem(Material.BOOK, "§6§lTransaction History",
                "§7View full treasury",
                "§7transaction logs",
                "",
                "§eClick to view");
        inv.setItem(31, historyItem);

        // Back
        ItemStack backItem = createItem(Material.ARROW, "§7§lBack");
        inv.setItem(36, backItem);

        fillGlass(inv);
        player.openInventory(inv);
    }

    public void openTreasuryTransactionsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§l📜 TREASURY LOGS 📜");
        Treasury treasury = plugin.getDataManager().getTreasury();

        List<Treasury.Transaction> transactions = treasury.getRecentTransactions(45);
        int slot = 0;

        for (Treasury.Transaction tx : transactions) {
            if (slot >= 45)
                break;

            boolean isDeposit = tx.getType().name().contains("INCOME") ||
                    tx.getType().name().contains("DEPOSIT") ||
                    tx.getType().name().contains("DONATION") ||
                    tx.getType().name().contains("FUND") ||
                    tx.getType().name().contains("REFUND");

            Material mat = isDeposit ? Material.LIME_DYE : Material.RED_DYE;
            String prefix = isDeposit ? "§a+" : "§c-";

            List<String> lore = new ArrayList<>();
            lore.add("§7Amount: " + prefix + MessageUtils.formatNumber(tx.getAmount()));
            lore.add("§7Type: §f" + tx.getType().getDisplayName());
            lore.add("§7Time: §f" + MessageUtils.formatTime(System.currentTimeMillis() - tx.getTimestamp()) + " ago");

            if (tx.getRelatedPlayer() != null) {
                String playerName = Bukkit.getOfflinePlayer(tx.getRelatedPlayer()).getName();
                lore.add("§7Player: §e" + (playerName != null ? playerName : "Unknown"));
            }

            lore.add("");
            lore.add("§7" + tx.getDescription());

            ItemStack item = createItem(mat, "§6Transaction #" + (slot + 1), lore.toArray(new String[0]));
            inv.setItem(slot, item);
            slot++;
        }

        if (transactions.isEmpty()) {
            ItemStack emptyItem = createItem(Material.PAPER, "§7§lNo Transactions",
                    "§7The treasury has no",
                    "§7recorded transactions yet.");
            inv.setItem(22, emptyItem);
        }

        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lBack", "§7Back to Treasury");
        inv.setItem(49, backItem);

        fillGlass(inv);
        player.openInventory(inv);
    }

    public void openHistoryMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§5§l📚 PRESIDENT HISTORY 📚");
        List<PresidentHistory.PresidentRecord> history = plugin.getDataManager().getAllPresidentHistory();

        int slot = 10;
        int rank = 1;

        for (PresidentHistory.PresidentRecord h : history) {
            if (slot >= 44)
                break;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            var offlinePlayer = Bukkit.getOfflinePlayer(h.getPlayerId());
            meta.setOwningPlayer(offlinePlayer);
            meta.setDisplayName("§6§l#" + rank + " " + offlinePlayer.getName());

            List<String> lore = new ArrayList<>();
            lore.add("§7Term: §f" + h.getTerm());
            lore.add("§7Approval: §e" + String.format("%.1f", h.getApproval()) + "⭐");
            lore.add("§7Orders: §f" + h.getOrders());
            lore.add("§7Games: §f" + h.getGames());
            lore.add("");
            lore.add("§7End reason:");
            lore.add("§f" + h.getReason());

            meta.setLore(lore);
            head.setItemMeta(meta);

            inv.setItem(slot, head);

            slot++;
            if ((slot + 1) % 9 == 0)
                slot += 2;
            rank++;
        }

        if (history.isEmpty()) {
            ItemStack emptyItem = createItem(Material.PAPER, "§7§lNo History Yet",
                    "§7No president has",
                    "§7completed a term yet");
            inv.setItem(22, emptyItem);
        }

        // Back
        ItemStack backItem = createItem(Material.ARROW, "§7§lBack");
        inv.setItem(45, backItem);

        fillGlass(inv);
        player.openInventory(inv);
    }

    public void openSalaryMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§2§l💰 SALARY & REWARDS 💰");
        Government gov = plugin.getDataManager().getGovernment();

        long cooldown = plugin.getGovernmentManager().getSalaryCooldown(player);

        ItemStack claimItem;
        if (cooldown == 0) {
            claimItem = createItem(Material.EMERALD_BLOCK, "§a§lCLAIM SALARY",
                    "§7Your daily salary is ready!",
                    "",
                    "§eClick to claim!");
        } else if (cooldown > 0) {
            long hours = cooldown / (60 * 60 * 1000);
            long minutes = (cooldown % (60 * 60 * 1000)) / (60 * 1000);
            claimItem = createItem(Material.RED_CONCRETE, "§c§lNEXT SALARY",
                    "§7Available in:",
                    "§f" + String.format("%02dh %02dm", hours, minutes));
        } else {
            claimItem = createItem(Material.BARRIER, "§c§lNOT ELIGIBLE",
                    "§7Only President and",
                    "§7Cabinet Ministers may claim.");
        }
        inv.setItem(13, claimItem);

        // Payout Stats
        ItemStack statsItem = createItem(Material.PAPER, "§e§lAdministration Stats",
                "§7Total Salary Paid:",
                "§6" + MessageUtils.formatNumber((long) gov.getTotalSalaryPayouts()));
        inv.setItem(4, statsItem);

        // Back
        ItemStack backItem = createItem(Material.ARROW, "§7§lBack", "§7Back to government menu");
        inv.setItem(18, backItem);

        fillGlass(inv);
        player.openInventory(inv);
    }

    private ItemStack createPresidentHead(Government gov) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        var offlinePlayer = Bukkit.getOfflinePlayer(gov.getPresidentUUID());
        meta.setOwningPlayer(offlinePlayer);
        meta.setDisplayName("§6§l👑 PRESIDENT: " + offlinePlayer.getName());

        List<String> lore = new ArrayList<>();
        lore.add("§7Term #" + gov.getCurrentTerm());
        lore.add("§7Time left in term:");
        lore.add("§f" + MessageUtils.formatTime(gov.getTermEndTime() - System.currentTimeMillis()));
        lore.add("");
        lore.add("§7Approval Rating:");
        lore.add("§e" + String.format("%.1f", gov.getApprovalRating()) + "/5.0 ⭐");

        meta.setLore(lore);
        head.setItemMeta(meta);

        return head;
    }

    private ItemStack createMinisterHead(UUID ministerUUID, CabinetDecision.CabinetPosition pos) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        var offlinePlayer = Bukkit.getOfflinePlayer(ministerUUID);
        meta.setOwningPlayer(offlinePlayer);
        meta.setDisplayName("§e§l" + pos.getDisplayName());

        List<String> lore = new ArrayList<>();
        lore.add("§7" + offlinePlayer.getName());
        lore.add("");

        // Active decisions count
        List<CabinetDecision> decisions = plugin.getDataManager().getActiveDecisions().stream()
                .filter(d -> d.getMinisterPosition() == pos)
                .collect(java.util.stream.Collectors.toList());
        lore.add("§7Active decisions: §f" + decisions.size());
        lore.add("");
        lore.add("§aClick for details");

        meta.setLore(lore);
        head.setItemMeta(meta);

        return head;
    }

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

    private void fillGlass(Inventory inv) {
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
    }
}
