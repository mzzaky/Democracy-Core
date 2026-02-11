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
import id.democracycore.models.Government;
import id.democracycore.models.PlayerData;
import id.democracycore.utils.MessageUtils;

/**
 * Player Stats GUI - Menampilkan statistik lengkap pemain
 */
public class PlayerStatsGUI {
    
    private final DemocracyCore plugin;
    
    public static final String STATS_GUI_TITLE = "§b§l📊 STATISTIK PEMAIN 📊";
    public static final String LEADERBOARD_TITLE = "§6§l🏆 LEADERBOARD 🏆";
    
    public PlayerStatsGUI(DemocracyCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Buka menu statistik pemain
     */
    public void openPlayerStats(Player viewer, UUID targetUUID) {
        PlayerData data = plugin.getDataManager().getPlayerData(targetUUID);
        var offlinePlayer = Bukkit.getOfflinePlayer(targetUUID);
        String targetName = offlinePlayer.getName();
        
        Inventory inv = Bukkit.createInventory(null, 45, STATS_GUI_TITLE);
        
        // Player Head (Center top)
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(offlinePlayer);
        headMeta.setDisplayName("§b§l" + targetName);
        
        List<String> headLore = new ArrayList<>();
        headLore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        headLore.add("§7Level: §f" + calculateLevel(data));
        headLore.add("§7Total Playtime: §f" + MessageUtils.formatTime(data.getTotalPlaytime()));
        headLore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        // Status role
        Government gov = plugin.getDataManager().getGovernment();
        String role = "§7Warga Biasa";
        if (gov.hasPresident() && gov.getPresidentUUID().equals(targetUUID)) {
            role = "§6§l👑 PRESIDEN";
        } else {
            for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
                UUID minister = gov.getCabinetMember(pos.toGovernmentPosition());
                if (minister != null && minister.equals(targetUUID)) {
                    role = "§e§l🎖 " + pos.getDisplayName();
                    break;
                }
            }
        }
        headLore.add("§7Status: " + role);
        
        headMeta.setLore(headLore);
        head.setItemMeta(headMeta);
        inv.setItem(4, head);
        
        // === BARIS 2: Statistik Politik ===
        
        // Votes Cast
        ItemStack votesItem = createItem(Material.PAPER, "§a§l🗳 Suara Diberikan",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Total votes: §f" + data.getTotalVotesCast(),
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Partisipasi aktif dalam",
            "§7pemilihan presiden"
        );
        inv.setItem(10, votesItem);
        
        // Endorsements
        ItemStack endorseItem = createItem(Material.GOLDEN_APPLE, "§6§l⭐ Endorsements",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Diberikan: §f" + data.getEndorsementsGiven(),
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Dukungan untuk kandidat",
            "§7dalam pemilu"
        );
        inv.setItem(12, endorseItem);
        
        // Times President
        ItemStack presItem = createItem(Material.GOLDEN_HELMET, "§6§l👑 Jabatan Presiden",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Kali menjabat: §f" + data.getTimesServedAsPresident() + "x",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pengalaman sebagai",
            "§7pemimpin negara"
        );
        inv.setItem(14, presItem);
        
        // Times Cabinet
        ItemStack cabItem = createItem(Material.IRON_HELMET, "§e§l🎖 Jabatan Menteri",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Kali menjabat: §f" + data.getTimesServedAsCabinet() + "x",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pengalaman sebagai",
            "§7anggota kabinet"
        );
        inv.setItem(16, cabItem);
        
        // === BARIS 3: Statistik Arena ===
        
        // Arena Kills
        ItemStack killsItem = createItem(Material.DIAMOND_SWORD, "§c§l⚔ Arena Kills",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Total kills: §a" + data.getArenaKills(),
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(19, killsItem);
        
        // Arena Deaths
        ItemStack deathsItem = createItem(Material.SKELETON_SKULL, "§4§l💀 Arena Deaths",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Total deaths: §c" + data.getArenaDeaths(),
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(21, deathsItem);
        
        // K/D Ratio
        double kd = data.getArenaDeaths() > 0 ? 
            (double) data.getArenaKills() / data.getArenaDeaths() : data.getArenaKills();
        String kdColor = kd >= 2.0 ? "§a" : (kd >= 1.0 ? "§e" : "§c");
        ItemStack kdItem = createItem(Material.EXPERIENCE_BOTTLE, "§e§l📈 K/D Ratio",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Ratio: " + kdColor + String.format("%.2f", kd),
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(23, kdItem);
        
        // Kill Streak
        ItemStack streakItem = createItem(Material.BLAZE_ROD, "§6§l🔥 Kill Streak",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Current: §f" + data.getCurrentKillstreak(),
            "§7Best: §6" + data.getBestKillstreak(),
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(25, streakItem);
        
        // === BARIS 4: Informasi Tambahan ===
        
        // Level Progress
        int currentLevel = calculateLevel(data);
        double hoursPlayed = data.getTotalPlaytime() / (1000.0 * 60 * 60);
        double progress = (hoursPlayed % 10) / 10 * 100;
        
        ItemStack levelItem = createItem(Material.ENCHANTING_TABLE, "§d§l✦ Level Progress",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Level saat ini: §d" + currentLevel,
            "§7Progress ke next: §f" + String.format("%.1f", progress) + "%",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7(10 jam playtime = 1 level)"
        );
        inv.setItem(30, levelItem);
        
        // Online Status
        boolean isOnline = offlinePlayer.isOnline();
        Material statusMat = isOnline ? Material.LIME_DYE : Material.GRAY_DYE;
        String statusText = isOnline ? "§a§lONLINE" : "§7§lOFFLINE";
        ItemStack statusItem = createItem(statusMat, statusText,
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            isOnline ? "§7Sedang bermain" : "§7Terakhir login:",
            isOnline ? "" : "§f" + (offlinePlayer.getLastPlayed() > 0 ? 
                MessageUtils.formatTime(System.currentTimeMillis() - offlinePlayer.getLastPlayed()) + " yang lalu" : "N/A"),
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(32, statusItem);
        
        // === Footer ===
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu utama");
        inv.setItem(36, backItem);
        
        // Leaderboard button
        ItemStack lbItem = createItem(Material.GOLD_INGOT, "§6§lLeaderboard", "§7Lihat peringkat pemain");
        inv.setItem(40, lbItem);
        
        // Close button
        ItemStack closeItem = createItem(Material.BARRIER, "§c§lTutup", "§7Tutup menu");
        inv.setItem(44, closeItem);
        
        fillGlass(inv);
        viewer.openInventory(inv);
    }
    
    /**
     * Buka leaderboard
     */
    public void openLeaderboard(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, LEADERBOARD_TITLE);
        
        List<PlayerData> allPlayers = new ArrayList<>(plugin.getDataManager().getAllPlayerData());
        
        // === Top Kills ===
        List<PlayerData> topKills = new ArrayList<>(allPlayers);
        topKills.sort((a, b) -> Integer.compare(b.getArenaKills(), a.getArenaKills()));
        
        ItemStack killsHeader = createItem(Material.DIAMOND_SWORD, "§c§l⚔ TOP KILLS",
            "§7Pemain dengan kill terbanyak");
        inv.setItem(1, killsHeader);
        
        int slot = 10;
        for (int i = 0; i < Math.min(5, topKills.size()); i++) {
            PlayerData data = topKills.get(i);
            ItemStack item = createLeaderboardEntry(i + 1, data.getUuid(), 
                "§a" + data.getArenaKills() + " kills", Material.PLAYER_HEAD);
            inv.setItem(slot, item);
            slot += 9;
        }
        
        // === Top Playtime ===
        List<PlayerData> topPlaytime = new ArrayList<>(allPlayers);
        topPlaytime.sort((a, b) -> Long.compare(b.getTotalPlaytime(), a.getTotalPlaytime()));
        
        ItemStack playtimeHeader = createItem(Material.CLOCK, "§b§l⏰ TOP PLAYTIME",
            "§7Pemain dengan playtime terlama");
        inv.setItem(4, playtimeHeader);
        
        slot = 13;
        for (int i = 0; i < Math.min(5, topPlaytime.size()); i++) {
            PlayerData data = topPlaytime.get(i);
            ItemStack item = createLeaderboardEntry(i + 1, data.getUuid(), 
                "§f" + MessageUtils.formatTime(data.getTotalPlaytime()), Material.PLAYER_HEAD);
            inv.setItem(slot, item);
            slot += 9;
        }
        
        // === Top Presidents ===
        List<PlayerData> topPresidents = new ArrayList<>(allPlayers);
        topPresidents.sort((a, b) -> Integer.compare(b.getTimesServedAsPresident(), a.getTimesServedAsPresident()));
        topPresidents.removeIf(d -> d.getTimesServedAsPresident() == 0);
        
        ItemStack presHeader = createItem(Material.GOLDEN_HELMET, "§6§l👑 TOP PRESIDEN",
            "§7Pemain paling sering jadi presiden");
        inv.setItem(7, presHeader);
        
        slot = 16;
        for (int i = 0; i < Math.min(5, topPresidents.size()); i++) {
            PlayerData data = topPresidents.get(i);
            ItemStack item = createLeaderboardEntry(i + 1, data.getUuid(), 
                "§6" + data.getTimesServedAsPresident() + "x presiden", Material.PLAYER_HEAD);
            inv.setItem(slot, item);
            slot += 9;
        }
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu utama");
        inv.setItem(45, backItem);
        
        // Close button
        ItemStack closeItem = createItem(Material.BARRIER, "§c§lTutup", "§7Tutup menu");
        inv.setItem(53, closeItem);
        
        fillGlass(inv);
        player.openInventory(inv);
    }
    
    private ItemStack createLeaderboardEntry(int rank, UUID playerUUID, String stat, Material fallback) {
        var offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        String name = offlinePlayer.getName();
        
        String rankColor;
        Material material;
        switch (rank) {
            case 1 -> { rankColor = "§6§l"; material = Material.GOLD_BLOCK; }
            case 2 -> { rankColor = "§7§l"; material = Material.IRON_BLOCK; }
            case 3 -> { rankColor = "§c§l"; material = Material.COPPER_BLOCK; }
            default -> { rankColor = "§f"; material = Material.STONE; }
        }
        
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(offlinePlayer);
        meta.setDisplayName(rankColor + "#" + rank + " §f" + name);
        
        List<String> lore = new ArrayList<>();
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add(stat);
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§aKlik untuk lihat profil");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
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
    
    private int calculateLevel(PlayerData data) {
        double hours = data.getTotalPlaytime() / (1000.0 * 60 * 60);
        return (int) (hours / 10) + 1;
    }
}
