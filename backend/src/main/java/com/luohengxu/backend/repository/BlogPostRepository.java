package com.luohengxu.backend.repository;

import com.luohengxu.backend.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    Page<BlogPost> findByStatusOrderByPublishedAtDesc(BlogPost.PostStatus status, Pageable pageable);
    
    List<BlogPost> findByStatusOrderByPublishedAtDesc(BlogPost.PostStatus status);
    
    List<BlogPost> findByCategoryAndStatusOrderByPublishedAtDesc(String category, BlogPost.PostStatus status);
    
    @Query("SELECT p FROM BlogPost p JOIN p.tags t WHERE t = :tag AND p.status = :status ORDER BY p.publishedAt DESC")
    List<BlogPost> findByTagAndStatus(@Param("tag") String tag, @Param("status") BlogPost.PostStatus status);
    
    @Query("SELECT p FROM BlogPost p WHERE p.status = :status AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.publishedAt DESC")
    List<BlogPost> searchByContentAndStatus(@Param("query") String query, @Param("status") BlogPost.PostStatus status);
    
    @Query("SELECT DISTINCT p.category FROM BlogPost p WHERE p.status = :status")
    List<String> findDistinctCategoriesByStatus(@Param("status") BlogPost.PostStatus status);
    
    @Query("SELECT DISTINCT t FROM BlogPost p JOIN p.tags t WHERE p.status = :status")
    List<String> findDistinctTagsByStatus(@Param("status") BlogPost.PostStatus status);
} 