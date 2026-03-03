package id.democracycore.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import id.democracycore.DemocracyCore;
import id.democracycore.managers.ArenaManager;
import id.democracycore.models.CabinetDecision;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.Government;
import id.democracycore.models.RecallPetition;
import id.democracycore.utils.MessageUtils;

public class GUIListener implements Listener {

    private final DemocracyCore plugin;
    private final VotingGUI votingGUI;
    private final GovernmentGUI governmentGUI;
    private final CabinetGUI cabinetGUI;
    private final MainMenuGUI mainMenuGUI;
    private final PlayerStatsGUI playerStatsGUI;
    private final HelpGUI helpGUI;
    private final RecallGUI recallGUI;
    private final TaxGUI taxGUI;
    private final PresidentHistoryGUI presidentHistoryGUI;
    private final ArenaGUI arenaGUI;

    // Track which candidate a player is viewing
    private final Map<UUID, UUID> viewingCandidate = new HashMap<>();
    // Track which cabinet position a player is viewing
    private final Map<UUID, CabinetDecision.CabinetPosition> viewingCabinetPosition = new HashMap<>();
    // Track which cabinet position a player is selecting for appointment
    private final Map<UUID, Government.CabinetPosition> viewingAppointPosition = new HashMap<>();
    // Track which player stats a player is viewing
    private final Map<UUID, UUID> viewingPlayerStats = new HashMap<>();

    public GUIListener(DemocracyCore plugin) {
        this.plugin = plugin;
        this.votingGUI = new VotingGUI(plugin);
        this.governmentGUI = new GovernmentGUI(plugin);
        this.cabinetGUI = new CabinetGUI(plugin);
        this.mainMenuGUI = new MainMenuGUI(plugin);
        this.playerStatsGUI = new PlayerStatsGUI(plugin);
        this.helpGUI = new HelpGUI(plugin);
        this.recallGUI = new RecallGUI(plugin);
        this.taxGUI = new TaxGUI(plugin);
        this.presidentHistoryGUI = new PresidentHistoryGUI(plugin);
        this.arenaGUI = new ArenaGUI(plugin);
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

        } else if (title.equals(VotingGUI.VOTING_GUI_TITLE)) {
            event.setCancelled(true);
            handleVotingGUI(player, clicked);
        } else if (title.startsWith(VotingGUI.CANDIDATE_GUI_TITLE)) {
            event.setCancelled(true);
            handleCandidateGUI(player, clicked, title);
        } else if (title.equals(GovernmentGUI.GOVERNMENT_GUI_TITLE)) {
            event.setCancelled(true);
            handleGovernmentGUI(player, clicked);
        } else if (title.equals(GovernmentGUI.SALARY_GUI_TITLE)) {
            event.setCancelled(true);
            handleSalaryGUI(player, clicked);
        } else if (title.equals(GovernmentGUI.ORDERS_GUI_TITLE)) {
            event.setCancelled(true);
            handleOrdersGUI(player, clicked);
        } else if (title.equals(GovernmentGUI.CABINET_GUI_TITLE)) {
            event.setCancelled(true);
            handleCabinetGUI(player, clicked, event.getSlot(), event.getClick());
        } else if (title.startsWith(CabinetGUI.CABINET_APPOINT_TITLE)) {
            event.setCancelled(true);
            handleCabinetAppointGUI(player, clicked, title);
        } else if (title.equals(GovernmentGUI.CABINET_DECISIONS_TITLE)) {
            event.setCancelled(true);
            handleCabinetDecisionsGUI(player, clicked);
        } else if (title.contains("TREASURY") && !title.contains("LOGS")) {
            event.setCancelled(true);
            handleTreasuryGUI(player, clicked);
        } else if (title.equals("§6§l📜 TREASURY LOGS 📜")) {
            event.setCancelled(true);
            handleTreasuryTransactionsGUI(player, clicked);
        } else if (title.equals(PresidentHistoryGUI.HISTORY_TITLE)) {
            event.setCancelled(true);
            handleHistoryGUI(player, clicked, event.getSlot(), false);
        } else if (title.equals(PresidentHistoryGUI.DETAIL_TITLE)) {
            event.setCancelled(true);
            handleHistoryGUI(player, clicked, event.getSlot(), true);
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
        } else if (title.equals(TaxGUI.TAX_MENU_TITLE)) {
            event.setCancelled(true);
            handleTaxMenuGUI(player, clicked, event.getSlot());
        } else if (title.equals(TaxGUI.TAX_HISTORY_TITLE)) {
            event.setCancelled(true);
            handleTaxHistoryGUI(player, clicked);
        } else if (title.equals(TaxGUI.TAX_DEBTORS_TITLE)) {
            event.setCancelled(true);
            handleTaxDebtorsGUI(player, clicked);
        } else if (title.equals(ArenaGUI.ARENA_MENU_TITLE)) {
            event.setCancelled(true);
            handleArenaGUI(player, clicked, event.getSlot());
        } else if (title.equals(ArenaGUI.ARENA_LEADERBOARD_TITLE)) {
            event.setCancelled(true);
            handleArenaLeaderboardGUI(player, clicked);
        } else if (title.equals(ArenaGUI.ARENA_KIT_TITLE)) {
            event.setCancelled(true);
            handleArenaKitGUI(player, clicked);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();

        // Cancel drag in all our GUIs
        if (title.equals(MainMenuGUI.MAIN_MENU_TITLE) ||

                title.equals(VotingGUI.VOTING_GUI_TITLE) ||
                title.startsWith(VotingGUI.CANDIDATE_GUI_TITLE) ||
                title.equals(GovernmentGUI.GOVERNMENT_GUI_TITLE) ||
                title.equals(GovernmentGUI.SALARY_GUI_TITLE) ||
                title.equals(GovernmentGUI.ORDERS_GUI_TITLE) ||
                title.equals(GovernmentGUI.CABINET_GUI_TITLE) ||
                title.startsWith(CabinetGUI.CABINET_APPOINT_TITLE) ||
                title.equals(GovernmentGUI.CABINET_DECISIONS_TITLE) ||
                title.contains("TREASURY") ||
                title.equals(PresidentHistoryGUI.HISTORY_TITLE) ||
                title.equals(PresidentHistoryGUI.DETAIL_TITLE) ||
                title.equals(PlayerStatsGUI.STATS_GUI_TITLE) ||
                title.equals(PlayerStatsGUI.LEADERBOARD_TITLE) ||
                title.equals(HelpGUI.HELP_MENU_TITLE) ||
                title.contains("PANDUAN") ||
                title.equals(RecallGUI.RECALL_MENU_TITLE) ||
                title.equals(RecallGUI.RECALL_CONFIRM_TITLE) ||
                title.equals(RecallGUI.RECALL_VOTE_TITLE) ||
                title.equals(TaxGUI.TAX_MENU_TITLE) ||
                title.equals(TaxGUI.TAX_HISTORY_TITLE) ||
                title.equals(TaxGUI.TAX_DEBTORS_TITLE) ||
                title.equals(ArenaGUI.ARENA_MENU_TITLE) ||
                title.equals(ArenaGUI.ARENA_LEADERBOARD_TITLE) ||
                title.equals(ArenaGUI.ARENA_KIT_TITLE)) {
            event.setCancelled(true);
        }
    }

