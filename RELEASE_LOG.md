# Democracy Core Plugin - Release Log v1.0.0

## Overview
Democracy Core is a comprehensive democracy and government simulation plugin for Minecraft servers. This release introduces a full-featured democratic system with elections, government management, executive powers, cabinet decisions, and community events.

## New Features

### 🏛️ Core Government System
- **Presidential Elections**: Multi-phase election system (Registration → Campaign → Voting → Inauguration)
- **Cabinet System**: 5 ministries (Defense, Treasury, Commerce, Infrastructure, Culture) with appointed ministers
- **Approval Ratings**: Players can rate the president (1-5 stars) affecting government performance
- **Term Limits**: Presidents serve 30-day terms with automatic elections
- **Government History**: Track all past presidents and their achievements

### 🗳️ Election Mechanics
- **Candidate Registration**: Level/playtime/balance requirements with campaign deposits
- **Endorsement System**: Candidates gain support through player endorsements
- **Weighted Voting**: Vote weight based on player level, playtime, and balance
- **Campaign Broadcasting**: Candidates can broadcast campaign messages during campaign phase
- **Voter Rewards**: Participation rewards and lottery system for voters
- **Automatic Elections**: Elections trigger automatically before term end or when no president exists

### ⚖️ Executive Powers
- **Executive Orders**: President can issue powerful orders with treasury costs and cooldowns:
  - **Golden Age**: XP/Vault multipliers, rare drop bonuses
  - **State of Emergency**: Disables PvP server-wide
  - **National Holiday**: Clears player cooldowns
  - **War Economy**: Doubles PvP rewards
  - **Economic Recovery**: Stimulus payments and shop discounts
  - **Infrastructure Initiative**: Haste effects for all players
  - **Environmental Protection**: Farming multipliers
  - **Education Advancement**: XP multipliers
  - **Purge Protocol**: Full PvP everywhere
  - **Presidential Pardon**: Clears player punishments

### 📜 Cabinet Decisions
Each ministry has unique decisions with costs and durations:

#### Defense Ministry
- **War Games Event**: Combat buffs and doubled PvP rewards
- **Military Draft**: Gear distribution and combat buffs
- **National Protection**: Resistance and fire resistance buffs
- **Armory Discount**: 50% off weapons/armor
- **Border Patrol**: Increased mob spawns at borders

#### Treasury Ministry
- **Tax Holiday**: No transaction taxes
- **Economic Stimulus**: Direct payments to all players
- **Auction Boost**: Reduced auction fees
- **Treasury Bonus**: Share treasury with players
- **Market Crash**: 80% price reduction on all shops

#### Commerce Ministry
- **Trade Fair**: Bonus items for trades
- **Resource Boom**: Doubled mining/farming drops
- **Merchant Caravan**: Special traders appear
- **Black Friday**: 75% off everything
- **Treasure Hunt**: Hidden treasures spawn

#### Infrastructure Ministry
- **Construction Boom**: Haste effects and triple building drops
- **Free Real Estate**: Increased claim blocks
- **Builder's Paradise**: Infinite durability and speed
- **Public Works**: Community building events
- **Infrastructure Grants**: Direct funding to players

#### Culture Ministry
- **Festival Week**: Speed/jump buffs and events
- **Double XP Weekend**: XP gains doubled
- **Firework Celebration**: Spectacular fireworks
- **Cultural Exchange**: Bonus rewards for visiting builds
- **Community Event**: Participation tokens and rewards

### ⚔️ Presidential Arena Games
- **Arena Sessions**: President can start 7-day PvP events (max 2 per term)
- **Killstreaks**: Rewards for 5/10/25/50 kill streaks
- **Daily Leaderboards**: Top 10 players receive vault rewards and netherite
- **Grand Prize**: Champion receives 1,000,000 vault + exclusive trophy and gear
- **Arena Kits**: Full iron armor, weapons, and food provided
- **Death/Respawn**: Quick respawn with invulnerability period

### 📜 Recall System
- **Petition Creation**: Players can start recall petitions against president (50k deposit)
- **Signature Collection**: Need 30% of active players to sign (7-day period)
- **Recall Voting**: 3-day voting period requiring 60% to remove
- **Deposit System**: Signers pay deposits, refunded if recall succeeds
- **Cooldowns**: 15-day cooldown after failed recalls

### 💰 Treasury Management
- **Government Funding**: Starting fund of 5M vault per term
- **Donations**: Players can donate to treasury
- **Transaction Tracking**: Full audit trail of all treasury movements
- **Automatic Salaries**: Daily payments to cabinet ministers
- **Executive Order Costs**: Treasury-funded government actions

### 👤 Player Features
- **Player Statistics**: Track playtime, votes cast, times served as president/minister
- **Arena Statistics**: Kills, deaths, killstreaks, best streaks
- **Level Calculation**: Based on playtime (1 level per 10 hours)
- **Daily Rewards**: Presidents and ministers receive vault points and items
- **Buff System**: Special effects for government officials

### 🔧 Administrative Tools
- **Force Elections**: Admin can trigger elections anytime
- **President Management**: Set/remove presidents manually
- **Treasury Control**: Add funds or reset systems
- **Data Management**: Reset government, election, or treasury data
- **Recall Control**: Cancel petitions or force impeachments

### 🎮 Quality of Life
- **Chat Integration**: Government prefixes and announcements
- **Sound Effects**: Audio feedback for major events
- **Visual Effects**: Lightning strikes, fireworks, and title announcements
- **Data Persistence**: All data automatically saved and loaded
- **Configurable**: Extensive configuration options for all systems

## Technical Details
- **Minecraft Version**: 1.21+
- **Dependencies**: Vault (for economy integration)
- **Database**: File-based storage with automatic backups
- **Performance**: Optimized with scheduled tasks and efficient data structures
- **Compatibility**: Designed for large servers with many concurrent players

## Known Issues
- None reported in initial release

## Future Plans
- International relations system
- Political parties
- Advanced diplomacy features
- Custom election maps
- Mobile voting support

---
**Release Date**: December 19, 2025  
**Version**: 1.0.0  
**Authors**: DemocracyCore Team