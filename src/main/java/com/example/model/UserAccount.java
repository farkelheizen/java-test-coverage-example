package com.example.model;

import java.time.LocalDateTime;

public class UserAccount {
    private String accountId;
    private String username;
    private String passwordHash;
    private LocalDateTime lastLogin;
    private boolean isActive;
    private int failedLoginAttempts;
    private boolean isLocked;

    public UserAccount() {}

    public UserAccount(String accountId, String username, String passwordHash,
                       LocalDateTime lastLogin, boolean isActive, int failedLoginAttempts, boolean isLocked) {
        this.accountId = accountId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
        this.failedLoginAttempts = failedLoginAttempts;
        this.isLocked = isLocked;
    }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }
}
