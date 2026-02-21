# 🏛️ Sistem Kabinet Menteri (Cabinet System)

Dokumen ini menjelaskan secara detil mengenai struktur, fungsi, dan kekuatan dari **Kabinet Menteri** dalam pemerintahan server. Menteri adalah tangan kanan Presiden yang membantu mengelola aspek spesifik dari server.

## 👥 Posisi Kabinet

Terdapat 5 posisi kementerian strategis yang dapat ditunjuk oleh Presiden. Setiap menteri memiliki fokus area, prefix chat khusus, dan kemampuan unik (*Cabinet Decisions*).

### 1. 🛡️ Minister of Defense (Pertahanan)
*   **Prefix:** `&4[MoD]`
*   **Fokus:** Keamanan server, PvP, dan perlindungan wilayah.
*   **Peran:** Bertanggung jawab atas stabilitas keamanan dan mengelola event pertempuran.

### 2. 💰 Minister of Treasury (Keuangan)
*   **Prefix:** `&2[MoT]`
*   **Fokus:** Ekonomi, pajak, dan pasar.
*   **Peran:** Mengelola kestabilan ekonomi server dan mendistribusikan kekayaan.

### 3. 📈 Minister of Commerce (Perdagangan)
*   **Prefix:** `&9[MoC]`
*   **Fokus:** Perdagangan antar pemain, toko, dan sumber daya.
*   **Peran:** Meningkatkan aktivitas perdagangan dan ketersediaan barang langka.

### 4. 🏗️ Minister of Infrastructure (Infrastruktur)
*   **Prefix:** `&7[MoI]`
*   **Fokus:** Pembangunan, klaim lahan, dan material.
*   **Peran:** Memfasilitasi pembangunan kota dan proyek publik.

### 5. 🎨 Minister of Culture (Kebudayaan)
*   **Prefix:** `&d[MoK]`
*   **Fokus:** Event, hiburan, dan pengalaman komunitas.
*   **Peran:** Menjaga semangat komunitas tetap tinggi melalui festival dan perayaan.

## ⚖️ Kekuasaan Menteri (Decisions)

Setiap menteri memiliki akses ke serangkaian kebijakan khusus yang disebut **Cabinet Decisions**. Kebijakan ini memiliki biaya (dari Kas Negara) dan durasi tertentu.

### 🛡️ Pertahanan (Defense)
| Keputusan | Durasi | Efek | deskripsi |
| :--- | :--- | :--- | :--- |
| **DECLARE_WAR** | 24 Jam | Mengaktifkan War Games Event. | Zona PvP terbuka dengan loot ganda. |
| **MILITARY_DRAFT** | - | Wajib militer & turnamen PvP. | Memberikan item tempur dasar ke semua pemain. |
| **DEFENSE_PROTOCOL** | 12 Jam | Protokol pertahanan nasional. | Mengurangi damage yang diterima seluruh pemain sebesar 20%. |
| **ARMORY_DISCOUNT** | 24 Jam | Subsidi persenjataan. | Diskon 30% untuk senjata & armor di toko NPC. |
| **BORDER_PATROL** | 48 Jam | Patroli perbatasan. | Spawn mob elit di perbatasan dengan drop khusus. |

### 💰 Keuangan (Treasury)
| Keputusan | Durasi | Efek | deskripsi |
| :--- | :--- | :--- | :--- |
| **TAX_HOLIDAY** | 24 Jam | Pembebasan pajak. | Tidak ada pajak transaksi server. |
| **ECONOMIC_STIMULUS** | Instan | Bantuan Langsung Tunai (BLT). | Membagikan 10k Vault Points ke semua pemain online. |
| **AUCTION_BOOST** | 48 Jam | Peningkatan pasar lelang. | Menggandakan batas lelang & mengurangi biaya admin. |
| **TREASURY_BONUS** | 12 Jam | Dividen negara. | +50% uang dari membunuh mob & mining. |
| **MARKET_CRASH** | 6 Jam | Simulasi krisis pasar. | Diskon 40% di Admin Shop (Flash Sale). |

### 📈 Perdagangan (Commerce)
| Keputusan | Durasi | Efek | deskripsi |
| :--- | :--- | :--- | :--- |
| **TRADE_FAIR** | 24 Jam | Pameran dagang. | Muncul NPC pedagang item eksklusif. |
| **RESOURCE_BOOM** | 12 Jam | Ledakan sumber daya. | Drop rate x2 untuk mining, farming, fishing. |
| **MERCHANT_CARAVAN** | 6 Jam | Kafilah pedagang. | NPC Caravan dengan item langka harga murah. |
| **BLACK_FRIDAY** | 48 Jam | Promosi besar-besaran. | Iklan toko gratis & visibilitas meningkat. |
| **TREASURE_HUNT** | Instan | Perburuan harta karun. | Menyebarkan 20 peti harta karun di seluruh map. |

### 🏗️ Infrastruktur (Infrastructure)
| Keputusan | Durasi | Efek | deskripsi |
| :--- | :--- | :--- | :--- |
| **CONSTRUCTION_BOOM** | 24 Jam | Percepatan pembangunan. | Efek Haste III + Jump Boost II global. |
| **FREE_REAL_ESTATE** | 48 Jam | Subsidi lahan. | Diskon 50% biaya claim block. |
| **BUILDERS_PARADISE** | 12 Jam | Surga pembangun. | Drop item bangunan (batu, kayu) menjadi ganda. |
| **PUBLIC_WORKS** | 30 Menit | Proyek publik. | Hujan material bangunan di spawn. |
| **INFRASTRUCTURE_GRANT** | Instan | Hibah pembangunan. | Memberikan 50k ke 10 pemain acak untuk membangun. |

### 🎨 Kebudayaan (Culture)
| Keputusan | Durasi | Efek | deskripsi |
| :--- | :--- | :--- | :--- |
| **FESTIVAL_WEEK** | 72 Jam | Pekan festival. | Turnamen minigames dengan hadiah. |
| **DOUBLE_XP_WEEKEND** | 48 Jam | Pesta XP. | 2x XP gain server-wide. |
| **FIREWORK_SHOW** | 24 Jam | Pesta kembang api. | Pertunjukan kembang api otomatis setiap jam di spawn. |
| **CULTURAL_EXCHANGE** | 24 Jam | Pertukaran budaya. | NPC trading item dekorasi langka. |
| **COMMUNITY_EVENT** | Instan | Kompetisi komunitas. | Memulai event building/PvP dengan hadiah 500k. |

## ⚙️ Mekanisme & Batasan

1.  **Penunjukan:**
    *   Hanya Presiden yang dapat menunjuk atau memecat menteri.
    *   Perintah: `/dc cabinet appoint <posisi> <player>`
2.  **Biaya & Cooldown:**
    *   Setiap keputusan menggunakan uang dari **Kas Negara (Treasury)**.
    *   Terdapat cooldown antar penggunaan keputusan (Default: 48 Jam per menteri).
3.  **Persyaratan:**
    *   Menteri harus sedang menjabat.
    *   Kas negara harus mencukupi.

## 🔧 Konfigurasi
Pengaturan biaya dan durasi dapat diubah di `config.yml` di bawah node `cabinet.decisions`.
