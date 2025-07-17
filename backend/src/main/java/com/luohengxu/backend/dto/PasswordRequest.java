package com.luohengxu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordRequest {
    
    @NotBlank(message = "密碼不能為空")
    private String password;
} 