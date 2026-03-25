package com.example.model;

import java.time.LocalDateTime;

public class AuditLog {
    private String logId;
    private String entityType;
    private long entityId;
    private String action;
    private long performedBy;
    private LocalDateTime timestamp;
    private String oldValue;
    private String newValue;

    public AuditLog() {}

    public AuditLog(String logId, String entityType, long entityId, String action,
                    long performedBy, LocalDateTime timestamp, String oldValue, String newValue) {
        this.logId = logId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public long getEntityId() { return entityId; }
    public void setEntityId(long entityId) { this.entityId = entityId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public long getPerformedBy() { return performedBy; }
    public void setPerformedBy(long performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
}
