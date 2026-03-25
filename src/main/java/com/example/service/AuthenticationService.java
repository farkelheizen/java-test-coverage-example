package com.example.service;

import com.example.model.UserAccount;
import java.time.LocalDateTime;

public class AuthenticationService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    public boolean login(UserAccount account, String password) {
        if (account.isLocked()) {
            throw new IllegalStateException("Account is locked");
        }
        if (!account.isActive()) {
            throw new IllegalStateException("Account is not active");
        }
        boolean passwordCorrect = account.getPasswordHash() != null
                && account.getPasswordHash().equals(password);
        if (passwordCorrect) {
            account.setFailedLoginAttempts(0);
            account.setLastLogin(LocalDateTime.now());
            return true;
        } else {
            int attempts = account.getFailedLoginAttempts() + 1;
            account.setFailedLoginAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                account.setLocked(true);
            }
            return false;
        }
    }

    public void logout(UserAccount account) {
        account.setLastLogin(LocalDateTime.now());
    }

    public void resetPassword(UserAccount account, String newPassword) {
        ValidationService validationService = new ValidationService();
        if (!validationService.validatePassword(newPassword)) {
            throw new IllegalArgumentException("Password does not meet complexity requirements");
        }
        account.setPasswordHash(newPassword);
        account.setFailedLoginAttempts(0);
        account.setLocked(false);
    }
}