    // === MAIN MENU GUI ===

    /**
     * Checks if the item has a configured console command action and executes it.
     * 
     * @return true if an action was executed, false otherwise.
     */
    private boolean handleConfiguredAction(Player player, String itemKey, String state) {
        // Check if main menu specific action is configured
        GUIAction action = mainMenuGUI.getGUIAction(itemKey, state);

        if (action == GUIAction.ACTION_CONSOLE_COMMAND) {
            String command = mainMenuGUI.getGUICommandValue(itemKey, state);
            if (command != null && !command.isEmpty()) {
                // Process placeholders
                command = mainMenuGUI.processPlaceholders(command, player);
                // Execute command
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                // Close inventory
                player.closeInventory();
                return true;
            }
        } else if (action == GUIAction.ACTION_REFRESH_GUI) {
            mainMenuGUI.openMainMenu(player); // Re-open to refresh
            return true;
        } else if (action == GUIAction.ACTION_CLOSE_INVENTORY) {
            player.closeInventory();
            return true;
        }

        return false;
    }

    private void handleMainMenuGUI(Player player, ItemStack clicked, int slot) {
        // Play click sound based on slot
        mainMenuGUI.playClickSound(player, slot);

        // Close button — read slot from config
        int closeSlot = mainMenuGUI.getItemSlot("close_button", 49);
        if (slot == closeSlot) {
            player.closeInventory();
            return;
        }

        // Player Head - Open Stats
        int playerHeadSlot = mainMenuGUI.getItemSlot("player_head", 4);
        if (slot == playerHeadSlot) {
            if (handleConfiguredAction(player, "player_head", null))
                return;
            viewingPlayerStats.put(player.getUniqueId(), player.getUniqueId());
            playerStatsGUI.openPlayerStats(player, player.getUniqueId());
            return;
        }

        // President Info
        int presidentSlot = mainMenuGUI.getItemSlot("president_item", 10);
        if (slot == presidentSlot) {
            Government gov = plugin.getDataManager().getGovernment();
            String state = gov.hasPresident() ? "default" : "no_president";
            if (handleConfiguredAction(player, "president_item", state))
                return;

            if (gov.hasPresident()) {
                governmentGUI.openGovernmentMenu(player);
            } else {
                votingGUI.openVotingMenu(player);
            }
            return;
        }

        // Cabinet
        int cabinetSlot = mainMenuGUI.getItemSlot("cabinet_item", 12);
        if (slot == cabinetSlot) {
            if (handleConfiguredAction(player, "cabinet_item", null))
                return;
            cabinetGUI.openCabinetMenu(player);
            return;
        }

        // Treasury
        int treasurySlot = mainMenuGUI.getItemSlot("treasury_item", 14);
        if (slot == treasurySlot) {
            if (handleConfiguredAction(player, "treasury_item", null))
                return;
            governmentGUI.openTreasuryMenu(player);
            return;
        }

        // Active Effects
        int effectsSlot = mainMenuGUI.getItemSlot("active_effects_item", 16);
        if (slot == effectsSlot) {
            int total = plugin.getExecutiveOrderManager().getActiveOrders().size() +
                    plugin.getDataManager().getActiveDecisions().size();
            String state = total > 0 ? "active" : "inactive";
            if (handleConfiguredAction(player, "active_effects_item", state))
                return;

            governmentGUI.openExecutiveOrdersMenu(player);
            return;
        }

        // Election
        int electionSlot = mainMenuGUI.getItemSlot("election_item", 19);
        if (slot == electionSlot) {
            if (handleConfiguredAction(player, "election_item", null))
                return;
            votingGUI.openVotingMenu(player);
            return;
        }

        // Executive Orders
        int ordersSlot = mainMenuGUI.getItemSlot("executive_orders_item", 21);
        if (slot == ordersSlot) {
            if (handleConfiguredAction(player, "executive_orders_item", null))
                return;
            governmentGUI.openExecutiveOrdersMenu(player);
            return;
        }

        // Tax
        int taxSlot = mainMenuGUI.getItemSlot("tax_item", 22);
        if (slot == taxSlot) {
            if (handleConfiguredAction(player, "tax_item", null))
                return;
            taxGUI.openTaxMenu(player);
            return;
        }

        // Arena
        int arenaSlot = mainMenuGUI.getItemSlot("arena_item", 23);
        if (slot == arenaSlot) {
            boolean isActive = plugin.getArenaManager().isArenaActive();
            String state = isActive ? "active" : "inactive";
            if (handleConfiguredAction(player, "arena_item", state))
                return;

            // Open the dedicated Arena Management GUI
            arenaGUI.openArenaMenu(player);
            return;
        }

        // Recall
        int recallSlot = mainMenuGUI.getItemSlot("recall_item", 25);
        if (slot == recallSlot) {
            RecallPetition petition = plugin.getDataManager().getRecallPetition();
            boolean isActive = petition != null &&
                    petition.getPhase() != RecallPetition.RecallPhase.COMPLETED &&
                    petition.getPhase() != RecallPetition.RecallPhase.FAILED;
            String state = isActive ? "active" : "inactive";
            if (handleConfiguredAction(player, "recall_item", state))
                return;

            recallGUI.openRecallMenu(player);
            return;
        }

        // History
        int historySlot = mainMenuGUI.getItemSlot("history_item", 28);
        if (slot == historySlot) {
            if (handleConfiguredAction(player, "history_item", null))
                return;
            presidentHistoryGUI.openHistoryMenu(player);
            return;
        }

        // My Stats
        int statsSlot = mainMenuGUI.getItemSlot("my_stats_item", 30);
        if (slot == statsSlot) {
            if (handleConfiguredAction(player, "my_stats_item", null))
                return;
            viewingPlayerStats.put(player.getUniqueId(), player.getUniqueId());
            playerStatsGUI.openPlayerStats(player, player.getUniqueId());
            return;
        }

        // Leaderboard
        int leaderboardSlot = mainMenuGUI.getItemSlot("leaderboard_item", 32);
        if (slot == leaderboardSlot) {
            if (handleConfiguredAction(player, "leaderboard_item", null))
                return;
            playerStatsGUI.openLeaderboard(player);
            return;
        }

        // Help
        int helpSlot = mainMenuGUI.getItemSlot("help_item", 34);
        if (slot == helpSlot) {
            if (handleConfiguredAction(player, "help_item", null))
                return;
            helpGUI.openHelpMenu(player);
            return;
        }

        // Quick Actions - Register
        int registerSlot = mainMenuGUI.getItemSlot("register_candidate", 38);
        if (slot == registerSlot) {
            if (handleConfiguredAction(player, "register_candidate", null))
                return;
            player.closeInventory();
            plugin.getElectionManager().registerCandidate(player, "");
            return;
        }

        // Quick Actions - Vote
        int voteSlot = mainMenuGUI.getItemSlot("vote_now", 40);
        if (slot == voteSlot) {
            if (handleConfiguredAction(player, "vote_now", null))
                return;
            votingGUI.openVotingMenu(player);
            return;
        }

        // Quick Actions - Rate
        int rateSlot = mainMenuGUI.getItemSlot("rate_president", 42);
        if (slot == rateSlot) {
            if (handleConfiguredAction(player, "rate_president", null))
                return;
            player.closeInventory();
            MessageUtils.send(player, "<yellow>Gunakan: <white>/dc rate <1-5> <gray>untuk memberi rating presiden");
            return;
        }

        // ============================================================
        // GENERIC CONFIG-DRIVEN ITEM HANDLER
        // Handles any item added to gui.yml that is NOT in the hardcoded
        // list. Reads on_click from config and dispatches accordingly.
        // No Java code required for new custom items!
        // ============================================================
        String genericKey = mainMenuGUI.getGenericItemKeyForSlot(slot);
        if (genericKey != null) {
            // First, try ACTION_CONSOLE_COMMAND or other special actions
            if (handleConfiguredAction(player, genericKey, null))
                return;

            // Otherwise dispatch the on_click action from gui.yml
            GUIAction action = mainMenuGUI.getGUIAction(genericKey);
            dispatchGUIAction(player, action);
        }
    }

