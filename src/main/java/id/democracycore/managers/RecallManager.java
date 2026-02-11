package id.democracycore.managers;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import id.democracycore.DemocracyCore;
import id.democracycore.models.Government;
import id.democracycore.models.RecallPetition;
import id.democracycore.models.Treasury;
import id.democracycore.utils.MessageUtils;

public class RecallManager {
    
    private final DemocracyCore plugin;
    private RecallPetition currentPetition;
    private long lastFailedRecall = 0;
    
    public RecallManager(DemocracyCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean canStartPetition(UUID initiatorId) {
        Government gov = plugin.getDataManager().getGovernment();
        
        // Must have a president
        if (!gov.hasPresident()) return false;
        
        // Cannot recall yourself
        if (gov.getPresidentUUID().equals(initiatorId)) return false;
        
        // Check if petition already active
        if (currentPetition != null && !currentPetition.isExpired()) return false;
        
        // Check cooldown from last failed recall
        long cooldownMs = plugin.getConfig().getLong("recall.cooldown-days", 15) * 86400000L;
        if (System.currentTimeMillis() - lastFailedRecall < cooldownMs) return false;
        
        // Check if initiator has enough money for deposit
        Player initiator = Bukkit.getPlayer(initiatorId);
        if (initiator == null) return false;
        
        double deposit = plugin.getConfig().getDouble("recall.signature-deposit", 50000);
        return plugin.getVaultHook().getBalance(initiator.getUniqueId()) >= deposit;
    }
    
    public boolean startPetition(UUID initiatorId, String reason) {
        if (!canStartPetition(initiatorId)) return false;
        
        Player initiator = Bukkit.getPlayer(initiatorId);
        if (initiator == null) return false;
        
        Government gov = plugin.getDataManager().getGovernment();
        
        // Take deposit
        double deposit = plugin.getConfig().getDouble("recall.signature-deposit", 50000);
        plugin.getVaultHook().withdraw(initiator.getUniqueId(), deposit);
        
        // Create petition
        long collectionDays = plugin.getConfig().getLong("recall.collection-days", 7);
        currentPetition = new RecallPetition(
            gov.getPresidentUUID(),
            initiatorId,
            reason,
            collectionDays * 86400000L
        );
        
        // Add initiator's signature
        currentPetition.addSignature(initiatorId, deposit);
        
        // Broadcast
        String presidentName = Bukkit.getOfflinePlayer(gov.getPresidentUUID()).getName();
        
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<red>═══════════════════════════════════════");
        MessageUtils.broadcast("<dark_red>📜 <red><bold>RECALL PETITION INITIATED</bold> <dark_red>📜");
        MessageUtils.broadcast("<red>═══════════════════════════════════════");
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<yellow>Target: <white>" + presidentName);
        MessageUtils.broadcast("<yellow>Initiated by: <white>" + initiator.getName());
        MessageUtils.broadcast("<yellow>Reason: <white>" + reason);
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<gray>Use <white>/dc recall sign <gray>to add your signature");
        MessageUtils.broadcast("<gray>Deposit required: <gold>$" + MessageUtils.formatNumber(deposit));
        MessageUtils.broadcast("<gray>Signatures needed: <white>" + getRequiredSignatures());
        MessageUtils.broadcast("<gray>Time remaining: <white>" + MessageUtils.formatTime(collectionDays * 86400000L));
        MessageUtils.broadcast("");
        
        // Play sound
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 1.0f);
        }
        
        plugin.getDataManager().setRecallPetition(currentPetition);
        plugin.getDataManager().saveAll();
        
