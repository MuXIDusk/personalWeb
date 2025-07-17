package com.luohengxu.backend.controller;

import com.luohengxu.backend.dto.ApiResponse;
import com.luohengxu.backend.dto.PoliticalViewRequest;
import com.luohengxu.backend.entity.PoliticalView;
import com.luohengxu.backend.service.PoliticalViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/political")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PoliticalViewController {

    @Autowired
    private PoliticalViewService politicalViewService;

    // 獲取所有政治觀點
    @GetMapping
    public ResponseEntity<ApiResponse<List<PoliticalView>>> getAllPoliticalViews() {
        try {
            List<PoliticalView> views = politicalViewService.getAllPoliticalViews();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取政治觀點成功", views));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取政治觀點失敗: " + e.getMessage(), null));
        }
    }

    // 獲取活躍的政治觀點
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PoliticalView>>> getActivePoliticalViews() {
        try {
            List<PoliticalView> views = politicalViewService.getActivePoliticalViews();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取活躍政治觀點成功", views));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取活躍政治觀點失敗: " + e.getMessage(), null));
        }
    }

    // 根據ID獲取政治觀點
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PoliticalView>> getPoliticalViewById(@PathVariable Long id) {
        try {
            Optional<PoliticalView> view = politicalViewService.getPoliticalViewById(id);
            if (view.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "獲取政治觀點成功", view.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "找不到政治觀點", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取政治觀點失敗: " + e.getMessage(), null));
        }
    }

    // 根據類型獲取政治觀點
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<PoliticalView>>> getPoliticalViewsByType(@PathVariable String type) {
        try {
            PoliticalView.ViewType viewType = PoliticalView.ViewType.valueOf(type.toUpperCase());
            List<PoliticalView> views = politicalViewService.getPoliticalViewsByType(viewType);
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取政治觀點成功", views));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "無效的類型: " + type, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取政治觀點失敗: " + e.getMessage(), null));
        }
    }

    // 獲取核心價值觀
    @GetMapping("/core-values")
    public ResponseEntity<ApiResponse<List<PoliticalView>>> getCoreValues() {
        try {
            List<PoliticalView> views = politicalViewService.getActiveCoreValues();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取核心價值觀成功", views));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取核心價值觀失敗: " + e.getMessage(), null));
        }
    }

    // 獲取政治測試結果
    @GetMapping("/test-results")
    public ResponseEntity<ApiResponse<List<PoliticalView>>> getTestResults() {
        try {
            List<PoliticalView> views = politicalViewService.getActiveTestResults();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取政治測試結果成功", views));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取政治測試結果失敗: " + e.getMessage(), null));
        }
    }

    // 創建政治觀點
    @PostMapping
    public ResponseEntity<ApiResponse<PoliticalView>> createPoliticalView(@Valid @RequestBody PoliticalViewRequest request) {
        try {
            PoliticalView view = politicalViewService.createPoliticalView(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "創建政治觀點成功", view));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "創建政治觀點失敗: " + e.getMessage(), null));
        }
    }

    // 更新政治觀點
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PoliticalView>> updatePoliticalView(
            @PathVariable Long id, 
            @Valid @RequestBody PoliticalViewRequest request) {
        try {
            PoliticalView view = politicalViewService.updatePoliticalView(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "更新政治觀點成功", view));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "更新政治觀點失敗: " + e.getMessage(), null));
        }
    }

    // 刪除政治觀點
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePoliticalView(@PathVariable Long id) {
        try {
            politicalViewService.deletePoliticalView(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "刪除政治觀點成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "刪除政治觀點失敗: " + e.getMessage(), null));
        }
    }

    // 切換激活狀態
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<PoliticalView>> toggleActive(@PathVariable Long id) {
        try {
            PoliticalView view = politicalViewService.toggleActive(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "切換狀態成功", view));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "切換狀態失敗: " + e.getMessage(), null));
        }
    }

    // 更新顯示順序
    @PutMapping("/{id}/display-order")
    public ResponseEntity<ApiResponse<PoliticalView>> updateDisplayOrder(
            @PathVariable Long id, 
            @RequestParam Integer displayOrder) {
        try {
            PoliticalView view = politicalViewService.updateDisplayOrder(id, displayOrder);
            return ResponseEntity.ok(new ApiResponse<>(true, "更新順序成功", view));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "更新順序失敗: " + e.getMessage(), null));
        }
    }
} 