    // === GENERIC ACTION DISPATCHER ===

    /**
     * Dispatches a GUIAction to the appropriate GUI or command.
     * Used by the generic config item handler so that on_click values
     * in gui.yml work automatically without dedicated Java per item.
     */
    private void dispatchGUIAction(Player player, GUIAction action) {
        if (action == null || action == GUIAction.UNKNOWN)
            return;

        switch (action) {
            case OPEN_GUI_MAIN_MENU -> mainMenuGUI.openMainMenu(player);
            case OPEN_GUI_PLAYER_STATS -> {
                viewingPlayerStats.put(player.getUniqueId(), player.getUniqueId());
                playerStatsGUI.openPlayerStats(player, player.getUniqueId());
            }
            case OPEN_GUI_GOVERNMENT -> governmentGUI.openGovernmentMenu(player);
            case OPEN_GUI_VOTING -> votingGUI.openVotingMenu(player);
            case OPEN_GUI_CABINET -> cabinetGUI.openCabinetMenu(player);
            case OPEN_GUI_TREASURY -> governmentGUI.openTreasuryMenu(player);
            case OPEN_GUI_EXECUTIVE_ORDERS -> governmentGUI.openExecutiveOrdersMenu(player);
            case OPEN_GUI_RECALL -> recallGUI.openRecallMenu(player);
            case OPEN_GUI_HISTORY -> presidentHistoryGUI.openHistoryMenu(player);
            case OPEN_GUI_LEADERBOARD -> playerStatsGUI.openLeaderboard(player);
            case OPEN_GUI_HELP -> helpGUI.openHelpMenu(player);
            case OPEN_GUI_TAX -> taxGUI.openTaxMenu(player);
            case OPEN_GUI_ARENA -> arenaGUI.openArenaMenu(player);
            case OPEN_GUI_ARENA_LEADERBOARD -> arenaGUI.openLeaderboard(player);
            case OPEN_GUI_ARENA_KIT -> arenaGUI.openKitInfo(player);
            case OPEN_GUI_CABINET_APPOINT -> {
                Government gov = plugin.getDataManager().getGovernment();
                boolean isPresident = gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId());
                boolean isAdmin = player.hasPermission("democracy.admin");
                if (isPresident || isAdmin) {
                    cabinetGUI.openCabinetMenu(player);
                } else {
                    MessageUtils.send(player, "<red>Only the President can access cabinet appointments.");
                }
            }
            case ACTION_CLOSE_INVENTORY -> player.closeInventory();
            case ACTION_REFRESH_GUI -> mainMenuGUI.openMainMenu(player);
            case ACTION_RATE_PRESIDENT -> {
                player.closeInventory();
                MessageUtils.send(player, "<yellow>Use: <white>/dc rate <1-5> <gray>to rate the president");
            }
            case ACTION_DONATE_TREASURY -> {
                player.closeInventory();
                MessageUtils.send(player, "<yellow>Use: <white>/dc treasury donate <amount>");
            }
            case ACTION_JOIN_ARENA -> {
                player.closeInventory();
                plugin.getArenaManager().joinArena(player);
            }
            case ACTION_ARENA_START -> {
                Government gov = plugin.getDataManager().getGovernment();
                if (gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId())) {
                    player.closeInventory();
                    boolean started = plugin.getArenaManager().startArena(player.getUniqueId());
                    if (!started) {
                        MessageUtils.send(player, "<red>Cannot start arena at this time.");
                    }
                } else {
                    MessageUtils.send(player, "<red>Only the President can start the arena.");
                }
            }
            case ACTION_ARENA_END -> {
                Government gov = plugin.getDataManager().getGovernment();
                if (gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId())) {
                    player.closeInventory();
                    plugin.getArenaManager().endArena();
                } else {
                    MessageUtils.send(player, "<red>Only the President can end the arena.");
                }
            }
            case ACTION_ARENA_LEAVE -> {
                player.closeInventory();
                plugin.getArenaManager().leaveArena(player.getUniqueId());
            }
            case ACTION_REGISTER_CANDIDATE -> {
                player.closeInventory();
                plugin.getElectionManager().registerCandidate(player, "");
            }
            default -> {
                /* UNKNOWN / context-specific actions — do nothing */ }
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
            cabinetGUI.openCabinetMenu(player);
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
            presidentHistoryGUI.openHistoryMenu(player);
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

        if (clicked.getType() == Material.NETHER_STAR) {
            // Presidential Game
            Government gov = plugin.getDataManager().getGovernment();
            if (gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId())) {
                player.closeInventory();
                boolean started = plugin.getArenaManager().startArena(player.getUniqueId());
                if (!started) {
                    MessageUtils.send(player,
                            "<red>Cannot start Presidential Games at this time. (Check games limit or treasury balance)");
                }
            } else {
                MessageUtils.send(player, "<red>Only the President can start the Arena Games.");
            }
            return;
        }

        if (clicked.getType() == Material.BELL) {
            // Broadcast Message
            Government gov = plugin.getDataManager().getGovernment();
            if (gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId())) {
                long cooldown = 8L * 60 * 60 * 1000;
                long nextAvailable = gov.getLastBroadcastTime() + cooldown;
                if (System.currentTimeMillis() >= nextAvailable) {
                    player.closeInventory();
                    id.democracycore.listeners.ChatListener.pendingBroadcasts.add(player.getUniqueId());
                    MessageUtils.send(player, "government.broadcast_type_prompt");
                } else {
                    MessageUtils.send(player, "government.broadcast_cooldown");
                }
            } else {
                MessageUtils.send(player, "government.broadcast_not_president");
            }
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

    private void handleCabinetGUI(Player player, ItemStack clicked, int slot, ClickType clickType) {
        // Close
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // Back to Government
        if (clicked.getType() == Material.ARROW) {
            governmentGUI.openGovernmentMenu(player);
            return;
        }

        Government gov = plugin.getDataManager().getGovernment();
        boolean isPresident = gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId());
        boolean isAdmin = player.hasPermission("democracy.admin");
        boolean canAppoint = isPresident || isAdmin;

        // Minister position slots
        Government.CabinetPosition posAtSlot = cabinetGUI.getPositionForSlot(slot);
        if (posAtSlot != null) {
            // Right-click or if position is vacant and player can appoint → open appoint
            // menu
            boolean isVacant = gov.getCabinetMember(posAtSlot) == null;
            if (canAppoint && (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT || isVacant)) {
                viewingAppointPosition.put(player.getUniqueId(), posAtSlot);
                cabinetGUI.openAppointMenu(player, posAtSlot);
                return;
            }
            // Left-click filled slot → open decisions
            if (!isVacant) {
                CabinetDecision.CabinetPosition decPos = CabinetDecision.CabinetPosition.valueOf(posAtSlot.name());
                viewingCabinetPosition.put(player.getUniqueId(), decPos);
                cabinetGUI.openCabinetDecisionsMenu(player, decPos);
            }
            return;
        }

        // Active decisions summary (Slot 29 - BEACON/GLASS)
        if (slot == 29 && (clicked.getType() == Material.BEACON)) {
            java.util.List<CabinetDecision> activeDecisions = plugin.getDataManager().getActiveDecisions();
            if (!activeDecisions.isEmpty()) {
                CabinetDecision first = activeDecisions.get(0);
                viewingCabinetPosition.put(player.getUniqueId(), first.getMinisterPosition());
                cabinetGUI.openCabinetDecisionsMenu(player, first.getMinisterPosition());
            }
            return;
        }

        // "My position" button (Slot 33 - DIAMOND)
        if (clicked.getType() == Material.DIAMOND) {
            CabinetDecision.CabinetPosition[] positions = CabinetDecision.CabinetPosition.values();
            for (CabinetDecision.CabinetPosition pos : positions) {
                UUID minister = gov.getCabinetMember(Government.CabinetPosition.valueOf(pos.name()));
                if (minister != null && minister.equals(player.getUniqueId())) {
                    viewingCabinetPosition.put(player.getUniqueId(), pos);
                    cabinetGUI.openCabinetDecisionsMenu(player, pos);
                    return;
                }
            }
        }
    }

    // === CABINET DECISIONS GUI ===

    private void handleCabinetDecisionsGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        if (clicked.getType() == Material.ARROW) {
            cabinetGUI.openCabinetMenu(player);
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

    // === CABINET APPOINTMENT GUI ===

    /**
     * Handles clicks inside the "Appoint Minister" player-picker GUI.
     * Only accessible by the President (or admin).
     */
    private void handleCabinetAppointGUI(Player player, ItemStack clicked, String title) {
        // Close
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // Back to cabinet
        if (clicked.getType() == Material.ARROW) {
            cabinetGUI.openCabinetMenu(player);
            return;
        }

        // Recover which position we are appointing for
        Government.CabinetPosition targetPos = viewingAppointPosition.get(player.getUniqueId());
        if (targetPos == null) {
            cabinetGUI.openCabinetMenu(player);
            return;
        }

        Government gov = plugin.getDataManager().getGovernment();
        boolean isPresident = gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId());
        boolean isAdmin = player.hasPermission("democracy.admin");

        if (!isPresident && !isAdmin) {
            MessageUtils.send(player, "<red>Only the President can appoint cabinet members!");
            cabinetGUI.openCabinetMenu(player);
            return;
        }

        // "Remove current minister" button (RED_CONCRETE at slot 45)
        if (clicked.getType() == Material.RED_CONCRETE) {
            plugin.getGovernmentManager().removeCabinetMember(targetPos);
            MessageUtils.send(player,
                    "<gold>The <yellow>" + targetPos.getDisplayName() + "<gold> position is now vacant.");
            Bukkit.getScheduler().runTaskLater(plugin, () -> cabinetGUI.openCabinetMenu(player), 3L);
            return;
        }

        // Player head click → appoint that player
        if (clicked.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clicked.getItemMeta();
            if (meta == null || meta.getOwningPlayer() == null)
                return;

            UUID targetUUID = meta.getOwningPlayer().getUniqueId();
            UUID currentMinister = gov.getCabinetMember(targetPos);

            if (targetUUID.equals(currentMinister)) {
                // Clicking the current minister removes them
                plugin.getGovernmentManager().removeCabinetMember(targetPos);
                MessageUtils.send(player, "<gold>Removed <yellow>" + meta.getOwningPlayer().getName()
                        + "<gold> from <yellow>" + targetPos.getDisplayName() + "<gold>.");
            } else {
                // Appoint the clicked player
                plugin.getGovernmentManager().appointCabinetMember(targetPos, targetUUID);
                MessageUtils.send(player, "<gold>Appointed <yellow>" + meta.getOwningPlayer().getName()
                        + "<gold> as <yellow>" + targetPos.getDisplayName() + "<gold>!");
            }

            // Re-open cabinet menu after a tick so data is refreshed
            Bukkit.getScheduler().runTaskLater(plugin, () -> cabinetGUI.openCabinetMenu(player), 3L);
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

    private void handleHistoryGUI(Player player, ItemStack clicked, int slot, boolean isDetailView) {
        // ── Back / Close ──────────────────────────────────────────────────
        if (clicked.getType() == Material.ARROW) {
            if (isDetailView) {
                presidentHistoryGUI.openHistoryMenu(player);
            } else {
                governmentGUI.openGovernmentMenu(player);
            }
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        if (!isDetailView) {
            // ── Main list: click a president head → open detail view ──────
            if (clicked.getType() == Material.PLAYER_HEAD) {
                SkullMeta meta = (SkullMeta) clicked.getItemMeta();
                if (meta != null && meta.getOwningPlayer() != null) {
                    UUID targetUUID = meta.getOwningPlayer().getUniqueId();
                    java.util.List<id.democracycore.models.PresidentHistory.PresidentRecord> history = plugin
                            .getDataManager().getAllPresidentHistory();
                    for (int i = 0; i < history.size(); i++) {
                        if (history.get(i).getPlayerId().equals(targetUUID)) {
                            presidentHistoryGUI.openDetailMenu(player, i);
                            return;
                        }
                    }
                }
            }
        } else {
            // ── Detail view: Prev (slot 36) / Next (slot 44) nav heads ────
            if (clicked.getType() == Material.PLAYER_HEAD) {
                SkullMeta meta = (SkullMeta) clicked.getItemMeta();
                if (meta != null && meta.getOwningPlayer() != null) {
                    UUID targetUUID = meta.getOwningPlayer().getUniqueId();
                    java.util.List<id.democracycore.models.PresidentHistory.PresidentRecord> history = plugin
                            .getDataManager().getAllPresidentHistory();
                    for (int i = 0; i < history.size(); i++) {
                        if (history.get(i).getPlayerId().equals(targetUUID)) {
                            presidentHistoryGUI.openDetailMenu(player, i);
                            return;
                        }
                    }
                }
            }
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

    // === TAX MENU GUI ===

    private void handleTaxMenuGUI(Player player, ItemStack clicked, int slot) {
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
            taxGUI.openTaxMenu(player);
            return;
        }

        // Pay Debt (Slot 30 - EMERALD_BLOCK)
        if (slot == 30 && clicked.getType() == Material.EMERALD_BLOCK) {
            player.closeInventory();
            plugin.getTaxManager().payDebt(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> taxGUI.openTaxMenu(player), 10L);
            return;
        }

        // Tax History (Slot 24 - BOOK)
        if (slot == 24 && clicked.getType() == Material.BOOK) {
            taxGUI.openTaxHistory(player);
            return;
        }

        // Debtor List (Slot 32 - SKELETON_SKULL)
        if (slot == 32 && clicked.getType() == Material.SKELETON_SKULL) {
            taxGUI.openDebtorList(player);
            return;
        }
    }

    private void handleTaxHistoryGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            taxGUI.openTaxMenu(player);
        }
    }

    private void handleTaxDebtorsGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            taxGUI.openTaxMenu(player);
        }
    }

    // === ARENA GUI ===

    private void handleArenaGUI(Player player, ItemStack clicked, int slot) {
        // Close button (slot 53)
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // Back to main menu (slot 45)
        if (clicked.getType() == Material.ARROW) {
            mainMenuGUI.openMainMenu(player);
            return;
        }

        // Refresh (slot 49)
        if (slot == 49 && clicked.getType() == Material.CLOCK) {
            arenaGUI.openArenaMenu(player);
            return;
        }

        // Full Leaderboard button (slot 40)
        if (slot == 40 && clicked.getType() == Material.GOLD_BLOCK) {
            arenaGUI.openLeaderboard(player);
            return;
        }

        // Rewards info item (slot 16) — open leaderboard for context
        if (slot == 16 && clicked.getType() == Material.NETHER_STAR) {
            arenaGUI.openLeaderboard(player);
            return;
        }

        // Kit info (slot 25)
        if (slot == 25 && clicked.getType() == Material.IRON_CHESTPLATE) {
            arenaGUI.openKitInfo(player);
            return;
        }

        // Join Arena (slot 37) — green or red concrete
        if (slot == 37) {
            if (clicked.getType() == Material.LIME_CONCRETE) {
                // Join
                player.closeInventory();
                plugin.getArenaManager().joinArena(player);
            } else if (clicked.getType() == Material.RED_CONCRETE) {
                // Leave
                player.closeInventory();
                if (!plugin.getArenaManager().leaveArena(player.getUniqueId())) {
                    MessageUtils.send(player, "<red>You are not in the arena!");
                }
            }
            return;
        }

        // President controls (slot 43)
        if (slot == 43) {
            if (clicked.getType() == Material.BEACON) {
                // Start arena
                if (!plugin.getArenaManager().startArena(player.getUniqueId())) {
                    MessageUtils.send(player, "<red>Cannot start arena! Check requirements.");
                } else {
                    arenaGUI.openArenaMenu(player);
                }
            } else if (clicked.getType() == Material.TNT) {
                // End arena
                plugin.getArenaManager().endArena();
                MessageUtils.send(player, "<gold>Arena ended. Final rewards distributed.");
                arenaGUI.openArenaMenu(player);
            }
        }
    }

    private void handleArenaLeaderboardGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            arenaGUI.openArenaMenu(player);
            return;
        }
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
        }
    }

    private void handleArenaKitGUI(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            arenaGUI.openArenaMenu(player);
            return;
        }
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
        }
    }

    // === HELPER METHODS ===

    private void showArenaInfo(Player player) {
        // Redirect to the full Arena GUI
        arenaGUI.openArenaMenu(player);
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
        cabinetGUI.openCabinetMenu(player);
    }

    public void openTreasuryGUI(Player player) {
        governmentGUI.openTreasuryMenu(player);
    }

    public void openHistoryGUI(Player player) {
        presidentHistoryGUI.openHistoryMenu(player);
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

    public void openRecallGUI(Player player) {
        recallGUI.openRecallMenu(player);
    }

    public void openTaxGUI(Player player) {
        taxGUI.openTaxMenu(player);
    }

    public void openArenaGUI(Player player) {
        arenaGUI.openArenaMenu(player);
    }

    /**
     * Execute a GUI action generically based on GUIAction enum
     * 
     * @param player     The player executing the action
     * @param action     The GUIAction to execute
     * @param itemKey    The item key from gui.yml (for getting command value)
     * @param currentGUI The title of current GUI (for refresh action)
     * @param state      Optional state (for stateful items)
     */
    public void executeGUIAction(Player player, GUIAction action, String itemKey, String currentGUI, String state) {
        switch (action) {
            // GUI Opening Actions
            case OPEN_GUI_MAIN_MENU:
                mainMenuGUI.openMainMenu(player);
                break;
            case OPEN_GUI_PLAYER_STATS:
                viewingPlayerStats.put(player.getUniqueId(), player.getUniqueId());
                playerStatsGUI.openPlayerStats(player, player.getUniqueId());
                break;
            case OPEN_GUI_GOVERNMENT:
                governmentGUI.openGovernmentMenu(player);
                break;
            case OPEN_GUI_VOTING:
                votingGUI.openVotingMenu(player);
                break;
            case OPEN_GUI_CABINET:
                cabinetGUI.openCabinetMenu(player);
                break;
            case OPEN_GUI_TREASURY:
                governmentGUI.openTreasuryMenu(player);
                break;
            case OPEN_GUI_EXECUTIVE_ORDERS:
                governmentGUI.openExecutiveOrdersMenu(player);
                break;
            case OPEN_GUI_RECALL:
                recallGUI.openRecallMenu(player);
                break;
            case OPEN_GUI_HISTORY:
                presidentHistoryGUI.openHistoryMenu(player);
                break;
            case OPEN_GUI_LEADERBOARD:
                playerStatsGUI.openLeaderboard(player);
                break;
            case OPEN_GUI_HELP:
                helpGUI.openHelpMenu(player);
                break;
            case OPEN_GUI_TAX:
                taxGUI.openTaxMenu(player);
                break;
            case OPEN_GUI_ARENA:
                arenaGUI.openArenaMenu(player);
                break;
            case OPEN_GUI_ARENA_LEADERBOARD:
                arenaGUI.openLeaderboard(player);
                break;
            case OPEN_GUI_ARENA_KIT:
                arenaGUI.openKitInfo(player);
                break;
            case OPEN_GUI_CABINET_APPOINT:
                // Requires a position stored in viewingAppointPosition; falls back to cabinet
                // menu
                cabinetGUI.openCabinetMenu(player);
                break;

            // Specific Actions
            case ACTION_REGISTER_CANDIDATE:
                player.closeInventory();
                plugin.getElectionManager().registerCandidate(player, "");
                break;
            case ACTION_RATE_PRESIDENT:
                player.closeInventory();
                MessageUtils.send(player, "<yellow>Gunakan: <white>/dc rate <1-5> <gray>untuk memberi rating presiden");
                break;
            case ACTION_CLOSE_INVENTORY:
                player.closeInventory();
                break;
            case ACTION_ARENA_INFO:
                showArenaInfo(player);
                break;
            case ACTION_ENDORSE_CANDIDATE:
                player.closeInventory();
                MessageUtils.send(player, "<yellow>Gunakan: <white>/dc endorse <nama_kandidat>");
                break;
            case ACTION_DONATE_TREASURY:
                player.closeInventory();
                MessageUtils.send(player, "<yellow>Gunakan: <white>/dc treasury donate <jumlah>");
                break;
            case ACTION_JOIN_ARENA:
                player.closeInventory();
                plugin.getArenaManager().joinArena(player);
                break;
            case ACTION_ARENA_START:
                player.closeInventory();
                if (!plugin.getArenaManager().startArena(player.getUniqueId())) {
                    MessageUtils.send(player, "<red>Cannot start arena! Check requirements.");
                } else {
                    arenaGUI.openArenaMenu(player);
                }
                break;
            case ACTION_ARENA_END:
                player.closeInventory();
                plugin.getArenaManager().endArena();
                MessageUtils.send(player, "<gold>Arena ended. Final rewards distributed.");
                break;
            case ACTION_ARENA_LEAVE:
                player.closeInventory();
                if (!plugin.getArenaManager().leaveArena(player.getUniqueId())) {
                    MessageUtils.send(player, "<red>You are not in the arena!");
                }
                break;

            // New Actions
            case ACTION_REFRESH_GUI:
                // Refresh current GUI by reopening it
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (currentGUI.equals(MainMenuGUI.MAIN_MENU_TITLE)) {
                        mainMenuGUI.openMainMenu(player);

                    } else if (currentGUI.equals(VotingGUI.VOTING_GUI_TITLE)) {
                        votingGUI.openVotingMenu(player);
                    } else if (currentGUI.equals(GovernmentGUI.GOVERNMENT_GUI_TITLE)) {
                        governmentGUI.openGovernmentMenu(player);
                    } else if (currentGUI.equals(GovernmentGUI.CABINET_GUI_TITLE)) {
                        cabinetGUI.openCabinetMenu(player);
                    } else if (currentGUI.equals(RecallGUI.RECALL_MENU_TITLE)) {
                        recallGUI.openRecallMenu(player);
                    } else if (currentGUI.equals(PlayerStatsGUI.STATS_GUI_TITLE)) {
                        UUID targetUUID = viewingPlayerStats.getOrDefault(player.getUniqueId(), player.getUniqueId());
                        playerStatsGUI.openPlayerStats(player, targetUUID);
                    } else if (currentGUI.equals(PlayerStatsGUI.LEADERBOARD_TITLE)) {
                        playerStatsGUI.openLeaderboard(player);
                    } else if (currentGUI.equals(HelpGUI.HELP_MENU_TITLE)) {
                        helpGUI.openHelpMenu(player);
                    } else if (currentGUI.equals(TaxGUI.TAX_MENU_TITLE)) {
                        taxGUI.openTaxMenu(player);
                    }
                }, 1L);
                break;

            case ACTION_CONSOLE_COMMAND:
                // Get command from config
                String command = state != null && !state.isEmpty()
                        ? mainMenuGUI.getGUICommandValue(itemKey, state)
                        : mainMenuGUI.getGUICommandValue(itemKey);

                if (command != null && !command.isEmpty()) {
                    // Process placeholders
                    command = mainMenuGUI.processPlaceholders(command, player);

                    // Execute as console command
                    final String finalCommand = command;
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                    });

                    player.closeInventory();
                    MessageUtils.send(player, "<green>Command executed!");
                } else {
                    player.closeInventory();
                    MessageUtils.send(player, "<red>Error: No command configured for this action!");
                    plugin.getLogger()
                            .warning("ACTION_CONSOLE_COMMAND used but no 'value' field found for item: " + itemKey);
                }
                break;

            case UNKNOWN:
            default:
                plugin.getLogger().warning("Unknown GUI action: " + action);
                break;
        }
    }

    /**
     * Simplified executeGUIAction without state
     */
    public void executeGUIAction(Player player, GUIAction action, String itemKey, String currentGUI) {
        executeGUIAction(player, action, itemKey, currentGUI, null);
    }

}
