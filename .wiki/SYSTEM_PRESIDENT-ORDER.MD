# 🦅 Sistem Perintah Eksekutif (Executive Orders)

Dokumen ini menjelaskan secara teknis dan komprehensif mengenai **Perintah Eksekutif** (*Executive Orders*), hak istimewa tertinggi yang dimiliki oleh Presiden untuk mengubah kondisi server secara global melalui dekrit resmi.

## 📋 Ikhtisar

Perintah Eksekutif adalah kebijakan berskala nasional yang memiliki dampak signifikan terhadap *gameplay* seluruh pemain. Karena kekuatannya yang besar, penggunaannya dibatasi oleh biaya tinggi dari **Kas Negara (Treasury)** dan periode **pendinginan (Cooldown)** yang lama.

## 🏛️ Ketentuan Penggunaan

1.  **Otoritas:** Hanya **Presiden** yang menjabat yang dapat mengeluarkan perintah ini.
2.  **Biaya:** Setiap perintah membutuhkan biaya besar (Default: **$1,000,000**) yang diambil dari Kas Negara.
    *   Jika saldo kas tidak cukup, perintah gagal dikeluarkan.
3.  **Cooldown:** Terdapat jeda waktu antar penggunaan perintah eksekutif (Default: **7 Hari**).
4.  **Eksklusivitas:** Tidak bisa mengeluarkan perintah yang sama jika masih aktif.

## 📜 Daftar Perintah Eksekutif

Berikut adalah daftar lengkap perintah eksekutif yang tersedia beserta efek teknisnya:

### 1. Golden Age Decree (`GOLDEN_AGE`)
*   **Durasi:** 48 Jam
*   **Efek:** Masa keemasan kemakmuran.
    *   +25% XP Gain (Global)
    *   +25% Pendapatan Vault (Money)
    *   +15% Drop Rate Item Langka
*   **Kegunaan:** Meningkatkan ekonomi dan progres pemain secara umum.

### 2. State of Emergency (`STATE_OF_EMERGENCY`)
*   **Durasi:** 24 Jam
*   **Efek:** Penangguhan aktivitas berbahaya demi keamanan.
    *   **PvP Dinonaktifkan** di seluruh dunia (kecuali di Arena resmi).
*   **Kegunaan:** Mencegah kekacauan atau perang antar pemain yang tidak terkendali.

### 3. National Holiday (`NATIONAL_HOLIDAY`)
*   **Durasi:** 24 Jam
*   **Efek:** Hari libur nasional untuk bersenang-senang.
    *   Cooldown pekerjaan (*Jobs*) dihapus.
    *   Hadiah harian (*Daily Rewards*) dapat diklaim 2x lipat.
*   **Kegunaan:** Meningkatkan keterlibatan pemain dan memberikan istirahat dari grinding berat.

### 4. War Economy (`WAR_ECONOMY`)
*   **Durasi:** 72 Jam
*   **Efek:** Mobilisasi ekonomi untuk perang.
    *   Hadiah PvP (*Kill Rewards*) digandakan.
    *   Damage PvP meningkat +50%.
*   **Kegunaan:** Mendorong aktivitas PvP dan konflik faksi.

### 5. Economic Recovery Plan (`ECONOMIC_RECOVERY`)
*   **Durasi:** 36 Jam
*   **Efek:** Stimulus ekonomi besar-besaran.
    *   Subsidi **$50,000** langsung ke setiap pemain online (satu kali klaim).
    *   Diskon 25% di semua toko server.
*   **Kegunaan:** Membantu pemain baru dan memulihkan ekonomi yang lesu.

### 6. Infrastructure Initiative (`INFRASTRUCTURE_INITIATIVE`)
*   **Durasi:** 72 Jam
*   **Efek:** Percepatan pembangunan nasional.
    *   Memberikan **+100 Claim Blocks** gratis.
    *   Efek **Haste II** permanen untuk semua pemain selama durasi aktif.
*   **Kegunaan:** Mempercepat proyek pembangunan markas atau kota pemain.

### 7. Environmental Protection Act (`ENVIRONMENTAL_PROTECTION`)
*   **Durasi:** 48 Jam
*   **Efek:** Perlindungan dan penyuburan alam.
    *   **3x Drop Rate** untuk Farming, Fishing, dan Breeding.
*   **Kegunaan:** Menguntungkan pemain yang berprofesi sebagai petani atau nelayan.

### 8. Education Advancement (`EDUCATION_ADVANCEMENT`)
*   **Durasi:** 48 Jam
*   **Efek:** Investasi pada pengetahuan.
    *   **3x XP Gain**.
    *   Biaya Enchanting dipotong 50%.
    *   Penggunaan Anvil gratis (tanpa biaya XP).
*   **Kegunaan:** Mempercepat leveling dan upgrade equipment.

### 9. Purge Protocol (`PURGE_PROTOCOL`)
*   **Durasi:** 6 Jam
*   **Efek:** **⚠️ BERBAHAYA!** Hukum rimba berlaku.
    *   PvP aktif di SEMUA tempat (termasuk area aman).
    *   *Keep Inventory* dinonaktifkan (barang jatuh saat mati).
    *   Lokasi respawn diacak (Random Respawn).
*   **Kegunaan:** Event kekacauan total ("The Purge"). Sangat berisiko tinggi.

### 10. Presidential Pardon (`PRESIDENTIAL_PARDON`)
*   **Durasi:** Instan (Sekali Pakai)
*   **Efek:** Pengampunan massal.
    *   Menghapus 1 hukuman aktif (warn/mute) dari riwayat semua pemain yang sedang online.
*   **Kegunaan:** Meneduhkan suasana atau memberikan kesempatan kedua bagi pelanggar ringan.

## 🔧 Mekanisme Teknis

### Aktivasi
*   **Perintah:** `/dc order <tipe>`
*   Saat diaktifkan:
    1.  Biaya ditarik dari Treasury.
    2.  Jeda waktu (Cooldown) dimulai.
    3.  Pesan global disiarkan (+Title screen & Suara Naga/Wither).
    4.  Efek visual petir menyambar di lokasi Presiden.

### Pengaruh Global
Manajer teknis (`ExecutiveOrderManager`) memasang *hooks* ke berbagai event listener untuk memodifikasi parameter game secara *real-time*:
*   **XP & Drop:** Memodifikasi event `PlayerExpChangeEvent` dan `BlockDropItemEvent`.
*   **PvP:** Membatalkan atau memodifikasi `EntityDamageByEntityEvent`.
*   **Ekonomi:** Mengintervensi transaksi Vault dan plugin Shop.

### Konfigurasi
Parameter dasar dapat diubah melalui `config.yml`:

```yaml
executive-orders:
  # Biaya penerbitan order
  cost: 1000000
  
  # Jeda waktu antar order (hari)
  cooldown-days: 7
```
