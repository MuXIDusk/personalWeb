package com.luohengxu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {
    
    @NotNull(message = "文章ID不能為空")
    private Long postId;
    
    @NotBlank(message = "作者名稱不能為空")
    private String author;
    
    private String email;
    
    @NotBlank(message = "評論內容不能為空")
    private String content;
    
    private Long parentId;
    
    // 新增字段
    private String ipAddress;
    private String userAgent;
} 