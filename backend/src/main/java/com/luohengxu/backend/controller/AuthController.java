package com.luohengxu.backend.controller;

import com.luohengxu.backend.dto.ApiResponse;
import com.luohengxu.backend.dto.PasswordRequest;
import com.luohengxu.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/blog/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateBlogPassword(@Valid @RequestBody PasswordRequest request) {
        boolean isValid = authService.validateBlogAdminPassword(request.getPassword());
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("密碼驗證成功", true));
        } else {
            return ResponseEntity.ok(ApiResponse.error("密碼錯誤"));
        }
    }
    
    @PostMapping("/political/validate")
    public ResponseEntity<ApiResponse<Boolean>> validatePoliticalPassword(@Valid @RequestBody PasswordRequest request) {
        boolean isValid = authService.validatePoliticalPassword(request.getPassword());
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("密碼驗證成功", true));
        } else {
            return ResponseEntity.ok(ApiResponse.error("密碼錯誤"));
        }
    }
} 