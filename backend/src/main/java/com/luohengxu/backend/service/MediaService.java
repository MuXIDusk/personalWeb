package com.luohengxu.backend.service;

import com.luohengxu.backend.dto.MediaItemRequest;
import com.luohengxu.backend.entity.MediaItem;
import com.luohengxu.backend.repository.MediaItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MediaService {

    @Autowired
    private MediaItemRepository mediaItemRepository;

    // 獲取所有媒體項目
    public List<MediaItem> getAllMediaItems() {
        return mediaItemRepository.findAllByOrderByCreatedAtDesc();
    }

    // 根據ID獲取媒體項目
    public Optional<MediaItem> getMediaItemById(Long id) {
        return mediaItemRepository.findById(id);
    }

    // 根據類型獲取媒體項目
    public List<MediaItem> getMediaItemsByType(String type) {
        try {
            MediaItem.MediaType mediaType = MediaItem.MediaType.valueOf(type.toUpperCase());
            return mediaItemRepository.findByType(mediaType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的媒體類型: " + type);
        }
    }

    // 根據狀態獲取媒體項目
    public List<MediaItem> getMediaItemsByStatus(String status) {
        try {
            MediaItem.MediaStatus mediaStatus = MediaItem.MediaStatus.valueOf(status.toUpperCase());
            return mediaItemRepository.findByStatus(mediaStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的媒體狀態: " + status);
        }
    }

    // 搜索媒體項目
    public List<MediaItem> searchMediaItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllMediaItems();
        }
        return mediaItemRepository.searchByTitleOrCreator(query.trim());
    }

    // 根據標籤獲取媒體項目
    public List<MediaItem> getMediaItemsByTag(String tag) {
        return mediaItemRepository.findByTag(tag);
    }

    // 根據分類獲取媒體項目
    public List<MediaItem> getMediaItemsByGenre(String genre) {
        return mediaItemRepository.findByGenre(genre);
    }

    // 創建媒體項目
    public MediaItem createMediaItem(MediaItemRequest request) {
        MediaItem mediaItem = convertToEntity(request);
        return mediaItemRepository.save(mediaItem);
    }

    // 更新媒體項目
    public MediaItem updateMediaItem(Long id, MediaItemRequest request) {
        MediaItem existingItem = mediaItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到媒體項目，ID: " + id));

        updateEntityFromRequest(existingItem, request);
        return mediaItemRepository.save(existingItem);
    }

    // 刪除媒體項目
    public void deleteMediaItem(Long id) {
        if (!mediaItemRepository.existsById(id)) {
            throw new RuntimeException("找不到媒體項目，ID: " + id);
        }
        mediaItemRepository.deleteById(id);
    }

    // 獲取所有分類
    public List<String> getAllGenres() {
        return mediaItemRepository.findAllGenres();
    }

    // 獲取所有標籤
    public List<String> getAllTags() {
        return mediaItemRepository.findAllTags();
    }

    // 獲取媒體類型統計
    public List<String> getMediaTypes() {
        return List.of("BOOK", "MOVIE", "TV", "ANIME");
    }

    // 獲取媒體狀態列表
    public List<String> getMediaStatuses() {
        return List.of("COMPLETED", "WATCHING", "WANT_TO_WATCH");
    }

    // 根據評分範圍獲取媒體項目
    public List<MediaItem> getMediaItemsByRatingRange(Double minRating, Double maxRating) {
        return mediaItemRepository.findByRatingBetween(minRating, maxRating);
    }

    // 轉換DTO到實體
    private MediaItem convertToEntity(MediaItemRequest request) {
        MediaItem mediaItem = new MediaItem();
        updateEntityFromRequest(mediaItem, request);
        return mediaItem;
    }

    // 從請求更新實體
    private void updateEntityFromRequest(MediaItem mediaItem, MediaItemRequest request) {
        mediaItem.setTitle(request.getTitle());
        mediaItem.setOriginalTitle(request.getOriginalTitle());
        mediaItem.setCover(request.getCover());
        
        try {
            mediaItem.setType(MediaItem.MediaType.valueOf(request.getType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的媒體類型: " + request.getType());
        }
        
        mediaItem.setCreator(request.getCreator());
        mediaItem.setGenre(request.getGenre());
        mediaItem.setRating(request.getRating());
        
        try {
            mediaItem.setStatus(MediaItem.MediaStatus.valueOf(request.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的媒體狀態: " + request.getStatus());
        }
        
        mediaItem.setDateAdded(request.getDateAdded());
        mediaItem.setDateWatched(request.getDateWatched());
        mediaItem.setReview(request.getReview());
        mediaItem.setTags(request.getTags());
        mediaItem.setDoubanUrl(request.getDoubanUrl());
        mediaItem.setImdbUrl(request.getImdbUrl());
        mediaItem.setMalUrl(request.getMalUrl());
    }
} 