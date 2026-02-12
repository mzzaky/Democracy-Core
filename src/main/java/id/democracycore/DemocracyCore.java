package id.democracycore;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import id.democracycore.commands.DemocracyCommand;
import id.democracycore.gui.GUIListener;
import id.democracycore.listeners.ArenaListener;
import id.democracycore.listeners.ChatListener;
import id.democracycore.listeners.DamageListener;
import id.democracycore.listeners.EconomyListener;
import id.democracycore.listeners.PlayerListener;
import id.democracycore.managers.ArenaManager;
import id.democracycore.managers.BuffManager;
import id.democracycore.managers.CabinetManager;
import id.democracycore.managers.DataManager;
import id.democracycore.managers.ElectionManager;
import id.democracycore.managers.ExecutiveOrderManager;
import id.democracycore.managers.GovernmentManager;
import id.democracycore.managers.RecallManager;
import id.democracycore.managers.TreasuryManager;
import id.democracycore.utils.MessageUtils;
import id.democracycore.utils.VaultHook;

public class DemocracyCore extends JavaPlugin {

    private static DemocracyCore instance;

    private DataManager dataManager;
    private GovernmentManager governmentManager;
    private ElectionManager electionManager;
    private TreasuryManager treasuryManager;
    private ExecutiveOrderManager executiveOrderManager;
    private CabinetManager cabinetManager;
    private ArenaManager arenaManager;
    private BuffManager buffManager;
    private RecallManager recallManager;
    private VaultHook vaultHook;
    private GUIListener guiListener;
    private YamlConfiguration guiConfig;
    private YamlConfiguration languageConfig;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        if (!new File(getDataFolder(), "language.yml").exists()) {
            saveResource("language.yml", false);
        }
        languageConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "language.yml"));

        if (!new File(getDataFolder(), "gui.yml").exists()) {
            saveResource("gui.yml", false);
        }
        MessageUtils.loadLanguage();
        guiConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "gui.yml"));
        id.democracycore.gui.MainMenuGUI.loadGUITitles(this);

        // Initialize Vault
        vaultHook = new VaultHook(this);
        if (!vaultHook.setupEconomy()) {
            getLogger().severe("Vault not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize managers
        dataManager = new DataManager(this);
        treasuryManager = new TreasuryManager(this);
        buffManager = new BuffManager(this);
        governmentManager = new GovernmentManager(this);
        electionManager = new ElectionManager(this);
        executiveOrderManager = new ExecutiveOrderManager(this);
        cabinetManager = new CabinetManager(this);
        arenaManager = new ArenaManager(this);
        recallManager = new RecallManager(this);

        // Load data
        dataManager.loadAll();

        // Register commands
        getCommand("democracycore").setExecutor(new DemocracyCommand(this));
        getCommand("democracycore").setTabCompleter(new DemocracyCommand(this));

        // Register listeners
        registerListeners();

        // Start scheduled tasks
        startScheduledTasks();

        getLogger().info("DemocracyCore has been enabled!");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.saveAll();
        }
        getLogger().info("DemocracyCore has been disabled!");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new EconomyListener(this), this);
        guiListener = new GUIListener(this);
        getServer().getPluginManager().registerEvents(guiListener, this);
        getServer().getPluginManager().registerEvents(new ArenaListener(this), this);
    }

    private void startScheduledTasks() {
        // Check election phases every minute
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            electionManager.checkPhaseTransitions();
        }, 20L * 60, 20L * 60);

        // Apply buffs every 30 seconds
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            buffManager.refreshAllBuffs();
        }, 20L * 30, 20L * 30);

        // Check daily rewards - Moved to Manual Claim in GUI
        // Bukkit.getScheduler().runTaskTimer(this, () -> {
        // governmentManager.checkDailyRewards();
        // }, 20L * 60 * 60, 20L * 60 * 60);

        // Check president activity every hour
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            governmentManager.checkPresidentActivity();
        }, 20L * 60 * 60, 20L * 60 * 60);

        // Broadcast campaign messages every 5 minutes during campaign phase
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            electionManager.broadcastCampaignMessages();
        }, 20L * 60 * 5, 20L * 60 * 5);

        // Check executive order expirations every minute
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            executiveOrderManager.checkExpirations();
        }, 20L * 60, 20L * 60);

        // Check cabinet decision expirations every minute
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            cabinetManager.checkExpiredDecisions();
        }, 20L * 60, 20L * 60);

        // Save data every 5 minutes
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            dataManager.saveAll();
        }, 20L * 60 * 5, 20L * 60 * 5);
    }

    public static DemocracyCore getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public GovernmentManager getGovernmentManager() {
        return governmentManager;
    }

    public ElectionManager getElectionManager() {
        return electionManager;
    }

    public TreasuryManager getTreasuryManager() {
        return treasuryManager;
    }

    public ExecutiveOrderManager getExecutiveOrderManager() {
        return executiveOrderManager;
    }

    public CabinetManager getCabinetManager() {
        return cabinetManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public BuffManager getBuffManager() {
        return buffManager;
    }

    public RecallManager getRecallManager() {
        return recallManager;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public GUIListener getGUIListener() {
        return guiListener;
    }

    public String getPrefix() {
        return getConfig().getString("general.prefix", "&6[DemocracyCore]&r");
    }

    public void reloadLanguage() {
        languageConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "language.yml"));
        MessageUtils.reloadLanguage();
    }

    public YamlConfiguration getLanguageConfig() {
        return languageConfig;
    }

    public YamlConfiguration getGUIConfig() {
        return guiConfig;
    }

    public void reloadGUI() {
        guiConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "gui.yml"));
        id.democracycore.gui.MainMenuGUI.loadGUITitles(this);
    }
}
