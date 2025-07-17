package com.luohengxu.backend.controller;

import com.luohengxu.backend.dto.ApiResponse;
import com.luohengxu.backend.dto.CommentRequest;
import com.luohengxu.backend.dto.CommentReviewRequest;
import com.luohengxu.backend.entity.Comment;
import com.luohengxu.backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CommentController {
    
    private final CommentService commentService;
    
    // 原有API（保持向後兼容）
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<Comment>>> getCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getApprovedCommentsForPost(postId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
    
    @GetMapping("/post/{postId}/all")
    public ResponseEntity<ApiResponse<List<Comment>>> getAllCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getAllCommentsForPost(postId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Comment>>> getPendingComments() {
        List<Comment> comments = commentService.getPendingComments();
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> createComment(@Valid @RequestBody CommentRequest request) {
        Comment comment = commentService.createComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("評論提交成功，等待審核", comment));
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveComment(@PathVariable Long id) {
        boolean approved = commentService.approveComment(id);
        if (approved) {
            return ResponseEntity.ok(ApiResponse.success("評論審核通過", null));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        boolean deleted = commentService.deleteComment(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("評論刪除成功", null));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<ApiResponse<Long>> getCommentCountForPost(@PathVariable Long postId) {
        long count = commentService.getCommentCountForPost(postId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    // 簡化的API
    
    // 評論審核
    @PostMapping("/review")
    public ResponseEntity<ApiResponse<Void>> reviewComment(@Valid @RequestBody CommentReviewRequest request) {
        boolean success = commentService.reviewComment(request);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("評論審核操作成功", null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("評論審核操作失敗"));
        }
    }
    
    // 批量審核
    @PostMapping("/batch-review")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchReviewComments(
            @RequestParam List<Long> commentIds,
            @RequestParam String action,
            @RequestParam String reviewedBy) {
        
        Map<String, Object> result = commentService.batchReviewComments(commentIds, action, reviewedBy);
        return ResponseEntity.ok(ApiResponse.success("批量審核操作完成", result));
    }
    
    // 搜索評論
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Comment>>> searchComments(@RequestParam String keyword) {
        List<Comment> comments = commentService.searchComments(keyword);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
    
    // 獲取評論統計
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCommentStatistics() {
        Map<String, Object> stats = commentService.getCommentStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    // 按狀態獲取評論
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Comment>>> getCommentsByStatus(@PathVariable String status) {
        try {
            Comment.CommentStatus commentStatus = Comment.CommentStatus.valueOf(status.toUpperCase());
            List<Comment> comments = commentService.getCommentsByStatus().get(commentStatus);
            return ResponseEntity.ok(ApiResponse.success(comments != null ? comments : List.of()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("無效的評論狀態"));
        }
    }
    
    // 獲取高風險評論
    @GetMapping("/high-risk")
    public ResponseEntity<ApiResponse<List<Comment>>> getHighRiskComments(
            @RequestParam(defaultValue = "0.6") double threshold) {
        List<Comment> comments = commentService.getHighRiskComments(threshold);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
    
    // 按日期範圍獲取評論
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<Comment>>> getCommentsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59", formatter);
            
            List<Comment> comments = commentService.getCommentsByDateRange(start, end);
            return ResponseEntity.ok(ApiResponse.success(comments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("日期格式錯誤"));
        }
    }
    
    // 獲取所有評論（用於管理）
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<Comment>>> getAllCommentsForAdmin() {
        List<Comment> comments = commentService.searchComments("");
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
    
    // 批量刪除評論
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteComments(@RequestParam List<Long> commentIds) {
        int successCount = 0;
        int failCount = 0;
        
        for (Long id : commentIds) {
            if (commentService.deleteComment(id)) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        Map<String, Object> result = Map.of(
            "successCount", successCount,
            "failCount", failCount,
            "totalCount", commentIds.size()
        );
        
        return ResponseEntity.ok(ApiResponse.success("批量刪除操作完成", result));
    }
} 