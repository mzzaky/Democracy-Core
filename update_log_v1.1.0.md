# DemocracyCore Update Log v1.1.0

## 🆕 New Features - Main Menu GUI System

### Main Menu GUI (Entry Point)
- **New Feature**: Comprehensive Main Menu GUI sebagai entry point utama untuk semua fitur plugin.
- **Purpose**: Mempermudah pemain mengenali dan mengakses fitur-fitur DemocracyCore tanpa perlu mengingat banyak command.
- **Implementation**:
  - Created new `MainMenuGUI.java` class with full-featured menu system
  - Menu accessible via `/dc menu` or `/dc gui` command
  - Visual layout with 54-slot inventory organized into logical sections:
    - **Header Row**: Player info head with stats, level, playtime, balance, and role status
    - **Government Row**: President info, Cabinet overview, Treasury summary, Active effects indicator
    - **Features Row**: Election/Voting, Executive Orders, Presidential Arena, Recall System
    - **Info Row**: President History, Player Stats, Leaderboard, Help & Guide
    - **Quick Actions Row**: Context-aware buttons (Register, Vote, Rate President)
  - Smart icons with glow effects for important items (active voting, arena sessions)
  - Comprehensive lore text explaining each feature
- **Files Created**: `src/main/java/id/democracycore/gui/MainMenuGUI.java`
- **Impact**: Players can now easily navigate all plugin features from a single intuitive GUI interface.

### Player Statistics GUI
- **New Feature**: Detailed player statistics GUI for viewing own and other players' stats.
- **Implementation**:
  - Created new `PlayerStatsGUI.java` class
  - Shows comprehensive player data:
    - Political stats: Votes cast, endorsements given, times as president/minister
    - Arena stats: Kills, deaths, K/D ratio, kill streaks (current and best)
    - Progress info: Level, playtime, online status
  - Role status indicator (President, Minister, or regular citizen)
  - Navigation to leaderboard
- **Files Created**: `src/main/java/id/democracycore/gui/PlayerStatsGUI.java`
- **Command**: Accessible via Main Menu or clicking player heads in leaderboard

### Leaderboard GUI
- **New Feature**: Visual leaderboard showing top players in various categories.
- **Implementation**:
  - Three leaderboard categories displayed side-by-side:
    - **Top Kills**: Players with most arena kills
    - **Top Playtime**: Players with longest playtime
    - **Top Presidents**: Players who served as president most often
  - Click on any player head to view their full stats
  - Gold/Silver/Bronze coloring for top 3 positions
- **Command**: `/dc leaderboard` or `/dc lb`
- **Impact**: Adds competitive element and recognition for active players.

### Help & Guide GUI System
- **New Feature**: Comprehensive in-game help system explaining all plugin features.
- **Implementation**:
  - Created new `HelpGUI.java` class with multiple sub-menus
  - Main help menu with topic selection
  - Detailed guides for each major system:
    - **Election System**: All 4 phases explained with requirements and rewards
    - **President Guide**: Powers, buffs, daily rewards, term info
    - **Cabinet System**: All 5 minister positions with their buffs and salaries
    - **Executive Orders**: Order types, costs, cooldowns, effects
    - **Presidential Arena**: Rules, joining, killstreak rewards
    - **Treasury**: Income sources, expenses, donation guide
    - **Recall System**: How to start, sign, and vote on recall petitions
    - **Command List**: Quick reference for all available commands
- **Files Created**: `src/main/java/id/democracycore/gui/HelpGUI.java`
- **Impact**: New players can learn the plugin without external documentation.

### Quick Actions Menu
- **New Feature**: Context-aware quick actions for common tasks.
- **Implementation**:
  - Shows only relevant actions based on current game state
  - Available actions:
    - Register as candidate (during registration phase)
    - Vote (during voting phase if not voted)
    - Endorse candidate (during registration/campaign)
    - Rate president (if there is one)
    - Donate to treasury
    - Join arena (if session active)
- **Command**: `/dc quickactions` or `/dc qa`
- **Impact**: Streamlines common player actions.

---

## 🔧 Technical Changes

### GUIListener Updates
- **Major Refactor**: Extended GUIListener to handle all new GUI menus.
- **Changes**:
  - Added handlers for MainMenuGUI, PlayerStatsGUI, HelpGUI, and all sub-menus
  - New tracking maps for viewing player stats
  - Helper methods for showing arena and recall info
  - New public methods: `openMainMenu()`, `openPlayerStatsGUI()`, `openLeaderboardGUI()`, `openHelpGUI()`, `openQuickActionsGUI()`
  - Proper drag event cancellation for all new menus
- **Files Modified**: `src/main/java/id/democracycore/gui/GUIListener.java`

### DemocracyCommand Updates
- **New Commands Added**:
  - `/dc menu` or `/dc gui` - Opens main menu GUI
  - `/dc leaderboard` or `/dc lb` - Opens leaderboard
  - `/dc quickactions` or `/dc qa` - Opens quick actions menu
