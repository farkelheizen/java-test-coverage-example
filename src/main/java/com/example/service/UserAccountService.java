package com.example.service;

import com.example.model.UserAccount;

public class UserAccountService {

    public UserAccount createAccount(UserAccount account) {
        if (account.getUsername() == null || account.getUsername().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        if (account.getUsername().contains(" ")) {
            throw new IllegalArgumentException("Username cannot contain spaces");
        }
        if (account.getPasswordHash() == null) {
            throw new IllegalArgumentException("Password hash cannot be null");
        }
        account.setActive(true);
        account.setLocked(false);
        account.setFailedLoginAttempts(0);
        return account;
    }

    public void deactivateAccount(UserAccount account) {
        account.setActive(false);
    }

    public void lockAccount(UserAccount account, String reason) {
        account.setLocked(true);
        // In a real system we would log the reason
        if (reason == null || reason.isEmpty()) {
            throw new IllegalArgumentException("Lock reason cannot be null or empty");
        }
    }
}
