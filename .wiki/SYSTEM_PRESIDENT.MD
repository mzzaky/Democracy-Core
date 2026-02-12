# 🗳️ Sistem Presiden (President System)

Dokumen ini menjelaskan secara teknis dan detail mengenai modul Sistem Presiden dalam plugin DemocracyCore. Modul ini menangani siklus hidup pemilihan umum, persyaratan kandidat, mekanisme voting, hingga pelantikan presiden.

## 📋 Ikhtisar

Sistem Presiden dirancang untuk mensimulasikan proses demokrasi yang realistis di dalam server. Siklus pemilihan berjalan secara otomatis berdasarkan konfigurasi waktu, membagi proses menjadi beberapa fase: Registrasi, Kampanye, Voting, dan Pelantikan.

## 🔄 Siklus Pemilihan (Election Cycle)

Proses pemilihan dikelola oleh `ElectionManager` dan dibagi menjadi 4 fase utama (`ElectionPhase`):

### 1. Fase Registrasi (`REGISTRATION`)
*   **Durasi:** 3 Hari (Default, dapat dikonfigurasi).
*   **Aktivitas:** Pemain yang memenuhi syarat dapat mendaftarkan diri.
*   **Perintah:** `/dc register [slogan]`
*   **Biaya:** Kandidat harus membayar biaya pendaftaran (Default: $500,000).
*   **Logika:** Jika tidak ada kandidat yang mendaftar hingga akhir fase, fase registrasi akan diulang.

### 2. Fase Kampanye (`CAMPAIGN`)
*   **Durasi:** 7 Hari (Default).
*   **Aktivitas:** Kandidat mempromosikan diri untuk mendapatkan dukungan.
*   **Fitur Endorsement:** Pemain lain dapat memberikan dukungan resmi (`Endorsement`) kepada kandidat.
    *   Memberikan +10 Poin Kampanye per dukungan.
    *   Perintah: `/dc endorse <nama_kandidat>`
*   **Pesan Kampanye:** Kandidat dapat menyiarkan pesan kampanye berbayar dengan *cooldown* tertentu.
    *   Biaya Broadcast: $10,000.
    *   Cooldown: 6 Jam.

### 3. Fase Voting (`VOTING`)
*   **Durasi:** 3 Hari (Default).
*   **Aktivitas:** Pemain memberikan suara untuk kandidat pilihan mereka.
*   **Perintah:** `/dc vote <nama_kandidat>`
*   **Sistem Hadiah:**
    *   **Partisipasi:** Setiap pemilih mendapatkan hadiah uang (Default: $10,000).
    *   **Lotere:** Sejumlah pemilih beruntung (acak) mendapatkan bonus tambahan (Default: $50,000 untuk 10 pemenang).

### 4. Fase Pelantikan (`INAUGURATION`)
*   **Durasi:** 1 Hari (Default).
*   **Aktivitas:** Penghitungan suara final, pengumuman pemenang, dan transfer kekuasaan.
*   **Refund:**
    *   Pemenang mendapatkan pengembalian biaya registrasi 100%.
    *   Kalah mendapatkan pengembalian 50%.

## 📜 Persyaratan Kandidat

Untuk mendaftar sebagai presiden, pemain harus memenuhi kriteria ketat yang diperiksa di `ElectionManager#meetsRequirements`:

| Kriteria | Default Value | Deskripsi |
| :--- | :--- | :--- |
| **Minimum Level** | 100 | Level XP pemain harus mencapai batas ini. |
| **Playtime** | 100 Jam | Total waktu bermain yang tercatat di server. |
| **Saldo (Balance)** | $500,000 | Saldo minimum yang harus dimiliki di akun Vault. |
| **Catatan Hukuman** | 30 Hari | Tidak boleh memiliki catatan hukuman (banned/muted) dalam periode ini. |
| **Batas Periode** | 2 Periode | Tidak boleh menjabat lebih dari batas periode berturut-turut. |

## ⚖️ Mekanisme Voting Terbobot (Weighted Voting)

Sistem ini tidak menggunakan *one-person-one-vote* murni. Suara pemain memiliki "bobot" yang berbeda berdasarkan kontribusi mereka di server.

**Rumus:**
`Total Bobot = Base Weight + Bonus Playtime + Bonus Level + Bonus Saldo`

*   **Base Weight:** 1.0
*   **Bonus Playtime:** +0.5 (Jika playtime > 200 jam)
*   **Bonus Level:** +0.5 (Jika level > 75)
*   **Bonus Saldo:** +0.5 (Jika saldo > $1,000,000)
*   **Maksimal Bobot:** 2.5

*Catatan: Nilai-nilai di atas dapat diubah melalui konfigurasi (`config.yml`).*

## 📚 Riwayat & Data Presiden

Data historis presiden disimpan dalam `PresidentHistory`. Setiap kali masa jabatan berakhir (baik karena selesai, dimakzulkan, atau mengundurkan diri), rekor baru disimpan.

**Data yang disimpan meliputi:**
*   UUID & Nama Presiden
*   Tanggal Mulai & Akhir Jabatan
*   Approval Rating (Tingkat Kepuasan Publik)
*   Jumlah Executive Orders yang dikeluarkan
*   Jumlah Event yang diselenggarakan
*   Daftar Anggota Kabinet
*   Pencapaian (Accomplishments)
*   Alasan Berhenti (Term End, Recall, Inactive, Admin Removal)

## ⚙️ Konfigurasi Penting

Berikut adalah node konfigurasi utama dalam `config.yml` yang mengatur sistem ini:

```yaml
election:
  registration-days: 3
  campaign-days: 7
  voting-days: 3
  
  max-candidates: 3
  registration-fee: 500000
  
  registration-refund-winner: 1.0
  registration-refund-loser: 0.5

  voter-rewards:
    participation: 10000
    lottery-bonus: 50000
    lottery-winners: 10

  vote-weights:
    base: 1.0
    playtime-threshold-hours: 200
    playtime-bonus: 0.5
    level-threshold: 75
    level-bonus: 0.5
    balance-threshold: 1000000
    balance-bonus: 0.5
    max-weight: 2.5
```

## 🚨 Pemicu Pemilihan Darurat

Sistem akan memicu pemilihan otomatis jika:
1.  Masa jabatan presiden tersisa 5 hari lagi.
2.  Tidak ada presiden yang menjabat saat ini (Kekosongan kekuasaan).
3.  Admin memicu pemilihan darurat secara manual.
