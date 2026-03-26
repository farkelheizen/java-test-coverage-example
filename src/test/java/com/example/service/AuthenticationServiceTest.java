package com.example.service;

import com.example.model.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService();
    }

    // --- login ---

    @Test
    void login_throwsWhenAccountIsLocked() {
        UserAccount account = new UserAccount();
        account.setLocked(true);
        account.setActive(true);
        assertThrows(IllegalStateException.class, () -> authenticationService.login(account, "anyPassword"));
    }

    @Test
    void login_throwsWhenAccountIsNotActive() {
        UserAccount account = new UserAccount();
        account.setLocked(false);
        account.setActive(false);
        assertThrows(IllegalStateException.class, () -> authenticationService.login(account, "anyPassword"));
    }

    @Test
    void login_returnsTrueAndResetsAttemptsOnCorrectPassword() {
        UserAccount account = new UserAccount();
        account.setLocked(false);
        account.setActive(true);
        account.setPasswordHash("secret");
        account.setFailedLoginAttempts(3);

        boolean result = authenticationService.login(account, "secret");

        assertTrue(result);
        assertEquals(0, account.getFailedLoginAttempts());
        assertNotNull(account.getLastLogin());
    }

    @Test
    void login_returnsFalseAndIncrementsAttemptsOnWrongPassword() {
        UserAccount account = new UserAccount();
        account.setLocked(false);
        account.setActive(true);
        account.setPasswordHash("secret");
        account.setFailedLoginAttempts(0);

        boolean result = authenticationService.login(account, "wrong");

        assertFalse(result);
        assertEquals(1, account.getFailedLoginAttempts());
        assertFalse(account.isLocked());
    }

    @Test
    void login_locksAccountAfterFiveFailedAttempts() {
        UserAccount account = new UserAccount();
        account.setLocked(false);
        account.setActive(true);
        account.setPasswordHash("secret");
        account.setFailedLoginAttempts(4);

        authenticationService.login(account, "wrong");

        assertTrue(account.isLocked());
        assertEquals(5, account.getFailedLoginAttempts());
    }

    @Test
    void login_returnsFalseWhenPasswordHashIsNull() {
        UserAccount account = new UserAccount();
        account.setLocked(false);
        account.setActive(true);
        account.setPasswordHash(null);
        account.setFailedLoginAttempts(0);

        boolean result = authenticationService.login(account, "anyPassword");

        assertFalse(result);
    }

    @Test
    void login_doesNotLockWhenAttemptsLessThanMax() {
        UserAccount account = new UserAccount();
        account.setLocked(false);
        account.setActive(true);
        account.setPasswordHash("secret");
        account.setFailedLoginAttempts(2);

        authenticationService.login(account, "wrong");

        assertFalse(account.isLocked());
        assertEquals(3, account.getFailedLoginAttempts());
    }

    // --- logout ---

    @Test
    void logout_updatesLastLogin() {
        UserAccount account = new UserAccount();
        account.setLastLogin(null);

        authenticationService.logout(account);

        assertNotNull(account.getLastLogin());
    }

    // --- resetPassword ---

    @Test
    void resetPassword_throwsWhenPasswordDoesNotMeetComplexity() {
        UserAccount account = new UserAccount();
        // too short
        assertThrows(IllegalArgumentException.class,
                () -> authenticationService.resetPassword(account, "short"));
    }

    @Test
    void resetPassword_throwsWhenPasswordIsNull() {
        UserAccount account = new UserAccount();
        assertThrows(IllegalArgumentException.class,
                () -> authenticationService.resetPassword(account, null));
    }

    @Test
    void resetPassword_throwsWhenPasswordMissingSpecialChar() {
        UserAccount account = new UserAccount();
        // has upper, lower, digit, but no special char
        assertThrows(IllegalArgumentException.class,
                () -> authenticationService.resetPassword(account, "Password1"));
    }

    @Test
    void resetPassword_successfullySetsHashAndUnlocksAccount() {
        UserAccount account = new UserAccount();
        account.setLocked(true);
        account.setFailedLoginAttempts(5);

        // Valid password: upper + lower + digit + special
        authenticationService.resetPassword(account, "Secure1@pass");

        assertEquals("Secure1@pass", account.getPasswordHash());
        assertEquals(0, account.getFailedLoginAttempts());
        assertFalse(account.isLocked());
    }
}
