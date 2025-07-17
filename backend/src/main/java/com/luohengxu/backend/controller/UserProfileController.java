package com.luohengxu.backend.controller;

import com.luohengxu.backend.dto.ApiResponse;
import com.luohengxu.backend.dto.UserProfileRequest;
import com.luohengxu.backend.entity.UserProfile;
import com.luohengxu.backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // 獲取主要用戶資料
    @GetMapping
    public ResponseEntity<ApiResponse<UserProfile>> getUserProfile() {
        try {
            Optional<UserProfile> profile = userProfileService.getUserProfile();
            if (profile.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "獲取用戶資料成功", profile.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "找不到用戶資料", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取用戶資料失敗: " + e.getMessage(), null));
        }
    }

    // 根據ID獲取用戶資料
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfile>> getUserProfileById(@PathVariable Long id) {
        try {
            Optional<UserProfile> profile = userProfileService.getUserProfileById(id);
            if (profile.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "獲取用戶資料成功", profile.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "找不到用戶資料", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取用戶資料失敗: " + e.getMessage(), null));
        }
    }

    // 創建用戶資料
    @PostMapping
    public ResponseEntity<ApiResponse<UserProfile>> createUserProfile(@Valid @RequestBody UserProfileRequest request) {
        try {
            // 檢查郵箱是否已存在
            if (request.getEmail() != null && userProfileService.emailExists(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "郵箱已存在", null));
            }

            UserProfile profile = userProfileService.createUserProfile(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "創建用戶資料成功", profile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "創建用戶資料失敗: " + e.getMessage(), null));
        }
    }

    // 更新主要用戶資料
    @PutMapping
    public ResponseEntity<ApiResponse<UserProfile>> updateMainUserProfile(@Valid @RequestBody UserProfileRequest request) {
        try {
            UserProfile profile = userProfileService.updateMainUserProfile(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "更新用戶資料成功", profile));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "更新用戶資料失敗: " + e.getMessage(), null));
        }
    }

    // 更新指定ID的用戶資料
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfile>> updateUserProfile(
            @PathVariable Long id, 
            @Valid @RequestBody UserProfileRequest request) {
        try {
            // 檢查郵箱衝突（除了當前用戶）
            if (request.getEmail() != null) {
                Optional<UserProfile> existingByEmail = userProfileService.getUserByEmail(request.getEmail());
                if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(id)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ApiResponse<>(false, "郵箱已被其他用戶使用", null));
                }
            }

            UserProfile profile = userProfileService.updateUserProfile(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "更新用戶資料成功", profile));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "更新用戶資料失敗: " + e.getMessage(), null));
        }
    }

    // 刪除用戶資料
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(@PathVariable Long id) {
        try {
            userProfileService.deleteUserProfile(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "刪除用戶資料成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "刪除用戶資料失敗: " + e.getMessage(), null));
        }
    }

    // 檢查郵箱是否存在
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@RequestParam String email) {
        try {
            boolean exists = userProfileService.emailExists(email);
            return ResponseEntity.ok(new ApiResponse<>(true, "檢查郵箱成功", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "檢查郵箱失敗: " + e.getMessage(), null));
        }
    }

    // 獲取所有技能
    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<String>>> getAllSkills() {
        try {
            List<String> skills = userProfileService.getAllSkills();
            return ResponseEntity.ok(new ApiResponse<>(true, "獲取技能列表成功", skills));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "獲取技能列表失敗: " + e.getMessage(), null));
        }
    }
} 