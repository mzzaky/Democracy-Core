# 📜 Daftar Perintah (Command List)

Dokumen ini berisi daftar lengkap perintah yang tersedia dalam plugin **DemocracyCore**. Perintah dikategorikan berdasarkan izin (permission) dan fungsinya.

Semua perintah menggunakan prefix utama: `/democracycore` atau alias `/dc`.

---

## 🌍 Perintah Umum (General Commands)
Perintah ini dapat diakses oleh semua pemain di server.

| Perintah | Alias | Deskripsi |
| :--- | :--- | :--- |
| `/dc menu` | `/dc gui` | Membuka menu utama GUI DemocracyCore. |
| `/dc help` | - | Menampilkan pesan bantuan dan daftar perintah dasar. |
| `/dc info` | `/dc status` | Menampilkan informasi pemerintahan saat ini (Presiden, Kabinet, Kebijakan Aktif). |
| `/dc stats [player]` | - | Melihat statistik politik pribadi atau pemain lain (Playtime, Vote, Jabatan). |
| `/dc history` | - | Menampilkan daftar riwayat presiden yang pernah menjabat sebelumnya. |
| `/dc rate <1-5>` | `/dc approval` | Memberikan penilaian (1-5 bintang) terhadap kinerja pemerintah saat ini. |
| `/dc leaderboard` | `/dc lb` | Membuka GUI papan peringkat (Leaderboard). |
| `/dc quickactions` | `/dc qa` | Membuka menu aksi cepat untuk akses fitur yang sering digunakan. |

---

## 🗳️ Perintah Pemilihan (Election Commands)
Perintah yang digunakan selama siklus pemilihan umum berlangsung.

| Perintah | Deskripsi |
| :--- | :--- |
| `/dc election` | Mengecek status, fase, dan sisa waktu pemilihan umum saat ini. |
| `/dc candidates` | Menampilkan daftar kandidat presiden yang terdaftar. |
| `/dc register` | Mendaftarkan diri sebagai calon presiden (Hanya saat fase Registrasi). |
| `/dc endorse <player>` | Memberikan dukungan resmi ke kandidat untuk menambah poin kampanye mereka. |
| `/dc campaign <pesan>` | Menyiarkan pesan kampanye global (Berbayar & Cooldown). |
| `/dc vote <kandidat>` | Memberikan suara untuk kandidat pilihan (Hanya saat fase Voting). |

---

## 💰 Perintah Ekonomi & Kas Negara (Treasury)

| Perintah | Deskripsi |
| :--- | :--- |
| `/dc treasury` | Melihat saldo, pemasukan, dan pengeluaran negara terbaru. |
| `/dc treasury donate <jumlah>` | Menyumbangkan uang pribadi ke kas negara. |

---

## ⚔️ Perintah Arena

| Perintah | Deskripsi |
| :--- | :--- |
| `/dc arena join` | Bergabung ke lobi arena pertempuran. |
| `/dc arena leave` | Keluar dari arena. |
| `/dc arena stats` | Melihat statistik pembunuhan (Kills) dan kematian (Deaths) di arena. |
| `/dc arena leaderboard` | Melihat papan peringkat pemain terbaik di arena. |
| `/dc arena start` | **(Presiden Only)** Memulai sesi pertempuran baru di arena. |

---

## 📢 Perintah Pemakzulan (Recall/Impeachment)
Fitur untuk menurunkan presiden yang dianggap tidak kompeten.

| Perintah | Deskripsi |
| :--- | :--- |
| `/dc recall status` | Mengecek status petisi pemakzulan yang sedang berjalan. |
| `/dc recall start` | Memulai petisi pemakzulan baru terhadap presiden. |
| `/dc recall sign` | Menandatangani petisi pemakzulan yang aktif. |
| `/dc recall vote <yes/no>` | Memberikan suara dalam referendum pemakzulan (Jika petisi berhasil). |

---

## 🏛️ Perintah Presiden (President Only)
Hanya dapat digunakan oleh Presiden yang sedang menjabat.
*Permission node: `democracy.president`*

| Perintah | Deskripsi |
| :--- | :--- |
| `/dc order <tipe>` | Mengeluarkan Perintah Eksekutif (Executive Order).<br>Contoh: `/dc order GOLDEN_AGE` |
| `/dc cabinet appoint <posisi> <player>` | Mengangkat pemain menjadi menteri kabinet.<br>Posisi: `DEFENSE`, `TREASURY`, `COMMERCE`, `INFRASTRUCTURE`, `CULTURE` |
| `/dc cabinet dismiss <posisi>` | Memecat menteri dari jabatannya. |

---

## 🛡️ Perintah Kabinet (Cabinet Only)
Hanya dapat digunakan oleh Menteri yang sedang menjabat.
*Permission node: `democracy.cabinet`*

| Perintah | Deskripsi |
| :--- | :--- |
| `/dc cabinet info` | Melihat informasi anggota kabinet dan keputusan yang aktif. |
| `/dc cabinet decision <tipe>` | Mengambil keputusan/kebijakan sesuai kementeriannya.<br>Contoh: `/dc cabinet decision SUBSIDIZE_FOOD` |

---

## 🔧 Perintah Admin (Admin Only)
Perintah untuk manajemen sistem dan debugging.
*Permission node: `democracy.admin`*

| Perintah | Deskripsi |
| :--- | :--- |
| `/dc admin setpresident <player>` | Menunjuk paksa pemain menjadi Presiden. |
| `/dc admin removepresident` | Memberhentikan Presiden saat ini secara paksa. |
| `/dc admin startelection` | Memulai siklus pemilihan umum baru secara manual. |
| `/dc admin endelection` | Mengakhiri fase pemilihan umum saat ini. |
| `/dc admin addtreasury <jumlah>` | Menambahkan uang ke kas negara (dari udara). |
| `/dc admin stoporder <tipe>` | Menghentikan paksa Perintah Eksekutif yang sedang aktif. |
| `/dc admin reload` | Memuat ulang seluruh file konfigurasi. |
| `/dc admin reset <tipe>` | Menghapus data plugin.<br>Tipe: `government`, `election`, `treasury`, `all` |
