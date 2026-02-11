package id.democracycore.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import id.democracycore.DemocracyCore;

/**
 * Help GUI - Panduan dan bantuan untuk semua fitur DemocracyCore
 */
public class HelpGUI {
    
    private final DemocracyCore plugin;
    
    public static final String HELP_MENU_TITLE = "§a§l❓ PANDUAN DEMOCRACY CORE ❓";
    public static final String HELP_ELECTION_TITLE = "§b§l📖 PANDUAN PEMILU";
    public static final String HELP_PRESIDENT_TITLE = "§6§l📖 PANDUAN PRESIDEN";
    public static final String HELP_CABINET_TITLE = "§e§l📖 PANDUAN KABINET";
    public static final String HELP_ORDERS_TITLE = "§c§l📖 PANDUAN EXECUTIVE ORDERS";
    public static final String HELP_ARENA_TITLE = "§4§l📖 PANDUAN ARENA";
    public static final String HELP_TREASURY_TITLE = "§6§l📖 PANDUAN TREASURY";
    
    public HelpGUI(DemocracyCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Buka menu bantuan utama
     */
    public void openHelpMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, HELP_MENU_TITLE);
        
        // Header
        ItemStack headerItem = createItem(Material.KNOWLEDGE_BOOK, "§a§l✦ SELAMAT DATANG ✦",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§fDemocracyCore adalah plugin",
            "§fsimulasi demokrasi dan",
            "§fpemerintahan di Minecraft!",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pilih topik di bawah untuk",
            "§7mempelajari fitur-fitur plugin"
        );
        inv.setItem(4, headerItem);
        
        // === Topik Bantuan ===
        
        // Pemilu
        ItemStack electionHelp = createItem(Material.PAPER, "§b§l🗳 SISTEM PEMILU",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pelajari tentang:",
            "§f• Fase-fase pemilu",
            "§f• Cara mendaftar kandidat",
            "§f• Cara endorsement",
            "§f• Cara voting",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§aKlik untuk buka panduan"
        );
        inv.setItem(19, electionHelp);
        
        // Presiden
        ItemStack presidentHelp = createItem(Material.GOLDEN_HELMET, "§6§l👑 MENJADI PRESIDEN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pelajari tentang:",
            "§f• Syarat jadi presiden",
            "§f• Kekuasaan presiden",
            "§f• Buff & reward presiden",
            "§f• Masa jabatan",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§aKlik untuk buka panduan"
        );
        inv.setItem(21, presidentHelp);
        
        // Kabinet
        ItemStack cabinetHelp = createItem(Material.LECTERN, "§e§l📋 SISTEM KABINET",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pelajari tentang:",
            "§f• Posisi-posisi kabinet",
            "§f• Tugas setiap menteri",
            "§f• Keputusan kabinet",
            "§f• Buff & reward menteri",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§aKlik untuk buka panduan"
        );
        inv.setItem(23, cabinetHelp);
        
        // Executive Orders
        ItemStack ordersHelp = createItem(Material.WRITABLE_BOOK, "§c§l📜 EXECUTIVE ORDERS",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pelajari tentang:",
            "§f• Jenis-jenis orders",
            "§f• Efek setiap order",
            "§f• Biaya & cooldown",
            "§f• Cara mengeluarkan order",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§aKlik untuk buka panduan"
        );
        inv.setItem(25, ordersHelp);
        
        // Arena
        ItemStack arenaHelp = createItem(Material.IRON_SWORD, "§4§l⚔ PRESIDENTIAL ARENA",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pelajari tentang:",
            "§f• Cara join arena",
            "§f• Aturan pertarungan",
            "§f• Reward & killstreak",
            "§f• Leaderboard",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§aKlik untuk buka panduan"
        );
        inv.setItem(29, arenaHelp);
        
        // Treasury
        ItemStack treasuryHelp = createItem(Material.GOLD_BLOCK, "§6§l💰 TREASURY",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pelajari tentang:",
            "§f• Sumber pemasukan negara",
            "§f• Penggunaan dana negara",
            "§f• Cara donasi",
            "§f• Transparansi keuangan",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§aKlik untuk buka panduan"
        );
        inv.setItem(31, treasuryHelp);
        
        // Recall
        ItemStack recallHelp = createItem(Material.REDSTONE_TORCH, "§c§l⚠ RECALL PRESIDEN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pelajari tentang:",
            "§f• Cara memulai recall",
            "§f• Proses petisi",
            "§f• Voting recall",
            "§f• Threshold & deposit",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§aKlik untuk buka panduan"
        );
        inv.setItem(33, recallHelp);
        
