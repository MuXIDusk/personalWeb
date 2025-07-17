package com.luohengxu.backend.repository;

import com.luohengxu.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 原有方法
    List<Comment> findByPostIdAndApprovedTrueOrderByCreatedAtAsc(Long postId);
    
    List<Comment> findByApprovedFalseOrderByCreatedAtDesc();
    
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    
    long countByPostIdAndApprovedTrue(Long postId);
    
    // 新增：基於狀態查詢
    List<Comment> findByStatusOrderByCreatedAtDesc(Comment.CommentStatus status);
    
    List<Comment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, Comment.CommentStatus status);
    
    // 新增：待審核評論
    List<Comment> findByStatusInOrderByCreatedAtDesc(List<Comment.CommentStatus> statuses);
    
    // 新增：根據審核人查詢
    List<Comment> findByReviewedByOrderByReviewedAtDesc(String reviewedBy);
    
    // 新增：時間範圍內的評論
    List<Comment> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    // 新增：垃圾評論
    List<Comment> findByIsSpamTrueOrderByCreatedAtDesc();
    
    // 新增：高垃圾評分的評論
    List<Comment> findBySpamScoreGreaterThanOrderBySpamScoreDesc(Double threshold);
    
    // 新增：統計查詢
    long countByStatus(Comment.CommentStatus status);
    
    long countByCreatedAtAfter(LocalDateTime date);
    
    long countByIsSpamTrue();
    
    // 新增：複雜查詢
    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND c.status = :status AND c.parentId IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findTopLevelCommentsByPostAndStatus(@Param("postId") Long postId, @Param("status") Comment.CommentStatus status);
    
    @Query("SELECT c FROM Comment c WHERE c.parentId = :parentId AND c.status = :status ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentAndStatus(@Param("parentId") Long parentId, @Param("status") Comment.CommentStatus status);
    
    @Query("SELECT c FROM Comment c WHERE c.author LIKE %:keyword% OR c.content LIKE %:keyword% ORDER BY c.createdAt DESC")
    List<Comment> findByKeyword(@Param("keyword") String keyword);
} 