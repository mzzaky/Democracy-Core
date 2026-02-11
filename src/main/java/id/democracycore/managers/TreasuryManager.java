package id.democracycore.managers;

import id.democracycore.DemocracyCore;
import id.democracycore.models.Treasury;
import id.democracycore.models.Treasury.Transaction;
import id.democracycore.models.Treasury.TransactionType;

import java.util.List;
import java.util.UUID;

public class TreasuryManager {
    
    private final DemocracyCore plugin;
    
    public TreasuryManager(DemocracyCore plugin) {
        this.plugin = plugin;
    }
    
    public Treasury getTreasury() {
        return plugin.getDataManager().getTreasury();
    }
    
    public double getBalance() {
        return getTreasury().getBalance();
    }
    
    public boolean canAfford(double amount) {
        return getTreasury().canAfford(amount);
    }
    
    public boolean deposit(TransactionType type, double amount, String description, UUID player) {
        return getTreasury().deposit(type, amount, description, player);
    }
    
    public boolean withdraw(TransactionType type, double amount, String description, UUID player) {
        return getTreasury().withdraw(type, amount, description, player);
    }
    
    public void addTax(double originalAmount, UUID player) {
        double taxRate = plugin.getConfig().getDouble("treasury.transaction-tax", 0.05);
        double tax = originalAmount * taxRate;
        deposit(TransactionType.TAX_INCOME, tax, "Transaction tax", player);
    }
    
    public List<Transaction> getRecentTransactions(int count) {
        return getTreasury().getRecentTransactions(count);
    }
    
    public double getTotalIncome() {
        return getTreasury().getTotalIncome();
    }
    
    public double getTotalExpenses() {
        return getTreasury().getTotalExpenses();
    }
}