        // Command List
        ItemStack commandsItem = createItem(Material.COMMAND_BLOCK, "§d§l💻 DAFTAR COMMAND",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Lihat semua command:",
            "§f/dc menu §8- Buka menu utama",
            "§f/dc vote §8- Vote kandidat",
            "§f/dc register §8- Daftar kandidat",
            "§f/dc help §8- Bantuan command",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§aKlik untuk daftar lengkap"
        );
        inv.setItem(40, commandsItem);
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu utama");
        inv.setItem(36, backItem);
        
        // Close button
        ItemStack closeItem = createItem(Material.BARRIER, "§c§lTutup", "§7Tutup menu");
        inv.setItem(44, closeItem);
        
        fillGlass(inv);
        player.openInventory(inv);
    }
    
    /**
     * Buka panduan pemilu
     */
    public void openElectionHelp(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, HELP_ELECTION_TITLE);
        
        // Header
        ItemStack header = createItem(Material.PAPER, "§b§l🗳 SISTEM PEMILU",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Pemilu adalah cara untuk",
            "§7memilih presiden baru",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(4, header);
        
        // Fase Registration
        ItemStack phase1 = createItem(Material.EMERALD, "§a§l1️⃣ FASE PENDAFTARAN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Durasi: §f3 hari",
            "",
            "§7Pada fase ini pemain dapat:",
            "§f• Mendaftar sebagai kandidat",
            "§f• Memberikan endorsement",
            "",
            "§7Syarat daftar kandidat:",
            "§f• Level minimal 100",
            "§f• Playtime 100+ jam",
            "§f• Balance 500,000+",
            "§f• 10+ endorsements",
            "§f• Biaya pendaftaran 500,000",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(19, phase1);
        
        // Fase Campaign
        ItemStack phase2 = createItem(Material.GOLDEN_APPLE, "§6§l2️⃣ FASE KAMPANYE",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Durasi: §f7 hari",
            "",
            "§7Pada fase ini kandidat dapat:",
            "§f• Broadcast pesan kampanye",
            "§f• Mengumpulkan endorsement",
            "§f• Membangun campaign points",
            "",
            "§7Biaya broadcast: §f10,000",
            "§7Cooldown broadcast: §f6 jam",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(21, phase2);
        
        // Fase Voting
        ItemStack phase3 = createItem(Material.LIME_WOOL, "§a§l3️⃣ FASE VOTING",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Durasi: §f3 hari",
            "",
            "§7Pada fase ini pemain dapat:",
            "§f• Memberikan suara",
            "§f• Vote tidak bisa dibatalkan!",
            "",
            "§7Berat vote ditentukan oleh:",
            "§f• Playtime (+0.5 jika 200+ jam)",
            "§f• Level (+0.5 jika 75+)",
            "§f• Balance (+0.5 jika 1M+)",
            "§f• Max weight: 2.5x",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(23, phase3);
        
        // Fase Inauguration
        ItemStack phase4 = createItem(Material.GOLDEN_HELMET, "§e§l4️⃣ FASE PELANTIKAN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Durasi: §f1 hari",
            "",
            "§7Pada fase ini:",
            "§f• Pemenang diumumkan",
            "§f• Presiden baru dilantik",
            "§f• Reward diberikan",
            "",
            "§7Reward voter:",
            "§f• 10,000 partisipasi",
            "§f• 50,000 lottery (10 pemenang)",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(25, phase4);
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu bantuan");
        inv.setItem(45, backItem);
        
        fillGlass(inv);
        player.openInventory(inv);
    }
    
    /**
     * Buka panduan presiden
     */
    public void openPresidentHelp(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, HELP_PRESIDENT_TITLE);
        
        // Header
        ItemStack header = createItem(Material.GOLDEN_HELMET, "§6§l👑 MENJADI PRESIDEN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Presiden adalah pemimpin",
            "§7tertinggi dalam negara",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(4, header);
        
        // Kekuasaan
        ItemStack powers = createItem(Material.DIAMOND, "§b§lKEKUASAAN PRESIDEN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Presiden dapat:",
            "§f• Mengeluarkan Executive Orders",
            "§f• Mengangkat menteri kabinet",
            "§f• Memecat menteri kabinet",
            "§f• Memulai Presidential Games",
            "§f• Menggunakan dana treasury",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(19, powers);
        
        // Buffs
        ItemStack buffs = createItem(Material.BEACON, "§a§lBUFF PRESIDEN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Presiden mendapat buff:",
            "§f• +15% Damage",
            "§f• +12% Defense",
            "§f• +20% Vault earning",
            "§f• +10% XP gain",
            "§f• +2 Extra hearts",
            "§f• Hunger immunity",
            "§f• Night vision",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(21, buffs);
        
        // Daily Rewards
        ItemStack rewards = createItem(Material.CHEST, "§6§lDAILY REWARDS",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Presiden mendapat setiap hari:",
            "§f• 50,000 Vault points",
            "§f• 5 Diamond blocks",
            "§f• 3 Netherite ingots",
            "§f• 10 Enchanted golden apples",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(23, rewards);
        
        // Masa Jabatan
        ItemStack term = createItem(Material.CLOCK, "§e§lMASA JABATAN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Informasi jabatan:",
            "§f• Durasi: 30 hari",
            "§f• Max berturut: 2 term",
            "§f• Cooldown: 1 term",
            "",
            "§7Presiden dapat di-recall jika:",
            "§f• Approval rating rendah",
            "§f• Tidak aktif 7+ hari",
            "§f• Petisi recall berhasil",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(25, term);
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu bantuan");
        inv.setItem(45, backItem);
        
        fillGlass(inv);
        player.openInventory(inv);
    }
    
    /**
     * Buka panduan kabinet
     */
    public void openCabinetHelp(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, HELP_CABINET_TITLE);
        
        // Header
        ItemStack header = createItem(Material.LECTERN, "§e§l📋 SISTEM KABINET",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Kabinet adalah tim pembantu",
            "§7presiden dalam menjalankan",
            "§7pemerintahan",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(4, header);
        
        // Defense
        ItemStack defense = createItem(Material.IRON_SWORD, "§4§lMENTERI PERTAHANAN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Fokus: §fKemiliteran & PvP",
            "",
            "§7Buff yang didapat:",
            "§f• +12% Damage",
            "§f• +10% Defense",
            "§f• +15% Vault",
            "§f• +1.5 Extra hearts",
            "",
            "§7Gaji harian: §630,000",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(10, defense);
        
        // Treasury
        ItemStack treasury = createItem(Material.GOLD_INGOT, "§6§lMENTERI KEUANGAN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Fokus: §fEkonomi & Treasury",
            "",
            "§7Buff yang didapat:",
            "§f• +25% Vault earning",
            "§f• +10% Sell bonus",
            "§f• -10% Buy discount",
            "",
            "§7Gaji harian: §650,000",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(12, treasury);
        
        // Commerce
        ItemStack commerce = createItem(Material.EMERALD, "§2§lMENTERI PERDAGANGAN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Fokus: §fPerdagangan & Loot",
            "",
            "§7Buff yang didapat:",
            "§f• +20% Vault earning",
            "§f• +15% Rare drop bonus",
            "",
            "§7Gaji harian: §635,000",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(14, commerce);
        
        // Infrastructure
        ItemStack infra = createItem(Material.IRON_PICKAXE, "§7§lMENTERI INFRASTRUKTUR",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Fokus: §fPembangunan & Claim",
            "",
            "§7Buff yang didapat:",
            "§f• +20% Vault earning",
            "§f• 2x Claim blocks",
            "",
            "§7Gaji harian: §630,000",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(16, infra);
        
        // Culture
        ItemStack culture = createItem(Material.BOOK, "§d§lMENTERI KEBUDAYAAN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Fokus: §fEvent & Komunitas",
            "",
            "§7Buff yang didapat:",
            "§f• +15% XP gain",
            "§f• +10% Vault earning",
            "§f• AFK bypass",
            "",
            "§7Gaji harian: §630,000",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(28, culture);
        
        // Keputusan Kabinet
        ItemStack decisions = createItem(Material.ENCHANTED_BOOK, "§d§lKEPUTUSAN KABINET",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Setiap menteri dapat",
            "§7mengeluarkan keputusan",
            "§7sesuai bidangnya",
            "",
            "§7Info keputusan:",
            "§f• Biaya: 500,000",
            "§f• Cooldown: 48 jam",
            "§f• Durasi: bervariasi",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(31, decisions);
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu bantuan");
        inv.setItem(45, backItem);
        
        fillGlass(inv);
        player.openInventory(inv);
    }
    
    /**
     * Buka panduan executive orders
     */
    public void openOrdersHelp(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, HELP_ORDERS_TITLE);
        
        // Header
        ItemStack header = createItem(Material.WRITABLE_BOOK, "§c§l📜 EXECUTIVE ORDERS",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Executive Orders adalah",
            "§7keputusan khusus yang hanya",
            "§7bisa dikeluarkan presiden",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(4, header);
        
        // Info umum
        ItemStack info = createItem(Material.BOOK, "§e§lINFORMASI UMUM",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Biaya: §61,000,000",
            "§7Cooldown: §f7 hari",
            "§7Durasi: §fBervariasi",
            "",
            "§7Order mempengaruhi",
            "§7seluruh server!",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(22, info);
        
        // List beberapa order sebagai contoh
        ItemStack order1 = createItem(Material.DIAMOND_SWORD, "§c§lMARTIAL LAW",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Efek: §fPvP damage +20%",
            "§7Durasi: §f24 jam",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(19, order1);
        
        ItemStack order2 = createItem(Material.GOLD_BLOCK, "§6§lECONOMIC STIMULUS",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Efek: §fVault bonus +30%",
            "§7Durasi: §f48 jam",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(21, order2);
        
        ItemStack order3 = createItem(Material.EXPERIENCE_BOTTLE, "§a§lEDUCATION REFORM",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Efek: §fXP gain +25%",
            "§7Durasi: §f72 jam",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(23, order3);
        
        ItemStack order4 = createItem(Material.IRON_CHESTPLATE, "§7§lDEFENSE MOBILIZATION",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Efek: §fDefense +15%",
            "§7Durasi: §f24 jam",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(25, order4);
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu bantuan");
        inv.setItem(45, backItem);
        
        fillGlass(inv);
        player.openInventory(inv);
    }
    
    /**
     * Buka panduan arena
     */
    public void openArenaHelp(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, HELP_ARENA_TITLE);
        
        // Header
        ItemStack header = createItem(Material.IRON_SWORD, "§4§l⚔ PRESIDENTIAL ARENA",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Arena PvP yang diadakan",
            "§7oleh Presiden!",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(4, header);
        
        // Cara Join
        ItemStack join = createItem(Material.LIME_WOOL, "§a§lCARA BERGABUNG",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Gunakan command:",
            "§f/dc arena join",
            "",
            "§7Atau klik tombol di menu",
            "§7saat sesi arena aktif",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(19, join);
        
        // Aturan
        ItemStack rules = createItem(Material.BOOK, "§e§lATURAN ARENA",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§f• PvP enabled di area arena",
            "§f• Death penalty: 5,000",
            "§f• Safe zone immunity: 10 detik",
            "§f• AFK kick: 3 menit",
            "§f• Keep inventory ON",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(21, rules);
        
        // Rewards
        ItemStack rewards = createItem(Material.GOLD_INGOT, "§6§lKILLSTREAK REWARDS",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§75 kills: §610,000",
            "§710 kills: §625,000",
            "§725 kills: §6100,000",
            "§750 kills: §6250,000",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(23, rewards);
        
        // Leave
        ItemStack leave = createItem(Material.RED_WOOL, "§c§lKELUAR ARENA",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Gunakan command:",
            "§f/dc arena leave",
            "",
            "§7Atau keluar dari area",
            "§7arena secara manual",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(25, leave);
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu bantuan");
        inv.setItem(45, backItem);
        
        fillGlass(inv);
        player.openInventory(inv);
    }
    
    /**
     * Buka panduan treasury
     */
    public void openTreasuryHelp(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, HELP_TREASURY_TITLE);
        
        // Header
        ItemStack header = createItem(Material.GOLD_BLOCK, "§6§l💰 TREASURY NEGARA",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Treasury adalah kas",
            "§7keuangan negara",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(4, header);
        
        // Sumber Pemasukan
        ItemStack income = createItem(Material.EMERALD, "§a§lSUMBER PEMASUKAN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Treasury diisi dari:",
            "§f• Pajak transaksi (5%)",
            "§f• Biaya pendaftaran kandidat",
            "§f• Donasi pemain",
            "§f• Event rewards",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(19, income);
        
        // Pengeluaran
        ItemStack expense = createItem(Material.REDSTONE, "§c§lPENGELUARAN",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Treasury digunakan untuk:",
            "§f• Executive Orders",
            "§f• Presidential Games",
            "§f• Keputusan Kabinet",
            "§f• Gaji menteri",
            "§f• Reward presiden",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(21, expense);
        
        // Cara Donasi
        ItemStack donate = createItem(Material.HOPPER, "§e§lCARA DONASI",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Gunakan command:",
            "§f/dc treasury donate <jumlah>",
            "",
            "§7Donasi Anda akan membantu",
            "§7pembangunan negara!",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(23, donate);
        
        // Transparansi
        ItemStack transparency = createItem(Material.GLASS, "§b§lTRANSPARASI",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "§7Semua transaksi dicatat!",
            "",
            "§7Lihat di menu Treasury:",
            "§f/dc treasury",
            "",
            "§7Atau buka dari main menu",
            "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );
        inv.setItem(25, transparency);
        
        // Back button
        ItemStack backItem = createItem(Material.ARROW, "§7§lKembali", "§7Kembali ke menu bantuan");
        inv.setItem(36, backItem);
        
        fillGlass(inv);
        player.openInventory(inv);
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
    
    private void fillGlass(Inventory inv) {
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
    }
}
