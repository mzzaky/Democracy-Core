package id.democracycore.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import id.democracycore.gui.GUIAction;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import id.democracycore.DemocracyCore;
import id.democracycore.models.CabinetDecision;
import id.democracycore.models.Election;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.Government;
import id.democracycore.models.PlayerData;
import id.democracycore.models.PresidentHistory;
import id.democracycore.models.RecallPetition;
import id.democracycore.models.Treasury;
import id.democracycore.utils.MessageUtils;

public class DemocracyCommand implements CommandExecutor, TabCompleter {

    private final DemocracyCore plugin;
    private final Map<UUID, Runnable> pendingConfirmations = new HashMap<>();

    public DemocracyCommand(DemocracyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "menu":
            case "gui":
                handleMainMenu(sender);
                break;
            case "help":
                showHelp(sender);
                break;
            case "info":
            case "status":
                showGovernmentInfo(sender);
                break;
            case "vote":
                handleVote(sender, args);
                break;
            case "register":
                handleRegister(sender, args);
                break;
            case "endorse":
                handleEndorse(sender, args);
                break;
            case "campaign":
                handleCampaign(sender, args);
                break;
            case "election":
                showElectionInfo(sender);
                break;
            case "candidates":
                showCandidates(sender);
                break;
            case "order":
            case "executive":
                handleExecutiveOrder(sender, args);
                break;
            case "cabinet":
                handleCabinetCommand(sender, args);
                break;
            case "treasury":
                handleTreasury(sender, args);
                break;
            case "arena":
                handleArena(sender, args);
                break;
            case "recall":
                handleRecall(sender, args);
                break;
            case "rate":
            case "approval":
                handleRating(sender, args);
                break;
            case "history":
                showHistory(sender, args);
                break;
            case "stats":
                showPlayerStats(sender, args);
                break;
            case "admin":
                handleAdmin(sender, args);
                break;
            case "government":
            case "gov":
                handleGovernmentGUI(sender);
                break;
            case "leaderboard":
            case "lb":
                handleLeaderboard(sender);
                break;
            case "tax":
                handleTax(sender, args);
                break;

            default:
                MessageUtils.send(sender, "general.unknown_command");
                break;
        }

