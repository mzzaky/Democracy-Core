# Democracy Core Plugin - Changelog

## Unreleased

### Added
- **GUI Configuration: `hide_attributes` Option**
  - Tambahkan opsi `hide_attributes` pada konfigurasi GUI (`gui.yml`)
  - Memungkinkan penyembunyian item flags/attributes seperti enchantments, durability, attributes, dll
  - Mendukung value tunggal atau list: `ALL`, `ENCHANTS`, `ATTRIBUTES`, `UNBREAKABLE`, `DESTROYS`, `PLACED_ON`, `POTION_EFFECTS`, `DYE`
  - Value `ALL` untuk menyembunyikan semua item flags sekaligus
  - Contoh penggunaan:
    ```yaml
    hide_attributes: ALL
    hide_attributes: ENCHANTS
    hide_attributes: [ENCHANTS, ATTRIBUTES]
    ```
  - Diterapkan otomatis pada item Arena (IRON_SWORD/DIAMOND_SWORD) dengan `hide_attributes: ALL`
  - Diterapkan pada item President dengan `hide_attributes: ATTRIBUTES`
  
### Changed
- **MainMenuGUI.java Enhancement**
  - Tambahkan method `getGUIItemFlags(String path)` untuk membaca konfigurasi `hide_attributes`
  - Update `createPresidentItem()` untuk menggunakan `hide_attributes` dari konfigurasi
  - Update `createArenaItem()` untuk menggunakan `hide_attributes` dari konfigurasi (support state: active/inactive)
  - Tambahkan overloaded `createItem()` method dengan parameter `configPath` untuk auto-apply `custom_model_data` dan `hide_attributes`

### Technical Details
- Item flags didukung:
  - `HIDE_ENCHANTS` - Menyembunyikan enchantment
  - `HIDE_ATTRIBUTES` - Menyembunyikan attribute modifiers (damage, attack speed, etc)
  - `HIDE_UNBREAKABLE` - Menyembunyikan tag unbreakable
  - `HIDE_DESTROYS` - Menyembunyikan "can destroy" tags
  - `HIDE_PLACED_ON` - Menyembunyikan "can be placed on" tags
  - `HIDE_POTION_EFFECTS` - Menyembunyikan efek potion
  - `HIDE_DYE` - Menyembunyikan informasi dye (leather armor)
- Nilai `ALL` menggunakan `ItemFlag.values()` untuk mendapatkan semua flag yang tersedia

---

## Version History

_Previous versions not documented yet._
