package com.luohengxu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BlogPostRequest {
    
    @NotBlank(message = "標題不能為空")
    private String title;
    
    @NotBlank(message = "內容不能為空")
    private String content;
    
    private String excerpt;
    
    private String coverImage;
    
    @NotBlank(message = "分類不能為空")
    private String category;
    
    private List<String> tags;
    
    @NotNull(message = "狀態不能為空")
    private String status; // DRAFT 或 PUBLISHED
    
    @NotBlank(message = "作者不能為空")
    private String author;
} 