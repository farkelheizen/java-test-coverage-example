package com.example.model;

public class BankAccount {
    private String accountId;
    private String accountNumber;
    private String bankName;
    private String routingNumber;
    private String accountHolderName;
    private double balance;

    public BankAccount() {}

    public BankAccount(String accountId, String accountNumber, String bankName,
                       String routingNumber, String accountHolderName, double balance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.routingNumber = routingNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getRoutingNumber() { return routingNumber; }
    public void setRoutingNumber(String routingNumber) { this.routingNumber = routingNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
