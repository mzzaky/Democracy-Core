package id.democracycore.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import id.democracycore.DemocracyCore;
import id.democracycore.models.Treasury;
import id.democracycore.utils.MessageUtils;

class GovernmentTreasuryMenu {

    private final DemocracyCore plugin;

    GovernmentTreasuryMenu(DemocracyCore plugin) {
        this.plugin = plugin;
    }

    void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§6§l TREASURY ");
        Treasury treasury = plugin.getDataManager().getTreasury();

        inv.setItem(13, GovernmentGUIUtils.createItem(Material.GOLD_BLOCK,
                "§6§lTreasury Balance",
                "§7" + MessageUtils.formatNumber(treasury.getBalance())));

        inv.setItem(20, GovernmentGUIUtils.createItem(Material.EMERALD,
                "§a§lTotal Income",
                "§7" + MessageUtils.formatNumber(treasury.getTotalIncome())));

        inv.setItem(24, GovernmentGUIUtils.createItem(Material.REDSTONE,
                "§c§lTotal Expenses",
                "§7" + MessageUtils.formatNumber(treasury.getTotalExpenses())));

        inv.setItem(22, GovernmentGUIUtils.createItem(Material.HOPPER,
                "§e§lDonate to Treasury",
                "§7Use command:",
                "§f/dc treasury donate <amount>"));

        inv.setItem(31, GovernmentGUIUtils.createItem(Material.BOOK, "§6§lTransaction History",
                "§7View full treasury",
                "§7transaction logs",
                "",
                "§eClick to view"));

        inv.setItem(36, GovernmentGUIUtils.createItem(Material.ARROW, "§7§lBack"));

        GovernmentGUIUtils.fillGlass(inv);
        player.openInventory(inv);
    }

    void openTransactions(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6§l📜 TREASURY LOGS 📜");
        Treasury treasury = plugin.getDataManager().getTreasury();

        List<Treasury.Transaction> transactions = treasury.getRecentTransactions(45);
        int slot = 0;

        for (Treasury.Transaction tx : transactions) {
            if (slot >= 45) {
                break;
            }

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

            ItemStack item = GovernmentGUIUtils.createItem(mat, "§6Transaction #" + (slot + 1), lore.toArray(new String[0]));
            inv.setItem(slot, item);
            slot++;
        }

        if (transactions.isEmpty()) {
            inv.setItem(22, GovernmentGUIUtils.createItem(Material.PAPER, "§7§lNo Transactions",
                    "§7The treasury has no",
                    "§7recorded transactions yet."));
        }

        inv.setItem(49, GovernmentGUIUtils.createItem(Material.ARROW, "§7§lBack", "§7Back to Treasury"));

        GovernmentGUIUtils.fillGlass(inv);
        player.openInventory(inv);
    }
}
