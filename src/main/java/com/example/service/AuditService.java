package com.example.service;

import com.example.model.AuditLog;
import com.example.model.UserAccount;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuditService {

    public AuditLog logAction(String entityType, long entityId, String action,
                               long userId, String oldVal, String newVal) {
        AuditLog log = new AuditLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setPerformedBy(userId);
        log.setTimestamp(LocalDateTime.now());
        log.setOldValue(oldVal);
        log.setNewValue(newVal);
        return log;
    }

    public List<AuditLog> getAuditHistory(String entityType, long entityId, List<AuditLog> logs) {
        return logs.stream()
                .filter(l -> entityType.equals(l.getEntityType()) && l.getEntityId() == entityId)
                .collect(Collectors.toList());
    }

    public boolean isActionAllowed(String action, UserAccount account) {
        if ("DELETE".equalsIgnoreCase(action)) {
            // DELETE requires admin - simplified check
            return account.isActive() && !account.isLocked();
        }
        return account.isActive() && !account.isLocked();
    }
}
