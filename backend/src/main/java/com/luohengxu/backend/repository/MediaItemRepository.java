package com.luohengxu.backend.repository;

import com.luohengxu.backend.entity.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {
    
    // 根據類型查找
    List<MediaItem> findByType(MediaItem.MediaType type);
    
    // 根據狀態查找
    List<MediaItem> findByStatus(MediaItem.MediaStatus status);
    
    // 根據類型和狀態查找
    List<MediaItem> findByTypeAndStatus(MediaItem.MediaType type, MediaItem.MediaStatus status);
    
    // 搜索標題或創作者
    @Query("SELECT m FROM MediaItem m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.originalTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.creator) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MediaItem> searchByTitleOrCreator(@Param("query") String query);
    
    // 根據評分範圍查找
    List<MediaItem> findByRatingBetween(Double minRating, Double maxRating);
    
    // 查找包含特定標籤的媒體項目
    @Query("SELECT DISTINCT m FROM MediaItem m JOIN m.tags t WHERE t = :tag")
    List<MediaItem> findByTag(@Param("tag") String tag);
    
    // 查找包含特定分類的媒體項目
    @Query("SELECT DISTINCT m FROM MediaItem m JOIN m.genre g WHERE g = :genre")
    List<MediaItem> findByGenre(@Param("genre") String genre);
    
    // 獲取所有分類
    @Query("SELECT DISTINCT g FROM MediaItem m JOIN m.genre g ORDER BY g")
    List<String> findAllGenres();
    
    // 獲取所有標籤
    @Query("SELECT DISTINCT t FROM MediaItem m JOIN m.tags t ORDER BY t")
    List<String> findAllTags();
    
    // 按創建時間排序
    List<MediaItem> findAllByOrderByCreatedAtDesc();
    
    // 按評分排序
    List<MediaItem> findAllByOrderByRatingDesc();
} 