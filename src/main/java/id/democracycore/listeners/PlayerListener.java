package id.democracycore.listeners;

import id.democracycore.DemocracyCore;
import id.democracycore.models.Government;
import id.democracycore.models.PlayerData;
import id.democracycore.models.TaxRecord;
import id.democracycore.utils.MessageUtils;
import id.democracycore.utils.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerListener implements Listener {

    private final DemocracyCore plugin;

    public PlayerListener(DemocracyCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Initialize or load player data
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        data.updateLastSeen();

        // Apply buffs if president or cabinet
        plugin.getBuffManager().handlePlayerJoin(player);

        // Apply any active cabinet decision effects
        plugin.getCabinetManager().applyEffectsToPlayer(player);

        // Check for pending rewards
        double pendingRewards = data.getPendingReward();
        if (pendingRewards > 0) {
            plugin.getVaultHook().deposit(player.getUniqueId(), pendingRewards);
            data.clearPendingReward();
            MessageUtils.send(player,
                    "<gold>You received $" + MessageUtils.formatNumber(pendingRewards) + " in pending rewards!");
        }

        // Show government info
        Government gov = plugin.getDataManager().getGovernment();
        if (gov.hasPresident()) {
            String presidentName = Bukkit.getOfflinePlayer(gov.getPresidentUUID()).getName();
            MessageUtils.send(player, "<gray>Current President: <gold>" + presidentName);
        }

        // Check for active election
        if (plugin.getElectionManager().isElectionActive()) {
            MessageUtils.send(player,
                    "<yellow>📢 An election is currently in progress! Use <white>/dc election <yellow>for info.");
        }

        // Check for active arena
        if (plugin.getArenaManager().isArenaActive()) {
            MessageUtils.send(player,
                    "<red>⚔ <yellow>Presidential Arena Games are active! Use <white>/dc arena join <yellow>to participate!");
        }

        // Check for active recall petition
        if (plugin.getRecallManager().hasPetitionActive()) {
            MessageUtils.send(player, "<red>📜 A recall petition is active! Use <white>/dc recall <red>for info.");
        }

        // Check for outstanding tax debt
        if (plugin.getTaxManager().isEnabled()) {
            TaxRecord.PlayerTaxData taxData = plugin.getTaxManager().getTaxRecord()
                    .getPlayerTaxData(player.getUniqueId().toString());
            if (taxData != null && taxData.getOutstandingDebt() > 0) {
                MessageUtils.send(player, "<red>💲 You have an outstanding tax debt of <gold>$" +
                        MessageUtils.formatNumber(taxData.getOutstandingDebt()) +
                        "</gold>! <red>Use <white>/dc tax pay <red>to settle your debt.");
            }
        }

        plugin.getDataManager().saveAll();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Calculate and accumulate playtime for this session
        PlayerData data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        if (data.getLastSeen() > 0) {
            long sessionDuration = System.currentTimeMillis() - data.getLastSeen();
            // Cap at 24 hours per session as a safeguard against corrupt data
            long maxSession = 24L * 60 * 60 * 1000;
            data.addPlaytime(Math.min(sessionDuration, maxSession));
        }

        // Update last seen
        data.updateLastSeen();

        // Handle buff cleanup
        plugin.getBuffManager().handlePlayerQuit(player);

        // Leave arena if in it
        if (plugin.getArenaManager().isInArena(player.getUniqueId())) {
            plugin.getArenaManager().leaveArena(player.getUniqueId());
        }

        plugin.getDataManager().saveAll();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        // Handle arena death
        if (plugin.getArenaManager().isInArena(player.getUniqueId())) {
            // Keep inventory in arena
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.getDrops().clear();
            event.setDroppedExp(0);

            if (killer != null && plugin.getArenaManager().isInArena(killer.getUniqueId())) {
                plugin.getArenaManager().handleKill(killer, player);
            }

            plugin.getArenaManager().handleDeath(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // Reapply buffs after respawn
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getBuffManager().handlePlayerJoin(player);
            plugin.getCabinetManager().applyEffectsToPlayer(player);
        }, 5L);
    }
}
