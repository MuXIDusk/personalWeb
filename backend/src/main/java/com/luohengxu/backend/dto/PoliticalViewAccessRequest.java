package com.luohengxu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PoliticalViewAccessRequest {
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    private String description;
    
    private Boolean isActive = true;
} 