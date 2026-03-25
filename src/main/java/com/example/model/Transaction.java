package com.example.model;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private BankAccount fromAccount;
    private BankAccount toAccount;
    private double amount;
    private LocalDateTime timestamp;
    private String description;
    private boolean isSuccessful;

    public Transaction() {}

    public Transaction(String transactionId, BankAccount fromAccount, BankAccount toAccount,
                       double amount, LocalDateTime timestamp, String description, boolean isSuccessful) {
        this.transactionId = transactionId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
        this.isSuccessful = isSuccessful;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public BankAccount getFromAccount() { return fromAccount; }
    public void setFromAccount(BankAccount fromAccount) { this.fromAccount = fromAccount; }
    public BankAccount getToAccount() { return toAccount; }
    public void setToAccount(BankAccount toAccount) { this.toAccount = toAccount; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isSuccessful() { return isSuccessful; }
    public void setSuccessful(boolean successful) { isSuccessful = successful; }
}
