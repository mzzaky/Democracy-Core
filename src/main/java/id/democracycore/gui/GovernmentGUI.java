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
import id.democracycore.models.Treasury;
import id.democracycore.utils.MessageUtils;

public class GovernmentGUI {

    private final DemocracyCore plugin;
    public static final String GOVERNMENT_GUI_TITLE = "§6§lGOVERNMENT";
    public static final String ORDERS_GUI_TITLE = "§c§lEXECUTIVE ORDERS";
    // Cabinet title constants kept as references €” cabinet logic moved to
    // CabinetGUI
    public static final String CABINET_GUI_TITLE = CabinetGUI.CABINET_GUI_TITLE;
    public static final String CABINET_DECISIONS_TITLE = CabinetGUI.CABINET_DECISIONS_TITLE;
    public static final String CABINET_APPOINT_TITLE = CabinetGUI.CABINET_APPOINT_TITLE;
    public static final String SALARY_GUI_TITLE = "§2§l💰 SALARY & REWARDS 💰";

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
        inv.setItem(19, cabinetItem);

        // Executive Orders button
        ItemStack ordersItem = createItem(Material.WRITABLE_BOOK, "§c§lExecutive Orders",
                "§7View and manage",
                "§7executive orders",
                "",
                "§aClick to view");
        inv.setItem(21, ordersItem);

        // Treasury button
        ItemStack treasuryItem = createItem(Material.GOLD_BLOCK, "§6§lTreasury",
                "§7State finances",
                "",
                "§aClick to view");
        inv.setItem(23, treasuryItem);

        // Election info
        Election election = plugin.getDataManager().getElection();
        ItemStack electionItem = createItem(Material.PAPER, "§b§lElection Info",
                "§7Phase: §f" + election.getPhase().getDisplayName(),
                "§7Candidates: §f" + election.getCandidates().size(),
                "",
                "§aClick for voting GUI");
        inv.setItem(25, electionItem);

        // Salary Claim Button
        ItemStack salaryItem = createItem(Material.EMERALD, "§a§lSalary Claim",
                "§7President & Cabinet",
                "§7Daily Salary Claim",
                "",
                "§eClick to open");
        inv.setItem(28, salaryItem);

        // History
        ItemStack historyItem = createItem(Material.BOOK, "§5§lPresident History",
                "§7View previous",
                "§7presidents",
                "",
                "§aClick to view");
        inv.setItem(30, historyItem);

        // Presidential Game
        ItemStack arenaItem = createItem(Material.NETHER_STAR, "§c§lPresidential Games",
                "§7Start a new arena game!",
                "",
                "§cOnly President can start",
                "§aClick to start");
        inv.setItem(32, arenaItem);

        // Broadcast Message
        long cooldown = 8L * 60 * 60 * 1000;
        long lastBroadcast = gov.getLastBroadcastTime();
        long nextAvailable = lastBroadcast + cooldown;
        List<String> broadcastLore = new ArrayList<>();
        broadcastLore.add("§7Send a global broadcast");
        broadcastLore.add("§7message to all players!");
        broadcastLore.add("");
        if (System.currentTimeMillis() >= nextAvailable) {
            broadcastLore.add("§cOnly President can use");
            broadcastLore.add("§aClick to type message");
        } else {
            long remaining = nextAvailable - System.currentTimeMillis();
            broadcastLore.add("§cCooldown: " + MessageUtils.formatTime(remaining));
        }
        ItemStack broadcastItem = createItem(Material.BELL, "§b§lBroadcast Message",
                broadcastLore.toArray(new String[0]));
        inv.setItem(34, broadcastItem);

        // Active effects indicator
        var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
        List<CabinetDecision> activeDecisions = plugin.getDataManager().getActiveDecisions();
        int totalActive = activeOrders.size() + activeDecisions.size();

        ItemStack effectsItem = createItem(
                totalActive > 0 ? Material.BEACON : Material.GLASS,
                "§a§lActive Effects: §f" + totalActive,
                "§7Orders: §f" + activeOrders.size(),
                "§7Decisions: §f" + activeDecisions.size());
        inv.setItem(4, effectsItem);

        // Main Menu Shortcut
        ItemStack mainMenu = createItem(Material.COMPASS, "§b§lMain Menu", "§7Return to Main Menu");
        inv.setItem(36, mainMenu);

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

