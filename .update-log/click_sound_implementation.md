# Audit dan Implementasi Click Sound pada Custom GUI

## Tanggal: 2026-02-13

## Masalah yang Ditemukan
Fungsi `click_sound` yang dikonfigurasi di `gui.yml` tidak berfungsi karena:
1. Method `getGUISound()` sudah ada di `MainMenuGUI.java` tetapi tidak pernah dipanggil
2. Tidak ada integrasi antara konfigurasi sound di `gui.yml` dengan event handler di `GUIListener.java`
3. Tidak ada mekanisme untuk memutar sound saat player mengklik item di GUI

## Solusi yang Diimplementasikan

### 1. MainMenuGUI.java
Menambahkan 3 method baru untuk menangani click sound:

#### Method `playClickSound(Player player, int slot)`
- **Fungsi**: Public method yang dipanggil dari GUIListener untuk memutar sound berdasarkan slot yang diklik
- **Parameter**: 
  - `player`: Player yang mengklik item
  - `slot`: Slot inventory yang diklik (0-53)
- **Cara Kerja**: Mengambil path konfigurasi sound dari `getClickSoundPath()`, lalu memutar sound menggunakan `MessageUtils.playSound()`

#### Method `getClickSoundPath(int slot)`
- **Fungsi**: Private helper method untuk mapping slot ke path konfigurasi di `gui.yml`
- **Return**: String path ke konfigurasi `click_sound` di gui.yml, atau null jika tidak dikonfigurasi
- **Mapping Slot**:
  - Slot 4: Player Head
  - Slot 10: President Item (dengan deteksi state: default/no_president)
  - Slot 12: Cabinet
  - Slot 14: Treasury
  - Slot 16: Active Effects (dengan deteksi state: active/inactive)
  - Slot 19: Election
  - Slot 21: Executive Orders
  - Slot 23: Arena (dengan deteksi state: active/inactive)
  - Slot 25: Recall (dengan deteksi state: active/inactive)
  - Slot 28: History
  - Slot 30: My Stats
  - Slot 32: Leaderboard
  - Slot 34: Help
  - Slot 38: Register Candidate
  - Slot 40: Vote Now
  - Slot 42: Rate President
  - Slot 49: Close Button

#### Method `playQuickActionsSound(Player player, Material clickedMaterial)`
- **Fungsi**: Public method untuk memutar sound di Quick Actions menu
- **Parameter**:
  - `player`: Player yang mengklik item
  - `clickedMaterial`: Material dari item yang diklik
- **Mapping Material**:
  - EMERALD: Register Candidate
  - LIME_WOOL: Vote Candidate
  - GOLDEN_APPLE: Endorse Candidate
  - NETHER_STAR: Rate President
  - GOLD_INGOT: Donate Treasury
  - IRON_SWORD: Join Arena
  - ARROW: Back Button

### 2. GUIListener.java
Menambahkan integrasi sound di 2 handler:

#### handleMainMenuGUI()
- Menambahkan `mainMenuGUI.playClickSound(player, slot);` di awal method
- Sound akan diputar sebelum action apapun dilakukan
- Ini memastikan setiap klik di Main Menu memiliki feedback audio

#### handleQuickActionsGUI()
- Menambahkan `mainMenuGUI.playQuickActionsSound(player, clicked.getType());` di awal method
- Sound akan diputar berdasarkan material item yang diklik
- Memberikan feedback audio yang konsisten dengan Main Menu

## Konfigurasi di gui.yml
Semua sound sudah dikonfigurasi dengan benar di `gui.yml`, contoh:
```yaml
player_head:
  click_sound: "entity.player.levelup"

president_item:
  default:
    click_sound: "block.anvil.use"
  no_president:
    click_sound: "block.anvil.break"

arena_item:
  active:
    click_sound: "entity.player.attack.crit"
  inactive:
    click_sound: "entity.player.attack.sweep"
```

## Cara Kerja Sound System

1. **Player mengklik item di GUI**
2. **GUIListener menangkap event** (onInventoryClick)
3. **Handler yang sesuai dipanggil** (handleMainMenuGUI atau handleQuickActionsGUI)
4. **Sound dimainkan** melalui `mainMenuGUI.playClickSound()` atau `mainMenuGUI.playQuickActionsSound()`
5. **Method mengambil path konfigurasi** berdasarkan slot/material
6. **Sound dibaca dari gui.yml** menggunakan `getGUISound()`
7. **Sound dikonversi** dari format "entity.player.levelup" menjadi Sound.ENTITY_PLAYER_LEVELUP
8. **Sound diputar** ke player menggunakan `MessageUtils.playSound()`

## Fitur Tambahan

### Dynamic Sound Based on State
Beberapa item memiliki sound berbeda tergantung state:
- **President Item**: Sound berbeda saat ada presiden vs tidak ada presiden
- **Active Effects**: Sound berbeda saat ada effect aktif vs tidak ada
- **Arena**: Sound berbeda saat arena aktif vs tidak aktif
- **Recall**: Sound berbeda saat ada petition aktif vs tidak ada

### Fallback Sound
Jika sound di konfigurasi tidak valid atau tidak ditemukan, sistem akan menggunakan fallback sound `Sound.UI_BUTTON_CLICK`

## Testing yang Perlu Dilakukan

1. ✅ Compile project dan pastikan tidak ada error
2. ⏳ Buka Main Menu GUI dan klik setiap item
3. ⏳ Verifikasi sound yang tepat dimainkan untuk setiap item
4. ⏳ Test sound berbeda untuk item dengan multiple states (President, Arena, Recall, Active Effects)
5. ⏳ Buka Quick Actions menu dan test semua button
6. ⏳ Coba ubah konfigurasi sound di gui.yml dan reload config
7. ⏳ Test dengan sound yang tidak valid (harus fallback ke UI_BUTTON_CLICK)

## File yang Dimodifikasi

1. **MainMenuGUI.java**
   - Menambahkan 3 method baru (130+ baris kode)
   - Lokasi: Lines 803-928

2. **GUIListener.java**
   - Menambahkan 2 baris untuk integrasi sound
   - Lokasi: Lines 163, 281

## Catatan Penting

- Sound dimainkan SEBELUM action untuk memberikan feedback instan
- Semua sound menggunakan volume 1.0f dan pitch 1.0f (dapat disesuaikan di MessageUtils jika perlu)
- Sound hanya dimainkan untuk slot/material yang dikonfigurasi di gui.yml
- Slot yang tidak memiliki konfigurasi sound tidak akan error, hanya tidak memutar sound

## Backward Compatibility

Implementasi ini sepenuhnya backward compatible:
- Tidak mengubah konfigurasi yang sudah ada
- Tidak mengubah behavior GUI yang sudah ada
- Hanya menambahkan fitur audio feedback baru
- Jika konfigurasi sound tidak ada, tidak akan error
