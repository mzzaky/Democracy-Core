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
 * Main Menu GUI - Entry point untuk semua fitur DemocracyCore
 * Mempermudah pemain mengenali dan mengakses fitur plugin
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
        QUICK_ACTIONS_TITLE = plugin.getGUIConfig().getString("gui.quick_actions.title", "§e§l⚡ AKSI CEPAT ⚡");
    }

    private void loadGUITitles() {
        loadGUITitles(plugin);
    }
    
    /**
     * Buka Main Menu GUI untuk player
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
        inv.setItem(4, playerHead);
        
        // === ROW 2: Status Pemerintahan ===
        
        // Presiden Info (Slot 10)
        ItemStack presidentItem = createPresidentItem(gov);
        inv.setItem(10, presidentItem);
        
        // Kabinet Info (Slot 12)
        ItemStack cabinetItem = createCabinetItem(gov);
        inv.setItem(12, cabinetItem);
        
        // Treasury Info (Slot 14)
        ItemStack treasuryItem = createTreasuryItem(treasury);
        inv.setItem(14, treasuryItem);
        
        // Active Effects (Slot 16)
        ItemStack effectsItem = createActiveEffectsItem();
        inv.setItem(16, effectsItem);
        
        // === ROW 3: Fitur Utama ===
        
        // Pemilu (Slot 19)
        ItemStack electionItem = createElectionItem(election, player);
        inv.setItem(19, electionItem);
        
        // Executive Orders (Slot 21)
        ItemStack ordersItem = createExecutiveOrdersItem(gov, player);
        inv.setItem(21, ordersItem);
        
        // Presidential Arena (Slot 23)
        ItemStack arenaItem = createArenaItem(playerData);
        inv.setItem(23, arenaItem);
        
        // Recall System (Slot 25)
        ItemStack recallItem = createRecallItem();
        inv.setItem(25, recallItem);
        
        // === ROW 4: Info & Statistik ===
        
        // Sejarah Presiden (Slot 28)
        ItemStack historyItem = createHistoryItem();
        inv.setItem(28, historyItem);
        
        // Statistik Saya (Slot 30)
        ItemStack statsItem = createMyStatsItem(playerData);
        inv.setItem(30, statsItem);
        
        // Leaderboard (Slot 32)
        ItemStack leaderboardItem = createLeaderboardItem();
        inv.setItem(32, leaderboardItem);
        
        // Panduan/Help (Slot 34)
        ItemStack helpItem = createHelpItem();
        inv.setItem(34, helpItem);
        
        // === ROW 5: Quick Actions (jika eligible) ===
        
        // Register Kandidat (jika fase registration)
        if (election.getPhase() == Election.ElectionPhase.REGISTRATION) {
            if (!election.getCandidates().containsKey(player.getUniqueId())) {
                ItemStack registerItem = createQuickItem(Material.EMERALD, "§a§l📝 DAFTAR KANDIDAT",
                    "§7Daftarkan diri sebagai",
                    "§7kandidat presiden!",
                    "",
                    "§eBiaya: §6500,000",
                    "",
                    "§aKlik untuk mendaftar!"
                );
                inv.setItem(38, registerItem);
            }
        }
        
        // Vote (jika fase voting dan belum vote)
        if (election.getPhase() == Election.ElectionPhase.VOTING) {
            if (!election.hasVoted(player.getUniqueId())) {
                ItemStack voteItem = createQuickItem(Material.LIME_CONCRETE, "§a§l🗳 VOTE SEKARANG!",
                    "§7Berikan suara Anda",
                    "§7untuk kandidat pilihan!",
                    "",
                    "§c⚠ Tidak dapat dibatalkan!",
                    "",
                    "§aKlik untuk vote!"
                );
                addGlow(voteItem);
                inv.setItem(40, voteItem);
            }
        }
        
        // Rate President (jika ada presiden)
        if (gov.hasPresident() && !gov.getPresidentUUID().equals(player.getUniqueId())) {
            ItemStack rateItem = createQuickItem(Material.NETHER_STAR, "§e§l⭐ RATE PRESIDEN",
                "§7Berikan rating untuk",
                "§7kinerja presiden saat ini",
                "",
                "§7Rating saat ini: §e" + String.format("%.1f", gov.getApprovalRating()) + "/5.0",
                "",
                "§eKlik untuk memberi rating!"
            );
            inv.setItem(42, rateItem);
        }
        
        // === ROW 6: Footer ===
        
        // Close Button
        ItemStack closeItem = createItem(Material.BARRIER, "§c§lTutup Menu", "§7Klik untuk menutup");
        inv.setItem(49, closeItem);
        
        // Fill empty slots with glass
        fillGlass(inv);
        
        // Decorative corners
        ItemStack corner = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ");
        int[] corners = {0, 8, 45, 53};
        for (int c : corners) {
            inv.setItem(c, corner);
        }
        
        player.openInventory(inv);
    }
    
    /**
     * Buka Quick Actions Menu
     */
    public void openQuickActionsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, QUICK_ACTIONS_TITLE);
        
        Election election = plugin.getDataManager().getElection();
        Government gov = plugin.getDataManager().getGovernment();
        
        int slot = 10;
        
        // Register (jika bisa)
        if (election.getPhase() == Election.ElectionPhase.REGISTRATION) {
            if (!election.getCandidates().containsKey(player.getUniqueId())) {
                ItemStack item = createQuickItem(Material.EMERALD, "§a§lDaftar Kandidat",
                    "§7Biaya: §6500,000",
                    "§aKlik untuk mendaftar!"
                );
                inv.setItem(slot++, item);
            }
        }
        
        // Vote (jika bisa)
        if (election.getPhase() == Election.ElectionPhase.VOTING) {
            if (!election.hasVoted(player.getUniqueId())) {
                ItemStack item = createQuickItem(Material.LIME_WOOL, "§a§lVote Kandidat",
                    "§7Pilih kandidat presiden",
                    "§aKlik untuk voting!"
                );
                addGlow(item);
                inv.setItem(slot++, item);
            }
        }
        
        // Endorse (jika fase campaign/registration)
        if (election.getPhase() == Election.ElectionPhase.REGISTRATION ||
            election.getPhase() == Election.ElectionPhase.CAMPAIGN) {
            ItemStack item = createQuickItem(Material.GOLDEN_APPLE, "§6§lEndorse Kandidat",
                "§7Dukung kandidat pilihan",
                "§eKlik untuk endorse!"
            );
            inv.setItem(slot++, item);
        }
        
        // Rate President
        if (gov.hasPresident()) {
            ItemStack item = createQuickItem(Material.NETHER_STAR, "§e§lRate Presiden",
                "§7Beri rating presiden",
                "§eKlik untuk rate!"
            );
            inv.setItem(slot++, item);
        }
        
        // Donate to Treasury
        ItemStack donateItem = createQuickItem(Material.GOLD_INGOT, "§6§lDonasi Treasury",
            "§7Donasi ke kas negara",
            "§eKlik untuk info donasi!"
        );
        inv.setItem(slot++, donateItem);
        
        // Join Arena
        if (plugin.getArenaManager().isArenaActive()) {
            ItemStack arenaItem = createQuickItem(Material.IRON_SWORD, "§c§lJoin Arena",
                "§7Presidential Games aktif!",
                "§aKlik untuk bergabung!"
            );
            inv.setItem(slot++, arenaItem);
        }
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu utama");
        inv.setItem(18, backItem);
        
        fillGlass(inv);
        player.openInventory(inv);
    }
    
    // === GUI Config Helper Methods ===

    private String getGUIString(String path, Object... args) {
        String value = plugin.getGUIConfig().getString(path, "");
        for (int i = 0; i < args.length; i += 2) {
            String placeholder = "{" + args[i] + "}";
            String replacement = String.valueOf(args[i + 1]);
            value = value.replace(placeholder, replacement);
        }
        return value;
    }

    private List<String> getGUILore(String path, Object... args) {
        List<String> lore = plugin.getGUIConfig().getStringList(path);
        List<String> processed = new ArrayList<>();
        for (String line : lore) {
            for (int i = 0; i < args.length; i += 2) {
                String placeholder = "{" + args[i] + "}";
                String replacement = String.valueOf(args[i + 1]);
                line = line.replace(placeholder, replacement);
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

    private Sound getGUISound(String path) {
        String soundName = plugin.getGUIConfig().getString(path, "ui.button.click");
        try {
            return Sound.valueOf(soundName.toUpperCase().replace(".", "_"));
        } catch (IllegalArgumentException e) {
            return Sound.UI_BUTTON_CLICK;
        }
    }

    // === Item Creation Methods ===
    
    private ItemStack createPlayerHead(Player player, PlayerData data) {
        Material material = getGUIMaterial("gui.main_menu.items.player_head.material");
        ItemStack head = new ItemStack(material);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(player);
        meta.setDisplayName(getGUIString("gui.main_menu.items.player_head.display_name", "player_name", player.getName()));

        List<String> lore = getGUILore("gui.main_menu.items.player_head.lore",
            "level", calculateLevel(data),
            "playtime", MessageUtils.formatTime(data.getTotalPlaytime()),
            "balance", MessageUtils.formatNumber(plugin.getVaultHook().getBalance(player.getUniqueId())),
            "status_lines", getStatusLines(player)
        );

        meta.setLore(lore);
        head.setItemMeta(meta);

        return head;
    }

    private String getStatusLines(Player player) {
        Government gov = plugin.getDataManager().getGovernment();
        if (gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId())) {
            return "§6§l👑 PRESIDEN";
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
    
    private ItemStack createPresidentItem(Government gov) {
        ItemStack item;

        if (gov.hasPresident()) {
            Material material = getGUIMaterial("gui.main_menu.items.president_item.default.material");
            item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            String presName = Bukkit.getOfflinePlayer(gov.getPresidentUUID()).getName();
            meta.setDisplayName(getGUIString("gui.main_menu.items.president_item.default.display_name"));

            List<String> lore = getGUILore("gui.main_menu.items.president_item.default.lore",
                "president_name", presName,
                "term", gov.getCurrentTerm(),
                "remaining_time", MessageUtils.formatTime(gov.getTermEndTime() - System.currentTimeMillis()),
                "rating", String.format("%.1f", gov.getApprovalRating())
            );

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            addGlow(item);
        } else {
            Material material = getGUIMaterial("gui.main_menu.items.president_item.no_president.material");
            item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(getGUIString("gui.main_menu.items.president_item.no_president.display_name"));

            List<String> lore = getGUILore("gui.main_menu.items.president_item.no_president.lore");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
    
    private ItemStack createCabinetItem(Government gov) {
        ItemStack item = createItem(Material.LECTERN, "§e§l📋 KABINET");
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
                lore.add("§7" + pos.getDisplayName() + ": §8Kosong");
            }
        }
        
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Terisi: §f" + filled + "/5");
        lore.add("");
        lore.add("§aKlik untuk detail kabinet");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createTreasuryItem(Treasury treasury) {
        ItemStack item = createItem(Material.GOLD_BLOCK, "§6§l💰 TREASURY");
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Saldo: §6" + MessageUtils.formatNumber(treasury.getBalance()));
        lore.add("§7Pemasukan: §a+" + MessageUtils.formatNumber(treasury.getTotalIncome()));
        lore.add("§7Pengeluaran: §c-" + MessageUtils.formatNumber(treasury.getTotalExpenses()));
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aKlik untuk detail treasury");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createActiveEffectsItem() {
        var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
        var activeDecisions = plugin.getDataManager().getActiveDecisions();
        int total = activeOrders.size() + activeDecisions.size();
        
        Material mat = total > 0 ? Material.BEACON : Material.GLASS;
        ItemStack item = createItem(mat, "§a§l✨ EFEK AKTIF: " + total);
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        if (total == 0) {
            lore.add("§7Tidak ada efek aktif");
        } else {
            if (!activeOrders.isEmpty()) {
                lore.add("§c§lExecutive Orders:");
                for (ExecutiveOrder order : activeOrders) {
                    lore.add("§7- " + order.getType().getDisplayName());
                    lore.add("  §8(" + MessageUtils.formatTime(order.getRemainingTime()) + ")");
                }
            }
            
            if (!activeDecisions.isEmpty()) {
                if (!activeOrders.isEmpty()) lore.add("");
                lore.add("§e§lKeputusan Kabinet:");
                for (var decision : activeDecisions) {
                    lore.add("§7- " + decision.getType().getDisplayName());
                    lore.add("  §8(" + MessageUtils.formatTime(decision.getRemainingTime()) + ")");
                }
            }
        }
        
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        if (total > 0) addGlow(item);
        
        return item;
    }
    
    private ItemStack createElectionItem(Election election, Player player) {
        ItemStack item = createItem(Material.PAPER, "§b§l🗳 PEMILU");
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Fase: §f" + election.getPhase().getDisplayName());
        
        if (election.isActive()) {
            lore.add("§7Sisa waktu: §f" + MessageUtils.formatTime(election.getPhaseEndTime() - System.currentTimeMillis()));
            lore.add("§7Kandidat: §f" + election.getCandidates().size());
            lore.add("§7Total votes: §f" + election.getTotalVotes());
            lore.add("");
            
            if (election.getPhase() == Election.ElectionPhase.VOTING) {
                if (election.hasVoted(player.getUniqueId())) {
                    lore.add("§a✓ Anda sudah vote");
                } else {
                    lore.add("§c✗ Anda belum vote!");
                }
            } else if (election.getPhase() == Election.ElectionPhase.REGISTRATION) {
                if (election.getCandidates().containsKey(player.getUniqueId())) {
                    lore.add("§a✓ Anda terdaftar kandidat");
                } else {
                    lore.add("§e→ Daftar jadi kandidat!");
                }
            }
        } else {
            lore.add("§7Tidak ada pemilu aktif");
        }
        
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aKlik untuk buka voting GUI");
        
        meta.setLore(lore);
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
        lore.add("§7Aktif: §f" + activeOrders.size() + " orders");
        lore.add("§7Biaya: §6" + MessageUtils.formatNumber(1000000L));
        lore.add("§7Cooldown: §f7 hari");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        if (isPresident) {
            lore.add("§a→ Anda dapat mengeluarkan orders!");
        } else {
            lore.add("§7Hanya Presiden yang bisa");
            lore.add("§7mengeluarkan Executive Orders");
        }
        
        lore.add("");
        lore.add("§aKlik untuk lihat orders");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createArenaItem(PlayerData data) {
        boolean isActive = plugin.getArenaManager().isArenaActive();
        Material mat = isActive ? Material.DIAMOND_SWORD : Material.IRON_SWORD;
        
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§4§l⚔ PRESIDENTIAL ARENA");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        if (isActive) {
            lore.add("§a§lSESI AKTIF!");
            lore.add("§7Pemain di arena: §f" + plugin.getArenaManager().getArenaPlayers().size());
        } else {
            lore.add("§7Status: §cTidak aktif");
        }
        
        lore.add("");
        lore.add("§7§lStatistik Anda:");
        lore.add("§7Kills: §a" + data.getArenaKills());
        lore.add("§7Deaths: §c" + data.getArenaDeaths());
        lore.add("§7Best Streak: §6" + data.getBestKillstreak());
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aKlik untuk info arena");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        if (isActive) addGlow(item);
        
        return item;
    }
    
    private ItemStack createRecallItem() {
        RecallPetition petition = plugin.getDataManager().getRecallPetition();
        boolean isActive = petition != null && 
            petition.getPhase() != RecallPetition.RecallPhase.COMPLETED &&
            petition.getPhase() != RecallPetition.RecallPhase.FAILED;
        
        Material mat = isActive ? Material.REDSTONE_TORCH : Material.TORCH;
        ItemStack item = createItem(mat, "§c§l⚠ SISTEM RECALL");
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        if (isActive) {
            lore.add("§c§lPETISI AKTIF!");
            lore.add("§7Fase: §f" + petition.getPhase().name());
            lore.add("§7Tanda tangan: §f" + petition.getSignatureCount());
            
            if (petition.getPhase() == RecallPetition.RecallPhase.VOTING) {
                lore.add("§7Vote Remove: §c" + petition.getRemoveVotes());
                lore.add("§7Vote Keep: §a" + petition.getKeepVotes());
            }
        } else {
            lore.add("§7Tidak ada petisi aktif");
            lore.add("");
            lore.add("§7Mulai petisi untuk");
            lore.add("§7recall presiden");
        }
        
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Deposit: §650,000");
        lore.add("§7Threshold: §f30% online");
        lore.add("");
        lore.add("§eKlik untuk info recall");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        if (isActive) addGlow(item);
        
        return item;
    }
    
    private ItemStack createHistoryItem() {
        var history = plugin.getDataManager().getAllPresidentHistory();
        
        ItemStack item = createItem(Material.BOOK, "§5§l📚 SEJARAH PRESIDEN");
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Total presiden: §f" + history.size());
        
        if (!history.isEmpty()) {
            lore.add("");
            lore.add("§7§lPresiden Terakhir:");
            int show = Math.min(3, history.size());
            for (int i = 0; i < show; i++) {
                var record = history.get(i);
                String name = Bukkit.getOfflinePlayer(record.getPlayerId()).getName();
                lore.add("§7- " + name + " §8(⭐" + String.format("%.1f", record.getApproval()) + ")");
            }
        }
        
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aKlik untuk lihat sejarah");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createMyStatsItem(PlayerData data) {
        ItemStack item = createItem(Material.COMPASS, "§b§l📊 STATISTIK SAYA");
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Votes Cast: §f" + data.getTotalVotesCast());
        lore.add("§7Endorsements: §f" + data.getEndorsementsGiven());
        lore.add("§7Jadi Presiden: §f" + data.getTimesServedAsPresident() + "x");
        lore.add("§7Jadi Menteri: §f" + data.getTimesServedAsCabinet() + "x");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aKlik untuk detail lengkap");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createLeaderboardItem() {
        ItemStack item = createItem(Material.GOLDEN_APPLE, "§6§l🏆 LEADERBOARD");
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Lihat pemain terbaik:");
        lore.add("§7- Arena Kills");
        lore.add("§7- Playtime");
        lore.add("§7- Presiden Terbaik");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aKlik untuk lihat leaderboard");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private ItemStack createHelpItem() {
        ItemStack item = createItem(Material.KNOWLEDGE_BOOK, "§a§l❓ PANDUAN & BANTUAN");
        ItemMeta meta = item.getItemMeta();
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Pelajari cara bermain:");
        lore.add("§7- Sistem Pemilu");
        lore.add("§7- Menjadi Presiden");
        lore.add("§7- Kabinet & Menteri");
        lore.add("§7- Executive Orders");
        lore.add("§7- Dan lainnya!");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aKlik untuk buka panduan");
        
        meta.setLore(lore);
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
    
    private void fillGlass(Inventory inv) {
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
    }
    
    private int calculateLevel(PlayerData data) {
        double hours = data.getTotalPlaytime() / (1000.0 * 60 * 60);
        return (int) (hours / 10) + 1;
    }
}
