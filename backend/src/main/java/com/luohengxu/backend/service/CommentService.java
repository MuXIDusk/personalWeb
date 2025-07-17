package com.luohengxu.backend.service;

import com.luohengxu.backend.dto.CommentRequest;
import com.luohengxu.backend.dto.CommentReviewRequest;
import com.luohengxu.backend.entity.Comment;
import com.luohengxu.backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    
    private final CommentRepository commentRepository;
    
    // 原有方法（為了向後兼容）
    public List<Comment> getApprovedCommentsForPost(Long postId) {
        return commentRepository.findByPostIdAndStatusOrderByCreatedAtAsc(postId, Comment.CommentStatus.APPROVED);
    }
    
    public List<Comment> getAllCommentsForPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
    
    public List<Comment> getPendingComments() {
        return commentRepository.findByStatusOrderByCreatedAtDesc(Comment.CommentStatus.PENDING);
    }
    
    public Comment createComment(CommentRequest request) {
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setAuthor(request.getAuthor());
        comment.setEmail(request.getEmail());
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setIpAddress(request.getIpAddress());
        comment.setUserAgent(request.getUserAgent());
        
        // 設置初始狀態
        comment.setStatus(Comment.CommentStatus.PENDING);
        comment.setApproved(false);
        
        // 簡單的垃圾評論檢測
        double spamScore = calculateSpamScore(comment);
        comment.setSpamScore(spamScore);
        
        if (spamScore > 0.8) {
            comment.setStatus(Comment.CommentStatus.REJECTED);
            comment.setIsSpam(true);
        }
        
        return commentRepository.save(comment);
    }
    
    // 簡化的審核評論
    public boolean reviewComment(CommentReviewRequest request) {
        Optional<Comment> commentOpt = commentRepository.findById(request.getCommentId());
        if (commentOpt.isEmpty()) {
            return false;
        }
        
        Comment comment = commentOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        switch (request.getAction().toLowerCase()) {
            case "approve":
                comment.setStatus(Comment.CommentStatus.APPROVED);
                comment.setApproved(true);
                comment.setIsSpam(false);
                break;
            case "reject":
                comment.setStatus(Comment.CommentStatus.REJECTED);
                comment.setApproved(false);
                comment.setIsSpam(true);
                break;
            default:
                return false;
        }
        
        comment.setReviewedAt(now);
        comment.setReviewedBy(request.getReviewedBy());
        
        commentRepository.save(comment);
        return true;
    }
    
    // 簡化的批量審核
    public Map<String, Object> batchReviewComments(List<Long> commentIds, String action, String reviewedBy) {
        List<Comment> comments = commentRepository.findAllById(commentIds);
        int successCount = 0;
        int failCount = 0;
        
        LocalDateTime now = LocalDateTime.now();
        
        for (Comment comment : comments) {
            try {
                switch (action.toLowerCase()) {
                    case "approve":
                        comment.setStatus(Comment.CommentStatus.APPROVED);
                        comment.setApproved(true);
                        comment.setIsSpam(false);
                        break;
                    case "reject":
                        comment.setStatus(Comment.CommentStatus.REJECTED);
                        comment.setApproved(false);
                        comment.setIsSpam(true);
                        break;
                    default:
                        failCount++;
                        continue;
                }
                
                comment.setReviewedAt(now);
                comment.setReviewedBy(reviewedBy);
                commentRepository.save(comment);
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("totalCount", commentIds.size());
        
        return result;
    }
    
    // 搜索評論
    public List<Comment> searchComments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return commentRepository.findAll();
        }
        return commentRepository.findByKeyword(keyword.trim());
    }
    
    // 獲取評論統計
    public Map<String, Object> getCommentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalComments = commentRepository.count();
        long pendingComments = commentRepository.countByStatus(Comment.CommentStatus.PENDING);
        long approvedComments = commentRepository.countByStatus(Comment.CommentStatus.APPROVED);
        long rejectedComments = commentRepository.countByStatus(Comment.CommentStatus.REJECTED);
        
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        long recentComments = commentRepository.countByCreatedAtAfter(lastWeek);
        
        stats.put("total", totalComments);
        stats.put("pending", pendingComments);
        stats.put("approved", approvedComments);
        stats.put("rejected", rejectedComments);
        stats.put("recent", recentComments);
        stats.put("approvalRate", totalComments > 0 ? (double) approvedComments / totalComments * 100 : 0);
        
        return stats;
    }
    
    // 獲取垃圾評論（高風險評論）
    public List<Comment> getHighRiskComments(double threshold) {
        return commentRepository.findBySpamScoreGreaterThanOrderBySpamScoreDesc(threshold);
    }
    
    // 獲取按狀態分組的評論
    public Map<Comment.CommentStatus, List<Comment>> getCommentsByStatus() {
        List<Comment> allComments = commentRepository.findAll();
        return allComments.stream()
                .collect(Collectors.groupingBy(Comment::getStatus));
    }
    
    // 時間範圍內的評論
    public List<Comment> getCommentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return commentRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }
    
    // 保留原有方法
    public boolean approveComment(Long commentId) {
        CommentReviewRequest request = new CommentReviewRequest();
        request.setCommentId(commentId);
        request.setAction("approve");
        request.setReviewedBy("system");
        return reviewComment(request);
    }
    
    public boolean deleteComment(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }
    
    public long getCommentCountForPost(Long postId) {
        return commentRepository.countByPostIdAndApprovedTrue(postId);
    }
    
    // 簡化的垃圾評論評分計算
    private double calculateSpamScore(Comment comment) {
        double score = 0.0;
        
        String content = comment.getContent().toLowerCase();
        
        // 檢查常見垃圾詞彙
        String[] spamKeywords = {"賺錢", "兼職", "點擊", "免費", "優惠", "廣告", "推廣"};
        for (String keyword : spamKeywords) {
            if (content.contains(keyword)) {
                score += 0.2;
            }
        }
        
        // 檢查重複字符
        if (content.matches(".*([a-zA-Z0-9])\\1{3,}.*")) {
            score += 0.3;
        }
        
        // 檢查長度異常
        if (content.length() > 1000) {
            score += 0.2;
        }
        if (content.length() < 10) {
            score += 0.1;
        }
        
        // 檢查URL
        if (content.matches(".*https?://.*")) {
            score += 0.3;
        }
        
        // 檢查大量標點符號
        long punctuationCount = content.chars()
                .filter(ch -> "!@#$%^&*()".indexOf(ch) != -1)
                .count();
        if (punctuationCount > content.length() * 0.3) {
            score += 0.2;
        }
        
        return Math.min(score, 1.0);
    }
} 