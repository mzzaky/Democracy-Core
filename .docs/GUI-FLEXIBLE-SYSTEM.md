# Sistem GUI Fleksibel - Dokumentasi

## Gambaran Umum

Sistem GUI yang telah diperbarui memungkinkan konfigurasi item GUI yang lebih fleksibel melalui `gui.yml`. Setiap item di GUI sekarang dapat memiliki:
- **gui_id**: Identifier unik untuk item tersebut
- **on_click**: Action yang akan dijalankan ketika item diklik

## Struktur Konfigurasi `gui.yml`

### Format Dasar
```yaml
gui:
  main_menu:
    items:
      item_name:
        gui_id: "identifier_unique"
        gui_slot: 10
        material: GOLDEN_HELMET
        display_name: "§6Nama Item"
        lore:
          - "Deskripsi item"
        click_sound: "entity.player.levelup"
        on_click: "<action_name>"
```

### Item dengan Multiple States
Beberapa item memiliki multiple states (default/no_president, active/inactive):

```yaml
president_item:
  gui_id: "government"
  gui_slot: 10
  default:  # State ketika presiden ada
    material: GOLDEN_HELMET
    display_name: "§6§l👑 PRESIDENT"
    lore:
      - "..."
    click_sound: "block.anvil.use"
    on_click: "<open_gui_government>"
  no_president:  # State ketika tidak ada presiden
    material: BARRIER
    display_name: "§c§l👑 NO PRESIDENT"
    lore:
      - "..."
    click_sound: "block.anvil.break"
    on_click: "<open_gui_voting>"
```

## Daftar GUI Actions

### Actions untuk Membuka GUI (`OPEN_GUI_*`)
| Action | Deskripsi | Contoh Penggunaan |
|--------|-----------|-------------------|
| `<open_gui_main_menu>` | Membuka Main Menu GUI | Back button |
| `<open_gui_player_stats>` | Membuka Player Statistics GUI | Player head, My Stats item |
| `<open_gui_government>` | Membuka Government GUI | President item (when president exists) |
| `<open_gui_voting>` | Membuka Voting/Election GUI | Election item, Vote Now button |
| `<open_gui_cabinet>` | Membuka Cabinet GUI | Cabinet item |
| `<open_gui_treasury>` | Membuka Treasury GUI | Treasury item |
| `<open_gui_executive_orders>` | Membuka Executive Orders GUI | Executive Orders item |
| `<open_gui_recall>` | Membuka Recall System GUI | Recall item |
|  `<open_gui_history>` | Membuka Presidential History GUI | History item |
| `<open_gui_leaderboard>` | Membuka Leaderboard GUI | Leaderboard item |
| `<open_gui_help>` | Membuka Help/Guide GUI | Help item |

### Actions Khusus (`ACTION_*`)
| Action | Deskripsi | Hasil |
|--------|-----------|-------|
| `<action_register_candidate>` | Mendaftar sebagai kandidat presiden | Menutup inventory, menjalankan perintah registrasi |
| `<action_rate_president>` | Memberi rating kepada presiden | Menutup inventory, menampilkan pesan cara rating |
| `<action_close_inventory>` | Menutup inventory | Menutup GUI tanpa aksi tambahan |
| `<action_arena_info>` | Menampilkan info arena | Menampilkan informasi lengkap tentang arena |
| `<action_endorse_candidate>` | Mendukung kandidat | Menutup inventory, menampilkan  pesan cara endorse |
| `<action_donate_treasury>` | Donasi ke treasury | Menutup inventory, menampilkan pesan cara donasi |
| `<action_join_arena>` | Bergabung ke arena | Menutup inventory, join arena jika sedang active |

## Cara Menambahkan GUI Baru

### 1. Daftarkan Action Baru di `GUIAction.java`
```java
public enum GUIAction {
    // ...
    OPEN_GUI_YOUR_NEW_GUI("open_gui_your_new_gui"),
    // ...
}
```

### 2. Tambahkan Item di `gui.yml`
```yaml
your_new_item:
  gui_id: "your_gui"
  gui_slot: 35
  material: DIAMOND
  display_name: "§bYour New GUI"
  lore:
    - "§7Click to open"
  click_sound: "ui.button.click"
  on_click: "<open_gui_your_new_gui>"
```

### 3. Implementasi Handler di `GUIListener.java`
Tambahkan case handling di method `executeGUIAction`:
```java
case OPEN_GUI_YOUR_NEW_GUI:
    yourNewGUI.openYourNewMenu(player);
    break;
```

## Keuntungan Sistem Baru

### 1. **Fleksibilitas**
- Admin dapat mengubah fungsi item tanpa perlu edit code Java
- Mudah menambahkan atau menghapus item dari GUI

### 2. **Maintainability**
- Semua konfigurasi GUI di satu tempat (`gui.yml`)
- Code Java lebih clean dan modular

### 3. **Customization**
- Server dapat memiliki layout GUI yang berbeda
- Mudah disesuaikan dengan kebutuhan spesifik server

## Catatan Penggunaan

1. **Format Action**: Selalu gunakan format `<action_name>` dengan angle brackets
2. **Case Sensitivity**: Action names adalah case-insensitive
3. **Unknown Actions**: Jika action tidak ditemukan, sistem akan menggunakan `GUIAction.UNKNOWN` dan tidak melakukan apa-apa
4. **Multiple States**: Untuk item dengan multiple states, pastikan setiap state memiliki `on_click` yang sesuai

## Contoh Implementasi

### Mengubah Fungsi Item Eksisting
Misalnya, Anda ingin mengubah "President Item" untuk membuka Help GUI ketika tidak ada presiden:

```yaml
president_item:
  gui_id: "government"
  # ...
  no_president:
    # ...
    on_click: "<open_gui_help>"  # Changed from voting to help
```

### Menambahkan Custom Action
Untuk menambahkan action khusus yang menjalankan command:

1. Tambahkan action enum:
```java
ACTION_CUSTOM_COMMAND("action_custom_command")
```

2. Tambahkan handler:
```java
case ACTION_CUSTOM_COMMAND:
    player.closeInventory();
    player.performCommand("your-custom-command");
    break;
```

3. Gunakan di config:
```yaml
custom_item:
  # ...
  on_click: "<action_custom_command>"
```

## Troubleshooting

### Item Tidak Berfungsi
1. Cek apakah `on_click` ditulis dengan benar (ada angle brackets `< >`)
2. Pastikan action sudah terdaftar di `GUIAction.java`
3. Cek console untuk error messages

### GUI Tidak Terbuka
1. Pastikan GUI class sudah diinstansiasi di `GUIListener`
2. Cek apakah method open sudah benar di handler
3. Verifikasi player memiliki permission (jika ada)

## Future Enhancements

Sistem ini dapat dikembangkan lebih lanjut dengan:
1. **Conditional Actions**: Action berbeda berdasarkan kondisi player
2. **Action Chains**: Menjalankan multiple actions secara berurutan
3. **Permission-Based Actions**: Different actsions untuk different permission levels
4. **Custom Action Parameters**: Passing parameters to actions melalui config

## Changelog

**v1.0.0** (2026-02-13)
- Initial implementation of flexible GUI action system
- Added `gui_id` and `on_click` to all GUI items
- Created `GUIAction` enum for centralized action management
- Updated all items in `main_menu` and `quick_actions` configuration
