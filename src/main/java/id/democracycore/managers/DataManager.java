package id.democracycore.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import id.democracycore.DemocracyCore;
import id.democracycore.models.ArenaSession;
import id.democracycore.models.CabinetDecision;
import id.democracycore.models.Election;
import id.democracycore.models.ExecutiveOrder;
import id.democracycore.models.Government;
import id.democracycore.models.PlayerData;
import id.democracycore.models.PresidentHistory;
import id.democracycore.models.RecallPetition;
import id.democracycore.models.Treasury;

public class DataManager {
    
    private final DemocracyCore plugin;
    private final Gson gson;
    private final File dataFolder;
    
    private Government government;
    private Election election;
    private Treasury treasury;
    private PresidentHistory presidentHistory;
    private Map<UUID, PlayerData> playerDataMap;
    private List<ExecutiveOrder> activeOrders;
    private List<CabinetDecision> activeDecisions;
    private ArenaSession arenaSession;
    private RecallPetition recallPetition;
    private long lastExecutiveOrderTime;
    private int gamesThisTerm;
    
    public DataManager(DemocracyCore plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        this.government = new Government();
        this.election = new Election();
        this.treasury = new Treasury();
        this.presidentHistory = new PresidentHistory();
        this.playerDataMap = new HashMap<>();
        this.activeOrders = new ArrayList<>();
        this.activeDecisions = new ArrayList<>();
    }
    
    public void loadAll() {
        loadGovernment();
        loadElection();
        loadTreasury();
        loadPresidentHistory();
        loadPlayerData();
        loadActiveOrders();
        loadActiveDecisions();
        loadArenaSession();
        loadRecallPetition();
        loadMiscData();
    }
    
    public void saveAll() {
        saveGovernment();
        saveElection();
        saveTreasury();
        savePresidentHistory();
        savePlayerData();
        saveActiveOrders();
        saveActiveDecisions();
        saveArenaSession();
        saveRecallPetition();
        saveMiscData();
    }
    
