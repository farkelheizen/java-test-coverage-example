package com.example.service;

import com.example.model.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountServiceTest {

    private UserAccountService userAccountService;

    @BeforeEach
    void setUp() {
        userAccountService = new UserAccountService();
    }

    private UserAccount buildAccount(String username, String passwordHash) {
        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPasswordHash(passwordHash);
        return account;
    }

    // ── createAccount ─────────────────────────────────────────────────────────

    @Test
    void createAccount_validAccount_setsActiveTrue() {
        UserAccount account = buildAccount("alice", "hash123");
        UserAccount result = userAccountService.createAccount(account);
        assertTrue(result.isActive());
    }

    @Test
    void createAccount_validAccount_setsLockedFalse() {
        UserAccount account = buildAccount("alice", "hash123");
        UserAccount result = userAccountService.createAccount(account);
        assertFalse(result.isLocked());
    }

    @Test
    void createAccount_validAccount_setsFailedLoginAttemptsToZero() {
        UserAccount account = buildAccount("alice", "hash123");
        account.setFailedLoginAttempts(5);
        UserAccount result = userAccountService.createAccount(account);
        assertEquals(0, result.getFailedLoginAttempts());
    }

    @Test
    void createAccount_returnsSameAccountObject() {
        UserAccount account = buildAccount("bob123", "hash456");
        assertSame(account, userAccountService.createAccount(account));
    }

    @Test
    void createAccount_nullUsername_throwsIllegalArgumentException() {
        UserAccount account = buildAccount(null, "hash");
        assertThrows(IllegalArgumentException.class,
                () -> userAccountService.createAccount(account));
    }

    @Test
    void createAccount_tooShortUsername_throwsIllegalArgumentException() {
        UserAccount account = buildAccount("ab", "hash");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userAccountService.createAccount(account));
        assertTrue(ex.getMessage().contains("at least 3"));
    }

    @Test
    void createAccount_usernameWithSpace_throwsIllegalArgumentException() {
        UserAccount account = buildAccount("al ice", "hash");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userAccountService.createAccount(account));
        assertTrue(ex.getMessage().contains("spaces"));
    }

    @Test
    void createAccount_nullPasswordHash_throwsIllegalArgumentException() {
        UserAccount account = buildAccount("alice", null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userAccountService.createAccount(account));
        assertTrue(ex.getMessage().contains("Password hash"));
    }

    @Test
    void createAccount_exactlyThreeCharUsername_succeeds() {
        UserAccount account = buildAccount("abc", "hash");
        assertDoesNotThrow(() -> userAccountService.createAccount(account));
    }

    // ── deactivateAccount ─────────────────────────────────────────────────────

    @Test
    void deactivateAccount_setsActiveFalse() {
        UserAccount account = buildAccount("alice", "hash");
        account.setActive(true);
        userAccountService.deactivateAccount(account);
        assertFalse(account.isActive());
    }

    // ── lockAccount ───────────────────────────────────────────────────────────

    @Test
    void lockAccount_validReason_setsLockedTrue() {
        UserAccount account = buildAccount("alice", "hash");
        userAccountService.lockAccount(account, "Suspicious activity");
        assertTrue(account.isLocked());
    }

    @Test
    void lockAccount_nullReason_throwsIllegalArgumentException() {
        UserAccount account = buildAccount("alice", "hash");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userAccountService.lockAccount(account, null));
        assertTrue(ex.getMessage().contains("Lock reason"));
    }

    @Test
    void lockAccount_emptyReason_throwsIllegalArgumentException() {
        UserAccount account = buildAccount("alice", "hash");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userAccountService.lockAccount(account, ""));
        assertTrue(ex.getMessage().contains("Lock reason"));
    }

    @Test
    void lockAccount_nullReason_stillSetsLockedTrue() {
        // The lock is set before the reason check – account IS locked even on exception
        UserAccount account = buildAccount("alice", "hash");
        assertThrows(IllegalArgumentException.class,
                () -> userAccountService.lockAccount(account, null));
        assertTrue(account.isLocked());
    }
}
