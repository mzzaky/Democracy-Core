package id.democracycore.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import id.democracycore.DemocracyCore;
import id.democracycore.models.CabinetDecision;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.Government;
import id.democracycore.models.RecallPetition;
import id.democracycore.utils.MessageUtils;

public class GUIListener implements Listener {

    private final DemocracyCore plugin;
    private final VotingGUI votingGUI;
    private final GovernmentGUI governmentGUI;
    private final MainMenuGUI mainMenuGUI;
    private final PlayerStatsGUI playerStatsGUI;
    private final HelpGUI helpGUI;
    private final RecallGUI recallGUI;

    // Track which candidate a player is viewing
    private final Map<UUID, UUID> viewingCandidate = new HashMap<>();
    // Track which cabinet position a player is viewing
    private final Map<UUID, CabinetDecision.CabinetPosition> viewingCabinetPosition = new HashMap<>();
    // Track which player stats a player is viewing
    private final Map<UUID, UUID> viewingPlayerStats = new HashMap<>();

    public GUIListener(DemocracyCore plugin) {
        this.plugin = plugin;
        this.votingGUI = new VotingGUI(plugin);
        this.governmentGUI = new GovernmentGUI(plugin);
        this.mainMenuGUI = new MainMenuGUI(plugin);
        this.playerStatsGUI = new PlayerStatsGUI(plugin);
        this.helpGUI = new HelpGUI(plugin);
        this.recallGUI = new RecallGUI(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))
            return;

        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR)
            return;
        if (clicked.getType() == Material.GRAY_STAINED_GLASS_PANE ||
                clicked.getType() == Material.ORANGE_STAINED_GLASS_PANE) {
            event.setCancelled(true);
            return;
        }

        // Handle different GUIs
        if (title.equals(MainMenuGUI.MAIN_MENU_TITLE)) {
            event.setCancelled(true);
            handleMainMenuGUI(player, clicked, event.getSlot());
        } else if (title.equals(MainMenuGUI.QUICK_ACTIONS_TITLE)) {
            event.setCancelled(true);
            handleQuickActionsGUI(player, clicked);
        } else if (title.equals(VotingGUI.VOTING_GUI_TITLE)) {
            event.setCancelled(true);
            handleVotingGUI(player, clicked);
        } else if (title.startsWith(VotingGUI.CANDIDATE_GUI_TITLE)) {
            event.setCancelled(true);
            handleCandidateGUI(player, clicked, title);
        } else if (title.equals(GovernmentGUI.GOVERNMENT_GUI_TITLE)) {
            event.setCancelled(true);
            handleGovernmentGUI(player, clicked);
        } else if (title.equals("§2§l💰 SALARY & REWARDS 💰")) {
            event.setCancelled(true);
            handleSalaryGUI(player, clicked);
        } else if (title.equals(GovernmentGUI.ORDERS_GUI_TITLE)) {
            event.setCancelled(true);
            handleOrdersGUI(player, clicked);
        } else if (title.equals(GovernmentGUI.CABINET_GUI_TITLE)) {
            event.setCancelled(true);
            handleCabinetGUI(player, clicked, event.getSlot());
        } else if (title.equals(GovernmentGUI.CABINET_DECISIONS_TITLE)) {
            event.setCancelled(true);
            handleCabinetDecisionsGUI(player, clicked);
        } else if (title.contains("TREASURY") && !title.contains("LOGS")) {
            event.setCancelled(true);
            handleTreasuryGUI(player, clicked);
        } else if (title.equals("§6§l📜 TREASURY LOGS 📜")) {
            event.setCancelled(true);
            handleTreasuryTransactionsGUI(player, clicked);
        } else if (title.contains("SEJARAH")) {
            event.setCancelled(true);
            handleHistoryGUI(player, clicked);
        } else if (title.equals(PlayerStatsGUI.STATS_GUI_TITLE)) {
            event.setCancelled(true);
            handlePlayerStatsGUI(player, clicked, event.getSlot());
        } else if (title.equals(PlayerStatsGUI.LEADERBOARD_TITLE)) {
            event.setCancelled(true);
            handleLeaderboardGUI(player, clicked, event.getSlot());
        } else if (title.equals(HelpGUI.HELP_MENU_TITLE)) {
            event.setCancelled(true);
            handleHelpMenuGUI(player, clicked, event.getSlot());
        } else if (title.equals(HelpGUI.HELP_ELECTION_TITLE) ||
                title.equals(HelpGUI.HELP_PRESIDENT_TITLE) ||
                title.equals(HelpGUI.HELP_CABINET_TITLE) ||
                title.equals(HelpGUI.HELP_ORDERS_TITLE) ||
                title.equals(HelpGUI.HELP_ARENA_TITLE) ||
                title.equals(HelpGUI.HELP_TREASURY_TITLE)) {
            event.setCancelled(true);
            handleHelpSubMenuGUI(player, clicked);
        } else if (title.equals(RecallGUI.RECALL_MENU_TITLE)) {
            event.setCancelled(true);
            handleRecallMenuGUI(player, clicked, event.getSlot());
        } else if (title.equals(RecallGUI.RECALL_CONFIRM_TITLE)) {
            event.setCancelled(true);
            handleRecallConfirmGUI(player, clicked);
        } else if (title.equals(RecallGUI.RECALL_VOTE_TITLE)) {
            event.setCancelled(true);
            handleRecallVoteGUI(player, clicked);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();

        // Cancel drag in all our GUIs
        if (title.equals(MainMenuGUI.MAIN_MENU_TITLE) ||
                title.equals(MainMenuGUI.QUICK_ACTIONS_TITLE) ||
                title.equals(VotingGUI.VOTING_GUI_TITLE) ||
                title.startsWith(VotingGUI.CANDIDATE_GUI_TITLE) ||
                title.equals(GovernmentGUI.GOVERNMENT_GUI_TITLE) ||
                title.equals("§2§l💰 SALARY & REWARDS 💰") ||
                title.equals(GovernmentGUI.ORDERS_GUI_TITLE) ||
                title.equals(GovernmentGUI.CABINET_GUI_TITLE) ||
                title.equals(GovernmentGUI.CABINET_DECISIONS_TITLE) ||
                title.contains("TREASURY") ||
                title.contains("SEJARAH") ||
                title.equals(PlayerStatsGUI.STATS_GUI_TITLE) ||
                title.equals(PlayerStatsGUI.LEADERBOARD_TITLE) ||
                title.equals(HelpGUI.HELP_MENU_TITLE) ||
                title.contains("PANDUAN") ||
                title.equals(RecallGUI.RECALL_MENU_TITLE) ||
                title.equals(RecallGUI.RECALL_CONFIRM_TITLE) ||
                title.equals(RecallGUI.RECALL_VOTE_TITLE)) {
            event.setCancelled(true);
        }
    }

    // === MAIN MENU GUI ===

    private void handleMainMenuGUI(Player player, ItemStack clicked, int slot) {
        // Close button
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // Player Head - Open Stats
        if (slot == 4 && clicked.getType() == Material.PLAYER_HEAD) {
            viewingPlayerStats.put(player.getUniqueId(), player.getUniqueId());
            playerStatsGUI.openPlayerStats(player, player.getUniqueId());
            return;
        }

        // President Info - Slot 10
        if (slot == 10 && (clicked.getType() == Material.GOLDEN_HELMET || clicked.getType() == Material.BARRIER)) {
            Government gov = plugin.getDataManager().getGovernment();
            if (gov.hasPresident()) {
                governmentGUI.openGovernmentMenu(player);
            } else {
                votingGUI.openVotingMenu(player);
            }
            return;
        }

        // Cabinet - Slot 12
        if (slot == 12 && clicked.getType() == Material.LECTERN) {
            governmentGUI.openCabinetMenu(player);
            return;
        }

        // Treasury - Slot 14
        if (slot == 14 && clicked.getType() == Material.GOLD_BLOCK) {
            governmentGUI.openTreasuryMenu(player);
            return;
        }

        // Active Effects - Slot 16
        if (slot == 16 && (clicked.getType() == Material.BEACON || clicked.getType() == Material.GLASS)) {
            governmentGUI.openExecutiveOrdersMenu(player);
            return;
        }

        // Election - Slot 19
        if (slot == 19 && clicked.getType() == Material.PAPER) {
            votingGUI.openVotingMenu(player);
            return;
        }

        // Executive Orders - Slot 21
        if (slot == 21 && clicked.getType() == Material.WRITABLE_BOOK) {
            governmentGUI.openExecutiveOrdersMenu(player);
            return;
        }

        // Arena - Slot 23
        if (slot == 23 && (clicked.getType() == Material.DIAMOND_SWORD || clicked.getType() == Material.IRON_SWORD)) {
            // Show arena info
            showArenaInfo(player);
            return;
        }

        // Recall - Slot 25
        if (slot == 25 && (clicked.getType() == Material.REDSTONE_TORCH || clicked.getType() == Material.TORCH)) {
            recallGUI.openRecallMenu(player);
            return;
        }

        // History - Slot 28
        if (slot == 28 && clicked.getType() == Material.BOOK) {
            governmentGUI.openHistoryMenu(player);
            return;
        }

        // My Stats - Slot 30
        if (slot == 30 && clicked.getType() == Material.COMPASS) {
            viewingPlayerStats.put(player.getUniqueId(), player.getUniqueId());
            playerStatsGUI.openPlayerStats(player, player.getUniqueId());
            return;
        }

        // Leaderboard - Slot 32
        if (slot == 32 && clicked.getType() == Material.GOLDEN_APPLE) {
            playerStatsGUI.openLeaderboard(player);
            return;
        }

        // Help - Slot 34
        if (slot == 34 && clicked.getType() == Material.KNOWLEDGE_BOOK) {
            helpGUI.openHelpMenu(player);
            return;
        }

        // Quick Actions - Register (Slot 38)
        if (slot == 38 && clicked.getType() == Material.EMERALD) {
            player.closeInventory();
            plugin.getElectionManager().registerCandidate(player, "");
            return;
        }

        // Quick Actions - Vote (Slot 40)
        if (slot == 40 && clicked.getType() == Material.LIME_CONCRETE) {
            votingGUI.openVotingMenu(player);
            return;
        }

        // Quick Actions - Rate (Slot 42)
        if (slot == 42 && clicked.getType() == Material.NETHER_STAR) {
            player.closeInventory();
            MessageUtils.send(player, "<yellow>Gunakan: <white>/dc rate <1-5> <gray>untuk memberi rating presiden");
        }
    }

    // === QUICK ACTIONS GUI ===

    private void handleQuickActionsGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            mainMenuGUI.openMainMenu(player);
            return;
        }

        if (clicked.getType() == Material.EMERALD) {
            player.closeInventory();
            plugin.getElectionManager().registerCandidate(player, "");
            return;
        }

        if (clicked.getType() == Material.LIME_WOOL) {
            votingGUI.openVotingMenu(player);
            return;
        }

        if (clicked.getType() == Material.GOLDEN_APPLE) {
            player.closeInventory();
            MessageUtils.send(player, "<yellow>Gunakan: <white>/dc endorse <nama_kandidat>");
            return;
        }

        if (clicked.getType() == Material.NETHER_STAR) {
            player.closeInventory();
            MessageUtils.send(player, "<yellow>Gunakan: <white>/dc rate <1-5>");
        }

        if (clicked.getType() == Material.GOLD_INGOT) {
            player.closeInventory();
            MessageUtils.send(player, "<yellow>Gunakan: <white>/dc treasury donate <jumlah>");
            return;
        }

        if (clicked.getType() == Material.IRON_SWORD) {
            player.closeInventory();
            plugin.getArenaManager().joinArena(player);
            return;
        }
    }

    // === VOTING GUI ===

    private void handleVotingGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
        }

        if (clicked.getType() == Material.PLAYER_HEAD) {
            // Get candidate from head
            SkullMeta meta = (SkullMeta) clicked.getItemMeta();
            if (meta.getOwningPlayer() != null) {
                UUID candidateUUID = meta.getOwningPlayer().getUniqueId();
                viewingCandidate.put(player.getUniqueId(), candidateUUID);
                votingGUI.openCandidateInfo(player, candidateUUID);
            }
        }
    }

    private void handleCandidateGUI(Player player, ItemStack clicked, String title) {
        // Extract candidate name from title
        String candidateName = title.replace(VotingGUI.CANDIDATE_GUI_TITLE, "");
        UUID candidateUUID = viewingCandidate.get(player.getUniqueId());

        if (clicked.getType() == Material.ARROW) {
            // Go back
            votingGUI.openVotingMenu(player);
            return;
        }

        if (clicked.getType() == Material.LIME_WOOL && candidateUUID != null) {
            // Vote
            player.closeInventory();
            plugin.getElectionManager().castVote(player, candidateUUID);
            return;
        }

        if (clicked.getType() == Material.GOLDEN_APPLE && candidateUUID != null) {
            // Endorse
            player.closeInventory();
            plugin.getElectionManager().endorseCandidate(player, candidateUUID);
        }
    }

    // === GOVERNMENT GUI ===

    private void handleGovernmentGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
        }

        if (clicked.getType() == Material.LECTERN) {
            // Cabinet
            governmentGUI.openCabinetMenu(player);
            return;
        }

        if (clicked.getType() == Material.WRITABLE_BOOK) {
            // Executive Orders
            governmentGUI.openExecutiveOrdersMenu(player);
            return;
        }

        if (clicked.getType() == Material.GOLD_BLOCK) {
            // Treasury
            governmentGUI.openTreasuryMenu(player);
            return;
        }

        if (clicked.getType() == Material.PAPER) {
            // Election/Voting
            votingGUI.openVotingMenu(player);
            return;
        }

        if (clicked.getType() == Material.BOOK) {
            // History
            governmentGUI.openHistoryMenu(player);
            return;
        }

        if (clicked.getType() == Material.COMPASS) {
            // Main Menu
            mainMenuGUI.openMainMenu(player);
            return;
        }

        if (clicked.getType() == Material.EMERALD) {
            // Salary Claim
            governmentGUI.openSalaryMenu(player);
            return;
        }
    }

    // === SALARY GUI ===

    private void handleSalaryGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            governmentGUI.openGovernmentMenu(player);
            return;
        }

        // Claim Salary
        if (clicked.getType() == Material.EMERALD_BLOCK) {
            player.closeInventory();
            plugin.getGovernmentManager().claimDailySalary(player);
            return;
        }
    }

    // === ORDERS GUI ===

    private void handleOrdersGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            governmentGUI.openGovernmentMenu(player);
            return;
        }

        // Check if clicking on an available order
        if (clicked.getType() == Material.YELLOW_CONCRETE) {
            String displayName = clicked.getItemMeta().getDisplayName();

            // Find the order type
            for (ExecutiveOrder.ExecutiveOrderType type : ExecutiveOrder.ExecutiveOrderType.values()) {
                if (displayName.contains(type.getDisplayName())) {
                    player.closeInventory();
                    plugin.getExecutiveOrderManager().issueOrder(player, type);
                }
            }
        }
    }

    // === CABINET GUI ===

    private void handleCabinetGUI(Player player, ItemStack clicked, int slot) {
        if (clicked.getType() == Material.ARROW) {
            governmentGUI.openGovernmentMenu(player);
            return;
        }

        // Minister heads - map slots to positions
        int[] ministerSlots = { 11, 12, 13, 14, 15 };
        CabinetDecision.CabinetPosition[] positions = CabinetDecision.CabinetPosition.values();

        for (int i = 0; i < ministerSlots.length && i < positions.length; i++) {
            if (slot == ministerSlots[i]) {
                viewingCabinetPosition.put(player.getUniqueId(), positions[i]);
                governmentGUI.openCabinetDecisionsMenu(player, positions[i]);
                return;
            }
        }

        // My position button
        if (clicked.getType() == Material.DIAMOND) {
            Government gov = plugin.getDataManager().getGovernment();
            for (CabinetDecision.CabinetPosition pos : positions) {
                UUID minister = gov.getCabinetMember(Government.CabinetPosition.valueOf(pos.name()));
                if (minister != null && minister.equals(player.getUniqueId())) {
                    viewingCabinetPosition.put(player.getUniqueId(), pos);
                    governmentGUI.openCabinetDecisionsMenu(player, pos);
                    return;
                }
            }
        }

        // Active decisions
        if (clicked.getType() == Material.ENCHANTED_BOOK) {
            // For now, just show the first position with active decisions
            java.util.List<CabinetDecision> activeDecisions = plugin.getDataManager().getActiveDecisions();
            if (!activeDecisions.isEmpty()) {
                CabinetDecision first = activeDecisions.get(0);
                viewingCabinetPosition.put(player.getUniqueId(), first.getMinisterPosition());
                governmentGUI.openCabinetDecisionsMenu(player, first.getMinisterPosition());
            }
        }
    }

    // === CABINET DECISIONS GUI ===

    private void handleCabinetDecisionsGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            governmentGUI.openCabinetMenu(player);
            return;
        }

        // Check if clicking on available decision
        if (clicked.getType() == Material.YELLOW_WOOL) {
            String displayName = clicked.getItemMeta().getDisplayName();

            CabinetDecision.CabinetPosition position = viewingCabinetPosition.get(player.getUniqueId());
            if (position == null)
                return;

            // Find decision type
            for (CabinetDecision.DecisionType type : CabinetDecision.DecisionType.values()) {
                if (type.getPosition() == position && displayName.contains(type.name())) {
                    player.closeInventory();
                    // Create and activate the decision
                    plugin.getCabinetManager().executeDecision(player.getUniqueId(), type);
                }
            }
        }
    }

    // === TREASURY GUI ===

    private void handleTreasuryGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            governmentGUI.openGovernmentMenu(player);
            return;
        }

        if (clicked.getType() == Material.HOPPER) {
            player.closeInventory();
            MessageUtils.send(player, "<yellow>Gunakan: <white>/dc treasury donate <jumlah>");
        }

        if (clicked.getType() == Material.BOOK) {
            governmentGUI.openTreasuryTransactionsMenu(player);
        }
    }

    private void handleTreasuryTransactionsGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            governmentGUI.openTreasuryMenu(player);
        }
    }

    // === HISTORY GUI ===

    private void handleHistoryGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            governmentGUI.openGovernmentMenu(player);
        }
    }

    // === PLAYER STATS GUI ===

    private void handlePlayerStatsGUI(Player player, ItemStack clicked, int slot) {
        if (clicked.getType() == Material.ARROW) {
            mainMenuGUI.openMainMenu(player);
            return;
        }

        if (clicked.getType() == Material.GOLD_INGOT) {
            playerStatsGUI.openLeaderboard(player);
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }
    }

    // === LEADERBOARD GUI ===

    private void handleLeaderboardGUI(Player player, ItemStack clicked, int slot) {
        if (clicked.getType() == Material.ARROW) {
            mainMenuGUI.openMainMenu(player);
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // Click on player head to view their stats
        if (clicked.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clicked.getItemMeta();
            if (meta.getOwningPlayer() != null) {
                UUID targetUUID = meta.getOwningPlayer().getUniqueId();
                viewingPlayerStats.put(player.getUniqueId(), targetUUID);
                playerStatsGUI.openPlayerStats(player, targetUUID);
            }
        }
    }

    // === HELP MENU GUI ===

    private void handleHelpMenuGUI(Player player, ItemStack clicked, int slot) {
        if (clicked.getType() == Material.ARROW) {
            mainMenuGUI.openMainMenu(player);
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // Election Help - Slot 19
        if (slot == 19 && clicked.getType() == Material.PAPER) {
            helpGUI.openElectionHelp(player);
            return;
        }

        // President Help - Slot 21
        if (slot == 21 && clicked.getType() == Material.GOLDEN_HELMET) {
            helpGUI.openPresidentHelp(player);
            return;
        }

        // Cabinet Help - Slot 23
        if (slot == 23 && clicked.getType() == Material.LECTERN) {
            helpGUI.openCabinetHelp(player);
            return;
        }

        // Orders Help - Slot 25
        if (slot == 25 && clicked.getType() == Material.WRITABLE_BOOK) {
            helpGUI.openOrdersHelp(player);
            return;
        }

        // Arena Help - Slot 29
        if (slot == 29 && clicked.getType() == Material.IRON_SWORD) {
            helpGUI.openArenaHelp(player);
            return;
        }

        // Treasury Help - Slot 31
        if (slot == 31 && clicked.getType() == Material.GOLD_BLOCK) {
            helpGUI.openTreasuryHelp(player);
            return;
        }

        // Recall Help - Slot 33
        if (slot == 33 && clicked.getType() == Material.REDSTONE_TORCH) {
            recallGUI.openRecallMenu(player);
            return;
        }

        // Commands - Slot 40
        if (slot == 40 && clicked.getType() == Material.COMMAND_BLOCK) {
            player.closeInventory();
            showCommandList(player);
        }
    }

    // === HELP SUB MENU GUI ===

    private void handleHelpSubMenuGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            helpGUI.openHelpMenu(player);
        }
    }

    // === RECALL MENU GUI ===

    private void handleRecallMenuGUI(Player player, ItemStack clicked, int slot) {
        // Close button
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // Back to Main Menu
        if (clicked.getType() == Material.ARROW) {
            mainMenuGUI.openMainMenu(player);
            return;
        }

        // Refresh
        if (clicked.getType() == Material.CLOCK) {
            recallGUI.openRecallMenu(player);
            return;
        }

        RecallPetition petition = plugin.getDataManager().getRecallPetition();
        boolean hasActivePetition = petition != null
                && petition.getPhase() != RecallPetition.RecallPhase.COMPLETED
                && petition.getPhase() != RecallPetition.RecallPhase.FAILED;

        // Start Petition Button (Slot 22 when no active petition)
        if (slot == 22 && !hasActivePetition) {
            if (clicked.getType() == Material.REDSTONE_BLOCK) {
                // Open confirmation GUI
                recallGUI.openConfirmPetition(player);
                return;
            }
        }

        if (!hasActivePetition)
            return;

        // COLLECTING PHASE actions
        if (petition.getPhase() == RecallPetition.RecallPhase.COLLECTING) {
            // Sign Petition (Slot 38 - LIME_CONCRETE)
            if (slot == 38 && clicked.getType() == Material.LIME_CONCRETE) {
                player.closeInventory();
                plugin.getRecallManager().signPetition(player.getUniqueId());
                // Re-open after a tick to see updated data
                Bukkit.getScheduler().runTaskLater(plugin, () -> recallGUI.openRecallMenu(player), 5L);
                return;
            }

            // Withdraw Signature (Slot 38 - ORANGE_CONCRETE)
            if (slot == 38 && clicked.getType() == Material.ORANGE_CONCRETE) {
                player.closeInventory();
                plugin.getRecallManager().withdrawSignature(player.getUniqueId());
                Bukkit.getScheduler().runTaskLater(plugin, () -> recallGUI.openRecallMenu(player), 5L);
                return;
            }

            // View Status (Slot 40 - PAPER)
            if (slot == 40 && clicked.getType() == Material.PAPER) {
                player.closeInventory();
                showRecallInfo(player);
                return;
            }
        }

        // VOTING PHASE actions
        if (petition.getPhase() == RecallPetition.RecallPhase.VOTING) {
            // Vote REMOVE (Slot 38 - RED_CONCRETE)
            if (slot == 38 && clicked.getType() == Material.RED_CONCRETE) {
                player.closeInventory();
                plugin.getRecallManager().castRecallVote(player.getUniqueId(), true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> recallGUI.openRecallMenu(player), 5L);
                return;
            }

            // Vote KEEP (Slot 42 - LIME_CONCRETE)
            if (slot == 42 && clicked.getType() == Material.LIME_CONCRETE) {
                player.closeInventory();
                plugin.getRecallManager().castRecallVote(player.getUniqueId(), false);
                Bukkit.getScheduler().runTaskLater(plugin, () -> recallGUI.openRecallMenu(player), 5L);
                return;
            }
        }
    }

    // === RECALL CONFIRM GUI ===

    private void handleRecallConfirmGUI(Player player, ItemStack clicked) {
        // Confirm - start petition
        if (clicked.getType() == Material.LIME_CONCRETE) {
            player.closeInventory();
            boolean success = plugin.getRecallManager().startPetition(
                    player.getUniqueId(), "Player-initiated recall");
            if (success) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> recallGUI.openRecallMenu(player), 10L);
            } else {
                MessageUtils.send(player, "<red>Cannot start recall petition! Check requirements.");
                Bukkit.getScheduler().runTaskLater(plugin, () -> recallGUI.openRecallMenu(player), 10L);
            }
            return;
        }

        // Cancel - back to recall menu
        if (clicked.getType() == Material.RED_CONCRETE) {
            recallGUI.openRecallMenu(player);
            return;
        }
    }

    // === RECALL VOTE GUI (unused placeholder for future expansion) ===

    private void handleRecallVoteGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            recallGUI.openRecallMenu(player);
        }
    }

    // === HELPER METHODS ===

    private void showArenaInfo(Player player) {
        player.closeInventory();

        boolean isActive = plugin.getArenaManager().isArenaActive();

        MessageUtils.send(player, "<gold>═══════════════════════════════════════");
        MessageUtils.send(player, "<yellow>     ⚔ PRESIDENTIAL ARENA ⚔");
        MessageUtils.send(player, "<gold>═══════════════════════════════════════");

        if (isActive) {
            MessageUtils.send(player, "<green>Sesi arena sedang AKTIF!");
            MessageUtils.send(player, "<gray>Gunakan <white>/dc arena join <gray>untuk bergabung");
        } else {
            MessageUtils.send(player, "<red>Sesi arena tidak aktif.");
            MessageUtils.send(player, "<gray>Menunggu Presiden memulai sesi baru");
        }

        MessageUtils.send(player, "");
        MessageUtils.send(player, "<yellow>Commands:");
        MessageUtils.send(player, "<white>/dc arena join <gray>- Gabung arena");
        MessageUtils.send(player, "<white>/dc arena leave <gray>- Keluar arena");
        MessageUtils.send(player, "<white>/dc arena stats <gray>- Lihat statistik");
        MessageUtils.send(player, "<white>/dc arena leaderboard <gray>- Leaderboard");
        MessageUtils.send(player, "<gold>═══════════════════════════════════════");
    }

    private void showRecallInfo(Player player) {
        player.closeInventory();

        var petition = plugin.getDataManager().getRecallPetition();
        boolean isActive = petition != null &&
                petition.getPhase() != id.democracycore.models.RecallPetition.RecallPhase.COMPLETED &&
                petition.getPhase() != id.democracycore.models.RecallPetition.RecallPhase.FAILED;

        MessageUtils.send(player, "<gold>═══════════════════════════════════════");
        MessageUtils.send(player, "<yellow>     ⚠ SISTEM RECALL ⚠");
        MessageUtils.send(player, "<gold>═══════════════════════════════════════");

        if (isActive) {
            MessageUtils.send(player, "<red>Petisi recall sedang AKTIF!");
            MessageUtils.send(player, "<gray>Fase: <white>" + petition.getPhase().name());
            MessageUtils.send(player, "<gray>Tanda tangan: <white>" + petition.getSignatureCount());
        } else {
            MessageUtils.send(player, "<gray>Tidak ada petisi recall aktif.");
        }

        MessageUtils.send(player, "");
        MessageUtils.send(player, "<yellow>Commands:");
        MessageUtils.send(player, "<white>/dc recall start <gray>- Mulai petisi (50k deposit)");
        MessageUtils.send(player, "<white>/dc recall sign <gray>- Tanda tangani petisi");
        MessageUtils.send(player, "<white>/dc recall vote <yes/no> <gray>- Vote recall");
        MessageUtils.send(player, "<white>/dc recall status <gray>- Lihat status");
        MessageUtils.send(player, "<gold>═══════════════════════════════════════");
    }

    private void showCommandList(Player player) {
        MessageUtils.send(player, "<gold>═══════════════════════════════════════");
        MessageUtils.send(player, "<yellow>     💻 DAFTAR COMMAND 💻");
        MessageUtils.send(player, "<gold>═══════════════════════════════════════");
        MessageUtils.send(player, "<aqua>Menu & Info:");
        MessageUtils.send(player, "<white>/dc menu <gray>- Buka menu utama");
        MessageUtils.send(player, "<white>/dc info <gray>- Info pemerintahan");
        MessageUtils.send(player, "<white>/dc help <gray>- Bantuan command");
        MessageUtils.send(player, "");
        MessageUtils.send(player, "<aqua>Pemilu:");
        MessageUtils.send(player, "<white>/dc election <gray>- Status pemilu");
        MessageUtils.send(player, "<white>/dc candidates <gray>- Daftar kandidat");
        MessageUtils.send(player, "<white>/dc vote <player> <gray>- Vote kandidat");
        MessageUtils.send(player, "<white>/dc register <gray>- Daftar kandidat");
        MessageUtils.send(player, "<white>/dc endorse <player> <gray>- Endorse kandidat");
        MessageUtils.send(player, "");
        MessageUtils.send(player, "<aqua>Lainnya:");
        MessageUtils.send(player, "<white>/dc treasury <gray>- Info treasury");
        MessageUtils.send(player, "<white>/dc rate <1-5> <gray>- Rate presiden");
        MessageUtils.send(player, "<white>/dc stats [player] <gray>- Statistik");
        MessageUtils.send(player, "<white>/dc history <gray>- Sejarah presiden");
        MessageUtils.send(player, "<gold>═══════════════════════════════════════");
    }

    // === PUBLIC METHODS TO OPEN GUIS ===

    public void openMainMenu(Player player) {
        mainMenuGUI.openMainMenu(player);
    }

    public void openVotingGUI(Player player) {
        votingGUI.openVotingMenu(player);
    }

    public void openGovernmentGUI(Player player) {
        governmentGUI.openGovernmentMenu(player);
    }

    public void openOrdersGUI(Player player) {
        governmentGUI.openExecutiveOrdersMenu(player);
    }

    public void openCabinetGUI(Player player) {
        governmentGUI.openCabinetMenu(player);
    }

    public void openTreasuryGUI(Player player) {
        governmentGUI.openTreasuryMenu(player);
    }

    public void openHistoryGUI(Player player) {
        governmentGUI.openHistoryMenu(player);
    }

    public void openPlayerStatsGUI(Player player, UUID targetUUID) {
        viewingPlayerStats.put(player.getUniqueId(), targetUUID);
        playerStatsGUI.openPlayerStats(player, targetUUID);
    }

    public void openLeaderboardGUI(Player player) {
        playerStatsGUI.openLeaderboard(player);
    }

    public void openHelpGUI(Player player) {
        helpGUI.openHelpMenu(player);
    }

    public void openQuickActionsGUI(Player player) {
        mainMenuGUI.openQuickActionsMenu(player);
    }

    public void openRecallGUI(Player player) {
        recallGUI.openRecallMenu(player);
    }

}
