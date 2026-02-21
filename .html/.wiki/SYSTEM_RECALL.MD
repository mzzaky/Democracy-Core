# 🗳️ Sistem Pemakzulan Presiden (President Recall System)

Dokumen ini menjelaskan secara teknis dan detail mengenai modul **Recall System** (Pemakzulan) dalam plugin DemocracyCore. Sistem ini memberikan kekuasaan kepada rakyat untuk menurunkan presiden yang dianggap tidak kompeten atau menyalahgunakan kekuasaan sebelum masa jabatannya berakhir.

## 📋 Ikhtisar

Sistem Recall dirancang untuk menjadi mekanisme penyeimbang kekuasaan (*checks and balances*). Proses ini tidak mudah dilakukan, membutuhkan dukungan finansial dan suara mayoritas yang signifikan untuk mencegah penyalahgunaan fitur oleh kelompok minoritas.

Proses pemakzulan dibagi menjadi dua fase utama:
1.  **Fase Petisi (Petition / Collecting):** Pengumpulan tanda tangan dukungan.
2.  **Fase Voting (Recall Vote):** Referendum penentuan nasib presiden.

## 🔄 Alur Proses Pemakzulan

### 1. Inisiasi Petisi
Seorang pemain dapat memulai petisi pemakzulan terhadap presiden yang sedang menjabat.
*   **Syarat:**
    *   Presiden sedang menjabat (tidak bisa me-recall presiden yang sudah turun).
    *   Inisiator bukan Presiden itu sendiri.
    *   Tidak ada petisi lain yang sedang aktif.
    *   Cooldown global pasca-kegagalan recall sebelumnya sudah berakhir (Default: 15 Hari).
    *   Inisiator memiliki saldo cukup untuk membayar deposit awal.
*   **Biaya:** Inisiator harus membayar deposit (Default: $50,000).
*   **Perintah:** `/dc recall start`

### 2. Fase Pengumpulan Tanda Tangan (`COLLECTING`)
Setelah dimulai, petisi memasuki periode pengumpulan tanda tangan.
*   **Durasi:** 7 Hari (Default).
*   **Aktivitas:** Pemain lain dapat menandatangani petisi untuk mendukung pemakzulan.
*   **Biaya Tanda Tangan:** Setiap penandatangan harus membayar deposit yang sama dengan inisiator ($50,000).
    *   *Tujuannya adalah untuk memastikan keseriusan dukungan.*
*   **Target:** Jumlah tanda tangan harus mencapai persentase tertentu dari populasi aktif server (Default: 30% dari pemain aktif mingguan).
*   **Konsekuensi Kegagalan:** Jika target tidak tercapai hingga batas waktu, petisi gagal dan **semua deposit hangus** (masuk ke Kas Negara).
*   **Penarikan Dukungan:** Pemain dapat menarik tanda tangan mereka, namun hanya mendapatkan refund 50%.

### 3. Fase Referendum / Voting (`VOTING`)
Jika jumlah tanda tangan terpenuhi, sistem otomatis memulai fase voting.
*   **Durasi:** 3 Hari (Default).
*   **Aktivitas:** Seluruh pemain di server (termasuk yang tidak menandatangani petisi) dapat memberikan suara.
*   **Pilihan:**
    *   `REMOVE` (Merah): Setuju menurunkan Presiden.
    *   `KEEP` (Hijau): Setuju mempertahankan Presiden.
*   **Perintah:** `/dc recall vote <remove/keep>`

### 4. Hasil & Konsekuensi

#### ✅ Skenario Sukses (Presiden Dimakzulkan)
Terjadi jika suara `REMOVE` mencapai ambang batas mayoritas super (Default: 60%).
1.  **Presiden Diberhentikan:** Presiden langsung kehilangan jabatannya.
2.  **Refund Penuh:** Seluruh deposit inisiator dan penandatangan petisi dikembalikan 100%.
3.  **Pemilihan Darurat:** Sistem otomatis memicu pemilihan umum darurat (`Emergency Election`).
4.  **Efek Suara:** Suara *Wither Death* diputar secara global.

#### ❌ Skenario Gagal (Presiden Bertahan)
Terjadi jika suara `REMOVE` kurang dari ambang batas (meskipun lebih banyak dari `KEEP` tetap bisa gagal jika tidak mencapai 60%).
1.  **Presiden Aman:** Masa jabatan berlanjut seperti biasa.
2.  **Deposit Hangus:** Seluruh deposit inisiator dan penandatangan disita dan dimasukkan ke Kas Negara (`FINE_INCOME`).
3.  **Cooldown:** Sistem recall memasuki masa cooldown (Default: 15 Hari) dimana tidak ada petisi baru yang bisa dibuat.

## ⚙️ Konfigurasi Penting

Pengaturan sistem ini dapat ditemukan dalam `config.yml` di bagian `recall`.

```yaml
recall:
  # Waktu tunggu sebelum petisi baru bisa dibuat setelah kegagalan
  cooldown-days: 15
  
  # Durasi pengumpulan tanda tangan
  collection-days: 7
  
  # Durasi fase voting recall
  voting-days: 3
  
  # Uang jaminan yang harus dibayar penandatangan
  signature-deposit: 50000
  
  # Persentase pemain aktif yang dibutuhkan untuk memicu voting
  required-signature-percentage: 30
  
  # Persentase suara 'REMOVE' yang dibutuhkan untuk sukses
  required-percentage: 60
```

## 🛡️ Mekanisme Keamanan

Untuk mencegah *spam* dan penyalahgunaan:
1.  **Deposit Tinggi:** Mencegah pemain iseng membuat petisi tanpa dukungan nyata karena risiko kehilangan uang sangat besar.
2.  **Perhitungan Pemain Aktif:** Jumlah tanda tangan yang dibutuhkan dinamis, dihitung berdasarkan pemain yang login dalam 7 hari terakhir, bukan total pemain terdaftar (yang mungkin sudah pensiun).
3.  **Super-Majority:** Membutuhkan 60% suara untuk menurunkan presiden, memastikan keputusan tersebut benar-benar keinginan mayoritas besar, bukan hanya 50%+1.

## 🔧 Perintah Admin

Admin memiliki hak untuk mengintervensi proses recall:
*   **Membatalkan Petisi:** `/dc admin cancelrecall <alasan>`
    *   Membatalkan petisi aktif dan mengembalikan semua deposit (Refund 100%).
*   **Pemakzulan Paksa:** `/dc admin removepresident`
    *   Langsung menurunkan presiden tanpa melalui proses voting.

---
*Dokumen ini dibuat berdasarkan logika pada `RecallManager.java` dan `RecallPetition.java`.*