- **Updated Help Text**: Added new commands to help output
- **Tab Completion**: Added new commands to tab completion list
- **Files Modified**: `src/main/java/id/democracycore/commands/DemocracyCommand.java`

---

## Previous Changes (v1.1.0-pre)

### Chat Color Code Parsing Fix
- **Issue**: All messages from the plugin system were not reading chat color codes correctly. The `MessageUtils` class was using `MiniMessage.deserialize()` which only supports MiniMessage tags (e.g., `<red>`), but the configuration file uses legacy color codes (e.g., `&6` for gold).
- **Solution**:
  - Added import for `LegacyComponentSerializer` from Adventure API.
  - Added a static instance of `LegacyComponentSerializer.legacyAmpersand()` for parsing legacy codes.
  - Modified the `parse(String message)` method to check if the message contains `&`:
    - If yes, use legacy deserializer to parse legacy color codes.
    - If no, use MiniMessage deserializer for modern tags.
  - Updated `prefix()` method to use `parse()` instead of direct MiniMessage deserialization, ensuring the prefix (which uses legacy codes) is parsed correctly.
- **Files Modified**: `src/main/java/id/democracycore/utils/MessageUtils.java`
- **Impact**: Plugin messages now correctly display colors as defined in `config.yml`. Supports both legacy codes and MiniMessage tags for future flexibility.


### Admin Command: Stop Executive Orders
- **New Feature**: Added admin command to manually stop active executive orders.
- **Solution**:
  - Added `stopOrder(ExecutiveOrderType type)` method to `ExecutiveOrderManager` class that finds active orders, sets them inactive, and removes effects using existing `expireOrder()` method.
  - Implemented `/dc admin stoporder <order_type>` command in `DemocracyCommand` class with:
    - Permission check for `democracy.admin`
    - Input validation for order type
    - Success/failure feedback messages
    - Broadcast notification to all players when orders are stopped
    - Updated admin help text and tab completion
  - Added comprehensive tab completion support for all executive order types.
- **Files Modified**:
  - `src/main/java/id/democracycore/managers/ExecutiveOrderManager.java`
  - `src/main/java/id/democracycore/commands/DemocracyCommand.java`
- **Impact**:
  - Admins can now manually terminate any active executive order immediately.
  - Provides emergency control over game-altering executive orders.
  - Maintains proper cleanup of order effects and notifications.
  - Enhances server administration capabilities for managing active orders.

### Language Configuration File Separation
- **Configuration Improvement**: Moved language configuration from `config.yml` to dedicated `language.yml` file for better organization and easier management.
- **Solution**:
  - Created new `language.yml` file in `src/main/resources/` containing all plugin message configurations.
  - Removed language section from `config.yml` to separate concerns.
  - Updated `MessageUtils.loadLanguage()` method to load from `language.yml` instead of `config.yml`:
    - Added file handling for `language.yml` with automatic resource saving if file doesn't exist.
    - Modified to use `YamlConfiguration.loadConfiguration()` for loading the separate file.
    - Updated `reloadLanguage()` method to only reload language file without affecting main config.
  - Maintained backward compatibility and existing message key system.
- **Files Modified**:
  - `src/main/resources/config.yml` (removed language section)
  - `src/main/resources/language.yml` (new file)
  - `src/main/java/id/democracycore/utils/MessageUtils.java`
- **Impact**:
  - Cleaner separation of configuration types (game settings vs. messages).
  - Easier localization and message management without touching main config.
  - Reduced `config.yml` size and complexity.
  - Language changes can be reloaded independently using `/dc admin reload`.
  - Maintains all existing functionality and message key placeholders.

---

## 📁 Files Summary

### New Files Created (v1.1.0)
1. `src/main/java/id/democracycore/gui/MainMenuGUI.java` - Main menu entry point
2. `src/main/java/id/democracycore/gui/PlayerStatsGUI.java` - Player statistics and leaderboard
3. `src/main/java/id/democracycore/gui/HelpGUI.java` - Help and guide system

### Files Modified (v1.1.0)
1. `src/main/java/id/democracycore/gui/GUIListener.java` - Extended for new GUIs
2. `src/main/java/id/democracycore/commands/DemocracyCommand.java` - New commands added

---

## 🎮 New Commands Reference

| Command | Aliases | Description |
|---------|---------|-------------|
| `/dc menu` | `/dc gui` | Open main menu GUI |
| `/dc leaderboard` | `/dc lb` | Open leaderboard GUI |
| `/dc quickactions` | `/dc qa` | Open quick actions menu |

---

## Version Information
- **Previous Version**: 1.0.0
- **New Version**: 1.1.0
- **Date**: 2025-12-22
- **Update Focus**: GUI Enhancement & User Experience Improvement
