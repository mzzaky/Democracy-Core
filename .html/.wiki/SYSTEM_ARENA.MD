# ⚔️ Sistem Arena Presiden (Presidential Arena)

Dokumen ini menjelaskan secara teknis mengenai **Presidential Arena Games**, sebuah fitur event PvP massal yang dapat diselenggarakan oleh Presiden sebagai sarana hiburan dan distribusi kekayaan bagi warga.

## 📋 Ikhtisar

Arena adalah zona pertempuran khusus (Free-For-All) di mana pemain dapat bertarung satu sama lain untuk memperebutkan hadiah uang tunai, item langka, dan kejayaan. Event ini berlangsung selama beberapa hari dan memiliki sistem *leaderboard* harian.

## 🔧 Mekanisme Aktivasi

Hanya Presiden yang dapat memulai sesi Arena baru.
*   **Perintah:** `/dc arena start`
*   **Biaya:** $100,000 (dari Kas Negara).
*   **Durasi:** 7 Hari (Default).
*   **Batasan:** Maksimal 2 kali penyelenggaraan per periode jabatan presiden.
*   **Lokasi:** Menggunakan area di sekitar World Spawn dengan radius 100 blok.

## 🎮 Gameplay

### Bergabung & Keluar
*   **Join:** `/dc arena join`
    *   Menyimpan lokasi pemain saat ini.
    *   Mengosongkan inventory pemain (disimpan).
    *   Memberikan **Arena Kit** standar.
    *   Teleportasi ke arena.
*   **Leave:** `/dc arena leave`
    *   Mengembalikan inventory asli pemain.
    *   Teleportasi kembali ke lokasi asal.

### Arena Kit (Standar)
Setiap peserta mendapatkan perlengkapan yang sama agar adil:
*   Full Iron Armor.
*   Iron Sword & Bow (32 Arrows).
*   3x Golden Apple & 16x Cooked Beef.

### Aturan Pertempuran
*   **Batas Wilayah:** Pemain yang keluar dari radius arena akan otomatis diteleportasi kembali ke tengah.
*   **Respawn:** Pemain yang mati akan hidup kembali di lokasi acak dalam arena setelah 3 detik, dengan efek *invulnerability* singkat.
*   **Hukuman Mati:** Pemain yang mati didenda **$5,000** (diambil dari saldo pribadi).

## 💰 Sistem Hadiah (Rewards)

### 1. Hadiah Kill (Per Kill)
Setiap membunuh pemain lain, pembunuh mendapatkan hadiah instan.
*   **Uang:** +$5,000 (Masuk ke saldo Vault).
*   **Streak Bonus:** Hadiah tambahan jika mencapai killstreak tertentu.

### 2. Killstreak Bonus
Pemain yang bertahan hidup lama dan membunuh banyak musuh berturut-turut mendapatkan *upgrade* gear dan buff.

| Streak | Hadiah Tambahan | Buffs |
| :--- | :--- | :--- |
| **5 Kills** | +$10,000 | 3x G-Apple, Speed I (30s) |
| **10 Kills** | +$25,000 | **Diamond Sword**, Strength I (30s) |
| **25 Kills** | +$50,000 | **Netherite Axe**, Strength II (60s) |
| **50 Kills** | +$100,000 | **Full Netherite Armor**, Strength/Resist (2m) |

### 3. Papan Peringkat Harian (Daily Leaderboard)
Setiap hari, top 10 pemain dengan kill terbanyak hari itu mendapatkan hadiah:
*   **Juara 1:** $100,000 + 3 Netherite Ingot
*   **Juara 2:** $75,000 + 2 Netherite Ingot
*   **Juara 3:** $60,000 + 2 Netherite Ingot
*   *(Hadiah menurun hingga posisi 10)*

### 4. Hadiah Utama (Grand Prize)
Diberikan kepada pemain dengan total kill terbanyak di akhir periode event (setelah 7 hari).
*   **Uang Tunai:** **$1,000,000**
*   **Trophy:** *Arena Champion Trophy* (Nether Star unik).
*   **Item:** *Champion's Chestplate* (Netherite Chestplate spesial).

## 📊 Manajemen Data

Data sesi arena disimpan dalam `DataManager` (`arena_session.json` mungkin, atau di dalam `data.yml`).
*   **Statistik:** Mencatat Kills, Deaths, Current Streak, Best Streak, dan Total Pendapatan per pemain.
*   **Leaderboard:** Menyimpan riwayat peringkat harian.

## ⚙️ Konfigurasi

Parameter arena dapat diubah di `config.yml`:

```yaml
arena:
  # Biaya memulai event
  start-cost: 100000
  
  # Durasi event (hari)
  duration-days: 7
  
  # Radius arena dari spawn (blok)
  radius: 100
  
  # Hadiah kill dasar
  kill-reward: 5000
  
  # Denda kematian
  death-penalty: 5000
  
  # Hadiah utama akhir event
  grand-prize: 1000000
```
