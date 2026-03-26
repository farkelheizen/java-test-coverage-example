package com.example.service;

import com.example.model.AuditLog;
import com.example.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @InjectMocks
    private AuditService auditService;

    // ------------------------------------------------------------------ logAction

    @Test
    void logAction_returnsAuditLogWithCorrectFields() {
        AuditLog log = auditService.logAction("ORDER", 42L, "CREATE", 7L, null, "newValue");

        assertNotNull(log);
        assertEquals("ORDER", log.getEntityType());
        assertEquals(42L, log.getEntityId());
        assertEquals("CREATE", log.getAction());
        assertEquals(7L, log.getPerformedBy());
        assertNull(log.getOldValue());
        assertEquals("newValue", log.getNewValue());
    }

    @Test
    void logAction_setsNonNullLogId() {
        AuditLog log = auditService.logAction("PRODUCT", 1L, "UPDATE", 2L, "old", "new");
        assertNotNull(log.getLogId());
        assertFalse(log.getLogId().isBlank());
    }

    @Test
    void logAction_setsTimestampNearNow() {
        LocalDateTime before = LocalDateTime.now();
        AuditLog log = auditService.logAction("USER", 10L, "DELETE", 1L, "old", null);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(log.getTimestamp());
        assertFalse(log.getTimestamp().isBefore(before));
        assertFalse(log.getTimestamp().isAfter(after));
    }

    @Test
    void logAction_eachCallProducesUniqueLogId() {
        AuditLog log1 = auditService.logAction("X", 1L, "A", 1L, null, null);
        AuditLog log2 = auditService.logAction("X", 1L, "A", 1L, null, null);
        assertNotEquals(log1.getLogId(), log2.getLogId());
    }

    // ------------------------------------------------------------------ getAuditHistory

    @Test
    void getAuditHistory_returnsMatchingLogs() {
        AuditLog a = buildLog("ORDER", 1L);
        AuditLog b = buildLog("ORDER", 2L);
        AuditLog c = buildLog("PRODUCT", 1L);

        List<AuditLog> result = auditService.getAuditHistory("ORDER", 1L, Arrays.asList(a, b, c));

        assertEquals(1, result.size());
        assertSame(a, result.get(0));
    }

    @Test
    void getAuditHistory_returnsEmptyWhenNoMatch() {
        AuditLog a = buildLog("ORDER", 5L);
        List<AuditLog> result = auditService.getAuditHistory("PRODUCT", 5L, Collections.singletonList(a));
        assertTrue(result.isEmpty());
    }

    @Test
    void getAuditHistory_emptyInputReturnsEmpty() {
        List<AuditLog> result = auditService.getAuditHistory("ORDER", 1L, Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void getAuditHistory_returnsMultipleMatches() {
        AuditLog a = buildLog("ORDER", 3L);
        AuditLog b = buildLog("ORDER", 3L);
        AuditLog c = buildLog("ORDER", 99L);

        List<AuditLog> result = auditService.getAuditHistory("ORDER", 3L, Arrays.asList(a, b, c));

        assertEquals(2, result.size());
    }

    // ------------------------------------------------------------------ isActionAllowed

    @Test
    void isActionAllowed_activeNotLockedUserCanPerformRead() {
        UserAccount account = buildAccount(true, false);
        assertTrue(auditService.isActionAllowed("READ", account));
    }

    @Test
    void isActionAllowed_inactiveUserDenied() {
        UserAccount account = buildAccount(false, false);
        assertFalse(auditService.isActionAllowed("READ", account));
    }

    @Test
    void isActionAllowed_lockedUserDenied() {
        UserAccount account = buildAccount(true, true);
        assertFalse(auditService.isActionAllowed("READ", account));
    }

    @Test
    void isActionAllowed_deleteRequiresActiveAndNotLocked() {
        UserAccount active = buildAccount(true, false);
        assertTrue(auditService.isActionAllowed("DELETE", active));
    }

    @Test
    void isActionAllowed_deleteWithLockedAccountDenied() {
        UserAccount locked = buildAccount(true, true);
        assertFalse(auditService.isActionAllowed("DELETE", locked));
    }

    @Test
    void isActionAllowed_deleteCaseInsensitive() {
        UserAccount account = buildAccount(true, false);
        assertTrue(auditService.isActionAllowed("delete", account));
        assertTrue(auditService.isActionAllowed("Delete", account));
    }

    @Test
    void isActionAllowed_inactiveLockedUserDenied() {
        UserAccount account = buildAccount(false, true);
        assertFalse(auditService.isActionAllowed("UPDATE", account));
    }

    // ------------------------------------------------------------------ helpers

    private AuditLog buildLog(String entityType, long entityId) {
        AuditLog log = new AuditLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        return log;
    }

    private UserAccount buildAccount(boolean active, boolean locked) {
        UserAccount account = new UserAccount();
        account.setActive(active);
        account.setLocked(locked);
        return account;
    }
}
