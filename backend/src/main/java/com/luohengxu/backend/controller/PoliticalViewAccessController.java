package com.luohengxu.backend.controller;

import com.luohengxu.backend.dto.ApiResponse;
import com.luohengxu.backend.dto.PoliticalViewAccessRequest;
import com.luohengxu.backend.entity.PoliticalViewAccess;
import com.luohengxu.backend.service.PoliticalViewAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/political-access")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PoliticalViewAccessController {

    @Autowired
    private PoliticalViewAccessService accessService;

    // 获取所有访问权限
    @GetMapping
    public ResponseEntity<ApiResponse<List<PoliticalViewAccess>>> getAllAccess() {
        try {
            List<PoliticalViewAccess> accessList = accessService.getAllAccess();
            return ResponseEntity.ok(new ApiResponse<>(true, "获取访问权限列表成功", accessList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "获取访问权限列表失败: " + e.getMessage(), null));
        }
    }

    // 获取活跃的访问权限
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PoliticalViewAccess>>> getActiveAccess() {
        try {
            List<PoliticalViewAccess> accessList = accessService.getActiveAccess();
            return ResponseEntity.ok(new ApiResponse<>(true, "获取活跃访问权限成功", accessList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "获取活跃访问权限失败: " + e.getMessage(), null));
        }
    }

    // 根据ID获取访问权限
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PoliticalViewAccess>> getAccessById(@PathVariable Long id) {
        try {
            Optional<PoliticalViewAccess> access = accessService.getAccessById(id);
            if (access.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "获取访问权限成功", access.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "找不到访问权限", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "获取访问权限失败: " + e.getMessage(), null));
        }
    }

    // 验证访问权限
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateAccess(@RequestBody PoliticalViewAccessRequest request) {
        try {
            boolean isValid = accessService.validateAccess(request.getName());
            String message = isValid ? "验证成功" : "验证失败，该姓名不在访问列表中";
            return ResponseEntity.ok(new ApiResponse<>(true, message, isValid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "验证访问权限失败: " + e.getMessage(), null));
        }
    }

    // 创建访问权限
    @PostMapping
    public ResponseEntity<ApiResponse<PoliticalViewAccess>> createAccess(@Valid @RequestBody PoliticalViewAccessRequest request) {
        try {
            PoliticalViewAccess access = accessService.createAccess(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "创建访问权限成功", access));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "创建访问权限失败: " + e.getMessage(), null));
        }
    }

    // 更新访问权限
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PoliticalViewAccess>> updateAccess(
            @PathVariable Long id, 
            @Valid @RequestBody PoliticalViewAccessRequest request) {
        try {
            PoliticalViewAccess access = accessService.updateAccess(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "更新访问权限成功", access));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "更新访问权限失败: " + e.getMessage(), null));
        }
    }

    // 删除访问权限
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccess(@PathVariable Long id) {
        try {
            accessService.deleteAccess(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "删除访问权限成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "删除访问权限失败: " + e.getMessage(), null));
        }
    }

    // 切换激活状态
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<PoliticalViewAccess>> toggleActive(@PathVariable Long id) {
        try {
            PoliticalViewAccess access = accessService.toggleActive(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "切换状态成功", access));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "切换状态失败: " + e.getMessage(), null));
        }
    }
} 