        return true;
    }
    
    public boolean signPetition(UUID signerId) {
        if (currentPetition == null || currentPetition.isExpired()) {
            return false;
        }
        
        if (currentPetition.getPhase() != RecallPetition.RecallPhase.COLLECTING) {
            return false;
        }
        
        // Cannot sign your own recall
        if (currentPetition.getTargetId().equals(signerId)) {
            return false;
        }
        
        // Check if already signed
        if (currentPetition.hasSigned(signerId)) {
            return false;
        }
        
        Player signer = Bukkit.getPlayer(signerId);
        if (signer == null) return false;
        
        // Check deposit
        double deposit = plugin.getConfig().getDouble("recall.signature-deposit", 50000);
        if (plugin.getVaultHook().getBalance(signer.getUniqueId()) < deposit) {
            MessageUtils.send(signer, "<red>You need $" + MessageUtils.formatNumber(deposit) + " to sign the petition!");
            return false;
        }

        // Take deposit
        plugin.getVaultHook().withdraw(signer.getUniqueId(), deposit);
        
        // Add signature
        currentPetition.addSignature(signerId, deposit);
        
        MessageUtils.send(signer, "<green>You signed the recall petition! Deposit: $" + MessageUtils.formatNumber(deposit));
        
        int current = currentPetition.getSignatureCount();
        int required = getRequiredSignatures();
        
        MessageUtils.broadcast("<yellow>📜 <white>" + signer.getName() + " <yellow>signed the recall petition! <gray>(" + current + "/" + required + ")");
        
        // Check if enough signatures
        if (current >= required) {
            startRecallVote();
        }
        
        plugin.getDataManager().saveAll();
        return true;
    }
    
    public boolean withdrawSignature(UUID signerId) {
        if (currentPetition == null) return false;
        if (currentPetition.getPhase() != RecallPetition.RecallPhase.COLLECTING) return false;
        if (!currentPetition.hasSigned(signerId)) return false;
        
        // Cannot withdraw if you're the initiator
        if (currentPetition.getInitiatorId().equals(signerId)) {
            return false;
        }
        
        Player signer = Bukkit.getPlayer(signerId);
        
        // Refund 50% of deposit
        double deposit = currentPetition.getDeposit(signerId);
        double refund = deposit * 0.5;
        
        currentPetition.removeSignature(signerId);
        
        if (signer != null) {
            plugin.getVaultHook().deposit(signer.getUniqueId(), refund);
            MessageUtils.send(signer, "<yellow>You withdrew your signature. Refund: $" + MessageUtils.formatNumber(refund) + " (50%)");
        }
        
        plugin.getDataManager().saveAll();
        return true;
    }
    
    private void startRecallVote() {
        currentPetition.setPhase(RecallPetition.RecallPhase.VOTING);
        
        long votingDays = plugin.getConfig().getLong("recall.voting-days", 3);
        currentPetition.setVotingEndTime(System.currentTimeMillis() + votingDays * 86400000L);
        
        String presidentName = Bukkit.getOfflinePlayer(currentPetition.getTargetId()).getName();
        
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<red>═══════════════════════════════════════");
        MessageUtils.broadcast("<dark_red>🗳 <red><bold>RECALL VOTE INITIATED</bold> <dark_red>🗳");
        MessageUtils.broadcast("<red>═══════════════════════════════════════");
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<yellow>The recall petition for <white>" + presidentName + " <yellow>has enough signatures!");
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<green>/dc recall vote keep <gray>- Keep the president");
        MessageUtils.broadcast("<red>/dc recall vote remove <gray>- Remove the president");
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<gray>Voting ends in: <white>" + MessageUtils.formatTime(votingDays * 86400000L));
        MessageUtils.broadcast("<gray>Removal requires: <white>60% vote to remove");
        MessageUtils.broadcast("");
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.0f);
        }
        
        plugin.getDataManager().saveAll();
    }
    
    public boolean castRecallVote(UUID voterId, boolean removePresident) {
        if (currentPetition == null) return false;
        if (currentPetition.getPhase() != RecallPetition.RecallPhase.VOTING) return false;
        
        if (currentPetition.hasVoted(voterId)) {
            return false;
        }
        
        currentPetition.addVote(voterId, removePresident);
        
        Player voter = Bukkit.getPlayer(voterId);
        if (voter != null) {
            String vote = removePresident ? "<red>REMOVE" : "<green>KEEP";
            MessageUtils.send(voter, "<yellow>You voted to " + vote + " <yellow>the president.");
            voter.playSound(voter.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
        
        plugin.getDataManager().saveAll();
        return true;
    }
    
    public void checkPetitionStatus() {
        if (currentPetition == null) return;

        switch (currentPetition.getPhase()) {
            case COLLECTING -> {
                if (currentPetition.isExpired()) {
                    // Failed to get enough signatures
                    failPetition("Not enough signatures collected in time.");
                }
            }
            case VOTING -> {
                if (System.currentTimeMillis() >= currentPetition.getVotingEndTime()) {
                    // Voting period ended, count votes
                    concludeRecallVote();
                }
            }
            case COMPLETED, FAILED -> {
                // End states, no action needed
            }
        }
    }
    
    private void concludeRecallVote() {
        int removeVotes = currentPetition.getRemoveVotes();
        int keepVotes = currentPetition.getKeepVotes();
        int totalVotes = removeVotes + keepVotes;
        
        if (totalVotes == 0) {
            failPetition("No votes were cast.");
            return;
        }
        
        double removePercentage = (double) removeVotes / totalVotes * 100;
        double requiredPercentage = plugin.getConfig().getDouble("recall.required-percentage", 60);
        
        String presidentName = Bukkit.getOfflinePlayer(currentPetition.getTargetId()).getName();
        
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<gold>═══════════════════════════════════════");
        MessageUtils.broadcast("<yellow>🗳 <gold><bold>RECALL VOTE RESULTS</bold> <yellow>🗳");
        MessageUtils.broadcast("<gold>═══════════════════════════════════════");
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<red>Remove: <white>" + removeVotes + " (" + String.format("%.1f", removePercentage) + "%)");
        MessageUtils.broadcast("<green>Keep: <white>" + keepVotes + " (" + String.format("%.1f", 100 - removePercentage) + "%)");
        MessageUtils.broadcast("");
        
        if (removePercentage >= requiredPercentage) {
            // Recall successful
            MessageUtils.broadcast("<red><bold>RECALL SUCCESSFUL!");
            MessageUtils.broadcast("<yellow>" + presidentName + " has been removed from office!");
            
            // Remove president
            plugin.getGovernmentManager().endPresidency("Recalled by popular vote");
            
            // Refund all deposits
            refundAllDeposits(true);
            
            currentPetition.setPhase(RecallPetition.RecallPhase.COMPLETED);
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
            }
        } else {
            // Recall failed
            MessageUtils.broadcast("<green><bold>RECALL FAILED!");
            MessageUtils.broadcast("<yellow>" + presidentName + " remains in office!");
            
            failPetition("Did not reach " + requiredPercentage + "% votes to remove.");
        }
        
        MessageUtils.broadcast("");
        
        currentPetition = null;
        plugin.getDataManager().setRecallPetition(null);
        plugin.getDataManager().saveAll();
    }
    
    private void failPetition(String reason) {
        String presidentName = Bukkit.getOfflinePlayer(currentPetition.getTargetId()).getName();
        
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<red>═══════════════════════════════════════");
        MessageUtils.broadcast("<gray>📜 Recall petition against " + presidentName + " has failed.");
        MessageUtils.broadcast("<gray>Reason: " + reason);
        MessageUtils.broadcast("<red>═══════════════════════════════════════");
        MessageUtils.broadcast("");
        
        // Forfeit all deposits (they go to treasury)
        refundAllDeposits(false);
        
        lastFailedRecall = System.currentTimeMillis();
        currentPetition.setPhase(RecallPetition.RecallPhase.FAILED);
        
        currentPetition = null;
        plugin.getDataManager().setRecallPetition(null);
        plugin.getDataManager().saveAll();
    }
    
    private void refundAllDeposits(boolean fullRefund) {
        if (currentPetition == null) return;
        
        double totalDeposits = 0;
        
        for (Map.Entry<UUID, Double> entry : currentPetition.getDeposits().entrySet()) {
            UUID signerId = entry.getKey();
            double deposit = entry.getValue();
            totalDeposits += deposit;
            
            if (fullRefund) {
                // Refund to signers
                OfflinePlayer signer = Bukkit.getOfflinePlayer(signerId);
                if (signer.isOnline()) {
                    plugin.getVaultHook().deposit(((Player) signer).getUniqueId(), deposit);
                    MessageUtils.send((Player) signer, "<green>Your recall petition deposit of $" + MessageUtils.formatNumber(deposit) + " has been refunded!");
                } else {
                    // Store deposit directly to vault for offline player
                    plugin.getVaultHook().deposit(signerId, deposit);
                }
            }
        }
        
        if (!fullRefund) {
            // Failed recall - deposits go to treasury
            plugin.getTreasuryManager().deposit(Treasury.TransactionType.FINE_INCOME, totalDeposits, "Failed recall petition deposits", null);
            MessageUtils.broadcast("<gray>$" + MessageUtils.formatNumber(totalDeposits) + " in petition deposits transferred to treasury.");
        }
    }
    
    public int getRequiredSignatures() {
        double percentage = plugin.getConfig().getDouble("recall.required-signature-percentage", 30) / 100.0;
        int activePlayers = getActivePlayerCount();
        return Math.max(1, (int) Math.ceil(activePlayers * percentage));
    }
    
    private int getActivePlayerCount() {
        // Count players who logged in within the last 7 days
        long cutoff = System.currentTimeMillis() - (7 * 86400000L);
        int count = 0;
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getLastLogin() > cutoff) {
                count++;
            }
        }
        return Math.max(count, Bukkit.getOnlinePlayers().size());
    }
    
    // Admin functions
    public void adminCancelPetition(String reason) {
        if (currentPetition == null) return;
        
        MessageUtils.broadcast("<red>The recall petition has been cancelled by an administrator.");
        MessageUtils.broadcast("<gray>Reason: " + reason);
        
        // Full refund
        refundAllDeposits(true);
        
        currentPetition = null;
        plugin.getDataManager().setRecallPetition(null);
        plugin.getDataManager().saveAll();
    }
    
    public void adminForceRecall(String reason) {
        Government gov = plugin.getDataManager().getGovernment();
        if (!gov.hasPresident()) return;
        
        String presidentName = Bukkit.getOfflinePlayer(gov.getPresidentUUID()).getName();
        
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<dark_red>═══════════════════════════════════════");
        MessageUtils.broadcast("<red><bold>PRESIDENTIAL IMPEACHMENT");
        MessageUtils.broadcast("<dark_red>═══════════════════════════════════════");
        MessageUtils.broadcast("");
        MessageUtils.broadcast("<yellow>" + presidentName + " has been removed from office by administration.");
        MessageUtils.broadcast("<gray>Reason: " + reason);
        MessageUtils.broadcast("");
        
        plugin.getGovernmentManager().endPresidency("Impeached: " + reason);
        
        // Cancel any active petition
        if (currentPetition != null) {
            refundAllDeposits(true);
            currentPetition = null;
            plugin.getDataManager().setRecallPetition(null);
        }
        
        plugin.getDataManager().saveAll();
    }
    
    // Getters
    public RecallPetition getCurrentPetition() {
        return currentPetition;
    }
    
    public boolean hasPetitionActive() {
        return currentPetition != null && !currentPetition.isExpired();
    }
    
    public long getCooldownRemaining() {
        long cooldownMs = plugin.getConfig().getLong("recall.cooldown-days", 15) * 86400000L;
        long remaining = cooldownMs - (System.currentTimeMillis() - lastFailedRecall);
        return Math.max(0, remaining);
    }
    
    public void loadPetition() {
        currentPetition = plugin.getDataManager().getRecallPetition();
        if (currentPetition != null && currentPetition.isExpired()) {
            // Handle expired petition on load
            checkPetitionStatus();
        }
    }
}
