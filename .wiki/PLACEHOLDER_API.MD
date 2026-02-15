# PlaceholderAPI Integration - DemocracyCore

## 📋 Daftar Isi
1. [Pendahuluan](#pendahuluan)
2. [Persyaratan](#persyaratan)
3. [Instalasi](#instalasi)
4. [Daftar Placeholder](#daftar-placeholder)
5. [Contoh Penggunaan](#contoh-penggunaan)
6. [Implementasi Teknis](#implementasi-teknis)
7. [Troubleshooting](#troubleshooting)

---

## 📖 Pendahuluan

DemocracyCore menyediakan integrasi lengkap dengan **PlaceholderAPI**, memungkinkan Anda untuk menampilkan data pemerintahan, pemilihan, statistik pemain, treasury, dan arena di plugin lain seperti scoreboard, chat, tab list, dan hologram.

### Fitur Utama
- **40+ placeholder** yang mencakup semua aspek plugin
- Dukungan real-time untuk data pemerintahan dan pemilihan
- Statistik pemain yang komprehensif
- Data treasury terperinci
- Statistik arena PvP

---

## ⚙️ Persyaratan

### Plugin yang Diperlukan
1. **PlaceholderAPI** v2.11.6 atau lebih baru
2. **Vault** v1.7 atau lebih baru (untuk ekonomi)
3. **DemocracyCore** v1.3.0 atau lebih baru

### Kompatibilitas
- **Minecraft**: 1.21+
- **Server**: Paper/Spigot
- **Java**: 21+

---

## 🔧 Instalasi

### Langkah 1: Install PlaceholderAPI
```bash
# Download dari SpigotMC atau gunakan command in-game:
/papi ecloud download PlaceholderAPI
```

### Langkah 2: Verifikasi Instalasi
Setelah server restart, cek log untuk konfirmasi:
```
[DemocracyCore] PlaceholderAPI found! Registered placeholders.
```

### Langkah 3: Test Placeholder
```
/papi parse me %democracy_president%
```

---

## 📊 Daftar Placeholder

### 🏛️ Government Placeholders

#### Informasi Presiden
| Placeholder | Deskripsi | Contoh Output | Tipe Return |
|------------|-----------|---------------|-------------|
| `%democracy_president%` | Nama presiden saat ini | `Steve` atau `None` | String |
| `%democracy_term_time%` | Sisa waktu masa jabatan | `15d 5h 30m` | String (formatted) |
| `%democracy_approval_rating%` | Rating persetujuan presiden (1-5) | `4.2` | Decimal |
| `%democracy_consecutive_terms%` | Jumlah masa jabatan berturut-turut | `2` | Integer |
| `%democracy_salary_payouts%` | Total pembayaran gaji periode ini | `1500000.00` | Decimal |

#### Kabinet Menteri
| Placeholder | Deskripsi | Contoh Output |
|------------|-----------|---------------|
| `%democracy_defense_minister%` | Menteri Pertahanan | `Alex` atau `None` |
| `%democracy_treasury_minister%` | Menteri Keuangan | `Bob` atau `None` |
| `%democracy_commerce_minister%` | Menteri Perdagangan | `Charlie` atau `None` |
| `%democracy_infrastructure_minister%` | Menteri Infrastruktur | `Diana` atau `None` |
| `%democracy_culture_minister%` | Menteri Kebudayaan | `Eve` atau `None` |

---

### 🗳️ Election Placeholders

#### Informasi Pemilihan
| Placeholder | Deskripsi | Contoh Output | Tipe Return |
|------------|-----------|---------------|-------------|
| `%democracy_phase%` | Fase pemilihan saat ini | `Voting`, `Campaign`, `None` | String |
| `%democracy_phase_time%` | Sisa waktu fase pemilihan | `2d 8h 15m` | String (formatted) |
| `%democracy_election_active%` | Status pemilihan aktif | `Yes` atau `No` | String |
| `%democracy_candidates_count%` | Jumlah kandidat terdaftar | `3` | Integer |
| `%democracy_total_votes%` | Total suara yang masuk | `45` | Integer |

---

### 💰 Treasury Placeholders

| Placeholder | Deskripsi | Contoh Output | Tipe Return |
|------------|-----------|---------------|-------------|
| `%democracy_treasury%` | Saldo treasury saat ini | `5000000.00` | Decimal |
| `%democracy_treasury_income%` | Total pemasukan treasury | `8000000.00` | Decimal |
| `%democracy_treasury_expenses%` | Total pengeluaran treasury | `3000000.00` | Decimal |

---

### 👤 Player-Specific Placeholders

#### Peran Pemerintahan (Player)
| Placeholder | Deskripsi | Contoh Output | Tipe Return |
|------------|-----------|---------------|-------------|
| `%democracy_is_president%` | Apakah player adalah presiden | `Yes` atau `No` | String |
| `%democracy_is_cabinet%` | Apakah player anggota kabinet | `Yes` atau `No` | String |
| `%democracy_cabinet_position%` | Posisi kabinet player | `Menteri Pertahanan` atau `None` | String |

#### Statistik Lifetime Player
| Placeholder | Deskripsi | Contoh Output | Tipe Return |
|------------|-----------|---------------|-------------|
| `%democracy_votes_cast%` | Total suara yang diberikan (sepanjang waktu) | `12` | Integer |
| `%democracy_times_ran%` | Berapa kali mencalonkan diri | `3` | Integer |
| `%democracy_times_president%` | Berapa kali menjabat sebagai presiden | `1` | Integer |
| `%democracy_times_cabinet%` | Berapa kali menjabat sebagai menteri | `2` | Integer |
| `%democracy_endorsements_given%` | Total endorsement yang diberikan | `15` | Integer |
| `%democracy_endorsements_received%` | Total endorsement yang diterima | `20` | Integer |
| `%democracy_total_donations%` | Total donasi ke treasury | `250000.00` | Decimal |
| `%democracy_playtime_hours%` | Total waktu bermain (jam) | `125.5` | Decimal |

#### Partisipasi Pemilihan (Current)
| Placeholder | Deskripsi | Contoh Output | Tipe Return |
|------------|-----------|---------------|-------------|
| `%democracy_has_voted%` | Sudah vote di pemilihan aktif | `Yes` atau `No` | String |
| `%democracy_is_candidate%` | Apakah player adalah kandidat | `Yes` atau `No` | String |

#### Data Kandidat (Jika Player adalah Kandidat)
| Placeholder | Deskripsi | Contoh Output | Tipe Return |
|------------|-----------|---------------|-------------|
| `%democracy_candidate_votes%` | Jumlah suara yang diterima | `23.5` | Decimal (weighted) |
| `%democracy_candidate_endorsements%` | Jumlah endorsement yang diterima | `18` | Integer |
| `%democracy_candidate_slogan%` | Slogan kandidat | `Make Server Great!` | String |

---

### ⚔️ Arena PvP Placeholders

| Placeholder | Deskripsi | Contoh Output | Tipe Return |
|------------|-----------|---------------|-------------|
| `%democracy_arena_kills%` | Total kill di arena | `45` | Integer |
| `%democracy_arena_deaths%` | Total death di arena | `12` | Integer |
| `%democracy_arena_kd%` | Kill/Death ratio | `3.75` | Decimal |
| `%democracy_killstreak%` | Killstreak saat ini | `5` | Integer |
| `%democracy_best_killstreak%` | Killstreak terbaik | `15` | Integer |

---

## 💡 Contoh Penggunaan

### 1. Scoreboard (DeluxeMenus/FeatherBoard)

```yaml
# Contoh scoreboard dengan FeatherBoard
scoreboard:
  title: "&6&lDemocracy Server"
  lines:
    - "&7-------------------"
    - "&ePresident: &f%democracy_president%"
    - "&eTerm: &f%democracy_term_time%"
    - "&eRating: &f%democracy_approval_rating%/5.0"
    - ""
    - "&6Treasury: &f$%democracy_treasury%"
    - ""
    - "&aElection: &f%democracy_phase%"
    - "&aPhase Time: &f%democracy_phase_time%"
    - "&aCandidates: &f%democracy_candidates_count%"
    - ""
    - "&bYour Role: &f%democracy_cabinet_position%"
    - "&bVotes Cast: &f%democracy_votes_cast%"
    - "&7-------------------"
```

### 2. Chat Prefix (Chat Plugin)

```yaml
# Contoh untuk LuckPerms meta atau Vault chat
# Prefix untuk Presiden
- /lp group president meta addprefix "&6[President] "

# Atau gunakan conditional PlaceholderAPI:
prefix: "%democracy_is_president:&6[President] :%%democracy_cabinet_position%"
```

### 3. Tab List (TAB Plugin)

```yaml
# TAB plugin configuration
tablist-name-formatting:
  player-name-format: "%democracy_is_president:&6&l:%%democracy_is_cabinet:&e:%player_name%"
```

### 4. Hologram (HolographicDisplays)

```yaml
# Contoh hologram untuk info pemerintahan
/hd create government
/hd addline government &6&lCURRENT GOVERNMENT
/hd addline government &ePresident: &f%democracy_president%
/hd addline government &eApproval: &f%democracy_approval_rating%/5.0
/hd addline government &eTreasury: &f$%democracy_treasury%
/hd addline government ""
/hd addline government &6&lCABINET
/hd addline government &eDefense: &f%democracy_defense_minister%
/hd addline government &eTreasury: &f%democracy_treasury_minister%
/hd addline government &eCommerce: &f%democracy_commerce_minister%
```

### 5. MOTD atau Server List

```yaml
# Server.properties atau plugin MOTD
motd: |
  &6Democracy Server - President: %democracy_president%
  &eElection Phase: %democracy_phase% - Join now!
```

### 6. Conditional Placeholders (Advanced)

Gunakan dengan plugin seperti **PlaceholderAPI Expansion: CheckItem**:

```yaml
# Hanya tampilkan jika player adalah presiden
%checkitem_mat:democracy_is_president,Yes,==,true%&6[PRESIDENT]%checkitem_mat:democracy_is_president,Yes,==,false%%
```

---

## 🔧 Implementasi Teknis

### Arsitektur Sistem

```
DemocracyCore
    │
    ├─ DemocracyExpansion (PlaceholderAPI Expansion)
    │   ├─ onRequest(OfflinePlayer, String params)
    │   │   ├─ Government Data
    │   │   ├─ Election Data
    │   │   ├─ Treasury Data
    │   │   └─ Player Data
    │   └─ formatTime(long millis)
    │
    └─ Managers
        ├─ GovernmentManager
        ├─ ElectionManager
        ├─ TreasuryManager
        └─ DataManager
```

### Kode Registrasi

```java
// DemocracyCore.java - onEnable()
if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
    new id.democracycore.placeholders.DemocracyExpansion(this).register();
    getLogger().info("PlaceholderAPI found! Registered placeholders.");
}
```

### Custom Expansion Class

File: `id/democracycore/placeholders/DemocracyExpansion.java`

```java
public class DemocracyExpansion extends PlaceholderExpansion {
    
    @Override
    public String getIdentifier() {
        return "democracy";  // %democracy_xxx%
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        // Parsing logic untuk setiap placeholder
        // Return null jika placeholder tidak ditemukan
    }
}
```

### Performance Optimization

1. **Caching**: Semua data di-cache di memory oleh DemocracyCore
2. **Fast Lookup**: Menggunakan HashMap untuk O(1) lookup
3. **Lazy Loading**: Data player hanya dimuat saat diperlukan
4. **Async Safe**: Placeholder dapat dipanggil dari thread manapun

---

## 🐛 Troubleshooting

### Masalah: Placeholder Tidak Bekerja

**Gejala**: Placeholder menampilkan `%democracy_xxx%` tanpa parse

**Solusi**:
```bash
# 1. Cek apakah PlaceholderAPI terinstall
/plugins

# 2. Cek apakah DemocracyCore sudah register
/papi list
# Harus ada 'democracy' di list

# 3. Test manual
/papi parse me %democracy_president%

# 4. Reload PlaceholderAPI
/papi reload
```

### Masalah: Data Tidak Update Real-time

**Gejala**: Placeholder menampilkan data lama

**Solusi**:
- Data diambil langsung dari managers, seharusnya real-time
- Jika masih bermasalah, restart server
- Periksa log untuk error

### Masalah: Placeholder Player Null

**Gejala**: Player-specific placeholder menampilkan "None" atau "0"

**Penyebab**:
- Player belum memiliki data tersimpan
- Player offline saat diakses

**Solusi**:
```java
// Pastikan player online
%democracy_is_president%  // Akan return "No" jika data tidak ada

// Atau gunakan placeholder global
%democracy_president%  // Tidak memerlukan player context
```

### Debug Mode

Untuk debugging, tambahkan log di `DemocracyExpansion.java`:

```java
@Override
public String onRequest(OfflinePlayer player, String params) {
    // Debug log
    plugin.getLogger().info("Placeholder requested: " + params + " for player: " + 
                           (player != null ? player.getName() : "null"));
    
    // ... rest of code
}
```

### Compatibility Issues

| Plugin | Kompatibilitas | Catatan |
|--------|----------------|---------|
| FeatherBoard | ✅ Full | Semua placeholder bekerja |
| TAB | ✅ Full | Gunakan untuk tablist |
| DeluxeMenus | ✅ Full | Sempurna untuk GUI |
| HolographicDisplays | ✅ Full | Update otomatis |
| Essentials | ✅ Full | Untuk chat/nickname |
| CMI | ✅ Full | Untuk berbagai fitur |

---

## 📈 Update Log

### v1.3.0 - Current
- ✅ 40+ placeholders terimplementasi
- ✅ Dukungan penuh untuk semua fitur plugin
- ✅ Performance optimized
- ✅ Dokumentasi lengkap

### Rencana Update Mendatang
- 🔄 `%democracy_executive_orders_active%` - Jumlah executive order aktif
- 🔄 `%democracy_recall_votes%` - Status recall votes
- 🔄 `%democracy_next_election%` - Countdown ke pemilihan berikutnya
- 🔄 Top placeholders (top voter, top donor, dll)

---

## 📞 Support

Jika mengalami masalah dengan PlaceholderAPI integration:

1. **Periksa log server** untuk error messages
2. **Verifikasi versi plugin** (DemocracyCore & PlaceholderAPI)
3. **Test dengan `/papi parse`** untuk isolasi masalah
4. **Report bug** dengan informasi lengkap:
   - Versi server
   - Versi DemocracyCore
   - Versi PlaceholderAPI
   - Error log (jika ada)
   - Langkah reproduksi

---

## 📝 License & Credits

- **DemocracyCore**: DemocracyCore Team
- **PlaceholderAPI**: Extended Clip & Contributors
- **Documentation**: DemocracyCore Team

---

**Terakhir diperbarui**: 13 Februari 2026
**Versi Plugin**: 1.3.0
**Versi Dokumentasi**: 1.0