        return true;
    }

    // === HELP ===

    // === MAIN MENU ===

    private void handleMainMenu(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        plugin.getGUIListener().openMainMenu(player);
    }

    // === HELP ===

    private void showHelp(CommandSender sender) {
        MessageUtils.send(sender, "help.header");
        MessageUtils.send(sender, "help.title");
        MessageUtils.send(sender, "help.footer");
        MessageUtils.send(sender, "help.commands.info");
        MessageUtils.send(sender, "help.commands.election");
        MessageUtils.send(sender, "help.commands.candidates");
        MessageUtils.send(sender, "help.commands.vote");
        MessageUtils.send(sender, "help.commands.register");
        MessageUtils.send(sender, "help.commands.endorse");
        MessageUtils.send(sender, "help.commands.campaign");
        MessageUtils.send(sender, "help.commands.treasury");
        MessageUtils.send(sender, "help.commands.rate");
        MessageUtils.send(sender, "help.commands.stats");
        MessageUtils.send(sender, "help.commands.history");

        if (sender.hasPermission("democracy.president")) {
            MessageUtils.send(sender, "help.president_section");
            MessageUtils.send(sender, "help.president_commands.order");
            MessageUtils.send(sender, "help.president_commands.arena_start");
            MessageUtils.send(sender, "help.president_commands.cabinet_appoint");
        }

        if (sender.hasPermission("democracy.cabinet")) {
            MessageUtils.send(sender, "help.cabinet_section");
            MessageUtils.send(sender, "help.cabinet_commands.decision");
        }

        if (sender.hasPermission("democracy.admin")) {
            MessageUtils.send(sender, "help.admin_section");
            MessageUtils.send(sender, "help.admin_commands.admin");
        }
    }

    // === GOVERNMENT INFO ===

    private void showGovernmentInfo(CommandSender sender) {
        Government gov = plugin.getDataManager().getGovernment();

        MessageUtils.send(sender, "government.header");
        MessageUtils.send(sender, "government.title");
        MessageUtils.send(sender, "government.footer");

        // President
        if (gov.hasPresident()) {
            String presName = Bukkit.getOfflinePlayer(gov.getPresidentUUID()).getName();
            MessageUtils.send(sender, "government.president", "name", presName);
            MessageUtils.send(sender, "government.president_term", "term", gov.getCurrentTerm(), "time",
                    MessageUtils.formatTime(gov.getTermEndTime() - System.currentTimeMillis()));
            MessageUtils.send(sender, "government.approval_rating", "rating",
                    String.format("%.1f", gov.getApprovalRating()));
        } else {
            MessageUtils.send(sender, "government.no_president");
        }

        // Cabinet
        MessageUtils.send(sender, "");
        MessageUtils.send(sender, "government.cabinet_title");
        for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
            UUID ministerUUID = gov.getCabinetMember(pos.toGovernmentPosition());
            if (ministerUUID != null) {
                String minName = Bukkit.getOfflinePlayer(ministerUUID).getName();
                MessageUtils.send(sender, "government.cabinet_member", "position", pos.getDisplayName(), "name",
                        minName);
            } else {
                MessageUtils.send(sender, "government.cabinet_empty", "position", pos.getDisplayName());
            }
        }

        // Active Orders
        var activeOrders = plugin.getExecutiveOrderManager().getActiveOrders();
        if (!activeOrders.isEmpty()) {
            MessageUtils.send(sender, "");
            MessageUtils.send(sender, "government.active_orders_title");
            for (ExecutiveOrder order : activeOrders) {
                MessageUtils.send(sender, "government.active_order", "type", order.getType().getDisplayName(),
                        "time", MessageUtils.formatTime(order.getRemainingTime()));
            }
        }

        // Active Cabinet Decisions
        List<CabinetDecision> activeDecisions = plugin.getCabinetManager().getAllActiveDecisions();
        if (!activeDecisions.isEmpty()) {
            MessageUtils.send(sender, "");
            MessageUtils.send(sender, "government.active_decisions_title");
            for (CabinetDecision decision : activeDecisions) {
                MessageUtils.send(sender, "government.active_decision", "type", decision.getType().getDisplayName(),
                        "time", MessageUtils.formatTime(decision.getRemainingTime()));
            }
        }
    }

    // === ELECTION ===

    private void showElectionInfo(CommandSender sender) {
        Election election = plugin.getDataManager().getElection();

        MessageUtils.send(sender, "election.header");
        MessageUtils.send(sender, "election.title");
        MessageUtils.send(sender, "election.footer");

        MessageUtils.send(sender, "election.phase", "phase", election.getPhase().getDisplayName());
        MessageUtils.send(sender, "election.time_remaining", "time",
                MessageUtils.formatTime(election.getPhaseEndTime() - System.currentTimeMillis()));
        MessageUtils.send(sender, "election.candidate_count", "count", election.getCandidates().size());
        MessageUtils.send(sender, "election.total_votes", "count", election.getTotalVotes());

        if (sender instanceof Player player) {
            boolean hasVoted = election.hasVoted(player.getUniqueId());
            if (hasVoted) {
                MessageUtils.send(sender, "election.vote_status_voted");
            } else {
                MessageUtils.send(sender, "election.vote_status_not_voted");
            }
        }
    }

    private void showCandidates(CommandSender sender) {
        Election election = plugin.getDataManager().getElection();

        MessageUtils.send(sender, "candidates.header");
        MessageUtils.send(sender, "candidates.title");
        MessageUtils.send(sender, "candidates.footer");

        if (election.getCandidates().isEmpty()) {
            MessageUtils.send(sender, "candidates.no_candidates");
        } else {
            // Sort by votes (if voting phase) or endorsements
            List<UUID> sortedCandidates = new ArrayList<>(election.getCandidates().keySet());
            if (election.getPhase() == Election.ElectionPhase.VOTING ||
                    election.getPhase() == Election.ElectionPhase.INAUGURATION) {
                sortedCandidates.sort((a, b) -> Double.compare(election.getVoteCount(b), election.getVoteCount(a)));
            } else {
                sortedCandidates.sort(
                        (a, b) -> Integer.compare(election.getEndorsementCount(b), election.getEndorsementCount(a)));
            }

            int rank = 1;
            for (UUID candidateUUID : sortedCandidates) {
                String name = Bukkit.getOfflinePlayer(candidateUUID).getName();
                int endorsements = election.getEndorsementCount(candidateUUID);
                double votes = election.getVoteCount(candidateUUID);

                String info = election.getPhase() == Election.ElectionPhase.VOTING ? String.format("%.1f votes", votes)
                        : endorsements + " endorsements";

                MessageUtils.send(sender, "candidates.candidate_entry", "rank", rank, "name", name, "info", info);
                rank++;
            }
        }
    }

    // === VOTING ===

    private void handleVote(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        if (args.length < 2) {
            MessageUtils.send(player, "vote.usage");
            return;
        }

        String candidateName = args[1];
        Player candidate = Bukkit.getPlayer(candidateName);

        if (candidate == null) {
            // Try offline player
            var offlinePlayer = Bukkit.getOfflinePlayer(candidateName);
            if (offlinePlayer.hasPlayedBefore()) {
                plugin.getElectionManager().castVote(player, offlinePlayer.getUniqueId());
            } else {
                MessageUtils.send(player, "general.player_not_found");
            }
            return;
        }

        plugin.getElectionManager().castVote(player, candidate.getUniqueId());
    }

    // === REGISTRATION ===

    private void handleRegister(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        plugin.getElectionManager().registerCandidate(player, "");
    }

    // === ENDORSEMENT ===

    private void handleEndorse(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        if (args.length < 2) {
            MessageUtils.send(player, "endorse.usage");
            return;
        }

        String candidateName = args[1];
        var offlinePlayer = Bukkit.getOfflinePlayer(candidateName);

        if (!offlinePlayer.hasPlayedBefore()) {
            MessageUtils.send(player, "general.player_not_found");
            return;
        }

        plugin.getElectionManager().endorseCandidate(player, offlinePlayer.getUniqueId());
    }

    // === CAMPAIGN ===

    private void handleCampaign(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        if (args.length < 2) {
            MessageUtils.send(player, "campaign.usage");
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Election election = plugin.getDataManager().getElection();
        if (!election.getCandidates().containsKey(player.getUniqueId())) {
            MessageUtils.send(player, "campaign.not_candidate");
            return;
        }

        MessageUtils.broadcast("campaign.broadcast");
        MessageUtils.broadcast("campaign.broadcast_name", "name", player.getName());
        MessageUtils.broadcast("campaign.broadcast_message", "message", message);
        MessageUtils.broadcast("campaign.broadcast_footer");
    }

    // === EXECUTIVE ORDERS ===

    private void handleExecutiveOrder(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        Government gov = plugin.getDataManager().getGovernment();
        if (!gov.hasPresident() || !gov.getPresidentUUID().equals(player.getUniqueId())) {
            if (!player.hasPermission("democracy.admin")) {
                MessageUtils.send(player, "executive_orders.only_president");
                return;
            }
        }

        if (args.length < 2) {
            // Show available orders
            MessageUtils.send(player, "executive_orders.header");
            for (ExecutiveOrder.ExecutiveOrderType type : ExecutiveOrder.ExecutiveOrderType.values()) {
                boolean onCooldown = plugin.getExecutiveOrderManager().isOrderOnCooldown(type);
                boolean active = plugin.getExecutiveOrderManager().isOrderActive(type);

                String status = active ? "<green>[ACTIVE]" : (onCooldown ? "<red>[COOLDOWN]" : "<yellow>[AVAILABLE]");

                MessageUtils.send(player, "executive_orders.order_entry", "status", status, "name", type.name(),
                        "display", type.getDisplayName());
            }
            MessageUtils.send(player, "executive_orders.usage");
            return;
        }

        String orderName = args[1].toUpperCase();
        try {
            ExecutiveOrder.ExecutiveOrderType type = ExecutiveOrder.ExecutiveOrderType.valueOf(orderName);
            plugin.getExecutiveOrderManager().issueOrder(player, type);
        } catch (IllegalArgumentException e) {
            MessageUtils.send(player, "executive_orders.invalid_order");
        }
    }

    // === CABINET ===

    private void handleCabinetCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        if (args.length < 2) {
            showCabinetHelp(player);
            return;
        }

        String subCmd = args[1].toLowerCase();

        switch (subCmd) {
            case "appoint":
                handleCabinetAppoint(player, args);
                break;
            case "dismiss":
                handleCabinetDismiss(player, args);
                break;
            case "decision":
                handleCabinetDecision(player, args);
                break;
            case "info":
                showCabinetInfo(player);
                break;
            default:
                showCabinetHelp(player);
                break;
        }
    }

    private void showCabinetHelp(Player player) {
        MessageUtils.send(player, "cabinet.help_header");
        MessageUtils.send(player, "cabinet.help_info");
        MessageUtils.send(player, "cabinet.help_appoint");
        MessageUtils.send(player, "cabinet.help_dismiss");
        MessageUtils.send(player, "cabinet.help_decision");
        MessageUtils.send(player, "");
        MessageUtils.send(player, "cabinet.help_positions");
    }

    private void handleCabinetAppoint(Player player, String[] args) {
        Government gov = plugin.getDataManager().getGovernment();

        // Check if president
        if (!gov.hasPresident() || !gov.getPresidentUUID().equals(player.getUniqueId())) {
            if (!player.hasPermission("democracy.admin")) {
                MessageUtils.send(player, "cabinet.appoint_not_president");
                return;
            }
        }

        if (args.length < 4) {
            MessageUtils.send(player, "cabinet.appoint_usage");
            return;
        }

        String posName = args[2].toUpperCase();
        String targetName = args[3];

        try {
            CabinetDecision.CabinetPosition position = CabinetDecision.CabinetPosition.valueOf(posName);
            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                MessageUtils.send(player, "cabinet.appoint_offline");
                return;
            }

            plugin.getGovernmentManager().appointCabinetMember(position.toGovernmentPosition(), target.getUniqueId());
            MessageUtils.send(player, "cabinet.appoint_success", "player", target.getName(), "position",
                    position.getDisplayName());
            MessageUtils.send(target, "cabinet.appoint_target", "position", position.getDisplayName());

        } catch (IllegalArgumentException e) {
            MessageUtils.send(player, "cabinet.appoint_invalid_position");
        }
    }

    private void handleCabinetDismiss(Player player, String[] args) {
        Government gov = plugin.getDataManager().getGovernment();

        if (!gov.hasPresident() || !gov.getPresidentUUID().equals(player.getUniqueId())) {
            if (!player.hasPermission("democracy.admin")) {
                MessageUtils.send(player, "cabinet.appoint_not_president");
                return;
            }
        }

        if (args.length < 3) {
            MessageUtils.send(player, "cabinet.dismiss_usage");
            return;
        }

        String posName = args[2].toUpperCase();

        try {
            CabinetDecision.CabinetPosition position = CabinetDecision.CabinetPosition.valueOf(posName);
            UUID currentMinister = gov.getCabinetMember(position.toGovernmentPosition());

            if (currentMinister == null) {
                MessageUtils.send(player, "cabinet.dismiss_empty");
                return;
            }

            String ministerName = Bukkit.getOfflinePlayer(currentMinister).getName();
            plugin.getGovernmentManager().removeCabinetMember(position.toGovernmentPosition());
            MessageUtils.send(player, "cabinet.dismiss_success", "player", ministerName, "position",
                    position.getDisplayName());

        } catch (IllegalArgumentException e) {
            MessageUtils.send(player, "cabinet.appoint_invalid_position");
        }
    }

    private void handleCabinetDecision(Player player, String[] args) {
        if (args.length < 3) {
            // Show available decisions for this player
            Government gov = plugin.getDataManager().getGovernment();
            CabinetDecision.CabinetPosition myPosition = null;

            for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
                UUID minister = gov.getCabinetMember(pos.toGovernmentPosition());
                if (minister != null && minister.equals(player.getUniqueId())) {
                    myPosition = pos;
                    break;
                }
            }

            if (myPosition == null) {
                MessageUtils.send(player, "cabinet.decision_not_minister");
                return;
            }

            MessageUtils.send(player, "cabinet.decision_header", "POSITION", myPosition.getDisplayName().toUpperCase());

            for (CabinetDecision.DecisionType type : CabinetDecision.DecisionType.values()) {
                if (type.getPosition() != myPosition)
                    continue;

                boolean onCooldown = plugin.getCabinetManager().isDecisionOnCooldown(type);
                boolean active = plugin.getCabinetManager().isDecisionActive(type);

                String status = active ? "<green>[ACTIVE]" : (onCooldown ? "<red>[CD]" : "<yellow>[OK]");

                MessageUtils.send(player, "cabinet.decision_entry", "status", status, "name", type.name());
                MessageUtils.send(player, "cabinet.decision_description", "description", type.getDescription());
            }
            return;
        }

        String decisionName = args[2].toUpperCase();
        try {
            CabinetDecision.DecisionType type = CabinetDecision.DecisionType.valueOf(decisionName);
            plugin.getCabinetManager().issueDecision(player, type);
        } catch (IllegalArgumentException e) {
            MessageUtils.send(player, "cabinet.decision_invalid");
        }
    }

    private void showCabinetInfo(Player player) {
        Government gov = plugin.getDataManager().getGovernment();

        MessageUtils.send(player, "cabinet.info_header");
        MessageUtils.send(player, "cabinet.info_title");
        MessageUtils.send(player, "cabinet.info_footer");

        for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
            UUID ministerUUID = gov.getCabinetMember(pos.toGovernmentPosition());
            String ministerName = ministerUUID != null ? Bukkit.getOfflinePlayer(ministerUUID).getName() : "Empty";

            MessageUtils.send(player, "cabinet.info_member", "position", pos.getDisplayName(), "name", ministerName);

            // Show active decisions for this position
            var decisions = plugin.getCabinetManager().getActiveDecisionsByPosition(pos);
            if (decisions != null) {
                for (CabinetDecision decision : decisions) {
                    MessageUtils.send(player, "cabinet.info_decision", "type", decision.getType().getDisplayName(),
                            "time", MessageUtils.formatTime(decision.getRemainingTime()));
                }
            }
        }
    }

    // === TREASURY ===

    private void handleTreasury(CommandSender sender, String[] args) {
        Treasury treasury = plugin.getDataManager().getTreasury();

        MessageUtils.send(sender, "treasury.header");
        MessageUtils.send(sender, "treasury.title");
        MessageUtils.send(sender, "treasury.footer");
        MessageUtils.send(sender, "treasury.balance", "balance", MessageUtils.formatNumber(treasury.getBalance()));
        MessageUtils.send(sender, "treasury.income", "income",
                MessageUtils.formatNumber(treasury.getTotalIncome()));
        MessageUtils.send(sender, "treasury.expenses", "expenses",
                MessageUtils.formatNumber(treasury.getTotalExpenses()));

        // Recent transactions
        MessageUtils.send(sender, "");
        MessageUtils.send(sender, "treasury.transactions_title");
        var transactions = treasury.getRecentTransactions(5);
        for (Treasury.Transaction tx : transactions) {
            String type = tx.getType().name().contains("INCOME") || tx.getType().name().contains("DONATION")
                    || tx.getType().name().contains("FUND") || tx.getType().name().contains("REFUND")
                            ? "treasury.transaction_income"
                            : "treasury.transaction_expense";
            MessageUtils.send(sender, type, "amount", MessageUtils.formatNumber(tx.getAmount()), "description",
                    tx.getDescription());
        }

        // Donate option
        if (sender instanceof Player && args.length >= 3 && args[1].equalsIgnoreCase("donate")) {
            try {
                long amount = Long.parseLong(args[2]);
                Player player = (Player) sender;

                if (plugin.getVaultHook().getBalance(player.getUniqueId()) < amount) {
                    MessageUtils.send(player, "treasury.donate_insufficient");
                    return;
                }

                plugin.getVaultHook().withdraw(player.getUniqueId(), amount);
                plugin.getTreasuryManager().deposit(Treasury.TransactionType.DONATION, amount,
                        "Donation from " + player.getName(), player.getUniqueId());
                MessageUtils.send(player, "treasury.donate_success", "amount", MessageUtils.formatNumber(amount));
                MessageUtils.broadcast("treasury.donate_broadcast", "player", player.getName(), "amount",
                        MessageUtils.formatNumber(amount));
            } catch (NumberFormatException e) {
                MessageUtils.send(sender, "treasury.donate_invalid");
            }
        }

        MessageUtils.send(sender, "");
        MessageUtils.send(sender, "treasury.donate_usage");
    }

    // === ARENA ===

    private void handleArena(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        if (args.length < 2) {
            showArenaHelp(player);
            return;
        }

        String subCmd = args[1].toLowerCase();

        switch (subCmd) {
            case "start":
                handleArenaStart(player);
                break;
            case "join":
                handleArenaJoin(player);
                break;
            case "leave":
                handleArenaLeave(player);
                break;
            case "stats":
                showArenaStats(player);
                break;
            case "leaderboard":
            case "lb":
                showArenaLeaderboard(player);
                break;
            default:
                showArenaHelp(player);
                break;
        }
    }

    private void showArenaHelp(Player player) {
        MessageUtils.send(player, "arena.help_header");
        MessageUtils.send(player, "arena.help_join");
        MessageUtils.send(player, "arena.help_leave");
        MessageUtils.send(player, "arena.help_stats");
        MessageUtils.send(player, "arena.help_leaderboard");

        if (player.hasPermission("democracy.president")) {
            MessageUtils.send(player, "arena.help_start");
        }
    }

    private void handleArenaStart(Player player) {
        Government gov = plugin.getDataManager().getGovernment();

        if (!gov.hasPresident() || !gov.getPresidentUUID().equals(player.getUniqueId())) {
            if (!player.hasPermission("democracy.admin")) {
                MessageUtils.send(player, "arena.not_president");
                return;
            }
        }

        plugin.getArenaManager().startNewSession(player);
    }

    private void handleArenaJoin(Player player) {
        plugin.getArenaManager().joinArena(player);
    }

    private void handleArenaLeave(Player player) {
        plugin.getArenaManager().leaveArena(player.getUniqueId());
    }

    private void showArenaStats(Player player) {
        showArenaStatsFor(player);
    }

    private void showArenaLeaderboard(Player player) {
        showArenaLeaderboardFor(player);
    }

    // === RECALL ===

    private void handleRecall(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        if (args.length < 2) {
            showRecallHelp(player);
            return;
        }

        String subCmd = args[1].toLowerCase();

        switch (subCmd) {
            case "start":
                plugin.getRecallManager().startPetition(player.getUniqueId(), "General dissatisfaction");
                break;
            case "sign":
                plugin.getRecallManager().signPetition(player.getUniqueId());
                break;
            case "vote":
                if (args.length < 3) {
                    MessageUtils.send(player, "recall.vote_usage");
                    return;
                }
                boolean voteYes = args[2].equalsIgnoreCase("yes") || args[2].equalsIgnoreCase("remove");
                plugin.getRecallManager().castRecallVote(player.getUniqueId(), voteYes);
                break;
            case "status":
                showRecallStatus(player);
                break;
            default:
                showRecallHelp(player);
                break;
        }
    }

    private void showRecallHelp(Player player) {
        MessageUtils.send(player, "recall.help_header");
        MessageUtils.send(player, "recall.help_start");
        MessageUtils.send(player, "recall.help_sign");
        MessageUtils.send(player, "recall.help_vote");
        MessageUtils.send(player, "recall.help_status");
    }

    private void showRecallStatus(Player player) {
        RecallPetition petition = plugin.getDataManager().getRecallPetition();

        if (petition == null || petition.getPhase() == RecallPetition.RecallPhase.COMPLETED
                || petition.getPhase() == RecallPetition.RecallPhase.FAILED) {
            MessageUtils.send(player, "recall.no_active_petition");
            return;
        }

        MessageUtils.send(player, "recall.status_header");
        MessageUtils.send(player, "recall.status_phase", "phase", petition.getPhase().name());
        int requiredSignatures = (int) Math.ceil(plugin.getServer().getOnlinePlayers().size() * 0.3);
        MessageUtils.send(player, "recall.status_signatures", "current", petition.getSignatureCount(), "required",
                requiredSignatures);

        if (petition.getPhase() == RecallPetition.RecallPhase.VOTING) {
            MessageUtils.send(player, "recall.status_remove_votes", "votes", petition.getRemoveVotes());
            MessageUtils.send(player, "recall.status_keep_votes", "votes", petition.getKeepVotes());
        }
    }

    // === RATING ===

    private void handleRating(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        Government gov = plugin.getDataManager().getGovernment();

        if (!gov.hasPresident()) {
            MessageUtils.send(player, "rating.no_president");
            return;
        }

        if (args.length < 2) {
            MessageUtils.send(player, "rating.usage");
            MessageUtils.send(player, "rating.current_rating", "rating",
                    String.format("%.1f", gov.getApprovalRating()));
            return;
        }

        try {
            int rating = Integer.parseInt(args[1]);
            if (rating < 1 || rating > 5) {
                MessageUtils.send(player, "rating.invalid_range");
                return;
            }

            gov.getApprovalRatings().add(new Government.ApprovalRating(player.getUniqueId(), rating));
            gov.calculateApprovalRating();
            plugin.getDataManager().saveGovernment();
            MessageUtils.send(player, "rating.success", "rating", rating);

        } catch (NumberFormatException e) {
            MessageUtils.send(player, "rating.invalid_number");
        }
    }

    // === GOVERNMENT GUI ===

    private void handleGovernmentGUI(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        Government gov = plugin.getDataManager().getGovernment();
        boolean isPresident = gov.hasPresident() && gov.getPresidentUUID().equals(player.getUniqueId());
        boolean isMinister = false;

        for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
            UUID minister = gov.getCabinetMember(pos.toGovernmentPosition());
            if (minister != null && minister.equals(player.getUniqueId())) {
                isMinister = true;
                break;
            }
        }

        if (!isPresident && !isMinister) {
            MessageUtils.send(player, "gui.government_not_allowed");
            return;
        }

        plugin.getGUIListener().openGovernmentGUI(player);
    }

    // === LEADERBOARD ===

    private void handleLeaderboard(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "<red>Perintah ini hanya untuk player!");
            return;
        }

        plugin.getGUIListener().openLeaderboardGUI(player);
    }

    // === HISTORY ===

    private void showHistory(CommandSender sender, String[] args) {
        PresidentHistory history = plugin.getDataManager().getPresidentHistory();
        List<PresidentHistory.PresidentRecord> records = history != null ? history.getRecords() : new ArrayList<>();

        MessageUtils.send(sender, "history.header");
        MessageUtils.send(sender, "history.title");
        MessageUtils.send(sender, "history.footer");

        if (records.isEmpty()) {
            MessageUtils.send(sender, "history.no_history");
        } else {
            int limit = Math.min(10, records.size());
            for (int i = 0; i < limit; i++) {
                PresidentHistory.PresidentRecord h = records.get(i);
                String name = Bukkit.getOfflinePlayer(h.getUuid()).getName();
                MessageUtils.send(sender, "history.entry", "rank", (i + 1), "name", name);
                MessageUtils.send(sender, "history.entry_stats", "rating",
                        String.format("%.1f", h.getFinalApprovalRating()),
                        "orders", h.getExecutiveOrdersIssued(), "games", h.getPresidentialGamesHosted());
            }
        }
    }

    // === STATS ===

    private void showPlayerStats(CommandSender sender, String[] args) {
        UUID targetUUID;
        String targetName;

        if (args.length >= 2) {
            var offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (!offlinePlayer.hasPlayedBefore()) {
                MessageUtils.send(sender, "general.player_not_found");
                return;
            }
            targetUUID = offlinePlayer.getUniqueId();
            targetName = offlinePlayer.getName();
        } else if (sender instanceof Player player) {
            targetUUID = player.getUniqueId();
            targetName = player.getName();
        } else {
            MessageUtils.send(sender, "stats.usage");
            return;
        }

        PlayerData data = plugin.getDataManager().getPlayerData(targetUUID);

        MessageUtils.send(sender, "stats.header");
        MessageUtils.send(sender, "stats.title", "name", targetName.toUpperCase());
        MessageUtils.send(sender, "stats.footer");
        MessageUtils.send(sender, "stats.playtime", "time", MessageUtils.formatTime(data.getTotalPlaytime()));
        MessageUtils.send(sender, "stats.level", "level", calculateLevel(data));
        MessageUtils.send(sender, "stats.votes", "votes", data.getTotalVotesCast());
        MessageUtils.send(sender, "stats.endorsements", "endorsements", data.getEndorsementsGiven());
        MessageUtils.send(sender, "stats.president_times", "times", data.getTimesServedAsPresident());
        MessageUtils.send(sender, "stats.minister_times", "times", data.getTimesServedAsCabinet());
        MessageUtils.send(sender, "");
        MessageUtils.send(sender, "stats.arena_title");
        MessageUtils.send(sender, "stats.arena_kills", "kills", data.getArenaKills(), "deaths", data.getArenaDeaths());
        MessageUtils.send(sender, "stats.arena_streak", "streak", data.getBestKillstreak());
    }

    // === ADMIN ===

    private void handleAdmin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("democracy.admin")) {
            MessageUtils.send(sender, "general.no_permission");
            return;
        }

        if (args.length < 2) {
            showAdminHelp(sender);
            return;
        }

        String subCmd = args[1].toLowerCase();

        switch (subCmd) {
            case "set":
                if (args.length > 2 && args[2].equalsIgnoreCase("president")) {
                    handleAdminSetPresident(sender, args);
                } else {
                    MessageUtils.send(sender, "admin.help_setpresident");
                }
                break;
            case "remove":
                if (args.length > 2 && args[2].equalsIgnoreCase("president")) {
                    handleAdminRemovePresident(sender);
                } else {
                    MessageUtils.send(sender, "admin.help_removepresident");
                }
                break;
            case "confirm":
                handleAdminConfirm(sender);
                break;
            case "startelection":
            case "startElection": // Case insensitive fallback handled by toLowerCase() above, but keeping for
                                  // clarity
                handleAdminStartElection(sender);
                break;
            case "endelection":
            case "endElection":
                handleAdminEndElection(sender);
                break;
            case "skipelection":
            case "skipElection":
                handleAdminSkipElection(sender);
                break;
            case "endarena":
            case "endArena":
                handleAdminEndArena(sender);
                break;
            case "addtreasury":
                handleAdminAddTreasury(sender, args);
                break;
            case "reload":
                handleAdminReload(sender);
                break;
            case "reset":
                handleAdminReset(sender, args);
                break;
            case "stoporder":
                handleAdminStopOrder(sender, args);
                break;
            case "action":
                handleAdminAction(sender, args);
                break;
            default:
                showAdminHelp(sender);
                break;
        }
    }

    private void showAdminHelp(CommandSender sender) {
        MessageUtils.send(sender, "admin.help_header");
        MessageUtils.send(sender, "admin.help_setpresident");
        MessageUtils.send(sender, "admin.help_removepresident");
        MessageUtils.send(sender, "admin.help_startelection");
        MessageUtils.send(sender, "admin.help_endelection");
        MessageUtils.send(sender, "<gold>/dc admin skipelection <gray>- Force skip to the next election phase");
        MessageUtils.send(sender, "<gold>/dc admin endarena <gray>- Force end active arena games");
        MessageUtils.send(sender, "admin.help_addtreasury");
        MessageUtils.send(sender, "admin.help_reload");
        MessageUtils.send(sender, "admin.help_reset");
        MessageUtils.send(sender,
                "<gold>/dc admin action <action_id> <player> <gray>- Execute a GUI action on a player");
    }

    // === ADMIN GUI ACTION ===

    /**
     * Executes any GUI action (open_gui_* or action_*) on a target player.
     * Usage: /dc admin action <action_id> <player>
     * Works from console, RCON, or any admin sender.
     */
    private void handleAdminAction(CommandSender sender, String[] args) {
        if (args.length < 4) {
            MessageUtils.send(sender, "<gold>═══ ADMIN ACTION COMMAND ═══");
            MessageUtils.send(sender, "<white>/dc admin action <action_id> <player>");
            MessageUtils.send(sender, "");
            MessageUtils.send(sender, "<yellow>--- Open GUI Actions ---");
            for (GUIAction action : GUIAction.values()) {
                if (action == GUIAction.UNKNOWN)
                    continue;
                MessageUtils.send(sender, "<gray>  " + action.getConfigKey());
            }
            return;
        }

        String actionId = args[2].toLowerCase();
        String playerName = args[3];

        // Resolve target player
        Player target = Bukkit.getPlayer(playerName);
        if (target == null || !target.isOnline()) {
            MessageUtils.send(sender, "<red>Player '" + playerName + "' is not online.");
            return;
        }

        // Parse action
        GUIAction action = GUIAction.fromConfig(actionId);
        if (action == GUIAction.UNKNOWN) {
            MessageUtils.send(sender,
                    "<red>Unknown action: '" + actionId + "'. Use /dc admin action to see valid actions.");
            return;
        }

        // Execute action on the target player
        try {
            plugin.getGUIListener().executeGUIAction(target, action, actionId, "", null);
            MessageUtils.send(sender, "<green>Executed action '<gold>" + actionId + "<green>' on player '<gold>"
                    + target.getName() + "<green>'.");
        } catch (Exception e) {
            MessageUtils.send(sender, "<red>Failed to execute action: " + e.getMessage());
            plugin.getLogger().severe("Error executing admin GUI action '" + actionId + "' on '" + target.getName()
                    + "': " + e.getMessage());
        }
    }

    private void handleAdminSetPresident(CommandSender sender, String[] args) {
        if (args.length < 4) {
            MessageUtils.send(sender, "admin.setpresident_usage");
            return;
        }

        String targetName = args[3];
        // First try to get online player
        Player onlinePlayer = Bukkit.getPlayer(targetName);

        org.bukkit.OfflinePlayer targetPlayer;
        if (onlinePlayer != null) {
            targetPlayer = onlinePlayer;
        } else {
            // If not online, try to get offline player
            targetPlayer = Bukkit.getOfflinePlayer(targetName);
            if (!targetPlayer.hasPlayedBefore()) {
                MessageUtils.send(sender, "general.player_not_found");
                return;
            }
        }

        final org.bukkit.OfflinePlayer offlinePlayer = targetPlayer;

        if (sender instanceof Player player) {
            pendingConfirmations.put(player.getUniqueId(), () -> {
                plugin.getGovernmentManager().setPresident(offlinePlayer.getUniqueId(), offlinePlayer.getName(), false);

                // Ensure election phase is stopped/reset
                plugin.getElectionManager().endElection();
                plugin.getDataManager().saveElection();
                plugin.getDataManager().saveGovernment();

                MessageUtils.send(sender, "admin.setpresident_success", "player", offlinePlayer.getName());
            });
            MessageUtils.send(sender, "<yellow>Are you sure you want to set <gold>" + offlinePlayer.getName() +
                    "</gold> as president? This will end the current election.</yellow>");
            MessageUtils.send(sender, "<green>Type <gold>/dc admin confirm</gold> to proceed.</green>");
        } else {
            // Console doesn't need confirmation
            plugin.getGovernmentManager().setPresident(offlinePlayer.getUniqueId(), offlinePlayer.getName(), false);
            plugin.getElectionManager().endElection();
            plugin.getDataManager().saveElection();
            plugin.getDataManager().saveGovernment();
            MessageUtils.send(sender, "admin.setpresident_success", "player", offlinePlayer.getName());
        }
    }

    private void handleAdminConfirm(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        if (pendingConfirmations.containsKey(player.getUniqueId())) {
            pendingConfirmations.remove(player.getUniqueId()).run();
        } else {
            MessageUtils.send(player, "<red>You have no pending confirmations.</red>");
        }
    }

    private void handleAdminRemovePresident(CommandSender sender) {
        Government gov = plugin.getDataManager().getGovernment();
        if (!gov.hasPresident()) {
            MessageUtils.send(sender, "<red>There is no active president to remove.</red>");
            return;
        }

        if (sender instanceof Player player) {
            pendingConfirmations.put(player.getUniqueId(), () -> {
                plugin.getGovernmentManager().endTerm("ADMIN_REMOVAL");
                plugin.getElectionManager().startElection(); // Sync phase: Start registration immediately
                MessageUtils.send(sender, "admin.removepresident_success");
                MessageUtils.broadcast(
                        "<yellow>A new election cycle has been initiated following the removal of the president.</yellow>");
            });
            MessageUtils.send(sender,
                    "<yellow>Are you sure you want to remove the current president? This will start a new election.</yellow>");
            MessageUtils.send(sender, "<green>Type <gold>/dc admin confirm</gold> to proceed.</green>");
        } else {
            // Console doesn't need confirmation
            plugin.getGovernmentManager().endTerm("ADMIN_REMOVAL");
            plugin.getElectionManager().startElection();
            MessageUtils.send(sender, "admin.removepresident_success");
        }
    }

    private void handleAdminStartElection(CommandSender sender) {
        plugin.getElectionManager().startElection();
        MessageUtils.send(sender, "admin.startelection_success");
    }

    private void handleAdminEndElection(CommandSender sender) {
        plugin.getElectionManager().endElection();
        MessageUtils.send(sender, "admin.endelection_success");
    }

    private void handleAdminSkipElection(CommandSender sender) {
        if (!plugin.getElectionManager().isElectionActive()) {
            MessageUtils.send(sender, "<red>There is no active election to skip.</red>");
            return;
        }
        plugin.getElectionManager().forceNextPhase();
        MessageUtils.send(sender, "<green>Election skipped to the next phase.</green>");
    }

    private void handleAdminEndArena(CommandSender sender) {
        if (!plugin.getArenaManager().isArenaActive()) {
            MessageUtils.send(sender, "<red>There are no active Arena Games to end.</red>");
            return;
        }
        plugin.getArenaManager().endArena();
        MessageUtils.send(sender, "<green>Arena games force-ended.</green>");
    }

    private void handleAdminAddTreasury(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtils.send(sender, "admin.addtreasury_usage");
            return;
        }

        try {
            long amount = Long.parseLong(args[2]);
            plugin.getTreasuryManager().deposit(Treasury.TransactionType.MISC_EXPENSE, amount,
                    "Admin deposit by " + sender.getName(), null);
            MessageUtils.send(sender, "admin.addtreasury_success", "amount", MessageUtils.formatNumber(amount));
        } catch (NumberFormatException e) {
            MessageUtils.send(sender, "admin.addtreasury_invalid");
        }
    }

    private void handleAdminReload(CommandSender sender) {
        plugin.reloadConfig();
        plugin.reloadLanguage();
        plugin.reloadGUI();
        MessageUtils.send(sender, "general.config_reloaded");
    }

    private void handleAdminReset(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtils.send(sender, "admin.reset_usage");
            return;
        }

        String data = args[2].toLowerCase();

        switch (data) {
            case "government":
                plugin.getDataManager().resetGovernment();
                MessageUtils.send(sender, "general.data_reset", "data", "Government");
                break;
            case "election":
                plugin.getDataManager().resetElection();
                MessageUtils.send(sender, "general.data_reset", "data", "Election");
                break;
            case "treasury":
                plugin.getDataManager().resetTreasury();
                MessageUtils.send(sender, "general.data_reset", "data", "Treasury");
                break;
            case "all":
                plugin.getDataManager().resetGovernment();
                plugin.getDataManager().resetElection();
                plugin.getDataManager().resetTreasury();
                MessageUtils.send(sender, "general.all_data_reset");
                break;
            default:
                MessageUtils.send(sender, "admin.reset_invalid");
                break;
        }
    }

    private void handleAdminStopOrder(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtils.send(sender, "admin.stoporder_usage");
            return;
        }

        String orderName = args[2].toUpperCase();
        try {
            ExecutiveOrder.ExecutiveOrderType type = ExecutiveOrder.ExecutiveOrderType.valueOf(orderName);
            boolean stopped = plugin.getExecutiveOrderManager().stopOrder(type);
            if (stopped) {
                MessageUtils.send(sender, "admin.stoporder_success", "order", type.getDisplayName());
                MessageUtils.broadcast("admin.stoporder_broadcast", "order", type.getDisplayName());
            } else {
                MessageUtils.send(sender, "admin.stoporder_not_active");
            }
        } catch (IllegalArgumentException e) {
            MessageUtils.send(sender, "admin.stoporder_invalid");
        }
    }

    // === TAB COMPLETE ===

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList(
                    "menu", "help", "info", "election", "candidates", "vote", "register",
                    "endorse", "campaign", "order", "cabinet", "treasury", "arena",
                    "recall", "rate", "history", "stats", "government", "leaderboard", "tax"));
            if (sender.hasPermission("democracy.admin")) {
                completions.add("admin");
            }
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();

            switch (sub) {
                case "vote":
                case "endorse":
                    Election election = plugin.getDataManager().getElection();
                    for (UUID candidate : election.getCandidates().keySet()) {
                        completions.add(Bukkit.getOfflinePlayer(candidate).getName());
                    }
                    break;
                case "order":
                case "executive":
                    for (ExecutiveOrder.ExecutiveOrderType type : ExecutiveOrder.ExecutiveOrderType.values()) {
                        completions.add(type.name());
                    }
                    break;
                case "cabinet":
                    completions.addAll(Arrays.asList("info", "appoint", "dismiss", "decision"));
                    break;
                case "arena":
                    completions.addAll(Arrays.asList("join", "leave", "stats", "leaderboard"));
                    if (sender.hasPermission("democracy.president")) {
                        completions.add("start");
                    }
                    break;
                case "recall":
                    completions.addAll(Arrays.asList("start", "sign", "vote", "status"));
                    break;
                case "rate":
                    completions.addAll(Arrays.asList("1", "2", "3", "4", "5"));
                    break;
                case "stats":
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        completions.add(p.getName());
                    }
                    break;
                case "admin":
                    if (sender.hasPermission("democracy.admin")) {
                        completions.addAll(Arrays.asList(
                                "set", "remove", "startelection", "skipelection", "endarena",
                                "endelection", "addtreasury", "reload", "reset", "stoporder", "confirm", "action"));
                    }
                    break;
                case "treasury":
                    completions.add("donate");
                    break;
                case "tax":
                    completions.addAll(Arrays.asList("info", "pay", "gui"));
                    if (sender.hasPermission("democracy.admin")) {
                        completions.add("admin");
                    }
                    break;
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase();
            String sub2 = args[1].toLowerCase();

            if (sub.equals("cabinet")) {
                if (sub2.equals("appoint") || sub2.equals("dismiss")) {
                    for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
                        completions.add(pos.name());
                    }
                } else if (sub2.equals("decision")) {
                    // Show decisions for player's position
                    if (sender instanceof Player player) {
                        Government gov = plugin.getDataManager().getGovernment();
                        for (CabinetDecision.CabinetPosition pos : CabinetDecision.CabinetPosition.values()) {
                            UUID minister = gov.getCabinetMember(pos.toGovernmentPosition());
                            if (minister != null && minister.equals(player.getUniqueId())) {
                                for (CabinetDecision.DecisionType type : CabinetDecision.DecisionType.values()) {
                                    if (type.getPosition() == pos) {
                                        completions.add(type.name());
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (sub.equals("tax") && sub2.equals("admin")) {
                if (sender.hasPermission("democracy.admin")) {
                    completions.addAll(Arrays.asList("collect", "enable", "disable",
                            "exempt", "unexempt", "forgive", "setamount"));
                }
            } else if (sub.equals("recall") && sub2.equals("vote")) {
                completions.addAll(Arrays.asList("yes", "no", "remove", "keep"));
            } else if (sub.equals("admin")) {
                if (sub2.equals("set") || sub2.equals("remove")) {
                    completions.add("president");
                } else if (sub2.equals("reset")) {
                    completions.addAll(Arrays.asList("government", "election", "treasury", "all"));
                } else if (sub2.equals("action")) {
                    // Tab-complete all available GUIAction config keys
                    for (GUIAction action : GUIAction.values()) {
                        if (action != GUIAction.UNKNOWN) {
                            completions.add(action.getConfigKey());
                        }
                    }
                }
            }
        } else if (args.length == 4) {
            String sub = args[0].toLowerCase();
            String sub2 = args[1].toLowerCase();

            if (sub.equals("cabinet") && sub2.equals("appoint")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            } else if (sub.equals("tax") && sub2.equals("admin") &&
                    (args[2].equalsIgnoreCase("exempt") || args[2].equalsIgnoreCase("unexempt")
                            || args[2].equalsIgnoreCase("forgive"))) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            } else if (sub.equals("admin") && sub2.equals("set") && args[2].equalsIgnoreCase("president")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            } else if (sub.equals("admin") && sub2.equals("action")) {
                // Tab-complete online player names for the target
                if (sender.hasPermission("democracy.admin")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        completions.add(p.getName());
                    }
                }
            }
        }

        // Filter based on input
        String input = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
    }

    // === TAX ===

    private void handleTax(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, "general.player_only");
            return;
        }

        if (args.length < 2) {
            // Open Tax GUI
            plugin.getGUIListener().openTaxGUI(player);
            return;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "info":
            case "status":
                showTaxInfo(player);
                break;
            case "pay":
                plugin.getTaxManager().payDebt(player);
                break;
            case "gui":
                plugin.getGUIListener().openTaxGUI(player);
                break;
            case "admin":
                if (!player.hasPermission("democracy.admin")) {
                    MessageUtils.send(player, "general.no_permission");
                    return;
                }
                handleTaxAdmin(player, args);
                break;
            default:
                MessageUtils.send(player,
                        "<red>Unknown tax command. Use <yellow>/dc tax</yellow> to open the tax menu.");
                break;
        }
    }

    private void showTaxInfo(Player player) {
        var taxManager = plugin.getTaxManager();
        var record = taxManager.getTaxRecord();
        String uuidStr = player.getUniqueId().toString();
        var taxData = record.getPlayerTaxData(uuidStr);

        MessageUtils.send(player, "tax.header");
        MessageUtils.send(player, "tax.title");
        MessageUtils.send(player, "tax.footer");

        if (taxManager.isEnabled()) {
            MessageUtils.send(player, "tax.status_enabled");
        } else {
            MessageUtils.send(player, "tax.status_disabled");
        }

        MessageUtils.send(player, "<gray>Tax Amount: <gold>$" + MessageUtils.formatNumber(taxManager.getTaxAmount()));
        MessageUtils.send(player, "<gray>Collection Interval: <white>" +
                plugin.getConfig().getLong("global-tax.collection-interval-hours", 24) + " hours");

        long remaining = taxManager.getTimeUntilNextCollection();
        if (remaining > 0) {
            MessageUtils.send(player, "<gray>Next Collection: <white>" + MessageUtils.formatTime(remaining));
        } else {
            MessageUtils.send(player, "<gray>Next Collection: <yellow>Pending...");
        }

        MessageUtils.send(player,
                "<gray>Total Collected: <green>$" + MessageUtils.formatNumber(record.getTotalTaxCollected()));
        MessageUtils.send(player, "<gray>Collection Cycles: <white>" + record.getTotalCollectionCycles());

        if (taxData != null && taxData.getOutstandingDebt() > 0) {
            MessageUtils.send(player,
                    "<red>Your Debt: <gold>$" + MessageUtils.formatNumber(taxData.getOutstandingDebt()));
            MessageUtils.send(player, "<gray>Use <white>/dc tax pay <gray>to pay your debt.");
        } else {
            MessageUtils.send(player, "<green>Your Debt: $0 (Good standing!)");
        }

        MessageUtils.send(player, "tax.footer");
    }

    private void handleTaxAdmin(Player player, String[] args) {
        if (args.length < 3) {
            MessageUtils.send(player, "<gold>═══ TAX ADMIN COMMANDS ═══");
            MessageUtils.send(player, "<white>/dc tax admin collect <gray>- Force tax collection");
            MessageUtils.send(player, "<white>/dc tax admin enable/disable <gray>- Toggle tax system");
            MessageUtils.send(player, "<white>/dc tax admin exempt <player> <gray>- Exempt player");
            MessageUtils.send(player, "<white>/dc tax admin unexempt <player> <gray>- Remove exemption");
            MessageUtils.send(player, "<white>/dc tax admin forgive <player> <gray>- Forgive player's debt");
            MessageUtils.send(player, "<white>/dc tax admin setamount <amount> <gray>- Set tax amount");
            return;
        }

        String adminSub = args[2].toLowerCase();

        switch (adminSub) {
            case "collect":
                plugin.getTaxManager().collectTaxes();
                MessageUtils.send(player, "tax.admin_collect");
                break;
            case "enable":
                plugin.getTaxManager().getTaxRecord().setEnabled(true);
                MessageUtils.send(player, "tax.admin_enable");
                break;
            case "disable":
                plugin.getTaxManager().getTaxRecord().setEnabled(false);
                MessageUtils.send(player, "tax.admin_disable");
                break;
            case "exempt":
                if (args.length < 4) {
                    MessageUtils.send(player, "<red>Usage: /dc tax admin exempt <player>");
                    return;
                }
                Player targetExempt = Bukkit.getPlayer(args[3]);
                if (targetExempt == null) {
                    MessageUtils.send(player, "general.player_not_found");
                    return;
                }
                plugin.getTaxManager().setExempt(targetExempt.getUniqueId(), targetExempt.getName(), true);
                MessageUtils.send(player, "<green>" + targetExempt.getName() + " is now tax exempt.");
                break;
            case "unexempt":
                if (args.length < 4) {
                    MessageUtils.send(player, "<red>Usage: /dc tax admin unexempt <player>");
                    return;
                }
                Player targetUnexempt = Bukkit.getPlayer(args[3]);
                if (targetUnexempt == null) {
                    MessageUtils.send(player, "general.player_not_found");
                    return;
                }
                plugin.getTaxManager().setExempt(targetUnexempt.getUniqueId(), targetUnexempt.getName(), false);
                MessageUtils.send(player, "<green>" + targetUnexempt.getName() + " is no longer tax exempt.");
                break;
            case "forgive":
                if (args.length < 4) {
                    MessageUtils.send(player, "<red>Usage: /dc tax admin forgive <player>");
                    return;
                }
                Player targetForgive = Bukkit.getPlayer(args[3]);
                if (targetForgive == null) {
                    MessageUtils.send(player, "general.player_not_found");
                    return;
                }
                plugin.getTaxManager().forgiveDebt(targetForgive.getUniqueId(), targetForgive.getName());
                MessageUtils.send(player, "<green>Forgave " + targetForgive.getName() + "'s tax debt.");
                break;
            case "setamount":
                if (args.length < 4) {
                    MessageUtils.send(player, "<red>Usage: /dc tax admin setamount <amount>");
                    return;
                }
                try {
                    double newAmount = Double.parseDouble(args[3]);
                    if (newAmount <= 0) {
                        MessageUtils.send(player, "<red>Amount must be positive!");
                        return;
                    }
                    plugin.getConfig().set("global-tax.amount", newAmount);
                    plugin.saveConfig();
                    MessageUtils.send(player,
                            "<green>Tax amount set to <gold>$" + MessageUtils.formatNumber(newAmount));
                } catch (NumberFormatException e) {
                    MessageUtils.send(player, "<red>Invalid amount!");
                }
                break;
            default:
                MessageUtils.send(player, "<red>Unknown tax admin command.");
                break;
        }
    }

    // === HELPER METHODS ===

    private int calculateLevel(PlayerData data) {
        // Calculate level based on playtime hours (1 level per 10 hours)
        double hours = data.getTotalPlaytime() / (1000.0 * 60 * 60);
        return (int) (hours / 10) + 1;
    }

    private void showArenaStatsFor(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        MessageUtils.send(player, "arena.stats_header");
        MessageUtils.send(player, "arena.stats_title");
        MessageUtils.send(player, "arena.stats_footer");
        MessageUtils.send(player, "arena.stats_kills", "kills", data.getArenaKills());
        MessageUtils.send(player, "arena.stats_deaths", "deaths", data.getArenaDeaths());
        MessageUtils.send(player, "arena.stats_kd", "ratio",
                String.format("%.2f", data.getArenaDeaths() > 0 ? (double) data.getArenaKills() / data.getArenaDeaths()
                        : data.getArenaKills()));
        MessageUtils.send(player, "arena.stats_streak", "streak", data.getCurrentKillstreak());
        MessageUtils.send(player, "arena.stats_best", "best", data.getBestKillstreak());
    }

    private void showArenaLeaderboardFor(Player player) {
        MessageUtils.send(player, "arena.leaderboard_header");
        MessageUtils.send(player, "arena.leaderboard_title");
        MessageUtils.send(player, "arena.leaderboard_footer");

        // Get top players by kills
        var allPlayers = plugin.getDataManager().getAllPlayerData();
        var topPlayers = allPlayers.stream()
                .sorted((a, b) -> Integer.compare(b.getArenaKills(), a.getArenaKills()))
                .limit(10)
                .toList();

        int rank = 1;
        for (PlayerData data : topPlayers) {
            String name = Bukkit.getOfflinePlayer(data.getUuid()).getName();
            MessageUtils.send(player, "arena.leaderboard_entry", "rank", rank, "name", name, "kills",
                    data.getArenaKills(), "streak", data.getBestKillstreak());
            rank++;
        }
    }
}
