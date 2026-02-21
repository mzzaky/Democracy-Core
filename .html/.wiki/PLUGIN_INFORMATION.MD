# 📘 Informasi Plugin: DemocracyCore

**DemocracyCore** adalah plugin Minecraft komprehensif yang dirancang untuk mensimulasikan sistem pemerintahan demokratis yang hidup dan realistis di dalam server. Plugin ini mengubah cara pemain berinteraksi dengan memperkenalkan struktur politik, ekonomi negara, dan dinamika kekuasaan yang nyata.

## 🌟 Fitur Utama

Plugin ini dibangun di atas beberapa pilar utama:

### 1. 🏛️ Sistem Pemerintahan (Government System)
Pusat dari plugin ini adalah **Presiden** dan **Kabinet**.
*   **Presiden:** Pemimpin tertinggi yang dipilih melalui pemilu. Memiliki kekuasaan untuk mengeluarkan perintah eksekutif, mengelola kas negara, dan menunjuk menteri.
*   **Masa Jabatan:** Dibatasi oleh waktu (default: 30 hari) dan batas periode berturut-turut (default: 2 periode) untuk mencegah monopoli kekuasaan.
*   **Kabinet:** Presiden dapat menunjuk pemain lain untuk mengisi posisi strategis:
    *   🛡️ **Menteri Pertahanan (Defense):** Fokus pada keamanan dan PvP.
    *   💰 **Menteri Keuangan (Treasury):** Mengelola ekonomi dan pajak.
    *   📈 **Menteri Perdagangan (Commerce):** Mengatur pasar dan ekonomi pemain.
    *   🏗️ **Menteri Infrastruktur (Infrastructure):** Fokus pada pembangunan.
    *   🎨 **Menteri Kebudayaan (Culture):** Menangani event dan pengalaman pemain.

### 2. 🗳️ Sistem Pemilihan Umum (Election System)
Siklus demokrasi berjalan otomatis tanpa campur tangan admin.
*   **Otomatisasi:** Transisi fase dari Registrasi -> Kampanye -> Voting -> Pelantikan berjalan sendiri.
*   **Voting Cerdas:** Suara pemain memiliki "bobot" yang berbeda berdasarkan kontribusi mereka (Playtime, Level, Kekayaan).
*   **Kampanye:** Kandidat dapat mempromosikan diri dan mendapatkan *endorsement* resmi.
*(Detail lengkap tersedia di `PRESIDENT_SYSTEM.MD`)*

### 3. 💵 Ekonomi & Kas Negara (Treasury)
Plugin ini memisahkan uang pribadi dan uang negara.
*   **Kas Negara (Treasury):** Dana yang dikelola pemerintah untuk membiayai proyek, gaji pejabat, dan subsidi.
*   **Gaji Pejabat:** Presiden dan Menteri menerima gaji harian otomatis dari kas negara.
*   **Pajak Transaksi:** Sebagian kecil dari transaksi ekonomi dapat masuk ke kas negara (dikonfigurasi).
*   **Transparansi:** Setiap pemasukan dan pengeluaran negara mencatat log transaksi yang jelas.

### 4. ⚡ Perintah Eksekutif (Executive Orders)
Presiden memiliki "kartu as" berupa perintah eksekutif yang memberikan efek global di server. Menggunakan biaya dari Kas Negara.
Beberapa contoh perintah:
*   ✨ **Golden Age:** Meningkatkan XP, Drop Rate, dan ekonomi global.
*   ⚔️ **Purge Protocol:** Mengaktifkan PvP di seluruh dunia untuk waktu terbatas.
*   🏗️ **Infrastructure Initiative:** Memberikan efek Haste II ke semua pemain untuk mempercepat pembangunan.
*   💸 **Economic Recovery:** Membagikan stimulus uang tunai ke semua warga & diskon toko.
*   🛡️ **State of Emergency:** Menonaktifkan PvP server-wide.

### 5. 🎁 Sistem Insentif & Buff
*   **Approval Rating:** Rakyat dapat memberikan penilaian kepuasan terhadap kinerja pemerintah.
*   **Daily Rewards:** Hadiah harian khusus untuk pejabat pemerintah (Item langka, XP, Uang).
*   **Welfare:** Mekanisme subsidi dan bantuan untuk pemain.

---

## 🏗️ Struktur Teknis

Secara teknis, plugin ini diorganisir menggunakan sistem **Manager** modular untuk menangani setiap aspek logika:

| Manager | Fungsi Utama |
| :--- | :--- |
| `ElectionManager` | Menangani seluruh logika pemilu, fase, dan voting. |
| `GovernmentManager` | Mengelola data presiden, kabinet, masa jabatan, dan gaji. |
| `TreasuryManager` | Menangani transaksi keuangan negara dan integrasi Vault. |
| `ExecutiveOrderManager` | Menangani aktivasi, cooldown, dan efek dari perintah eksekutif. |
| `CabinetManager` | Logika spesifik untuk izin dan kemampuan anggota kabinet. |
| `BuffManager` | Mengaplikasikan efek potion/stat ke pemain secara global/spesifik. |
| `DataManager` | Menangani penyimpanan data persisten (JSON/YAML). |

### Integrasi
*   **Vault:** Wajib. Digunakan untuk semua transaksi ekonomi (biaya pendaftaran, gaji, pajak, biaya perintah eksekutif).
*   **PlaceholderAPI (Opsional):** Untuk menampilkan nama presiden, status pemilu, dll di scoreboard/chat.

---

## ⚙️ Perintah Dasar (Basic Commands)

Berikut adalah beberapa perintah utama yang tersedia bagi pemain dan admin:

*   `/dc` - Membuka menu utama GUI DemocracyCore.
*   `/dc help` - Menampilkan daftar perintah.
*   `/dc vote` - Memberikan suara saat fase voting.
*   `/dc register` - Mendaftar sebagai kandidat presiden.
*   `/dc cabinet` - Melihat atau mengelola anggota kabinet.
*   `/dc treasury` - Melihat status kas negara.
*   `/dc order` - (Presiden) Membuka menu Perintah Eksekutif.
*   `/dc stats` - Melihat statistik politik pribadi pemain.

---

## 📝 Catatan Pengembang
Plugin ini dirancang untuk "mengatur dirinya sendiri" (self-regulating). Jika Presiden tidak aktif selama beberapa hari (configurable), sistem otomatis akan memecatnya dan memulai pemilu darurat. Ini memastikan server tidak "mati" karena pemimpin yang pensiun.