    // openCabinetMenu and openCabinetDecisionsMenu have been moved to CabinetGUI.

    public void openTreasuryMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§6§l TREASURY ");
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
        Inventory inv = Bukkit.createInventory(null, 54, "§6§l“œ TREASURY LOGS “œ");
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

    public void openSalaryMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, SALARY_GUI_TITLE);
        Government gov = plugin.getDataManager().getGovernment();

        long cooldown = plugin.getGovernmentManager().getSalaryCooldown(player);
        boolean isPresident = gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId());
        id.democracycore.models.Government.CabinetMember cabinetMember = gov
                .getCabinetMemberByUUID(player.getUniqueId());

        ItemStack claimItem;
        if (cooldown == 0) {
            List<String> lore = new ArrayList<>();
            lore.add("§7Your daily salary is ready!");
            lore.add("");
            lore.add("§8§m------------------------");
            lore.add("§b§lRewards:");

            if (isPresident) {
                org.bukkit.configuration.ConfigurationSection rewardsSection = plugin.getConfig()
                        .getConfigurationSection("president.daily-rewards");
                if (rewardsSection != null) {
                    for (String key : rewardsSection.getKeys(false)) {
                        if (key.equalsIgnoreCase("vault-points")) {
                            double vaultPoints = rewardsSection.getDouble(key);
                            if (vaultPoints > 0) {
                                lore.add("§7• §6" + MessageUtils.formatNumber((long) vaultPoints) + " §eVault Points");
                            }
                        } else {
                            int amount = rewardsSection.getInt(key);
                            if (amount > 0) {
                                String[] words = key.split("-");
                                StringBuilder itemName = new StringBuilder();
                                for (String word : words) {
                                    if (!word.isEmpty()) {
                                        itemName.append(Character.toUpperCase(word.charAt(0)))
                                                .append(word.substring(1).toLowerCase()).append(" ");
                                    }
                                }
                                lore.add("§7• §b" + amount + "x " + itemName.toString().trim());
                            }
                        }
                    }
                }
            } else if (cabinetMember != null) {
                Government.CabinetPosition position = cabinetMember.getPosition();
                String configPath = switch (position) {
                    case DEFENSE -> "cabinet.defense.daily-vault";
                    case TREASURY -> "cabinet.treasury-minister.daily-vault";
                    case COMMERCE -> "cabinet.commerce.daily-vault";
                    case INFRASTRUCTURE -> "cabinet.infrastructure.daily-vault";
                    case CULTURE -> "cabinet.culture.daily-vault";
                };

                double vaultPoints = plugin.getConfig().getDouble(configPath, 30000);
                double salary = plugin.getConfig().getDouble("cabinet.daily-salary", 20000);
                double totalPay = vaultPoints + salary;

                lore.add("§7• §6" + MessageUtils.formatNumber((long) totalPay) + " §eVault Points");

                switch (position) {
                    case DEFENSE -> {
                        lore.add("§7• §b3x Diamond");
                        lore.add("§7• §e5x Golden Apple");
                    }
                    case TREASURY -> lore.add("§7• §a10x Emerald Block");
                    case COMMERCE -> {
                        lore.add("§7• §b5x Diamond");
                    }
                    case INFRASTRUCTURE -> {
                        lore.add("§7• §c10x Redstone Block");
                    }
                    case CULTURE -> {
                        lore.add("§7• §a5x Experience Bottle");
                        lore.add("§7• §f3x Name Tag");
                    }
                }
            }
            lore.add("§8§m------------------------");
            lore.add("");
            lore.add("§eClick to claim!");

            claimItem = createItem(Material.EMERALD_BLOCK, "§a§lCLAIM SALARY", lore.toArray(new String[0]));
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
        meta.setDisplayName("§6§lPRESIDENT: " + offlinePlayer.getName());

        List<String> lore = new ArrayList<>();
        lore.add("§7Term #" + gov.getCurrentTerm());
        lore.add("§7Time left in term:");
        lore.add("§f" + MessageUtils.formatTime(gov.getTermEndTime() - System.currentTimeMillis()));
        lore.add("");
        lore.add("§7Approval Rating:");
        lore.add("§e" + String.format("%.1f", gov.getApprovalRating()) + "/5.0");

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