    // Government
    private void loadGovernment() {
        File file = new File(dataFolder, "government.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                government = gson.fromJson(reader, Government.class);
                if (government == null) government = new Government();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load government data: " + e.getMessage());
                government = new Government();
            }
        }
    }
    
    public void saveGovernment() {
        File file = new File(dataFolder, "government.json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(government, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save government data: " + e.getMessage());
        }
    }
    
    // Election
    private void loadElection() {
        File file = new File(dataFolder, "election.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                election = gson.fromJson(reader, Election.class);
                if (election == null) election = new Election();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load election data: " + e.getMessage());
                election = new Election();
            }
        }
    }
    
    private void saveElection() {
        File file = new File(dataFolder, "election.json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(election, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save election data: " + e.getMessage());
        }
    }
    
    // Treasury
    private void loadTreasury() {
        File file = new File(dataFolder, "treasury.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                treasury = gson.fromJson(reader, Treasury.class);
                if (treasury == null) treasury = new Treasury();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load treasury data: " + e.getMessage());
                treasury = new Treasury();
            }
        }
    }
    
    private void saveTreasury() {
        File file = new File(dataFolder, "treasury.json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(treasury, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save treasury data: " + e.getMessage());
        }
    }
    
    // President History
    private void loadPresidentHistory() {
        File file = new File(dataFolder, "history.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                presidentHistory = gson.fromJson(reader, PresidentHistory.class);
                if (presidentHistory == null) presidentHistory = new PresidentHistory();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load president history: " + e.getMessage());
                presidentHistory = new PresidentHistory();
            }
        }
    }
    
    private void savePresidentHistory() {
        File file = new File(dataFolder, "history.json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(presidentHistory, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save president history: " + e.getMessage());
        }
    }
    
    // Player Data
    private void loadPlayerData() {
        File file = new File(dataFolder, "players.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                PlayerData[] players = gson.fromJson(reader, PlayerData[].class);
                if (players != null) {
                    for (PlayerData data : players) {
                        playerDataMap.put(data.getUuid(), data);
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load player data: " + e.getMessage());
            }
        }
    }
    
    private void savePlayerData() {
        File file = new File(dataFolder, "players.json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(playerDataMap.values().toArray(new PlayerData[0]), writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save player data: " + e.getMessage());
        }
    }
    
    // Active Orders
    private void loadActiveOrders() {
        File file = new File(dataFolder, "orders.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                ExecutiveOrder[] orders = gson.fromJson(reader, ExecutiveOrder[].class);
                if (orders != null) {
                    activeOrders = new ArrayList<>(Arrays.asList(orders));
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load executive orders: " + e.getMessage());
            }
        }
    }
    
    private void saveActiveOrders() {
        File file = new File(dataFolder, "orders.json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(activeOrders.toArray(new ExecutiveOrder[0]), writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save executive orders: " + e.getMessage());
        }
    }
    
    // Active Decisions
    private void loadActiveDecisions() {
        File file = new File(dataFolder, "decisions.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                CabinetDecision[] decisions = gson.fromJson(reader, CabinetDecision[].class);
                if (decisions != null) {
                    activeDecisions = new ArrayList<>(Arrays.asList(decisions));
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load cabinet decisions: " + e.getMessage());
            }
        }
    }
    
    private void saveActiveDecisions() {
        File file = new File(dataFolder, "decisions.json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(activeDecisions.toArray(new CabinetDecision[0]), writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save cabinet decisions: " + e.getMessage());
        }
    }
    
    // Arena Session
    private void loadArenaSession() {
        File file = new File(dataFolder, "arena.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                arenaSession = gson.fromJson(reader, ArenaSession.class);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load arena session: " + e.getMessage());
            }
        }
    }
    
    private void saveArenaSession() {
        if (arenaSession == null) return;
        File file = new File(dataFolder, "arena.json");
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(arenaSession, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save arena session: " + e.getMessage());
        }
    }
    
    // Recall Petition
    private void loadRecallPetition() {
        File file = new File(dataFolder, "recall.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                recallPetition = gson.fromJson(reader, RecallPetition.class);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load recall petition: " + e.getMessage());
            }
        }
    }
    
    private void saveRecallPetition() {
        File file = new File(dataFolder, "recall.json");
        if (recallPetition == null) {
            if (file.exists()) file.delete();
            return;
        }
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(recallPetition, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save recall petition: " + e.getMessage());
        }
    }
    
    // Misc Data
    private void loadMiscData() {
        File file = new File(dataFolder, "misc.json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                MiscData data = gson.fromJson(reader, MiscData.class);
                if (data != null) {
                    lastExecutiveOrderTime = data.lastExecutiveOrderTime;
                    gamesThisTerm = data.gamesThisTerm;
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load misc data: " + e.getMessage());
            }
        }
    }
    
    private void saveMiscData() {
        File file = new File(dataFolder, "misc.json");
        MiscData data = new MiscData();
        data.lastExecutiveOrderTime = lastExecutiveOrderTime;
        data.gamesThisTerm = gamesThisTerm;
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save misc data: " + e.getMessage());
        }
    }
    
    private static class MiscData {
        long lastExecutiveOrderTime;
        int gamesThisTerm;
    }
    
    // Getters
    public Government getGovernment() { return government; }
    public Election getElection() { return election; }
    public Treasury getTreasury() { return treasury; }
    public PresidentHistory getPresidentHistory() { return presidentHistory; }
    public List<ExecutiveOrder> getActiveOrders() { return activeOrders; }
    public List<CabinetDecision> getActiveDecisions() { return activeDecisions; }
    public ArenaSession getArenaSession() { return arenaSession; }
    public void setArenaSession(ArenaSession arenaSession) { this.arenaSession = arenaSession; }
    public RecallPetition getRecallPetition() { return recallPetition; }
    public void setRecallPetition(RecallPetition recallPetition) { this.recallPetition = recallPetition; }
    public long getLastExecutiveOrderTime() { return lastExecutiveOrderTime; }
    public void setLastExecutiveOrderTime(long time) { this.lastExecutiveOrderTime = time; }
    public int getGamesThisTerm() { return gamesThisTerm; }
    public void setGamesThisTerm(int games) { this.gamesThisTerm = games; }
    
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }
    
    public PlayerData getOrCreatePlayerData(UUID uuid, String name) {
        return playerDataMap.computeIfAbsent(uuid, k -> new PlayerData(uuid, name));
    }
    
    public Collection<PlayerData> getAllPlayerData() {
        return playerDataMap.values();
    }
    
    public int getActivePlayerCount() {
        long threshold = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000); // 7 days
        return (int) playerDataMap.values().stream()
            .filter(p -> p.getLastSeen() > threshold)
            .count();
    }

    public List<PresidentHistory.PresidentRecord> getAllPresidentHistory() {
        return presidentHistory.getRecords();
    }

    // Reset methods for DemocracyCommand
    public void resetGovernment() {
        government = new Government();
        saveGovernment();
    }

    public void resetElection() {
        election = new Election();
        saveElection();
    }

    public void resetTreasury() {
        treasury = new Treasury();
        saveTreasury();
    }
}
