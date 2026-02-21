# 🎁 Keuntungan Pejabat (Government Perks/Benefits)

Dokumen ini menjelaskan secara detail mengenai keuntungan pribadi, buff, dan fasilitas yang didapatkan oleh pemain yang menjabat sebagai Presiden atau Menteri dalam pemerintahan DemocracyCore.

## 🎖️ Keuntungan Presiden (Presidential Perks)

Sebagai pemimpin tertinggi, Presiden mendapatkan set buff terkuat di server untuk memastikan keamanan dan wibawanya.

### 1. Status Effects (Permanen)
Selama menjabat, Presiden otomatis mendapatkan efek potion permanen:
*   **Night Vision:** Penglihatan malam tanpa batas.
*   **Saturation:** Tidak akan pernah merasa lapar (Hunger bar selalu penuh).

### 2. Atribut Fisik
Peningkatan kemampuan tempur dan pertahanan diri:
*   **Attack Damage:** +15% Damage serangan fisik.
*   **Armor Toughness:** +1.2 Poin ketahanan armor (Setara Diamond/Netherite tambahan).
*   **Max Health:** +4 Hati (8 HP) tambahan.

### 3. Keuntungan Ekonomi & Progres
*   **Gaji Harian:** $5,000 (Otomatis masuk ke saldo setiap hari).
*   **Vault Bonus:** +20% uang lebih banyak dari sumber manapun (Kill mob/mining).
*   **XP Bonus:** +10% Experience gain.

---

## 🛡️ Keuntungan Menteri (Cabinet Perks)

Setiap menteri mendapatkan buff spesifik yang disesuaikan dengan peran kementerian mereka untuk membantu tugas sehari-hari.

### 1. Minister of Defense (Pertahanan)
*   **Role:** Komandan tempur.
*   **Buffs:**
    *   **Strength I:** Peningkatan damage serangan.
    *   **Resistance I:** Pengurangan damage yang diterima.
    *   **Atribut:** +10% Damage, +8% Defense.
*   **Gaji:** $2,500/hari.

### 2. Minister of Treasury (Keuangan)
*   **Role:** Pengelola uang.
*   **Buffs:**
    *   **Luck II:** Meningkatkan peluang mendapat item langka saat memancing/loot chest.
    *   **Vault Bonus:** +15% uang lebih banyak dari sumber manapun.
*   **Gaji:** $2,500/hari.

### 3. Minister of Commerce (Perdagangan)
*   **Role:** Pedagang cepat.
*   **Buffs:**
    *   **Speed I:** Gerakan lebih cepat untuk mobilisasi.
    *   **Luck I:** Keberuntungan moderat.
    *   **Diskon:** 10% Diskon di semua toko server (jika terintegrasi).
*   **Gaji:** $2,500/hari.

### 4. Minister of Infrastructure (Infrastruktur)
*   **Role:** Pembangun kota.
*   **Buffs:**
    *   **Haste II:** Kecepatan menambang instan (Insta-mine untuk stone/deepslate).
    *   **Klaim Lahan:** Bonus +50 Claim Blocks setiap hari.
*   **Gaji:** $2,500/hari.

### 5. Minister of Culture (Kebudayaan)
*   **Role:** Ikon publik.
*   **Buffs:**
    *   **Glowing:** Efek bersinar (memudahkan terlihat saat event).
    *   **Dolphin's Grace:** Berenang lebih cepat.
    *   **XP Bonus:** +10% Experience gain.
*   **Gaji:** $2,500/hari.

## ⚙️ Mekanisme Buff

1.  **Otomatisasi:** Buff diterapkan secara otomatis saat pemain:
    *   Dilantik menjadi pejabat.
    *   Login ke dalam server (Join Event).
    *   Respawn setelah mati.
2.  **Pencabutan:** Buff otomatis hilang seketika jika pemain:
    *   Turun jabatan (Periode berakhir/Dimakzulkan/Dipecat).
    *   Keluar dari server (Quit Event).

## 🔧 Konfigurasi

Nilai kekuatan buff dan gaji dapat diubah melalui `config.yml`:

```yaml
president:
  buffs:
    damage-bonus: 0.15
    defense-bonus: 0.12
    extra-hearts: 2

treasury:
  salaries:
    president: 5000
    minister: 2500
```
