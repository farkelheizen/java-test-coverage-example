package com.example.model;

import java.time.LocalDateTime;

public class Comment {
    private String commentId;
    private String content;
    private long authorId;
    private long entityId;
    private String entityType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isEdited;

    public Comment() {}

    public Comment(String commentId, String content, long authorId, long entityId,
                   String entityType, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isEdited) {
        this.commentId = commentId;
        this.content = content;
        this.authorId = authorId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isEdited = isEdited;
    }

    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getAuthorId() { return authorId; }
    public void setAuthorId(long authorId) { this.authorId = authorId; }
    public long getEntityId() { return entityId; }
    public void setEntityId(long entityId) { this.entityId = entityId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isEdited() { return isEdited; }
    public void setEdited(boolean edited) { isEdited = edited; }
}
