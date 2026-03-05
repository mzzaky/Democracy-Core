package id.democracycore.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import id.democracycore.DemocracyCore;
import id.democracycore.models.CabinetDecision;
import id.democracycore.models.Election;
import id.democracycore.models.Government;
import id.democracycore.utils.MessageUtils;

public class GovernmentGUI {

    private final DemocracyCore plugin;
    private final GovernmentExecutiveOrdersMenu executiveOrdersMenu;
    private final GovernmentTreasuryMenu treasuryMenu;
    private final GovernmentSalaryMenu salaryMenu;

    public static final String GOVERNMENT_GUI_TITLE = "§6§lGOVERNMENT";
    public static final String ORDERS_GUI_TITLE = "§c§lEXECUTIVE ORDERS";
    // Cabinet title constants kept as references — cabinet logic moved to
    // CabinetGUI
    public static final String CABINET_GUI_TITLE = CabinetGUI.CABINET_GUI_TITLE;
    public static final String CABINET_DECISIONS_TITLE = CabinetGUI.CABINET_DECISIONS_TITLE;
    public static final String CABINET_APPOINT_TITLE = CabinetGUI.CABINET_APPOINT_TITLE;
    public static final String SALARY_GUI_TITLE = "§2§l💰 SALARY & REWARDS 💰";

    public GovernmentGUI(DemocracyCore plugin) {
        this.plugin = plugin;
        this.executiveOrdersMenu = new GovernmentExecutiveOrdersMenu(plugin);
        this.treasuryMenu = new GovernmentTreasuryMenu(plugin);
        this.salaryMenu = new GovernmentSalaryMenu(plugin);
    }

    @SuppressWarnings("deprecation")
    public void openGovernmentMenu(Player player) {
        Government gov = plugin.getDataManager().getGovernment();

        boolean isPresident = gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId());
        boolean isMinister = gov.getCabinetMemberByUUID(player.getUniqueId()) != null;

        if (!isPresident && !isMinister) {
            MessageUtils.send(player, "§cOnly the President and Ministers can open the Government GUI.");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 45, GOVERNMENT_GUI_TITLE);

        if (gov.hasPresident()) {
            inv.setItem(13, createPresidentHead(gov));
        } else {
            inv.setItem(13, GovernmentGUIUtils.createItem(Material.BARRIER, "§c§lNo President",
                    "§7Election in progress",
                    "",
                    "§eClick for election info"));
        }

        inv.setItem(19, GovernmentGUIUtils.createItem(Material.LECTERN, "§e§lCabinet",
                "§7View cabinet members",
                "§7and active decisions",
                "",
                "§aClick to view"));

        inv.setItem(21, GovernmentGUIUtils.createItem(Material.WRITABLE_BOOK, "§c§lExecutive Orders",
                "§7View and manage",
                "§7executive orders",
                "",
                "§aClick to view"));

        inv.setItem(23, GovernmentGUIUtils.createItem(Material.GOLD_BLOCK, "§6§lTreasury",
                "§7State finances",
                "",
                "§aClick to view"));

        Election election = plugin.getDataManager().getElection();
        inv.setItem(25, GovernmentGUIUtils.createItem(Material.PAPER, "§b§lElection Info",
                "§7Phase: §f" + election.getPhase().getDisplayName(),
                "§7Candidates: §f" + election.getCandidates().size(),
                "",
                "§aClick for voting GUI"));

        inv.setItem(28, GovernmentGUIUtils.createItem(Material.EMERALD, "§a§lSalary Claim",
                "§7President & Cabinet",
                "§7Daily Salary Claim",
                "",
                "§eClick to open"));

        inv.setItem(30, GovernmentGUIUtils.createItem(Material.BOOK, "§5§lPresident History",
                "§7View previous",
                "§7presidents",
                "",
                "§aClick to view"));

        inv.setItem(32, GovernmentGUIUtils.createItem(Material.NETHER_STAR, "§c§lPresidential Games",
                "§7Start a new arena game!",
                "",
                "§cOnly President can start",
                "§aClick to start"));

        long cooldown = 8L * 60 * 60 * 1000;
        long nextAvailable = gov.getLastBroadcastTime() + cooldown;
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
        inv.setItem(34, GovernmentGUIUtils.createItem(Material.BELL, "§b§lBroadcast Message",
                broadcastLore.toArray(new String[0])));

        var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
        List<CabinetDecision> activeDecisions = plugin.getDataManager().getActiveDecisions();
        int totalActive = activeOrders.size() + activeDecisions.size();

        inv.setItem(4, GovernmentGUIUtils.createItem(
                totalActive > 0 ? Material.BEACON : Material.GLASS,
                "§a§lActive Effects: §f" + totalActive,
                "§7Orders: §f" + activeOrders.size(),
                "§7Decisions: §f" + activeDecisions.size()));

        inv.setItem(36, GovernmentGUIUtils.createItem(Material.COMPASS, "§b§lMain Menu", "§7Return to Main Menu"));
        inv.setItem(40, GovernmentGUIUtils.createItem(Material.BARRIER, "§c§lClose", "§7Click to close"));

        GovernmentGUIUtils.fillGlass(inv);
        player.openInventory(inv);
    }

    public void openExecutiveOrdersMenu(Player player) {
        executiveOrdersMenu.open(player);
    }

    public void openTreasuryMenu(Player player) {
        treasuryMenu.open(player);
    }

    public void openTreasuryTransactionsMenu(Player player) {
        treasuryMenu.openTransactions(player);
    }

    public void openSalaryMenu(Player player) {
        salaryMenu.open(player);
    }

    @SuppressWarnings("deprecation")
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
}
