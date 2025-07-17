package com.luohengxu.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "post_id", nullable = false)
    private Long postId;
    
    @Column(nullable = false, length = 100)
    private String author;
    
    @Column(length = 255)
    private String email;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private Boolean approved = false;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    // 簡化的審核相關字段
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CommentStatus status = CommentStatus.PENDING;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "is_spam", nullable = false)
    private Boolean isSpam = false;
    
    @Column(name = "spam_score")
    private Double spamScore;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    // 簡化的評論狀態枚舉
    public enum CommentStatus {
        PENDING,    // 待審核
        APPROVED,   // 已通過
        REJECTED    // 已拒絕
    }
} 