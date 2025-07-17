package com.luohengxu.backend.controller;

import com.luohengxu.backend.dto.ApiResponse;
import com.luohengxu.backend.dto.MediaItemRequest;
import com.luohengxu.backend.entity.MediaItem;
import com.luohengxu.backend.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class MediaController {

    @Autowired
    private MediaService mediaService;

    // 獲取所有媒體項目
    @GetMapping
    public ResponseEntity<ApiResponse<List<MediaItem>>> getAllMediaItems() {
        try {
            List<MediaItem> items = mediaService.getAllMediaItems();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取媒體項目成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 根據ID獲取媒體項目
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MediaItem>> getMediaItemById(@PathVariable Long id) {
        try {
            Optional<MediaItem> item = mediaService.getMediaItemById(id);
            if (item.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "獲取媒體項目成功", item.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "找不到媒體項目", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 根據類型獲取媒體項目
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<MediaItem>>> getMediaItemsByType(@PathVariable String type) {
        try {
            List<MediaItem> items = mediaService.getMediaItemsByType(type);
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取媒體項目成功", items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 根據狀態獲取媒體項目
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<MediaItem>>> getMediaItemsByStatus(@PathVariable String status) {
        try {
            List<MediaItem> items = mediaService.getMediaItemsByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取媒體項目成功", items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 搜索媒體項目
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MediaItem>>> searchMediaItems(@RequestParam String q) {
        try {
            List<MediaItem> items = mediaService.searchMediaItems(q);
            return ResponseEntity.ok(new ApiResponse<>(true, "搜索媒體項目成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "搜索媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 根據標籤獲取媒體項目
    @GetMapping("/tag/{tag}")
    public ResponseEntity<ApiResponse<List<MediaItem>>> getMediaItemsByTag(@PathVariable String tag) {
        try {
            List<MediaItem> items = mediaService.getMediaItemsByTag(tag);
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取媒體項目成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 根據分類獲取媒體項目
    @GetMapping("/genre/{genre}")
    public ResponseEntity<ApiResponse<List<MediaItem>>> getMediaItemsByGenre(@PathVariable String genre) {
        try {
            List<MediaItem> items = mediaService.getMediaItemsByGenre(genre);
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取媒體項目成功", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 創建媒體項目
    @PostMapping
    public ResponseEntity<ApiResponse<MediaItem>> createMediaItem(@Valid @RequestBody MediaItemRequest request) {
        try {
            MediaItem item = mediaService.createMediaItem(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "創建媒體項目成功", item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "創建媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 更新媒體項目
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MediaItem>> updateMediaItem(
            @PathVariable Long id, 
            @Valid @RequestBody MediaItemRequest request) {
        try {
            MediaItem item = mediaService.updateMediaItem(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "更新媒體項目成功", item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "更新媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 刪除媒體項目
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMediaItem(@PathVariable Long id) {
        try {
            mediaService.deleteMediaItem(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "刪除媒體項目成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "刪除媒體項目失敗: " + e.getMessage(), null));
        }
    }

    // 獲取所有分類
    @GetMapping("/genres")
    public ResponseEntity<ApiResponse<List<String>>> getAllGenres() {
        try {
            List<String> genres = mediaService.getAllGenres();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取分類成功", genres));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取分類失敗: " + e.getMessage(), null));
        }
    }

    // 獲取所有標籤
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<String>>> getAllTags() {
        try {
            List<String> tags = mediaService.getAllTags();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取標籤成功", tags));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取標籤失敗: " + e.getMessage(), null));
        }
    }

    // 獲取媒體類型列表
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<String>>> getMediaTypes() {
        try {
            List<String> types = mediaService.getMediaTypes();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取媒體類型成功", types));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取媒體類型失敗: " + e.getMessage(), null));
        }
    }

    // 獲取媒體狀態列表
    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse<List<String>>> getMediaStatuses() {
        try {
            List<String> statuses = mediaService.getMediaStatuses();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取媒體狀態成功", statuses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取媒體狀態失敗: " + e.getMessage(), null));
        }
    }
} 