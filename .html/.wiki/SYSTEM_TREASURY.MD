# 💰 Sistem Perbendaharaan Negara (National Treasury)

Dokumen ini menjelaskan secara teknis mengenai sistem **Treasury** (Kas Negara) dalam plugin DemocracyCore. Sistem ini mengatur sirkulasi uang, pendapatan negara, dan pengeluaran pemerintah untuk membiayai kebijakan.

## 📋 Ikhtisar

Treasury adalah entitas ekonomi terpisah dari akun pribadi pemain mana pun. Negara memiliki saldo sendiri yang digunakan untuk:
1.  Membiayai **Executive Orders** presiden.
2.  Mendanai **Cabinet Decisions** para menteri.
3.  Membayar gaji pejabat (Presiden & Menteri).
4.  Memberikan hadiah bagi pemilih (*Voter Rewards*).

Jika kas negara kosong, pemerintah tidak dapat menjalankan fungsinya (kolaps).

## 📥 Sumber Pendapatan (Income)

Pemasukan negara berasal dari beberapa sumber otomatis dan manual:

### 1. Pajak Transaksi (`TAX_INCOME`)
*   Setiap kali terjadi transaksi (yang terintegrasi dengan plugin), potongan pajak akan masuk ke kas negara.
*   **Default:** 5% dari nilai transaksi.
*   **Konfigurasi:** `treasury.transaction-tax`

### 2. Denda & Sitaan (`FINE_INCOME`)
*   Uang deposit dari petisi pamakzulan (*Recall*) yang gagal akan disita masuk ke sini.
*   Uang registrasi kandidat yang kalah telak (jika dikonfigurasi).

### 3. Donasi (`DONATION`)
*   Pemain dapat menyumbangkan uang pribadi mereka ke negara untuk membantu program pemerintah.
*   **Perintah:** `/dc treasury donate <jumlah>`

### 4. Dana Awal Periode (`TERM_START_FUND`)
*   Suntikan dana awal setiap kali presiden baru terpilih (opsional/admin).

## 📤 Jenis Pengeluaran (Expense)

### 1. Program Pemerintah
*   **Executive Orders:** Biaya besar untuk mengaktifkan kebijakan global (misal: *Golden Age*).
*   **Cabinet Decisions:** Biaya operasional kementerian (misal: *Festival*, *Subsidi*).

### 2. Gaji Pejabat (`CABINET_SALARY`)
*   Gaji harian yang dibayarkan kepada Presiden dan Menteri.
*   Dibayar otomatis setiap hari pada jam tertentu.

### 3. Hadiah Publik
*   **Voter Reward:** Hadiah uang tunai bagi warga yang menggunakan hak pilihnya saat pemilu.
*   **Stimulus:** Bantuan langsung tunai (BLT) dari Menteri Keuangan.
*   **Deposit Refund:** Pengembalian uang deposit kampanye bagi pemenang pemilu.

## 📊 Manajemen Transaksi

Sistem mencatat setiap aliran dana keluar-masuk secara transparan.
*   **Pencatatan:** Setiap deposit/withdraw dicatat dengan: Tipe, Jumlah, Deskripsi, Pelaku (UUID), dan Waktu.
*   **Batas Log:** Sistem menyimpan 1000 transaksi terakhir untuk audit.
*   **Audit:** Pemain dapat melihat riwayat transaksi melalui GUI atau perintah.

## 💾 Struktur Data

Data treasury disimpan dalam format JSON/YAML yang mencakup:
*   `balance`: Saldo saat ini (double).
*   `total_income`: Akumulasi total pendapatan sepanjang masa.
*   `total_expenses`: Akumulasi total pengeluaran sepanjang masa.
*   `transactions`: List objek transaksi.

## 🔧 Perintah Terkait

| Perintah | Fungsi | Akses |
| :--- | :--- | :--- |
| `/dc treasury` | Melihat status keuangan negara (Saldo, Income, Expense, Log). | Publik |
| `/dc treasury donate <jumlah>` | Menyumbang uang ke kas negara. | Publik |
| `/dc admin addtreasury <jumlah>` | Menambahkan uang ke kas negara secara instan (Cheat). | Admin |

## ⚙️ Konfigurasi

Pengaturan terkait ekonomi ada di `config.yml` bagian `treasury`.

```yaml
treasury:
  # Pajak transaksi (0.05 = 5%)
  transaction-tax: 0.05
  
  # Gaji harian pejabat
  salaries:
    president: 5000
    minister: 2500
```
