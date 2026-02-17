# Dokumentasi: Hide Attributes (Item Flags)

## Deskripsi
Fitur `hide_attributes` memungkinkan administrator untuk menyembunyikan informasi tertentu pada item dalam GUI, seperti enchantment, durability, attributes, dan lain-lain. Ini membuat tampilan GUI lebih bersih dan profesional.

## Lokasi Konfigurasi
File: `src/main/resources/gui.yml`

## Cara Penggunaan

### Format Dasar
```yaml
gui:
  main_menu:
    items:
      nama_item:
        material: MATERIAL_NAME
        display_name: "§aDisplay Name"
        hide_attributes: VALUE  # <-- Tambahkan opsi ini
        lore:
          - "§7Lore line 1"
```

### Nilai yang Didukung

#### 1. Menyembunyikan Semua Flags
```yaml
hide_attributes: ALL
```
Menyembunyikan semua informasi item flags (enchantment, attributes, dll).

**Contoh Penggunaan:**
- Sword/Axe tanpa info attack damage
- Armor tanpa info armor value
- Item dengan enchantment tanpa daftar enchant

#### 2. Menyembunyikan Flag Spesifik (Single Value)
```yaml
hide_attributes: ENCHANTS
```
Hanya menyembunyikan enchantment saja.

**Nilai yang tersedia:**
- `ENCHANTS` - Menyembunyikan enchantment
- `ATTRIBUTES` - Menyembunyikan modifier attributes (damage, speed, armor, dll)
- `UNBREAKABLE` - Menyembunyikan tag "Unbreakable"
- `DESTROYS` - Menyembunyikan tag "Can Destroy"
- `PLACED_ON` - Menyembunyikan tag "Can be Placed On"
- `POTION_EFFECTS` - Menyembunyikan efek potion
- `DYE` - Menyembunyikan info dye (untuk leather armor)

#### 3. Menyembunyikan Beberapa Flags (List)
```yaml
hide_attributes:
  - ENCHANTS
  - ATTRIBUTES
  - UNBREAKABLE
```
Menyembunyikan beberapa flags sekaligus.

## Contoh Implementasi

### Contoh 1: Arena Item dengan ALL Flags
```yaml
arena_item:
  gui_slot: 23
  inactive:
    material: IRON_SWORD
    custom_model_data: 0
    hide_attributes: ALL  # Sembunyikan semua info (termasuk attack damage)
    display_name: "§4§l⚔ PRESIDENTIAL ARENA"
    lore:
      - "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
      - "§7Status: §cInactive"
      - "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
```

**Hasil:** IRON_SWORD tidak akan menampilkan "Attack Damage: 6" dan info lainnya.

### Contoh 2: President Item dengan ATTRIBUTES
```yaml
president_item:
  gui_slot: 10
  default:
    material: GOLDEN_HELMET
    custom_model_data: 0
    hide_attributes: ATTRIBUTES  # Hanya sembunyikan attributes
    display_name: "§6§l👑 PRESIDENT"
    lore:
      - "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
      - "§7Name: §f{president_name}"
      - "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
```

**Hasil:** GOLDEN_HELMET tidak akan menampilkan "Armor: +3" tapi masih menampilkan info lain.

### Contoh 3: Item dengan Enchant (Hide Multiple)
```yaml
special_item:
  material: DIAMOND_SWORD
  hide_attributes:
    - ENCHANTS      # Sembunyikan daftar enchantment
    - ATTRIBUTES    # Sembunyikan attack damage/speed
  display_name: "§bSpecial Sword"
  lore:
    - "§7A mysterious sword..."
```

**Hasil:** DIAMOND_SWORD dengan enchantment tidak akan menampilkan nama enchant dan attack damage.

## Implementasi Teknis

### Java Implementation
Di `MainMenuGUI.java`, method `getGUIItemFlags()` akan:
1. Membaca nilai dari `gui.yml`
2. Convert string ke `ItemFlag` enum
3. Return array `ItemFlag[]` untuk diterapkan ke ItemMeta

```java
// Contoh penggunaan di code
ItemFlag[] hideFlags = getGUIItemFlags("gui.main_menu.items.arena_item.active.hide_attributes");
if (hideFlags.length > 0) {
    meta.addItemFlags(hideFlags);
}
```

### State-based Configuration
Item dengan multiple state (seperti arena: active/inactive) dapat memiliki `hide_attributes` berbeda:

```yaml
arena_item:
  inactive:
    hide_attributes: ALL  # Sembunyikan semua saat inactive
  active:
    hide_attributes: [ATTRIBUTES, ENCHANTS]  # Hanya sembunyikan attributes & enchants saat active
```

## Tips & Best Practices

### 1. Gunakan ALL untuk Weapons/Armor
Untuk item seperti sword, axe, helmet, dll yang hanya dekoratif di GUI:
```yaml
hide_attributes: ALL
```

### 2. Gunakan ATTRIBUTES untuk Item dengan Enchant Khusus
Jika ingin menampilkan enchantment tapi menyembunyikan stats:
```yaml
hide_attributes: ATTRIBUTES
```

### 3. Gunakan List untuk Kontrol Presisi
Jika perlu kontrol lebih detail:
```yaml
hide_attributes:
  - ENCHANTS
  - UNBREAKABLE
  - DESTROYS
```

### 4. Tidak Perlu untuk Item Dekoratif
Item seperti GLASS_PANE, BARRIER, dll tidak perlu `hide_attributes`:
```yaml
glass_pane:
  material: GRAY_STAINED_GLASS_PANE
  display_name: " "
  # Tidak perlu hide_attributes
```

## Troubleshooting

### Value Tidak Valid
Jika menggunakan value yang tidak valid, akan muncul warning di console:
```
[WARN] Invalid hide_attributes value at gui.main_menu.items.example.hide_attributes: INVALID_VALUE
```

**Solusi:** Gunakan nilai yang valid (lihat daftar di atas).

### Hide Attributes Tidak Bekerja
1. Pastikan path konfigurasi benar
2. Pastikan value menggunakan huruf kapital: `ALL`, `ENCHANTS`, bukan `all`, `enchants`
3. Reload plugin setelah mengubah `gui.yml`

### Enchantment Masih Terlihat
Jika menggunakan `ATTRIBUTES` tapi enchantment masih terlihat, gunakan:
```yaml
hide_attributes:
  - ENCHANTS
  - ATTRIBUTES
```

## Compatibility Notes
- Fitur ini menggunakan Bukkit/Spigot `ItemFlag` API
- Compatible dengan Minecraft 1.8+
- Semua ItemFlag values akan otomatis didukung jika Bukkit API diupdate

## Version History
- **v1.3.0** - Initial implementation of `hide_attributes` feature
