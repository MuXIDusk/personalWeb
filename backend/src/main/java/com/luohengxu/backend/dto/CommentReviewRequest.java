package com.luohengxu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentReviewRequest {
    
    @NotNull(message = "評論ID不能為空")
    private Long commentId;
    
    @NotBlank(message = "審核操作不能為空")
    private String action; // "approve", "reject"
    
    @NotBlank(message = "審核人不能為空")
    private String reviewedBy;
} 