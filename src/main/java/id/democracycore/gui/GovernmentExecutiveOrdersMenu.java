package id.democracycore.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import id.democracycore.DemocracyCore;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.Government;
import id.democracycore.models.Treasury;
import id.democracycore.utils.MessageUtils;

class GovernmentExecutiveOrdersMenu {

    private final DemocracyCore plugin;

    GovernmentExecutiveOrdersMenu(DemocracyCore plugin) {
        this.plugin = plugin;
    }

    void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, GovernmentGUI.ORDERS_GUI_TITLE);
        Government gov = plugin.getDataManager().getGovernment();
        boolean isPresident = gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId());
        boolean isAdmin = player.hasPermission("democracy.admin");

        int slot = 10;
        for (ExecutiveOrder.ExecutiveOrderType type : ExecutiveOrder.ExecutiveOrderType.values()) {
            if (slot >= 44) {
                break;
            }

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

            ItemStack item = GovernmentGUIUtils.createItem(material, "§6§l" + type.getDisplayName() + " " + status,
                    lore.toArray(new String[0]));
            inv.setItem(slot, item);

            slot++;
            if ((slot + 1) % 9 == 0) {
                slot += 2;
            }
        }

        inv.setItem(45, GovernmentGUIUtils.createItem(Material.ARROW, "§7§lBack", "§7Back to main menu"));

        Treasury treasury = plugin.getDataManager().getTreasury();
        inv.setItem(49, GovernmentGUIUtils.createItem(Material.BOOK, "§e§lInfo",
                "§7Treasury: §6" + MessageUtils.formatNumber(treasury.getBalance()),
                "§7Order Cost: §61,000,000",
                "§7Cooldown: §f7 days"));

        GovernmentGUIUtils.fillGlass(inv);
        player.openInventory(inv);
    }
}
